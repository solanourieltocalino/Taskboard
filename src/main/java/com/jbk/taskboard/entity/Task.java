package com.jbk.taskboard.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Entity class representing a task.
 * Maps to the "task" table in the database.
 * Includes fields for ID, title, description, status, priority, creation
 * timestamp, due date, and associated project.
 * Uses JPA annotations for ORM mapping.
 * The project field establishes a many-to-one relationship with the Project
 * entity.
 * Status and priority fields use enumerated types for predefined values.
 * Defaults: status = TODO, priority = MEDIUM.
 */
@Entity
@Table(name = "task")
public class Task {

    // Primary key (auto-incremented by the database).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Task's title (required, max length 150).
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    // Task's description (optional, max length 1000).
    @Column(name = "description", length = 1000)
    private String description;

    // Task's status (enum: TODO, IN_PROGRESS, DONE). Default is TODO.
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private TaskStatus status = TaskStatus.TODO;

    // Task's priority (enum: LOW, MEDIUM, HIGH). Default is MEDIUM.
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 10)
    private TaskPriority priority = TaskPriority.MEDIUM;

    // Timestamp automatically set by the database (CURRENT_TIMESTAMP).
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    // Due date for the task (optional).
    @Column(name = "due_date")
    private LocalDate dueDate;

    // Many-to-one relationship with Project (the project this task belongs to).
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
