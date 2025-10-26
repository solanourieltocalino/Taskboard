package com.jbk.taskboard.exception;

/**
 * Exception thrown when a requested resource is not found.
 * @param message The detail message for the exception.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
