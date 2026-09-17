package com.community.backend.modules.user.dto;

import com.community.backend.modules.user.entity.User;
import java.time.LocalDateTime;

// 返回给前端的用户信息，不包含密码
public record UserResponse(
        Long id,
        String username,
        String nickname,
        String role,
        LocalDateTime createTime
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getRole(),
                user.getCreateTime()
        );
    }
}
