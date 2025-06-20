package com.EggTart.dyst.EggTart.exception;

public class EggTartException extends RuntimeException {
    
    public EggTartException(String message) {
        super(message);
    }
    
    public EggTartException(String message, Throwable cause) {
        super(message, cause);
    }
} 