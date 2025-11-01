package com.jbk.taskboard.exception;

/**
 * Exception thrown when a business rule is violated.
 * 
 * @param message The detail message for the exception.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
