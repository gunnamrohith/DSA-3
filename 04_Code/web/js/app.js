/**
 * Modern Application Controller for DSA Similarity Analyzer.
 * Fully Real & Transparent Architecture:
 * - 100 predefined student submissions across 25 DSA problems
 * - Dynamic Batch Exam Matrix computed via real Java REST API (/api/batch)
 * - Mathematical Proof & Live Calculation Breakdown
 * - Canvas Radar & Microsecond Latency Bar Charts
 * - Synchronized side-by-side diff viewer
 */

// Legacy samples retained only as source examples; the UI uses CODE_CATALOG.
const LEGACY_CODES = {
  bubble_sort_orig: {
    name: "1. Bubble Sort (Standard Java)",
    code: `public class BubbleSort {
    public static void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    // Swap elements
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
}`
  },

  bubble_sort_copy: {
    name: "2. Bubble Sort (Plagiarized / Renamed Variables)",
    code: `// Plagiarized copy with modified spacing & comments
public class BubbleSortCopy {
    public static void sort(int[] arr) {
        int len = arr.length;
        for (int p = 0; p < len - 1; p++) {
            for (int q = 0; q < len - p - 1; q++) {
                if (arr[q] > arr[q + 1]) {
                    // Perform exchange
                    int swapVal = arr[q];
                    arr[q] = arr[q + 1];
                    arr[q + 1] = swapVal;
                }
            }
        }
    }
}`
  },

  linear_search_orig: {
    name: "3. Linear Search (Original)",
    code: `public class LinearSearch {
    public static int search(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i; // Return index of match
            }
        }
        return -1; // Target not found
    }
}`
  },

  linear_search_copy: {
    name: "4. Linear Search (Copied with Minor Tweaks)",
    code: `// Copied linear search implementation
public class SearchEngine {
    public static int search(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i; // Return index of match
            }
        }
        return -1; // Target not found
    }
}`
  },

  binary_search_iter: {
    name: "5. Binary Search (Iterative While Loop)",
    code: `public class BinarySearchIterative {
    public static int binarySearch(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }
}`
  },

  binary_search_rec: {
    name: "6. Binary Search (Recursive Function)",
    code: `public class BinarySearchRecursive {
    public static int search(int[] arr, int low, int high, int target) {
        if (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == target) return mid;
            if (arr[mid] > target) {
                return search(arr, low, mid - 1, target);
            }
            return search(arr, mid + 1, high, target);
        }
        return -1;
    }
}`
  },

  factorial_rec: {
    name: "7. Factorial (Recursive Formula)",
    code: `public class FactorialRec {
    public static long factorial(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }
}`
  },

  factorial_iter: {
    name: "8. Factorial (Iterative For Loop)",
    code: `public class FactorialIter {
    public static long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result = result * i;
        }
        return result;
    }
}`
  },

  palindrome_check: {
    name: "9. Palindrome String Checker (Two Pointers)",
    code: `public class PalindromeChecker {
    public static boolean isPalindrome(String s) {
        int left = 0;
        int right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}`
  },

  fibonacci_series: {
    name: "10. Fibonacci Generator (Iterative Array)",
    code: `public class FibonacciSeries {
    public static int[] generate(int n) {
        if (n <= 0) return new int[0];
        int[] fib = new int[n];
        fib[0] = 0;
        if (n > 1) fib[1] = 1;
        for (int i = 2; i < n; i++) {
            fib[i] = fib[i - 1] + fib[i - 2];
        }
        return fib;
    }
}`
  }
};

const DEFAULT_ALGORITHM_ID = 'bubble-sort';
let selectedAlgorithmId = DEFAULT_ALGORITHM_ID;
let batchSubmissions = getAlgorithmSubmissions(DEFAULT_ALGORITHM_ID).map(toBatchSubmission);

// Application Initialization
document.addEventListener('DOMContentLoaded', () => {
  initTheme();
  initCatalogBrowser();
  setupFileUploads();
  renderBatchCandidateChips();
  loadSelectedComparison();
});

function getAlgorithms() {
  return ALGORITHM_DEFINITIONS.map(([id, name, category]) => ({ id, name, category }));
}

function getAlgorithmSubmissions(algorithmId) {
  return CODE_CATALOG.filter(item => item.algorithmId === algorithmId);
}

function toBatchSubmission(item) {
  return { name: `${item.studentName} - ${item.algorithm}`, code: item.code };
}

