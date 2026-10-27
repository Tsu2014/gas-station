package com.gasstation.controller;

import com.gasstation.common.R;
import com.gasstation.entity.Station;
import com.gasstation.service.StationService;
import com.gasstation.vo.StationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 公共接口 - 油站查询（小程序首页）
 */
@Tag(name = "油站查询（公开）")
@RestController
@RequestMapping("/api/public/station")
@RequiredArgsConstructor
public class PublicStationController {

    private final StationService stationService;

    @Operation(summary = "附近油站")
    @GetMapping("/nearby")
    public R<List<Station>> nearby(@RequestParam(required = false) BigDecimal lng,
                                   @RequestParam(required = false) BigDecimal lat,
                                   @RequestParam(required = false) String keyword) {
        BigDecimal useLng = lng == null ? new BigDecimal("116.397128") : lng;
        BigDecimal useLat = lat == null ? new BigDecimal("39.916527") : lat;
        return R.ok(stationService.nearby(useLng, useLat, keyword));
    }

    @Operation(summary = "油站详情（含油价、油枪）")
    @GetMapping("/{id}")
    public R<StationVO> detail(@PathVariable Long id) {
        return R.ok(stationService.detail(id));
    }
}
