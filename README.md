# Document and Code Similarity Analyzer

> Data Structures and Algorithms - 3 (25CS2103E)<br>
> Department of Computer Science and Engineering

## Overview

This project is a zero-dependency Java application for comparing source-code submissions and identifying exact or structurally similar regions. It combines a Java analysis engine with a responsive HTML, CSS, and JavaScript dashboard.

The included dataset contains 100 predefined submissions: 25 DSA problems with four student-style implementations per problem. Users can search by method, filter by category, compare two submissions, and generate a four-student similarity matrix.

## Core Techniques

| Technique | Purpose |
| --- | --- |
| Rabin-Karp and winnowing | Detect shared fingerprints efficiently |
| KMP | Find exact token-pattern matches in linear time |
| Z-Algorithm | Measure prefix-based structural overlap |
| Suffix Array and Kasai LCP | Extract long common code regions |
| TF-IDF cosine similarity | Compare lexical distributions |

## Run the Project

Requirements: JDK 11 or newer and a modern web browser.

```bash
cd 04_Code
./run.sh
```

Open [http://localhost:8080](http://localhost:8080). Run `./build.sh` from the same directory to compile the Java sources and execute the self-tests.

## Repository Organization

| Folder | Contents |
| --- | --- |
| [01_Project_Title](01_Project_Title/) | Official project title |
| [02_Abstract](02_Abstract/) | Project abstract |
| [03_Description](03_Description/) | Architecture, algorithm analysis, and user manual |
| [04_Code](04_Code/) | Java engine, web application, scripts, and sample code |
| [05_Outputs](05_Outputs/) | Screenshots, reports, and result exports |
| [06_Readme](06_Readme/) | Detailed project and usage documentation |
| [07_Data](07_Data/) | Dataset of 100 predefined DSA submissions |
| [08_PPTs](08_PPTs/) | Presentation files |

## Dataset Workflow

1. Search for a DSA method or select a category.
2. Choose an algorithm and two different student implementations.
3. Load the pair and run the multi-algorithm analysis.
4. Review the score, matching blocks, charts, and execution timings.
5. Add all four solutions to the batch matrix for pairwise comparison.

See [06_Readme/README.md](06_Readme/README.md) for detailed usage and project notes.
