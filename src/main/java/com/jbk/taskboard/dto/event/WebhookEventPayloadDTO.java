package com.jbk.taskboard.dto.event;

import java.time.OffsetDateTime;

/**
 * DTO representing the payload of a webhook event.
 * Includes event details such as id, source, type, message, and creation
 * timestamp.
 * This DTO is used to encapsulate the data sent in webhook notifications.
 */
public record WebhookEventPayloadDTO(
        String id,
        String source,
        String type,
        String message,
        OffsetDateTime createdAt) {
}
