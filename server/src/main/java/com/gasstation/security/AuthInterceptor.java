package com.gasstation.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasstation.common.BusinessException;
import com.gasstation.common.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录鉴权拦截器
 * <p>
 * /api/admin/** 需要后台角色；/api/app/** 需要会员角色；其他接口 Token 可选。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_AUTH = "Authorization";

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = request.getRequestURI();
        String token = resolveToken(request);
        LoginUser user = null;
        if (token != null && !token.isEmpty()) {
            try {
                user = jwtUtil.parseToken(token);
                UserContext.set(user);
            } catch (BusinessException e) {
                if (needLogin(uri)) {
                    writeJson(response, R.fail(R.CODE_UNAUTHORIZED, e.getMessage()));
                    return false;
                }
            }
        }

        if (!needLogin(uri)) {
            return true;
        }
        if (user == null) {
            writeJson(response, R.fail(R.CODE_UNAUTHORIZED, "请先登录"));
            return false;
        }
        if (PATH_MATCHER.match("/api/admin/**", uri) && !user.isAdmin()) {
            writeJson(response, R.fail(R.CODE_FORBIDDEN, "无后台访问权限"));
            return false;
        }
        if (PATH_MATCHER.match("/api/app/**", uri) && !user.isMember()) {
            writeJson(response, R.fail(R.CODE_FORBIDDEN, "请使用微信会员身份登录"));
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private boolean needLogin(String uri) {
        return PATH_MATCHER.match("/api/admin/**", uri) || PATH_MATCHER.match("/api/app/**", uri);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTH);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length()).trim();
        }
        // 兼容 ?token= 方式（小程序下载文件等场景）
        return request.getParameter("token");
    }

    private void writeJson(HttpServletResponse response, R<?> result) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
