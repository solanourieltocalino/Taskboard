package com.jbk.taskboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbk.taskboard.dto.user.AppUserResponseDTO;
import com.jbk.taskboard.dto.project.*;
import com.jbk.taskboard.exception.ApiExceptionHandler;
import com.jbk.taskboard.exception.BusinessRuleException;
import com.jbk.taskboard.exception.NotFoundException;
import com.jbk.taskboard.service.ProjectService;
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

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web slice test for ProjectController.
 * Mocks ProjectService and imports ApiExceptionHandler for exception handling.
 * Uses MockMvc to perform HTTP requests and verify responses.
 */
@WebMvcTest(controllers = ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;

    @SuppressWarnings("removal")
    @MockBean
    private ProjectService service;

    /**
     * --- POST /api/projects (201) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldCreateProject_andReturn201WithLocation() throws Exception {
        var req = new ProjectRequestDTO("Alpha", "Desc", 1L);
        var owner = AppUserResponseDTO.of(1L, "Alice", "alice@mail.com");
        var res = ProjectResponseDTO.of(10L, "Alpha", "Desc", owner);

        given(service.create(any(ProjectRequestDTO.class))).willReturn(res);

        mvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/projects/10")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Alpha"))
                .andExpect(jsonPath("$.owner.id").value(1));

        verify(service).create(any(ProjectRequestDTO.class));
    }

    /**
     * --- POST /api/projects (400) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn400_whenCreateBodyInvalid() throws Exception {
        var bad = new ProjectRequestDTO("  ", "D", 1L); // name blank -> @NotBlank

        mvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation errors"))
                .andExpect(jsonPath("$.messages.name").exists());

        Mockito.verifyNoInteractions(service);
    }

    /**
     * --- GET /api/projects/{id} (200) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldGetProjectById() throws Exception {
        var owner = AppUserResponseDTO.of(2L, "Bob", "bob@mail.com");
        given(service.getById(5L)).willReturn(ProjectResponseDTO.of(5L, "P", "D", owner));

        mvc.perform(get("/api/projects/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("P"))
                .andExpect(jsonPath("$.owner.id").value(2));

        verify(service).getById(5L);
    }

    /**
     * --- GET /api/projects/{id} (404) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn404_whenProjectNotFound() throws Exception {
        given(service.getById(99L)).willThrow(new NotFoundException("Project not found: 99"));

        mvc.perform(get("/api/projects/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message", containsString("Project not found")));

        verify(service).getById(99L);
    }

    /**
     * --- GET /api/projects?page&size (200) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldListProjects_withPagination() throws Exception {
        var owner = AppUserResponseDTO.of(3L, "Carol", "carol@mail.com");
        List<ProjectResponseDTO> content = List.of(
                ProjectResponseDTO.of(3L, "A", "a", owner),
                ProjectResponseDTO.of(1L, "B", "b", owner));
        Page<ProjectResponseDTO> page = new PageImpl<>(content, PageRequest.of(0, 2), 2);
        given(service.list(0, 2)).willReturn(page);

        mvc.perform(get("/api/projects").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(3))
                .andExpect(jsonPath("$.content[1].id").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(service).list(0, 2);
    }

    /**
     * --- PUT /api/projects/{id} (200) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldUpdateProject() throws Exception {
        var req = new ProjectRequestDTO("New", "D2", 7L);
        var owner = AppUserResponseDTO.of(7L, "Frank", "frank@mail.com");
        var res = ProjectResponseDTO.of(12L, "New", "D2", owner);

        given(service.update(eq(12L), any(ProjectRequestDTO.class))).willReturn(res);

        mvc.perform(put("/api/projects/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.owner.id").value(7));

        verify(service).update(eq(12L), any(ProjectRequestDTO.class));
    }

    /**
     * --- PUT /api/projects/{id} (404) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn404_whenUpdateProjectMissing() throws Exception {
        var req = new ProjectRequestDTO("X", "D", 1L);
        given(service.update(eq(77L), any(ProjectRequestDTO.class)))
                .willThrow(new NotFoundException("Project not found: 77"));

        mvc.perform(put("/api/projects/77")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Project not found")));

        verify(service).update(eq(77L), any(ProjectRequestDTO.class));
    }

    /**
     * --- PUT /api/projects/{id} (409) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn409_whenDuplicateNamePerOwner() throws Exception {
        var req = new ProjectRequestDTO("Alpha", "D", 10L);
        given(service.update(eq(15L), any(ProjectRequestDTO.class)))
                .willThrow(new BusinessRuleException("Project name already exists for this owner"));

        mvc.perform(put("/api/projects/15")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message", containsString("already exists")));

        verify(service).update(eq(15L), any(ProjectRequestDTO.class));
    }

    /**
     * --- DELETE /api/projects/{id} (204) ---
     * 
     * @throws Exception
     */
    @Test
    void shouldDeleteProject() throws Exception {
        mvc.perform(delete("/api/projects/20"))
                .andExpect(status().isNoContent());

        verify(service).delete(20L);
    }

    /**
     * --- DELETE /api/projects/{id} (404) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn404_whenDeleteProjectMissing() throws Exception {
        Mockito.doThrow(new NotFoundException("Project not found: 30")).when(service).delete(30L);

        mvc.perform(delete("/api/projects/30"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Project not found")));

        verify(service).delete(30L);
    }
}
