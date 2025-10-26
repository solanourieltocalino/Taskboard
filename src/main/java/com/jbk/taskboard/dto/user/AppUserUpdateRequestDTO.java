package com.jbk.taskboard.dto.user;

import jakarta.validation.constraints.*;

/** 
 * DTO used for updating an existing user.
 * Includes validation rules similar to the create request.
 */
public record AppUserUpdateRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is not valid")
        @Size(max = 120, message = "Email cannot exceed 120 characters")
        String email
) {}
