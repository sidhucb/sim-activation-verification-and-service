package com.example.documentverification.util;

public class MaskingUtil {

    public static String maskPan(String pan) {
        if (pan == null || pan.length() != 10) return pan;
        return pan.substring(0, 5) + "****" + pan.substring(9);
    }

    public static String maskAadhar(String aadhar) {
        if (aadhar == null || aadhar.length() != 12) return aadhar;
        return "**** **** " + aadhar.substring(8);
    }
}
