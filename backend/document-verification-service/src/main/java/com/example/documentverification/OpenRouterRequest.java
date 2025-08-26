package com.example.documentverification;

import java.util.ArrayList;
import java.util.List;

public class OpenRouterRequest {
    private String model;
    private List<Message> messages = new ArrayList<>();

    public OpenRouterRequest() {
        // Default constructor
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

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }
}