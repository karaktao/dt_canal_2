<template>
  <el-card class="info-lock-card" style="width:100%; padding:0px;border: 0;">
    <div class="header-row">
      <div class="title">- Average Passage Time -</div>
    </div>

    <div v-if="error" class="error-msg">{{ error }}</div>

    <!-- 三行四列：type=3 简洁卡片 -->
    <div class="cards-row">
      <div
        class="lock-card"
        v-for="(s, isrs) in stats"
        :key="isrs"
        role="group"
        :aria-label="`Lock ${s.name} average times`"
      >
        <div class="card-head">
          <div class="loc-name">{{ s.name }}</div>
        </div>

        <div class="card-row">
          <div class="row-value">
            <div class="value">{{ formatDisplay(s.up) }}</div>
            <div class="row-label">min</div>
          </div>
        </div>

        <div class="card-row">
          <div class="row-value">
            <div class="value">{{ formatDisplay(s.down) }}</div>
            <div class="row-label">min</div>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== Type=0 Chart 区块 ===== -->
    <div class="type0-section" style="margin-top:-20px; padding:0px 0;">
      <div class="type0-header" style="display:flex; gap:60px; align-items:center; margin-bottom:8px;">
        <div class="title" style="font-weight:700">- Passages per day -</div>
        <el-select class="transparent-select" v-model="selectedIsrs" placeholder="Select measurement point" size="small" style="min-width:240px,">
          <el-option
            v-for="(l) in locksList"
            :key="l.isrs"
            :label="l.name"
            :value="l.isrs"
          />
        </el-select>
      </div>

      <div class="chart-wrap" ref="chartDom" style="height:100px; width:100%;"></div>


    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick, onBeforeUnmount } from "vue";
import axios from "axios";
import * as echarts from "echarts";

/* ---------- props / defaults ---------- */
const props = defineProps({
  locks: { type: Array, default: null }, // 可传入覆盖，格式 [{ isrs, name }]
  apiBase: { type: String, default: "/api/euris/visuris/api/Locks/GetStatistic" },
  autoFetch: { type: Boolean, default: true },
  unit: { type: String, default: "" },
  debug: { type: Boolean, default: false },
});

/* ---------- default lock list ---------- */
const DEFAULT_LOCKS = [
  { isrs: "NLEFE000810975700033", name: "Noordkolk Eefde" },
  { isrs: "NLEFE000810975500031", name: "Zuidkolk Eefde" },
  { isrs: "NLDEL000810975900362", name: "Sluiskolk Delden" },
  { isrs: "NLHGL000814584400451", name: "Sluiskolk Hengelo" },
];

const locksList = computed(() => (Array.isArray(props.locks) && props.locks.length ? props.locks : DEFAULT_LOCKS));

/* ---------- type=3 existing state ---------- */
const TYPE3 = 3;
const loading = ref(false);
const error = ref(null);
const lockStore = reactive({});
const expanded = reactive({});

