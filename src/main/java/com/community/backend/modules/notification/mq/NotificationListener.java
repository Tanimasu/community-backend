package com.community.backend.modules.notification.mq;

import com.community.backend.config.RabbitMqConfig;
import com.community.backend.modules.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    private final NotificationService notificationService;

    public NotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // 消费者在独立线程里运行，处理失败会按配置重试，重试用尽后进入死信队列
    @RabbitListener(queues = RabbitMqConfig.NOTIFICATION_QUEUE)
    public void onNotificationEvent(NotificationEvent event) {
        log.info("Handling notification event: {}", event);
        notificationService.createNotification(event);
    }
}
