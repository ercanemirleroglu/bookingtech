package com.shameless.bookingtech.common.util;

public class StringUtil {
    public static String returnJustDigits(String str) {
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isDigit(c))
                digits.append(c);
        }
        return digits.toString();
    }
}
