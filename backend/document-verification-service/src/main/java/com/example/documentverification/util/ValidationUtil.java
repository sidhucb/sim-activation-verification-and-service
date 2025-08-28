package com.example.documentverification.util;

import java.time.LocalDate;
import java.time.Period;

public class ValidationUtil {

    public static boolean isAdult(LocalDate dob) {
        if (dob == null) return false;
        return Period.between(dob, LocalDate.now()).getYears() >= 18;
    }

    public static boolean isValidAadharFormat(String aadhar) {
        return aadhar != null && aadhar.matches("\\d{12}") && VerhoeffAlgorithm.validateVerhoeff(aadhar);
    }

    public static boolean isValidPan(String pan) {
        return pan != null && pan.matches("[A-Z]{5}[0-9]{4}[A-Z]");
    }
}
