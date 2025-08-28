package simapp;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "allocated_numbers")
public class AllocatedNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    // Link the allocated number to the specific SIM request
    @Column(name = "request_id", unique = true, nullable = false)
    private String requestId;

    private Instant allocatedAt = Instant.now();

    // Default constructor
    public AllocatedNumber() {}

    // Parameterized constructor
    public AllocatedNumber(Long id, String phoneNumber, String requestId, Instant allocatedAt) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.requestId = requestId;
        this.allocatedAt = allocatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Instant getAllocatedAt() {
        return allocatedAt;
    }

    public void setAllocatedAt(Instant allocatedAt) {
        this.allocatedAt = allocatedAt;
    }

    // Optional: For debugging/logging
    @Override
    public String toString() {
        return "AllocatedNumber{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", requestId='" + requestId + '\'' +
                ", allocatedAt=" + allocatedAt +
                '}';
    }
}
