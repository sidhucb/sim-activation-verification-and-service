package com.example.documentverification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;

@Configuration
public class OpenRouterConfig {

    @Value("${openrouter.api-key}") // Ensure this matches application.yml
    private String openRouterApiKey;

    @Bean
    public RestTemplate openRouterRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                HttpHeaders headers = request.getHeaders();
                headers.set("Authorization", "Bearer " + openRouterApiKey);
                headers.set("Content-Type", "application/json");
                return execution.execute(request, body);
            }
        });
        return restTemplate;
    }
}