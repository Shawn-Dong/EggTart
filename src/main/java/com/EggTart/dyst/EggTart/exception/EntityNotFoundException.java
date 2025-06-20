package com.EggTart.dyst.EggTart.exception;

public class EntityNotFoundException extends EggTartException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("%s not found with ID: %s", entityName, id));
    }
} 