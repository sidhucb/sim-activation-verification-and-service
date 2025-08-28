package com.example.documentverification.service;

import com.example.documentverification.model.KycData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AiParsingService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openrouter.api-key}")
    private String apiKey;

    public AiParsingService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.openrouter.ai").build();
    }

    /**
     * Sends the raw OCR text to OpenRouter AI and expects JSON with fields:
     * name, dob (yyyy-MM-dd), cardType, cardNumber
     *
     * This method returns KycData or null if parsing fails.
     */
    public KycData parseTextWithAi(String ocrText) {
        try {
            // NOTE: Replace endpoint & payload according to the exact OpenRouter spec you will use.
            String payload = "{\"text\": " + objectMapper.writeValueAsString(ocrText) + "}";

            String jsonResponse = webClient.post()
                    .uri("/v1/parse") // placeholder — change if OpenRouter uses different endpoint
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (jsonResponse == null) return null;

            JsonNode root = objectMapper.readTree(jsonResponse);
            // adapt these paths based on actual response
            String name = root.path("name").asText(null);
            String dob = root.path("dob").asText(null); // expect yyyy-MM-dd
            String cardType = root.path("cardType").asText(null);
            String cardNumber = root.path("cardNumber").asText(null);

            if (name == null || dob == null || cardType == null || cardNumber == null) return null;

            return new KycData(name, java.time.LocalDate.parse(dob), cardType, cardNumber);
        } catch (Exception e) {
            // parsing or API error — return null to signal failure
            return null;
        }
    }
}
