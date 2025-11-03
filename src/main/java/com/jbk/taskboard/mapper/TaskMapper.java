package com.jbk.taskboard.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import com.jbk.taskboard.dto.project.ProjectResponseDTO;
import com.jbk.taskboard.dto.task.*;
import com.jbk.taskboard.entity.*;

/**
 * Mapper class for converting between Task entities and DTOs.
 * Provides methods to map request DTOs to entities and entities to response
 * DTOs.
 * All methods are static and the class cannot be instantiated.
 */
public final class TaskMapper {

    private static final Logger log = LoggerFactory.getLogger(ProjectMapper.class);
    
    // Private constructor to prevent instantiation.
    private TaskMapper() {
    }

    /**
     * Converts a TaskCreateRequestDTO to a Task entity.
     * 
     * @param req
     * @param project
     * @return
     */
    @NonNull
    public static Task toEntity(TaskCreateRequestDTO req, Project project) {
        log.debug("Mapping TaskCreateRequestDTO to Task entity");
        Task t = new Task();
        t.setTitle(req.title());
        t.setDescription(req.description());
        t.setStatus(req.status() != null ? req.status() : TaskStatus.TODO);
        t.setPriority(req.priority() != null ? req.priority() : TaskPriority.MEDIUM);
        t.setDueDate(req.dueDate());
        t.setProject(project);
        return t;
    }

    /**
     * Converts a TaskCreateForProjectRequestDTO to a Task entity.
     * 
     * @param req
     * @param project
     * @return
     */
    @NonNull
    public static Task toEntity(TaskCreateForProjectRequestDTO req, Project project) {
        log.debug("Mapping TaskCreateForProjectRequestDTO to Task entity");
        Task t = new Task();
        t.setTitle(req.title());
        t.setDescription(req.description());
        t.setStatus(req.status() != null ? req.status() : TaskStatus.TODO);
        t.setPriority(req.priority() != null ? req.priority() : TaskPriority.MEDIUM);
        t.setDueDate(req.dueDate());
        t.setProject(project);
        return t;
    }

    /**
     * Applies updates from a TaskUpdateRequestDTO to an existing Task entity.
     * 
     * @param entity
     * @param req
     * @param project
     */
    public static void applyUpdate(Task entity, TaskUpdateRequestDTO req, Project project) {
        log.debug("Applying updates to Task entity with id={}", entity.getId());
        entity.setTitle(req.title());
        entity.setDescription(req.description());
        entity.setStatus(req.status());
        entity.setPriority(req.priority());
        entity.setDueDate(req.dueDate());
        entity.setProject(project);
    }

    /**
     * Converts a Task entity to a TaskResponseDTO.
     * 
     * @param e
     * @return
     */
    public static TaskResponseDTO toResponse(Task e) {
        log.debug("Mapping Task entity (id={}) to TaskResponseDTO", e.getId());
        Project p = e.getProject();
        ProjectResponseDTO projectDTO = ProjectResponseDTO.of(p.getId(), p.getName(), p.getDescription(),
                AppUserMapper.toResponse(p.getOwner()));
        return TaskResponseDTO.of(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getStatus(),
                e.getPriority(),
                e.getCreatedAt(),
                e.getDueDate(),
                projectDTO);
    }
}
