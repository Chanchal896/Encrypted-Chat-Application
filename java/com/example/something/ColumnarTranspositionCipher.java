package com.example.something;

import java.util.Arrays;
import java.util.Comparator;

public class ColumnarTranspositionCipher {

    private static final String KEY = "CHAT_APP";

    public static String encrypt(String input) {
        return encrypt(input, KEY);
    }

    public static String decrypt(String cipher) {
        return decrypt(cipher, KEY);
    }

    private static String encrypt(String input, String key) {
        int numCols = key.length();
        int numRows = (int) Math.ceil((double) input.length() / numCols);
        char[][] grid = new char[numRows][numCols];
        int pos = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                grid[r][c] = (pos < input.length()) ? input.charAt(pos++) : ' ';
            }
        }
        Integer[] colOrder = new Integer[numCols];
        for (int i = 0; i < numCols; i++) {
            colOrder[i] = i;
        }
        Arrays.sort(colOrder, Comparator.comparingInt(i -> key.charAt(i)));
        StringBuilder cipherText = new StringBuilder();
        for (int index : colOrder) {
            for (int r = 0; r < numRows; r++) {
                cipherText.append(grid[r][index]);
            }
        }
        return cipherText.toString();
    }

    private static String decrypt(String cipher, String key) {
        int numCols = key.length();
        int numRows = (int) Math.ceil((double) cipher.length() / numCols);
        char[][] grid = new char[numRows][numCols];
        Integer[] colOrder = new Integer[numCols];
        for (int i = 0; i < numCols; i++) {
            colOrder[i] = i;
        }
        Arrays.sort(colOrder, Comparator.comparingInt(i -> key.charAt(i)));
        int pos = 0;
        for (int index : colOrder) {
            for (int r = 0; r < numRows; r++) {
                if (pos < cipher.length()) {
                    grid[r][index] = cipher.charAt(pos++);
                } else {
                    grid[r][index] = ' ';
                }
            }
        }
        StringBuilder plain = new StringBuilder();
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                plain.append(grid[r][c]);
            }
        }
        return plain.toString().trim();
    }
}
