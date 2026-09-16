# Project Guide

## Purpose

The analyzer helps review programming submissions for shared code patterns. It reports an overall similarity score together with the individual algorithm scores, matching source regions, and execution timings used to produce the result.

## Requirements

- JDK 11 or newer (`java` and `javac` available in the terminal)
- A current Chrome, Edge, Firefox, or Safari browser
- No third-party Java or JavaScript dependencies

## Start the Dashboard

From the repository root:

```bash
cd 04_Code
./run.sh
```

Open [http://localhost:8080](http://localhost:8080). To use another port, run `./run.sh 9090` and open `http://localhost:9090`.

## Build and Verify

```bash
cd 04_Code
./build.sh
```

The script compiles the Java files into `04_Code/bin` and runs the built-in algorithm checks.

## Compare Files from the CLI

```bash
cd 04_Code
java -cp bin com.dsa.similarity.Main --cli \
  sample_codes/submission1_original.java \
  sample_codes/submission2_obfuscated_plagiarized.java
```

## Web Workflow

1. Select a category or search for an algorithm.
2. Choose Student A and Student B.
3. Select **Load student comparison**.
4. Select **Run Multi-Algorithm Analysis**.
5. Inspect the overall score, individual metrics, matched blocks, and visual diff.

The editors also accept pasted source code and uploaded `.java`, `.c`, `.cpp`, and `.txt` files.

## Dataset

The predefined dataset is stored in `07_Data/catalog.js`. It defines 25 algorithms in four categories and generates four controlled student variants for each algorithm, producing 100 unique submissions.

## Documentation

Technical details are available in `03_Description`, including the system architecture, algorithm analysis, and complete user manual.