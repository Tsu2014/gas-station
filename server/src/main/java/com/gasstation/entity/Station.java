package com.gasstation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gasstation.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 加油站
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gs_station")
public class Station extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** 油站编码，用于生成二维码 */
    private String code;

    private String province;

    private String city;

    private String address;

    /** 经度 */
    private BigDecimal lng;

    /** 纬度 */
    private BigDecimal lat;

    private String phone;

    private String businessHours;

    /** 1 营业中 0 停业 */
    private Integer status;

    /** 非持久化：距离（公里），用于附近油站列表 */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private BigDecimal distance;
}
