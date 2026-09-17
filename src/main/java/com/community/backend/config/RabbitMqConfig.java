package com.community.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String DEMO_EXCHANGE = "demo.exchange";
    public static final String DEMO_QUEUE = "demo.queue";
    public static final String DEMO_ROUTING_KEY = "demo.hello";

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

    // 用 JSON 传输消息，而不是默认的 Java 序列化
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
