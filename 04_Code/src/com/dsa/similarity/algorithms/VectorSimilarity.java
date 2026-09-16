package com.dsa.similarity.algorithms;

import java.util.*;

/**
 * TF-IDF and Cosine Vector Space Similarity Model.
 * Computes frequency vectors over words/identifiers to measure lexical similarity.
 */
public class VectorSimilarity {

    /**
     * Calculates Cosine Similarity between word frequency distributions of two submissions.
     */
    public static double computeCosineSimilarity(List<String> wordsA, List<String> wordsB) {
        if (wordsA == null || wordsB == null || wordsA.isEmpty() || wordsB.isEmpty()) {
            return 0.0;
        }

        Map<String, Integer> freqA = new HashMap<>();
        Map<String, Integer> freqB = new HashMap<>();
        Set<String> vocabulary = new HashSet<>();

        for (String w : wordsA) {
            freqA.put(w, freqA.getOrDefault(w, 0) + 1);
            vocabulary.add(w);
        }

        for (String w : wordsB) {
            freqB.put(w, freqB.getOrDefault(w, 0) + 1);
            vocabulary.add(w);
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (String word : vocabulary) {
            int countA = freqA.getOrDefault(word, 0);
            int countB = freqB.getOrDefault(word, 0);

            // Sublinear TF scaling: 1 + log(tf)
            double tfA = (countA > 0) ? (1.0 + Math.log(countA)) : 0.0;
            double tfB = (countB > 0) ? (1.0 + Math.log(countB)) : 0.0;

            dotProduct += (tfA * tfB);
            normA += (tfA * tfA);
            normB += (tfB * tfB);
        }

        if (normA == 0.0 || normB == 0.0) return 0.0;

        double cosine = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        return Math.min(100.0, cosine * 100.0);
    }
}
