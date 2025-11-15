package com.jbk.taskboard.repository.spec;

import com.jbk.taskboard.entity.Task;
import com.jbk.taskboard.entity.TaskPriority;
import com.jbk.taskboard.entity.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for querying Task entities.
 * Provides methods to create specifications based on status, priority,
 * and project ID.
 * These specifications can be combined to build dynamic queries.
 * Usage example:
 * Specification<Task> spec =
 * Specification.where(TaskSpecs.hasStatus(TaskStatus.TO​DO))
 * .and(TaskSpecs.hasPriority(TaskPriority.HIGH));
 */
public final class TaskSpecs {

    // Private constructor to prevent instantiation.
    private TaskSpecs() {
    }

    // Specification to filter tasks by status.
    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, q, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    // Specification to filter tasks by priority.
    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, q, cb) -> priority == null ? null : cb.equal(root.get("priority"), priority);
    }

    // Specification to filter tasks by associated project ID.
    public static Specification<Task> hasProjectId(Long projectId) {
        return (root, q, cb) -> projectId == null ? null : cb.equal(root.get("project").get("id"), projectId);
    }
}
