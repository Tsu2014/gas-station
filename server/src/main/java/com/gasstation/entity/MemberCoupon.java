package com.gasstation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gasstation.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员已领取的优惠券
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gs_member_coupon")
public class MemberCoupon extends BaseEntity {

    /** 0 未使用 1 已使用 2 已过期 */
    public static final int STATUS_UNUSED = 0;
    public static final int STATUS_USED = 1;
    public static final int STATUS_EXPIRED = 2;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private Long couponId;

    /** 0 未使用 1 已使用 2 已过期 */
    private Integer status;

    /** 实际抵扣金额 */
    private BigDecimal discountAmount;

    private Long orderId;

    private LocalDateTime obtainTime;

    private LocalDateTime useTime;

    /** 冗余展示字段 */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String couponName;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Integer couponType;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private BigDecimal threshold;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private BigDecimal amount;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private BigDecimal couponDiscount;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private LocalDateTime endTime;
}
