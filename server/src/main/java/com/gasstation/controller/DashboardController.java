package com.gasstation.controller;

import com.gasstation.common.R;
import com.gasstation.service.DashboardService;
import com.gasstation.vo.DashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台 - 首页统计
 */
@Tag(name = "数据统计")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "首页汇总数据")
    @GetMapping("/summary")
    public R<DashboardVO> summary() {
        return R.ok(dashboardService.summary());
    }
}
