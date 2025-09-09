
<script setup>
import { ref, onMounted, computed } from "vue";
import axios from "axios";

/* ----- 配置 ----- */
const levelCodes = ["EEFD", "WESTV", "LOBI"];
const locationMap = {
  EEFD: "Eefde-beneden(EEFD)",
  WESTV: "Westervoort-IJsseldijkerw(WESTV)",
  LOBI: "Lobith(LOBI)",
  WEST: "Westervoort(WEST)",
};
const names = {
  EEFD: "Eefde",
  WESTV: "Westervoort",
  LOBI: "Lobith",
  WEST: "Westervoort",
  KAUB: "Kaub",
};
const cardToDischargeSource = { EEFD: null, WESTV: "WEST", LOBI: "LOBI" };

const API_PATH = "/api/waterinfo/api/chart/get";
const MAP_LEVEL = "waterhoogte";
const MAP_DISCHARGE = "waterafvoer";
const VALUES_PARAM = "-6,3";

/* Kaub (PegelOnline) */
const KAUB_STATION_ID = "1d26e504-7f9e-480a-b52c-5932be6549ab";
const KAUB_LEVEL_URL = `https://www.pegelonline.wsv.de/webservices/rest-api/v2/stations/${KAUB_STATION_ID}/W/currentmeasurement.json`;
const KAUB_DISCHARGE_URL = `https://www.pegelonline.wsv.de/webservices/rest-api/v2/stations/${KAUB_STATION_ID}/Q/currentmeasurement.json`;

/* Lobith -48,48 特别请求（你要的 URL） */
const LOBITH_48_VALUES = "-48,48";
const LOBITH_48_URL = `${API_PATH}?mapType=${MAP_LEVEL}&locationCodes=${locationMap.LOBI}&values=${LOBITH_48_VALUES}`;

/* ----- 响应数据状态 ----- */
const loading = ref(false);

const levelValues = ref({ EEFD: null, WESTV: null, LOBI: null });
const levelTimes = ref({ EEFD: null, WESTV: null, LOBI: null });

const dischargeValues = ref({ WEST: null, LOBI: null });
const dischargeTimes = ref({ WEST: null, LOBI: null });

const kaubLevel = ref({ value: null, timestamp: null, status: null });
const kaubDischarge = ref({ value: null, timestamp: null });

/* 新：解析后的对象（方便做表格/图表） */
const lobithChart48Obj = ref(null);

/* 这里保存 Lobith -48,48 的原始 JSON 字符串（格式化后） */
const lobithChart48 = ref(null);

/* 滚动容器引用 */
const scrollRef = ref(null);

// 用于渲染的一行四个城市顺序（可调整）
const displayCodes = ["EEFD", "WESTV", "LOBI", "KAUB"];

// 从已有显示函数复用并统一输出（字符串，不带单位）
function getLevelDisplay(code) {
  if (code === "KAUB") return displayKaubLevel(); // 你的 displayKaubLevel 已存在
  // 其他站点用 displayLevel（返回字符串或 '—'）
  return displayLevel(code);
}

function getDischargeDisplay(code) {
  if (code === "KAUB") return displayKaubDischarge(); // 已存在
  return displayDischargeForRow(code);
}

function getTimeDisplay(code) {
  if (code === "KAUB") return kaubLevelTimeDisplay(); // 或 kaubDischargeTimeDisplay() 根据需要
  // 其他站点尝试使用 levelTimeDisplay 或 dischargeTimeDisplayForRow
  // 优先显示水位时间
  const t = levelTimeDisplay(code);
  if (t && t !== "no data") return t;
  // fallback to discharge
  const td = dischargeTimeDisplayForRow(code);
  return td || "no data";
}

/* ----- 通用解析函数 ----- */
function parseLatestMeasuredFromChart(data) {
  if (!data || !Array.isArray(data.series)) return null;
  let latest = { dateTime: null, value: null };
  for (const s of data.series) {
    if (s.isPrediction) continue;
    if (!Array.isArray(s.data)) continue;
    for (const d of s.data) {
      if (d.value === undefined || d.value === null) continue;
      const dt = d.dateTime || d.date || null;
      if (!dt) continue;
      if (!latest.dateTime || dt > latest.dateTime) {
        latest.dateTime = dt;
        latest.value = d.value;
      }
    }
  }
  return latest.value !== null
    ? { value: latest.value, dateTime: latest.dateTime }
    : null;
}

