package com.jbk.taskboard.service;

import com.jbk.taskboard.dto.task.*;
import com.jbk.taskboard.entity.TaskPriority;
import com.jbk.taskboard.entity.TaskStatus;
import org.springframework.data.domain.Page;

/**
 * Service interface for managing Task entities.
 * Defines methods for creating, retrieving, updating, and deleting tasks.
 * All methods are transactional to ensure data integrity.
 * Implements CRUD operations for Task.
 */
public interface TaskService {

    // Creates a new task and returns the created task DTO.
    TaskResponseDTO create(TaskCreateRequestDTO req);

    // Creates a new task for a specific project and returns the created task DTO.
    TaskResponseDTO createForProject(long projectId, TaskCreateForProjectRequestDTO req);

    // Retrieves a task by its ID.
    TaskResponseDTO getById(long id);

    // Returns a paginated list of tasks with optional filtering by status,
    // priority, and project ID.
    Page<TaskResponseDTO> list(Integer page, Integer size, TaskStatus status, TaskPriority priority, Long projectId);

    // Updates an existing task by ID.
    TaskResponseDTO update(long id, TaskUpdateRequestDTO req);

    // Deletes a task by ID.
    void delete(long id);
}
