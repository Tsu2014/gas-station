package com.gasstation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gasstation.common.BusinessException;
import com.gasstation.common.PageResult;
import com.gasstation.dto.MemberQueryDTO;
import com.gasstation.entity.BalanceLog;
import com.gasstation.entity.Member;
import com.gasstation.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 会员服务
 */
@Service
@RequiredArgsConstructor
public class MemberService extends ServiceImpl<MemberMapper, Member> {

    private final BalanceLogService balanceLogService;

    public Member getByOpenid(String openid) {
        return getOne(new LambdaQueryWrapper<Member>()
                .eq(Member::getOpenid, openid)
                .last("LIMIT 1"));
    }

    /** 首次登录自动注册 */
    @Transactional(rollbackFor = Exception.class)
    public Member register(String openid, String nickname, String avatar) {
        Member member = new Member();
        member.setOpenid(openid);
        member.setNickname(StringUtils.hasText(nickname) ? nickname : "微信用户" + openid.substring(Math.max(0, openid.length() - 4)));
        member.setAvatar(avatar);
        member.setBalance(BigDecimal.ZERO.setScale(2));
        member.setPoints(0);
        member.setLevel(1);
        member.setStatus(1);
        member.setCreateTime(LocalDateTime.now());
        save(member);
        return member;
    }

    /** 调整余额（正数为充值） */
    @Transactional(rollbackFor = Exception.class)
    public Member adjustBalance(Long memberId, BigDecimal delta, int type, String bizNo, String remark) {
        Member member = getById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        BigDecimal balance = (member.getBalance() == null ? BigDecimal.ZERO : member.getBalance()).add(delta);
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("会员余额不足");
        }
        member.setBalance(balance.setScale(2, RoundingMode.HALF_UP));
        updateById(member);
        balanceLogService.record(memberId, type, delta, member.getBalance(), bizNo, remark);
        return member;
    }

    /** 增加积分 */
    @Transactional(rollbackFor = Exception.class)
    public void addPoints(Long memberId, int points) {
        if (points <= 0) {
            return;
        }
        Member member = getById(memberId);
        if (member == null) {
            return;
        }
        member.setPoints((member.getPoints() == null ? 0 : member.getPoints()) + points);
        updateById(member);
    }

    /** 后台分页查询 */
    public PageResult<Member> pageQuery(MemberQueryDTO query) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<Member>()
                .and(StringUtils.hasText(query.getKeyword()), w -> w
                        .like(Member::getNickname, query.getKeyword())
                        .or()
                        .like(Member::getPhone, query.getKeyword()))
                .eq(query.getStatus() != null, Member::getStatus, query.getStatus())
                .orderByDesc(Member::getId);
        return PageResult.of(page(query.toPage(), wrapper));
    }

    /** 消费：扣减余额 */
    public Member consume(Long memberId, BigDecimal amount, String bizNo) {
        return adjustBalance(memberId, amount.negate(), BalanceLog.TYPE_CONSUME, bizNo, "加油消费");
    }
}
