package com.dsa.similarity.model;

import java.util.*;

/**
 * Stores results of pairwise similarity analysis across an entire batch
 * of candidate submissions.
 */
public class BatchReport {
    private final List<String> candidateNames;
    private final double[][] similarityMatrix;
    private final List<AnalysisReport> pairwiseReports;

    public BatchReport(List<String> candidateNames, double[][] similarityMatrix, List<AnalysisReport> pairwiseReports) {
        this.candidateNames = candidateNames;
        this.similarityMatrix = similarityMatrix;
        this.pairwiseReports = pairwiseReports;
    }

    public List<String> getCandidateNames() { return candidateNames; }
    public double[][] getSimilarityMatrix() { return similarityMatrix; }
    public List<AnalysisReport> getPairwiseReports() { return pairwiseReports; }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"candidates\":[");
        for (int i = 0; i < candidateNames.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(candidateNames.get(i)).append("\"");
        }
        sb.append("],\"matrix\":[");
        for (int i = 0; i < similarityMatrix.length; i++) {
            if (i > 0) sb.append(",");
            sb.append("[");
            for (int j = 0; j < similarityMatrix[i].length; j++) {
                if (j > 0) sb.append(",");
                sb.append(String.format(Locale.US, "%.2f", similarityMatrix[i][j]));
            }
            sb.append("]");
        }
        sb.append("],\"reports\":[");
        for (int i = 0; i < pairwiseReports.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(pairwiseReports.get(i).toJson());
        }
        sb.append("]}");
        return sb.toString();
    }
}
