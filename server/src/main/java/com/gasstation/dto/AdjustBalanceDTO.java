package com.gasstation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 后台调整会员余额
 */
@Data
public class AdjustBalanceDTO {

    @NotNull(message = "会员不能为空")
    private Long memberId;

    /** 正数充值，负数扣减 */
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    private String remark;
}
