package com.app.fraudengine.extended.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, Object value) {

        redisTemplate.opsForValue()
            .set(key, value);
    }

    public void saveWithExpiry(
        String key,
        Object value,
        long timeout
    ) {

        redisTemplate.opsForValue()
            .set(key, value, timeout, TimeUnit.MINUTES);
    }

    public Object get(String key) {

        return redisTemplate.opsForValue()
            .get(key);
    }

    public void delete(String key) {

        redisTemplate.delete(key);
    }

    public Long incrementWithTTL(String key, long ttlMinutes) {

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null) {
            redisTemplate.expire(key, ttlMinutes, TimeUnit.MINUTES);
        }

        return count;
    }
}
