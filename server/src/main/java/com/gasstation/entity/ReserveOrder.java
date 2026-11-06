package com.gasstation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gasstation.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预约加油订单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gs_reserve_order")
public class ReserveOrder extends BaseEntity {

    /** 0 待确认 1 已加油 2 已拒绝 3 已取消 */
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_FUELED = 1;
    public static final int STATUS_REJECTED = 2;
    public static final int STATUS_CANCELED = 3;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 预约单号 */
    private String reserveNo;

    private Long memberId;

    private Long stationId;

    /** 油站名称（快照） */
    private String stationName;

    /** 车牌号 */
    private String plateNo;

    /** 油品类型 */
    private String fuelType;

    /** 预约时单价（元/升） */
    private BigDecimal unitPrice;

    /** 预加油量（升） */
    private BigDecimal liters;

    /** 预估金额（元） */
    private BigDecimal estAmount;

    /** 预计到达时间 */
    private LocalDateTime expectTime;

    private Integer status;

    /** 后台回复（拒绝原因等） */
    private String reply;

    /** 后台处理时间 */
    private LocalDateTime handleTime;

    private String remark;

    /** 会员昵称（看板展示用，非表字段） */
    @TableField(exist = false)
    private String memberNickname;

    /** 会员手机号（看板展示用，非表字段） */
    @TableField(exist = false)
    private String memberPhone;
}
