package com.jbk.taskboard.dto.event;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO used for sending event webhook requests.
 * Contains validation annotations to ensure correct input data.
 */
public record EventRequestDTO(
        @NotBlank String message,
        String source,
        String type) {
}