/* ----- 请求函数 ----- */
async function fetchChartByKey(key, mapType, values = VALUES_PARAM) {
  const locStr = locationMap[key] || key;
  const fullUrl = `${API_PATH}?mapType=${mapType}&locationCodes=${locStr}&values=${values}`;

  try {
    const resp = await axios.get(encodeURI(fullUrl), {
      headers: { Accept: "application/json" },
      validateStatus: () => true,
    });
    if (resp.status !== 200) {
      return null;
    }
    return resp.data;
  } catch (err) {
    console.error(`[fetchChartByKey] ${key} ${mapType} error`, err);
    return null;
  }
}

async function fetchKaubLevel() {
  try {
    const resp = await axios.get(KAUB_LEVEL_URL, {
      headers: { Accept: "application/json" },
      validateStatus: () => true,
    });
    if (resp.status !== 200 || !resp.data) {
      kaubLevel.value = { value: null, timestamp: null, status: null };
      return;
    }
    const data = resp.data;
    const ts = data.timestamp || data.time || null;
    const val =
      data.value !== undefined
        ? data.value
        : data.currentValue !== undefined
        ? data.currentValue
        : null;
    const status = data.status || data.quality || data.flag || null;
    kaubLevel.value = { value: val, timestamp: ts, status };
  } catch (err) {
    console.error("[Kaub] level error", err);
    kaubLevel.value = { value: null, timestamp: null, status: null };
  }
}

async function fetchKaubDischarge() {
  try {
    const resp = await axios.get(KAUB_DISCHARGE_URL, {
      headers: { Accept: "application/json" },
      validateStatus: () => true,
    });
    if (resp.status !== 200 || !resp.data) {
      kaubDischarge.value = { value: null, timestamp: null };
      return;
    }
    const data = resp.data;
    const ts = data.timestamp || data.time || null;
    const val =
      data.value !== undefined
        ? data.value
        : data.currentValue !== undefined
        ? data.currentValue
        : null;
    kaubDischarge.value = { value: val, timestamp: ts };
  } catch (err) {
    console.error("[Kaub] discharge error", err);
    kaubDischarge.value = { value: null, timestamp: null };
  }
}

/* ----- Lobith -48,48 原始数据请求 ----- */
async function fetchLobith48Raw() {
  try {
    const resp = await axios.get(encodeURI(LOBITH_48_URL), {
      headers: { Accept: "application/json" },
      validateStatus: () => true,
    });
    if (resp.status !== 200 || !resp.data) {
      lobithChart48.value = null;
      lobithChart48Obj.value = null;
      return;
    }
    // 保存字符串（便于调试）
    try {
      lobithChart48.value = JSON.stringify(resp.data, null, 2);
    } catch (e) {
      lobithChart48.value = String(resp.data);
    }
    // 保存解析后的对象以便表格使用
    try {
      lobithChart48Obj.value = resp.data;
    } catch (e) {
      lobithChart48Obj.value = null;
    }
  } catch (err) {
    console.error("[fetchLobith48Raw] error", err);
    lobithChart48.value = null;
    lobithChart48Obj.value = null;
  }
}

/* ----- 用于表格的数据转换（Lobith） ----- */
/* lobithChart48Obj 结构按照你给出的样例：{ t0, series: [{ isPrediction, data: [...] , color }] } */
const lobithRows = computed(() => {
  const obj = lobithChart48Obj.value;
  if (!obj || !Array.isArray(obj.series)) return [];
  const rows = [];
  for (const s of obj.series) {
    const isPrediction = !!s.isPrediction;
    const seriesColor = s.color || (isPrediction ? "#b90101" : "#0178ca");
    if (!Array.isArray(s.data)) continue;
    for (const d of s.data) {
      if (!d || !d.dateTime) continue;
      rows.push({
        dateTime: d.dateTime,
        value: d.value,
        min: d.min ?? null,
        max: d.max ?? null,
        isPrediction,
        seriesColor,
      });
    }
  }
  // sort by date ascending
  rows.sort((a, b) => (a.dateTime > b.dateTime ? 1 : a.dateTime < b.dateTime ? -1 : 0));
  return rows;
});

/* 辅助：把 ISO 时间转成本地可读短串 */
function fmtLocalShort(iso) {
  try {
    const d = new Date(iso);
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    const hh = String(d.getHours()).padStart(2, "0");
    const mm = String(d.getMinutes()).padStart(2, "0");
    return `${y}-${m}-${day} ${hh}:${mm}`;
  } catch (e) {
    return iso;
  }
}

