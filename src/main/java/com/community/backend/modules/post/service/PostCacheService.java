package com.community.backend.modules.post.service;

import com.community.backend.modules.post.dto.PostResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

// 帖子详情缓存（Cache Aside）：读时先查缓存，没有再查库并写入；数据变更时删除缓存
@Component
public class PostCacheService {

    private static final String KEY_PREFIX = "post:detail:";
    private static final Duration TTL = Duration.ofMinutes(10);
    private static final Duration NULL_TTL = Duration.ofMinutes(1);
    // 帖子不存在时缓存一个空标记，防止有人反复查不存在的 id 打穿到数据库（缓存穿透）
    private static final String NULL_MARKER = "null";

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public PostCacheService(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    // loader 返回 null 表示帖子不存在
    public PostResponse getOrLoad(Long postId, Supplier<PostResponse> loader) {
        String key = key(postId);
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached != null) {
            return NULL_MARKER.equals(cached) ? null : fromJson(cached);
        }

        PostResponse post = loader.get();
        if (post == null) {
            stringRedisTemplate.opsForValue().set(key, NULL_MARKER, NULL_TTL);
        } else {
            stringRedisTemplate.opsForValue().set(key, toJson(post), ttlWithJitter());
        }
        return post;
    }

    // 必须等事务提交后再删缓存：如果提交前就删，别的请求可能把还没提交的旧数据重新写回缓存
    public void evictAfterCommit(Long postId) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    stringRedisTemplate.delete(key(postId));
                }
            });
        } else {
            stringRedisTemplate.delete(key(postId));
        }
    }

    // 过期时间加一点随机值，避免大量缓存在同一时刻一起过期（缓存雪崩）
    private Duration ttlWithJitter() {
        return TTL.plusSeconds(ThreadLocalRandom.current().nextInt(60));
    }

    private String key(Long postId) {
        return KEY_PREFIX + postId;
    }

    private String toJson(PostResponse post) {
        try {
            return objectMapper.writeValueAsString(post);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize post cache", e);
        }
    }

    private PostResponse fromJson(String json) {
        try {
            return objectMapper.readValue(json, PostResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize post cache", e);
        }
    }
}
