package com.java_spring_boot.first_demo.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
