package com.example.documentverification;

import java.util.List;
import java.util.ArrayList;

// Represents the request we send to OpenRouter
public class OpenRouterRequest {
    public String model = "meta-llama/llama-3.3-70b-instruct:free"; // A fast and capable model
    public List<Message> messages = new ArrayList<>();

    public static class Message {
        public String role;
        public String content;
    }
}