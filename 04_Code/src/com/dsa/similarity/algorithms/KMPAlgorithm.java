package com.dsa.similarity.algorithms;

import java.util.*;

/**
 * Knuth-Morris-Pratt (KMP) Algorithm with Longest Prefix-Suffix (LPS) Array (CO2).
 * Operates in strict linear time O(N + M) without backtracking in the main text.
 * Used for detecting verbatim and structural cloned token segments.
 */
public class KMPAlgorithm {

    /**
     * Constructs the LPS (pi) table for pattern P.
     * LPS[i] = length of the longest proper prefix of P[0..i] that is also a suffix of P[0..i].
     * Complexity: O(M) time, O(M) space.
     */
    public static int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1]; // fall back using previous LPS
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Executes KMP search of pattern in text.
     * Returns 0-based start indices of all occurrences.
     * Complexity: O(N) time.
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        int n = text.length();
        int m = pattern.length();
        int[] lps = computeLPS(pattern);

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            if (j == m) {
                matches.add(i - j);
                j = lps[j - 1]; // prepare for next match
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }

    /**
     * Evaluates the proportion of text B that can be covered by patterns from text A
     * using KMP chunk search.
     */
    public static double computeCoverage(String textA, String textB, int minChunkSize) {
        if (textA == null || textB == null || textA.isEmpty() || textB.isEmpty()) return 0.0;
        if (textA.equals(textB)) return 100.0;
        if (textA.length() < minChunkSize || textB.length() < minChunkSize) {
            return textA.equals(textB) ? 100.0 : 0.0;
        }

        boolean[] matchedInB = new boolean[textB.length()];
        int step = Math.max(1, minChunkSize / 2);

        for (int i = 0; i <= textA.length() - minChunkSize; i += step) {
            String chunk = textA.substring(i, i + minChunkSize);
            List<Integer> occurrences = search(textB, chunk);
            for (int pos : occurrences) {
                for (int k = 0; k < minChunkSize && (pos + k) < textB.length(); k++) {
                    matchedInB[pos + k] = true;
                }
            }
        }

        int covered = 0;
        for (boolean b : matchedInB) {
            if (b) covered++;
        }

        return (double) covered / textB.length() * 100.0;
    }
}
