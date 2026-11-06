package com.gasstation.dto;

import com.gasstation.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预约订单查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReserveQueryDTO extends PageQuery {

    /** 车牌号（模糊） */
    private String plateNo;

    private Integer status;

    private Long stationId;

    /** 起始日期 yyyy-MM-dd（按创建时间） */
    private String startDate;

    /** 结束日期 yyyy-MM-dd（按创建时间） */
    private String endDate;
}
