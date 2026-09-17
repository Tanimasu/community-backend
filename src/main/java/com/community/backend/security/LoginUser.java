package com.community.backend.security;

// 从 Access Token 里解析出来的当前登录用户，不查数据库
public record LoginUser(Long id, String username, String role) {
}
