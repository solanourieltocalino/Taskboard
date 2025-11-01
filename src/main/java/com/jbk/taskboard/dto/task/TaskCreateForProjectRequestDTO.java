package com.jbk.taskboard.dto.task;

import com.jbk.taskboard.entity.TaskPriority;
import com.jbk.taskboard.entity.TaskStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO used for creating a new task within a specific project.
 * Contains validation annotations to ensure correct input data.
 * Optional fields (status, priority, dueDate) can be null.
 * If status or priority are null, defaults will be applied in the service
 * layer.
 * ProjectId is not needed here as the task is being created for a known
 * project.
 * All other fields are similar to TaskCreateRequestDTO.
 */
public record TaskCreateForProjectRequestDTO(
        @NotBlank(message = "title is required") @Size(max = 150, message = "title must be <= 150 characters") String title,

        @Size(max = 1000, message = "description must be <= 1000 characters") String description,

        TaskStatus status, // optional
        TaskPriority priority, // optional
        LocalDate dueDate // optional
) {
}