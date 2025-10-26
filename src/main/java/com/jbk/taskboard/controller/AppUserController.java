package com.jbk.taskboard.controller;

import com.jbk.taskboard.dto.user.AppUserCreateRequestDTO;
import com.jbk.taskboard.dto.user.AppUserResponseDTO;
import com.jbk.taskboard.dto.user.AppUserUpdateRequestDTO;
import com.jbk.taskboard.service.AppUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST controller that exposes CRUD endpoints for users.
 */
@RestController
@RequestMapping("/api/users")
public class AppUserController {

    // Injected service that contains the business logic.
    private final AppUserService service;

    /**
     * Constructor that injects the AppUserService.
     * @param service
     */
    public AppUserController(AppUserService service) {
        this.service = service;
    }

    /**
     * POST endpoint - Creates a new user.
     * Validates the request body and returns 201 Created with the new user.
     * @param req
     * @param uriBuilder
     * @return
     */
    @PostMapping
    public ResponseEntity<AppUserResponseDTO> create(@Valid @RequestBody AppUserCreateRequestDTO req,
            UriComponentsBuilder uriBuilder) {
        AppUserResponseDTO res = service.create(req);
        var location = uriBuilder.path("/api/users/{id}").build(res.id());
        return ResponseEntity.created(location).body(res);
    }

    /**
     * GET endpoint - Retrieves a user by ID.
     * Returns 200 OK with the user data.
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppUserResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * GET endpoint - Lists users with pagination.
     * Accepts page and size as query parameters and returns 200 OK with the user list
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page must be >= 0") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "size must be >= 1") int size) {
        return ResponseEntity.ok(service.list(page, size));
    }

    /**
     * PUT endpoint - Updates an existing user by ID.
     * Validates the request body and returns 200 OK with the updated user.
     * @param id
     * @param req
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<AppUserResponseDTO> update(@PathVariable Long id,
            @Valid @RequestBody AppUserUpdateRequestDTO req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    /**
     * DELETE endpoint - Deletes a user by ID.
     * Returns 204 No Content on successful deletion.
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
