package com.gasstation.service;

import com.gasstation.common.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 微信接口封装
 * <p>
 * 未配置 appid/secret 时自动降级为 mock 登录：openid = "mock_" + code，方便本地联调。
 */
@Slf4j
@Service
public class WxService {

    private static final String JSCODE2SESSION = "https://api.weixin.qq.com/sns/jscode2session" +
            "?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";

    @Value("${gas.wx.appid}")
    private String appid;

    @Value("${gas.wx.secret}")
    private String secret;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getOpenid(String code) {
        if (!StringUtils.hasText(appid) || !StringUtils.hasText(secret)) {
            log.warn("未配置微信小程序 appid/secret，使用 mock 登录，code={}", code);
            return "mock_" + code;
        }
        Map<String, Object> response = restTemplate.getForObject(JSCODE2SESSION, Map.class, appid, secret, code);
        if (response == null || response.get("openid") == null) {
            throw new BusinessException("微信登录失败：" + (response == null ? "无响应" : response.get("errmsg")));
        }
        return String.valueOf(response.get("openid"));
    }
}
