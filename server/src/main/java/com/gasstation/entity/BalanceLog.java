package com.gasstation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gasstation.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 会员余额 / 积分流水
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gs_balance_log")
public class BalanceLog extends BaseEntity {

    /** 1 充值 2 消费 3 退款 4 后台调整 */
    public static final int TYPE_RECHARGE = 1;
    public static final int TYPE_CONSUME = 2;
    public static final int TYPE_REFUND = 3;
    public static final int TYPE_ADJUST = 4;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    /** 1 充值 2 消费 3 退款 4 后台调整 */
    private Integer type;

    /** 变动金额，正数为增加 */
    private BigDecimal amount;

    /** 变动后余额 */
    private BigDecimal balanceAfter;

    /** 关联业务单号 */
    private String bizNo;

    private String remark;
}
