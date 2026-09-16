package com.dsa.similarity.model;

/**
 * Encapsulates an identified region of copied or highly similar code
 * between two candidate submissions, including source line ranges,
 * algorithm that detected it, and confidence.
 */
public class MatchBlock {
    private final int startLineA;
    private final int endLineA;
    private final int startLineB;
    private final int endLineB;
    private final int tokenCount;
    private final String algorithm;
    private final double similarity;
    private final String snippetA;
    private final String snippetB;

    public MatchBlock(int startLineA, int endLineA, int startLineB, int endLineB,
                      int tokenCount, String algorithm, double similarity,
                      String snippetA, String snippetB) {
        this.startLineA = startLineA;
        this.endLineA = endLineA;
        this.startLineB = startLineB;
        this.endLineB = endLineB;
        this.tokenCount = tokenCount;
        this.algorithm = algorithm;
        this.similarity = similarity;
        this.snippetA = snippetA;
        this.snippetB = snippetB;
    }

    public int getStartLineA() { return startLineA; }
    public int getEndLineA() { return endLineA; }
    public int getStartLineB() { return startLineB; }
    public int getEndLineB() { return endLineB; }
    public int getTokenCount() { return tokenCount; }
    public String getAlgorithm() { return algorithm; }
    public double getSimilarity() { return similarity; }
    public String getSnippetA() { return snippetA; }
    public String getSnippetB() { return snippetB; }

    public String toJson() {
        return String.format(
            "{\"startLineA\":%d,\"endLineA\":%d,\"startLineB\":%d,\"endLineB\":%d,\"tokenCount\":%d,\"algorithm\":\"%s\",\"similarity\":%.2f,\"snippetA\":%s,\"snippetB\":%s}",
            startLineA, endLineA, startLineB, endLineB, tokenCount, algorithm, similarity,
            escapeJson(snippetA), escapeJson(snippetB)
        );
    }

    private static String escapeJson(String s) {
        if (s == null) return "\"\"";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
