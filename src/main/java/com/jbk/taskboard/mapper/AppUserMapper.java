package com.jbk.taskboard.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jbk.taskboard.dto.user.AppUserCreateRequestDTO;
import com.jbk.taskboard.dto.user.AppUserResponseDTO;
import com.jbk.taskboard.dto.user.AppUserUpdateRequestDTO;
import com.jbk.taskboard.entity.AppUser;

/**
 * Mapper class for converting between AppUser entities and DTOs.
 * Provides methods to convert create and update request DTOs to entities,
 * and to convert entities to response DTOs.
 */
public final class AppUserMapper {

    private static final Logger log = LoggerFactory.getLogger(AppUserMapper.class);

    // Private constructor to prevent instantiation.
    private AppUserMapper() {
    }

    /**
     * Converts a create request DTO to an AppUser entity.
     * 
     * @param req
     * @return
     */
    public static AppUser toEntity(AppUserCreateRequestDTO req) {
        log.debug("Mapping AppUserCreateRequestDTO to AppUser entity");
        AppUser u = new AppUser();
        u.setName(req.name());
        u.setEmail(req.email());
        return u;
    }

    /**
     * Applies updates from an update request DTO to an existing AppUser entity.
     * 
     * @param entity
     * @param req
     */
    public static void applyUpdate(AppUser entity, AppUserUpdateRequestDTO req) {
        log.debug("Applying updates to AppUser entity with id={}", entity.getId());
        entity.setName(req.name());
        entity.setEmail(req.email());
    }

    /**
     * Converts an AppUser entity to a response DTO.
     * 
     * @param e
     * @return
     */
    public static AppUserResponseDTO toResponse(AppUser e) {
        log.debug("Mapping AppUser entity (id={}) to AppUserResponseDTO", e.getId());
        return new AppUserResponseDTO(
                e.getId(),
                e.getName(),
                e.getEmail(),
                e.getCreatedAt());
    }
}
