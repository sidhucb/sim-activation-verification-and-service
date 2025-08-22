package com.example.documentverification;

import java.util.ArrayList;
import java.util.List;

public class OpenRouterRequest {

    private String model;
    private List<Message> messages;

    public OpenRouterRequest(String model) {
        this.model = model != null ? model : "meta-llama/llama-3.3-70b-instruct:free";
        this.messages = new ArrayList<>();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(String role, String content) {
        this.messages.add(new Message(role, content));
    }

    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}
