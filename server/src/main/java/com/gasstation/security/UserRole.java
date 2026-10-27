package com.gasstation.security;

import java.util.Arrays;

/**
 * 系统角色定义
 */
public enum UserRole {

    /** 超级管理员 */
    ADMIN("ADMIN"),
    /** 油站站长 */
    MANAGER("MANAGER"),
    /** 加油员 */
    STAFF("STAFF"),
    /** 小程序会员 */
    MEMBER("MEMBER");

    private final String code;

    UserRole(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    /** 后台管理端角色（可访问 /api/admin/**） */
    public static boolean isAdmin(String role) {
        if (role == null) {
            return false;
        }
        return Arrays.asList(ADMIN.code(), MANAGER.code(), STAFF.code()).contains(role);
    }
}
