package com.gasstation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 下单参数：金额与升数二选一
 */
@Data
public class PlaceOrderDTO {

    @NotNull(message = "请选择油站")
    private Long stationId;

    /** 加油枪 ID，扫码加油时必填 */
    private Long gunId;

    /** 未选枪时按油品下单 */
    private String fuelType;

    /** 加油金额（元），与升数二选一 */
    private BigDecimal amount;

    /** 加油升数（升），与金额二选一 */
    private BigDecimal liters;

    /** 会员优惠券 ID（gs_member_coupon 主键） */
    private Long memberCouponId;

    private String remark;
}
