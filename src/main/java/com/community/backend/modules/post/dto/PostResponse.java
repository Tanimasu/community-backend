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
        // 当前用户是否点赞过；每个用户不一样，所以不放进缓存，查询时再填
        boolean liked,
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
                false,
                post.getCreateTime()
        );
    }

    public PostResponse withLiked(boolean liked) {
        return new PostResponse(id, title, content, author, commentCount, likeCount, liked, createTime);
    }
}
