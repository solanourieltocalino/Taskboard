package com.jbk.taskboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for WebClient beans.
 */
@Configuration
public class WebClientConfig {

    /**
     * Creates a WebClient bean configured for sending webhook events.
     * 
     * @param webhookEventUrl
     * @return
     */
    @SuppressWarnings("null")
    @Bean
    public WebClient webhookWebClient(@Value("${webhook.event.url}") String webhookEventUrl) {

        return WebClient.builder()
                .baseUrl(webhookEventUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
