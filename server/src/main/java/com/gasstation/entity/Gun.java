package com.gasstation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gasstation.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 加油枪
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gs_gun")
public class Gun extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long stationId;

    /** 枪号，如 01 */
    private String gunNo;

    /** 油品类型 92#/95#/98#/0# */
    private String fuelType;

    /** 1 空闲 2 使用中 3 故障 */
    private Integer status;
}
