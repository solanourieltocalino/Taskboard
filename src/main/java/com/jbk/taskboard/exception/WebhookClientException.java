package com.jbk.taskboard.exception;

/**
 * Exception thrown when there is an error sending a webhook event.
 * 
 * @param message The detail message for the exception.
 * @param cause   The cause of the exception.
 */
public class WebhookClientException extends RuntimeException {

    public WebhookClientException(String message) {
        super(message);
    }

    public WebhookClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