function initCatalogBrowser() {
  const categoryFilter = document.getElementById('categoryFilter');
  const searchInput = document.getElementById('catalogSearch');
  const algorithmFilter = document.getElementById('algorithmFilter');
  const categories = [...new Set(getAlgorithms().map(item => item.category))];

  categoryFilter.innerHTML = categories.map(category => `<option value="${category}">${category}</option>`).join('');
  categoryFilter.addEventListener('change', refreshAlgorithmOptions);
  searchInput.addEventListener('input', refreshAlgorithmOptions);
  algorithmFilter.addEventListener('change', () => selectAlgorithm(algorithmFilter.value));

  populateStudentSelectors();
  refreshAlgorithmOptions();
}

function populateStudentSelectors() {
  ['studentFilterA', 'studentFilterB'].forEach((id, selectorIndex) => {
    const select = document.getElementById(id);
    select.innerHTML = STUDENT_PROFILES.map(student =>
      `<option value="${student.id}">${student.name} - ${student.style}</option>`
    ).join('');
    select.value = selectorIndex === 0 ? STUDENT_PROFILES[0].id : STUDENT_PROFILES[1].id;
  });
}

function refreshAlgorithmOptions() {
  const category = document.getElementById('categoryFilter').value;
  const query = document.getElementById('catalogSearch').value.trim().toLowerCase();
  const matches = getAlgorithms().filter(item =>
    query
      ? `${item.name} ${item.category}`.toLowerCase().includes(query)
      : item.category === category
  );
  const algorithmFilter = document.getElementById('algorithmFilter');
  const results = document.getElementById('algorithmResults');

  algorithmFilter.innerHTML = matches.length
    ? matches.map(item => `<option value="${item.id}">${item.name}</option>`).join('')
    : '<option value="">No matching methods</option>';
  algorithmFilter.disabled = matches.length === 0;
  results.innerHTML = matches.length
  const visibleMatches = matches.slice(0, 7);
  results.innerHTML = visibleMatches.length
    ? visibleMatches.map(item => `<button type="button" class="algorithm-result${item.id === selectedAlgorithmId ? ' active' : ''}" data-algorithm-id="${item.id}" onclick="selectAlgorithm('${item.id}')"><span>${item.name}</span><small>${item.category}</small></button>`).join('')
    : '<span class="empty-filter-state">No methods match this search in the selected category.</span>';

  document.getElementById('catalogCount').textContent = CODE_CATALOG.length;

  if (matches.length) {
    const nextId = matches.some(item => item.id === selectedAlgorithmId) ? selectedAlgorithmId : matches[0].id;
    selectAlgorithm(nextId);
  }
}

function selectAlgorithm(algorithmId) {
  if (!algorithmId) return;
  selectedAlgorithmId = algorithmId;
  document.getElementById('algorithmFilter').value = algorithmId;
  document.querySelectorAll('.algorithm-result').forEach(button => {
    button.classList.toggle('active', button.dataset.algorithmId === algorithmId);
  });
}

function loadSelectedComparison() {
  const studentA = document.getElementById('studentFilterA').value;
  const studentB = document.getElementById('studentFilterB').value;
  if (studentA === studentB) {
    alert('Choose two different students for comparison.');
    return;
  }

  const submissions = getAlgorithmSubmissions(selectedAlgorithmId);
  const submissionA = submissions.find(item => item.studentId === studentA);
  const submissionB = submissions.find(item => item.studentId === studentB);
  if (!submissionA || !submissionB) return;

  loadSubmissionIntoPane(submissionA, 'A');
  loadSubmissionIntoPane(submissionB, 'B');
  document.getElementById('resultsSection').classList.remove('active');
}

function loadSubmissionIntoPane(submission, pane) {
  document.getElementById(`candName${pane}`).value = `${submission.studentName} - ${submission.algorithm}`;
  document.getElementById(`code${pane}`).value = submission.code;
  document.getElementById(`selectionMeta${pane}`).textContent = `${submission.category} / ${submission.style}`;
}

function addSelectedAlgorithmToBatch() {
  batchSubmissions = getAlgorithmSubmissions(selectedAlgorithmId).map(toBatchSubmission);
  renderBatchCandidateChips();
  switchTab('tab-batch');
}

// Theme Management
function initTheme() {
  const saved = localStorage.getItem('dsa_theme') || 'dark';
  document.documentElement.setAttribute('data-theme', saved);
  updateThemeBtn(saved);
}

