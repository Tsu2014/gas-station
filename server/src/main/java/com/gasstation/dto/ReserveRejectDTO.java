package com.gasstation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 后台拒绝预约参数
 */
@Data
public class ReserveRejectDTO {

    @NotBlank(message = "请填写回复（拒绝原因）")
    @Size(max = 255, message = "回复不能超过 255 字")
    private String reply;
}
