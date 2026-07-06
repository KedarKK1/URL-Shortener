package com.example.urlshortner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaClickEventPublisher implements ClickEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaClickEventPublisher.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaClickEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String topic, String payload) {
        try {
            kafkaTemplate.send(topic, payload);
        } catch (KafkaException ex) {
            log.warn("Kafka unavailable, skipping click event publish for topic {}", topic, ex);
        }
    }
}
