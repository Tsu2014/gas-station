package com.gasstation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gasstation.common.BusinessException;
import com.gasstation.common.PageQuery;
import com.gasstation.common.PageResult;
import com.gasstation.entity.SysUser;
import com.gasstation.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 后台账号服务
 */
@Service
@RequiredArgsConstructor
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    private final PasswordEncoder passwordEncoder;

    public SysUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .last("LIMIT 1"));
    }

    /** 账号密码登录 */
    public SysUser login(String username, String rawPassword) {
        SysUser user = getByUsername(username);
        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        return user;
    }

    /** 保存账号：新增时密码加密，修改时密码留空则不更新 */
    public void saveOrUpdateUser(SysUser user) {
        boolean isNew = user.getId() == null;
        if (isNew) {
            if (getByUsername(user.getUsername()) != null) {
                throw new BusinessException("用户名已存在");
            }
            if (!StringUtils.hasText(user.getPassword())) {
                user.setPassword("123456");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (user.getStatus() == null) {
                user.setStatus(1);
            }
            save(user);
        } else {
            if (!StringUtils.hasText(user.getPassword())) {
                user.setPassword(null);
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            updateById(user);
        }
    }

    public PageResult<SysUser> pageQuery(PageQuery query) {
        return PageResult.of(page(query.toPage(), new LambdaQueryWrapper<SysUser>().orderByDesc(SysUser::getId)));
    }
}
