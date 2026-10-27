package com.gasstation.vo;

import com.gasstation.entity.Member;
import lombok.Data;

/**
 * 会员信息（脱敏后返回给小程序）
 */
@Data
public class MemberVO {

    private Long id;

    private String nickname;

    private String avatar;

    private String phone;

    private java.math.BigDecimal balance;

    private Integer points;

    private Integer level;

    private Integer status;

    public static MemberVO from(Member member) {
        MemberVO vo = new MemberVO();
        vo.setId(member.getId());
        vo.setNickname(member.getNickname());
        vo.setAvatar(member.getAvatar());
        vo.setPhone(member.getPhone());
        vo.setBalance(member.getBalance());
        vo.setPoints(member.getPoints());
        vo.setLevel(member.getLevel());
        vo.setStatus(member.getStatus());
        return vo;
    }
}
