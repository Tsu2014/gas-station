package com.gasstation.vo;

import lombok.Data;

/**
 * 登录返回
 */
@Data
public class LoginVO {

    private String token;

    private Long userId;

    private String username;

    private String realName;

    private String role;

    private Long stationId;
}
