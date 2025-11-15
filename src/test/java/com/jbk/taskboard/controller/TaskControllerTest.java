package com.jbk.taskboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbk.taskboard.dto.task.*;
import com.jbk.taskboard.dto.project.ProjectResponseDTO;
import com.jbk.taskboard.dto.user.AppUserResponseDTO;
import com.jbk.taskboard.entity.TaskPriority;
import com.jbk.taskboard.entity.TaskStatus;
import com.jbk.taskboard.exception.ApiExceptionHandler;
import com.jbk.taskboard.exception.BusinessRuleException;
import com.jbk.taskboard.exception.NotFoundException;
import com.jbk.taskboard.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web slice test for TaskController.
 * Mocks TaskService and imports ApiExceptionHandler for exception handling.
 * Uses MockMvc to perform HTTP requests and verify responses.
 */
@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;

    @SuppressWarnings("removal")
    @MockBean
    private TaskService service;

    // Helper methods to create DTOs for tests
    private AppUserResponseDTO owner(long id) {
        return AppUserResponseDTO.of(id, "User" + id, "u" + id + "@mail.com");
    }

    private ProjectResponseDTO project(long id, long ownerId) {
        return ProjectResponseDTO.of(id, "P" + id, "Desc", owner(ownerId));
    }

    private TaskResponseDTO task(long id, String title, TaskStatus st, TaskPriority pr, long projectId, long ownerId) {
        return TaskResponseDTO.of(
                id, title, "d", st, pr,
                Instant.parse("2024-01-01T00:00:00Z"),
                LocalDate.of(2024, 5, 1),
                project(projectId, ownerId));
    }

    /**
     * --- POST /api/tasks (201) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldCreateTask_andReturn201WithLocation() throws Exception {
        var req = new TaskCreateRequestDTO("T1", "d", null, null, LocalDate.of(2024, 5, 1), 10L);
        var res = task(100L, "T1", TaskStatus.TODO, TaskPriority.MEDIUM, 10L, 1L);

        given(service.create(any(TaskCreateRequestDTO.class))).willReturn(res);

        mvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/tasks/100")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.title").value("T1"))
                .andExpect(jsonPath("$.project.id").value(10));

        verify(service).create(any(TaskCreateRequestDTO.class));
    }

    /**
     * --- POST /api/tasks (400) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn400_whenCreateBodyInvalid() throws Exception {
        var bad = new TaskCreateRequestDTO("  ", "d", null, null, null, 10L);

        mvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation errors"))
                .andExpect(jsonPath("$.messages.title").exists());

        Mockito.verifyNoInteractions(service);
    }

    /**
     * --- POST /api/projects/{projectId}/tasks (201) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldCreateTaskForProject_andReturn201WithLocation() throws Exception {
        var req = new TaskCreateForProjectRequestDTO("T2", "d2", null, null, LocalDate.of(2024, 6, 1));
        var res = task(101L, "T2", TaskStatus.TODO, TaskPriority.MEDIUM, 20L, 2L);

        given(service.createForProject(eq(20L), any(TaskCreateForProjectRequestDTO.class))).willReturn(res);

        mvc.perform(post("/api/projects/20/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/tasks/101")))
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.project.id").value(20));

        verify(service).createForProject(eq(20L), any(TaskCreateForProjectRequestDTO.class));
    }

    /**
     * --- POST /api/projects/{projectId}/tasks (400) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn409_whenDuplicateTitleInProject() throws Exception {
        var req = new TaskCreateForProjectRequestDTO("Clash", "d", null, null, null);
        given(service.createForProject(eq(30L), any(TaskCreateForProjectRequestDTO.class)))
                .willThrow(new BusinessRuleException("Task title already exists in this project"));

        mvc.perform(post("/api/projects/30/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message", containsString("already exists")));

        verify(service).createForProject(eq(30L), any(TaskCreateForProjectRequestDTO.class));
    }

    /**
     * --- GET /api/tasks/{id} (200) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldGetTaskById() throws Exception {
        given(service.getById(5L)).willReturn(task(5L, "X", TaskStatus.DOING, TaskPriority.HIGH, 40L, 3L));

        mvc.perform(get("/api/tasks/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("X"))
                .andExpect(jsonPath("$.status").value("DOING"))
                .andExpect(jsonPath("$.project.id").value(40));

        verify(service).getById(5L);
    }

    /**
     * --- GET /api/tasks/{id} (404) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn404_whenTaskNotFound() throws Exception {
        given(service.getById(99L)).willThrow(new NotFoundException("Task not found: 99"));

        mvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message", containsString("Task not found")));

        verify(service).getById(99L);
    }

    /**
     * --- GET /api/tasks (200) with filters and pagination ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldListTasks_withFiltersAndPagination() throws Exception {
        List<TaskResponseDTO> content = List.of(
                task(3L, "A", TaskStatus.DONE, TaskPriority.LOW, 40L, 4L),
                task(1L, "B", TaskStatus.DONE, TaskPriority.LOW, 40L, 4L));
        Page<TaskResponseDTO> page = new PageImpl<>(content, PageRequest.of(0, 2), 2);
        given(service.list(0, 2, TaskStatus.DONE, TaskPriority.LOW, 40L)).willReturn(page);

        mvc.perform(get("/api/tasks")
                .param("page", "0")
                .param("size", "2")
                .param("status", "DONE")
                .param("priority", "LOW")
                .param("projectId", "40"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(3))
                .andExpect(jsonPath("$.content[1].id").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(service).list(0, 2, TaskStatus.DONE, TaskPriority.LOW, 40L);
    }

    /**
     * --- PUT /api/tasks/{id} (200) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldUpdateTask() throws Exception {
        var req = new TaskUpdateRequestDTO("New", "nd", TaskStatus.DOING, TaskPriority.HIGH, LocalDate.of(2025, 1, 1),
                51L);
        var res = task(12L, "New", TaskStatus.DOING, TaskPriority.HIGH, 51L, 5L);

        given(service.update(eq(12L), any(TaskUpdateRequestDTO.class))).willReturn(res);

        mvc.perform(put("/api/tasks/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.title").value("New"))
                .andExpect(jsonPath("$.status").value("DOING"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.project.id").value(51));

        verify(service).update(eq(12L), any(TaskUpdateRequestDTO.class));
    }

    /**
     * --- PUT /api/tasks/{id} (404) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn404_whenUpdateTaskMissing() throws Exception {
        var req = new TaskUpdateRequestDTO("X", "d", TaskStatus.TODO, TaskPriority.MEDIUM, null, 1L);
        given(service.update(eq(77L), any(TaskUpdateRequestDTO.class)))
                .willThrow(new NotFoundException("Task not found: 77"));

        mvc.perform(put("/api/tasks/77")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Task not found")));

        verify(service).update(eq(77L), any(TaskUpdateRequestDTO.class));
    }

    /**
     * --- PUT /api/tasks/{id} (409) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn409_whenUpdateDuplicateTitleInProject() throws Exception {
        var req = new TaskUpdateRequestDTO("Clash", "d", TaskStatus.DOING, TaskPriority.HIGH, null, 80L);
        given(service.update(eq(15L), any(TaskUpdateRequestDTO.class)))
                .willThrow(new BusinessRuleException("Task title already exists in this project"));

        mvc.perform(put("/api/tasks/15")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message", containsString("already exists")));

        verify(service).update(eq(15L), any(TaskUpdateRequestDTO.class));
    }

    /**
     * --- DELETE /api/tasks/{id} (204) ---
     * 
     * @throws Exception
     */
    @Test
    void shouldDeleteTask() throws Exception {
        mvc.perform(delete("/api/tasks/20"))
                .andExpect(status().isNoContent());

        verify(service).delete(20L);
    }

    /**
     * --- DELETE /api/tasks/{id} (404) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn404_whenDeleteTaskMissing() throws Exception {
        Mockito.doThrow(new NotFoundException("Task not found: 30")).when(service).delete(30L);

        mvc.perform(delete("/api/tasks/30"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Task not found")));

        verify(service).delete(30L);
    }
}
