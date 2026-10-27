package com.gasstation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 小程序登录参数
 */
@Data
public class MiniLoginDTO {

    /** wx.login 返回的临时凭证 */
    @NotBlank(message = "code 不能为空")
    private String code;

    /** 微信昵称（授权后） */
    private String nickname;

    /** 微信头像 */
    private String avatar;
}
