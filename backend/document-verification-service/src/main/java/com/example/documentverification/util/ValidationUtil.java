package com.example.documentverification.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern PAN_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]");
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("\\d{12}");

    public static boolean isValidPan(String pan) {
        return pan != null && PAN_PATTERN.matcher(pan).matches();
    }

    public static boolean isValidAadharFormat(String aadhar) {
        return aadhar != null && AADHAAR_PATTERN.matcher(aadhar).matches();
    }
}
