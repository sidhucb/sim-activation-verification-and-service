package com.example.documentverification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;

import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class LlmService {

    @Value("${openrouter.api-key}")
    private String openRouterApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LlmService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<ExtractedAadharData> extractDataWithLlm(String documentText) {
        return CompletableFuture.supplyAsync(() -> {
            String apiUrl = "https://openrouter.ai/api/v1/chat/completions";

            // IMPROVED PROMPT: Emphasize complete address, and confirm DOB format
            String prompt = "Given the following document text, extract the person's full name, " +
                            "date of birth (DOB) strictly in dd/MM/yyyy format, gender (Male/Female/Other), " +
                            "the complete full address (including multiple lines if present), " + // More explicit for address
                            "and the card number. If any field is not found or cannot be confidently extracted " +
                            "(especially DOB not in dd/MM/yyyy format), return 'null' for that specific field. " +
                            "Format the output as a JSON object with keys: " +
                            "name, dob, gender, address, cardNumber. " +
                            "Document text: \n" + documentText;

            OpenRouterRequest request = new OpenRouterRequest();
            request.setModel("openai/gpt-3.5-turbo"); 
            request.addMessage(new Message("user", prompt)); 
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openRouterApiKey);

            HttpEntity<OpenRouterRequest> entity = new HttpEntity<>(request, headers);

            try {
                System.out.println("Sending LLM Request: " + objectMapper.writeValueAsString(request)); 
                OpenRouterResponse response = restTemplate.postForObject(apiUrl, entity, OpenRouterResponse.class);
                
                if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                    String llmOutput = response.getChoices().get(0).getMessage().getContent();
                    System.out.println("LLM Raw Output: " + llmOutput); // CRITICAL: Check this for address content

                    return parseLlmOutput(llmOutput);
                } else {
                    System.err.println("LLM API returned an empty or unexpected response structure.");
                    if (response != null) {
                        System.err.println("Full LLM Response (if not empty): " + objectMapper.writeValueAsString(response));
                    }
                    throw new RuntimeException("LLM API returned no valid choices.");
                }
            } catch (HttpClientErrorException.Unauthorized e) {
                System.err.println("OpenRouter API Key Unauthorized: Check your API key. " + e.getResponseBodyAsString());
                throw new RuntimeException("LLM API Unauthorized. Please check your API key.", e);
            } catch (RestClientException e) {
                System.err.println("Error calling OpenRouter API (RestClientException): " + e.getMessage());
                System.err.println("Response body (if available): " + ((e instanceof HttpClientErrorException) ? ((HttpClientErrorException)e).getResponseBodyAsString() : "N/A"));
                throw new RuntimeException("Failed to call LLM API.", e);
            } catch (JsonProcessingException e) {
                System.err.println("Error processing JSON for LLM request/response: " + e.getMessage());
                throw new RuntimeException("Failed to process LLM JSON.", e);
            } catch (Exception e) {
                System.err.println("An unexpected error occurred during LLM API call: " + e.getMessage());
                throw new RuntimeException("Failed to call LLM API.", e);
            }
        });
    }

    private ExtractedAadharData parseLlmOutput(String llmOutput) {
        try {
            ExtractedAadharData data = objectMapper.readValue(llmOutput, ExtractedAadharData.class);
            return data;
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing LLM JSON output to ExtractedAadharData: " + e.getMessage());
            System.err.println("Attempting simple field extraction from: " + llmOutput);
            return simpleFieldExtraction(llmOutput);
        }
    }

    private ExtractedAadharData simpleFieldExtraction(String llmOutput) {
        ExtractedAadharData data = new ExtractedAadharData();
        data.setName(extractField(llmOutput, "\"name\"\\s*:\\s*\"([^\"]*)\""));
        data.setDob(extractField(llmOutput, "\"dob\"\\s*:\\s*\"([^\"]*)\""));
        data.setGender(extractField(llmOutput, "\"gender\"\\s*:\\s*\"([^\"]*)\""));
        // IMPROVED REGEX for address: try to capture more broadly if JSON parsing fails and it's free-form
        data.setAddress(extractField(llmOutput, "\"address\"\\s*:\\s*\"([\\s\\S]*?)(?=\",|\"$|$)")); // Capture multi-line up to next field or end
        data.setCardNumber(extractField(llmOutput, "\"cardNumber\"\\s*:\\s*\"([^\"]*)\""));
        return data;
    }

    private String extractField(String text, String regex) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String value = matcher.group(1).trim();
            // Replace newlines with spaces for single-line display if multiple lines were captured
            value = value.replace("\n", " ").replace("\r", " ");
            return "null".equalsIgnoreCase(value) || value.isEmpty() ? null : value;
        }
        return null;
    }
}
