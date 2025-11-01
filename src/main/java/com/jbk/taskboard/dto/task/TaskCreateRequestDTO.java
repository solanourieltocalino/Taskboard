package com.jbk.taskboard.dto.task;

import com.jbk.taskboard.entity.TaskPriority;
import com.jbk.taskboard.entity.TaskStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO used for creating a new task.
 * Contains validation annotations to ensure correct input data.
 * Optional fields (status, priority, dueDate) can be null.
 * If status or priority are null, defaults will be applied in the service
 * layer.
 * ProjectId is required to associate the task with a project.
 */
public record TaskCreateRequestDTO(
        @NotBlank(message = "Title is required") @Size(max = 150, message = "Title cannot exceed 150 characters") String title,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters") String description,

        TaskStatus status, // optional, default TODO if null
        TaskPriority priority, // optional, default MEDIUM if null
        LocalDate dueDate, // optional, past allowed

        @NotNull(message = "ProjectId is required") @Positive(message = "ProjectId must be > 0") Long projectId) {
}