function toggleTheme() {
  const current = document.documentElement.getAttribute('data-theme') || 'dark';
  const next = current === 'dark' ? 'light' : 'dark';
  document.documentElement.setAttribute('data-theme', next);
  localStorage.setItem('dsa_theme', next);
  updateThemeBtn(next);
}

function updateThemeBtn(theme) {
  const btn = document.getElementById('themeToggleBtn');
  if (btn) {
    btn.innerHTML = theme === 'dark' ? '☀️ Light Mode' : '🌙 Dark Mode';
  }
}

// Navigation Tabs
function switchTab(tabId) {
  document.querySelectorAll('.nav-tab-btn').forEach(b => b.classList.remove('active'));
  document.querySelectorAll('.tab-view').forEach(v => v.classList.remove('active'));

  const activeBtn = document.querySelector(`[data-tab="${tabId}"]`);
  const activeView = document.getElementById(tabId);
  if (activeBtn) activeBtn.classList.add('active');
  if (activeView) activeView.classList.add('active');
}

function clearEditors() {
  document.getElementById('codeA').value = '';
  document.getElementById('codeB').value = '';
  document.getElementById('candNameA').value = 'Candidate 1';
  document.getElementById('candNameB').value = 'Candidate 2';
  document.getElementById('selectionMetaA').textContent = 'Custom or uploaded submission';
  document.getElementById('selectionMetaB').textContent = 'Custom or uploaded submission';
  document.getElementById('resultsSection').classList.remove('active');
}

function setupFileUploads() {
  setupSingleUpload('fileInputA', 'codeA', 'candNameA', 'selectionMetaA');
  setupSingleUpload('fileInputB', 'codeB', 'candNameB', 'selectionMetaB');
}

function setupSingleUpload(fileInputId, textareaId, nameInputId, metaId) {
  const input = document.getElementById(fileInputId);
  if (!input) return;
  input.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file) {
      document.getElementById(nameInputId).value = file.name;
      document.getElementById(metaId).textContent = 'Uploaded file';
      const reader = new FileReader();
      reader.onload = (ev) => {
        document.getElementById(textareaId).value = ev.target.result;
      };
      reader.readAsText(file);
    }
  });
}

// Run Similarity Analysis API (Pairwise)
async function runAnalysis() {
  const codeA = document.getElementById('codeA').value;
  const codeB = document.getElementById('codeB').value;
  const nameA = document.getElementById('candNameA').value || 'Candidate 1';
  const nameB = document.getElementById('candNameB').value || 'Candidate 2';

  if (!codeA.trim() || !codeB.trim()) {
    alert('Please enter or select code for both Candidate 1 and Candidate 2.');
    return;
  }

  const btn = document.getElementById('btnRun');
  btn.innerHTML = `Analyzing Algorithms in Java...`;
  btn.disabled = true;

  try {
    const res = await fetch('/api/analyze', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        candidateA: nameA,
        codeA: codeA,
        candidateB: nameB,
        codeB: codeB
      })
    });

    if (!res.ok) throw new Error(`HTTP Error ${res.status}`);
    const report = await res.json();
    displayResults(report, codeA, codeB);
  } catch (err) {
    console.error('API Error:', err);
    alert('Could not connect to Java HTTP server. Ensure ./run.sh is running on port 8080.');
  } finally {
    btn.innerHTML = `
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <polygon points="5 3 19 12 5 21 5 3"></polygon>
      </svg>
      Run Multi-Algorithm Analysis
    `;
    btn.disabled = false;
  }
}

