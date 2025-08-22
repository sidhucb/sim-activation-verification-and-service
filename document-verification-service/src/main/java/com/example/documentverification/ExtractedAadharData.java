package com.example.documentverification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Represents the clean, structured data extracted by the LLM
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtractedAadharData {
    private String name;
    private String dob;
    private String gender;
    private String address;

    // Standard Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}