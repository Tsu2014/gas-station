package com.gasstation.dto;

import com.gasstation.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderQueryDTO extends PageQuery {

    private String orderNo;

    private Integer status;

    private Long stationId;

    private String memberPhone;

    /** 起始日期 yyyy-MM-dd */
    private String startDate;

    /** 结束日期 yyyy-MM-dd */
    private String endDate;
}
