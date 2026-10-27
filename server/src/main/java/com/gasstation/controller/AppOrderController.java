package com.gasstation.controller;

import com.gasstation.common.PageResult;
import com.gasstation.common.R;
import com.gasstation.dto.PayDTO;
import com.gasstation.dto.PlaceOrderDTO;
import com.gasstation.entity.FuelOrder;
import com.gasstation.security.UserContext;
import com.gasstation.service.FuelOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序 - 加油订单
 */
@Tag(name = "小程序-订单")
@RestController
@RequestMapping("/api/app/order")
@Validated
@RequiredArgsConstructor
public class AppOrderController {

    private final FuelOrderService fuelOrderService;

    @Operation(summary = "下单（生成待支付订单）")
    @PostMapping("/place")
    public R<FuelOrder> place(@Valid @RequestBody PlaceOrderDTO dto) {
        return R.ok(fuelOrderService.placeOrder(UserContext.getUserId(), dto));
    }

    @Operation(summary = "支付订单")
    @PostMapping("/pay")
    public R<FuelOrder> pay(@Valid @RequestBody PayDTO dto) {
        return R.ok(fuelOrderService.pay(UserContext.getUserId(), dto.getOrderNo(), dto.getPayType()));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel/{orderNo}")
    public R<FuelOrder> cancel(@PathVariable String orderNo) {
        return R.ok(fuelOrderService.cancel(UserContext.getUserId(), orderNo));
    }

    @Operation(summary = "我的订单")
    @GetMapping("/page")
    public R<PageResult<FuelOrder>> page(@RequestParam(defaultValue = "1") int current,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) Integer status) {
        return R.ok(fuelOrderService.myPage(UserContext.getUserId(), status, current, size));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{orderNo}")
    public R<FuelOrder> detail(@PathVariable String orderNo) {
        return R.ok(fuelOrderService.getByOrderNo(orderNo));
    }
}
