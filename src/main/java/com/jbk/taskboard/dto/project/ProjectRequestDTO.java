package com.jbk.taskboard.dto.project;

import jakarta.validation.constraints.*;

/**
 * DTO used for creating a new project.
 * Contains validation annotations to ensure correct input data.
 */
public record ProjectRequestDTO(
        @NotBlank(message = "Name is required") @Size(max = 120, message = "Name cannot exceed 120 characters") String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters") String description,

        @NotNull(message = "OwnerId is required") @Positive(message = "OwnerId must be > 0") Long ownerId) {
}
