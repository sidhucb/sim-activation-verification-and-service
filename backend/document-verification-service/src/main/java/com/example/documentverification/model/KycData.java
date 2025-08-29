package com.example.documentverification.model;

import java.time.LocalDate;

public class KycData {
    private String name;
    private LocalDate dob;
    private String cardType;   // "AADHAAR", "PAN", "PASSPORT", "UNKNOWN"
    private String cardNumber;

    public KycData() {}

    public KycData(String name, LocalDate dob, String cardType, String cardNumber) {
        this.name = name;
        this.dob = dob;
        this.cardType = cardType;
        this.cardNumber = cardNumber;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
}
