package com.gasstation.controller;

import com.gasstation.common.BusinessException;
import com.gasstation.common.R;
import com.gasstation.dto.LoginDTO;
import com.gasstation.dto.MiniLoginDTO;
import com.gasstation.entity.Member;
import com.gasstation.entity.SysUser;
import com.gasstation.security.JwtUtil;
import com.gasstation.security.LoginUser;
import com.gasstation.security.UserContext;
import com.gasstation.security.UserRole;
import com.gasstation.service.MemberService;
import com.gasstation.service.SysUserService;
import com.gasstation.service.WxService;
import com.gasstation.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录认证
 */
@Tag(name = "登录认证")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final MemberService memberService;
    private final WxService wxService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "后台账号登录")
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        SysUser user = sysUserService.login(dto.getUsername(), dto.getPassword());
        LoginUser loginUser = new LoginUser();
        loginUser.setId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setRole(user.getRole());
        loginUser.setStationId(user.getStationId());
        return R.ok(buildVO(loginUser, user.getRealName()));
    }

    @Operation(summary = "微信小程序登录")
    @PostMapping("/mini-login")
    public R<LoginVO> miniLogin(@Valid @RequestBody MiniLoginDTO dto) {
        String openid = wxService.getOpenid(dto.getCode());
        Member member = memberService.getByOpenid(openid);
        if (member == null) {
            member = memberService.register(openid, dto.getNickname(), dto.getAvatar());
        } else if (dto.getNickname() != null) {
            member.setNickname(dto.getNickname());
            member.setAvatar(dto.getAvatar());
            memberService.updateById(member);
        }
        if (member.getStatus() != null && member.getStatus() != 1) {
            throw new BusinessException("账号已被禁用，请联系客服");
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setId(member.getId());
        loginUser.setUsername(member.getNickname());
        loginUser.setRole(UserRole.MEMBER.code());
        return R.ok(buildVO(loginUser, member.getNickname()));
    }

    @Operation(summary = "当前登录信息")
    @GetMapping("/info")
    public R<LoginUser> info() {
        LoginUser user = UserContext.get();
        if (user == null) {
            throw new BusinessException(R.CODE_UNAUTHORIZED, "未登录");
        }
        return R.ok(user);
    }

    private LoginVO buildVO(LoginUser loginUser, String realName) {
        LoginVO vo = new LoginVO();
        vo.setToken(jwtUtil.createToken(loginUser));
        vo.setUserId(loginUser.getId());
        vo.setUsername(loginUser.getUsername());
        vo.setRealName(realName);
        vo.setRole(loginUser.getRole());
        vo.setStationId(loginUser.getStationId());
        return vo;
    }
}
