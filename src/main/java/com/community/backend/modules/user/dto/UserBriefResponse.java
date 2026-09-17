package com.community.backend.modules.user.dto;

import com.community.backend.modules.user.entity.User;

// 帖子、评论里展示的作者信息，只需要 id 和昵称
public record UserBriefResponse(Long id, String nickname) {

    public static UserBriefResponse from(User user) {
        if (user == null) {
            return null;
        }
        return new UserBriefResponse(user.getId(), user.getNickname());
    }
}
