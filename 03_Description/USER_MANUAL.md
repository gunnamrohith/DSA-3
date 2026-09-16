# User & Operator Manual

## Document & Code Similarity Analyzer
### Course: Data Structures and Algorithms - 3 (25CS2103E)

---

## 1. Prerequisites
- **Operating System**: macOS, Linux, or Windows.
- **Java Runtime**: Java SE 11, 17, 21, or higher (`javac` and `java` commands in PATH).
- **Web Browser**: Google Chrome, Mozilla Firefox, Apple Safari, or Microsoft Edge.
- **External Dependencies**: **NONE** (Zero external libraries; runs completely offline using pure Java standard runtime and native HTML5/CSS3/JavaScript).

---

## 2. Quick Start Guide

### Option A: One-Click Web Dashboard (Recommended)
1. Open your terminal and navigate to the project folder:
   ```bash
   cd DSA-PROJECT/04_Code
   ```
2. Run the start script:
   ```bash
   ./run.sh
   ```
3. Open your web browser and navigate to:
   ```
   http://localhost:8080
   ```
4. The dashboard will load with preconfigured test cases ready to evaluate!

---

### Option B: Terminal CLI Pairwise Analysis
To compare two candidate files directly from the terminal:
```bash
cd DSA-PROJECT/04_Code
java -cp bin com.dsa.similarity.Main --cli sample_codes/submission1_original.java sample_codes/submission2_obfuscated_plagiarized.java
```

---

### Option C: Algorithmic Verification Self-Test
To verify all algorithms against known test oracles:
```bash
java -cp bin com.dsa.similarity.Main --test
```

---

## 3. Web Dashboard Features & Navigation

### Exam Dataset Selection
The landing screen contains 100 predefined submissions built from 25 DSA algorithms and four student implementation styles per algorithm.

1. Use **Search methods** to locate an algorithm across the complete dataset, or choose a category to browse 6-7 related algorithms.
2. Select the algorithm and choose different values for **Student A** and **Student B**.
3. Select **Load student comparison** to populate both editors.
4. Run the analysis to calculate similarity and inspect matching source blocks.
5. Select **Add all 4 students to batch** to create a focused pairwise matrix for the chosen exam problem.

### File Upload & Custom Code Entry
- Click the **"Upload File"** button in either editor to load any `.java`, `.c`, `.cpp`, or `.txt` file directly from your disk.
- Or simply paste or type custom code directly into the textarea.

### Running Analysis
- Click the cyan **"Run Multi-Algorithm Analysis"** button.
- The results section will animate into view.

### Interpreting Results
- **Overall Similarity Score**: Weighted aggregate percentage across all DSA techniques.
- **Verdict Badge**:
   - **CRITICAL: HIGH PLAGIARISM DETECTED** (Red, 70% or higher): Strong overlap across the comparison metrics.
   - **WARNING: MODERATE SIMILARITY** (Amber, 35% to 70%): Partial clone or significant shared blocks requiring manual inspection.
   - **SAFE: LOW SIMILARITY** (Green, below 35%): Independent solutions with incidental syntax overlap.
- **Algorithm Dimension Radar Chart**: Visualizes Rabin-Karp, KMP, Z-Algorithm, Suffix Array/LCP, and TF-IDF similarity scores.
- **Execution Latency Bar Chart**: Displays benchmark time in microseconds ($\mu s$) for each algorithm.
- **Synchronized Side-by-Side Diff Viewer**:
  - Left pane displays Candidate A; Right pane displays Candidate B.
  - Scrolling either pane synchronizes the opposite pane.
  - Plagiarized code spans are highlighted in transparent red.
- **Interactive Block List**: Click a detected match to scroll both source panes to the corresponding lines.

---

## 4. Troubleshooting & FAQ

**Q: Port 8080 is already in use by another application. How can I change the port?**
A: Specify a custom port when launching:
```bash
./run.sh 9090
```
Then visit `http://localhost:9090`.

**Q: How do I recompile the project after making code modifications?**
A: Run:
```bash
./build.sh
```
This automatically compiles all files in `src/` to `bin/` and runs the verification test suite.
