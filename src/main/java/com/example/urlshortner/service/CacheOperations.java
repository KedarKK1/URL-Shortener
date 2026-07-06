package com.example.urlshortner.service;

import java.util.concurrent.TimeUnit;

public interface CacheOperations {

    String get(String key);

    void set(String key, String value, long ttl, TimeUnit unit);

    void delete(String key);
}
