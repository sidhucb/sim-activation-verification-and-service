package simapp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "sim_status")
public class SimRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;
    private String username;
    private String email;

    @Column(name = "simstatus")
    private String status;

    private String phoneNumber;
    private String selectedNumber;
    private String fourDigits;

    /**
     * Stores the exact timestamp when the status changes to 'Provisioning'.
     */
    private Instant provisionedAt;

    // Default constructor
    public SimRequest() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSelectedNumber() {
        return selectedNumber;
    }

    public void setSelectedNumber(String selectedNumber) {
        this.selectedNumber = selectedNumber;
    }

    public String getFourDigits() {
        return fourDigits;
    }

    public void setFourDigits(String fourDigits) {
        this.fourDigits = fourDigits;
    }

    public Instant getProvisionedAt() {
        return provisionedAt;
    }

    public void setProvisionedAt(Instant provisionedAt) {
        this.provisionedAt = provisionedAt;
    }

    // Helper method: check if request is incom
}