package com.example.documentverification;


public class MaskingUtil {

    public static String maskAadhar(String aadhar) {
        if (aadhar.length() == 12) return "XXXX-XXXX-" + aadhar.substring(8);
        return aadhar;
    }

    public static String maskPAN(String pan) {
        if (pan.length() == 10) return pan.substring(0, 5) + "XXXXX";
        return pan;
    }
}
