package com.jbk.taskboard.dto.task;

import com.jbk.taskboard.dto.project.ProjectResponseDTO;
import com.jbk.taskboard.entity.TaskPriority;
import com.jbk.taskboard.entity.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;

/**
 * DTO used to return task data in responses.
 * Represents the structure sent back to the client.
 * Includes nested project information.
 */
public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Instant createdAt,
        LocalDate dueDate,
        ProjectResponseDTO project) {

    public static TaskResponseDTO of(Long id, String title, String description, TaskStatus status,
            TaskPriority priority, Instant createdAt, LocalDate dueDate, ProjectResponseDTO project) {
        return new TaskResponseDTO(id, title, description, status, priority, createdAt, dueDate, project);
    }
}