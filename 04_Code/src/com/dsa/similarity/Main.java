package com.dsa.similarity;

import com.dsa.similarity.engine.SimilarityAnalyzer;
import com.dsa.similarity.model.AnalysisReport;
import com.dsa.similarity.model.MatchBlock;
import com.dsa.similarity.model.Submission;
import com.dsa.similarity.server.HttpServerEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

/**
 * Master Application Entry Point for Code & Document Similarity Analyzer.
 * Course: Data Structures and Algorithms - 3 (25CS2103E)
 * Focuses on Linear-Time String Algorithms & Suffix Structures (CO2, CO6):
 * - Rabin-Karp Rolling Hash & Winnowing
 * - Knuth-Morris-Pratt (KMP)
 * - Z-Algorithm
 * - Suffix Array + Kasai's LCP Array
 * - TF-IDF Word Frequency Cosine
 */
public class Main {

    private static void printBanner() {
        System.out.println("==========================================================================");
        System.out.println("  DOCUMENT & CODE SIMILARITY ANALYZER");
        System.out.println("  Linear-Time String Algorithms & Suffix Structures");
        System.out.println("==========================================================================");
    }

    public static void main(String[] args) {
        printBanner();

        if (args.length == 0 || "--server".equalsIgnoreCase(args[0])) {
            int port = 8080;
            if (args.length > 1) {
                try {
                    port = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) {}
            }
            startServer(port);
        } else if ("--cli".equalsIgnoreCase(args[0])) {
            if (args.length < 3) {
                System.out.println("Usage: java Main --cli <fileA.java> <fileB.java>");
                System.exit(1);
            }
            runCliComparison(args[1], args[2]);
        } else if ("--test".equalsIgnoreCase(args[0])) {
            runSelfTest();
        } else {
            System.out.println("Unknown command: " + args[0]);
            System.out.println("Available options:");
            System.out.println("  --server [port]           Launch Web Dashboard (default port 8080)");
            System.out.println("  --cli <fileA> <fileB>     Run CLI pairwise plagiarism analysis");
            System.out.println("  --test                    Execute self-diagnostic test suite");
        }
    }

