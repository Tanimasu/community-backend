package com.community.backend.modules.demo.mq;

import java.time.LocalDateTime;

public record DemoMessage(String content, LocalDateTime sentAt) {
}
