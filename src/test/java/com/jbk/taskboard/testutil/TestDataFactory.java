package com.jbk.taskboard.testutil;

import com.jbk.taskboard.dto.user.AppUserRequestDTO;
import com.jbk.taskboard.dto.project.ProjectRequestDTO;
import com.jbk.taskboard.dto.task.TaskCreateRequestDTO;
import com.jbk.taskboard.dto.task.TaskCreateForProjectRequestDTO;
import com.jbk.taskboard.dto.task.TaskUpdateRequestDTO;
import com.jbk.taskboard.entity.*;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Factory class for creating test data objects.
 * Provides static methods to create instances of DTOs and entities for testing
 * purposes.
 * This class should not be instantiated.
 */
public final class TestDataFactory {

    // Private constructor to prevent instantiation
    private TestDataFactory() {
    }

    /**
     * Creates an AppUserRequestDTO with the given name and email.
     * 
     * @param name
     * @param email
     * @return
     */
    public static AppUserRequestDTO userReq(String name, String email) {
        return new AppUserRequestDTO(name, email);
    }

    /**
     * Creates an AppUser entity with the given id, name, and email.
     * 
     * @param id
     * @param name
     * @param email
     * @return
     */
    public static AppUser userEntity(long id, String name, String email) {
        AppUser u = new AppUser();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        u.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
        return u;
    }

    /**
     * Creates a ProjectRequestDTO with the given name, desc and ownerId.
     * 
     * @param name
     * @param desc
     * @param ownerId
     * @return
     */
    public static ProjectRequestDTO projectReq(String name, String desc, long ownerId) {
        return new ProjectRequestDTO(name, desc, ownerId);
    }

    /**
     * Creates a Project entity with the given id, name, desc and owner.
     * 
     * @param id
     * @param name
     * @param desc
     * @param owner
     * @return
     */
    public static Project projectEntity(long id, String name, String desc, AppUser owner) {
        Project p = new Project();
        p.setId(id);
        p.setName(name);
        p.setDescription(desc);
        p.setOwner(owner);
        p.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
        return p;
    }

    /**
     * Creates a TaskCreateRequestDTO with the given title, desc, status, priority,
     * dueDate and projectId.
     * 
     * @param title
     * @param desc
     * @param status
     * @param priority
     * @param dueDate
     * @param projectId
     * @return
     */
    public static TaskCreateRequestDTO taskCreateReq(String title, String desc, TaskStatus status,
            TaskPriority priority, LocalDate dueDate, long projectId) {
        return new TaskCreateRequestDTO(title, desc, status, priority, dueDate, projectId);
    }

    /**
     * Creates a TaskCreateForProjectRequestDTO with the given title, desc, status,
     * priority and dueDate.
     * 
     * @param title
     * @param desc
     * @param status
     * @param priority
     * @param dueDate
     * @return
     */
    public static TaskCreateForProjectRequestDTO taskCreateForProjectReq(String title, String desc, TaskStatus status,
            TaskPriority priority, LocalDate dueDate) {
        return new TaskCreateForProjectRequestDTO(title, desc, status, priority, dueDate);
    }

    /**
     * Creates a TaskUpdateRequestDTO with the given title, desc, status, priority,
     * dueDate and projectId.
     * 
     * @param title
     * @param desc
     * @param status
     * @param priority
     * @param dueDate
     * @param projectId
     * @return
     */
    public static TaskUpdateRequestDTO taskUpdateReq(String title, String desc, TaskStatus status,
            TaskPriority priority, LocalDate dueDate, long projectId) {
        return new TaskUpdateRequestDTO(title, desc, status, priority, dueDate, projectId);
    }

    /**
     * Creates a Task entity with the given id, title, desc, status, priority,
     * dueDate and project.
     * 
     * @param id
     * @param title
     * @param desc
     * @param status
     * @param priority
     * @param dueDate
     * @param project
     * @return
     */
    public static Task taskEntity(long id, String title, String desc, TaskStatus status,
            TaskPriority priority, LocalDate dueDate, Project project) {
        Task t = new Task();
        t.setId(id);
        t.setTitle(title);
        t.setDescription(desc);
        t.setStatus(status);
        t.setPriority(priority);
        t.setDueDate(dueDate);
        t.setProject(project);
        t.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
        return t;
    }
}
