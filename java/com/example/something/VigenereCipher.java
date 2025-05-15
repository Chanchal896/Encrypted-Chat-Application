package com.example.something;

public class VigenereCipher {
    private static final String KEY = "Vigenere";

    public static String encrypt(String input) {
        return process(input, KEY, true);
    }

    public static String decrypt(String input) {
        return process(input, KEY, false);
    }

    private static String process(String input, String key, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        key = key.toLowerCase();
        int keyLen = key.length();
        int j = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c)) {
                int base = Character.isLowerCase(c) ? 'a' : 'A';
                int shift = key.charAt(j % keyLen) - 'a';
                if (!encrypt) {
                    shift = 26 - shift;
                }
                char ch = (char) ((c - base + shift) % 26 + base);
                result.append(ch);
                j++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
