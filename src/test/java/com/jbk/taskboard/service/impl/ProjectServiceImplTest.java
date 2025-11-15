package com.jbk.taskboard.service.impl;

import com.jbk.taskboard.dto.project.*;
import com.jbk.taskboard.entity.AppUser;
import com.jbk.taskboard.entity.Project;
import com.jbk.taskboard.exception.BusinessRuleException;
import com.jbk.taskboard.exception.NotFoundException;
import com.jbk.taskboard.repository.AppUserRepository;
import com.jbk.taskboard.repository.ProjectRepository;
import com.jbk.taskboard.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProjectServiceImpl.
 * Uses Mockito for mocking dependencies.
 * Tests cover create, getById, list, update, and delete operations,
 * including validation and exception scenarios.
 * Each test is independent and verifies specific behavior of the service.
 * The repository is mocked to isolate service logic.
 * Validator is used to demonstrate DTO validation.
 * MockitoExtension is used to enable Mockito annotations.
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    // --- Mocks and Service Under Test ---
    @Mock
    private ProjectRepository projectRepo;

    @Mock
    private AppUserRepository userRepo;

    @InjectMocks
    private ProjectServiceImpl service;

    private Validator validator;

    @BeforeEach
    void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // --- CREATE ---

    /**
     * Should create project when owner exists and name is unique.
     * Verifies that the repository's findById, existsByOwner_IdAndNameIgnoreCase,
     * and save methods are called.
     * Asserts that the returned DTO has the expected values.
     * 
     * @throws NotFoundException     if owner is not found (not
     *                               expected in this test).
     * @throws BusinessRuleException when project name is not
     *                               unique for owner.
     */
    @SuppressWarnings("null")
    @Test
    void shouldCreateProject_whenOwnerExistsAndNameUnique() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(1L, "Alice", "alice@mail.com");
        when(userRepo.findById(1L)).thenReturn(Optional.of(owner));
        when(projectRepo.existsByOwner_IdAndNameIgnoreCase(1L, "Alpha")).thenReturn(false);

        Project saved = TestDataFactory.projectEntity(100L, "Alpha", "Desc", owner);
        when(projectRepo.save(any(Project.class))).thenReturn(saved);

        ProjectRequestDTO req = TestDataFactory.projectReq("Alpha", "Desc", 1L);

        // Act
        ProjectResponseDTO res = service.create(req);

        // Assert
        assertThat(res.id()).isEqualTo(100L);
        assertThat(res.name()).isEqualTo("Alpha");
        assertThat(res.owner().id()).isEqualTo(1L);
        verify(userRepo).findById(1L);
        verify(projectRepo).existsByOwner_IdAndNameIgnoreCase(1L, "Alpha");
        verify(projectRepo).save(any(Project.class));
        verifyNoMoreInteractions(projectRepo, userRepo);
    }

    /**
     * Should throw NotFoundException when creating project with missing owner.
     * Verifies that the repository's findById method is called.
     * 
     * @throws NotFoundException     when owner is not found.
     * @throws BusinessRuleException when project name is not
     *                               unique for owner.
     */
    @Test
    void shouldThrowNotFound_whenCreateOwnerMissing() {
        // Arrange
        when(userRepo.findById(7L)).thenReturn(Optional.empty());
        ProjectRequestDTO req = TestDataFactory.projectReq("Alpha", "Desc", 7L);

        // Act + Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Owner not found");
        verify(userRepo).findById(7L);
        verifyNoMoreInteractions(projectRepo, userRepo);
    }

    /**
     * Should throw BusinessRuleException when creating project with duplicate name
     * per owner.
     * Verifies that the repository's findById and existsByOwner_IdAndNameIgnoreCase
     * methods are called.
     * Asserts that no project is saved.
     * 
     * @throws NotFoundException     if owner is not found (not
     *                               expected in this test).
     * @throws BusinessRuleException when project name is not
     *                               unique for owner.
     */
    @Test
    void shouldThrowBusinessRule_whenCreateDuplicateNamePerOwner() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(2L, "Bob", "bob@mail.com");
        when(userRepo.findById(2L)).thenReturn(Optional.of(owner));
        when(projectRepo.existsByOwner_IdAndNameIgnoreCase(2L, "Alpha")).thenReturn(true);

        ProjectRequestDTO req = TestDataFactory.projectReq("Alpha", "Desc", 2L);

        // Act + Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Project name already exists");
        verify(userRepo).findById(2L);
        verify(projectRepo).existsByOwner_IdAndNameIgnoreCase(2L, "Alpha");
        verifyNoMoreInteractions(projectRepo, userRepo);
    }

    // --- GET BY ID ---

    /**
     * Should return project when getById exists.
     * Verifies that the repository's findById method is called.
     * Asserts that the returned DTO has the expected values.
     * 
     * @throws NotFoundException if project is not found (not expected in this
     *                           test).
     */
    @Test
    void shouldReturnProject_whenGetByIdExists() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(3L, "Carol", "carol@mail.com");
        Project entity = TestDataFactory.projectEntity(200L, "Beta", "B", owner);
        when(projectRepo.findById(200L)).thenReturn(Optional.of(entity));

        // Act
        ProjectResponseDTO res = service.getById(200L);

        // Assert
        assertThat(res.id()).isEqualTo(200L);
        assertThat(res.name()).isEqualTo("Beta");
        assertThat(res.owner().email()).isEqualTo("carol@mail.com");
        verify(projectRepo).findById(200L);
        verifyNoMoreInteractions(projectRepo, userRepo);
    }

    /**
     * Should throw NotFoundException when getById missing.
     * Verifies that the repository's findById method is called.
     * 
     * @throws NotFoundException if project is not found (expected in this test).
     */
    @Test
    void shouldThrowNotFound_whenGetByIdMissing() {
        // Arrange
        when(projectRepo.findById(404L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> service.getById(404L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Project not found");
        verify(projectRepo).findById(404L);
        verifyNoMoreInteractions(projectRepo, userRepo);
    }

    // --- LIST ---

    /**
     * Should list projects with pagination.
     * Verifies that the repository's findAll method is called.
     * Asserts that the returned page has the expected content and order.
     * 
     * @throws NotFoundException if any referenced entity is not found (not expected
     *                           in
     *                           this test).
     */
    @SuppressWarnings("null")
    @Test
    void shouldListProjects_withPagination() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(5L, "Dan", "dan@mail.com");
        List<Project> content = List.of(
                TestDataFactory.projectEntity(3L, "Gamma", "G", owner),
                TestDataFactory.projectEntity(1L, "Omega", "O", owner));
        Page<Project> page = new PageImpl<>(content, PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id")), 2);
        when(projectRepo.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Page<ProjectResponseDTO> res = service.list(0, 2);

        // Assert
        assertThat(res.getTotalElements()).isEqualTo(2);
        assertThat(res.getContent()).extracting(ProjectResponseDTO::id).containsExactly(3L, 1L);
        verify(projectRepo).findAll(any(Pageable.class));
        verifyNoMoreInteractions(projectRepo, userRepo);
    }

    // --- UPDATE ---

    /**
     * Should update project when owner exists and name is unique.
     * Verifies that the repository's findById,
     * existsByOwner_IdAndNameIgnoreCaseAndIdNot,
     * and save methods are called.
     * Asserts that the returned DTO has the expected values.
     * 
     * @throws NotFoundException     if project or owner is not found.
     * @throws BusinessRuleException when project name is not unique per owner.
     */
    @Test
    void shouldUpdateProject_whenOwnerExistsAndNameUnique() {
        // Arrange
        AppUser oldOwner = TestDataFactory.userEntity(6L, "Eve", "eve@mail.com");
        AppUser newOwner = TestDataFactory.userEntity(7L, "Frank", "frank@mail.com");
        Project entity = TestDataFactory.projectEntity(500L, "Old", "D", oldOwner);

        when(projectRepo.findById(500L)).thenReturn(Optional.of(entity));
        when(userRepo.findById(7L)).thenReturn(Optional.of(newOwner));
        when(projectRepo.existsByOwner_IdAndNameIgnoreCaseAndIdNot(7L, "NewName", 500L)).thenReturn(false);

        ProjectRequestDTO req = TestDataFactory.projectReq("NewName", "NewDesc", 7L);

        // Act
        ProjectResponseDTO res = service.update(500L, req);

        // Assert
        assertThat(res.id()).isEqualTo(500L);
        assertThat(res.name()).isEqualTo("NewName");
        assertThat(res.description()).isEqualTo("NewDesc");
        assertThat(res.owner().id()).isEqualTo(7L);
        verify(projectRepo).findById(500L);
        verify(userRepo).findById(7L);
        verify(projectRepo).existsByOwner_IdAndNameIgnoreCaseAndIdNot(7L, "NewName", 500L);
        verifyNoMoreInteractions(projectRepo, userRepo);
    }

    /**
     * Should throw NotFoundException when updating project with missing project.
     * Verifies that the repository's findById method is called.
     * 
     * @throws NotFoundException     when project is not found.
     * @throws BusinessRuleException when project name is not unique per owner.
     */
    @Test
    void shouldThrowNotFound_whenUpdateProjectMissing() {
        // Arrange
        when(projectRepo.findById(600L)).thenReturn(Optional.empty());
        ProjectRequestDTO req = TestDataFactory.projectReq("X", "D", 1L);

        // Act + Assert
        assertThatThrownBy(() -> service.update(600L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Project not found");
        verify(projectRepo).findById(600L);
        verifyNoMoreInteractions(projectRepo, userRepo);
    }

    /**
     * Should throw NotFoundException when updating project with missing owner.
     * Verifies that the repository's findById method is called.
     * 
     * @throws NotFoundException     when owner is not found.
     * @throws BusinessRuleException when project name is not unique per owner.
     */
    @Test
    void shouldThrowNotFound_whenUpdateOwnerMissing() {
        // Arrange
        AppUser oldOwner = TestDataFactory.userEntity(8L, "Gus", "gus@mail.com");
        Project entity = TestDataFactory.projectEntity(700L, "P", "D", oldOwner);
        when(projectRepo.findById(700L)).thenReturn(Optional.of(entity));
        when(userRepo.findById(999L)).thenReturn(Optional.empty());

        ProjectRequestDTO req = TestDataFactory.projectReq("P2", "D2", 999L);

        // Act + Assert
        assertThatThrownBy(() -> service.update(700L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Owner not found");
        verify(projectRepo).findById(700L);
        verify(userRepo).findById(999L);
        verifyNoMoreInteractions(projectRepo, userRepo);
    }

    /**
     * Should throw BusinessRuleException when updating project with duplicate name
     * per owner.
     * Verifies that the repository's findById and
     * existsByOwner_IdAndNameIgnoreCaseAndIdNot methods are called.
     * 
     * @throws NotFoundException     when project or owner is not found.
     * @throws BusinessRuleException when project name is not unique per owner.
     */
    @Test
    void shouldThrowBusinessRule_whenUpdateDuplicateNamePerOwner() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(10L, "H", "h@mail.com");
        Project entity = TestDataFactory.projectEntity(800L, "Old", "D", owner);
        when(projectRepo.findById(800L)).thenReturn(Optional.of(entity));
        when(userRepo.findById(10L)).thenReturn(Optional.of(owner));
        when(projectRepo.existsByOwner_IdAndNameIgnoreCaseAndIdNot(10L, "Clash", 800L)).thenReturn(true);

        ProjectRequestDTO req = TestDataFactory.projectReq("Clash", "D2", 10L);

        // Act + Assert
        assertThatThrownBy(() -> service.update(800L, req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Project name already exists");
        verify(projectRepo).findById(800L);
        verify(userRepo).findById(10L);
        verify(projectRepo).existsByOwner_IdAndNameIgnoreCaseAndIdNot(10L, "Clash", 800L);
        verifyNoMoreInteractions(projectRepo, userRepo);
    }

    // --- DELETE ---

    /**
     * Should delete project when it exists.
     * Verifies that the repository's existsById and deleteById methods are called.
     * 
     * @throws NotFoundException if project is not found (not expected in this
     *                           test).
     */
    @Test
    void shouldDelete_whenExists() {
        // Arrange
        when(projectRepo.existsById(900L)).thenReturn(true);

        // Act
        service.delete(900L);

        // Assert
        verify(projectRepo).existsById(900L);
        verify(projectRepo).deleteById(900L);
        verifyNoMoreInteractions(projectRepo, userRepo);
    }

    /**
     * Should throw NotFoundException when deleting missing project.
     * Verifies that the repository's existsById method is called.
     * 
     * @throws NotFoundException when project is not found.
     */
    @Test
    void shouldThrowNotFound_whenDeleteMissing() {
        // Arrange
        when(projectRepo.existsById(901L)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> service.delete(901L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Project not found");
        verify(projectRepo).existsById(901L);
        verifyNoMoreInteractions(projectRepo, userRepo);
    }

    // --- SIMPLE DTO VALIDATION ---

    /**
     * Should fail validation when name is blank.
     * Asserts that the validation violations contain an entry for the name field.
     * 
     * @throws BusinessRuleException if validation fails (expected in this test).
     */
    @Test
    void shouldFailValidation_whenNameBlank() {
        // Arrange
        ProjectRequestDTO invalid = new ProjectRequestDTO("  ", "desc", 1L);

        // Act
        var violations = validator.validate(invalid);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }
}
