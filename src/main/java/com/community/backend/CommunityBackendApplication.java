package com.community.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// 登录由我们自己的 AuthService + JWT 完成，不需要 Spring Security 默认生成的内存用户
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class CommunityBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityBackendApplication.class, args);
    }
}