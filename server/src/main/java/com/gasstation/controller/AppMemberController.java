package com.gasstation.controller;

import com.gasstation.common.R;
import com.gasstation.entity.BalanceLog;
import com.gasstation.entity.Member;
import com.gasstation.security.UserContext;
import com.gasstation.service.BalanceLogService;
import com.gasstation.service.MemberService;
import com.gasstation.vo.MemberVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序 - 会员中心
 */
@Tag(name = "小程序-会员")
@RestController
@RequestMapping("/api/app/member")
@RequiredArgsConstructor
public class AppMemberController {

    private final MemberService memberService;
    private final BalanceLogService balanceLogService;

    @Operation(summary = "会员信息")
    @GetMapping("/info")
    public R<MemberVO> info() {
        Member member = memberService.getById(UserContext.getUserId());
        if (member == null) {
            return R.fail(401, "会员不存在，请重新登录");
        }
        return R.ok(MemberVO.from(member));
    }

    @Operation(summary = "更新资料")
    @PutMapping("/profile")
    public R<MemberVO> updateProfile(@RequestBody Member body) {
        Long memberId = UserContext.getUserId();
        Member member = new Member();
        member.setId(memberId);
        member.setNickname(body.getNickname());
        member.setAvatar(body.getAvatar());
        member.setPhone(body.getPhone());
        memberService.updateById(member);
        return R.ok(MemberVO.from(memberService.getById(memberId)));
    }

    @Operation(summary = "余额 / 积分流水")
    @GetMapping("/balance-logs")
    public R<List<BalanceLog>> balanceLogs() {
        return R.ok(balanceLogService.list(new LambdaQueryWrapper<BalanceLog>()
                .eq(BalanceLog::getMemberId, UserContext.getUserId())
                .orderByDesc(BalanceLog::getId)));
    }
}
