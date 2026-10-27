package com.gasstation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI（Swagger）文档配置
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "Authorization";

    @Bean
    public OpenAPI gasStationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智慧加油站平台接口文档")
                        .version("1.0.0")
                        .description("管理后台 /api/admin/**，小程序 /api/app/**，公共接口 /api/public/**"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
                .schemaRequirement(SECURITY_SCHEME, new SecurityScheme()
                        .name(SECURITY_SCHEME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER));
    }
}
