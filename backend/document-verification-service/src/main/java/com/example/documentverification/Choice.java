package com.example.documentverification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Import this annotation

@JsonIgnoreProperties(ignoreUnknown = true) // This tells Jackson to ignore any unknown fields
public class Choice {
    private Message message;
    private String finish_reason;
    private int index;

    public Choice() {
        // Default constructor
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getFinish_reason() {
        return finish_reason;
    }

    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
