package com.community.backend.modules.notification.mq;

import com.community.backend.modules.like.entity.LikeTargetType;
import com.community.backend.modules.notification.entity.NotificationType;

// 通过 RabbitMQ 传输的消息体：谁对谁做了什么
public record NotificationEvent(
        NotificationType type,
        Long receiverId,
        Long senderId,
        LikeTargetType targetType,
        Long targetId,
        String content
) {
}
