package com.cardactivation;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sim_activation")
public class  CardActivationEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    private String fileName; // Uploaded ID file name
    
    @Lob
    @Column(name = "image_data", columnDefinition = "BYTEA")
    private byte[] imageData;

    private String filePath; // Uploaded file path

    private String extractedText; // OCR extracted or simulated text

    private String kycStatus;// Pending, Verified, Rejected
    
    private String aiValidation;

    private String simStatus; // Requested, Verified, Suspended

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
  
    public CardActivationEntity(String userName, String fileName, String filePath,byte[] imageData) {
        this.userName = userName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.kycStatus = "Pending";
        this.simStatus = "Requested";
        this.imageData = imageData;
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public String getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = kycStatus;
    }

    public String getSimStatus() {
        return simStatus;
    }

    public void setSimStatus(String simStatus) {
        this.simStatus = simStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


	public String getAiValidation() {
		return aiValidation;
	}


	public void setAiValidation(String aiValidation) {
		this.aiValidation = aiValidation;
	}


	public byte[] getImageData() {
		return imageData;
	}


	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}
}
