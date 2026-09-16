/**
 * Synchronized Side-by-Side Diff & Plagiarism Highlight Viewer.
 */
class DiffViewer {

  static render(containerIdA, containerIdB, codeA, codeB, matchingBlocks) {
    const paneA = document.getElementById(containerIdA);
    const paneB = document.getElementById(containerIdB);
    if (!paneA || !paneB) return;

    paneA.innerHTML = '';
    paneB.innerHTML = '';

    const linesA = (codeA || '').split('\n');
    const linesB = (codeB || '').split('\n');

    // Build lookup sets for lines involved in plagiarism
    const plagLinesA = new Set();
    const plagLinesB = new Set();

    (matchingBlocks || []).forEach(m => {
      for (let l = m.startLineA; l <= m.endLineA; l++) plagLinesA.add(l);
      for (let l = m.startLineB; l <= m.endLineB; l++) plagLinesB.add(l);
    });

    // Populate Pane A
    linesA.forEach((lineText, idx) => {
      const lineNum = idx + 1;
      const row = document.createElement('div');
      row.className = 'diff-line' + (plagLinesA.has(lineNum) ? ' plagiarized' : '');
      row.id = `line-a-${lineNum}`;

      const numSpan = document.createElement('span');
      numSpan.className = 'line-num';
      numSpan.textContent = lineNum;

      const codeSpan = document.createElement('span');
      codeSpan.className = 'line-code';
      codeSpan.textContent = lineText || ' ';

      row.appendChild(numSpan);
      row.appendChild(codeSpan);
      paneA.appendChild(row);
    });

    // Populate Pane B
    linesB.forEach((lineText, idx) => {
      const lineNum = idx + 1;
      const row = document.createElement('div');
      row.className = 'diff-line' + (plagLinesB.has(lineNum) ? ' plagiarized' : '');
      row.id = `line-b-${lineNum}`;

      const numSpan = document.createElement('span');
      numSpan.className = 'line-num';
      numSpan.textContent = lineNum;

      const codeSpan = document.createElement('span');
      codeSpan.className = 'line-code';
      codeSpan.textContent = lineText || ' ';

      row.appendChild(numSpan);
      row.appendChild(codeSpan);
      paneB.appendChild(row);
    });

    // Synchronize scrolling between panes
    let isSyncingA = false;
    let isSyncingB = false;

    paneA.onscroll = () => {
      if (!isSyncingA) {
        isSyncingB = true;
        paneB.scrollTop = paneA.scrollTop;
      }
      isSyncingA = false;
    };

    paneB.onscroll = () => {
      if (!isSyncingB) {
        isSyncingA = true;
        paneA.scrollTop = paneB.scrollTop;
      }
      isSyncingB = false;
    };
  }

  static scrollToLines(lineA, lineB) {
    const elA = document.getElementById(`line-a-${lineA}`);
    const elB = document.getElementById(`line-b-${lineB}`);
    if (elA) elA.scrollIntoView({ behavior: 'smooth', block: 'center' });
    if (elB) elB.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }
}
