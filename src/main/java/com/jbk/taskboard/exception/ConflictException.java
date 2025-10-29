package com.jbk.taskboard.exception;

/**
 * Exception thrown when a conflict occurs, such as duplicate entries.
 * Includes a message describing the conflict.
 * 
 * @param message The conflict message.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
