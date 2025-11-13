package com.jbk.taskboard.controller;

import com.jbk.taskboard.dto.task.*;
import com.jbk.taskboard.entity.TaskPriority;
import com.jbk.taskboard.entity.TaskStatus;
import com.jbk.taskboard.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST controller that exposes CRUD endpoints for tasks.
 * Uses TaskService to handle business logic.
 * All endpoints return appropriate HTTP status codes and responses.
 * 
 * @Validated enables method-level validation for request parameters.
 */
@Validated
@RestController
@RequestMapping
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskService service;

    /**
     * Constructor that injects the TaskService.
     * 
     * @param service
     */
    public TaskController(TaskService service) {
        this.service = service;
    }

    /**
     * POST endpoint - Creates a new task.
     * Validates the request body and returns 201 Created with the new task.
     * 
     * @param req
     * @param uriBuilder
     * @return
     */
    @PostMapping("/api/tasks")
    public ResponseEntity<TaskResponseDTO> create(@Valid @RequestBody TaskCreateRequestDTO req,
            UriComponentsBuilder uriBuilder) {
        log.info("[POST] /api/tasks - Creating task with title={}", req.title());
        TaskResponseDTO res = service.create(req);
        var location = uriBuilder.path("/api/tasks/{id}").build(res.id());
        log.info("Task created successfully with id={}", res.id());
        return ResponseEntity.created(location).body(res);
    }

    /**
     * POST endpoint - Creates a new task for a specific project.
     * Validates the request body and returns 201 Created with the new task.
     * 
     * @param projectId
     * @param req
     * @param uriBuilder
     * @return
     */
    @PostMapping("/api/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponseDTO> createForProject(@PathVariable long projectId,
            @Valid @RequestBody TaskCreateForProjectRequestDTO req,
            UriComponentsBuilder uriBuilder) {
        log.info("[POST] /api/projects/{}/tasks - Creating task with title={}", projectId, req.title());
        TaskResponseDTO res = service.createForProject(projectId, req);
        var location = uriBuilder.path("/api/tasks/{id}").build(res.id());
        log.info("Task created successfully with id={}", res.id());
        return ResponseEntity.created(location).body(res);
    }

    /**
     * GET endpoint - Retrieves a task by ID.
     * Returns 200 OK with the task data.
     * 
     * @param id
     * @return
     */
    @GetMapping("/api/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> get(@PathVariable long id) {
        log.info("[GET] /api/tasks/{} - Fetching task", id);
        var res = service.getById(id);
        log.debug("Task with id={} fetched successfully", id);
        return ResponseEntity.ok(res);
    }

    /**
     * GET endpoint - Lists tasks with pagination and optional filtering.
     * Accepts page, size, status, priority, and projectId as query parameters and
     * returns 200 OK with the task list.
     * 
     * @param page
     * @param size
     * @param status
     * @param priority
     * @param projectId
     * @return
     */
    @GetMapping("/api/tasks")
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "page must be >= 0") int page,
            @RequestParam(defaultValue = "20") @Positive(message = "size must be >= 1") int size,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) long projectId) {
        log.info("[GET] /api/tasks - Listing tasks (page={}, size={}, status={}, priority={}, projectId={})", page,
                size, status, priority, projectId);
        var res = service.list(page, size, status, priority, projectId);
        log.debug("Task list fetched with {} elements", res.getContent().size());
        return ResponseEntity.ok(res);
    }

    /**
     * PUT endpoint - Updates an existing task.
     * Validates the request body and returns 200 OK with the updated task.
     * 
     * @param id
     * @param req
     * @return
     */
    @PutMapping("/api/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable long id,
            @Valid @RequestBody TaskUpdateRequestDTO req) {
        log.info("[PUT] /api/tasks/{} - Updating task", id);
        var res = service.update(id, req);
        log.info("Task with id={} updated successfully", id);
        return ResponseEntity.ok(res);
    }

    /**
     * DELETE endpoint - Deletes a task by ID.
     * Returns 204 No Content on successful deletion.
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/api/tasks/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        log.info("[DELETE] /api/tasks/{} - Deleting task", id);
        service.delete(id);
        log.info("Task with id={} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
