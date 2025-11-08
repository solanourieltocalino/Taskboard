package com.jbk.taskboard.service.impl;

import com.jbk.taskboard.dto.user.*;
import com.jbk.taskboard.entity.AppUser;
import com.jbk.taskboard.exception.BusinessRuleException;
import com.jbk.taskboard.exception.NotFoundException;
import com.jbk.taskboard.repository.AppUserRepository;
import com.jbk.taskboard.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AppUserServiceImpl.
 * Uses Mockito for mocking dependencies.
 * Tests cover create, getById, list, update, and delete operations,
 * including validation and exception scenarios.
 * Each test is independent and verifies specific behavior of the service.
 * The repository is mocked to isolate service logic.
 * Validator is used to demonstrate DTO validation.
 * MockitoExtension is used to enable Mockito annotations.
 */
@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {

    // --- Mocks and Service Under Test ---
    @Mock
    private AppUserRepository repo;

    @InjectMocks
    private AppUserServiceImpl service;

    private Validator validator;

    @BeforeEach
    void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // --- create ---

    @SuppressWarnings("null")
    @Test
    void shouldCreateUser_whenEmailNotExists() {
        // Arrange
        AppUserRequestDTO req = TestDataFactory.userReq("Alice", "alice@mail.com");
        when(repo.existsByEmail("alice@mail.com")).thenReturn(false);
        AppUser saved = TestDataFactory.userEntity(1L, "Alice", "alice@mail.com");
        when(repo.save(any(AppUser.class))).thenReturn(saved);

        // Act
        AppUserResponseDTO res = service.create(req);

        // Assert
        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.name()).isEqualTo("Alice");
        assertThat(res.email()).isEqualTo("alice@mail.com");
        verify(repo).existsByEmail("alice@mail.com");
        verify(repo).save(any(AppUser.class));
        verifyNoMoreInteractions(repo);
    }

    @Test
    void shouldThrowBusinessRule_whenCreateWithDuplicateEmail() {
        // Arrange
        AppUserRequestDTO req = TestDataFactory.userReq("Bob", "dup@mail.com");
        when(repo.existsByEmail("dup@mail.com")).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Email already in use");
        verify(repo).existsByEmail("dup@mail.com");
        verifyNoMoreInteractions(repo);
    }

    // --- getById ---

    @Test
    void shouldReturnUser_whenGetByIdExists() {
        // Arrange
        AppUser entity = TestDataFactory.userEntity(5L, "Carol", "carol@mail.com");
        when(repo.findById(5L)).thenReturn(Optional.of(entity));

        // Act
        AppUserResponseDTO res = service.getById(5L);

        // Assert
        assertThat(res.id()).isEqualTo(5L);
        assertThat(res.name()).isEqualTo("Carol");
        assertThat(res.email()).isEqualTo("carol@mail.com");
        verify(repo).findById(5L);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void shouldThrowNotFound_whenGetByIdMissing() {
        // Arrange
        when(repo.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
        verify(repo).findById(99L);
        verifyNoMoreInteractions(repo);
    }

    // --- list ---

    @SuppressWarnings("null")
    @Test
    void shouldListUsers_withPagination() {
        // Arrange
        List<AppUser> content = List.of(
                TestDataFactory.userEntity(2L, "A", "a@mail.com"),
                TestDataFactory.userEntity(1L, "B", "b@mail.com"));
        Page<AppUser> page = new PageImpl<>(content, PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id")), 2);
        when(repo.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Page<AppUserResponseDTO> res = service.list(0, 2);

        // Assert
        assertThat(res.getTotalElements()).isEqualTo(2);
        assertThat(res.getContent()).extracting(AppUserResponseDTO::id).containsExactly(2L, 1L);
        verify(repo).findAll(any(Pageable.class));
        verifyNoMoreInteractions(repo);
    }

    // --- update ---

    @Test
    void shouldUpdateUser_whenEmailChangedAndNotTaken() {
        // Arrange
        AppUser entity = TestDataFactory.userEntity(10L, "Old", "old@mail.com");
        when(repo.findById(10L)).thenReturn(Optional.of(entity));
        when(repo.existsByEmail("new@mail.com")).thenReturn(false);
        AppUserRequestDTO req = TestDataFactory.userReq("New Name", "new@mail.com");

        // Act
        AppUserResponseDTO res = service.update(10L, req);

        // Assert
        assertThat(res.id()).isEqualTo(10L);
        assertThat(res.name()).isEqualTo("New Name");
        assertThat(res.email()).isEqualTo("new@mail.com");
        verify(repo).findById(10L);
        verify(repo).existsByEmail("new@mail.com");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void shouldNotCheckEmailUniqueness_whenEmailUnchanged() {
        // Arrange
        AppUser entity = TestDataFactory.userEntity(11L, "Same", "same@mail.com");
        when(repo.findById(11L)).thenReturn(Optional.of(entity));
        AppUserRequestDTO req = TestDataFactory.userReq("Same Name", "same@mail.com");

        // Act
        AppUserResponseDTO res = service.update(11L, req);

        // Assert
        assertThat(res.name()).isEqualTo("Same Name");
        assertThat(res.email()).isEqualTo("same@mail.com");
        // existsByEmail should NOT be called when the email does not change
        verify(repo).findById(11L);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void shouldThrowBusinessRule_whenUpdateEmailTaken() {
        // Arrange
        AppUser entity = TestDataFactory.userEntity(12L, "User", "old@mail.com");
        when(repo.findById(12L)).thenReturn(Optional.of(entity));
        when(repo.existsByEmail("taken@mail.com")).thenReturn(true);
        AppUserRequestDTO req = TestDataFactory.userReq("User", "taken@mail.com");

        // Act + Assert
        assertThatThrownBy(() -> service.update(12L, req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Email already in use");
        verify(repo).findById(12L);
        verify(repo).existsByEmail("taken@mail.com");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void shouldThrowNotFound_whenUpdateMissing() {
        // Arrange
        when(repo.findById(77L)).thenReturn(Optional.empty());
        AppUserRequestDTO req = TestDataFactory.userReq("X", "x@mail.com");

        // Act + Assert
        assertThatThrownBy(() -> service.update(77L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
        verify(repo).findById(77L);
        verifyNoMoreInteractions(repo);
    }

    // --- delete ---

    @Test
    void shouldDelete_whenExists() {
        // Arrange
        when(repo.existsById(15L)).thenReturn(true);

        // Act
        service.delete(15L);

        // Assert
        verify(repo).existsById(15L);
        verify(repo).deleteById(15L);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void shouldThrowNotFound_whenDeleteMissing() {
        // Arrange
        when(repo.existsById(16L)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> service.delete(16L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
        verify(repo).existsById(16L);
        verifyNoMoreInteractions(repo);
    }

    // --- simple DTO validation ---

    @Test
    void shouldFailValidation_whenNameBlank() {
        // Arrange
        AppUserRequestDTO invalid = new AppUserRequestDTO("  ", "valid@mail.com");

        // Act
        var violations = validator.validate(invalid);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void shouldFailValidation_whenEmailNotValid() {
        // Arrange
        AppUserRequestDTO invalid = new AppUserRequestDTO("Test", "Test Email");

        // Act
        var violations = validator.validate(invalid);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }
}
