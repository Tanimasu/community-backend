package com.community.backend.modules.demo.mq;

import com.community.backend.config.RabbitMqConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DemoMessageListener {

    private static final Logger log = LoggerFactory.getLogger(DemoMessageListener.class);
    private static final int MAX_RECORDS = 20;

    // 消费者在另一个线程里运行，所以用线程安全的 List
    private final List<DemoMessage> received = new CopyOnWriteArrayList<>();

    @RabbitListener(queues = RabbitMqConfig.DEMO_QUEUE)
    public void onMessage(DemoMessage message) {
        log.info("Received demo message: {}", message);
        received.add(message);
        if (received.size() > MAX_RECORDS) {
            received.remove(0);
        }
    }

    public List<DemoMessage> getReceived() {
        return new ArrayList<>(received);
    }
}