/* ---------- helpers from your code (normalize/mean/parse/extract) ---------- */
function normalizeForMatch(s) {
  if (!s) return "";
  let t = String(s).toLowerCase();
  t = t.replace(/lockage basin:?/gi, "");
  t = t.replace(/sluis/gi, "");
  t = t.replace(/[^a-z0-9\s]/gi, " ");
  t = t.replace(/\s+/g, " ").trim();
  return t;
}
function matchScore(normalizedCategory, normalizedLockName) {
  if (!normalizedCategory || !normalizedLockName) return 999;
  if (normalizedCategory === normalizedLockName) return 0;
  if (normalizedCategory.includes(normalizedLockName) || normalizedLockName.includes(normalizedCategory)) return 1;
  const tokens = normalizedLockName.split(" ").filter(Boolean);
  let matched = 0;
  for (const tk of tokens) if (normalizedCategory.includes(tk)) matched++;
  if (matched > 0) return 10 - matched;
  return 100;
}
function mean(arr) {
  if (!Array.isArray(arr)) return null;
  const nums = arr.filter((v) => typeof v === "number" && isFinite(v));
  if (!nums.length) return null;
  return nums.reduce((a, b) => a + b, 0) / nums.length;
}
function parseValues(json) {
  if (!json) return null;
  if (json.values && typeof json.values === "object") {
    return { values: json.values, typeMessage: json.typeMessage || null, category: json.category || null };
  }
  return { values: { _raw: json }, typeMessage: json.typeMessage || null };
}
function extractUpDown(parsed) {
  if (!parsed || !parsed.values) return { up: null, down: null };
  const v = parsed.values;
  const keys = Object.keys(v);
  const upKey = keys.find((k) => k.toLowerCase().includes("up")) || keys.find((k) => k.toLowerCase().includes("upstream"));
  const downKey = keys.find((k) => k.toLowerCase().includes("down")) || keys.find((k) => k.toLowerCase().includes("downstream"));
  if (upKey || downKey) {
    const upVal = upKey ? v[upKey] : null;
    const downVal = downKey ? v[downKey] : null;
    const up = Array.isArray(upVal) ? mean(upVal) : typeof upVal === "number" ? upVal : null;
    const down = Array.isArray(downVal) ? mean(downVal) : typeof downVal === "number" ? downVal : null;
    return { up, down };
  }
  const candidates = [];
  for (const k of keys) {
    const val = v[k];
    if (Array.isArray(val)) candidates.push(mean(val));
    else if (typeof val === "number") candidates.push(val);
  }
  if (candidates.length >= 2) return { up: candidates[0], down: candidates[1] };
  if (candidates.length === 1) return { up: candidates[0], down: null };
  if (Array.isArray(v)) {
    const m = mean(v);
    return { up: m, down: m };
  }
  return { up: null, down: null };
}

/* ---------- applyMultiCategoryResponse & fetchOne & fetchAll (same approach as current) ---------- */
function applyMultiCategoryResponse(data, respStatus) {
  if (!data || !Array.isArray(data.categories) || !data.values) return false;
  const cats = data.categories;
  const vals = data.values;
  const n = cats.length;
  const lockEntries = Object.keys(lockStore).map((isrs) => {
    return { isrs, name: lockStore[isrs].name || '', norm: normalizeForMatch(lockStore[isrs].name || '') };
  });

  for (let i = 0; i < n; i++) {
    const cat = cats[i] || '';
    const catLower = String(cat).toLowerCase();
    let matchedIsrs = null;
    if (catLower.includes('zuid')) {
      const found = lockEntries.find(l => (l.name || '').toLowerCase().includes('zuid'));
      if (found) matchedIsrs = found.isrs;
    } else if (catLower.includes('noord')) {
      const found = lockEntries.find(l => (l.name || '').toLowerCase().includes('noord'));
      if (found) matchedIsrs = found.isrs;
    }
    if (!matchedIsrs) {
      const catNorm = normalizeForMatch(cat || '');
      let best = null;
      let bestScore = 9999;
      for (const L of lockEntries) {
        const sc = matchScore(catNorm, L.norm);
        if (sc < bestScore) {
          bestScore = sc;
          best = L;
        }
      }
      if (best && bestScore <= 50) matchedIsrs = best.isrs;
    }
    if (!matchedIsrs) {
      if (props.debug) console.debug('[InfoLock] 未匹配 category，跳过：', cat);
      continue;
    }
    const singleValues = {};
    for (const k of Object.keys(vals)) {
      const arr = vals[k];
      if (Array.isArray(arr)) singleValues[k] = arr[i] !== undefined ? arr[i] : null;
      else singleValues[k] = arr;
    }
    lockStore[matchedIsrs].raw = {
      rawObj: { type: data.type, category: cat, values: singleValues, typeMessage: data.typeMessage || null },
      rawText: JSON.stringify({ type: data.type, category: cat, values: singleValues, typeMessage: data.typeMessage || null }, null, 2),
      status: respStatus ?? 200,
      error: null,
      source: 'split',
    };
    lockStore[matchedIsrs].parsed = { values: singleValues, typeMessage: data.typeMessage || null, category: cat, source: 'split' };
  }
  return true;
}

