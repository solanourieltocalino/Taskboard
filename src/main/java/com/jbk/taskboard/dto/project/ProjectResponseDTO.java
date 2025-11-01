package com.jbk.taskboard.dto.project;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
        @JsonInclude(Include.NON_NULL) Instant createdAt,
        AppUserResponseDTO owner) {

    public ProjectResponseDTO(Long id, String name, String description, AppUserResponseDTO owner) {
        this(id, name, description, null, owner);
    }

    public static ProjectResponseDTO of(Long id, String name, String description, Instant createdAt,
            AppUserResponseDTO owner) {
        return new ProjectResponseDTO(id, name, description, createdAt, owner);
    }

    public static ProjectResponseDTO of(Long id, String name, String description, AppUserResponseDTO owner) {
        return new ProjectResponseDTO(id, name, description, owner);
    }
}