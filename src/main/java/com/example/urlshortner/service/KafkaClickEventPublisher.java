package com.example.urlshortner.service;

import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class KafkaClickEventPublisher implements ClickEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publish(String topic, String payload) {
        try {
            kafkaTemplate.send(topic, payload);
        } catch (KafkaException ex) {
            log.warn("Kafka unavailable, skipping click event publish for topic {}", topic, ex);
        }
    }
}