async function fetchOne(isrs) {
  try {
    const url = `${props.apiBase}?isrs=${encodeURIComponent(isrs)}&type=${encodeURIComponent(TYPE3)}`;
    if (props.debug) console.debug("[InfoLock] GET", url);
    const resp = await axios.get(encodeURI(url), { validateStatus: () => true });
    const data = resp.data ?? null;
    if (data && Array.isArray(data.categories) && data.categories.length > 1 && data.values) {
      const applied = applyMultiCategoryResponse(data, resp.status);
      if (applied) {
        return resp;
      }
    }
    const existing = lockStore[isrs] && lockStore[isrs].raw;
    if (existing && existing.source === 'split') {
      if (props.debug) console.debug(`[InfoLock] 保留 split 来源数据，不覆盖 ${isrs}（来自单条请求）`);
      return resp;
    }
    lockStore[isrs].raw = {
      rawObj: data,
      rawText: data ? JSON.stringify(data, null, 2) : null,
      status: resp.status,
      error: resp.status >= 400 ? `HTTP ${resp.status}` : null,
      source: 'direct',
    };
    lockStore[isrs].parsed = parseValues(data);
    return resp;
  } catch (e) {
    lockStore[isrs].raw = { rawObj: null, rawText: null, status: null, error: String(e), source: 'direct' };
    lockStore[isrs].parsed = null;
    return null;
  }
}

async function fetchAll() {
  loading.value = true;
  error.value = null;
  try {
    initStore();
    const keys = Object.keys(lockStore);
    const tasks = keys.map((k) => fetchOne(k));
    await Promise.allSettled(tasks);
  } catch (e) {
    console.error("[InfoLock] fetchAll error", e);
    error.value = String(e);
  } finally {
    loading.value = false;
  }
}

/* ---------- stats computed ---------- */
const stats = computed(() => {
  const out = {};
  for (const isrs of Object.keys(lockStore)) {
    const info = lockStore[isrs];
    const name = info?.name ?? isrs;
    let up = null;
    let down = null;
    if (info?.parsed) {
      const ex = extractUpDown(info.parsed);
      up = ex.up;
      down = ex.down;
    } else if (info?.raw && info.raw.rawObj) {
      const ex = extractUpDown(parseValues(info.raw.rawObj));
      up = ex.up;
      down = ex.down;
    }
    out[isrs] = { name, up, down };
  }
  return out;
});

/* ---------- UI helpers ---------- */
function formatDisplay(v) {
  if (v === null || v === undefined || Number.isNaN(v)) return "—";
  const n = Number(v);
  if (!isFinite(n)) return "—";
  if (Math.abs(n) >= 1000) return Math.round(n).toString();
  const digits = Math.abs(n) >= 10 ? 1 : 2;
  return n.toFixed(digits);
}
function toggleJson(isrs) { expanded[isrs] = !expanded[isrs]; }
function isExpanded(isrs) { return !!expanded[isrs]; }
function rawJson(isrs) { const r = lockStore[isrs] && lockStore[isrs].raw; return r && r.rawText ? r.rawText : "no data"; }

/* ---------- init ---------- */
function initStore() {
  const list = locksList.value;
  for (const k of Object.keys(lockStore)) delete lockStore[k];
  for (const l of list) {
    lockStore[l.isrs] = { name: l.name || l.isrs, raw: null, parsed: null };
    expanded[l.isrs] = false;
  }
}

