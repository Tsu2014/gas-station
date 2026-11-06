package com.gasstation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gasstation.common.BusinessException;
import com.gasstation.common.PageResult;
import com.gasstation.dto.ReserveCreateDTO;
import com.gasstation.dto.ReserveQueryDTO;
import com.gasstation.entity.Member;
import com.gasstation.entity.ReserveOrder;
import com.gasstation.entity.Station;
import com.gasstation.mapper.ReserveOrderMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 预约加油订单服务
 */
@Service
@RequiredArgsConstructor
public class ReserveOrderService extends ServiceImpl<ReserveOrderMapper, ReserveOrder> {

    private static final DateTimeFormatter RESERVE_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final StationService stationService;
    private final FuelPriceService fuelPriceService;
    private final MemberService memberService;

    /**
     * 小程序发起预约加油。同一会员同一时间仅允许一个进行中的预约。
     */
    @Transactional(rollbackFor = Exception.class)
    public ReserveOrder create(Long memberId, ReserveCreateDTO dto) {
        long activeCount = count(new LambdaQueryWrapper<ReserveOrder>()
                .eq(ReserveOrder::getMemberId, memberId)
                .eq(ReserveOrder::getStatus, ReserveOrder.STATUS_PENDING));
        if (activeCount > 0) {
            throw new BusinessException("您已有进行中的预约，请先完成或取消后再预约");
        }

        Station station = stationService.getById(dto.getStationId());
        if (station == null) {
            throw new BusinessException("油站不存在");
        }
        if (station.getStatus() == null || station.getStatus() != 1) {
            throw new BusinessException("该油站已停业，无法预约");
        }

        BigDecimal unitPrice = fuelPriceService.getUnitPrice(dto.getStationId(), dto.getFuelType());
        BigDecimal liters = dto.getLiters().setScale(2, RoundingMode.HALF_UP);
        BigDecimal estAmount = liters.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
        String plateNo = dto.getPlateNo().trim().toUpperCase();

        ReserveOrder order = new ReserveOrder();
        order.setReserveNo(generateReserveNo());
        order.setMemberId(memberId);
        order.setStationId(station.getId());
        order.setStationName(station.getName());
        order.setPlateNo(plateNo);
        order.setFuelType(dto.getFuelType());
        order.setUnitPrice(unitPrice);
        order.setLiters(liters);
        order.setEstAmount(estAmount);
        order.setExpectTime(LocalDateTime.now().plusMinutes(dto.getExpectMinutes()));
        order.setStatus(ReserveOrder.STATUS_PENDING);
        order.setRemark(dto.getRemark());
        save(order);

        // 回写会员车牌与默认油站，下次自动带出
        Member memberUpdate = new Member();
        memberUpdate.setId(memberId);
        memberUpdate.setPlateNo(plateNo);
        if (Boolean.TRUE.equals(dto.getSetDefaultStation())) {
            memberUpdate.setDefaultStationId(station.getId());
        }
        memberService.updateById(memberUpdate);
        return order;
    }

