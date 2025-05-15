package com.example.something;

public class RailFenceCipher {

    private static final int RAILS = 5;

    public static String encrypt(String input) {
        return encrypt(input, RAILS);
    }

    public static String decrypt(String cipher) {
        return decrypt(cipher, RAILS);
    }

    private static String encrypt(String input, int rails) {
        if (rails <= 1) return input;
        StringBuilder[] fence = new StringBuilder[rails];
        for (int i = 0; i < rails; i++) {
            fence[i] = new StringBuilder();
        }
        int rail = 0;
        boolean down = true;
        for (char c : input.toCharArray()) {
            fence[rail].append(c);
            if (rail == 0) {
                down = true;
            } else if (rail == rails - 1) {
                down = false;
            }
            rail += down ? 1 : -1;
        }
        StringBuilder result = new StringBuilder();
        for (StringBuilder sb : fence) {
            result.append(sb);
        }
        return result.toString();
    }

    private static String decrypt(String cipher, int rails) {
        if (rails <= 1) return cipher;
        int length = cipher.length();
        int[] railCounts = new int[rails];
        int rail = 0;
        boolean down = true;
        // Count characters per rail.
        for (int i = 0; i < length; i++) {
            railCounts[rail]++;
            if (rail == 0) {
                down = true;
            } else if (rail == rails - 1) {
                down = false;
            }
            rail += down ? 1 : -1;
        }
        String[] railStrings = new String[rails];
        int pos = 0;
        for (int r = 0; r < rails; r++) {
            railStrings[r] = cipher.substring(pos, pos + railCounts[r]);
            pos += railCounts[r];
        }
        StringBuilder result = new StringBuilder();
        int[] railPositions = new int[rails];
        rail = 0;
        down = true;
        for (int i = 0; i < length; i++) {
            result.append(railStrings[rail].charAt(railPositions[rail]++));
            if (rail == 0) {
                down = true;
            } else if (rail == rails - 1) {
                down = false;
            }
            rail += down ? 1 : -1;
        }
        return result.toString();
    }
}
