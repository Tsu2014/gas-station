package com.gasstation.controller;

import com.gasstation.common.R;
import com.gasstation.entity.Gun;
import com.gasstation.service.GunService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台 - 加油枪管理
 */
@Tag(name = "加油枪管理")
@RestController
@RequestMapping("/api/admin/gun")
@RequiredArgsConstructor
public class AdminGunController {

    private final GunService gunService;

    @Operation(summary = "油枪列表")
    @GetMapping("/list")
    public R<List<Gun>> list(@RequestParam Long stationId) {
        return R.ok(gunService.listByStation(stationId));
    }

    @Operation(summary = "新增油枪")
    @PostMapping
    public R<Boolean> create(@RequestBody Gun gun) {
        gun.setId(null);
        if (gun.getStatus() == null) {
            gun.setStatus(GunService.STATUS_FREE);
        }
        return R.ok(gunService.save(gun));
    }

    @Operation(summary = "修改油枪")
    @PutMapping
    public R<Boolean> update(@RequestBody Gun gun) {
        return R.ok(gunService.updateById(gun));
    }

    @Operation(summary = "变更油枪状态")
    @PutMapping("/{id}/status")
    public R<Boolean> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        gunService.changeStatus(id, status);
        return R.ok(true);
    }

    @Operation(summary = "删除油枪")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(gunService.removeById(id));
    }
}
