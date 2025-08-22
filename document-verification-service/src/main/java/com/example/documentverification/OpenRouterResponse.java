package com.example.documentverification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

// Represents the response we get from OpenRouter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenRouterResponse {
    public List<Choice> choices;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        public Message message;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        public String content;
    }
}