package com.defers.integration.domain.exception;

public class EntityValidationException extends RuntimeException {
    public EntityValidationException(String message) {
        super(message);
    }
}
