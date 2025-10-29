package com.jbk.taskboard.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entity class representing a project.
 * Maps to the "project" table in the database.
 * Includes fields for ID, name, description, creation timestamp, and owner.
 * Uses JPA annotations for ORM mapping.
 * The owner field establishes a many-to-one relationship with the AppUser
 * entity.
 */
@Entity
@Table(name = "project")
public class Project {

    // Primary key (auto-incremented by the database).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Project's name (required, max length 120).
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    // Project's description (optional, max length 500).
    @Column(name = "description", length = 500)
    private String description;

    // Timestamp automatically set by the database (CURRENT_TIMESTAMP).
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    // Many-to-one relationship with AppUser (owner of the project).
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }
}
