package com.jbk.taskboard.service;

import com.jbk.taskboard.dto.event.EventRequestDTO;

/**
 * Service interface for sending webhook events.
 */
public interface EventWebhookService {

    // Sends a webhook event with the given request data.
    void sendWebhookEvent(EventRequestDTO request);
}
