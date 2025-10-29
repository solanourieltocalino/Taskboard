package com.jbk.taskboard.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jbk.taskboard.dto.project.*;
import com.jbk.taskboard.dto.user.AppUserResponseDTO;
import com.jbk.taskboard.entity.AppUser;
import com.jbk.taskboard.entity.Project;

/**
 * Mapper class for converting between Project entities and DTOs.
 * Provides methods to map request DTOs to entities and entities to response
 * DTOs.
 * All methods are static and the class cannot be instantiated.
 */
public final class ProjectMapper {

    private static final Logger log = LoggerFactory.getLogger(ProjectMapper.class);

    // Private constructor to prevent instantiation.
    private ProjectMapper() {
    }

    /**
     * Converts a ProjectRequestDTO to a Project entity.
     * 
     * @param req
     * @param owner
     * @return
     */
    public static Project toEntity(ProjectRequestDTO req, AppUser owner) {
        log.debug("Mapping ProjectRequestDTO to Project entity");
        Project p = new Project();
        p.setName(req.name());
        p.setDescription(req.description());
        p.setOwner(owner);
        return p;
    }

    /**
     * Applies updates from a ProjectRequestDTO to an existing Project entity.
     * 
     * @param entity
     * @param req
     * @param owner
     */
    public static void applyUpdate(Project entity, ProjectRequestDTO req, AppUser owner) {
        log.debug("Applying updates to Project entity with id={}", entity.getId());
        entity.setName(req.name());
        entity.setDescription(req.description());
        entity.setOwner(owner);
    }

    /**
     * Converts a Project entity to a ProjectResponseDTO.
     * 
     * @param e
     * @return
     */
    public static ProjectResponseDTO toResponse(Project e) {
        log.debug("Mapping Project entity (id={}) to ProjectResponseDTO", e.getId());
        AppUser o = e.getOwner();
        AppUserResponseDTO ownerDTO = AppUserResponseDTO.of(o.getId(), o.getName(), o.getEmail());

        return ProjectResponseDTO.of(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getCreatedAt(),
                ownerDTO);
    }
}
