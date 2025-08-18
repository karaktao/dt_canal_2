<template>
  <el-card style="width:100%; padding:12px;">

    <div ref="scrollRef" class="scroll-container">
      <table class="simple-table">

        <tbody>
          <tr v-for="code in levelCodes" :key="code">
            <td class="loc-cell">
              <div class="loc-name">{{ displayName(code) }}</div>
            </td>

            <td class="value-cell">{{ displayLevel(code) }}</td>

            <td class="value-cell">{{ displayDischargeForRow(code) }}</td>
          </tr>

          <tr key="KAUB">
            <td class="loc-cell">
              <div class="loc-name">Kaub</div>
            </td>
            <td class="value-cell">{{ displayKaubLevel() }}</td>
            <td class="value-cell">{{ displayKaubDischarge() }}</td>
          </tr>
        </tbody>
      </table>

      <!-- Lobith -48,48 的原始 JSON 显示 -->
      <div class="lobith-section">
        <div class="lobith-header">
          <span style="font-weight:600">Lobith (LOBI) — values = -48,48</span>
        </div>
        <div class="lobith-pre-wrapper">
          <pre v-if="lobithChart48" class="lobith-pre">{{ lobithChart48 }}</pre>
          <div v-else class="lobith-empty">暂无 Lobith -48,48 数据（请点击刷新）</div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

/* ----- 配置 ----- */
const levelCodes = ['EEFD','WESTV','LOBI'];
const locationMap = {
  EEFD: 'Eefde-beneden(EEFD)',
  WESTV: 'Westervoort-IJsseldijkerw(WESTV)',
  LOBI:  'Lobith(LOBI)',
  WEST:  'Westervoort(WEST)'
};
const names = { EEFD:'Eefde-beneden', WESTV:'Westervoort-IJsseldijkerw', LOBI:'Lobith', WEST:'Westervoort' };
const cardToDischargeSource = { EEFD: null, WESTV: 'WEST', LOBI: 'LOBI' };

const API_PATH = '/api/waterinfo/api/chart/get';
const MAP_LEVEL = 'waterhoogte';
const MAP_DISCHARGE = 'waterafvoer';
const VALUES_PARAM = '-6,3';

/* Kaub (PegelOnline) */
const KAUB_STATION_ID = '1d26e504-7f9e-480a-b52c-5932be6549ab';
const KAUB_LEVEL_URL = `https://www.pegelonline.wsv.de/webservices/rest-api/v2/stations/${KAUB_STATION_ID}/W/currentmeasurement.json`;
const KAUB_DISCHARGE_URL = `https://www.pegelonline.wsv.de/webservices/rest-api/v2/stations/${KAUB_STATION_ID}/Q/currentmeasurement.json`;

/* Lobith -48,48 特别请求（你要的 URL） */
const LOBITH_48_VALUES = '-48,48';
const LOBITH_48_URL = `${API_PATH}?mapType=${MAP_LEVEL}&locationCodes=${locationMap.LOBI}&values=${LOBITH_48_VALUES}`;

/* ----- 响应数据状态 ----- */
const loading = ref(false);

const levelValues = ref({ EEFD: null, WESTV: null, LOBI: null });
const levelTimes  = ref({ EEFD: null, WESTV: null, LOBI: null });

const dischargeValues = ref({ WEST: null, LOBI: null });
const dischargeTimes  = ref({ WEST: null, LOBI: null });

const kaubLevel = ref({ value: null, timestamp: null, status: null });
const kaubDischarge = ref({ value: null, timestamp: null });

/* 这里保存 Lobith -48,48 的原始 JSON 字符串（格式化后） */
const lobithChart48 = ref(null);

/* 滚动容器引用 */
const scrollRef = ref(null);

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
  return latest.value !== null ? { value: latest.value, dateTime: latest.dateTime } : null;
}

