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
 * 优惠券模板
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gs_coupon")
public class Coupon extends BaseEntity {

    /** 1 满减券 2 折扣券 */
    public static final int TYPE_CASH = 1;
    public static final int TYPE_DISCOUNT = 2;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** 1 满减 2 折扣 */
    private Integer type;

    /** 使用门槛（满减金额） */
    private BigDecimal threshold;

    /** 优惠金额（满减券） */
    private BigDecimal amount;

    /** 折扣率，如 0.95（折扣券） */
    private BigDecimal discount;

    /** 发放总量 */
    private Integer total;

    /** 剩余数量 */
    private Integer remain;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    /** 1 上架 0 下架 */
    private Integer status;
}
