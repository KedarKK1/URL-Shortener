package com.example.urlshortner.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class InfrastructureConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public CacheOperations cacheOperations(StringRedisTemplate redisTemplate) {
        return new RedisCacheOperations(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(CacheOperations.class)
    public CacheOperations noOpCacheOperations() {
        return new NoOpCacheOperations();
    }

    @Bean
    @ConditionalOnMissingBean
    public ClickEventPublisher clickEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        return kafkaTemplate != null ? new KafkaClickEventPublisher(kafkaTemplate) : new NoOpClickEventPublisher();
    }
}