/* ---------- ========== TYPE=0 (Average amount of passages per day) ========== ---------- */
const TYPE0 = 0;
const type0Store = reactive({}); // type0Store[isrs] = { categories: [], values: {upstream,downstream}, rawText, status }
const type0Loading = ref(false);
const selectedIsrs = ref(locksList.value.length ? locksList.value[0].isrs : null);

let chartInstance = null;
const chartDom = ref(null);
let showUp = ref(true);
let showDown = ref(true);

async function fetchType0One(isrs) {
  try {
    const url = `${props.apiBase}?isrs=${encodeURIComponent(isrs)}&type=${encodeURIComponent(TYPE0)}`;
    if (props.debug) console.debug("[InfoLock:type0] GET", url);
    const resp = await axios.get(encodeURI(url), { validateStatus: () => true });
    const data = resp.data ?? null;
    if (data && data.categories && data.values) {
      // store arrays as-is
      type0Store[isrs] = {
        categories: Array.isArray(data.categories) ? data.categories : [],
        values: data.values || {},
        rawText: JSON.stringify(data, null, 2),
        status: resp.status,
      };
    } else {
      type0Store[isrs] = { categories: [], values: {}, rawText: data ? JSON.stringify(data, null, 2) : null, status: resp.status };
    }
    return resp;
  } catch (e) {
    type0Store[isrs] = { categories: [], values: {}, rawText: null, status: null, error: String(e) };
    return null;
  }
}

async function fetchType0All() {
  type0Loading.value = true;
  try {
    const keys = locksList.value.map((l) => l.isrs);
    const tasks = keys.map((k) => fetchType0One(k));
    await Promise.allSettled(tasks);
    await nextTick();
    renderChart();
  } catch (e) {
    console.error(e);
  } finally {
    type0Loading.value = false;
  }
}

const type0HasData = computed(() => {
  const s = selectedIsrs.value;
  if (!s || !type0Store[s]) return false;
  const cat = type0Store[s].categories;
  const vals = type0Store[s].values;
  return Array.isArray(cat) && cat.length && vals && (Array.isArray(vals.upstream) || Array.isArray(vals.downstream));
});
// 将原始日期字符串解析为 Date 对象（若无法解析返回 null）
// 支持格式： DD/MM/YYYY, MM/DD/YYYY, YYYY-MM-DD, ISO 2025-09-10T..., 以及 "10 Sep 2025" 等
function parseDate(raw) {
  if (!raw) return null;
  const s = String(raw).trim();

  // ISO-like: 2025-09-10 or 2025-09-10T...
  let m = s.match(/^(\d{4})[\/\-](\d{1,2})[\/\-](\d{1,2})/);
  if (m) {
    const y = Number(m[1]), mo = Number(m[2]) - 1, d = Number(m[3]);
    return new Date(Date.UTC(y, mo, d));
  }

  // common D/M/Y or M/D/Y using slashes or dashes (ambiguous)
  m = s.match(/^(\d{1,2})[\/\-](\d{1,2})[\/\-](\d{2,4})$/);
  if (m) {
    const a = Number(m[1]), b = Number(m[2]), c = Number(m[3]);
    // 如果第一段是大于12的数，很可能是 day (DD/MM/YYYY)
    if (a > 12) {
      const day = a, month = b - 1, year = c < 100 ? 2000 + c : c;
      return new Date(Date.UTC(year, month, day));
    }
    // 否则可能是 MM/DD/YYYY 或 DD/MM/YYYY — 假定输入为 DD/MM/YYYY（欧洲格式）
    // 若你确知 API 为 MM/DD/YYYY，请改此处为 month=a-1, day=b
    const day = a, month = b - 1, year = c < 100 ? 2000 + c : c;
    return new Date(Date.UTC(year, month, day));
  }

  // english textual: "10 Sep 2025" or "Sep 10 2025"
  const months = {
    jan: 0, feb: 1, mar: 2, apr: 3, may: 4, jun: 5,
    jul: 6, aug: 7, sep: 8, oct: 9, nov: 10, dec: 11
  };
  const parts = s.replace(/[,]/g, "").split(/\s+/);
  if (parts.length >= 3) {
    // try find day & month & year
    let day = null, month = null, year = null;
    for (const p of parts) {
      if (!day && /^\d{1,2}$/.test(p)) day = Number(p);
      const key = p.slice(0,3).toLowerCase();
      if (!month && months.hasOwnProperty(key)) month = months[key];
      if (!year && /^\d{4}$/.test(p)) year = Number(p);
    }
    if (day !== null && month !== null && year !== null) {
      return new Date(Date.UTC(year, month, day));
    }
  }

  // fallback: try Date.parse, may work for some locales
  const dt = Date.parse(s);
  if (!isNaN(dt)) return new Date(dt);

  return null;
}

