package com.gasstation.controller;

import com.gasstation.common.PageResult;
import com.gasstation.common.R;
import com.gasstation.dto.OrderQueryDTO;
import com.gasstation.entity.FuelOrder;
import com.gasstation.service.FuelOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台 - 订单管理
 */
@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/admin/order")
@Validated
@RequiredArgsConstructor
public class AdminOrderController {

    private final FuelOrderService fuelOrderService;

    @Operation(summary = "订单分页列表")
    @GetMapping("/page")
    public R<PageResult<FuelOrder>> page(@Validated OrderQueryDTO query) {
        return R.ok(fuelOrderService.adminPage(query));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public R<FuelOrder> detail(@PathVariable Long id) {
        return R.ok(fuelOrderService.getById(id));
    }

    @Operation(summary = "完成加油")
    @PostMapping("/{id}/finish")
    public R<Boolean> finish(@PathVariable Long id) {
        fuelOrderService.finish(id);
        return R.ok(true);
    }

    @Operation(summary = "订单退款")
    @PostMapping("/{id}/refund")
    public R<Boolean> refund(@PathVariable Long id) {
        fuelOrderService.refund(id);
        return R.ok(true);
    }
}
