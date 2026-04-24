package com.java_spring_boot.first_demo.exception;

public class ExistedException extends RuntimeException {
    public ExistedException(String message) {
        super(message);
    }
}
