package com.jbk.taskboard.service.impl;

import com.jbk.taskboard.dto.project.ProjectResponseDTO;
import com.jbk.taskboard.dto.project.ProjectRequestDTO;
import com.jbk.taskboard.entity.AppUser;
import com.jbk.taskboard.entity.Project;
import com.jbk.taskboard.exception.ConflictException;
import com.jbk.taskboard.exception.NotFoundException;
import com.jbk.taskboard.mapper.ProjectMapper;
import com.jbk.taskboard.repository.AppUserRepository;
import com.jbk.taskboard.repository.ProjectRepository;
import com.jbk.taskboard.service.ProjectService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing Project entities.
 * Provides methods for creating, retrieving, updating, and deleting projects.
 * Uses ProjectRepository for database interactions and ProjectMapper for DTO
 * conversions.
 * All methods are transactional to ensure data integrity.
 * Implements the ProjectService interface.
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);
    private final ProjectRepository projectRepo;
    private final AppUserRepository userRepo;

    /**
     * Constructor that injects the ProjectRepository and AppUserRepository.
     * 
     * @param projectRepo
     * @param userRepo
     */
    public ProjectServiceImpl(ProjectRepository projectRepo, AppUserRepository userRepo) {
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
    }

    /**
     * Creates a new project after checking for name uniqueness per owner.
     * 
     * @param req The project creation request DTO.
     * @return The created project as a response DTO.
     * @throws ConflictException if the project name already exists for the owner.
     */
    @Override
    public ProjectResponseDTO create(ProjectRequestDTO req) {
        log.info("Attempting to create project with name='{}' for ownerId={}", req.name(), req.ownerId());
        AppUser owner = userRepo.findById(req.ownerId())
                .orElseThrow(() -> {
                    log.warn("Owner not found: id={}", req.ownerId());
                    return new NotFoundException("Owner not found: " + req.ownerId());
                });

        if (projectRepo.existsByOwner_IdAndNameIgnoreCase(req.ownerId(), req.name())) {
            log.warn("Duplicate project name '{}' for ownerId={}", req.name(), req.ownerId());
            throw new ConflictException("Project name already exists for this owner");
        }

        Project saved = projectRepo.save(ProjectMapper.toEntity(req, owner));
        log.info("Project created successfully with id={}", saved.getId());
        return ProjectMapper.toResponse(saved);
    }

    /**
     * Retrieves a project by ID.
     * 
     * @param id The ID of the project to retrieve.
     * @return The project as a response DTO.
     * @throws NotFoundException if the project is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO getById(Long id) {
        log.debug("Fetching project by id={}", id);
        Project found = projectRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Project not found: id={}", id);
                    return new NotFoundException("Project not found: " + id);
                });
        log.info("Project retrieved successfully: id={}", id);
        return ProjectMapper.toResponse(found);
    }

    /**
     * Lists projects with pagination.
     * 
     * @param page The page number to retrieve.
     * @param size The number of projects per page.
     * @return A page of project response DTOs.
     * @throws NotFoundException if no projects are found.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponseDTO> list(int page, int size) {
        log.debug("Listing projects: page={}, size={}", page, size);
        Page<Project> p = projectRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        log.info("Projects listed successfully: page={}, size={}", page, size);
        return p.map(ProjectMapper::toResponse);
    }

    /**
     * Updates an existing project after checking for name uniqueness per owner.
     * 
     * @param id  The ID of the project to update.
     * @param req The project update request DTO.
     * @return The updated project as a response DTO.
     * @throws NotFoundException if the project or owner is not found.
     * @throws ConflictException if the project name already exists for the owner.
     */
    @Override
    public ProjectResponseDTO update(Long id, ProjectRequestDTO req) {
        log.info("Updating project with id={}", id);
        Project entity = projectRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Project not found: id={}", id);
                    return new NotFoundException("Project not found: " + id);
                });

        AppUser newOwner = userRepo.findById(req.ownerId())
                .orElseThrow(() -> {
                    log.warn("Owner not found: id={}", req.ownerId());
                    return new NotFoundException("Owner not found: " + req.ownerId());
                });

        Long targetOwnerId = newOwner.getId();
        String targetName = req.name();

        if (projectRepo.existsByOwner_IdAndNameIgnoreCaseAndIdNot(targetOwnerId, targetName, id)) {
            log.warn("Duplicate project name '{}' for ownerId={}", targetName, targetOwnerId);
            throw new ConflictException("Project name already exists for this owner");
        }

        ProjectMapper.applyUpdate(entity, req, newOwner);
        log.info("Project updated successfully: id={}", id);
        return ProjectMapper.toResponse(entity);
    }

    @Override
    public void delete(Long id) {
        log.info("Attempting to delete project with id={}", id);
        if (!projectRepo.existsById(id)) {
            log.warn("Project not found: id={}", id);
            throw new NotFoundException("Project not found: " + id);
        }
        projectRepo.deleteById(id);
        log.info("Project deleted successfully: id={}", id);
    }
}
