package com.gasstation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gasstation.common.BusinessException;
import com.gasstation.entity.Coupon;
import com.gasstation.entity.MemberCoupon;
import com.gasstation.mapper.MemberCouponMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会员优惠券服务
 */
@Service
@RequiredArgsConstructor
public class MemberCouponService extends ServiceImpl<MemberCouponMapper, MemberCoupon> {

    private final CouponService couponService;

    /** 我的优惠券 */
    public List<MemberCoupon> myList(Long memberId, Integer status) {
        if (status != null) {
            return baseMapper.selectAvailable(memberId, status);
        }
        return list(new LambdaQueryWrapper<MemberCoupon>()
                .eq(MemberCoupon::getMemberId, memberId)
                .orderByDesc(MemberCoupon::getId));
    }

    /** 领券 */
    @Transactional(rollbackFor = Exception.class)
    public MemberCoupon receive(Long memberId, Long couponId) {
        Coupon coupon = couponService.decreaseStock(couponId);
        long owned = count(new LambdaQueryWrapper<MemberCoupon>()
                .eq(MemberCoupon::getMemberId, memberId)
                .eq(MemberCoupon::getCouponId, couponId));
        if (owned > 0) {
            throw new BusinessException("每人限领一张，请勿重复领取");
        }
        MemberCoupon memberCoupon = new MemberCoupon();
        memberCoupon.setMemberId(memberId);
        memberCoupon.setCouponId(couponId);
        memberCoupon.setStatus(MemberCoupon.STATUS_UNUSED);
        memberCoupon.setObtainTime(LocalDateTime.now());
        save(memberCoupon);
        return memberCoupon;
    }

    /** 核销 */
    @Transactional(rollbackFor = Exception.class)
    public void useCoupon(Long memberCouponId, Long orderId, BigDecimal discountAmount) {
        MemberCoupon memberCoupon = new MemberCoupon();
        memberCoupon.setId(memberCouponId);
        memberCoupon.setStatus(MemberCoupon.STATUS_USED);
        memberCoupon.setOrderId(orderId);
        memberCoupon.setDiscountAmount(discountAmount);
        memberCoupon.setUseTime(LocalDateTime.now());
        updateById(memberCoupon);
    }

    /**
     * 计算优惠金额
     */
    public BigDecimal calcDiscount(MemberCoupon memberCoupon, BigDecimal orderAmount) {
        Coupon coupon = couponService.getById(memberCoupon.getCouponId());
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (coupon.getEndTime() != null && coupon.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("优惠券已过期");
        }
        if (coupon.getStartTime() != null && coupon.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("优惠券尚未生效");
        }
        BigDecimal discount;
        if (coupon.getType() != null && coupon.getType() == Coupon.TYPE_DISCOUNT) {
            // 折扣券
            BigDecimal rate = coupon.getDiscount() == null ? BigDecimal.ONE : coupon.getDiscount();
            discount = orderAmount.multiply(BigDecimal.ONE.subtract(rate));
        } else {
            // 满减券
            if (coupon.getThreshold() != null && orderAmount.compareTo(coupon.getThreshold()) < 0) {
                throw new BusinessException("订单金额未达到优惠券使用门槛");
            }
            discount = coupon.getAmount() == null ? BigDecimal.ZERO : coupon.getAmount();
        }
        discount = discount.setScale(2, RoundingMode.HALF_UP);
        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }
        return discount;
    }
}
