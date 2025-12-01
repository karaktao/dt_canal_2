<script setup>
import { ref, onMounted, computed, watch, onBeforeUnmount } from "vue";
import axios from "axios";
import * as echarts from "echarts";

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

/* Lobith -48,48 特别请求 */
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

// 水位折线图
const lobithChartEl = ref(null);
let lobithChartInstance = null;

// 从已有显示函数复用并统一输出（字符串，不带单位）
function getLevelDisplay(code) {
  if (code === "KAUB") return displayKaubLevel();
  return displayLevel(code);
}
function getDischargeDisplay(code) {
  if (code === "KAUB") return displayKaubDischarge();
  return displayDischargeForRow(code);
}
function getTimeDisplay(code) {
  if (code === "KAUB") return kaubLevelTimeDisplay();
  const t = levelTimeDisplay(code);
  if (t && t !== "no data") return t;
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
    try {
      lobithChart48.value = JSON.stringify(resp.data, null, 2);
    } catch (e) {
      lobithChart48.value = String(resp.data);
    }
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

/* ----- 用于表格的数据转换（Lobith 原始平铺）----- */
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
  rows.sort((a, b) =>
    a.dateTime > b.dateTime ? 1 : a.dateTime < b.dateTime ? -1 : 0
  );
  return rows;
});

/* ----- 关键：透视为统一时间轴（解决重叠段空白） ----- */
const lobithSeries = computed(() => {
  const obj = lobithChart48Obj.value;
  if (!obj || !Array.isArray(obj.series)) {
    return { times: [], labels: [], measured: [], prediction: [] };
  }

  // 聚合到同一时间戳
  const map = new Map(); // ts -> { measured, prediction }
  for (const s of obj.series) {
    const isPrediction = !!s.isPrediction;
    if (!Array.isArray(s.data)) continue;
    for (const d of s.data) {
      const ts = d?.dateTime;
      if (!ts) continue;
      const v = Number.isFinite(+d.value) ? +d.value : null;
      if (!map.has(ts)) map.set(ts, { measured: null, prediction: null });
      const rec = map.get(ts);
      if (isPrediction) rec.prediction = v;
      else rec.measured = v;
    }
  }

  const times = Array.from(map.keys()).sort();
  const labels = times.map((iso) => {
    try {
      const d = new Date(iso);
      const day = String(d.getDate()).padStart(2, "0");
      const mon = String(d.getMonth() + 1).padStart(2, "0");
      return `${day}-${mon}`;
    } catch {
      return iso;
    }
  });
  const measured = times.map((t) => map.get(t).measured);
  const prediction = times.map((t) => map.get(t).prediction);

  return { times, labels, measured, prediction };
});

/* ----- 构建 ECharts 配置（基于统一时间轴） ----- */
function buildLobithOptionFromSeries(seriesPack) {
  const { times, labels, measured, prediction } = seriesPack;

  const allVals = [...measured, ...prediction].filter((v) => Number.isFinite(v));
  let yMax = 10,
    yMin = -10;
  if (allVals.length) {
    const maxVal = Math.max(...allVals);
    const minVal = Math.min(...allVals);
    yMax = Math.floor(maxVal / 10) * 10 + 10; // 严格高于 max
    yMin = Math.ceil(minVal / 10) * 10 - 10;  // 严格低于 min
    if (yMin >= yMax) {
      const center = (maxVal + minVal) / 2 || 0;
      yMax = Math.ceil((center + 10) / 10) * 10;
      yMin = Math.floor((center - 10) / 10) * 10;
      if (yMin === yMax) yMax = yMin + 10;
    }
  }

  const fmt = (iso) => {
    try {
      const d = new Date(iso);
      const Y = d.getFullYear();
      const M = String(d.getMonth() + 1).padStart(2, "0");
      const D = String(d.getDate()).padStart(2, "0");
      const hh = String(d.getHours()).padStart(2, "0");
      const mm = String(d.getMinutes()).padStart(2, "0");
      return `${D}-${M}-${Y} ${hh}:${mm}`;
    } catch {
      return iso;
    }
  };

  return {
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "shadow" },
      textStyle: { fontSize: 10 },
      formatter: (params) => {
        if (!params?.length) return "";
        const idx = params[0].dataIndex;
        const fullTime = times[idx] ? fmt(times[idx]) : "";
        const lines = params
          .map((p) => `${p.marker} ${p.seriesName}：${p.value ?? "—"}`)
          .join("<br/>");
        return `${fullTime}<br/>${lines}`;
      },
    },
    legend: { data: ["Measured", "Prediction"], top: 0 },
    grid: { top: 32, left: 35, right: 5, bottom: 60, height: 70 },
    xAxis: {
      type: "category",
      data: labels,
      boundaryGap: false,
      axisLabel: { rotate: 0, fontSize: 10, interval: Math.ceil(labels.length / 8) },
    },
    yAxis: {
      type: "value",
      name: "Lobith (cm)",
      min: yMin,
      max: yMax,
      axisLabel: { fontSize: 10, formatter: "{value}" },
    },
    series: [
      {
        name: "Measured",
        type: "line",
        data: measured,
        showSymbol: false,
        connectNulls: true,               // 关键：跨 null 连线
        lineStyle: { width: 2 },
        itemStyle: { color: "#409eff" },
      },
      {
        name: "Prediction",
        type: "line",
        data: prediction,
        showSymbol: false,
        connectNulls: true,               // 关键：跨 null 连线
        lineStyle: { width: 2, type: "dashed" }, // 预测虚线
        itemStyle: { color: "#91CC75" },
      },
    ],
  };
}

