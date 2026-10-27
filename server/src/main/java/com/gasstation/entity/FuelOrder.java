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
 * 加油订单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gs_order")
public class FuelOrder extends BaseEntity {

    /** 0 待支付 1 已支付 2 已完成 3 已取消 4 已退款 */
    public static final int STATUS_UNPAID = 0;
    public static final int STATUS_PAID = 1;
    public static final int STATUS_FINISHED = 2;
    public static final int STATUS_CANCELED = 3;
    public static final int STATUS_REFUNDED = 4;

    /** 支付方式：balance 余额 / wx 微信 */
    public static final String PAY_BALANCE = "balance";
    public static final String PAY_WX = "wx";

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long memberId;

    private Long stationId;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String stationName;

    private Long gunId;

    private String gunNo;

    /** 油品类型 */
    private String fuelType;

    /** 单价（元/升） */
    private BigDecimal unitPrice;

    /** 加油升数 */
    private BigDecimal liters;

    /** 原始金额 */
    private BigDecimal amount;

    private Long couponId;

    /** 优惠金额 */
    private BigDecimal discountAmount;

    /** 实付金额 */
    private BigDecimal payAmount;

    /** 获得积分 */
    private Integer points;

    private String payType;

    private Integer status;

    private LocalDateTime payTime;

    private String remark;
}
