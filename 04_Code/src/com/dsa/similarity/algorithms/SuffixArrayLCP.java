package com.dsa.similarity.algorithms;

import java.util.*;

/**
 * Suffix Array and Kasai's LCP (Longest Common Prefix) Array (CO2).
 *
 * Concepts:
 * 1. Suffix Array (SA): Lexicographically sorted indices of all suffixes of string S.
 *    Constructed using prefix doubling in O(N log^2 N) time.
 * 2. Kasai's Algorithm: Constructs the LCP array in strict O(N) linear time using
 *    the property that LCP of successive suffixes in text decreases by at most 1.
 * 3. Generalized Suffix Array: Solves the Longest Common Substring (LCS) problem
 *    between two submissions in linear time once SA and LCP are constructed.
 */
public class SuffixArrayLCP {

    public static class CommonSubstringMatch {
        public final int posA;
        public final int posB;
        public final int length;
        public final String substring;

        public CommonSubstringMatch(int posA, int posB, int length, String substring) {
            this.posA = posA;
            this.posB = posB;
            this.length = length;
            this.substring = substring;
        }
    }

    /**
     * Builds Suffix Array using Prefix Doubling.
     * Complexity: O(N log^2 N).
     */
    public static int[] buildSuffixArray(String s) {
        int n = s.length();
        Integer[] sa = new Integer[n];
        int[] rank = new int[n];
        int[] tempRank = new int[n];

        for (int i = 0; i < n; i++) {
            sa[i] = i;
            rank[i] = (int) s.charAt(i);
        }

        for (int k = 1; k < n; k *= 2) {
            final int currentK = k;
            final int[] currentRank = rank;

            Comparator<Integer> comp = (a, b) -> {
                if (currentRank[a] != currentRank[b]) {
                    return Integer.compare(currentRank[a], currentRank[b]);
                }
                int rankA2 = (a + currentK < n) ? currentRank[a + currentK] : -1;
                int rankB2 = (b + currentK < n) ? currentRank[b + currentK] : -1;
                return Integer.compare(rankA2, rankB2);
            };

            Arrays.sort(sa, comp);

            tempRank[sa[0]] = 0;
            for (int i = 1; i < n; i++) {
                int prev = sa[i - 1];
                int curr = sa[i];
                boolean sameFirst = currentRank[curr] == currentRank[prev];
                int prevSecond = (prev + currentK < n) ? currentRank[prev + currentK] : -1;
                int currSecond = (curr + currentK < n) ? currentRank[curr + currentK] : -1;
                tempRank[curr] = tempRank[prev] + (sameFirst && prevSecond == currSecond ? 0 : 1);
            }

            System.arraycopy(tempRank, 0, rank, 0, n);
            if (rank[sa[n - 1]] == n - 1) break; // all ranks unique
        }

        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = sa[i];
        return result;
    }

    /**
     * Computes the LCP array using Kasai's algorithm in O(N) time.
     * LCP[i] stores the length of the longest common prefix of SA[i] and SA[i-1].
     */
    public static int[] buildLCPArray(String s, int[] sa) {
        int n = s.length();
        int[] lcp = new int[n];
        int[] invSa = new int[n];

        for (int i = 0; i < n; i++) {
            invSa[sa[i]] = i;
        }

        int k = 0;
        for (int i = 0; i < n; i++) {
            if (invSa[i] == 0) {
                k = 0;
                continue;
            }
            int j = sa[invSa[i] - 1];
            while (i + k < n && j + k < n && s.charAt(i + k) == s.charAt(j + k)) {
                k++;
            }
            lcp[invSa[i]] = k;
            if (k > 0) {
                k--;
            }
        }
        return lcp;
    }

    /**
     * Finds the Longest Common Substring (LCS) and all significant shared blocks
     * between textA and textB using Generalized Suffix Array + Kasai's LCP.
     */
    public static List<CommonSubstringMatch> findCommonSubstrings(String textA, String textB, int minLength) {
        List<CommonSubstringMatch> matches = new ArrayList<>();
        if (textA == null || textB == null || textA.isEmpty() || textB.isEmpty()) {
            return matches;
        }

        int lenA = textA.length();
        // Concatenate using unique delimiter '#'
        String combined = textA + "#" + textB;
        int n = combined.length();

        int[] sa = buildSuffixArray(combined);
        int[] lcp = buildLCPArray(combined, sa);

        for (int i = 1; i < n; i++) {
            int length = lcp[i];
            if (length >= minLength) {
                int idx1 = sa[i];
                int idx2 = sa[i - 1];

                // Check if one suffix is from textA and the other from textB
                boolean isFromDifferent = (idx1 < lenA && idx2 > lenA) || (idx2 < lenA && idx1 > lenA);
                if (isFromDifferent) {
                    int posA = Math.min(idx1, idx2);
                    int posB = Math.max(idx1, idx2) - (lenA + 1);

                    // Ensure substring doesn't cross the delimiter
                    if (posA + length <= lenA) {
                        String matchStr = combined.substring(Math.min(idx1, idx2), Math.min(idx1, idx2) + length);
                        matches.add(new CommonSubstringMatch(posA, posB, length, matchStr));
                    }
                }
            }
        }

        // Sort descending by length
        matches.sort((a, b) -> Integer.compare(b.length, a.length));
        return matches;
    }

    /**
     * Computes similarity score based on Longest Common Substrings coverage.
     */
    public static double computeSimilarity(String textA, String textB, int minLength) {
        if (textA == null || textB == null || textA.isEmpty() || textB.isEmpty()) return 0.0;
        if (textA.equals(textB)) return 100.0;

        List<CommonSubstringMatch> matches = findCommonSubstrings(textA, textB, minLength);
        if (matches.isEmpty()) return 0.0;

        int longest = matches.get(0).length;
        int minLen = Math.min(textA.length(), textB.length());
        double longestRatio = (double) longest / minLen;

        // Coverage estimate
        boolean[] covered = new boolean[textA.length()];
        for (CommonSubstringMatch m : matches) {
            for (int k = 0; k < m.length && (m.posA + k) < covered.length; k++) {
                covered[m.posA + k] = true;
            }
        }
        int totalCovered = 0;
        for (boolean b : covered) if (b) totalCovered++;

        double coverageRatio = (double) totalCovered / textA.length();
        return Math.min(100.0, (0.5 * longestRatio + 0.5 * coverageRatio) * 100.0);
    }
}
