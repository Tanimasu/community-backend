package com.community.backend.modules.demo.mq;

import com.community.backend.common.ApiResponse;
import com.community.backend.config.RabbitMqConfig;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo/mq")
public class MqDemoController {

    private final RabbitTemplate rabbitTemplate;
    private final DemoMessageListener demoMessageListener;

    public MqDemoController(RabbitTemplate rabbitTemplate, DemoMessageListener demoMessageListener) {
        this.rabbitTemplate = rabbitTemplate;
        this.demoMessageListener = demoMessageListener;
    }

    @PostMapping
    public ApiResponse<DemoMessage> send(@RequestParam String content) {
        DemoMessage message = new DemoMessage(content, LocalDateTime.now());
        rabbitTemplate.convertAndSend(RabbitMqConfig.DEMO_EXCHANGE, RabbitMqConfig.DEMO_ROUTING_KEY, message);
        return ApiResponse.success(message);
    }

    @GetMapping("/received")
    public ApiResponse<List<DemoMessage>> received() {
        return ApiResponse.success(demoMessageListener.getReceived());
    }
}
