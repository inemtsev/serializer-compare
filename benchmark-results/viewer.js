const COLORS = {
  kotlinx: '#4fc3f7',
  jackson2: '#ffb74d',
  jackson3: '#81c784'
};

const SHORT_NAMES = {
  'kotlinx': 'kotlinx.serialization',
  'jackson2': 'Jackson 2.x',
  'jackson3': 'Jackson 3.x'
};

let throughputChart = null;
let allocationChart = null;

async function fetchJSON(url, bustCache = false) {
  if (bustCache) url += (url.includes('?') ? '&' : '?') + '_=' + Date.now();
  const r = await fetch(url);
  if (!r.ok) throw new Error(`HTTP ${r.status}: ${url}`);
  return r.json();
}

function getRunId() {
  const params = new URLSearchParams(location.search);
  return params.get('run');
}

function setRunId(runId) {
  const url = new URL(location);
  url.searchParams.set('run', runId);
  history.replaceState(null, '', url);
}

function showWarning(msg) {
  const el = document.getElementById('warning');
  el.textContent = msg;
  el.style.display = 'block';
}

function hideWarning() {
  document.getElementById('warning').style.display = 'none';
}

function shortBenchmark(benchmark) {
  const parts = benchmark.split('.');
  const full = parts[parts.length - 1];
  const match = full.match(/^(serialize|deserialize)(Small|Large)Object$/);
  if (match) return `${match[1]} ${match[2].toLowerCase()}`;
  return full;
}

function extractSerializer(filename) {
  if (filename.includes('kotlinx')) return 'kotlinx';
  if (filename.includes('jackson2')) return 'jackson2';
  if (filename.includes('jackson3')) return 'jackson3';
  return null;
}

function processRunData(runDir, jsonFiles, runInfo) {
  const benchmarks = {};
  const serializerFiles = {};

  for (const file of jsonFiles) {
    const ser = extractSerializer(file);
    if (!ser) continue;
    serializerFiles[ser] = file;
  }

  const loadPromises = Object.entries(serializerFiles).map(async ([ser, file]) => {
    const data = await fetchJSON(`${runDir}/${file}`);
    for (const entry of data) {
      const key = shortBenchmark(entry.benchmark);
      if (!benchmarks[key]) benchmarks[key] = {};
      benchmarks[key][ser] = entry;
    }
  });

  return Promise.all(loadPromises).then(() => ({ benchmarks, runInfo }));
}

function clearResults() {
  if (throughputChart) { throughputChart.destroy(); throughputChart = null; }
  if (allocationChart) { allocationChart.destroy(); allocationChart = null; }
  document.getElementById('tableContainer').innerHTML = '';
  document.getElementById('currentRun').textContent = '';
}

async function loadRuns() {
  const runs = await fetchJSON('runs.json', true);
  const select = document.getElementById('runSelect');
  select.innerHTML = '';

  const runId = getRunId();
  runs.sort((a, b) => (a.runId < b.runId ? 1 : -1));
  let selectedRun = runs.find(r => r.runId === runId);
  let isFallback = false;

  if (runs.length === 0) {
    showWarning('No benchmark runs found. Run ./run-all-benchmarks.sh first.');
    clearResults();
    return { runs: [], selectedRun: null };
  }

  if (runId && !selectedRun) {
    showWarning(`Run "${runId}" not found. Falling back to latest.`);
    selectedRun = runs[0];
    isFallback = true;
  }

  if (!selectedRun) {
    selectedRun = runs[0];
  }

  for (const run of runs) {
    const opt = document.createElement('option');
    opt.value = run.runId;
    opt.textContent = `${run.runId} — ${run.timestamp || ''} ${run.note || ''}`.trim();
    if (selectedRun && run.runId === selectedRun.runId) opt.selected = true;
    select.appendChild(opt);
  }

  if (selectedRun) {
    setRunId(selectedRun.runId);
    await loadRunData(selectedRun.runId, selectedRun, isFallback);
  }

  return { runs, selectedRun };
}

