package com.gasstation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gasstation.common.BusinessException;
import com.gasstation.common.PageResult;
import com.gasstation.dto.OrderQueryDTO;
import com.gasstation.dto.PlaceOrderDTO;
import com.gasstation.entity.BalanceLog;
import com.gasstation.entity.FuelOrder;
import com.gasstation.entity.Gun;
import com.gasstation.entity.Member;
import com.gasstation.entity.MemberCoupon;
import com.gasstation.entity.Station;
import com.gasstation.mapper.FuelOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 加油订单服务
 */
@Service
@RequiredArgsConstructor
public class FuelOrderService extends ServiceImpl<FuelOrderMapper, FuelOrder> {

    private static final DateTimeFormatter ORDER_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final StationService stationService;
    private final GunService gunService;
    private final FuelPriceService fuelPriceService;
    private final MemberService memberService;
    private final MemberCouponService memberCouponService;

    /**
     * 下单（待支付）
     */
    @Transactional(rollbackFor = Exception.class)
    public FuelOrder placeOrder(Long memberId, PlaceOrderDTO dto) {
        Station station = stationService.getById(dto.getStationId());
        if (station == null) {
            throw new BusinessException("油站不存在");
        }
        if (station.getStatus() == null || station.getStatus() != 1) {
            throw new BusinessException("该油站已停业");
        }

        Gun gun = null;
        String fuelType;
        if (dto.getGunId() != null) {
            gun = gunService.checkAvailable(dto.getGunId(), dto.getStationId());
            fuelType = gun.getFuelType();
        } else {
            fuelType = dto.getFuelType();
            if (!StringUtils.hasText(fuelType)) {
                throw new BusinessException("请选择油品或加油枪");
            }
        }

        BigDecimal unitPrice = fuelPriceService.getUnitPrice(dto.getStationId(), fuelType);
        BigDecimal amount;
        BigDecimal liters;
        if (dto.getAmount() != null && dto.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            amount = dto.getAmount().setScale(2, RoundingMode.HALF_UP);
            liters = amount.divide(unitPrice, 2, RoundingMode.HALF_UP);
        } else if (dto.getLiters() != null && dto.getLiters().compareTo(BigDecimal.ZERO) > 0) {
            liters = dto.getLiters().setScale(2, RoundingMode.HALF_UP);
            amount = liters.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
        } else {
            throw new BusinessException("请输入加油金额或加油升数");
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        Long memberCouponId = dto.getMemberCouponId();
        if (memberCouponId != null) {
            MemberCoupon memberCoupon = memberCouponService.getById(memberCouponId);
            if (memberCoupon == null) {
                throw new BusinessException("优惠券不存在");
            }
            if (!memberCoupon.getMemberId().equals(memberId)) {
                throw new BusinessException("优惠券不属于当前会员");
            }
            if (memberCoupon.getStatus() == null || memberCoupon.getStatus() != MemberCoupon.STATUS_UNUSED) {
                throw new BusinessException("优惠券已使用");
            }
            discountAmount = memberCouponService.calcDiscount(memberCoupon, amount);
        }

        FuelOrder order = new FuelOrder();
        order.setOrderNo(generateOrderNo());
        order.setMemberId(memberId);
        order.setStationId(station.getId());
        order.setStationName(station.getName());
        order.setGunId(gun == null ? null : gun.getId());
        order.setGunNo(gun == null ? null : gun.getGunNo());
        order.setFuelType(fuelType);
        order.setUnitPrice(unitPrice);
        order.setLiters(liters);
        order.setAmount(amount);
        order.setCouponId(memberCouponId);
        order.setDiscountAmount(discountAmount);
        order.setPayAmount(amount.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP));
        order.setStatus(FuelOrder.STATUS_UNPAID);
        order.setRemark(dto.getRemark());
        save(order);

        // 占用加油枪
        if (gun != null) {
            gunService.changeStatus(gun.getId(), GunService.STATUS_BUSY);
        }
        return order;
    }

    /**
     * 支付（余额 / 微信）
     */
    @Transactional(rollbackFor = Exception.class)
    public FuelOrder pay(Long memberId, String orderNo, String payType) {
        FuelOrder order = getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getMemberId().equals(memberId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != FuelOrder.STATUS_UNPAID) {
            throw new BusinessException("订单状态不允许支付");
        }
        String type = StringUtils.hasText(payType) ? payType : FuelOrder.PAY_BALANCE;
        if (FuelOrder.PAY_BALANCE.equals(type)) {
            memberService.consume(memberId, order.getPayAmount(), orderNo);
        } else if (FuelOrder.PAY_WX.equals(type)) {
            // TODO 接入微信支付统一下单，此处演示环境直接视为支付成功
            logPaymentByWx(order);
        } else {
            throw new BusinessException("不支持的支付方式");
        }

        if (order.getCouponId() != null) {
            memberCouponService.useCoupon(order.getCouponId(), order.getId(), order.getDiscountAmount());
        }
        int points = order.getPayAmount().setScale(0, RoundingMode.FLOOR).intValue();
        memberService.addPoints(memberId, points);

        order.setPayType(type);
        order.setPoints(points);
        order.setStatus(FuelOrder.STATUS_PAID);
        order.setPayTime(LocalDateTime.now());
        updateById(order);
        return order;
    }