// Render Results & Mathematical Proof
function displayResults(report, codeA, codeB) {
  const resultsSection = document.getElementById('resultsSection');
  resultsSection.classList.add('active');

  // Overall Score & Gauge
  const scoreVal = document.getElementById('overallScoreVal');
  const verdictBadge = document.getElementById('verdictBadge');
  const verdictTitle = document.getElementById('verdictTitle');
  const scoreCircle = document.getElementById('scoreCircle');

  scoreVal.textContent = `${report.overallScore.toFixed(1)}%`;
  scoreCircle.style.setProperty('--percent', report.overallScore);

  verdictBadge.className = 'status-badge';
  if (report.overallScore >= 70.0) {
    verdictBadge.classList.add('critical');
    verdictBadge.textContent = 'CRITICAL: HIGH PLAGIARISM DETECTED';
    verdictTitle.textContent = 'Substantial Shared Code Logic Across Both Submissions';
  } else if (report.overallScore >= 35.0) {
    verdictBadge.classList.add('warning');
    verdictBadge.textContent = 'WARNING: MODERATE SIMILARITY (SUSPICIOUS)';
    verdictTitle.textContent = 'Partial Substring Overlap Identified — Requires Manual Review';
  } else {
    verdictBadge.classList.add('safe');
    verdictBadge.textContent = 'SAFE: LOW SIMILARITY (INDEPENDENT WORK)';
    verdictTitle.textContent = 'Submissions Exhibit Independent Algorithms and Structures';
  }

  // Stat Cards
  document.getElementById('valRabinKarp').textContent = `${report.rabinKarpWinnowingScore.toFixed(1)}%`;
  document.getElementById('valKMP').textContent = `${report.kmpScore.toFixed(1)}%`;
  document.getElementById('valZAlgo').textContent = `${report.zAlgorithmScore.toFixed(1)}%`;
  document.getElementById('valSuffixLCP').textContent = `${report.suffixArrayLcpScore.toFixed(1)}%`;
  document.getElementById('detailSuffixLCP').textContent = `LCS Length: ${report.longestCommonSubstringLength} chars`;
  document.getElementById('valTfidf').textContent = `${report.tfidfCosineScore.toFixed(1)}%`;

  // Render Step-by-Step Mathematical Calculation Proof
  const rk = report.rabinKarpWinnowingScore;
  const sa = report.suffixArrayLcpScore;
  const kmp = report.kmpScore;
  const z = report.zAlgorithmScore;
  const tf = report.tfidfCosineScore;

  const formulaText = `// EXACT FORMULA COMPUTED BY JAVA BACKEND:
Overall_Score = (0.35 * RK_Winnowing) + (0.25 * Suffix_LCP) + (0.20 * KMP) + (0.10 * Z_Algorithm) + (0.10 * TF_IDF)
              = (0.35 * ${rk.toFixed(2)}%) + (0.25 * ${sa.toFixed(2)}%) + (0.20 * ${kmp.toFixed(2)}%) + (0.10 * ${z.toFixed(2)}%) + (0.10 * ${tf.toFixed(2)}%)
              = ${(0.35 * rk).toFixed(2)} + ${(0.25 * sa).toFixed(2)} + ${(0.20 * kmp).toFixed(2)} + ${(0.10 * z).toFixed(2)} + ${(0.10 * tf).toFixed(2)}
              = ${report.overallScore.toFixed(2)}%`;

  const formulaEl = document.getElementById('formulaProofBox');
  if (formulaEl) formulaEl.textContent = formulaText;

  // Draw Radar Chart
  const radarLabels = ['Rabin-Karp', 'KMP', 'Z-Algorithm', 'Suffix LCP', 'Word TF-IDF'];
  const radarValues = [rk, kmp, z, sa, tf];
  VisualCharts.drawRadar('radarCanvas', radarLabels, radarValues);

  // Draw Latency Bar Chart
  if (report.benchmarks) {
    VisualCharts.drawLatencyBar('latencyCanvas', report.benchmarks);
  }

  // Render Diff Viewer
  DiffViewer.render('diffPaneA', 'diffPaneB', codeA, codeB, report.matchingBlocks);

  // Matched Clones List
  const list = document.getElementById('matchedBlocksList');
  list.innerHTML = '';
  if (!report.matchingBlocks || report.matchingBlocks.length === 0) {
    list.innerHTML = '<div style="color:var(--text-muted); font-size:0.85rem; padding:8px;">No major cloned code spans detected.</div>';
  } else {
    report.matchingBlocks.forEach((m, idx) => {
      const item = document.createElement('div');
      item.className = 'clone-pill-item';
      item.innerHTML = `
        <div>
          <strong>Substring #${idx + 1}</strong> [${m.algorithm}] &nbsp;
          <span style="color:var(--primary-accent)">Candidate 1: L${m.startLineA}-L${m.endLineA}</span> &harr;
          <span style="color:var(--accent-purple)">Candidate 2: L${m.startLineB}-L${m.endLineB}</span>
          <span style="color:var(--text-muted); margin-left:8px;">(${m.tokenCount} characters)</span>
        </div>
        <div style="font-weight:700; color:var(--primary-accent);">${m.similarity.toFixed(1)}% match</div>
      `;
      item.onclick = () => {
        DiffViewer.scrollToLines(m.startLineA, m.startLineB);
      };
      list.appendChild(item);
    });
  }

  resultsSection.scrollIntoView({ behavior: 'smooth' });
}

