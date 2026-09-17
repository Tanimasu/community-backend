package com.community.backend.modules.like.dto;

import com.community.backend.modules.like.entity.LikeTargetType;
import jakarta.validation.constraints.NotNull;

// liked 传 true 表示点赞，false 表示取消点赞；重复提交结果不变（幂等）
public record LikeRequest(
        @NotNull(message = "targetType 不能为空")
        LikeTargetType targetType,

        @NotNull(message = "targetId 不能为空")
        Long targetId,

        @NotNull(message = "liked 不能为空")
        Boolean liked
) {
}