    private static void startServer(int port) {
        File webDir = new File("web");
        if (!webDir.exists()) {
            webDir = new File("../web");
        }

        try {
            HttpServerEngine server = new HttpServerEngine(port, webDir);
            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runCliComparison(String pathA, String pathB) {
        try {
            File fA = new File(pathA);
            File fB = new File(pathB);
            if (!fA.exists() || !fB.exists()) {
                System.err.println("Error: One or both files do not exist.");
                return;
            }

            String codeA = Files.readString(fA.toPath());
            String codeB = Files.readString(fB.toPath());

            Submission subA = new Submission(fA.getName(), fA.getName(), codeA);
            Submission subB = new Submission(fB.getName(), fB.getName(), codeB);

            AnalysisReport rep = SimilarityAnalyzer.compare(subA, subB);
            printCliReport(rep);

        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
        }
    }

    private static void printCliReport(AnalysisReport rep) {
        System.out.println("\n>>> ANALYSIS RESULTS FOR: " + rep.getCandidateA() + " vs " + rep.getCandidateB());
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("  OVERALL SIMILARITY SCORE : %6.2f %%\n", rep.getOverallScore());
        System.out.println("  VERDICT                  : " + rep.getRiskLevel());
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("  ALGORITHM BREAKDOWN (DSA-3 Linear String Algorithms CO2, CO6):");
        System.out.printf("   [CO2, CO6] Rabin-Karp Winnowing Hash   : %6.2f %%\n", rep.getRabinKarpWinnowingScore());
        System.out.printf("   [CO2]      Knuth-Morris-Pratt (KMP)    : %6.2f %%\n", rep.getKmpScore());
        System.out.printf("   [CO2]      Z-Algorithm Prefix Align    : %6.2f %%\n", rep.getZAlgorithmScore());
        System.out.printf("   [CO2]      Suffix Array + Kasai's LCP  : %6.2f %% (LCS: %d chars)\n",
                rep.getSuffixArrayLcpScore(), rep.getLongestCommonSubstringLength());
        System.out.printf("   [INFO]     TF-IDF Word Vector Cosine   : %6.2f %%\n", rep.getTfidfCosineScore());
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("  LATENCY BENCHMARKS (Microseconds):");
        for (Map.Entry<String, Long> entry : rep.getExecutionTimesMs().entrySet()) {
            System.out.printf("   - %-28s : %6d µs\n", entry.getKey(), entry.getValue());
        }
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("  DETECTED MATCHING CODE BLOCKS: " + rep.getMatchingBlocks().size());
        int idx = 1;
        for (MatchBlock mb : rep.getMatchingBlocks()) {
            System.out.printf("   #%d [%s] Lines %d-%d (A) <=> Lines %d-%d (B) | Length: %d chars\n",
                    idx++, mb.getAlgorithm(), mb.getStartLineA(), mb.getEndLineA(),
                    mb.getStartLineB(), mb.getEndLineB(), mb.getTokenCount());
        }
        System.out.println("==========================================================================\n");
    }

    private static void runSelfTest() {
        System.out.println("[*] Running algorithmic verification self-test (CO2 String Algorithms)...");
        String sampleOriginal =
            "public class QuickSort {\n" +
            "    public void sort(int[] arr, int low, int high) {\n" +
            "        if (low < high) {\n" +
            "            int pi = partition(arr, low, high);\n" +
            "            sort(arr, low, pi - 1);\n" +
            "            sort(arr, pi + 1, high);\n" +
            "        }\n" +
            "    }\n" +
            "    private int partition(int[] arr, int low, int high) {\n" +
            "        int pivot = arr[high];\n" +
            "        int i = (low - 1);\n" +
            "        for (int j = low; j < high; j++) {\n" +
            "            if (arr[j] <= pivot) {\n" +
            "                i++;\n" +
            "                int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;\n" +
            "            }\n" +
            "        }\n" +
            "        int temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;\n" +
            "        return i + 1;\n" +
            "    }\n" +
            "}\n";

        String sampleCopy =
            "// Copy with rearranged comments and extra spaces\n" +
            "public class QuickSortCopy {\n" +
            "    public void sort(int[] arr, int low, int high) {\n" +
            "        if (low < high) {\n" +
            "            int pi = partition(arr, low, high);\n" +
            "            sort(arr, low, pi - 1);\n" +
            "            sort(arr, pi + 1, high);\n" +
            "        }\n" +
            "    }\n" +
            "    private int partition(int[] arr, int low, int high) {\n" +
            "        int pivot = arr[high];\n" +
            "        int i = (low - 1);\n" +
            "        for (int j = low; j < high; j++) {\n" +
            "            if (arr[j] <= pivot) {\n" +
            "                i++;\n" +
            "                int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;\n" +
            "            }\n" +
            "        }\n" +
            "        int temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;\n" +
            "        return i + 1;\n" +
            "    }\n" +
            "}\n";

        String sampleIndependent =
            "public class LinearSearch {\n" +
            "    public static int find(int[] arr, int target) {\n" +
            "        for (int i = 0; i < arr.length; i++) {\n" +
            "            if (arr[i] == target) {\n" +
            "                return i;\n" +
            "            }\n" +
            "        }\n" +
            "        return -1;\n" +
            "    }\n" +
            "}\n";

        Submission s1 = new Submission("1", "Candidate_1_Original", sampleOriginal);
        Submission s2 = new Submission("2", "Candidate_2_Copy", sampleCopy);
        Submission s3 = new Submission("3", "Candidate_3_Independent", sampleIndependent);

        System.out.println("\n--- TEST CASE 1: Original vs Direct Copy ---");
        AnalysisReport rep1 = SimilarityAnalyzer.compare(s1, s2);
        printCliReport(rep1);

        System.out.println("\n--- TEST CASE 2: Original vs Independent Solution ---");
        AnalysisReport rep2 = SimilarityAnalyzer.compare(s1, s3);
        printCliReport(rep2);

        if (rep1.getOverallScore() > 75.0 && rep2.getOverallScore() < 35.0) {
            System.out.println("[SUCCESS] ALL STRING ALGORITHM CHECKS PASSED PERFECTLY!");
        } else {
            System.out.println("[WARNING] Algorithmic score calibration check.");
        }
    }
}
