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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
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
@TestPropertySource(properties = {
        "spring.mvc.throw-exception-if-no-handler-found=true" // For NoHandlerFoundException
})
class ApiExceptionHandlerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

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

    /**
     * 400: ConstraintViolationException (invalid param value)
     * 
     * @throws Exception
     */
    @Test
    void shouldReturn400_whenConstraintViolation() throws Exception {
        mvc.perform(get("/dummy/constraint").param("size", "0")) // less than min 1
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation errors"))
                .andExpect(jsonPath("$.messages['constraint.size']").exists());
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

    /**
     * 400: IllegalArgumentException
     * 
     * @throws Exception
     */
    @Test
    void shouldReturn400_whenIllegalArgument() throws Exception {
        mvc.perform(get("/dummy/illegal-arg"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Bad arg"));
    }

    /**
     * 400: HttpMessageNotReadableException (malformed JSON)
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn400_whenMalformedJson() throws Exception {
        mvc.perform(post("/dummy/malformed-json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"test\",}")) // malformed JSON
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Malformed JSON request"));
    }

    /**
     * 400: MissingServletRequestParameterException
     * 
     * @throws Exception
     */
    @Test
    void shouldReturn400_whenMissingRequestParam() throws Exception {
        mvc.perform(get("/dummy/missing")) // missing ?q=
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Missing required parameter"))
                .andExpect(jsonPath("$.messages.q").value("is required"));
    }

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

    /**
     * 404: NoHandlerFoundException / NoResourceFoundException
     * 
     * @throws Exception
     */
    @Test
    void shouldReturn404_whenNoHandlerFound() throws Exception {
        mvc.perform(get("/no-such-endpoint")) // not found in DummyController
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Endpoint not found"));
    }

    /**
     * 405: HttpRequestMethodNotSupportedException
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn405_whenMethodNotAllowed() throws Exception {
        mvc.perform(post("/dummy/only-get"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"))
                .andExpect(jsonPath("$.message", containsString("HTTP method POST is not supported")));
    }

    /**
     * 406: HttpMediaTypeNotAcceptableException
     * 
     * @throws Exception
     */
    @Test
    void shouldReturn406_whenNotAcceptable() throws Exception {
        mvc.perform(get("/dummy/produce-json").accept(MediaType.TEXT_PLAIN)) // only produces JSON
                .andExpect(status().isNotAcceptable()); // no body expected because it can be empty
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

    /**
     * 409: DataIntegrityViolationException
     * 
     * @throws Exception
     */
    @Test
    void shouldReturn409_whenDataIntegrityViolation() throws Exception {
        mvc.perform(get("/dummy/data-integrity"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Data integrity violation"));
    }

    /**
     * 415: HttpMediaTypeNotSupportedException
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn415_whenUnsupportedMediaType() throws Exception {
        mvc.perform(post("/dummy/malformed-json")
                .contentType(MediaType.TEXT_PLAIN) // endpoint demands application/json
                .content("plain text"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.error").value("Unsupported Media Type"))
                .andExpect(jsonPath("$.message", containsString("Unsupported media type")));
    }

    /**
     * 500: Generic Exception
     * 
     * @throws Exception
     */
    @Test
    void shouldReturn500_whenGenericException() throws Exception {
        mvc.perform(get("/dummy/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }
}
