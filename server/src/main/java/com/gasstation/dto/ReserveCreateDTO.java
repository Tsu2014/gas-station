package com.gasstation.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 预约加油下单参数
 */
@Data
public class ReserveCreateDTO {

    @NotNull(message = "请选择加油站")
    private Long stationId;

    @NotBlank(message = "请填写车牌号")
    @Size(max = 16, message = "车牌号长度不能超过 16 位")
    private String plateNo;

    @NotBlank(message = "请选择油品")
    private String fuelType;

    @NotNull(message = "请填写预加油量")
    @DecimalMin(value = "1", message = "预加油量至少 1 升")
    @DecimalMax(value = "500", message = "预加油量不能超过 500 升")
    private BigDecimal liters;

    /** 预计多少分钟后到达（如 30 / 60 / 120） */
    @NotNull(message = "请选择预计到达时间")
    @Min(value = 5, message = "预计到达时间至少 5 分钟后")
    @Max(value = 720, message = "预计到达时间不能超过 12 小时")
    private Integer expectMinutes;

    /** 是否将该油站设为默认加油站 */
    private Boolean setDefaultStation;

    private String remark;
}
