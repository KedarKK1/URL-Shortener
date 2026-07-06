package com.example.urlshortner.service;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisCacheOperations implements CacheOperations {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheOperations.class);
    private final StringRedisTemplate redisTemplate;

    public RedisCacheOperations(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

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
