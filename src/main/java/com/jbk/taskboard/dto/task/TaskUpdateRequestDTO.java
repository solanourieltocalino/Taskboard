package com.jbk.taskboard.dto.task;

import com.jbk.taskboard.entity.TaskPriority;
import com.jbk.taskboard.entity.TaskStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO used for creating a new task.
 * Contains validation annotations to ensure correct input data.
 * All fields are required except dueDate.
 */
public record TaskUpdateRequestDTO(
        @NotBlank(message = "Title is required") @Size(max = 150, message = "Title cannot exceed 150 characters") String title,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters") String description,

        @NotNull(message = "Status is required") TaskStatus status,

        @NotNull(message = "Priority is required") TaskPriority priority,

        LocalDate dueDate,

        @NotNull(message = "ProjectId is required") @Positive(message = "ProjectId must be > 0") long projectId) {
}