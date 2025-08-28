package com.example.documentverification.util;

public class MaskingUtil {

    public static String maskAadhar(String aadhar) {
        if (aadhar == null || aadhar.length() != 12) return aadhar;
        return "********" + aadhar.substring(8); // first 8 masked, last 4 visible
    }

    public static String maskPan(String pan) {
        if (pan == null || pan.length() != 10) return pan;
        return "******" + pan.substring(6); // last 4 visible
    }
}
