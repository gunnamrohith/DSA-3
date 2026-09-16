package com.dsa.similarity.model;

import com.dsa.similarity.tokenizer.SimplePreprocessor;

import java.util.List;

/**
 * Represents a student or candidate exam submission.
 * Uses clean text-level preprocessing without AST lexical tokenization.
 */
public class Submission {
    private final String id;
    private final String candidateName;
    private final String rawCode;
    private final String cleanedCode;
    private final List<SimplePreprocessor.CleanedLine> lines;
    private final List<String> words;

    public Submission(String id, String candidateName, String rawCode) {
        this.id = id;
        this.candidateName = candidateName;
        this.rawCode = rawCode;
        SimplePreprocessor.ProcessedCode processed = SimplePreprocessor.process(rawCode);
        this.cleanedCode = processed.cleanedCode;
        this.lines = processed.lines;
        this.words = processed.words;
    }

    public String getId() { return id; }
    public String getCandidateName() { return candidateName; }
    public String getRawCode() { return rawCode; }
    public String getCleanedCode() { return cleanedCode; }
    public List<SimplePreprocessor.CleanedLine> getLines() { return lines; }
    public List<String> getWords() { return words; }

    public int getLineCount() {
        if (rawCode == null || rawCode.isEmpty()) return 0;
        return rawCode.split("\r\n|\r|\n", -1).length;
    }

    /**
     * Maps a cleaned line index back to the original source line number.
     */
    public int getOriginalLineNumber(int cleanedLineIdx) {
        if (cleanedLineIdx < 0 || cleanedLineIdx >= lines.size()) {
            return Math.max(1, Math.min(cleanedLineIdx + 1, getLineCount()));
        }
        return lines.get(cleanedLineIdx).originalLineNumber;
    }
}
