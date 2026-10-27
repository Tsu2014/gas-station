package com.gasstation.controller;

import com.gasstation.common.PageResult;
import com.gasstation.common.R;
import com.gasstation.dto.StationQueryDTO;
import com.gasstation.entity.Station;
import com.gasstation.service.StationService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台 - 油站管理
 */
@Tag(name = "油站管理")
@RestController
@RequestMapping("/api/admin/station")
@Validated
@RequiredArgsConstructor
public class AdminStationController {

    private final StationService stationService;

    @Operation(summary = "油站分页列表")
    @GetMapping("/page")
    public R<PageResult<Station>> page(@Validated StationQueryDTO query) {
        return R.ok(stationService.pageQuery(query));
    }

    @Operation(summary = "全部油站（下拉选择）")
    @GetMapping("/list")
    public R<List<Station>> list() {
        return R.ok(stationService.list());
    }

    @Operation(summary = "油站详情")
    @GetMapping("/{id}")
    public R<Station> detail(@PathVariable Long id) {
        return R.ok(stationService.getById(id));
    }

    @Operation(summary = "新增油站")
    @PostMapping
    public R<Boolean> create(@RequestBody Station station) {
        station.setId(null);
        stationService.saveOrUpdateStation(station);
        return R.ok(true);
    }

    @Operation(summary = "修改油站")
    @PutMapping
    public R<Boolean> update(@RequestBody Station station) {
        stationService.saveOrUpdateStation(station);
        return R.ok(true);
    }

    @Operation(summary = "删除油站")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(stationService.removeById(id));
    }
}
