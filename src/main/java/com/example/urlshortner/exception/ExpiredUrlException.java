package com.example.urlshortner.exception;

public class ExpiredUrlException extends RuntimeException {

    public ExpiredUrlException(String message) {
        super(message);
    }
}
