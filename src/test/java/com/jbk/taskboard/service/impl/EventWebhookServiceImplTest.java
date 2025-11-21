package com.jbk.taskboard.service.impl;

import com.jbk.taskboard.dto.event.EventRequestDTO;
import com.jbk.taskboard.dto.event.WebhookEventPayloadDTO;
import com.jbk.taskboard.exception.WebhookClientException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for EventWebhookServiceImpl.
 * Mocks WebClient to simulate HTTP interactions.
 * Tests both successful event sending and exception handling.
 */
@ExtendWith(MockitoExtension.class)
class EventWebhookServiceImplTest {

    // --- Mocks and Service Under Test ---
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestBodyUriSpec requestSpec;
    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec headersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private EventWebhookServiceImpl service;

    /**
     * --- sendWebhookEvent() happy path ---
     */
    @SuppressWarnings({ "null", "unchecked" })
    @Test
    void shouldSendWebhookEvent_happyPath() {
        // chain: post() -> bodyValue(..) -> retrieve() -> toBodilessEntity() -> block()
        given(webClient.post()).willReturn(requestSpec);
        given(requestSpec.bodyValue(any(WebhookEventPayloadDTO.class))).willReturn(headersSpec);
        given(headersSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.toBodilessEntity()).willReturn(Mono.just(ResponseEntity.ok().build()));

        var req = new EventRequestDTO("hello", "app", "INFO");

        assertThatCode(() -> service.sendWebhookEvent(req)).doesNotThrowAnyException();

        Mockito.verify(webClient).post();
        Mockito.verify(requestSpec).bodyValue(any(WebhookEventPayloadDTO.class));
        Mockito.verify(headersSpec).retrieve();
        Mockito.verify(responseSpec).toBodilessEntity();
    }

    /**
     * --- sendWebhookEvent() exception path ---
     * 
     * @throws Exception
     */
    @SuppressWarnings({ "null", "unchecked" })
    @Test
    void shouldWrapExceptionIntoWebhookClientException() {
        given(webClient.post()).willReturn(requestSpec);
        given(requestSpec.bodyValue(any(WebhookEventPayloadDTO.class))).willReturn(headersSpec);
        // chain will throw at toBodilessEntity()
        given(headersSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.toBodilessEntity()).willThrow(new RuntimeException("http 500"));

        var req = new EventRequestDTO("boom", "app", "ERR");

        assertThatThrownBy(() -> service.sendWebhookEvent(req))
                .isInstanceOf(WebhookClientException.class)
                .hasMessageContaining("Failed to send webhook event")
                .hasCauseInstanceOf(RuntimeException.class);

        Mockito.verify(webClient).post();
        Mockito.verify(requestSpec).bodyValue(any(WebhookEventPayloadDTO.class));
        Mockito.verify(headersSpec).retrieve();
        Mockito.verify(responseSpec).toBodilessEntity();
    }
}
