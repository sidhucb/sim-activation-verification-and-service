package com.example.documentverification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Import this annotation

@JsonIgnoreProperties(ignoreUnknown = true) // This tells Jackson to ignore any unknown fields
public class Message {
    private String role;
    private String content;

    public Message() {
        // Default constructor for JSON deserialization
    }

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
