package com.example.documentverification;


public class ExtractedCardData {

    private String name;
    private String dob;       // e.g., "1995-08-28"
    private String gender;    // Male / Female / Others
    private String address;
    private Integer age;
    private String cardNumber; // PAN or Aadhaar

    public ExtractedCardData() {}

    public ExtractedCardData(String name, String dob, String gender, String address, Integer age, String cardNumber) {
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.age = age;
        this.cardNumber = cardNumber;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
}