/* ----- 请求函数 ----- */
async function fetchChartByKey(key, mapType, values = VALUES_PARAM) {
  const locStr = locationMap[key] || key;
  const fullUrl = `${API_PATH}?mapType=${mapType}&locationCodes=${locStr}&values=${values}`;
  console.log(`[fetchChartByKey] ${key} ${mapType} ->`, fullUrl);
  try {
    const resp = await axios.get(encodeURI(fullUrl), { headers: { Accept: 'application/json' }, validateStatus: () => true });
    if (resp.status !== 200) {
      console.warn(`[fetchChartByKey] ${key} ${mapType} status ${resp.status}`, resp.data);
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
    const resp = await axios.get(KAUB_LEVEL_URL, { headers: { Accept: 'application/json' }, validateStatus: () => true });
    if (resp.status !== 200 || !resp.data) { kaubLevel.value = { value: null, timestamp: null, status: null }; return; }
    const data = resp.data;
    const ts = data.timestamp || data.time || null;
    const val = data.value !== undefined ? data.value : (data.currentValue !== undefined ? data.currentValue : null);
    const status = data.status || data.quality || data.flag || null;
    kaubLevel.value = { value: val, timestamp: ts, status };
  } catch (err) {
    console.error('[Kaub] level error', err);
    kaubLevel.value = { value: null, timestamp: null, status: null };
  }
}

async function fetchKaubDischarge() {
  try {
    const resp = await axios.get(KAUB_DISCHARGE_URL, { headers: { Accept: 'application/json' }, validateStatus: () => true });
    if (resp.status !== 200 || !resp.data) { kaubDischarge.value = { value: null, timestamp: null }; return; }
    const data = resp.data;
    const ts = data.timestamp || data.time || null;
    const val = data.value !== undefined ? data.value : (data.currentValue !== undefined ? data.currentValue : null);
    kaubDischarge.value = { value: val, timestamp: ts };
  } catch (err) {
    console.error('[Kaub] discharge error', err);
    kaubDischarge.value = { value: null, timestamp: null };
  }
}

/* ----- Lobith -48,48 原始数据请求 ----- */
async function fetchLobith48Raw() {
  try {
    console.log('[fetchLobith48Raw] requesting:', LOBITH_48_URL);
    const resp = await axios.get(encodeURI(LOBITH_48_URL), { headers: { Accept: 'application/json' }, validateStatus: () => true });
    if (resp.status !== 200 || !resp.data) {
      console.warn('[fetchLobith48Raw] non-200 or empty', resp.status, resp.data);
      lobithChart48.value = null;
      return;
    }
    // 格式化 JSON 字符串显示
    try {
      lobithChart48.value = JSON.stringify(resp.data, null, 2);
    } catch (e) {
      lobithChart48.value = String(resp.data);
    }
  } catch (err) {
    console.error('[fetchLobith48Raw] error', err);
    lobithChart48.value = null;
  }
}

/* ----- 主流程：并行请求所有需要的数据 ----- */
async function fetchAll() {
  loading.value = true;
  try {
    // 水位（EEFD, WESTV, LOBI）
    const levelPromises = levelCodes.map(code => fetchChartByKey(code, MAP_LEVEL));
    // 流量（WEST, LOBI）
    const dischargeSources = ['WEST','LOBI'];
    const dischargePromises = dischargeSources.map(src => fetchChartByKey(src, MAP_DISCHARGE));

    // 同时并行发起 Lobith -48,48 的原始请求（不会影响主解析）
    const lobith48Promise = fetchLobith48Raw();

    const [levelResults, dischargeResults] = await Promise.all([
      Promise.all(levelPromises),
      Promise.all(dischargePromises)
    ]);

    // 解析水位
    levelResults.forEach((res, idx) => {
      const code = levelCodes[idx];
      if (!res) { levelValues.value[code] = null; levelTimes.value[code] = null; return; }
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
              if (d && d.value !== undefined && d.value !== null) { fb = { value: d.value, dateTime: d.dateTime || d.date || null }; break; }
            }
            if (fb) break;
          }
        }
        if (fb) { levelValues.value[code] = fb.value; levelTimes.value[code] = fb.dateTime; }
        else { levelValues.value[code] = null; levelTimes.value[code] = null; }
      }
    });

    // 解析流量
    dischargeResults.forEach((res, idx) => {
      const src = dischargeSources[idx];
      if (!res) { dischargeValues.value[src] = null; dischargeTimes.value[src] = null; return; }
      const latest = parseLatestMeasuredFromChart(res);
      if (latest) { dischargeValues.value[src] = latest.value; dischargeTimes.value[src] = latest.dateTime; }
      else { dischargeValues.value[src] = null; dischargeTimes.value[src] = null; }
    });

    // 请求 Kaub 的 pegelonline 数据（并行）
    await Promise.all([fetchKaubLevel(), fetchKaubDischarge(), lobith48Promise]);

  } finally {
    loading.value = false;
  }
}

