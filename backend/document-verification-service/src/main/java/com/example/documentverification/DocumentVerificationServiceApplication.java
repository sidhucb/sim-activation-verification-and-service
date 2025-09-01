package com.example.documentverification;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootApplication
@EnableDiscoveryClient
public class DocumentVerificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentVerificationServiceApplication.class, args);
    }

    /**
     * Configures and provides an ObjectMapper bean for JSON serialization/deserialization.
     * Explicitly registers JavaTimeModule to handle java.time.LocalDate.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper; 
    }
}
