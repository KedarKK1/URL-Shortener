package com.example.urlshortner.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit; 

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org. springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
public class InfrastructureAdaptersTest {
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;
    
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void redisCacheOperationsShouldUseRedisTemplate() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        RedisCacheOperations ops = new RedisCacheOperations(redisTemplate);
        ops.set("k", "v", 5, TimeUnit.MINUTES); 
        ops.delete("k");

        verify(valueOperations).set("k", "v", 5, TimeUnit.MINUTES);
        verify(redisTemplate).delete ("k");
    }

    @Test
    void redisCacheOperationsShouldSwallowConnectionErrors(){
        when(redisTemplate.opsForValue()).thenThrow(new RedisConnectionFailureException("down"));
        RedisCacheOperations ops = new RedisCacheOperations(redisTemplate);

        assertThatCode(() -> ops.get("k")).doesNotThrowAnyException();
    }
    
    @Test
    void kafkaPublisherShouldSwallowKafkaException() {
        when(kafkaTemplate.send("topic", "payload")) .thenThrow(new KafkaException("down"));
        KafkaClickEventPublisher publisher = new KafkaClickEventPublisher(kafkaTemplate); 
        
        assertThatCode(() -> publisher.publish("topic", "payload")).doesNotThrowAnyException();
    }
}
