package com.jbk.taskboard.exception;

import java.time.Instant;
import java.util.Map;

/**
 * Structured error response for API exceptions.
 * Includes timestamp, status code, error description, message, and optional
 * field validation errors.
 * 
 * @param timestamp The time the error occurred.
 * @param status    The HTTP status code.
 * @param error     The HTTP status description.
 * @param message   A detailed error message.
 * @param messages  A map of field validation errors, if applicable.
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        Map<String, String> messages // for field validation errors
) {
    public static ErrorResponse of(int status, String error, String message, Map<String, String> messages) {
        return new ErrorResponse(Instant.now(), status, error, message, messages);
    }
}
