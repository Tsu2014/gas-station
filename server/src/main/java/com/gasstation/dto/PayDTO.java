package com.gasstation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 支付参数
 */
@Data
public class PayDTO {

    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /** balance 余额 / wx 微信支付 */
    private String payType = "balance";
}
