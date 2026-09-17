package com.community.backend.modules.comment.dto;

import com.community.backend.modules.comment.entity.Comment;
import com.community.backend.modules.user.dto.UserBriefResponse;
import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long postId,
        String content,
        UserBriefResponse author,
        Integer likeCount,
        LocalDateTime createTime
) {

    public static CommentResponse from(Comment comment, UserBriefResponse author) {
        return new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getContent(),
                author,
                comment.getLikeCount(),
                comment.getCreateTime()
        );
    }
}
