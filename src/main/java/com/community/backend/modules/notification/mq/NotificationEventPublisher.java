package com.community.backend.modules.notification.mq;

import com.community.backend.common.TransactionUtils;
import com.community.backend.config.RabbitMqConfig;
import com.community.backend.modules.like.entity.LikeTargetType;
import com.community.backend.modules.notification.entity.NotificationType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventPublisher {

    private static final int MAX_CONTENT_LENGTH = 50;

    private final RabbitTemplate rabbitTemplate;

    public NotificationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishComment(Long senderId, Long receiverId, Long postId, String commentContent) {
        publish(new NotificationEvent(
                NotificationType.COMMENT,
                receiverId,
                senderId,
                LikeTargetType.POST,
                postId,
                truncate(commentContent)
        ));
    }

    public void publishLike(Long senderId, Long receiverId, LikeTargetType targetType, Long targetId) {
        NotificationType type = targetType == LikeTargetType.POST
                ? NotificationType.LIKE_POST
                : NotificationType.LIKE_COMMENT;
        publish(new NotificationEvent(type, receiverId, senderId, targetType, targetId, null));
    }

    private void publish(NotificationEvent event) {
        // 自己评论自己的帖子、自己点赞自己的评论，不需要通知
        if (event.receiverId().equals(event.senderId())) {
            return;
        }
        // 等业务事务提交后再发消息：否则消费者可能比事务提交更快，读不到刚写入的数据
        TransactionUtils.afterCommit(() -> rabbitTemplate.convertAndSend(
                RabbitMqConfig.NOTIFICATION_EXCHANGE,
                RabbitMqConfig.NOTIFICATION_ROUTING_KEY,
                event
        ));
    }

    private String truncate(String content) {
        if (content == null || content.length() <= MAX_CONTENT_LENGTH) {
            return content;
        }
        return content.substring(0, MAX_CONTENT_LENGTH) + "...";
    }
}
