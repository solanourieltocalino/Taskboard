package com.jbk.taskboard.repository;

import com.jbk.taskboard.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository interface for Task entities.
 * Extends JpaRepository to provide CRUD operations.
 * Extends JpaSpecificationExecutor to support Specifications for dynamic
 * queries.
 * Includes methods to check existence by project ID and task title.
 */
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    // Checks if a task exists for a given project ID and task title (case
    // insensitive).
    boolean existsByProject_IdAndTitleIgnoreCase(long projectId, String title);

    // Checks if a task exists for a given project ID and task title (case
    // insensitive), excluding a specific task ID.
    boolean existsByProject_IdAndTitleIgnoreCaseAndIdNot(long projectId, String title, long id);
}
