package com.gasstation.controller;

import com.gasstation.common.PageResult;
import com.gasstation.common.R;
import com.gasstation.dto.AdjustBalanceDTO;
import com.gasstation.dto.MemberQueryDTO;
import com.gasstation.entity.BalanceLog;
import com.gasstation.entity.Member;
import com.gasstation.service.BalanceLogService;
import com.gasstation.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台 - 会员管理
 */
@Tag(name = "会员管理")
@RestController
@RequestMapping("/api/admin/member")
@Validated
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;
    private final BalanceLogService balanceLogService;

    @Operation(summary = "会员分页列表")
    @GetMapping("/page")
    public R<PageResult<Member>> page(@Validated MemberQueryDTO query) {
        return R.ok(memberService.pageQuery(query));
    }

    @Operation(summary = "会员详情")
    @GetMapping("/{id}")
    public R<Member> detail(@PathVariable Long id) {
        return R.ok(memberService.getById(id));
    }

    @Operation(summary = "启用 / 禁用会员")
    @PutMapping("/{id}/status")
    public R<Boolean> changeStatus(@PathVariable Long id, Integer status) {
        Member member = new Member();
        member.setId(id);
        member.setStatus(status);
        return R.ok(memberService.updateById(member));
    }

    @Operation(summary = "后台调整余额（正数为充值）")
    @PostMapping("/adjust-balance")
    public R<Boolean> adjustBalance(@Valid @RequestBody AdjustBalanceDTO dto) {
        memberService.adjustBalance(dto.getMemberId(), dto.getAmount(), BalanceLog.TYPE_ADJUST, null,
                dto.getRemark() == null ? "后台调整" : dto.getRemark());
        return R.ok(true);
    }

    @Operation(summary = "余额流水")
    @GetMapping("/{id}/balance-logs")
    public R<List<BalanceLog>> balanceLogs(@PathVariable Long id) {
        return R.ok(balanceLogService.list(new LambdaQueryWrapper<BalanceLog>()
                .eq(BalanceLog::getMemberId, id)
                .orderByDesc(BalanceLog::getId)));
    }
}
