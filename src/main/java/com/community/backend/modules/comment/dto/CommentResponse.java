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
        boolean liked,
        LocalDateTime createTime
) {

    public static CommentResponse from(Comment comment, UserBriefResponse author, boolean liked) {
        return new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getContent(),
                author,
                comment.getLikeCount(),
                liked,
                comment.getCreateTime()
        );
    }
}
