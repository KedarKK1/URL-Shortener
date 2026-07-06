package com.example.urlshortner.service;

import java.util.concurrent.TimeUnit;

public class NoOpCacheOperations implements CacheOperations {

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public void set(String key, String value, long ttl, TimeUnit unit) {
    }

    @Override
    public void delete(String key) {
    }
}
