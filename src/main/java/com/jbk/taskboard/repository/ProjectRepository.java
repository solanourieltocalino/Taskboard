package com.jbk.taskboard.repository;

import com.jbk.taskboard.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Project entities.
 * Extends JpaRepository to provide CRUD operations.
 * Includes methods to check existence by owner ID and project name.
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Checks if a project exists for a given owner ID and project name (case
    // insensitive).
    boolean existsByOwner_IdAndNameIgnoreCase(long ownerId, String name);

    // Checks if a project exists for a given owner ID and project name (case
    // insensitive), excluding a specific project ID.
    boolean existsByOwner_IdAndNameIgnoreCaseAndIdNot(long ownerId, String name, long id);
}
