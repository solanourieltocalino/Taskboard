package com.jbk.taskboard.exception;

/**
 * Exception thrown when a duplicate email is encountered.
 * Includes the duplicate email in the exception message.
 * @param email The duplicate email address.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("Email is already in use: " + email);
    }
}
