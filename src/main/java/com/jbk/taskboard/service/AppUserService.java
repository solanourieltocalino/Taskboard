package com.jbk.taskboard.service;

import com.jbk.taskboard.dto.user.AppUserRequestDTO;
import com.jbk.taskboard.dto.user.AppUserResponseDTO;
import org.springframework.data.domain.Page;

/**
 * Service interface for managing AppUser entities.
 * Defines methods for creating, retrieving, updating, and deleting users.
 * All methods are transactional to ensure data integrity.
 * Implements CRUD operations for AppUser.
 */
public interface AppUserService {

    // Creates a new user and returns the created user DTO.
    AppUserResponseDTO create(AppUserRequestDTO req);

    // Retrieves a user by its ID.
    AppUserResponseDTO getById(long id);

    // Returns a paginated list of users.
    Page<AppUserResponseDTO> list(int page, int size);

    // Updates an existing user by ID.
    AppUserResponseDTO update(long id, AppUserRequestDTO req);

    // Deletes a user by ID.
    void delete(long id);
}
