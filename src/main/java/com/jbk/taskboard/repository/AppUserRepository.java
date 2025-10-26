package com.jbk.taskboard.repository;

import com.jbk.taskboard.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for AppUser entities.
 * Extends JpaRepository to provide CRUD operations.
 * Includes methods to check existence by email and to find by email.
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    // Checks if a user already exists with the given email.
    boolean existsByEmail(String email);

    // Finds a user by email and returns an Optional<AppUser>.
    Optional<AppUser> findByEmail(String email);
}
