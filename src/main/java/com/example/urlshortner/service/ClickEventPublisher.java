package com.example.urlshortner.service;

public interface ClickEventPublisher {

    void publish(String topic, String payload);
}
