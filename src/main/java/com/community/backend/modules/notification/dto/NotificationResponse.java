package com.community.backend.modules.notification.dto;

import com.community.backend.modules.like.entity.LikeTargetType;
import com.community.backend.modules.notification.entity.Notification;
import com.community.backend.modules.notification.entity.NotificationType;
import com.community.backend.modules.user.dto.UserBriefResponse;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        NotificationType type,
        UserBriefResponse sender,
        LikeTargetType targetType,
        Long targetId,
        String content,
        boolean read,
        LocalDateTime createTime
) {

    public static NotificationResponse from(Notification notification, UserBriefResponse sender) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                sender,
                notification.getTargetType(),
                notification.getTargetId(),
                notification.getContent(),
                Boolean.TRUE.equals(notification.getIsRead()),
                notification.getCreateTime()
        );
    }
}
