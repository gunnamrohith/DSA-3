package com.dsa.similarity.engine;

import com.dsa.similarity.algorithms.*;
import com.dsa.similarity.model.*;
import com.dsa.similarity.tokenizer.SimplePreprocessor;

import java.util.*;

/**
 * Main Orchestration Engine.
 * Executes linear string algorithms and suffix structures:
 * - Rabin-Karp Rolling Hash & Winnowing (CO2, CO6)
 * - Knuth-Morris-Pratt (KMP) (CO2)
 * - Z-Algorithm (CO2)
 * - Suffix Array + Kasai's LCP Array (CO2)
 * - TF-IDF Word Vector Cosine Similarity
 */
public class SimilarityAnalyzer {

    public static AnalysisReport compare(Submission subA, Submission subB) {
        Map<String, Long> benchmarks = new LinkedHashMap<>();
        List<MatchBlock> matches = new ArrayList<>();

        String cleanA = subA.getCleanedCode();
        String cleanB = subB.getCleanedCode();

        // 1. Rabin-Karp Rolling Hash & Winnowing Fingerprinting (CO2, CO6)
        long t0 = System.nanoTime();
        double rkScore = RabinKarpRollingHash.calculateSimilarity(cleanA, cleanB, 12, 6);
        benchmarks.put("Rabin-Karp Winnowing", (System.nanoTime() - t0) / 1000); // µs

        // 2. KMP Algorithm Substring Coverage (CO2)
        long t1 = System.nanoTime();
        double kmpScore = KMPAlgorithm.computeCoverage(cleanA, cleanB, 20);
        benchmarks.put("KMP Algorithm", (System.nanoTime() - t1) / 1000);

        // 3. Z-Algorithm Structural Prefix Alignment (CO2)
        long t2 = System.nanoTime();
        double zScore = ZAlgorithm.computeSimilarity(cleanA, cleanB);
        benchmarks.put("Z-Algorithm", (System.nanoTime() - t2) / 1000);

        // 4. Suffix Array + Kasai's LCP Array (CO2)
        long t3 = System.nanoTime();
        double saScore = SuffixArrayLCP.computeSimilarity(cleanA, cleanB, 25);
        List<SuffixArrayLCP.CommonSubstringMatch> commonSubstrings =
            SuffixArrayLCP.findCommonSubstrings(cleanA, cleanB, 30);
        int maxLcsLength = commonSubstrings.isEmpty() ? 0 : commonSubstrings.get(0).length;
        benchmarks.put("Suffix Array + LCP", (System.nanoTime() - t3) / 1000);

        // 5. TF-IDF Word Vector Cosine Similarity
        long t4 = System.nanoTime();
        double tfidfScore = VectorSimilarity.computeCosineSimilarity(subA.getWords(), subB.getWords());
        benchmarks.put("TF-IDF Word Cosine", (System.nanoTime() - t4) / 1000);

        // Map Suffix Array Common Substrings to source line numbers
        Set<String> seenMatches = new HashSet<>();
        for (SuffixArrayLCP.CommonSubstringMatch sm : commonSubstrings) {
            if (matches.size() >= 8) break; // keep top 8 distinct blocks
            if (sm.length < 20) continue;

            int lineA = findLineAtChar(cleanA, subA.getLines(), sm.posA);
            int endLineA = findLineAtChar(cleanA, subA.getLines(), sm.posA + sm.length);
            int lineB = findLineAtChar(cleanB, subB.getLines(), sm.posB);
            int endLineB = findLineAtChar(cleanB, subB.getLines(), sm.posB + sm.length);

            String key = lineA + ":" + endLineA + "<->" + lineB + ":" + endLineB;
            if (!seenMatches.contains(key)) {
                seenMatches.add(key);
                String preview = sm.substring.replace("\n", " ").trim();
                if (preview.length() > 60) preview = preview.substring(0, 60) + "...";

                matches.add(new MatchBlock(
                    lineA, endLineA,
                    lineB, endLineB,
                    sm.length,
                    "Suffix-Array-LCS",
                    saScore,
                    preview,
                    preview
                ));
            }
        }

        // Weighted Overall Similarity Score
        double overallScore = (0.35 * rkScore) +
                              (0.25 * saScore) +
                              (0.20 * kmpScore) +
                              (0.10 * zScore) +
                              (0.10 * tfidfScore);

        if (rkScore > 80.0 && saScore > 75.0) {
            overallScore = Math.max(overallScore, (rkScore + saScore) / 2.0);
        }

        overallScore = Math.min(100.0, Math.max(0.0, overallScore));

        String riskLevel;
        if (overallScore >= 70.0) {
            riskLevel = "CRITICAL: HIGH PLAGIARISM DETECTED";
        } else if (overallScore >= 35.0) {
            riskLevel = "WARNING: MODERATE SIMILARITY (SUSPICIOUS)";
        } else {
            riskLevel = "SAFE: LOW SIMILARITY (INDEPENDENT WORK)";
        }

        return new AnalysisReport(
            subA.getCandidateName(),
            subB.getCandidateName(),
            overallScore,
            riskLevel,
            rkScore,
            kmpScore,
            zScore,
            saScore,
            maxLcsLength,
            tfidfScore,
            matches,
            benchmarks
        );
    }

    private static int findLineAtChar(String cleanedText, List<SimplePreprocessor.CleanedLine> lines, int charPos) {
        if (lines == null || lines.isEmpty()) return 1;
        int currentPos = 0;
        for (int i = 0; i < lines.size(); i++) {
            currentPos += lines.get(i).text.length() + 1; // including \n
            if (currentPos >= charPos) {
                return lines.get(i).originalLineNumber;
            }
        }
        return lines.get(lines.size() - 1).originalLineNumber;
    }

    public static BatchReport compareBatch(List<Submission> submissions) {
        int n = submissions.size();
        List<String> names = new ArrayList<>();
        for (Submission s : submissions) {
            names.add(s.getCandidateName());
        }

        double[][] matrix = new double[n][n];
        List<AnalysisReport> reports = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            matrix[i][i] = 100.0;
            for (int j = i + 1; j < n; j++) {
                AnalysisReport rep = compare(submissions.get(i), submissions.get(j));
                matrix[i][j] = rep.getOverallScore();
                matrix[j][i] = rep.getOverallScore();
                reports.add(rep);
            }
        }

        return new BatchReport(names, matrix, reports);
    }
}