/* ----- 主流程：并行请求所有需要的数据 ----- */
async function fetchAll() {
  loading.value = true;
  try {
    // 水位（EEFD, WESTV, LOBI）
    const levelPromises = levelCodes.map((code) =>
      fetchChartByKey(code, MAP_LEVEL)
    );
    // 流量（WEST, LOBI）
    const dischargeSources = ["WEST", "LOBI"];
    const dischargePromises = dischargeSources.map((src) =>
      fetchChartByKey(src, MAP_DISCHARGE)
    );

    // 同时并行发起 Lobith -48,48 的原始请求（不会影响主解析）
    const lobith48Promise = fetchLobith48Raw();

    const [levelResults, dischargeResults] = await Promise.all([
      Promise.all(levelPromises),
      Promise.all(dischargePromises),
    ]);

    // 解析水位
    levelResults.forEach((res, idx) => {
      const code = levelCodes[idx];
      if (!res) {
        levelValues.value[code] = null;
        levelTimes.value[code] = null;
        return;
      }
      const latest = parseLatestMeasuredFromChart(res);
      if (latest) {
        levelValues.value[code] = latest.value;
        levelTimes.value[code] = latest.dateTime;
      } else {
        // fallback：最后非空点
        let fb = null;
        if (Array.isArray(res.series)) {
          for (const s of res.series) {
            if (!Array.isArray(s.data)) continue;
            for (let i = s.data.length - 1; i >= 0; i--) {
              const d = s.data[i];
              if (d && d.value !== undefined && d.value !== null) {
                fb = { value: d.value, dateTime: d.dateTime || d.date || null };
                break;
              }
            }
            if (fb) break;
          }
        }
        if (fb) {
          levelValues.value[code] = fb.value;
          levelTimes.value[code] = fb.dateTime;
        } else {
          levelValues.value[code] = null;
          levelTimes.value[code] = null;
        }
      }
    });

    // 解析流量
    dischargeResults.forEach((res, idx) => {
      const src = dischargeSources[idx];
      if (!res) {
        dischargeValues.value[src] = null;
        dischargeTimes.value[src] = null;
        return;
      }
      const latest = parseLatestMeasuredFromChart(res);
      if (latest) {
        dischargeValues.value[src] = latest.value;
        dischargeTimes.value[src] = latest.dateTime;
      } else {
        dischargeValues.value[src] = null;
        dischargeTimes.value[src] = null;
      }
    });

    // 请求 Kaub 的 pegelonline 数据（并行）
    await Promise.all([
      fetchKaubLevel(),
      fetchKaubDischarge(),
      lobith48Promise,
    ]);
  } finally {
    loading.value = false;
  }
}

/* ----- 显示格式化 ----- */
function exactLocationStr(key) {
  return locationMap[key] || key;
}
function displayName(key) {
  return names[key] || exactLocationStr(key);
}

function displayLevel(code) {
  const v = levelValues.value[code];
  if (v === null || v === undefined) return "—";
  const n = Number.parseFloat(v);
  return Number.isFinite(n) ? `${Math.round(n)} ` : "—"; // 改为整数（四舍五入）
}

function displayDischargeForRow(code) {
  const src = cardToDischargeSource[code];
  if (!src) return "—";
  const v = dischargeValues.value[src];
  if (v === null || v === undefined) return "—";
  const n = Number.parseFloat(v);
  return Number.isFinite(n) ? `${n.toFixed(2)} ` : "—";
}
function dischargeTimeDisplayForRow(code) {
  const src = cardToDischargeSource[code];
  if (!src) return "no data";
  const dt = dischargeTimes.value[src];
  return dt ? dt.slice(0, 16).replace("T", " ") : "no data";
}

/* Kaub 显示 */
function displayKaubLevel() {
  const v = kaubLevel.value.value;
  if (v === null || v === undefined) return "—";
  const n = Number.parseFloat(v);
  return Number.isFinite(n) ? `${Math.round(n)} ` : "—"; // 改为整数（四舍五入）
}
function kaubLevelTimeDisplay() {
  const t = kaubLevel.value.timestamp;
  return t ? t.slice(0, 16).replace("T", " ") : "no data";
}
function kaubLevelStatusDisplay() {
  return kaubLevel.value.status || "—";
}
function displayKaubDischarge() {
  const v = kaubDischarge.value.value;
  if (v === null || v === undefined) return "—";
  const n = Number.parseFloat(v);
  return Number.isFinite(n) ? `${n.toFixed(2)} ` : "—";
}
function kaubDischargeTimeDisplay() {
  const t = kaubDischarge.value.timestamp;
  return t ? t.slice(0, 16).replace("T", " ") : "no data";
}

/* ----- 启动 ----- */
onMounted(() => {
  fetchAll();
});
</script>

