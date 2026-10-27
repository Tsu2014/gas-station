package com.gasstation.controller;

import com.gasstation.common.R;
import com.gasstation.entity.FuelPrice;
import com.gasstation.service.FuelPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台 - 油价管理
 */
@Tag(name = "油价管理")
@RestController
@RequestMapping("/api/admin/fuel-price")
@RequiredArgsConstructor
public class AdminFuelPriceController {

    private final FuelPriceService fuelPriceService;

    @Operation(summary = "油价列表")
    @GetMapping("/list")
    public R<List<FuelPrice>> list(@RequestParam Long stationId) {
        return R.ok(fuelPriceService.listByStation(stationId));
    }

    @Operation(summary = "新增油价")
    @PostMapping
    public R<Boolean> create(@RequestBody FuelPrice price) {
        price.setId(null);
        fuelPriceService.saveOrUpdatePrice(price);
        return R.ok(true);
    }

    @Operation(summary = "修改油价")
    @PutMapping
    public R<Boolean> update(@RequestBody FuelPrice price) {
        fuelPriceService.saveOrUpdatePrice(price);
        return R.ok(true);
    }

    @Operation(summary = "删除油价")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@org.springframework.web.bind.annotation.PathVariable Long id) {
        return R.ok(fuelPriceService.removeById(id));
    }
}
