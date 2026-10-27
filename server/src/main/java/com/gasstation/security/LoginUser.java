package com.gasstation.security;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户信息（存放在 ThreadLocal 中）
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户/会员 ID */
    private Long id;

    /** 用户名或昵称 */
    private String username;

    /** 角色：ADMIN / MANAGER / STAFF / MEMBER */
    private String role;

    /** 管理端用户绑定的油站 ID */
    private Long stationId;

    public boolean isAdmin() {
        return UserRole.isAdmin(role);
    }

    public boolean isMember() {
        return UserRole.MEMBER.code().equals(role);
    }
}
