package com.java_spring_boot.first_demo.exception;

public class RateLimitRedisException extends RuntimeException {
    public RateLimitRedisException(String message) {
        super(message);
    }
}
