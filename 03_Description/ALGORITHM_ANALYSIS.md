# Algorithmic Analysis: Linear-Time String Algorithms & Suffix Structures

## Course: Data Structures and Algorithms - 3 (25CS2103E)
### Department of Computer Science and Engineering

---

## 1. Focused Scope: Syllabus Course Outcome CO2 & CO6
This version of the analyzer focuses on the core algorithms completed in the course:
- **CO2**: Linear-time string algorithms (Rabin-Karp with rolling hash, Knuth-Morris-Pratt, Z-Algorithm) and suffix-based structures (Suffix Array, Kasai's LCP Array).
- **CO6**: Randomized algorithms (Las Vegas prime rolling hash verification).
- **IR**: Word frequency distribution (TF-IDF Cosine Similarity).

---

## 2. Core String Algorithms Detailed Analysis

### 2.1 Rabin-Karp Rolling Hash & Winnowing Fingerprinting (CO2, CO6)
#### Mathematical Formulation
The polynomial rolling hash of a $k$-gram string $S[0 \dots k-1]$ is computed as:
$$H(S) = \left( \sum_{i=0}^{k-1} S[i] \cdot B^{k-1-i} \right) \pmod M$$
Where:
- Base $B = 313$ (a prime larger than standard ASCII).
- Modulo $M = 10^9 + 7$ (a large prime minimizing collision probability).

When shifting the window by 1 character, discarding $c_{out}$ and ingesting $c_{in}$:
$$H_{new} = \left( \left( H_{old} - c_{out} \cdot B^{k-1} \pmod M + M \right) \cdot B + c_{in} \right) \pmod M$$
This rolling update takes strict $O(1)$ constant time!

#### Winnowing Algorithm
To compress the number of stored hashes, the Winnowing algorithm selects the rightmost minimum hash in every sliding window of size $w$:
1. Slide a window of size $w$ over the calculated rolling hashes.
2. Select the minimum hash value.
3. Record its fingerprint.
- **Guarantee**: Any shared substring between two submissions of length $\ge t = k + w - 1$ is mathematically guaranteed to generate at least one matching fingerprint.
- **Verification**: Exact string verification (`regionMatches`) is performed on hash matches, guaranteeing zero false positives (Las Vegas principle).

---

### 2.2 Knuth-Morris-Pratt (KMP) Algorithm (CO2)
#### Longest Prefix-Suffix (LPS / $\pi$) Table
The LPS table stores the length of the longest proper prefix of $P[0 \dots i]$ that is also a suffix of $P[0 \dots i]$:
$$\text{LPS}[i] = \max \{ k : k < i+1 \text{ and } P[0 \dots k-1] = P[i-k+1 \dots i] \}$$

#### Proof of $O(N + M)$ Time Complexity
- Building the LPS array requires at most $2M$ operations because the prefix length pointer increments at most once per step and decrements on mismatch without falling below 0.
- Scanning the text of length $N$ advances the text pointer monotonically from $0$ to $N$ with zero backtracking. Total operations cannot exceed $2N$.
- **Worst-case Time**: Strict $O(N + M)$.
- **Space Complexity**: $O(M)$ auxiliary memory for the LPS table.

---

### 2.3 Z-Algorithm / Z-Function (CO2)
#### Definition
For a string $S$ of length $N$, $Z[i]$ is the length of the longest substring starting from $S[i]$ that is also a prefix of $S$:
$$Z[i] = \max \{ k : S[i \dots i+k-1] = S[0 \dots k-1] \}$$

#### $O(N)$ Linear Time Proof
The algorithm maintains an interval $[L, R]$ where $R$ is the farthest right boundary reached so far:
- If $i > R$: Compute $Z[i]$ by comparing characters directly starting from index 0. Update $[L, R]$.
- If $i \le R$: Let $k = i - L$.
  - If $Z[k] < R - i + 1$, then $Z[i] = Z[k]$ immediately (no character comparisons!).
  - If $Z[k] \ge R - i + 1$, compare characters starting from $R+1$ and advance $R$.
Because $R$ advances monotonically from $0$ to $N$, the algorithm operates in $O(N)$ linear time.

---

### 2.4 Suffix Array & Kasai's LCP Array (CO2)
#### Suffix Array (Prefix Doubling)
The Suffix Array ($SA$) contains the starting indices of all suffixes of string $S$ sorted in lexicographical order:
- Constructed in $O(N \log^2 N)$ using prefix doubling.

#### Kasai's Algorithm for LCP Array in $O(N)$ Time
The LCP array stores the length of the Longest Common Prefix between adjacent suffixes in the sorted Suffix Array:
$$\text{LCP}[i] = \text{LCP}(\text{Suffix}(SA[i]), \text{Suffix}(SA[i-1]))$$
- **Kasai's Lemma**: $\text{LCP}(\text{rank}[i+1]) \ge \text{LCP}(\text{rank}[i]) - 1$.
- Because the common prefix length $h$ decreases by at most $1$ per step and cannot exceed $N$, the number of character comparisons is bounded by $2N = O(N)$.

#### Finding Longest Common Substrings (LCS)
1. Concatenate Candidate A and Candidate B with a unique delimiter: $T = Code_A + '\#' + Code_B$.
2. Compute $SA$ and $LCP$ for $T$.
3. Any adjacent pair $SA[i]$ and $SA[i-1]$ where one suffix belongs to $Code_A$ and the other to $Code_B$ represents a shared cloned substring.
4. The maximum $\text{LCP}[i]$ among such pairs gives the exact length and position of the **Longest Common Substring**!

---

## 3. Complexity Summary Table

| Algorithm | Course Outcome | Time Complexity | Space Complexity |
|---|---|---|---|
| **Rabin-Karp Rolling Hash** | CO2, CO6 | $O(N + M)$ | $O(1)$ |
| **Winnowing Fingerprinting** | CO2, CO6 | $O(N)$ | $O(N / w)$ |
| **Knuth-Morris-Pratt (KMP)** | CO2 | $O(N + M)$ | $O(M)$ |
| **Z-Algorithm** | CO2 | $O(N)$ | $O(N)$ |
| **Suffix Array (Doubling)** | CO2 | $O(N \log^2 N)$ | $O(N)$ |
| **Kasai's LCP Array** | CO2 | $O(N)$ | $O(N)$ |
| **TF-IDF Word Cosine** | IR | $O(N)$ | $O(\text{Vocabulary})$ |
