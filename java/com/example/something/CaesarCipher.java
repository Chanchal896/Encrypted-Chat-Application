package com.example.something;

public class CaesarCipher {
    private static final int SHIFT = 3;

    public static String encrypt(String input) {
        return shift(input, SHIFT);
    }
    public static String decrypt(String input) {
        return shift(input, 26 - SHIFT);
    }
    private static String shift(String input, int shift) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                result.append((char) ((c - 'a' + shift) % 26 + 'a'));
            } else if (c >= 'A' && c <= 'Z') {
                result.append((char) ((c - 'A' + shift) % 26 + 'A'));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
