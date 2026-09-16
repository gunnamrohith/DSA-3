package com.dsa.similarity.tokenizer;

import java.util.*;

/**
 * Clean and straightforward Source Code Preprocessor.
 * Performs comment stripping and whitespace normalization without complex AST/lexical tokenization.
 * Preserves original line mappings so matching blocks can be highlighted accurately in the UI.
 */
public class SimplePreprocessor {

    public static class CleanedLine {
        public final int originalLineNumber;
        public final String text;

        public CleanedLine(int originalLineNumber, String text) {
            this.originalLineNumber = originalLineNumber;
            this.text = text;
        }
    }

    public static class ProcessedCode {
        public final String originalCode;
        public final String cleanedCode;
        public final List<CleanedLine> lines;
        public final List<String> words;

        public ProcessedCode(String originalCode, String cleanedCode, List<CleanedLine> lines, List<String> words) {
            this.originalCode = originalCode;
            this.cleanedCode = cleanedCode;
            this.lines = lines;
            this.words = words;
        }
    }

    /**
     * Preprocesses code: removes single-line and multi-line comments,
     * strips trailing/leading whitespaces, and normalizes space runs.
     */
    public static ProcessedCode process(String rawCode) {
        if (rawCode == null) rawCode = "";

        StringBuilder cleaned = new StringBuilder();
        List<CleanedLine> lineList = new ArrayList<>();
        List<String> words = new ArrayList<>();

        String[] rawLines = rawCode.split("\r\n|\r|\n", -1);
        boolean inBlockComment = false;

        for (int i = 0; i < rawLines.length; i++) {
            int lineNum = i + 1;
            String line = rawLines[i];
            StringBuilder sb = new StringBuilder();

            int j = 0;
            int len = line.length();

            while (j < len) {
                if (inBlockComment) {
                    if (j + 1 < len && line.charAt(j) == '*' && line.charAt(j + 1) == '/') {
                        inBlockComment = false;
                        j += 2;
                    } else {
                        j++;
                    }
                } else {
                    if (j + 1 < len && line.charAt(j) == '/' && line.charAt(j + 1) == '*') {
                        inBlockComment = true;
                        j += 2;
                    } else if (j + 1 < len && line.charAt(j) == '/' && line.charAt(j + 1) == '/') {
                        // rest of line is comment
                        break;
                    } else {
                        sb.append(line.charAt(j));
                        j++;
                    }
                }
            }

            String processedLine = sb.toString().trim().replaceAll("\\s+", " ");
            if (!processedLine.isEmpty()) {
                lineList.add(new CleanedLine(lineNum, processedLine));
                cleaned.append(processedLine).append("\n");

                // Collect words for frequency vector analysis
                String[] lineWords = processedLine.split("[^a-zA-Z0-9_]+");
                for (String w : lineWords) {
                    if (!w.isEmpty()) {
                        words.add(w.toLowerCase());
                    }
                }
            }
        }

        return new ProcessedCode(rawCode, cleaned.toString().trim(), lineList, words);
    }
}
