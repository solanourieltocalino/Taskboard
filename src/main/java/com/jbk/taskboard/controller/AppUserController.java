package com.jbk.taskboard.controller;

import com.jbk.taskboard.dto.user.AppUserCreateRequestDTO;
import com.jbk.taskboard.dto.user.AppUserResponseDTO;
import com.jbk.taskboard.dto.user.AppUserUpdateRequestDTO;
import com.jbk.taskboard.service.AppUserService;
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
 * REST controller that exposes CRUD endpoints for users.
 * Uses AppUserService to handle business logic.
 * All endpoints return appropriate HTTP status codes and responses.
 * 
 * @Validated enables method-level validation for request parameters.
 */
@Validated
@RestController
@RequestMapping("/api/users")
public class AppUserController {

    private static final Logger log = LoggerFactory.getLogger(AppUserController.class);
    private final AppUserService service;

    /**
     * Constructor that injects the AppUserService.
     * 
     * @param service
     */
    public AppUserController(AppUserService service) {
        this.service = service;
    }

    /**
     * POST endpoint - Creates a new user.
     * Validates the request body and returns 201 Created with the new user.
     * 
     * @param req
     * @param uriBuilder
     * @return
     */
    @PostMapping
    public ResponseEntity<AppUserResponseDTO> create(@Valid @RequestBody AppUserCreateRequestDTO req,
            UriComponentsBuilder uriBuilder) {
        log.info("[POST] /api/users - Creating user with email={}", req.email());
        AppUserResponseDTO res = service.create(req);
        var location = uriBuilder.path("/api/users/{id}").build(res.id());
        log.info("User created successfully with id={}", res.id());
        return ResponseEntity.created(location).body(res);
    }

    /**
     * GET endpoint - Retrieves a user by ID.
     * Returns 200 OK with the user data.
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppUserResponseDTO> get(@PathVariable Long id) {
        log.info("[GET] /api/users/{} - Fetching user", id);
        var res = service.getById(id);
        log.debug("Fetched user: {}", res);
        return ResponseEntity.ok(res);
    }

    /**
     * GET endpoint - Lists users with pagination.
     * Accepts page and size as query parameters and returns 200 OK with the user
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
        log.info("[GET] /api/users - Listing users (page={}, size={})", page, size);
        var res = service.list(page, size);
        log.debug("User list fetched with {} elements", res.getContent().size());
        return ResponseEntity.ok(res);
    }

    /**
     * PUT endpoint - Updates an existing user by ID.
     * Validates the request body and returns 200 OK with the updated user.
     * 
     * @param id
     * @param req
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<AppUserResponseDTO> update(@PathVariable Long id,
            @Valid @RequestBody AppUserUpdateRequestDTO req) {
        log.info("[PUT] /api/users/{} - Updating user", id);
        var res = service.update(id, req);
        log.info("User with id={} updated successfully", id);
        return ResponseEntity.ok(res);
    }

    /**
     * DELETE endpoint - Deletes a user by ID.
     * Returns 204 No Content on successful deletion.
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("[DELETE] /api/users/{} - Deleting user", id);
        service.delete(id);
        log.info("User with id={} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
