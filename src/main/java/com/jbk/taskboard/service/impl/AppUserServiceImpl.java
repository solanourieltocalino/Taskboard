package com.jbk.taskboard.service.impl;

import com.jbk.taskboard.dto.user.AppUserCreateRequestDTO;
import com.jbk.taskboard.dto.user.AppUserResponseDTO;
import com.jbk.taskboard.dto.user.AppUserUpdateRequestDTO;
import com.jbk.taskboard.entity.AppUser;
import com.jbk.taskboard.exception.DuplicateEmailException;
import com.jbk.taskboard.exception.NotFoundException;
import com.jbk.taskboard.mapper.AppUserMapper;
import com.jbk.taskboard.repository.AppUserRepository;
import com.jbk.taskboard.service.AppUserService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing AppUser entities.
 * Provides methods for creating, retrieving, updating, and deleting users.
 * Uses AppUserRepository for database interactions and AppUserMapper for DTO conversions.
 * All methods are transactional to ensure data integrity.
 * Implements the AppUserService interface.
 */
@Service
@Transactional
public class AppUserServiceImpl implements AppUserService {

    // Repository dependency for interacting with the database.
    private final AppUserRepository repo;

    // Constructor injection of the repository.
    public AppUserServiceImpl(AppUserRepository repo) {
        this.repo = repo;
    }

    /**
     * Creates a new user after checking for email uniqueness.
     * @param req   The user creation request DTO.
     * @return      The created user as a response DTO.
     * @throws DuplicateEmailException if the email is already in use.
     */
    @Override
    public AppUserResponseDTO create(AppUserCreateRequestDTO req) {
        if (repo.existsByEmail(req.email()))
            throw new DuplicateEmailException(req.email());
        AppUser entity = AppUserMapper.toEntity(req);
        AppUser saved = repo.save(entity);
        return AppUserMapper.toResponse(saved);
    }

    /**
     * Retrieves a user by ID.
     * @param id    The ID of the user to retrieve.
     * @return      The user as a response DTO.
     * @throws NotFoundException if the user is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public AppUserResponseDTO getById(Long id) {
        AppUser found = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        return AppUserMapper.toResponse(found);
    }

    /**
     * Lists users with pagination.
     * @param page  The page number to retrieve.
     * @param size  The number of users per page.
     * @return      A page of user response DTOs.
     * @throws NotFoundException if no users are found.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AppUserResponseDTO> list(int page, int size) {
        Page<AppUser> p = repo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        return p.map(AppUserMapper::toResponse);
    }

    /**
     * Updates an existing user.
     * @param id    The ID of the user to update.
     * @param req   The user update request DTO.
     * @return      The updated user as a response DTO.
     * @throws NotFoundException if the user is not found.
     * @throws DuplicateEmailException if the new email is already in use by another user.
     */
    @Override
    public AppUserResponseDTO update(Long id, AppUserUpdateRequestDTO req) {
        AppUser entity = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        // Check for email uniqueness if the email is being changed
        if (!entity.getEmail().equalsIgnoreCase(req.email()) && repo.existsByEmail(req.email())) {
            throw new DuplicateEmailException(req.email());
        }

        AppUserMapper.applyUpdate(entity, req);
        return AppUserMapper.toResponse(entity);
    }

    /**
     * Deletes a user by ID.
     * @param id    The ID of the user to delete.
     * @throws NotFoundException if the user is not found.
     */
    @Override
    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new NotFoundException("User not found: " + id);
        repo.deleteById(id);
    }
}
