package com.gasstation.controller;

import com.gasstation.common.PageQuery;
import com.gasstation.common.PageResult;
import com.gasstation.common.R;
import com.gasstation.entity.Coupon;
import com.gasstation.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台 - 优惠券管理
 */
@Tag(name = "优惠券管理")
@RestController
@RequestMapping("/api/admin/coupon")
@Validated
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    @Operation(summary = "优惠券分页列表")
    @GetMapping("/page")
    public R<PageResult<Coupon>> page(@Validated PageQuery query) {
        return R.ok(couponService.pageQuery(query));
    }

    @Operation(summary = "新增优惠券")
    @PostMapping
    public R<Boolean> create(@RequestBody Coupon coupon) {
        coupon.setId(null);
        if (coupon.getStatus() == null) {
            coupon.setStatus(1);
        }
        if (coupon.getRemain() == null) {
            coupon.setRemain(coupon.getTotal());
        }
        return R.ok(couponService.save(coupon));
    }

    @Operation(summary = "修改优惠券")
    @PutMapping
    public R<Boolean> update(@RequestBody Coupon coupon) {
        return R.ok(couponService.updateById(coupon));
    }

    @Operation(summary = "上架 / 下架")
    @PutMapping("/{id}/status")
    public R<Boolean> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        Coupon coupon = new Coupon();
        coupon.setId(id);
        coupon.setStatus(status);
        return R.ok(couponService.updateById(coupon));
    }

    @Operation(summary = "删除优惠券")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(couponService.removeById(id));
    }
}
