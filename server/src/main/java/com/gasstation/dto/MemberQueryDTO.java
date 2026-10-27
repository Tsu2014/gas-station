package com.gasstation.dto;

import com.gasstation.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberQueryDTO extends PageQuery {

    /** 昵称或手机号关键字 */
    private String keyword;

    private Integer status;
}
