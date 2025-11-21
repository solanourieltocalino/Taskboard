package com.jbk.taskboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbk.taskboard.dto.event.EventRequestDTO;
import com.jbk.taskboard.exception.ApiExceptionHandler;
import com.jbk.taskboard.exception.WebhookClientException;
import com.jbk.taskboard.service.EventWebhookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web slice test for EventController.
 * Mocks EventWebhookService and imports ApiExceptionHandler for exception handling.
 * Uses MockMvc to perform HTTP requests and verify responses.
 */
@WebMvcTest(controllers = EventController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class EventControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;

    @SuppressWarnings("removal")
    @MockBean
    private EventWebhookService eventWebhookService;

    /**
     * --- POST /api/events (200) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldSendEvent_andReturn200() throws Exception {
        var req = new EventRequestDTO("hello", "app", "INFO");

        mvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("")); // no body

        verify(eventWebhookService).sendWebhookEvent(any(EventRequestDTO.class));
    }

    /**
     * --- POST /api/events (400) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn400_whenMessageBlank() throws Exception {
        var bad = new EventRequestDTO("   ", "app", "INFO");

        mvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation errors"))
                .andExpect(jsonPath("$.messages.message").exists());

        Mockito.verifyNoInteractions(eventWebhookService);
    }

    /**
     * --- POST /api/events (502) ---
     * 
     * @throws Exception
     */
    @SuppressWarnings("null")
    @Test
    void shouldReturn502_whenServiceThrowsWebhookClientException() throws Exception {
        var req = new EventRequestDTO("boom", "app", "ERR");
        Mockito.doThrow(new WebhookClientException("Failed to send webhook event"))
                .when(eventWebhookService).sendWebhookEvent(any(EventRequestDTO.class));

        mvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.error").value("Bad Gateway"))
                .andExpect(jsonPath("$.message", containsString("Failed to send")));

        verify(eventWebhookService).sendWebhookEvent(any(EventRequestDTO.class));
    }
}
