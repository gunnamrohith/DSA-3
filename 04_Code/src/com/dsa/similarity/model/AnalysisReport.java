package com.dsa.similarity.model;

import java.util.*;

/**
 * Encapsulates the multi-algorithmic similarity evaluation between two submissions
 * using linear-time string algorithms (Rabin-Karp, KMP, Z-Algorithm, Suffix Array + LCP)
 * and TF-IDF cosine similarity.
 */
public class AnalysisReport {
    private final String candidateA;
    private final String candidateB;
    private final double overallScore; // 0.0 - 100.0%
    private final String riskLevel;     // LOW, SUSPICIOUS, HIGH PLAGIARISM DETECTED

    // Metric Breakdown
    private final double rabinKarpWinnowingScore;
    private final double kmpScore;
    private final double zAlgorithmScore;
    private final double suffixArrayLcpScore;
    private final int longestCommonSubstringLength;
    private final double tfidfCosineScore;

    private final List<MatchBlock> matchingBlocks;
    private final Map<String, Long> executionTimesMs;

    public AnalysisReport(String candidateA, String candidateB, double overallScore,
                          String riskLevel, double rabinKarpWinnowingScore, double kmpScore,
                          double zAlgorithmScore, double suffixArrayLcpScore,
                          int longestCommonSubstringLength, double tfidfCosineScore,
                          List<MatchBlock> matchingBlocks, Map<String, Long> executionTimesMs) {
        this.candidateA = candidateA;
        this.candidateB = candidateB;
        this.overallScore = overallScore;
        this.riskLevel = riskLevel;
        this.rabinKarpWinnowingScore = rabinKarpWinnowingScore;
        this.kmpScore = kmpScore;
        this.zAlgorithmScore = zAlgorithmScore;
        this.suffixArrayLcpScore = suffixArrayLcpScore;
        this.longestCommonSubstringLength = longestCommonSubstringLength;
        this.tfidfCosineScore = tfidfCosineScore;
        this.matchingBlocks = matchingBlocks;
        this.executionTimesMs = executionTimesMs;
    }

    public String getCandidateA() { return candidateA; }
    public String getCandidateB() { return candidateB; }
    public double getOverallScore() { return overallScore; }
    public String getRiskLevel() { return riskLevel; }
    public double getRabinKarpWinnowingScore() { return rabinKarpWinnowingScore; }
    public double getKmpScore() { return kmpScore; }
    public double getZAlgorithmScore() { return zAlgorithmScore; }
    public double getSuffixArrayLcpScore() { return suffixArrayLcpScore; }
    public int getLongestCommonSubstringLength() { return longestCommonSubstringLength; }
    public double getTfidfCosineScore() { return tfidfCosineScore; }
    public List<MatchBlock> getMatchingBlocks() { return matchingBlocks; }
    public Map<String, Long> getExecutionTimesMs() { return executionTimesMs; }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(String.format(Locale.US, "\"candidateA\":\"%s\",", escape(candidateA)));
        sb.append(String.format(Locale.US, "\"candidateB\":\"%s\",", escape(candidateB)));
        sb.append(String.format(Locale.US, "\"overallScore\":%.2f,", overallScore));
        sb.append(String.format(Locale.US, "\"riskLevel\":\"%s\",", riskLevel));
        sb.append(String.format(Locale.US, "\"rabinKarpWinnowingScore\":%.2f,", rabinKarpWinnowingScore));
        sb.append(String.format(Locale.US, "\"kmpScore\":%.2f,", kmpScore));
        sb.append(String.format(Locale.US, "\"zAlgorithmScore\":%.2f,", zAlgorithmScore));
        sb.append(String.format(Locale.US, "\"suffixArrayLcpScore\":%.2f,", suffixArrayLcpScore));
        sb.append(String.format(Locale.US, "\"longestCommonSubstringLength\":%d,", longestCommonSubstringLength));
        sb.append(String.format(Locale.US, "\"tfidfCosineScore\":%.2f,", tfidfCosineScore));

        // Execution times
        sb.append("\"benchmarks\":{");
        int idx = 0;
        for (Map.Entry<String, Long> entry : executionTimesMs.entrySet()) {
            if (idx++ > 0) sb.append(",");
            sb.append(String.format(Locale.US, "\"%s\":%d", entry.getKey(), entry.getValue()));
        }
        sb.append("},");

        // Matching blocks
        sb.append("\"matchingBlocks\":[");
        for (int i = 0; i < matchingBlocks.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(matchingBlocks.get(i).toJson());
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}
