package com.jbk.taskboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for CORS settings.
 */
@Configuration
public class CorsConfig {

    // Load the allowed origin from application.properties
    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    /**
     * Creates a WebMvcConfigurer bean to configure CORS settings.
     * 
     * @return a WebMvcConfigurer with CORS mappings configured
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(allowedOrigin)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}
