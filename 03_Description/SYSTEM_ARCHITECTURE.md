# System Architecture Specification

## Course: Data Structures and Algorithms - 3 (25CS2103E)
### Project: Scalable Code & Document Similarity Analyzer for Exam and Interview Integrity

---

## 1. Executive Summary & Design Goals
During university programming examinations and technical coding interviews, automated integrity analysis is crucial to detect collusion, code sharing, and plagiarized logic. However, simple text diffing tools fail against trivial disguises such as:
1. Identifier renaming (e.g. `int count = 0;` replaced with `int totalStudents = 0;`).
2. Statement reordering and redundant whitespace changes.
3. Comment stripping or rewriting.
4. Method and helper function relocation.

The **DSA-3 Document Similarity Analyzer** solves this through a multi-tiered pipeline combining lexical normalization with classical and advanced algorithms from the **DSA-3 syllabus (CO1 through CO6)**:
- **Lexical Token Normalizer**: Removes comments, standardizes literals, and canonicalizes identifiers into an abstract token stream.
- **Rabin-Karp Winnowing Fingerprinting (CO2, CO6)**: Rolling polynomial hash with sliding window minima.
- **Knuth-Morris-Pratt (KMP) (CO2)**: Linear-time exact token clone detection using the LPS table.
- **Z-Algorithm (CO2)**: Linear-time prefix box pattern matching.
- **Suffix Array + Kasai's LCP Array (CO2)**: Identifies the Longest Common Substrings (LCS) across submissions in linear time once constructed.
- **Needleman-Wunsch Alignment DP (CO1, CO3)**: Dynamic programming global alignment measuring structural edit similarity.
- **Maximum Bipartite Matching (CO1, CO4)**: Network flow formulation mapping independent function blocks between Candidate A and Candidate B.
- **TF-IDF & Cosine Similarity**: High-dimensional vector space lexical footprint.

---

## 2. High-Level Architecture Diagram

```
+-------------------------------------------------------------------------+
|                           User Interfaces                               |
|   - Modern Web Dashboard (HTML5 / CSS3 / Vanilla JS / Canvas Radar)     |
|   - Terminal CLI Tool (Single Pair & Exam Batch Directory Runner)      |
+------------------------------------+------------------------------------+
                                     | (HTTP REST / In-Memory CLI)
                                     v
+-------------------------------------------------------------------------+
|                  Lightweight Java HTTP Server Engine                    |
|       (com.sun.net.httpserver.HttpServer - Zero Dependencies)          |
|   Endpoints: /api/analyze, /api/batch, /api/health, Static /           |
+------------------------------------+------------------------------------+
                                     |
                                     v
+-------------------------------------------------------------------------+
|                  Lexical Tokenizer & Canonicalizer                     |
|   - Comment Stripper (//, /* */)                                       |
|   - Identifier Renaming Resistance ($V1, $V2, $V3...)                  |
|   - Literal Normalization ($NUM, $STR)                                 |
|   - Scope-Based Code Block Segmenter (Track brace depth { ... })       |
+------------------------------------+------------------------------------+
                                     | Normalized Tokens & Blocks
                                     v
+-------------------------------------------------------------------------+
|                    Core DSA Algorithms Engine Pipeline                  |
|                                                                         |
|  [CO2, CO6] RabinKarpRollingHash.java                                   |
|             - Polynomial Rolling Hash (Base 313, Mod 10^9+7)            |
|             - Winnowing Fingerprinting (Window w=4, k=8)                |
|             - Las Vegas Exact Verification                              |
|                                                                         |
|  [CO2]      KMPAlgorithm.java                                           |
|             - Longest Prefix-Suffix (LPS / pi) Table O(M)               |
|             - Linear Text Scanning O(N)                                 |
|                                                                         |
|  [CO2]      ZAlgorithm.java                                             |
|             - [L, R] Interval Window Linear Z-Array Computation O(N)    |
|                                                                         |
|  [CO2]      SuffixArrayLCP.java                                         |
|             - Prefix Doubling Suffix Array Construction O(N log^2 N)    |
|             - Kasai's Algorithm for LCP Array O(N)                      |
|             - Generalized Suffix Array Longest Common Substring (LCS)   |
|                                                                         |
|  [CO1, CO3] SequenceAlignmentDP.java                                    |
|             - Needleman-Wunsch Matrix Dynamic Programming               |
|             - Affine Match (+2), Type-Match (+1), Gap/Mismatch (-1)     |
|             - Full Traceback for Aligned Sequence Distance              |
|                                                                         |
|  [CO1, CO4] BipartiteBlockMatcher.java                                  |
|             - Function Block Bipartite Graph Formulation                |
|             - Edmonds-Karp / Augmenting Paths Maximum Matching          |
|                                                                         |
|  [IR]       VectorSimilarity.java                                       |
|             - Sublinear TF-IDF Frequency Vectors & Cosine Angle         |
+------------------------------------+------------------------------------+
                                     |
                                     v
+-------------------------------------------------------------------------+
|                Consensus Scoring & Audit Reporting Engine               |
|   - Weighted Composite Formula                                          |
|   - Confidence Verdict (CRITICAL > 75%, WARNING 40-75%, SAFE < 40%)    |
|   - Synchronized Line Range Extraction for Side-by-Side Diff View       |
|   - Execution Latency Microsecond Benchmarks                            |
+-------------------------------------------------------------------------+
```

