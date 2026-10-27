package com.gasstation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gasstation.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 会员（小程序用户）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gs_member")
public class Member extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 微信 openid */
    private String openid;

    private String nickname;

    private String avatar;

    private String phone;

    /** 账户余额 */
    private BigDecimal balance;

    /** 积分 */
    private Integer points;

    /** 会员等级 1 普通 2 银卡 3 金卡 */
    private Integer level;

    /** 1 正常 0 禁用 */
    private Integer status;
}