// 把原始类别格式化为 "DD-MM"（用于 x 轴展示）
function formatDateLabel(raw) {
  const d = parseDate(raw);
  if (!d) {
    // 尝试用原字符串的前两段做简单截取
    const parts = String(raw || "").split(/[\s\/\-\.]+/).filter(Boolean);
    if (parts.length >= 2) return `${parts[0].padStart(2,"0")}-${parts[1].padStart(2,"0")}`;
    return String(raw || "");
  }
  const day = String(d.getUTCDate()).padStart(2, "0");
  const month = String(d.getUTCMonth() + 1).padStart(2, "0");
  return `${day}-${month}`;
}

// 用下面的函数替换你现在的 buildOptionFor 实现
function buildOptionFor(isrs) {
  const d = type0Store[isrs];
  const origCategories = (d && Array.isArray(d.categories)) ? d.categories.slice() : [];
  const upstreamRaw = (d && d.values && Array.isArray(d.values.upstream)) ? d.values.upstream.slice() : [];
  const downstreamRaw = (d && d.values && Array.isArray(d.values.downstream)) ? d.values.downstream.slice() : [];

  // 构建索引数组并解析日期
  const indexed = origCategories.map((c, idx) => {
    const dt = parseDate(c);
    return { idx, raw: c, date: dt };
  });

  // 如果解析失败（所有 date 都为 null），则保留原序列（不排序）
  const anyDate = indexed.some(i => i.date instanceof Date && !isNaN(i.date));
  let ordered = indexed;
  if (anyDate) {
    ordered = indexed.slice().sort((a, b) => {
      const ta = a.date ? a.date.getTime() : 0;
      const tb = b.date ? b.date.getTime() : 0;
      return ta - tb; // 升序：旧 -> 新
    });
  } else {
    // 如果无法解析日期，也尝试按字符串自然顺序（以防 API 给的顺序反向）
    ordered = indexed.slice().sort((a, b) => String(a.raw).localeCompare(String(b.raw)));
  }

  // 按 ordered 顺序重建 xLabels / upstream / downstream
  const xLabels = ordered.map(o => formatDateLabel(o.raw));
  const fullDates = ordered.map(o => o.raw);
  const upstream = ordered.map(o => (upstreamRaw[o.idx] !== undefined ? upstreamRaw[o.idx] : null));
  const downstream = ordered.map(o => (downstreamRaw[o.idx] !== undefined ? downstreamRaw[o.idx] : null));

  return {
  color: ['#3B82F6', '#10B981'], // Legend/series 默认颜色（Upstream, Downstream）
  tooltip: {
    trigger: "axis",
    textStyle: { fontSize: 10 },
    formatter: function (params) {
      const idx = params && params.length ? params[0].dataIndex : 0;
      const fullDate = fullDates[idx] ?? "";
      let html = `<div style="font-size:13px;">${fullDate}</div>`;
      params.forEach(p => {
        const name = p.seriesName;
        const val = (p.value === null || p.value === undefined) ? "—" : p.value;
        html += `<div style="margin-top:4px">${name}: <b>${val}</b></div>`;
      });
      return html;
    }
  },
  legend: { data: ["Upstream", "Downstream"], top: 6 },
  grid: { left: "0%", right: "4%", bottom: "8%", top: "20%", height: "80%", containLabel: true },
  xAxis: {
    type: "category",
    data: xLabels,
    axisLabel: { rotate: 0 },
    boundaryGap: false,
    lineStyle: { width: 1.5, color: '#3B82F6' },
  },
  yAxis: {
    type: "value",
    axisLabel: { formatter: "{value}" },
    lineStyle: { width: 1.5, color: '#10B981' },
  },
  series: [
    {
      name: "Upstream",
      type: "line",
      smooth: true,
      showSymbol: true,
      data: upstream,
      lineStyle: { width: 1.5, color: '#409eff' },   // ← Upstream 线颜色
      itemStyle: { color: '#3B82F6' },              // ← 数据点颜色
    },
    {
      name: "Downstream",
      type: "line",
      smooth: true,
      showSymbol: true,
      data: downstream,
      lineStyle: { width: 1.5, color: '#91CC75' },   // ← Downstream 线颜色
      itemStyle: { color: '#91CC75' },
    }
  ],
};

}


