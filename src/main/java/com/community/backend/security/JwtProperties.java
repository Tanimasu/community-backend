package com.community.backend.security;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

// 对应 application.yml 里的 jwt.* 配置
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, Duration accessTokenTtl, Duration refreshTokenTtl) {
}
