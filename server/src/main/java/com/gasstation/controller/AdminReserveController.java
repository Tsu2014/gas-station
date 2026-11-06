package com.gasstation.controller;

import com.gasstation.common.PageResult;
import com.gasstation.common.R;
import com.gasstation.dto.ReserveQueryDTO;
import com.gasstation.dto.ReserveRejectDTO;
import com.gasstation.entity.ReserveOrder;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台 - 预约加油看板与历史
 */
@Tag(name = "后台-预约加油")
@RestController
@RequestMapping("/api/admin/reserve")
@Validated
@RequiredArgsConstructor
public class AdminReserveController {

    private final ReserveOrderService reserveOrderService;

    @Operation(summary = "看板：待确认的预约（按预计到达时间升序）")
    @GetMapping("/board")
    public R<List<ReserveOrder>> board() {
        return R.ok(reserveOrderService.board());
    }

    @Operation(summary = "确认已加油")
    @PostMapping("/{id}/confirm")
    public R<ReserveOrder> confirm(@PathVariable Long id) {
        return R.ok(reserveOrderService.confirm(id));
    }

    @Operation(summary = "拒绝预约（带回复）")
    @PostMapping("/{id}/reject")
    public R<ReserveOrder> reject(@PathVariable Long id, @Valid @RequestBody ReserveRejectDTO dto) {
        return R.ok(reserveOrderService.reject(id, dto.getReply()));
    }

    @Operation(summary = "历史预约订单（车牌号/时间查询）")
    @GetMapping("/page")
    public R<PageResult<ReserveOrder>> page(ReserveQueryDTO query) {
        return R.ok(reserveOrderService.adminPage(query));
    }
}
