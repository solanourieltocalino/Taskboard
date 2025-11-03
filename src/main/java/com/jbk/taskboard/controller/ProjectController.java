package com.jbk.taskboard.controller;

import com.jbk.taskboard.dto.project.ProjectRequestDTO;
import com.jbk.taskboard.dto.project.ProjectResponseDTO;
import com.jbk.taskboard.service.ProjectService;

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
 * REST controller that exposes CRUD endpoints for projects.
 * Uses ProjectService to handle business logic.
 * All endpoints return appropriate HTTP status codes and responses.
 * 
 * @Validated enables method-level validation for request parameters.
 */
@Validated
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService service;

    /**
     * Constructor that injects the ProjectService.
     * 
     * @param service
     */
    public ProjectController(ProjectService service) {
        this.service = service;
    }

    /**
     * POST endpoint - Creates a new project.
     * Validates the request body and returns 201 Created with the new project.
     * 
     * @param req
     * @param uriBuilder
     * @return
     */
    @PostMapping
    public ResponseEntity<ProjectResponseDTO> create(@Valid @RequestBody ProjectRequestDTO req,
            UriComponentsBuilder uriBuilder) {
        log.info("[POST] /api/projects - Creating project with name={}", req.name());
        ProjectResponseDTO res = service.create(req);
        var location = uriBuilder.path("/api/projects/{id}").build(res.id());
        log.info("Project created successfully with id={}", res.id());
        return ResponseEntity.created(location).body(res);
    }

    /**
     * GET endpoint - Retrieves a project by ID.
     * Returns 200 OK with the project data.
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> get(@PathVariable long id) {
        log.info("[GET] /api/projects/{} - Fetching project", id);
        var res = service.getById(id);
        log.debug("Project with id={} fetched successfully", id);
        return ResponseEntity.ok(res);
    }

    /**
     * GET endpoint - Lists projects with pagination.
     * Accepts page and size as query parameters and returns 200 OK with the project
     * list
     * 
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "Page must be >= 0") int page,
            @RequestParam(defaultValue = "20") @Positive(message = "Size must be >= 1") int size) {
        log.info("[GET] /api/projects - Listing projects (page={}, size={})", page, size);
        var res = service.list(page, size);
        log.debug("Project list fetched with {} elements", res.getContent().size());
        return ResponseEntity.ok(res);
    }

    /**
     * PUT endpoint - Updates an existing project by ID.
     * Validates the request body and returns 200 OK with the updated project.
     * 
     * @param id
     * @param req
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> update(@PathVariable long id,
            @Valid @RequestBody ProjectRequestDTO req) {
        log.info("[PUT] /api/projects/{} - Updating project", id);
        var res = service.update(id, req);
        log.info("Project with id={} updated successfully", id);
        return ResponseEntity.ok(res);
    }

    /**
     * DELETE endpoint - Deletes a project by ID.
     * Returns 204 No Content on successful deletion.
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        log.info("[DELETE] /api/projects/{} - Deleting project", id);
        service.delete(id);
        log.info("Project with id={} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
