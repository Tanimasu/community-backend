package com.community.backend.modules.post.dto;

import com.community.backend.modules.post.entity.Post;
import com.community.backend.modules.user.dto.UserBriefResponse;
import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        UserBriefResponse author,
        Integer commentCount,
        Integer likeCount,
        LocalDateTime createTime
) {

    public static PostResponse from(Post post, UserBriefResponse author) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                author,
                post.getCommentCount(),
                post.getLikeCount(),
                post.getCreateTime()
        );
    }
}
