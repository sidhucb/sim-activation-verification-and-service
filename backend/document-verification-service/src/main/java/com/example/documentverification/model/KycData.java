package com.example.documentverification.model;

import java.time.LocalDate;

public class KycData {
    private final String name;
    private final LocalDate dob;
    private final String cardType;
    private final String cardNumber;

    public KycData(String name, LocalDate dob, String cardType, String cardNumber) {
        this.name = name;
        this.dob = dob;
        this.cardType = cardType;
        this.cardNumber = cardNumber;
    }

    public String getName() { return name; }
    public LocalDate getDob() { return dob; }
    public String getCardType() { return cardType; }
    public String getCardNumber() { return cardNumber; }
}
