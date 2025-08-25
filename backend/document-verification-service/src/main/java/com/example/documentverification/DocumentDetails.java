package com.example.documentverification;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime; // NEW: Import for preparedAt

@Entity
public class DocumentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Explicitly named for clarity
    private Long id;

    @Column(name = "card_type") // Keeps existing snake_case for card type
    private String cardType;

    @Column(name = "name") // Explicitly named 'name'
    private String name;

    @Column(name = "dob") // Explicitly named 'dob'
    private LocalDate dob;

    @Column(name = "gender") // Explicitly named 'gender'
    private String gender;

    @Column(name = "address") // Explicitly named 'address'
    private String address;

    @Column(name = "age") // Explicitly named 'age'
    private Integer age;

    @Column(name = "card_number") // Explicitly named 'card_number'
    private String cardNumber;

    @Column(name = "status") // Explicitly named 'status'
    private String status; 
    
    @Column(name = "sim_eligibility_message") // Explicitly named 'sim_eligibility_message'
    private String simEligibilityMessage;

    @Column(name = "prepared_at") // NEW: Field for timestamp, explicitly named
    private LocalDateTime preparedAt;

    // Default constructor
    public DocumentDetails() {
    }

    // Getters and Setters (updated to standard camelCase method names)
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

    public String getName() { // Standard getter for 'name'
        return name;
    }

    public void setName(String name) { // Standard setter for 'name'
        this.name = name;
    }

    public LocalDate getDob() { // Standard getter for 'dob'
        return dob;
    }

    public void setDob(LocalDate dob) { // Standard setter for 'dob'
        this.dob = dob;
    }

    public String getGender() { // Standard getter for 'gender'
        return gender;
    }

    public void setGender(String gender) { // Standard setter for 'gender'
        this.gender = gender;
    }

    public String getAddress() { // Standard getter for 'address'
        return address;
    }

    public void setAddress(String address) { // Standard setter for 'address'
        this.address = address;
    }

    public Integer getAge() { // Standard getter for 'age'
        return age;
    }

    public void setAge(Integer age) { // Standard setter for 'age'
        this.age = age;
    }

    public String getCardNumber() { // Standard getter for 'cardNumber'
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) { // Standard setter for 'cardNumber'
        this.cardNumber = cardNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSimEligibilityMessage() {
        return simEligibilityMessage;
    }

    public void setSimEligibilityMessage(String simEligibilityMessage) {
        this.simEligibilityMessage = simEligibilityMessage;
    }

    // NEW: Getter and Setter for preparedAt
    public LocalDateTime getPreparedAt() {
        return preparedAt;
    }

    public void setPreparedAt(LocalDateTime preparedAt) {
        this.preparedAt = preparedAt;
    }
}
