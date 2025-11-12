package com.jbk.taskboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbk.taskboard.dto.user.*;
import com.jbk.taskboard.exception.ApiExceptionHandler;
import com.jbk.taskboard.exception.BusinessRuleException;
import com.jbk.taskboard.exception.NotFoundException;
import com.jbk.taskboard.service.AppUserService;
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

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web slice test for AppUserController.
 * Mocks AppUserService and imports ApiExceptionHandler for exception handling.
 * Uses MockMvc to perform HTTP requests and verify responses.
 */
@WebMvcTest(controllers = AppUserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class AppUserControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;

    @SuppressWarnings("removal")
    @MockBean
    private AppUserService service;

    /**
     * --- POST /api/users (201) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldCreateUser_andReturn201WithLocation() throws Exception {
        AppUserRequestDTO req = new AppUserRequestDTO("Alice", "alice@mail.com");
        AppUserResponseDTO res = AppUserResponseDTO.of(10L, "Alice", "alice@mail.com");

        given(service.create(any(AppUserRequestDTO.class))).willReturn(res);

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/users/10")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@mail.com"));

        verify(service).create(any(AppUserRequestDTO.class));
    }

    /**
     * --- POST /api/users (400) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn400_whenCreateBodyInvalid() throws Exception {
        var bad = new AppUserRequestDTO("  ", "bad@mail.com");

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation errors"))
                .andExpect(jsonPath("$.messages.name").exists());

        Mockito.verifyNoInteractions(service);
    }

    /**
     * --- GET /api/users/{id} (200) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldGetUserById() throws Exception {
        given(service.getById(5L)).willReturn(AppUserResponseDTO.of(5L, "Bob", "bob@mail.com"));

        mvc.perform(get("/api/users/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@mail.com"));

        verify(service).getById(5L);
    }

    /**
     * --- GET /api/users/{id} (404) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn404_whenUserNotFound() throws Exception {
        given(service.getById(99L)).willThrow(new NotFoundException("User not found: 99"));

        mvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message", containsString("User not found")));

        verify(service).getById(99L);
    }

    /**
     * --- GET /api/users?page&size (200) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldListUsers_withPagination() throws Exception {
        List<AppUserResponseDTO> content = List.of(
                AppUserResponseDTO.of(2L, "A", "a@mail.com"),
                AppUserResponseDTO.of(1L, "B", "b@mail.com"));
        Page<AppUserResponseDTO> page = new PageImpl<>(content, PageRequest.of(0, 2), 2);
        given(service.list(0, 2)).willReturn(page);

        mvc.perform(get("/api/users").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[1].id").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(service).list(0, 2);
    }

    /**
     * --- PUT /api/users/{id} (200) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldUpdateUser() throws Exception {
        var req = new AppUserRequestDTO("Carol", "carol@mail.com");
        var res = AppUserResponseDTO.of(7L, "Carol", "carol@mail.com");
        given(service.update(eq(7L), any(AppUserRequestDTO.class))).willReturn(res);

        mvc.perform(put("/api/users/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Carol"));

        verify(service).update(eq(7L), any(AppUserRequestDTO.class));
    }

    /**
     * --- PUT /api/users/{id} (404) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn404_whenUpdateUserMissing() throws Exception {
        var req = new AppUserRequestDTO("X", "x@mail.com");
        given(service.update(eq(77L), any(AppUserRequestDTO.class)))
                .willThrow(new NotFoundException("User not found: 77"));

        mvc.perform(put("/api/users/77")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("User not found")));

        verify(service).update(eq(77L), any(AppUserRequestDTO.class));
    }

    /**
     * --- PUT /api/users/{id} (409) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn409_whenUpdateEmailConflict() throws Exception {
        var req = new AppUserRequestDTO("D", "dup@mail.com");
        given(service.update(eq(12L), any(AppUserRequestDTO.class)))
                .willThrow(new BusinessRuleException("Email already in use: dup@mail.com"));

        mvc.perform(put("/api/users/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message", containsString("Email already in use")));

        verify(service).update(eq(12L), any(AppUserRequestDTO.class));
    }

    /**
     * --- DELETE /api/users/{id} (204) ---
     * 
     * @throws Exception
     */
    @Test
    void shouldDeleteUser() throws Exception {
        mvc.perform(delete("/api/users/20"))
                .andExpect(status().isNoContent());

        verify(service).delete(20L);
    }

    /**
     * --- DELETE /api/users/{id} (404) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn404_whenDeleteUserMissing() throws Exception {
        Mockito.doThrow(new NotFoundException("User not found: 30")).when(service).delete(30L);

        mvc.perform(delete("/api/users/30"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("User not found")));

        verify(service).delete(30L);
    }
}
