package com.community.backend.modules.demo.redis;

import com.community.backend.common.ApiResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo/redis")
public class RedisDemoController {

    private static final String KEY_PREFIX = "demo:";

    private final StringRedisTemplate stringRedisTemplate;

    public RedisDemoController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostMapping
    public ApiResponse<String> set(@RequestParam String key,
                                   @RequestParam String value,
                                   @RequestParam(required = false) Long ttlSeconds) {
        String redisKey = KEY_PREFIX + key;
        if (ttlSeconds != null) {
            stringRedisTemplate.opsForValue().set(redisKey, value, Duration.ofSeconds(ttlSeconds));
        } else {
            stringRedisTemplate.opsForValue().set(redisKey, value);
        }
        return ApiResponse.success("Saved " + redisKey);
    }

    @GetMapping("/{key}")
    public ApiResponse<Map<String, Object>> get(@PathVariable String key) {
        String redisKey = KEY_PREFIX + key;
        Map<String, Object> result = new HashMap<>();
        result.put("key", redisKey);
        result.put("value", stringRedisTemplate.opsForValue().get(redisKey));
        // -1 表示没有过期时间，-2 表示 key 不存在
        result.put("ttlSeconds", stringRedisTemplate.getExpire(redisKey));
        return ApiResponse.success(result);
    }
}
