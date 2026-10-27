package com.gasstation.controller;

import com.gasstation.common.R;
import com.gasstation.entity.Coupon;
import com.gasstation.entity.MemberCoupon;
import com.gasstation.security.UserContext;
import com.gasstation.service.CouponService;
import com.gasstation.service.MemberCouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 小程序 - 优惠券
 */
@Tag(name = "小程序-优惠券")
@RestController
@RequestMapping("/api/app/coupon")
@RequiredArgsConstructor
public class AppCouponController {

    private final CouponService couponService;
    private final MemberCouponService memberCouponService;

    @Operation(summary = "领券中心")
    @GetMapping("/center")
    public R<List<Coupon>> center() {
        Long memberId = UserContext.getUserId();
        List<Coupon> coupons = couponService.listOnSale();
        Set<Long> owned = new HashSet<>();
        for (MemberCoupon mc : memberCouponService.myList(memberId, null)) {
            owned.add(mc.getCouponId());
        }
        coupons.forEach(coupon -> coupon.setRemain(owned.contains(coupon.getId()) ? 0 : coupon.getRemain()));
        return R.ok(coupons);
    }

    @Operation(summary = "我的优惠券")
    @GetMapping("/mine")
    public R<List<MemberCoupon>> mine(@RequestParam(required = false) Integer status) {
        return R.ok(memberCouponService.myList(UserContext.getUserId(), status));
    }

    @Operation(summary = "领取优惠券")
    @PostMapping("/receive/{couponId}")
    public R<MemberCoupon> receive(@PathVariable Long couponId) {
        return R.ok(memberCouponService.receive(UserContext.getUserId(), couponId));
    }

    @Operation(summary = "下单时可用优惠券（带优惠后金额）")
    @GetMapping("/usable")
    public R<List<MemberCoupon>> usable(@RequestParam BigDecimal amount) {
        Long memberId = UserContext.getUserId();
        List<MemberCoupon> usableList = new ArrayList<>();
        for (MemberCoupon mc : memberCouponService.myList(memberId, MemberCoupon.STATUS_UNUSED)) {
            try {
                BigDecimal discount = memberCouponService.calcDiscount(mc, amount);
                mc.setDiscountAmount(discount);
                usableList.add(mc);
            } catch (Exception ignored) {
                // 不满足门槛的券不展示
            }
        }
        return R.ok(usableList);
    }
}