    /**
     * 会员取消预约（仅待确认且本人）
     */
    @Transactional(rollbackFor = Exception.class)
    public ReserveOrder cancel(Long memberId, Long id) {
        ReserveOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("预约不存在");
        }
        if (!order.getMemberId().equals(memberId)) {
            throw new BusinessException("无权操作该预约");
        }
        if (order.getStatus() != ReserveOrder.STATUS_PENDING) {
            throw new BusinessException("油站已处理，无法取消");
        }
        order.setStatus(ReserveOrder.STATUS_CANCELED);
        updateById(order);
        return order;
    }

    /** 会员当前进行中的预约 */
    public ReserveOrder active(Long memberId) {
        return getOne(new LambdaQueryWrapper<ReserveOrder>()
                .eq(ReserveOrder::getMemberId, memberId)
                .eq(ReserveOrder::getStatus, ReserveOrder.STATUS_PENDING)
                .orderByDesc(ReserveOrder::getId)
                .last("LIMIT 1"));
    }

    /** 会员预约记录 */
    public PageResult<ReserveOrder> myPage(Long memberId, int current, int size) {
        IPage<ReserveOrder> page = page(new Page<>(current, size), new LambdaQueryWrapper<ReserveOrder>()
                .eq(ReserveOrder::getMemberId, memberId)
                .orderByDesc(ReserveOrder::getId));
        return PageResult.of(page);
    }

    /** 后台看板：全部待确认预约，按预计到达时间升序 */
    public List<ReserveOrder> board() {
        List<ReserveOrder> list = list(new LambdaQueryWrapper<ReserveOrder>()
                .eq(ReserveOrder::getStatus, ReserveOrder.STATUS_PENDING)
                .orderByAsc(ReserveOrder::getExpectTime)
                .orderByAsc(ReserveOrder::getId));
        fillMemberInfo(list);
        return list;
    }

    /** 后台确认已加油 */
    @Transactional(rollbackFor = Exception.class)
    public ReserveOrder confirm(Long id) {
        ReserveOrder order = checkPending(id);
        order.setStatus(ReserveOrder.STATUS_FUELED);
        order.setHandleTime(LocalDateTime.now());
        updateById(order);
        return order;
    }

    /** 后台拒绝预约（带回复） */
    @Transactional(rollbackFor = Exception.class)
    public ReserveOrder reject(Long id, String reply) {
        ReserveOrder order = checkPending(id);
        order.setStatus(ReserveOrder.STATUS_REJECTED);
        order.setReply(reply);
        order.setHandleTime(LocalDateTime.now());
        updateById(order);
        return order;
    }

    /** 后台历史预约分页（车牌号 / 状态 / 时间） */
    public PageResult<ReserveOrder> adminPage(ReserveQueryDTO query) {
        LambdaQueryWrapper<ReserveOrder> wrapper = new LambdaQueryWrapper<ReserveOrder>()
                .like(StringUtils.hasText(query.getPlateNo()), ReserveOrder::getPlateNo,
                        query.getPlateNo() == null ? null : query.getPlateNo().trim().toUpperCase())
                .eq(query.getStatus() != null, ReserveOrder::getStatus, query.getStatus())
                .eq(query.getStationId() != null, ReserveOrder::getStationId, query.getStationId())
                .orderByDesc(ReserveOrder::getId);
        if (StringUtils.hasText(query.getStartDate()) && StringUtils.hasText(query.getEndDate())) {
            wrapper.between(ReserveOrder::getCreateTime,
                    LocalDateTime.of(LocalDate.parse(query.getStartDate()), LocalTime.MIN),
                    LocalDateTime.of(LocalDate.parse(query.getEndDate()), LocalTime.MAX));
        }
        IPage<ReserveOrder> page = page(query.toPage(), wrapper);
        fillMemberInfo(page.getRecords());
        return PageResult.of(page);
    }

    private ReserveOrder checkPending(Long id) {
        ReserveOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("预约不存在");
        }
        if (order.getStatus() != ReserveOrder.STATUS_PENDING) {
            throw new BusinessException("该预约已处理，请刷新看板");
        }
        return order;
    }

    private void fillMemberInfo(List<ReserveOrder> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        List<Long> memberIds = records.stream()
                .map(ReserveOrder::getMemberId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        if (memberIds.isEmpty()) {
            return;
        }
        Map<Long, Member> memberMap = new HashMap<>();
        for (Member member : memberService.listByIds(memberIds)) {
            memberMap.put(member.getId(), member);
        }
        records.forEach(order -> {
            Member member = memberMap.get(order.getMemberId());
            if (member != null) {
                order.setMemberNickname(member.getNickname());
                order.setMemberPhone(member.getPhone());
            }
        });
    }

    private String generateReserveNo() {
        String time = LocalDateTime.now().format(RESERVE_NO_FORMATTER);
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "YY" + time + random;
    }
}
