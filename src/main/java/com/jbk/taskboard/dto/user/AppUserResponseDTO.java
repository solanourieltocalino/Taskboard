package com.jbk.taskboard.dto.user;

import java.time.Instant;

/**
 * DTO used to return user data in responses.
 * Represents the structure sent back to the client.
 */
public record AppUserResponseDTO(
        Long id,
        String name,
        String email,
        Instant createdAt) {
}