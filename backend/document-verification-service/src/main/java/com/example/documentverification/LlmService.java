package com.example.documentverification;

import com.example.documentverification.ExtractedCardData;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LLMService {

    private final WebClient webClient;

    @Value("${openai.api-key}")
    private String openAiApiKey;

    public LLMService(WebClient.Builder webClientBuilder) {
    	
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
    }

    @CircuitBreaker(name = "llmService", fallbackMethod = "fallbackExtractDetails")
    public ExtractedCardData extractDetailsFromOCR(String ocrText, String openApiKey) {
        String prompt = "Extract the following details from this OCR text: "
                + "name, dob (yyyy-MM-dd), gender, address, card number.\n\nText:\n" + ocrText;

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue("""
                        {
                          "model": "gpt-4o-mini",
                          "messages": [{"role": "user", "content": "%s"}]
                        }
                        """.formatted(prompt))
                .retrieve()
                .bodyToMono(String.class)
                .map(this::mapResponseToExtractedData)
                .onErrorResume(e -> Mono.error(new RuntimeException("LLM request failed: " + e.getMessage())))
                .block();
    }

    private ExtractedCardData mapResponseToExtractedData(String response) {
        // TODO: Parse the OpenAI response JSON to ExtractedCardData
        // Example with minimal parsing — implement JSON parsing logic
        ExtractedCardData data = new ExtractedCardData();
        data.setName(null);
        data.setDob(null);
        data.setGender(null);
        data.setAddress(null);
        data.setCardNumber(null);
        return data;
    }

    private ExtractedCardData fallbackExtractDetails(String ocrText, Throwable throwable) {
        System.err.println("LLM Circuit Breaker activated: " + throwable.getMessage());
        return null; // No dummy data — returns null so processDocument() can mark as failed
    }
}
