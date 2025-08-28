package com.example.documentverification.manual;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_document_manual")
public class UserDocumentManual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // reference to the user uploading the doc

    private String fullName;
    private String dob;
    private String address;
    private String idNumber; // aadhar / PAN number
    private String phoneNumber;
    private String email;

    private String status = "pending"; 
    
    // default pending, admin can approve/reject
    
    private String simEligibilityMessage;

    public String getSimEligibilityMessage() {
        return simEligibilityMessage;
    }

    public void setSimEligibilityMessage(String simEligibilityMessage) {
        this.simEligibilityMessage = simEligibilityMessage;
    }

    public UserDocumentManual() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

//    public String getSimPlan() { return simPlan; }
//    public void setSimPlan(String simPlan) { this.simPlan = simPlan; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