/* ----- 显示格式化 ----- */
function exactLocationStr(key) { return locationMap[key] || key; }
function displayName(key) { return names[key] || exactLocationStr(key); }

function displayLevel(code) {
  const v = levelValues.value[code];
  if (v === null || v === undefined) return '—';
  const n = Number.parseFloat(v);
  return Number.isFinite(n) ? `${n.toFixed(1)} cm` : '—';
}
function levelTimeDisplay(code) {
  const dt = levelTimes.value[code];
  return dt ? dt.slice(0,16).replace('T',' ') : 'no data';
}

function displayDischargeForRow(code) {
  const src = cardToDischargeSource[code];
  if (!src) return '—';
  const v = dischargeValues.value[src];
  if (v === null || v === undefined) return '—';
  const n = Number.parseFloat(v);
  return Number.isFinite(n) ? `${n.toFixed(2)} m³/s` : '—';
}
function dischargeTimeDisplayForRow(code) {
  const src = cardToDischargeSource[code];
  if (!src) return 'no data';
  const dt = dischargeTimes.value[src];
  return dt ? dt.slice(0,16).replace('T',' ') : 'no data';
}

/* Kaub 显示 */
function displayKaubLevel() {
  const v = kaubLevel.value.value;
  if (v === null || v === undefined) return '—';
  const n = Number.parseFloat(v);
  return Number.isFinite(n) ? `${n.toFixed(1)} cm` : '—';
}
function kaubLevelTimeDisplay() { const t = kaubLevel.value.timestamp; return t ? t.slice(0,16).replace('T',' ') : 'no data'; }
function kaubLevelStatusDisplay() { return kaubLevel.value.status || '—'; }
function displayKaubDischarge() { const v = kaubDischarge.value.value; if (v === null || v === undefined) return '—'; const n = Number.parseFloat(v); return Number.isFinite(n) ? `${n.toFixed(2)} m³/s` : '—'; }
function kaubDischargeTimeDisplay() { const t = kaubDischarge.value.timestamp; return t ? t.slice(0,16).replace('T',' ') : 'no data'; }

/* ----- 启动 ----- */
onMounted(() => {
  fetchAll();
});
</script>

<style scoped>
.scroll-container {
  height: 300px;
  max-height: 520px;
  overflow-y: auto;
  overflow-x: hidden;
  -webkit-overflow-scrolling: touch;
  padding-right: 6px;
}

/* 表格样式 */
.simple-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 10px;
  table-layout: fixed;
  word-break: break-word;
}
.simple-table thead th {
  text-align: left;
  padding: 8px 10px;
  font-weight: 600;
  color: #4b5563;
  border-bottom: 1px solid #e6edf3;
}
.simple-table tbody td {
  padding: 10px;
  border-bottom: 1px solid #f3f6f9;
  vertical-align: middle;
  white-space: normal;
}
.loc-cell .loc-name { font-weight:600; }
.loc-cell .loc-code { color:#909399; font-size:12px; margin-top:4px; }
.value-cell { font-weight:700; }
.time-cell { color:#909399; font-size:12px; }

/* Lobith 原始 JSON 区域样式 */
.lobith-section { margin-top: 12px; }
.lobith-header { margin-bottom: 6px; color:#374151; }
.lobith-pre-wrapper {
  background:#0f1724;
  color:#e6eef8;
  border-radius:6px;
  padding:10px;
  max-height:260px;
  overflow:auto;
}
.lobith-pre { white-space:pre-wrap; font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, "Roboto Mono", monospace; font-size:12px; line-height:1.4; margin:0; }
.lobith-empty { color:#9ca3af; padding:12px; }
</style>
