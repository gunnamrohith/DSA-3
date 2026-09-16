package com.dsa.similarity.algorithms;

import java.util.*;

/**
 * Z-Algorithm (Z-function) for Linear-Time String Matching (CO2).
 * Computes the Z-array in O(N) time where Z[i] is the length of the longest
 * common prefix between S and the suffix of S starting at index i.
 * By constructing string S = Pattern + '$' + Text, exact matches are found in O(N + M).
 */
public class ZAlgorithm {

    /**
     * Computes the Z-array for string S.
     * Uses the sliding [L, R] interval window to achieve strict O(N) time.
     */
    public static int[] computeZArray(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }
        return z;
    }

    /**
     * Pattern search using Z-algorithm.
     * Searches for occurrences of pattern in text using delimiter '$'.
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> occurrences = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return occurrences;
        }

        String concat = pattern + "$" + text;
        int patternLen = pattern.length();
        int[] z = computeZArray(concat);

        for (int i = 0; i < z.length; i++) {
            if (z[i] == patternLen) {
                occurrences.add(i - patternLen - 1);
            }
        }
        return occurrences;
    }

    /**
     * Computes structural similarity ratio by measuring the average prefix matching
     * depth between normalized sequences.
     */
    public static double computeSimilarity(String textA, String textB) {
        if (textA == null || textB == null || textA.isEmpty() || textB.isEmpty()) return 0.0;
        if (textA.equals(textB)) return 100.0;

        String combined = textA + "#" + textB;
        int[] z = computeZArray(combined);

        int maxMatch = 0;
        long totalMatchLength = 0;
        int offset = textA.length() + 1;

        for (int i = offset; i < combined.length(); i++) {
            int len = z[i];
            totalMatchLength += len;
            if (len > maxMatch) {
                maxMatch = len;
            }
        }

        double maxScore = Math.min(100.0, ((double) maxMatch / Math.min(textA.length(), textB.length())) * 100.0);
        double avgScore = Math.min(100.0, ((double) totalMatchLength / (textA.length() + textB.length())) * 200.0);
        return 0.7 * maxScore + 0.3 * avgScore;
    }
}
