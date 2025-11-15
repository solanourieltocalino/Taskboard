package com.jbk.taskboard.service.impl;

import com.jbk.taskboard.dto.task.*;
import com.jbk.taskboard.entity.Project;
import com.jbk.taskboard.entity.Task;
import com.jbk.taskboard.entity.TaskPriority;
import com.jbk.taskboard.entity.TaskStatus;
import com.jbk.taskboard.exception.BusinessRuleException;
import com.jbk.taskboard.exception.NotFoundException;
import com.jbk.taskboard.mapper.TaskMapper;
import com.jbk.taskboard.repository.ProjectRepository;
import com.jbk.taskboard.repository.TaskRepository;
import com.jbk.taskboard.repository.spec.TaskSpecs;
import com.jbk.taskboard.service.TaskService;

import org.slf4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing Task entities.
 * Provides methods for creating, retrieving, updating, and deleting tasks.
 * Uses TaskRepository for database interactions and TaskMapper for DTO
 * conversions.
 * All methods are transactional to ensure data integrity.
 * Implements the TaskService interface.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TaskServiceImpl.class);
    private final TaskRepository taskRepo;
    private final ProjectRepository projectRepo;

    /**
     * Constructor that injects the TaskRepository and ProjectRepository.
     * 
     * @param taskRepo
     * @param projectRepo
     */
    public TaskServiceImpl(TaskRepository taskRepo, ProjectRepository projectRepo) {
        this.taskRepo = taskRepo;
        this.projectRepo = projectRepo;
    }

    /**
     * Creates a new task after checking for title uniqueness within the project.
     * 
     * @param req The task creation request DTO.
     * @return The created task as a response DTO.
     * @throws BusinessRuleException if a task with the same title already exists in
     *                               the project.
     */
    @Override
    public TaskResponseDTO create(TaskCreateRequestDTO req) {
        log.info("Attempting to create task with title='{}' for projectId={}", req.title(), req.projectId());
        Project project = projectRepo.findById(req.projectId())
                .orElseThrow(() -> {
                    log.warn("Project not found: id={}", req.projectId());
                    return new NotFoundException("Project not found: " + req.projectId());
                });

        if (taskRepo.existsByProject_IdAndTitleIgnoreCase(req.projectId(), req.title())) {
            log.warn("Duplicate task title '{}' in projectId={}", req.title(), req.projectId());
            throw new BusinessRuleException("Task title already exists in this project");
        }

        Task saved = taskRepo.save(TaskMapper.toEntity(req, project));
        log.info("Created task id={} with title='{}' for projectId={}", saved.getId(), saved.getTitle(),
                req.projectId());
        return TaskMapper.toResponse(saved);
    }

    /**
     * Creates a new task for a specific project after checking for title
     * uniqueness within that project.
     * 
     * @param projectId The ID of the project to which the task will be added.
     * @param req       The task creation request DTO.
     * @return The created task as a response DTO.
     * @throws BusinessRuleException if a task with the same title already exists in
     *                               the project.
     */
    @Override
    public TaskResponseDTO createForProject(long projectId, TaskCreateForProjectRequestDTO req) {
        log.info("Attempting to create task with title='{}' for projectId={}", req.title(), projectId);
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Project not found: id={}", projectId);
                    return new NotFoundException("Project not found: " + projectId);
                });

        if (taskRepo.existsByProject_IdAndTitleIgnoreCase(projectId, req.title())) {
            log.warn("Duplicate task title '{}' in projectId={}", req.title(), projectId);
            throw new BusinessRuleException("Task title already exists in this project");
        }

        Task saved = taskRepo.save(TaskMapper.toEntity(req, project));
        log.info("Created task id={} with title='{}' for projectId={}", saved.getId(), saved.getTitle(),
                projectId);
        return TaskMapper.toResponse(saved);
    }

    /**
     * Retrieves a task by ID.
     * 
     * @param id The ID of the task to retrieve.
     * @return The task as a response DTO.
     * @throws NotFoundException if the task is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public TaskResponseDTO getById(long id) {
        log.debug("Fetching task by id={}", id);
        Task found = taskRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found: id={}", id);
                    return new NotFoundException("Task not found: " + id);
                });
        log.info("Task retrieved successfully: id={}", id);
        return TaskMapper.toResponse(found);
    }

    /**
     * Lists tasks with optional filtering by status, priority, and project ID.
     * 
     * @param page      The page number (0-based).
     * @param size      The page size.
     * @param status    Optional filter by task status.
     * @param priority  Optional filter by task priority.
     * @param projectId Optional filter by project ID.
     * @return A page of task response DTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> list(Integer page, Integer size, TaskStatus status, TaskPriority priority,
            Long projectId) {
        log.debug("Listing tasks with filters - page: {}, size: {}, status: {}, priority: {}, projectId: {}",
                page, size, status, priority, projectId);
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Specification<Task> spec = Specification.<Task>unrestricted()
                .and(TaskSpecs.hasStatus(status))
                .and(TaskSpecs.hasPriority(priority))
                .and(TaskSpecs.hasProjectId(projectId));
        log.info("Tasks listed successfully with applied filters");
        return taskRepo.findAll(spec, pr).map(TaskMapper::toResponse);
    }

    /**
     * Updates an existing task after checking for title uniqueness within the
     * project.
     * 
     * @param id  The ID of the task to update.
     * @param req The task update request DTO.
     * @return The updated task as a response DTO.
     * @throws NotFoundException     if the task or target project is not found.
     * @throws BusinessRuleException if a task with the same title already exists in
     *                               the target project.
     */
    @Override
    public TaskResponseDTO update(long id, TaskUpdateRequestDTO req) {
        log.info("Updating task with id={}", id);
        Task entity = taskRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found: id={}", id);
                    return new NotFoundException("Task not found: " + id);
                });

        long targetProjectId = req.projectId();
        Project targetProject = projectRepo.findById(targetProjectId)
                .orElseThrow(() -> {
                    log.warn("Target project not found: id={}", targetProjectId);
                    return new NotFoundException("Project not found: " + targetProjectId);
                });

        if (taskRepo.existsByProject_IdAndTitleIgnoreCaseAndIdNot(targetProjectId, req.title(), id)) {
            log.warn("Duplicate task title '{}' in projectId={}", req.title(), targetProjectId);
            throw new BusinessRuleException("Task title already exists in this project");
        }

        TaskMapper.applyUpdate(entity, req, targetProject);
        log.info("Task updated successfully: id={}", id);
        return TaskMapper.toResponse(entity);
    }

    /**
     * Deletes a task by ID.
     * 
     * @param id The ID of the task to delete.
     * @throws NotFoundException if the task is not found.
     */
    @Override
    public void delete(long id) {
        log.info("Attempting to delete task with id={}", id);
        if (!taskRepo.existsById(id)) {
            log.warn("Task not found: id={}", id);
            throw new NotFoundException("Task not found: " + id);
        }
        taskRepo.deleteById(id);
        log.info("Task deleted successfully: id={}", id);
    }
}
