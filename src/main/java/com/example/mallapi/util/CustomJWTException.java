package com.example.mallapi.util;

public class CustomJWTException extends RuntimeException{
    public CustomJWTException(String message) {
        super(message);
    }
}
