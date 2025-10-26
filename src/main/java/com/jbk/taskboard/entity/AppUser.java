package com.jbk.taskboard.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entity class representing an application user.
 * Maps to the "app_user" table in the database.
 * Includes fields for ID, name, email, and creation timestamp.
 * Uses JPA annotations for ORM mapping.
 */
@Entity
@Table(name = "app_user")
public class AppUser {

    // Primary key (auto-incremented by the database).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User's name (required, max length 100).
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // User's email (required, unique, max length 120).
    @Column(name = "email", nullable = false, length = 120, unique = true)
    private String email;

    // Timestamp automatically set by the database (CURRENT_TIMESTAMP).
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
