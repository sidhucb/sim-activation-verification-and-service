package com.example.documentverification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtractedAadharData {

    @NotBlank
    private String name;

    private String dob; // Could also use LocalDate if preferred
    private String gender;
    private String address;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return "ExtractedAadharData{" +
               "name='" + name + '\'' +
               ", dob='" + dob + '\'' +
               ", gender='" + gender + '\'' +
               ", address='" + address + '\'' +
               '}';
    }
}
