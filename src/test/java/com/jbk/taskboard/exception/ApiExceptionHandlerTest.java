package com.jbk.taskboard.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbk.taskboard.exception.DummyController.CreatePayload;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web slice test for ApiExceptionHandler.
 * It registers a DummyController to trigger exceptions handled by the advice.
 */
@ActiveProfiles("webmvc-test")
@WebMvcTest(controllers = DummyController.class)
@AutoConfigureMockMvc(addFilters = false) // Avoids security filters in this test
@Import(ApiExceptionHandler.class)
class ApiExceptionHandlerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    // ---- 404: NotFoundException ----
    @SuppressWarnings("null")
    @Test
    void shouldReturn404_whenNotFoundException() throws Exception {
        mvc.perform(get("/dummy/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("User not found")); // message sent by the dummy
    }

    // ---- 400: MethodArgumentNotValidException (invalid body) ----
    @SuppressWarnings("null")
    @Test
    void shouldReturn400_whenBodyValidationFails() throws Exception {
        var payload = new CreatePayload(""); // blank name -> @NotBlank
        mvc.perform(post("/dummy/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation errors"))
                .andExpect(jsonPath("$.messages.name").exists());
    }

    // ---- 400: MethodArgumentTypeMismatchException (invalid param type) ----
    @SuppressWarnings("null")
    @Test
    void shouldReturn400_whenTypeMismatchOnQueryParam() throws Exception {
        mvc.perform(get("/dummy/type-mismatch").param("size", "abc")) // must be int
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.messages.size").exists());
    }

    // ---- 409: BusinessRuleException ----
    @SuppressWarnings("null")
    @Test
    void shouldReturn409_whenBusinessRuleException() throws Exception {
        mvc.perform(get("/dummy/conflict"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Duplicate email"));
    }
}
