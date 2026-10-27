package com.gasstation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gasstation.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台账号（超管 / 站长 / 加油员）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gs_user")
public class SysUser extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String realName;

    private String phone;

    /** ADMIN / MANAGER / STAFF */
    private String role;

    /** 所属油站，超管可为空 */
    private Long stationId;

    /** 1 启用 0 禁用 */
    private Integer status;
}
