package com.example.urlshortner.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RedisCacheOperations implements CacheOperations {

    private final StringRedisTemplate redisTemplate;

    @Override
    public String get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis unavailable, skipping cache read for key {}", key, ex);
            return null;
        }
    }

    @Override
    public void set(String key, String value, long ttl, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl, unit);
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis unavailable, skipping cache write for key {}", key, ex);
        }
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis unavailable, skipping cache delete for key {}", key, ex);
        }
    }
}
