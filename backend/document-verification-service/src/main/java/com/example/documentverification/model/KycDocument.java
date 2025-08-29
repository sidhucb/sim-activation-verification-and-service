package com.example.documentverification.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_documents")
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;            // store user email or id from JWT
    private String name;
    private LocalDate dob;
    private String cardType;
    private String cardNumber;          // full number (stored securely if needed)
    public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
// store masked number only
    private String status;            // APPROVED, PENDING_MANUAL, APPROVED_MANUAL, REJECTED
    private String remarks;           // e.g. OCR failure reason

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public KycDocument() {}

    // Getters & setters (generate or use Lombok). Example for a few fields:
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
