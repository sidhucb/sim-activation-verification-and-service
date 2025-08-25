package com.example.documentverification;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Import this annotation

@JsonIgnoreProperties(ignoreUnknown = true) // This tells Jackson to ignore any unknown fields
public class OpenRouterResponse {
    private List<Choice> choices;

    public OpenRouterResponse() {
        // Default constructor
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }
}
