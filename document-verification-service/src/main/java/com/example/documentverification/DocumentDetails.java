package com.example.documentverification;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class DocumentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardType;
    private String name;
    private LocalDate dob;
    private String gender;
    private String address;
    private Integer age;
    private String status = "pending"; // Default status
    private String cardNumber; // fixed naming
    
    

    // If you want to mask cardNumber later, you can still override this setter
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    // Optional: Add a helper method to set dob from a String if OCR extracts it as text
    public void setDobFromString(String dobString) {
        try {
            this.dob = LocalDate.parse(dobString); // expects yyyy-MM-dd format
        } catch (Exception e) {
            this.dob = null; // or handle gracefully
        }
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCardNumber() {
		return cardNumber;
	}
    
}
