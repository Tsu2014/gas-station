package com.gasstation.controller;

import com.gasstation.common.PageQuery;
import com.gasstation.common.PageResult;
import com.gasstation.common.R;
import com.gasstation.entity.SysUser;
import com.gasstation.service.SysUserService;
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

/**
 * 后台 - 员工 / 账号管理
 */
@Tag(name = "账号管理")
@RestController
@RequestMapping("/api/admin/user")
@Validated
@RequiredArgsConstructor
public class AdminUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "账号分页列表")
    @GetMapping("/page")
    public R<PageResult<SysUser>> page(@Validated PageQuery query) {
        return R.ok(sysUserService.pageQuery(query));
    }

    @Operation(summary = "新增账号")
    @PostMapping
    public R<Boolean> create(@RequestBody SysUser user) {
        user.setId(null);
        sysUserService.saveOrUpdateUser(user);
        return R.ok(true);
    }

    @Operation(summary = "修改账号（密码留空表示不修改）")
    @PutMapping
    public R<Boolean> update(@RequestBody SysUser user) {
        sysUserService.saveOrUpdateUser(user);
        return R.ok(true);
    }

    @Operation(summary = "删除账号")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(sysUserService.removeById(id));
    }
}