function initChart() {
  if (!chartDom.value) return;
  chartInstance = echarts.init(chartDom.value);
  window.addEventListener("resize", resizeChart);
}

function resizeChart() { if (chartInstance) chartInstance.resize(); }

function renderChart() {
  if (!chartInstance || !selectedIsrs.value) return;
  const opt = buildOptionFor(selectedIsrs.value);
  chartInstance.clear();
  chartInstance.setOption(opt);
}

watch(selectedIsrs, () => { nextTick().then(() => renderChart()); });
watch(type0Store, () => { nextTick().then(() => renderChart()); }, { deep: true });

function toggleSeries() {
  showUp.value = !showUp.value;
  showDown.value = !showDown.value;
  renderChart();
}

/* ---------- lifecycle ---------- */
onMounted(() => {
  initStore();
  if (props.autoFetch) {
    fetchAll();
    fetchType0All();
  }
  initChart();
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeChart);
  if (chartInstance) { chartInstance.dispose(); chartInstance = null; }
});
</script>

<style scoped>
/* 与 InfoWaterLevel 保持一致的卡片格局与视觉风格 */
.info-lock-card {
  padding: 0;
  background: transparent;
  box-shadow: none;
}
.header-row {
  margin-top: -10px;
  margin-left: -10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0px;
}
.title {
  font-weight: 600;
  font-size: 13px;
}
.error-msg {
  color: var(--el-color-danger, #f56c6c);
  margin-bottom: 8px;
}

/* 四列布局（在窄屏时会换行） */
.cards-row {
  display: grid;
  margin-left: -20px;
  grid-template-columns: repeat(4, 1fr);
  gap: 0px;
}

/* 单个闸门卡片：三行结构 */
.lock-card {
  background: transparent;
  padding: 10px;
  border-radius: 6px;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}
.card-head {
  font-size: 12.5px;
  font-weight: 500;
  color: #454a57;
  margin-bottom: 8px;
}
.loc-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.card-row {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  padding: 4px 0;
}
.row-label {
  font-size: 12px;
  color: #6b7280;
}
.row-value {
  display: flex;
  align-items: baseline;
  gap: 6px;
}
.value {
  font-size: 20px;
  font-weight: 800;
  line-height: 1;
  color: #3c424be0;
}

/* type0 chart area */
.type0-section { margin-top: 12px; padding: 8px 12px; }
.type0-header { display:flex; align-items:center; gap:12px; margin-left: -10px; white-space: nowrap; }
.chart-wrap { border-radius:6px; background: #ffffff00; box-shadow: none; margin-top: -10px;  }

/* responsive */
@media (max-width: 1200px) {
  .cards-row { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 640px) {
  .cards-row { grid-template-columns: 1fr; }
}
</style>
