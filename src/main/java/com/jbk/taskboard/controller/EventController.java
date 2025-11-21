package com.jbk.taskboard.controller;

import com.jbk.taskboard.dto.event.EventRequestDTO;
import com.jbk.taskboard.service.EventWebhookService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling event-related requests.
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    private final EventWebhookService eventWebhookService;

    public EventController(EventWebhookService eventWebhookService) {
        this.eventWebhookService = eventWebhookService;
    }

    /**
     * POST endpoint - Sends a webhook event.
     * Validates the request body and returns 200 OK on success.
     * 
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> createEvent(@Valid @RequestBody EventRequestDTO request) {
        log.info("[POST] /api/events - Sending webhook event with message='{}', source={} and type={}",
                request.message(), request.source(), request.type());
        eventWebhookService.sendWebhookEvent(request);
        return ResponseEntity.ok().build();
    }
}