async function loadRunData(runId, runInfo, isFallback = false) {
  if (!isFallback) hideWarning();
  document.getElementById('currentRun').textContent = `(run: ${runId})`;

  const jsonFiles = ['kotlinx-results.json', 'jackson2-results.json', 'jackson3-results.json'];
  let runDir = runId;
  let data;

  try {
    const result = await processRunData(runDir, jsonFiles, runInfo);
    data = result.benchmarks;
  } catch {
    const result = await processRunData(`../benchmark-results/${runDir}`, jsonFiles, runInfo);
    data = result.benchmarks;
  }

  renderCharts(data);
  renderTable(data);
}

function renderCharts(data) {
  const benchmarkLabels = Object.keys(data);
  const serializers = ['kotlinx', 'jackson2', 'jackson3'];

  const tpDatasets = serializers.map(ser => ({
    label: SHORT_NAMES[ser],
    data: benchmarkLabels.map(b => {
      const entry = data[b][ser];
      return entry ? entry.primaryMetric.score : null;
    }),
    backgroundColor: COLORS[ser] + '88',
    borderColor: COLORS[ser],
    borderWidth: 2
  }));

  const allocDatasets = serializers.map(ser => ({
    label: SHORT_NAMES[ser],
    data: benchmarkLabels.map(b => {
      const entry = data[b][ser];
      if (!entry) return null;
      const alloc = entry.secondaryMetrics?.['gc.alloc.rate.norm'];
      return alloc ? alloc.score : null;
    }),
    backgroundColor: COLORS[ser] + '88',
    borderColor: COLORS[ser],
    borderWidth: 2
  }));

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { labels: { color: '#ccc' } } },
    scales: {
      x: { ticks: { color: '#ccc' }, grid: { color: '#333' } },
      y: { ticks: { color: '#ccc' }, grid: { color: '#333' } }
    }
  };

  if (throughputChart) throughputChart.destroy();
  if (allocationChart) allocationChart.destroy();

  throughputChart = new Chart(document.getElementById('throughputChart'), {
    type: 'bar',
    data: { labels: benchmarkLabels, datasets: tpDatasets },
    options: { ...chartOptions }
  });

  allocationChart = new Chart(document.getElementById('allocationChart'), {
    type: 'bar',
    data: { labels: benchmarkLabels, datasets: allocDatasets },
    options: { ...chartOptions }
  });
}

function renderTable(data) {
  const container = document.getElementById('tableContainer');
  const serializers = ['kotlinx', 'jackson2', 'jackson3'];
  const benchmarks = Object.keys(data);

  let html = '<table><thead><tr><th>Benchmark</th>';
  // Throughput columns first
  for (const ser of serializers) {
    html += `<th>${SHORT_NAMES[ser]} (ops/ms)</th>`;
  }
  // Then allocation columns
  for (const ser of serializers) {
    html += `<th>${SHORT_NAMES[ser]} alloc (B/op)</th>`;
  }
  html += '</tr></thead><tbody>';

  for (const b of benchmarks) {
    html += `<tr><td>${b}</td>`;
    // Throughput values
    for (const ser of serializers) {
      const entry = data[b][ser];
      if (entry) {
        const tp = entry.primaryMetric.score.toFixed(2);
        html += `<td>${tp}</td>`;
      } else {
        html += '<td>—</td>';
      }
    }
    // Allocation values
    for (const ser of serializers) {
      const entry = data[b][ser];
      if (entry) {
        const alloc = entry.secondaryMetrics?.['gc.alloc.rate.norm'];
        const allocStr = alloc ? alloc.score.toFixed(2) : 'N/A';
        html += `<td>${allocStr}</td>`;
      } else {
        html += '<td>—</td>';
      }
    }
    html += '</tr>';
  }
  html += '</tbody></table>';
  container.innerHTML = html;
}

document.getElementById('runSelect').addEventListener('change', async (e) => {
  const runs = await fetchJSON('runs.json', true);
  const run = runs.find(r => r.runId === e.target.value);
  if (run) {
    setRunId(run.runId);
    await loadRunData(run.runId, run);
  }
});

document.getElementById('refreshBtn').addEventListener('click', async () => {
  try {
    await loadRuns();
  } catch (e) {
    showWarning(`Failed to refresh: ${e.message}`);
  }
});

loadRuns();
