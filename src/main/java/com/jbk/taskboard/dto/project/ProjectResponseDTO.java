package com.jbk.taskboard.dto.project;

import java.time.Instant;

import com.jbk.taskboard.dto.user.AppUserResponseDTO;

/**
 * DTO used to return project data in responses.
 * Represents the structure sent back to the client.
 * Includes nested owner information.
 */
public record ProjectResponseDTO(
        Long id,
        String name,
        String description,
        Instant createdAt,
        AppUserResponseDTO owner) {

    public static ProjectResponseDTO of(Long id, String name, String description, Instant createdAt,
            AppUserResponseDTO owner) {
        return new ProjectResponseDTO(id, name, description, createdAt, owner);
    }
}