/* ----- 初始化/更新图表 ----- */
function initLobithChart() {
  if (!lobithChartEl.value) return;
  lobithChartInstance = echarts.init(lobithChartEl.value);
}
function updateLobithChart() {
  if (!lobithChartInstance) initLobithChart();
  if (!lobithChartInstance) return;
  const pack = lobithSeries.value || { labels: [] };
  const option = buildLobithOptionFromSeries(pack);
  lobithChartInstance.setOption(option, { notMerge: false });
}

/* ----- 工具显示函数（原有） ----- */
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
  return Number.isFinite(n) ? `${Math.round(n)} ` : "—";
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
  return Number.isFinite(n) ? `${Math.round(n)} ` : "—";
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

/* ----- 拉取所有数据 ----- */
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

    // 同时并行发起 Lobith -48,48 的原始请求
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
      const src = ["WEST", "LOBI"][idx];
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
    await Promise.all([fetchKaubLevel(), fetchKaubDischarge(), lobith48Promise]);
  } finally {
    loading.value = false;
  }
}

/* ----- 启动 & 清理 ----- */
function handleResize() {
  if (lobithChartInstance) lobithChartInstance.resize();
}

onMounted(async () => {
  initLobithChart();
  window.addEventListener("resize", handleResize);
  await fetchAll();
  updateLobithChart();
});

// 当“统一时间轴数据包”变化时更新图表
watch(
  lobithSeries,
  () => {
    updateLobithChart();
  },
  { immediate: false, deep: true }
);

onBeforeUnmount(() => {
  window.removeEventListener("resize", handleResize);
  if (lobithChartInstance) {
    lobithChartInstance.dispose();
    lobithChartInstance = null;
  }
});

/* （可选）时间格式短串函数，如需展示 */
function levelTimeDisplay(code) {
  const t = levelTimes.value[code];
  return t ? t.slice(0, 16).replace("T", " ") : "no data";
}
</script>

<template>
  <el-card
    class="four-city-card"
    style="
      width: 100%;
      padding: 0;
      box-shadow: none;
      border: 0;
      background: transparent;
      height: 210px;
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

      <!-- 使用统一时间轴的可视化 -->
      <div
        ref="lobithChartEl"
        class="lobith-chart-container"
        v-if="lobithSeries.labels && lobithSeries.labels.length"
        role="img"
        aria-label="Lobith 水位折线图"
      ></div>
    </div>
  </el-card>
</template>

<style scoped>
.scroll-container {
  margin-left: 0;
  margin-top: -10px;
  width: 100%;
  height: 500px;
  overflow: visible !important;
  padding: 0px;
  box-sizing: border-box;
}
.card-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0px;
  margin-bottom: 0px;
}

.lobith-chart-container {
  width: 100%;
  box-sizing: border-box;
  height: 300px;
  max-height: 60vh;
  margin-top: -45px;
  overflow: visible;
  border-radius: 6px;
  background: transparent;
}

.city-card {
  padding: 0px;
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
:deep(.loc-name) {
  color: #333333 !important;
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
  color: #3c424be0;
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
  color: #3c424bea;
}
.discharge-unit {
  font-size: 12px;
  color: #374151;
}
</style>
