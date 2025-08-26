package com.example.documentverification;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Make sure this is imported

@SpringBootApplication
@Configuration
@EnableDiscoveryClient
public class DocumentVerificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentVerificationServiceApplication.class, args);
    }

    /**
     * Configures and provides a RestTemplate bean for making HTTP requests.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * Configures and provides an ObjectMapper bean for JSON serialization/deserialization.
     * Explicitly registers JavaTimeModule to handle java.time.LocalDate.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // <-- This line is CRITICAL
        return objectMapper; 
    }
}
