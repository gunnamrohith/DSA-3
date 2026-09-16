/**
 * Canvas-based Visualizations (Zero CDN dependencies).
 * Renders Radar chart for algorithm dimensions and Bar chart for execution benchmarks.
 */
class VisualCharts {

  static drawRadar(canvasId, labels, values) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const width = canvas.width;
    const height = canvas.height;
    const centerX = width / 2;
    const centerY = height / 2;
    const radius = Math.min(centerX, centerY) - 35;
    const numAxes = labels.length;
    const angleStep = (Math.PI * 2) / numAxes;

    ctx.clearRect(0, 0, width, height);

    // Draw concentric polygon grid
    const levels = 4;
    ctx.strokeStyle = '#2d3748';
    ctx.lineWidth = 1;
    for (let l = 1; l <= levels; l++) {
      const r = (radius / levels) * l;
      ctx.beginPath();
      for (let i = 0; i < numAxes; i++) {
        const angle = i * angleStep - Math.PI / 2;
        const x = centerX + r * Math.cos(angle);
        const y = centerY + r * Math.sin(angle);
        if (i === 0) ctx.moveTo(x, y);
        else ctx.lineTo(x, y);
      }
      ctx.closePath();
      ctx.stroke();
    }

    // Draw axes & labels
    ctx.font = '10px Inter, sans-serif';
    ctx.fillStyle = '#9ca3af';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    for (let i = 0; i < numAxes; i++) {
      const angle = i * angleStep - Math.PI / 2;
      const x = centerX + radius * Math.cos(angle);
      const y = centerY + radius * Math.sin(angle);

      ctx.beginPath();
      ctx.moveTo(centerX, centerY);
      ctx.lineTo(x, y);
      ctx.strokeStyle = '#2d3748';
      ctx.stroke();

      const labelX = centerX + (radius + 20) * Math.cos(angle);
      const labelY = centerY + (radius + 20) * Math.sin(angle);
      ctx.fillText(labels[i], labelX, labelY);
    }

    // Draw filled data polygon
    ctx.beginPath();
    ctx.fillStyle = 'rgba(6, 182, 212, 0.35)';
    ctx.strokeStyle = '#06b6d4';
    ctx.lineWidth = 2;

    for (let i = 0; i < numAxes; i++) {
      const val = Math.min(100, Math.max(0, values[i]));
      const r = (radius * (val / 100));
      const angle = i * angleStep - Math.PI / 2;
      const x = centerX + r * Math.cos(angle);
      const y = centerY + r * Math.sin(angle);

      if (i === 0) ctx.moveTo(x, y);
      else ctx.lineTo(x, y);
    }
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    // Draw data points
    for (let i = 0; i < numAxes; i++) {
      const val = Math.min(100, Math.max(0, values[i]));
      const r = (radius * (val / 100));
      const angle = i * angleStep - Math.PI / 2;
      const x = centerX + r * Math.cos(angle);
      const y = centerY + r * Math.sin(angle);

      ctx.beginPath();
      ctx.arc(x, y, 4, 0, Math.PI * 2);
      ctx.fillStyle = '#38bdf8';
      ctx.fill();
    }
  }

  static drawLatencyBar(canvasId, benchmarks) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const width = canvas.width;
    const height = canvas.height;

    ctx.clearRect(0, 0, width, height);

    const keys = Object.keys(benchmarks);
    if (keys.length === 0) return;

    let maxVal = 1;
    for (const k of keys) {
      if (benchmarks[k] > maxVal) maxVal = benchmarks[k];
    }

    const paddingLeft = 140;
    const paddingRight = 60;
    const barHeight = 22;
    const gap = 12;
    const startY = 20;

    ctx.font = '11px Inter, sans-serif';
    ctx.textBaseline = 'middle';

    keys.forEach((key, idx) => {
      const y = startY + idx * (barHeight + gap);
      const val = benchmarks[key];
      const barWidth = ((width - paddingLeft - paddingRight) * (val / maxVal));

      // Label
      ctx.fillStyle = '#9ca3af';
      ctx.textAlign = 'right';
      ctx.fillText(key, paddingLeft - 10, y + barHeight / 2);

      // Bar Background
      ctx.fillStyle = '#1e293b';
      ctx.fillRect(paddingLeft, y, width - paddingLeft - paddingRight, barHeight);

      // Active Bar
      const grad = ctx.createLinearGradient(paddingLeft, 0, paddingLeft + barWidth, 0);
      grad.addColorStop(0, '#3b82f6');
      grad.addColorStop(1, '#8b5cf6');
      ctx.fillStyle = grad;
      ctx.fillRect(paddingLeft, y, barWidth, barHeight);

      // Value text
      ctx.fillStyle = '#e2e8f0';
      ctx.textAlign = 'left';
      ctx.fillText(`${val} µs`, paddingLeft + barWidth + 8, y + barHeight / 2);
    });
  }
}
