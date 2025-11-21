package com.jbk.taskboard.service.impl;

import com.jbk.taskboard.dto.event.EventRequestDTO;
import com.jbk.taskboard.dto.event.WebhookEventPayloadDTO;
import com.jbk.taskboard.exception.WebhookClientException;
import com.jbk.taskboard.service.EventWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Implementation of the EventWebhookService that sends webhook events to an
 * external endpoint.
 */
@Service
public class EventWebhookServiceImpl implements EventWebhookService {

    private static final Logger log = LoggerFactory.getLogger(EventWebhookServiceImpl.class);
    private final WebClient webhookWebClient;

    public EventWebhookServiceImpl(WebClient webhookWebClient) {
        this.webhookWebClient = webhookWebClient;
    }

    /**
     * Sends a webhook event to an external endpoint.
     * 
     * @param request the event request data
     * @throws WebhookClientException if there is an error sending the webhook event
     */
    @SuppressWarnings("null")
    @Override
    public void sendWebhookEvent(EventRequestDTO request) {
        WebhookEventPayloadDTO payload = buildPayload(request);
        try {
            webhookWebClient.post()
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(5)); // 5 seconds timeout

            log.info("Webhook event sent successfully to external endpoint");
        } catch (Exception ex) {
            log.error("Error sending webhook event to external endpoint", ex);
            throw new WebhookClientException("Failed to send webhook event", ex);
        }
    }

    /**
     * Builds the webhook event payload from the event request.
     * 
     * @param request
     * @return
     */
    private WebhookEventPayloadDTO buildPayload(EventRequestDTO request) {
        String id = UUID.randomUUID().toString();
        String source = (request.source() != null && !request.source().isBlank())
                ? request.source()
                : "taskboard-api";
        String type = (request.type() != null && !request.type().isBlank())
                ? request.type()
                : "CUSTOM_EVENT";

        return new WebhookEventPayloadDTO(
                id,
                source,
                type,
                request.message(),
                OffsetDateTime.now(ZoneOffset.UTC));
    }
}
