package com.gasstation.dto;

import com.gasstation.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 油站查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StationQueryDTO extends PageQuery {

    /** 油站名称关键字 */
    private String keyword;

    /** 状态：1 营业 0 停业 */
    private Integer status;
}
