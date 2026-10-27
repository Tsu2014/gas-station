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
 * 油价（按油站 + 油品）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gs_fuel_price")
public class FuelPrice extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long stationId;

    /** 油品类型 92#/95#/98#/0# */
    private String fuelType;

    /** 挂牌价（元/升） */
    private BigDecimal listPrice;

    /** 实际售价（元/升） */
    private BigDecimal salePrice;

    /** 生效时间 */
    private LocalDateTime effectiveTime;

    /** 1 生效 0 失效 */
    private Integer status;
}
