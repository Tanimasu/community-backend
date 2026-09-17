package com.community.backend.modules.like.dto;

import com.community.backend.modules.like.entity.LikeTargetType;

public record LikeResponse(
        LikeTargetType targetType,
        Long targetId,
        boolean liked,
        int likeCount
) {
}
