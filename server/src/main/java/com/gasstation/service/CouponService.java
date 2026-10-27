package com.gasstation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gasstation.common.BusinessException;
import com.gasstation.common.PageQuery;
import com.gasstation.common.PageResult;
import com.gasstation.entity.Coupon;
import com.gasstation.mapper.CouponMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券模板服务
 */
@Service
public class CouponService extends ServiceImpl<CouponMapper, Coupon> {

    /** 小程序领券中心：上架且在有效期内的券 */
    public List<Coupon> listOnSale() {
        return list(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getStatus, 1)
                .ge(Coupon::getEndTime, LocalDateTime.now())
                .le(Coupon::getStartTime, LocalDateTime.now())
                .orderByDesc(Coupon::getId));
    }

    public PageResult<Coupon> pageQuery(PageQuery query) {
        return PageResult.of(page(query.toPage(), new LambdaQueryWrapper<Coupon>().orderByDesc(Coupon::getId)));
    }

    /** 扣减库存 */
    public Coupon decreaseStock(Long couponId) {
        Coupon coupon = getById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (coupon.getStatus() == null || coupon.getStatus() != 1) {
            throw new BusinessException("优惠券已下架");
        }
        if (coupon.getEndTime() != null && coupon.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("优惠券已过期");
        }
        if (coupon.getRemain() == null || coupon.getRemain() <= 0) {
            throw new BusinessException("优惠券已被抢光");
        }
        coupon.setRemain(coupon.getRemain() - 1);
        updateById(coupon);
        return coupon;
    }
}
