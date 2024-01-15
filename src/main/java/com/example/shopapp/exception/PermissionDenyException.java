package com.example.shopapp.exception;

public class PermissionDenyException extends Exception {
    public PermissionDenyException(String message) {
        super(message);
    }
}