<template>
  <el-card
    class="4city-card"
    style="
      width: 100%;
      padding: 0;
      box-shadow: none;
      border: 0;
      background: transparent;
    "
  >
    <div ref="scrollRef" class="scroll-container">
      <!-- 一行放 4 个卡片 -->
      <div class="card-grid">
        <div
          class="city-card"
          v-for="code in displayCodes"
          :key="code"
          role="group"
          :aria-label="`水位与流量 ${displayName(code)}`"
        >
          <div class="card-head">
            <div class="loc-name">{{ displayName(code) }}</div>
          </div>

          <div class="card-body">
            <div class="level-wrap">
              <div class="level-value">{{ getLevelDisplay(code) }}</div>
              <div class="level-unit">cm</div>
            </div>

            <div class="discharge-wrap">
              <div class="discharge-value">{{ getDischargeDisplay(code) }}</div>
              <div class="discharge-unit">m³/s</div>
            </div>
          </div>
        </div>
        <!-- city-card -->
      </div>
      <!-- card-grid -->
    </div>

  <!-- ========== Lobith 表格可视化区 ========== -->
<section class="lobith-table-section" v-if="lobithRows.length">
  <div class="table-header">
    <h4>Lobith 数据（监测 vs 预测）</h4>
    <div class="legend">
      <span class="legend-item measured"><i></i> 监测</span>
      <span class="legend-item prediction"><i></i> 预测</span>
    </div>
  </div>

  <div class="table-wrap">
    <table class="lobith-table">
      <thead>
        <tr>
          <th>时间（本地）</th>
          <th>值 (cm)</th>
          <th>类型</th>
          <th>min</th>
          <th>max</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(r, idx) in lobithRows"
          :key="r.dateTime + '-' + idx"
          :class="[{ prediction: r.isPrediction, measured: !r.isPrediction }, idx === lobithRows.length - 1 ? 'latest' : '']"
        >
          <td>{{ fmtLocalShort(r.dateTime) }}</td>
          <td>{{ Number.isFinite(r.value) ? r.value : '—' }}</td>
          <td>{{ r.isPrediction ? '预测' : '监测' }}</td>
          <td>{{ r.min !== null ? r.min : '—' }}</td>
          <td>{{ r.max !== null ? r.max : '—' }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</section>

  </el-card>



</template>

<style scoped>
.scroll-container {
  margin-left: -8px;
  margin-top: -5px;
  width: 105%;
  height: 500px;
  overflow-y: auto;
  padding: 0px;
  box-sizing: border-box;
}
.card-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 3px;
}
.city-card {
  padding: 10px;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  min-height: 120px;
}
.card-head {
  font-size: 13px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}
.card-body {
  display: flex;
  justify-content: space-between;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

.level-wrap {
  display: flex;
  align-items: baseline;
  gap: 5px;
  margin-top: 6px;
}
.level-value {
  font-size: 22px;
  font-weight: 800;
  line-height: 1;
}
.level-unit {
  font-size: 14px;
  margin-left: 6px;
  color: #374151;
}
.delta {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.discharge-wrap {
  display: flex;
  align-items: baseline;
  gap: 2px;
  margin-top: 0px;
}
.discharge-value {
  font-size: 16px;
  font-weight: 700;
}
.discharge-unit {
  font-size: 12px;
  color: #374151;
}

/* 状态色 */
.status-normal {
  color: #111827;
}
.status-warning {
  color: #b45309;
} /* 橙 */
.status-danger {
  color: #dc2626;
} /* 红 */

/* 响应式 */
@media (max-width: 1100px) {
  .card-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 560px) {
  .card-grid {
    grid-template-columns: repeat(1, 1fr);
  }
}



/* ---------- Lobith 表格样式（新增） ---------- */
.lobith-table-section {
  margin-top: 14px;
  padding: 10px;
  background: rgba(255,255,255,0.03);
  border-radius: 6px;
}
.table-header {
  display:flex;
  justify-content:space-between;
  align-items:center;
  gap: 12px;
  margin-bottom: 8px;
}
.legend { display:flex; gap:8px; align-items:center; font-size:13px; }
.legend-item { display:flex; align-items:center; gap:6px; }
.legend-item i { width:12px; height:12px; display:inline-block; border-radius:2px; }
.legend-item.measured i { background:#0178ca; }
.legend-item.prediction i { background:#b90101; }

.table-wrap { overflow:auto; max-height:260px; border-radius:6px; }
.lobith-table { width:100%; border-collapse: collapse; min-width: 720px; }
.lobith-table thead th {
  text-align:left;
  padding:8px 10px;
  font-size:13px;
  background: rgba(0,0,0,0.04);
  position: sticky;
  top: 0;
  z-index: 3;
}
.lobith-table tbody td {
  padding:8px 10px;
  border-bottom: 1px solid rgba(0,0,0,0.05);
  font-size:13px;
}
.lobith-table tbody tr.measured td { color: #0b67a6; }     /* 监测：蓝 */
.lobith-table tbody tr.prediction td { color: #b90000; }   /* 预测：红 */
.lobith-table tbody tr.latest { background: rgba(255,215,0,0.06); }

</style>
