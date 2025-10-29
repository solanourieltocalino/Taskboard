package com.jbk.taskboard.dto.user;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;;

/**
 * DTO used to return user data in responses.
 * Represents the structure sent back to the client.
 */
public record AppUserResponseDTO(
        Long id,
        String name,
        String email,
        @JsonInclude(Include.NON_NULL) Instant createdAt) {

    public AppUserResponseDTO(Long id, String name, String email) {
        this(id, name, email, null);
    }

    public static AppUserResponseDTO of(Long id, String name, String email, Instant createdAt) {
        return new AppUserResponseDTO(id, name, email, createdAt);
    }

    public static AppUserResponseDTO of(Long id, String name, String email) {
        return new AppUserResponseDTO(id, name, email);
    }
}