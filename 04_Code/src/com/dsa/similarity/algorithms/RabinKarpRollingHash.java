package com.dsa.similarity.algorithms;

import java.util.*;

/**
 * Rabin-Karp Rolling Hash and Winnowing Fingerprinting Algorithm.
 * Implements linear-time pattern matching and robust document fingerprinting (CO2 & CO6).
 *
 * Mathematical Foundations:
 * 1. Polynomial Rolling Hash:
 *    H(c_0 ... c_{k-1}) = ( \sum_{i=0}^{k-1} c_i * B^{k-1-i} ) mod M
 *    Rolling Update:
 *    H_new = ( (H_old - c_out * B^{k-1}) * B + c_in ) mod M
 *
 * 2. Winnowing Algorithm:
 *    In every window of size w hashes, select the hash with the minimum value.
 *    Guarantees any shared substring of length >= (k + w - 1) is detected.
 */
public class RabinKarpRollingHash {

    private static final long BASE = 313;
    private static final long MOD = 1000000007L;

    public static class Fingerprint {
        public final long hash;
        public final int position; // token or character index

        public Fingerprint(long hash, int position) {
            this.hash = hash;
            this.position = position;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Fingerprint that = (Fingerprint) o;
            return hash == that.hash;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(hash);
        }
    }

    /**
     * Finds all exact occurrences of pattern in text using Rabin-Karp algorithm.
     * Time Complexity: Average O(N + M), Worst O(N * M).
     */
    public static List<Integer> searchPattern(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null) return matches;
        int n = text.length();
        int m = pattern.length();
        if (m == 0 || n < m) return matches;

        long patternHash = 0;
        long textHash = 0;
        long power = 1;

        for (int i = 0; i < m - 1; i++) {
            power = (power * BASE) % MOD;
        }

        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * BASE + text.charAt(i)) % MOD;
            textHash = (textHash * BASE + text.charAt(i)) % MOD;
        }

        for (int i = 0; i <= n - m; i++) {
            if (patternHash == textHash) {
                // Las Vegas verification to guarantee zero false positives from collision
                if (text.regionMatches(i, pattern, 0, m)) {
                    matches.add(i);
                }
            }
            if (i < n - m) {
                long removeVal = (text.charAt(i) * power) % MOD;
                textHash = (textHash - removeVal + MOD) % MOD;
                textHash = (textHash * BASE + text.charAt(i + m)) % MOD;
            }
        }
        return matches;
    }

    /**
     * Computes Winnowing fingerprints over a token sequence or string.
     * k = k-gram size (typically 5 to 10 tokens)
     * w = window size (typically 4 to 8)
     */
    public static List<Fingerprint> computeFingerprints(String text, int k, int w) {
        List<Fingerprint> fingerprints = new ArrayList<>();
        if (text == null || text.length() < k) return fingerprints;

        int n = text.length();
        long[] hashes = new long[n - k + 1];
        long power = 1;
        for (int i = 0; i < k - 1; i++) {
            power = (power * BASE) % MOD;
        }

        // 1. Compute rolling hashes for all k-grams
        long currentHash = 0;
        for (int i = 0; i < k; i++) {
            currentHash = (currentHash * BASE + text.charAt(i)) % MOD;
        }
        hashes[0] = currentHash;

        for (int i = 1; i <= n - k; i++) {
            long removeVal = (text.charAt(i - 1) * power) % MOD;
            currentHash = (currentHash - removeVal + MOD) % MOD;
            currentHash = (currentHash * BASE + text.charAt(i + k - 1)) % MOD;
            hashes[i] = currentHash;
        }

        // 2. Winnowing selection: In each window of w hashes, select rightmost minimum
        int numHashes = hashes.length;
        if (numHashes <= w) {
            int minIdx = 0;
            for (int i = 1; i < numHashes; i++) {
                if (hashes[i] <= hashes[minIdx]) {
                    minIdx = i;
                }
            }
            fingerprints.add(new Fingerprint(hashes[minIdx], minIdx));
            return fingerprints;
        }

        int minIdx = -1;
        for (int i = 0; i <= numHashes - w; i++) {
            int currentWindowMin = i;
            for (int j = 1; j < w; j++) {
                if (hashes[i + j] <= hashes[currentWindowMin]) {
                    currentWindowMin = i + j;
                }
            }
            if (minIdx != currentWindowMin) {
                minIdx = currentWindowMin;
                fingerprints.add(new Fingerprint(hashes[minIdx], minIdx));
            }
        }

        return fingerprints;
    }

    /**
     * Calculates similarity percentage between two texts using Jaccard / containment
     * score over Winnowing fingerprints.
     */
    public static double calculateSimilarity(String textA, String textB, int k, int w) {
        if (textA == null || textB == null || textA.isEmpty() || textB.isEmpty()) return 0.0;
        if (textA.equals(textB)) return 100.0;

        List<Fingerprint> fpsA = computeFingerprints(textA, k, w);
        List<Fingerprint> fpsB = computeFingerprints(textB, k, w);

        if (fpsA.isEmpty() || fpsB.isEmpty()) {
            return textA.equals(textB) ? 100.0 : 0.0;
        }

        Set<Long> setA = new HashSet<>();
        for (Fingerprint f : fpsA) setA.add(f.hash);

        Set<Long> setB = new HashSet<>();
        for (Fingerprint f : fpsB) setB.add(f.hash);

        int intersection = 0;
        for (Long h : setA) {
            if (setB.contains(h)) {
                intersection++;
            }
        }

        // Dice-Sorensen coefficient on fingerprints
        double score = (2.0 * intersection) / (setA.size() + setB.size());
        return Math.min(100.0, score * 100.0);
    }
}
