package com.gasstation.controller;

import com.gasstation.common.PageResult;
import com.gasstation.common.R;
import com.gasstation.dto.ReserveCreateDTO;
import com.gasstation.entity.ReserveOrder;
import com.gasstation.security.UserContext;
import com.gasstation.service.ReserveOrderService;
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
 * 小程序 - 预约加油
 */
@Tag(name = "小程序-预约加油")
@RestController
@RequestMapping("/api/app/reserve")
@Validated
@RequiredArgsConstructor
public class AppReserveController {

    private final ReserveOrderService reserveOrderService;

    @Operation(summary = "发起预约加油（带车牌号）")
    @PostMapping("/create")
    public R<ReserveOrder> create(@Valid @RequestBody ReserveCreateDTO dto) {
        return R.ok(reserveOrderService.create(UserContext.getUserId(), dto));
    }

    @Operation(summary = "取消预约（油站确认前）")
    @PostMapping("/cancel/{id}")
    public R<ReserveOrder> cancel(@PathVariable Long id) {
        return R.ok(reserveOrderService.cancel(UserContext.getUserId(), id));
    }

    @Operation(summary = "当前进行中的预约")
    @GetMapping("/active")
    public R<ReserveOrder> active() {
        return R.ok(reserveOrderService.active(UserContext.getUserId()));
    }

    @Operation(summary = "我的预约记录")
    @GetMapping("/page")
    public R<PageResult<ReserveOrder>> page(@RequestParam(defaultValue = "1") int current,
                                            @RequestParam(defaultValue = "10") int size) {
        return R.ok(reserveOrderService.myPage(UserContext.getUserId(), current, size));
    }
}
