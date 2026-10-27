package com.gasstation.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gasstation.common.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 工具类
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${gas.jwt.secret}")
    private String secret;

    @Value("${gas.jwt.expire-hours}")
    private long expireHours;

    public String createToken(LoginUser user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Date now = new Date();
        Date expire = new Date(now.getTime() + expireHours * 3600 * 1000);
        return JWT.create()
                .withClaim("uid", user.getId())
                .withClaim("username", user.getUsername() == null ? "" : user.getUsername())
                .withClaim("role", user.getRole())
                .withClaim("stationId", user.getStationId() == null ? 0L : user.getStationId())
                .withIssuedAt(now)
                .withExpiresAt(expire)
                .sign(algorithm);
    }

    public LoginUser parseToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            LoginUser user = new LoginUser();
            user.setId(jwt.getClaim("uid").asLong());
            user.setUsername(jwt.getClaim("username").asString());
            user.setRole(jwt.getClaim("role").asString());
            Long stationId = jwt.getClaim("stationId").asLong();
            user.setStationId(stationId == null || stationId == 0L ? null : stationId);
            return user;
        } catch (Exception e) {
            log.debug("token 解析失败：{}", e.getMessage());
            throw new BusinessException(com.gasstation.common.R.CODE_UNAUTHORIZED, "登录已失效，请重新登录");
        }
    }
}