    private void logPaymentByWx(FuelOrder order) {
        // 真实项目中应调用微信支付统一下单 + 异步回调校验，这里仅打印日志
        org.slf4j.LoggerFactory.getLogger(FuelOrderService.class)
                .info("微信支付下单（演示）：orderNo={}, amount={}", order.getOrderNo(), order.getPayAmount());
    }

    /**
     * 取消订单（仅待支付）
     */
    @Transactional(rollbackFor = Exception.class)
    public FuelOrder cancel(Long memberId, String orderNo) {
        FuelOrder order = getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getMemberId().equals(memberId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != FuelOrder.STATUS_UNPAID) {
            throw new BusinessException("仅待支付订单可取消");
        }
        order.setStatus(FuelOrder.STATUS_CANCELED);
        updateById(order);
        gunService.changeStatus(order.getGunId(), GunService.STATUS_FREE);
        return order;
    }

    /**
     * 完成加油（释放油枪）
     */
    @Transactional(rollbackFor = Exception.class)
    public void finish(Long orderId) {
        FuelOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != FuelOrder.STATUS_PAID) {
            throw new BusinessException("仅已支付订单可完成");
        }
        order.setStatus(FuelOrder.STATUS_FINISHED);
        updateById(order);
        gunService.changeStatus(order.getGunId(), GunService.STATUS_FREE);
    }

    /**
     * 退款（原路退回余额）
     */
    @Transactional(rollbackFor = Exception.class)
    public void refund(Long orderId) {
        FuelOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != FuelOrder.STATUS_PAID && order.getStatus() != FuelOrder.STATUS_FINISHED) {
            throw new BusinessException("该订单不可退款");
        }
        if (FuelOrder.PAY_BALANCE.equals(order.getPayType())) {
            memberService.adjustBalance(order.getMemberId(), order.getPayAmount(),
                    BalanceLog.TYPE_REFUND, order.getOrderNo(), "订单退款");
        }
        if (order.getPoints() != null && order.getPoints() > 0) {
            memberService.addPoints(order.getMemberId(), -order.getPoints());
        }
        order.setStatus(FuelOrder.STATUS_REFUNDED);
        updateById(order);
        gunService.changeStatus(order.getGunId(), GunService.STATUS_FREE);
    }

    public FuelOrder getByOrderNo(String orderNo) {
        return getOne(new LambdaQueryWrapper<FuelOrder>()
                .eq(FuelOrder::getOrderNo, orderNo)
                .last("LIMIT 1"));
    }

    /** 后台订单分页 */
    public PageResult<FuelOrder> adminPage(OrderQueryDTO query) {
        LambdaQueryWrapper<FuelOrder> wrapper = buildWrapper(query);
        IPage<FuelOrder> page = page(query.toPage(), wrapper);
        fillStationName(page.getRecords());
        return PageResult.of(page);
    }

    /** 我的订单 */
    public PageResult<FuelOrder> myPage(Long memberId, Integer status, int current, int size) {
        LambdaQueryWrapper<FuelOrder> wrapper = new LambdaQueryWrapper<FuelOrder>()
                .eq(FuelOrder::getMemberId, memberId)
                .eq(status != null, FuelOrder::getStatus, status)
                .orderByDesc(FuelOrder::getId);
        IPage<FuelOrder> page = page(new Page<>(current, size), wrapper);
        fillStationName(page.getRecords());
        return PageResult.of(page);
    }

    private LambdaQueryWrapper<FuelOrder> buildWrapper(OrderQueryDTO query) {
        LambdaQueryWrapper<FuelOrder> wrapper = new LambdaQueryWrapper<FuelOrder>()
                .like(StringUtils.hasText(query.getOrderNo()), FuelOrder::getOrderNo, query.getOrderNo())
                .eq(query.getStatus() != null, FuelOrder::getStatus, query.getStatus())
                .eq(query.getStationId() != null, FuelOrder::getStationId, query.getStationId())
                .orderByDesc(FuelOrder::getId);
        if (StringUtils.hasText(query.getMemberPhone())) {
            List<Member> members = memberService.list(new LambdaQueryWrapper<Member>()
                    .like(Member::getPhone, query.getMemberPhone()));
            List<Long> ids = members.stream().map(Member::getId).toList();
            if (ids.isEmpty()) {
                wrapper.apply("1 = 2");
            } else {
                wrapper.in(FuelOrder::getMemberId, ids);
            }
        }
        if (StringUtils.hasText(query.getStartDate()) && StringUtils.hasText(query.getEndDate())) {
            wrapper.between(FuelOrder::getCreateTime,
                    LocalDateTime.of(LocalDate.parse(query.getStartDate()), LocalTime.MIN),
                    LocalDateTime.of(LocalDate.parse(query.getEndDate()), LocalTime.MAX));
        }
        return wrapper;
    }

    private void fillStationName(List<FuelOrder> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        List<Long> stationIds = records.stream()
                .map(FuelOrder::getStationId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        if (stationIds.isEmpty()) {
            return;
        }
        Map<Long, String> nameMap = new HashMap<>();
        for (Station station : stationService.listByIds(stationIds)) {
            nameMap.put(station.getId(), station.getName());
        }
        records.forEach(order -> order.setStationName(nameMap.get(order.getStationId())));
    }

    private String generateOrderNo() {
        String time = LocalDateTime.now().format(ORDER_NO_FORMATTER);
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "GS" + time + random;
    }
}