// ============================================================================
// REAL CLASSROOM BATCH SCANNER (Tab 4)
// ============================================================================
function renderBatchCandidateChips() {
  const container = document.getElementById('batchCandidatesList');
  if (!container) return;

  container.innerHTML = '';
  batchSubmissions.forEach((sub, idx) => {
    const chip = document.createElement('div');
    chip.className = 'candidate-chip';
    chip.innerHTML = `
      <div>
        <span>${sub.name}</span>
        <small>${sub.code.split('\n').length} lines of code</small>
      </div>
      <button style="background:none; border:none; color:var(--danger); cursor:pointer; font-size:1.1rem;" onclick="removeBatchCandidate(${idx})" title="Remove">&times;</button>
    `;
    container.appendChild(chip);
  });
}

function removeBatchCandidate(index) {
  if (batchSubmissions.length <= 2) {
    alert('At least 2 submissions are required to compute a comparison matrix.');
    return;
  }
  batchSubmissions.splice(index, 1);
  renderBatchCandidateChips();
}

function addCustomBatchCandidate() {
  const name = prompt("Enter Candidate Name (e.g. 'Rohan - QuickSort'):");
  if (!name) return;
  const code = prompt("Paste Candidate Code:", "public class Solution { ... }");
  if (!code) return;
  batchSubmissions.push({ name, code });
  renderBatchCandidateChips();
}

function resetDefaultBatch() {
  batchSubmissions = getAlgorithmSubmissions(selectedAlgorithmId).map(toBatchSubmission);
  renderBatchCandidateChips();
}

// Execute the real Java Batch API
async function executeBatchScan() {
  const tableContainer = document.getElementById('batchMatrixTableContainer');
  const btn = document.getElementById('btnRunBatch');
  if (!tableContainer || !btn) return;

  btn.textContent = 'Computing Pairwise Matrix in Java...';
  btn.disabled = true;

  try {
    const res = await fetch('/api/batch', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(batchSubmissions)
    });

    if (!res.ok) throw new Error(`HTTP Error ${res.status}`);
    const data = await res.json();

    renderDynamicBatchTable(data.candidates, data.matrix, data.reports);
  } catch (err) {
    console.error('Batch error:', err);
    alert('Batch evaluation failed. Ensure ./run.sh is active.');
  } finally {
    btn.textContent = 'Execute Real-Time Batch API Call';
    btn.disabled = false;
  }
}

function renderDynamicBatchTable(candidates, matrix, reports) {
  const container = document.getElementById('batchMatrixTableContainer');
  if (!container) return;

  let html = '<table class="batch-table"><thead><tr><th>Candidate</th>';
  candidates.forEach(c => {
    html += `<th title="${c}">${c.split(' ')[0]}</th>`;
  });
  html += '</tr></thead><tbody>';

  for (let i = 0; i < candidates.length; i++) {
    html += `<tr><th style="text-align:left;">${candidates[i]}</th>`;
    for (let j = 0; j < candidates.length; j++) {
      const val = matrix[i][j];
      let cls = 'heat-low';
      if (i === j) {
        cls = 'heat-self';
        html += `<td class="${cls}">-</td>`;
      } else {
        if (val >= 70) cls = 'heat-high';
        else if (val >= 35) cls = 'heat-med';
        html += `<td class="${cls} matrix-cell-clickable" title="Click to view full comparison of ${candidates[i]} vs ${candidates[j]}" onclick="inspectBatchPair(${i}, ${j})">${val}%</td>`;
      }
    }
    html += '</tr>';
  }
  html += '</tbody></table>';
  container.innerHTML = html;
}

// Click on ANY matrix cell to inspect that exact pair in Tab 1 Diff Viewer!
function inspectBatchPair(idxA, idxB) {
  if (idxA === idxB) return;
  const subA = batchSubmissions[idxA];
  const subB = batchSubmissions[idxB];

  document.getElementById('candNameA').value = subA.name;
  document.getElementById('codeA').value = subA.code;
  document.getElementById('selectionMetaA').textContent = 'Loaded from batch matrix';

  document.getElementById('candNameB').value = subB.name;
  document.getElementById('codeB').value = subB.code;
  document.getElementById('selectionMetaB').textContent = 'Loaded from batch matrix';

  // Switch to Tab 1 and trigger live analysis
  switchTab('tab-analyzer');
  runAnalysis();
}
