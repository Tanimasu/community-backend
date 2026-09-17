package com.community.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String DEMO_EXCHANGE = "demo.exchange";
    public static final String DEMO_QUEUE = "demo.queue";
    public static final String DEMO_ROUTING_KEY = "demo.hello";

    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.create";

    // 死信：消息重试多次仍然失败后，会被投递到这里，而不是丢掉或无限重试
    public static final String NOTIFICATION_DLX = "notification.dlx";
    public static final String NOTIFICATION_DLQ = "notification.dlq";
    public static final String NOTIFICATION_DL_ROUTING_KEY = "notification.dead";

    @Bean
    public DirectExchange demoExchange() {
        return new DirectExchange(DEMO_EXCHANGE);
    }

    @Bean
    public Queue demoQueue() {
        // durable = true：RabbitMQ 重启后队列还在
        return new Queue(DEMO_QUEUE, true);
    }

    @Bean
    public Binding demoBinding(Queue demoQueue, DirectExchange demoExchange) {
        return BindingBuilder.bind(demoQueue).to(demoExchange).with(DEMO_ROUTING_KEY);
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .deadLetterExchange(NOTIFICATION_DLX)
                .deadLetterRoutingKey(NOTIFICATION_DL_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public DirectExchange notificationDlx() {
        return new DirectExchange(NOTIFICATION_DLX);
    }

    @Bean
    public Queue notificationDlq() {
        return QueueBuilder.durable(NOTIFICATION_DLQ).build();
    }

    @Bean
    public Binding notificationDlqBinding(Queue notificationDlq, DirectExchange notificationDlx) {
        return BindingBuilder.bind(notificationDlq).to(notificationDlx).with(NOTIFICATION_DL_ROUTING_KEY);
    }

    // 用 JSON 传输消息，而不是默认的 Java 序列化
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
