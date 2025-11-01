package com.jbk.taskboard.service.impl;

import com.jbk.taskboard.dto.user.AppUserRequestDTO;
import com.jbk.taskboard.dto.user.AppUserResponseDTO;
import com.jbk.taskboard.entity.AppUser;
import com.jbk.taskboard.exception.BusinessRuleException;
import com.jbk.taskboard.exception.NotFoundException;
import com.jbk.taskboard.mapper.AppUserMapper;
import com.jbk.taskboard.repository.AppUserRepository;
import com.jbk.taskboard.service.AppUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing AppUser entities.
 * Provides methods for creating, retrieving, updating, and deleting users.
 * Uses AppUserRepository for database interactions and AppUserMapper for DTO
 * conversions.
 * All methods are transactional to ensure data integrity.
 * Implements the AppUserService interface.
 */
@Service
@Transactional
public class AppUserServiceImpl implements AppUserService {

    private static final Logger log = LoggerFactory.getLogger(AppUserServiceImpl.class);
    private final AppUserRepository repo;

    /**
     * Constructor that injects the AppUserRepository.
     * 
     * @param repo
     */
    public AppUserServiceImpl(AppUserRepository repo) {
        this.repo = repo;
    }

    /**
     * Creates a new user after checking for email uniqueness.
     * 
     * @param req The user creation request DTO.
     * @return The created user as a response DTO.
     * @throws BusinessRuleException if the email is already in use.
     */
    @Override
    public AppUserResponseDTO create(AppUserRequestDTO req) {
        log.info("Attempting to create user with email={}", req.email());
        if (repo.existsByEmail(req.email())) {
            log.warn("Duplicate email detected: {}", req.email());
            throw new BusinessRuleException("Email already in use: " + req.email());
        }

        AppUser saved = repo.save(AppUserMapper.toEntity(req));
        log.info("User created successfully with id={}", saved.getId());
        return AppUserMapper.toResponse(saved);
    }

    /**
     * Retrieves a user by ID.
     * 
     * @param id The ID of the user to retrieve.
     * @return The user as a response DTO.
     * @throws NotFoundException if the user is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public AppUserResponseDTO getById(Long id) {
        log.debug("Fetching user by id={}", id);
        AppUser found = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", id);
                    return new NotFoundException("User not found: " + id);
                });
        log.info("User retrieved successfully: id={}", id);
        return AppUserMapper.toResponse(found);
    }

    /**
     * Lists users with pagination.
     * 
     * @param page The page number to retrieve.
     * @param size The number of users per page.
     * @return A page of user response DTOs.
     * @throws NotFoundException if no users are found.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AppUserResponseDTO> list(int page, int size) {
        log.debug("Listing users - page={}, size={}", page, size);
        Page<AppUser> p = repo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        log.info("Fetched {} users from page {}", p.getContent().size(), page);
        return p.map(AppUserMapper::toResponse);
    }

    /**
     * Updates an existing user.
     * 
     * @param id  The ID of the user to update.
     * @param req The user update request DTO.
     * @return The updated user as a response DTO.
     * @throws NotFoundException     if the user is not found.
     * @throws BusinessRuleException if the email is already in use by another user.
     */
    @Override
    public AppUserResponseDTO update(Long id, AppUserRequestDTO req) {
        log.info("Updating user with id={}", id);
        AppUser entity = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", id);
                    return new NotFoundException("User not found: " + id);
                });

        // Check for email uniqueness if the email is being changed
        if (!entity.getEmail().equalsIgnoreCase(req.email()) && repo.existsByEmail(req.email())) {
            log.warn("Duplicate email detected during update: {}", req.email());
            throw new BusinessRuleException("Email already in use: " + req.email());
        }

        AppUserMapper.applyUpdate(entity, req);
        log.info("User with id={} updated successfully", id);
        return AppUserMapper.toResponse(entity);
    }

    /**
     * Deletes a user by ID.
     * 
     * @param id The ID of the user to delete.
     * @throws NotFoundException if the user is not found.
     */
    @Override
    public void delete(Long id) {
        log.info("Attempting to delete user with id={}", id);
        if (!repo.existsById(id)) {
            log.warn("User not found: id={}", id);
            throw new NotFoundException("User not found: " + id);
        }
        repo.deleteById(id);
        log.info("User with id={} deleted successfully", id);
    }
}