---

## 3. Data Flow & Processing Pipeline

### Step 1: Ingestion & Lexical Tokenization
1. Raw source code is accepted via web upload, pasted text, or file path.
2. The `CodeTokenizer` scans the character stream:
   - Discards all comments while tracking physical source line numbers.
   - Categorizes characters into: `KEYWORD`, `IDENTIFIER`, `OPERATOR`, `LITERAL`, `DELIMITER`, `BLOCK_START`, `BLOCK_END`.
   - Variable renaming resistance: Maps user identifiers dynamically to canonical tokens: the first identifier becomes `$V1`, the second `$V2`, etc.
   - Code Block Extraction: Counts brace nesting depth to group functions into discrete `CodeBlock` objects with start and end line coordinates.

### Step 2: Algorithmic Evaluation
The normalized token sequence and code blocks are passed to the 7 core algorithms:
1. **Rabin-Karp Winnowing**: Generates rolling hashes of size $k=8$, selects minimum hashes within sliding windows $w=4$, and computes Dice-Sorensen fingerprint overlap.
2. **KMP Search**: Scans token sequences to measure percentage coverage of Candidate B that can be constructed from substrings of Candidate A.
3. **Z-Algorithm**: Evaluates common prefix expansion between concatenated token streams.
4. **Suffix Array + Kasai's LCP**: Builds generalized suffix array on $Tokens_A + \# + Tokens_B$, runs Kasai's algorithm, and extracts the longest shared continuous token clones.
5. **Needleman-Wunsch Sequence Alignment**: Constructs the dynamic programming scoring matrix and backtracks to calculate structural similarity.
6. **Maximum Bipartite Matching**: Constructs bipartite graph between functions of Candidate A and Candidate B. Runs augmenting path search to compute max cardinality matching.
7. **TF-IDF Cosine**: Measures token vocabulary distribution cosine similarity.

### Step 3: Composite Consensus & Benchmarking
The engine combines all metric signals using a calibrated weighted formula:
$$\text{Overall Score} = (0.25 \cdot \text{RK}) + (0.20 \cdot \text{SA}) + (0.20 \cdot \text{DP}) + (0.15 \cdot \text{BPM}) + (0.10 \cdot \text{TFIDF}) + (0.10 \cdot \text{KMP})$$

If both Rabin-Karp Winnowing and Suffix Array LCS exceed $85\%$, an override boost is applied to prevent masking by superficial surrounding boilerplate.

---

## 4. Class & Component Map

| Class | Package | Responsibility |
|---|---|---|
| `Token` | `tokenizer` | Stores token type, original token, normalized token, and source line number. |
| `CodeTokenizer` | `tokenizer` | Lexer, comment stripper, identifier normalizer, and block extractor. |
| `RabinKarpRollingHash` | `algorithms` | Polynomial rolling hash and Winnowing fingerprint generator (CO2, CO6). |
| `KMPAlgorithm` | `algorithms` | Knuth-Morris-Pratt pattern matcher and LPS table builder (CO2). |
| `ZAlgorithm` | `algorithms` | Linear-time Z-function pattern matcher (CO2). |
| `SuffixArrayLCP` | `algorithms` | Prefix-doubling Suffix Array and Kasai's LCP builder (CO2). |
| `SequenceAlignmentDP` | `algorithms` | Needleman-Wunsch matrix dynamic programming and Levenshtein edit distance (CO1, CO3). |
| `BipartiteBlockMatcher`| `algorithms` | Maximum bipartite matching on code blocks using network flow principles (CO1, CO4). |
| `VectorSimilarity` | `algorithms` | TF-IDF token vector space model and Cosine similarity. |
| `MatchBlock` | `model` | Represents an individual detected plagiarized line span. |
| `Submission` | `model` | Encapsulates candidate metadata, raw code, and tokenized structures. |
| `AnalysisReport` | `model` | Contains composite score, individual metrics, matched blocks, and JSON converter. |
| `BatchReport` | `model` | Stores $N \times N$ similarity matrix for whole classroom/batch analysis. |
| `SimilarityAnalyzer` | `engine` | Coordinates the end-to-end multi-algorithmic pipeline and benchmarks. |
| `HttpServerEngine` | `server` | Built-in Java HTTP server delivering web UI and REST API endpoints. |
| `Main` | `com.dsa.similarity` | CLI and Web Server master entry point. |
