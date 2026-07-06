package com.example.urlshortner.service;

public class NoOpClickEventPublisher implements ClickEventPublisher {

    @Override
    public void publish(String topic, String payload) {
    }
}
