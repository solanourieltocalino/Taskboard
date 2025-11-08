package com.jbk.taskboard.service.impl;

import com.jbk.taskboard.dto.task.*;
import com.jbk.taskboard.entity.AppUser;
import com.jbk.taskboard.entity.Project;
import com.jbk.taskboard.entity.Task;
import com.jbk.taskboard.entity.TaskPriority;
import com.jbk.taskboard.entity.TaskStatus;
import com.jbk.taskboard.exception.BusinessRuleException;
import com.jbk.taskboard.exception.NotFoundException;
import com.jbk.taskboard.repository.ProjectRepository;
import com.jbk.taskboard.repository.TaskRepository;
import com.jbk.taskboard.testutil.TestDataFactory;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskServiceImpl.
 * Uses Mockito for mocking dependencies.
 * Tests cover create, getById, list, update, and delete operations,
 * including validation and exception scenarios.
 * Each test is independent and verifies specific behavior of the service.
 * The repository is mocked to isolate service logic.
 * Validator is used to demonstrate DTO validation.
 * MockitoExtension is used to enable Mockito annotations.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    // --- Mocks and Service Under Test ---
    @Mock
    private TaskRepository taskRepo;

    @Mock
    private ProjectRepository projectRepo;

    @InjectMocks
    private TaskServiceImpl service;

    private Validator validator;

    @BeforeEach
    void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // --- create ---

    @SuppressWarnings("null")
    @Test
    void shouldCreateTask_whenProjectExistsAndTitleUnique() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(1L, "Alice", "alice@mail.com");
        Project project = TestDataFactory.projectEntity(10L, "Alpha", "P", owner);
        when(projectRepo.findById(10L)).thenReturn(Optional.of(project));
        when(taskRepo.existsByProject_IdAndTitleIgnoreCase(10L, "T1")).thenReturn(false);

        Task saved = TestDataFactory.taskEntity(100L, "T1", "desc", TaskStatus.TODO, TaskPriority.MEDIUM,
                LocalDate.of(2024, 5, 1), project);
        when(taskRepo.save(any(Task.class))).thenReturn(saved);

        TaskCreateRequestDTO req = TestDataFactory.taskCreateReq("T1", "desc", null, null, LocalDate.of(2024, 5, 1),
                10L);

        // Act
        TaskResponseDTO res = service.create(req);

        // Assert
        assertThat(res.id()).isEqualTo(100L);
        assertThat(res.title()).isEqualTo("T1");
        assertThat(res.status()).isEqualTo(TaskStatus.TODO);
        assertThat(res.priority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(res.project().id()).isEqualTo(10L);
        verify(projectRepo).findById(10L);
        verify(taskRepo).existsByProject_IdAndTitleIgnoreCase(10L, "T1");
        verify(taskRepo).save(any(Task.class));
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    @Test
    void shouldThrowNotFound_whenCreateProjectMissing() {
        // Arrange
        when(projectRepo.findById(999L)).thenReturn(Optional.empty());
        TaskCreateRequestDTO req = TestDataFactory.taskCreateReq("T1", "d", null, null, null, 999L);

        // Act + Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Project not found");
        verify(projectRepo).findById(999L);
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    @Test
    void shouldThrowBusinessRule_whenCreateDuplicateTitleInProject() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(1L, "Alice", "alice@mail.com");
        Project project = TestDataFactory.projectEntity(10L, "Alpha", "P", owner);
        when(projectRepo.findById(10L)).thenReturn(Optional.of(project));
        when(taskRepo.existsByProject_IdAndTitleIgnoreCase(10L, "T1")).thenReturn(true);

        TaskCreateRequestDTO req = TestDataFactory.taskCreateReq("T1", "d", null, null, null, 10L);

        // Act + Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Task title already exists");
        verify(projectRepo).findById(10L);
        verify(taskRepo).existsByProject_IdAndTitleIgnoreCase(10L, "T1");
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    // --- createForProject ---

    @SuppressWarnings("null")
    @Test
    void shouldCreateTaskForProject_whenProjectExistsAndTitleUnique() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(2L, "Bob", "bob@mail.com");
        Project project = TestDataFactory.projectEntity(20L, "Beta", "B", owner);
        when(projectRepo.findById(20L)).thenReturn(Optional.of(project));
        when(taskRepo.existsByProject_IdAndTitleIgnoreCase(20L, "T2")).thenReturn(false);

        Task saved = TestDataFactory.taskEntity(101L, "T2", "d2", TaskStatus.TODO, TaskPriority.MEDIUM,
                LocalDate.of(2024, 6, 1), project);
        when(taskRepo.save(any(Task.class))).thenReturn(saved);

        TaskCreateForProjectRequestDTO req = TestDataFactory.taskCreateForProjectReq("T2", "d2", null, null,
                LocalDate.of(2024, 6, 1));

        // Act
        TaskResponseDTO res = service.createForProject(20L, req);

        // Assert
        assertThat(res.id()).isEqualTo(101L);
        assertThat(res.title()).isEqualTo("T2");
        assertThat(res.project().id()).isEqualTo(20L);
        verify(projectRepo).findById(20L);
        verify(taskRepo).existsByProject_IdAndTitleIgnoreCase(20L, "T2");
        verify(taskRepo).save(any(Task.class));
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    @Test
    void shouldThrowBusinessRule_whenCreateForProjectDuplicateTitle() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(2L, "Bob", "bob@mail.com");
        Project project = TestDataFactory.projectEntity(20L, "Beta", "B", owner);
        when(projectRepo.findById(20L)).thenReturn(Optional.of(project));
        when(taskRepo.existsByProject_IdAndTitleIgnoreCase(20L, "T2")).thenReturn(true);

        TaskCreateForProjectRequestDTO req = TestDataFactory.taskCreateForProjectReq("T2", "d2", null, null, null);

        // Act + Assert
        assertThatThrownBy(() -> service.createForProject(20L, req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Task title already exists");
        verify(projectRepo).findById(20L);
        verify(taskRepo).existsByProject_IdAndTitleIgnoreCase(20L, "T2");
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    @Test
    void shouldThrowNotFound_whenCreateForProjectProjectMissing() {
        // Arrange
        when(projectRepo.findById(123L)).thenReturn(Optional.empty());
        TaskCreateForProjectRequestDTO req = TestDataFactory.taskCreateForProjectReq("T", "d", null, null, null);

        // Act + Assert
        assertThatThrownBy(() -> service.createForProject(123L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Project not found");
        verify(projectRepo).findById(123L);
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    // --- getById ---

    @Test
    void shouldReturnTask_whenGetByIdExists() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(3L, "Carol", "carol@mail.com");
        Project project = TestDataFactory.projectEntity(30L, "Gamma", "G", owner);
        Task entity = TestDataFactory.taskEntity(200L, "T3", "g", TaskStatus.DOING, TaskPriority.HIGH, null, project);
        when(taskRepo.findById(200L)).thenReturn(Optional.of(entity));

        // Act
        TaskResponseDTO res = service.getById(200L);

        // Assert
        assertThat(res.id()).isEqualTo(200L);
        assertThat(res.title()).isEqualTo("T3");
        assertThat(res.status()).isEqualTo(TaskStatus.DOING);
        assertThat(res.project().id()).isEqualTo(30L);
        verify(taskRepo).findById(200L);
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    @Test
    void shouldThrowNotFound_whenGetByIdMissing() {
        // Arrange
        when(taskRepo.findById(404L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> service.getById(404L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Task not found");
        verify(taskRepo).findById(404L);
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    // --- list (with filters) ---

    @SuppressWarnings("null")
    @Test
    void shouldListTasks_withFiltersAndPagination() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(4L, "Dan", "dan@mail.com");
        Project project = TestDataFactory.projectEntity(40L, "Delta", "D", owner);
        List<Task> content = List.of(
                TestDataFactory.taskEntity(3L, "A", "a", TaskStatus.DONE, TaskPriority.LOW, null, project),
                TestDataFactory.taskEntity(1L, "B", "b", TaskStatus.DONE, TaskPriority.LOW, null, project));
        Page<Task> page = new PageImpl<>(content, PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id")), 2);
        when(taskRepo.findAll(ArgumentMatchers.<Specification<Task>>any(), any(Pageable.class))).thenReturn(page);

        // Act
        var res = service.list(0, 2, TaskStatus.DONE, TaskPriority.LOW, 40L);

        // Assert
        assertThat(res.getTotalElements()).isEqualTo(2);
        assertThat(res.getContent()).extracting(TaskResponseDTO::id).containsExactly(3L, 1L);
        verify(taskRepo).findAll(ArgumentMatchers.<Specification<Task>>any(), any(Pageable.class));
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    // --- update ---

    @Test
    void shouldUpdateTask_whenTargetProjectExistsAndTitleUnique() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(5L, "Eve", "eve@mail.com");
        Project oldProject = TestDataFactory.projectEntity(50L, "OldP", "o", owner);
        Project newProject = TestDataFactory.projectEntity(51L, "NewP", "n", owner);
        Task entity = TestDataFactory.taskEntity(500L, "OldTitle", "od", TaskStatus.TODO, TaskPriority.MEDIUM, null,
                oldProject);

        when(taskRepo.findById(500L)).thenReturn(Optional.of(entity));
        when(projectRepo.findById(51L)).thenReturn(Optional.of(newProject));
        when(taskRepo.existsByProject_IdAndTitleIgnoreCaseAndIdNot(51L, "NewTitle", 500L)).thenReturn(false);

        TaskUpdateRequestDTO req = TestDataFactory.taskUpdateReq("NewTitle", "nd", TaskStatus.DOING, TaskPriority.HIGH,
                LocalDate.of(2025, 1, 1), 51L);

        // Act
        TaskResponseDTO res = service.update(500L, req);

        // Assert
        assertThat(res.id()).isEqualTo(500L);
        assertThat(res.title()).isEqualTo("NewTitle");
        assertThat(res.status()).isEqualTo(TaskStatus.DOING);
        assertThat(res.priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(res.project().id()).isEqualTo(51L);
        verify(taskRepo).findById(500L);
        verify(projectRepo).findById(51L);
        verify(taskRepo).existsByProject_IdAndTitleIgnoreCaseAndIdNot(51L, "NewTitle", 500L);
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    @Test
    void shouldThrowNotFound_whenUpdateTaskMissing() {
        // Arrange
        when(taskRepo.findById(600L)).thenReturn(Optional.empty());
        TaskUpdateRequestDTO req = TestDataFactory.taskUpdateReq("T", "d", TaskStatus.TODO, TaskPriority.MEDIUM, null,
                1L);

        // Act + Assert
        assertThatThrownBy(() -> service.update(600L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Task not found");
        verify(taskRepo).findById(600L);
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    @Test
    void shouldThrowNotFound_whenUpdateTargetProjectMissing() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(7L, "H", "h@mail.com");
        Project oldProject = TestDataFactory.projectEntity(70L, "P", "p", owner);
        Task entity = TestDataFactory.taskEntity(700L, "Title", "d", TaskStatus.TODO, TaskPriority.MEDIUM, null,
                oldProject);
        when(taskRepo.findById(700L)).thenReturn(Optional.of(entity));
        when(projectRepo.findById(999L)).thenReturn(Optional.empty());

        TaskUpdateRequestDTO req = TestDataFactory.taskUpdateReq("X", "d", TaskStatus.DONE, TaskPriority.LOW, null,
                999L);

        // Act + Assert
        assertThatThrownBy(() -> service.update(700L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Project not found");
        verify(taskRepo).findById(700L);
        verify(projectRepo).findById(999L);
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    @Test
    void shouldThrowBusinessRule_whenUpdateDuplicateTitleInTargetProject() {
        // Arrange
        AppUser owner = TestDataFactory.userEntity(8L, "I", "i@mail.com");
        Project project = TestDataFactory.projectEntity(80L, "P", "p", owner);
        Task entity = TestDataFactory.taskEntity(800L, "Old", "d", TaskStatus.TODO, TaskPriority.MEDIUM, null, project);
        when(taskRepo.findById(800L)).thenReturn(Optional.of(entity));
        when(projectRepo.findById(80L)).thenReturn(Optional.of(project));
        when(taskRepo.existsByProject_IdAndTitleIgnoreCaseAndIdNot(80L, "Clash", 800L)).thenReturn(true);

        TaskUpdateRequestDTO req = TestDataFactory.taskUpdateReq("Clash", "d2", TaskStatus.DOING, TaskPriority.HIGH,
                null, 80L);

        // Act + Assert
        assertThatThrownBy(() -> service.update(800L, req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Task title already exists");
        verify(taskRepo).findById(800L);
        verify(projectRepo).findById(80L);
        verify(taskRepo).existsByProject_IdAndTitleIgnoreCaseAndIdNot(80L, "Clash", 800L);
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    // --- delete ---

    @Test
    void shouldDelete_whenExists() {
        // Arrange
        when(taskRepo.existsById(900L)).thenReturn(true);

        // Act
        service.delete(900L);

        // Assert
        verify(taskRepo).existsById(900L);
        verify(taskRepo).deleteById(900L);
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    @Test
    void shouldThrowNotFound_whenDeleteMissing() {
        // Arrange
        when(taskRepo.existsById(901L)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> service.delete(901L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Task not found");
        verify(taskRepo).existsById(901L);
        verifyNoMoreInteractions(taskRepo, projectRepo);
    }

    // --- simple DTO validation ---

    @Test
    void shouldFailValidation_whenCreateTitleBlank() {
        // Arrange
        TaskCreateRequestDTO invalid = new TaskCreateRequestDTO("  ", "d", null, null, null, 1L);

        // Act
        var violations = validator.validate(invalid);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }

    @Test
    void shouldFailValidation_whenUpdateStatusNull() {
        // Arrange
        TaskUpdateRequestDTO invalid = new TaskUpdateRequestDTO("Test", "Test desc", null, TaskPriority.LOW, null, 1L);

        // Act
        var violations = validator.validate(invalid);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("status"));
    }

    @Test
    void shouldFailValidation_whenUpdatePriorityNull() {
        // Arrange
        TaskUpdateRequestDTO invalid = new TaskUpdateRequestDTO("Test", "Test desc", TaskStatus.TODO, null, null, 1L);

        // Act
        var violations = validator.validate(invalid);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("priority"));
    }
}
