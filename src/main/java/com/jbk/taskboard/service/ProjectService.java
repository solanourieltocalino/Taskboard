package com.jbk.taskboard.service;

import com.jbk.taskboard.dto.project.ProjectResponseDTO;
import com.jbk.taskboard.dto.project.ProjectRequestDTO;

import org.springframework.data.domain.Page;

/**
 * Service interface for managing Project entities.
 * Defines methods for creating, retrieving, updating, and deleting projects.
 * All methods are transactional to ensure data integrity.
 * Implements CRUD operations for Project.
 */
public interface ProjectService {

    // Creates a new project and returns the created project DTO.
    ProjectResponseDTO create(ProjectRequestDTO req);

    // Retrieves a project by its ID.
    ProjectResponseDTO getById(Long id);

    // Returns a paginated list of projects.
    Page<ProjectResponseDTO> list(int page, int size);

    // Updates an existing project by ID.
    ProjectResponseDTO update(Long id, ProjectRequestDTO req);

    // Deletes a project by ID.
    void delete(Long id);
}
