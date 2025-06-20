package com.EggTart.dyst.EggTart.exception;

public class InvalidTaskStateException extends EggTartException {
    
    public InvalidTaskStateException(String message) {
        super(message);
    }
    
    public InvalidTaskStateException(String currentStatus, String operation) {
        super(String.format("Cannot %s task in status: %s", operation, currentStatus));
    }
} 