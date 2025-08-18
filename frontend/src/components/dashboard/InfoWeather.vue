<template>
  <el-card class="info-box">
    <div class="content" style="padding: 12px">
      <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
        <!-- Current tab -->
        <el-tab-pane label="Current" name="first">
          <div class="tab-pane-content">
            <div style="margin-bottom: 8px">
              <div style="font-weight: 600; margin-bottom: 6px">
                Current (raw)
              </div>
              <pre class="raw-pre">{{
                rawCurrent
                  ? JSON.stringify(rawCurrent, null, 2)
                  : "No current metadata available."
              }}</pre>
            </div>

            <div style="margin-top: 10px; margin-bottom: 8px">
              <div style="font-weight: 600; margin-bottom: 6px">
                Minutely (15-min) - raw
              </div>
              <pre class="raw-pre">{{
                rawMinutely
                  ? JSON.stringify(rawMinutely, null, 2)
                  : "No minutely_15 data available."
              }}</pre>
            </div>

            <div v-if="lastMinutely" class="last-minutely">
              <div style="font-weight: 600; margin-bottom: 4px">
                Latest minutely (15-min) data
              </div>
              <div style="font-size: 13px">
                <div><strong>time</strong>: {{ lastMinutely.time }}</div>
                <div v-for="(v, k) in lastMinutely.values" :key="k">
                  <strong>{{ k }}</strong
                  >: {{ v }}
                </div>
              </div>
            </div>

            <div
              style="
                align-self: flex-end;
                color: #666;
                font-size: 12px;
                margin-top: 8px;
              "
            >
              Last update:
              {{ lastUpdated ? new Date(lastUpdated).toLocaleString() : "—" }}
            </div>
          </div>
        </el-tab-pane>

        <!-- Today tab: hourly -->
        <el-tab-pane label="Today" name="second">
          <div class="tab-pane-content">
            <div style="font-weight: 600; margin-bottom: 6px">Hourly (raw)</div>
            <pre class="raw-pre">{{
              hourlyRaw
                ? JSON.stringify(hourlyRaw, null, 2)
                : "No hourly data available."
            }}</pre>

            <div
              v-if="hourlyLatest"
              class="last-hourly"
              style="margin-top: 8px"
            >
              <div style="font-weight: 600; margin-bottom: 4px">
                Latest hourly data
              </div>
              <div style="font-size: 13px">
                <div><strong>time</strong>: {{ hourlyLatest.time }}</div>
                <div v-for="(v, k) in hourlyLatest.values" :key="k">
                  <strong>{{ k }}</strong
                  >: {{ v }}
                </div>
              </div>
            </div>

            <div v-if="hourlyPreview.length" style="margin-top: 10px">
              <div style="font-weight: 600; margin-bottom: 6px">
                Next / recent 24 hourly entries (preview)
              </div>
              <div class="hourly-list">
                <div
                  class="hour-row"
                  v-for="(h, idx) in hourlyPreview"
                  :key="idx"
                >
                  <div class="hour-time">{{ h.time }}</div>
                  <div class="hour-values">
                    <span v-if="h.values.temperature_2m !== undefined"
                      >T: {{ h.values.temperature_2m }}</span
                    >
                    <span v-if="h.values.wind_speed_10m !== undefined">
                      | W: {{ h.values.wind_speed_10m }}</span
                    >
                    <span v-if="h.values.precipitation !== undefined">
                      | P: {{ h.values.precipitation }}</span
                    >
                    <span v-if="h.values.visibility !== undefined">
                      | V: {{ h.values.visibility }}</span
                    >
                    <span
                      v-if="h.values.precipitation_probability !== undefined"
                    >
                      | P%: {{ h.values.precipitation_probability }}</span
                    >
                    <span v-if="h.values.weather_code !== undefined">
                      | code: {{ h.values.weather_code }}</span
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- Month tab: daily -->
        <el-tab-pane label="Month" name="third">
          <div class="tab-pane-content">
            <div style="font-weight: 600; margin-bottom: 6px">Daily (raw)</div>
            <pre class="raw-pre">{{
              dailyRaw
                ? JSON.stringify(dailyRaw, null, 2)
                : "No daily data available."
            }}</pre>

            <div v-if="dailySummary.length" style="margin-top: 10px">
              <div style="font-weight: 600; margin-bottom: 6px">
                Daily summary (next {{ dailySummary.length }} days)
              </div>
              <div class="daily-list">
                <div
                  class="day-row"
                  v-for="(d, idx) in dailySummary"
                  :key="idx"
                >
                  <div class="day-date">{{ d.date }}</div>
                  <div class="day-values">
                    <span>min: {{ d.temp_min }}</span>
                    <span> | max: {{ d.temp_max }}</span>
                    <span v-if="d.wind_speed_max !== undefined">
                      | wind_max: {{ d.wind_speed_max }}</span
                    >
                    <span v-if="d.precipitation_sum !== undefined">
                      | precip: {{ d.precipitation_sum }}</span
                    >
                    <span v-if="d.precipitation_probability_max !== undefined">
                      | p%: {{ d.precipitation_probability_max }}</span
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-card>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import axios from "axios";
import proj4 from "proj4";
import { register } from "ol/proj/proj4";
import { transform } from "ol/proj";

const props = defineProps({
  selectedCoordinates: { type: Array, default: null },
  location: { type: String, default: "" },
  geoFeatures: { type: Array, default: () => [] },
  data: { type: Object, default: () => ({}) },
  title: { type: String, default: "" },
});

const activeName = ref("first");

// raw holders
const rawCurrent = ref(null);
const rawMinutely = ref(null);
const lastMinutely = ref(null);
const lastUpdated = ref(null);

const hourlyRaw = ref(null);
const hourlyLatest = ref(null);
const hourlyPreview = ref([]); // array of {time, values}

const dailyRaw = ref(null);
const dailySummary = ref([]); // array of {date, temp_min, temp_max, ...}

function registerProjs() {
  try {
    proj4.defs(
      "EPSG:25831",
      "+proj=utm +zone=31 +ellps=GRS80 +units=m +no_defs"
    );
    register(proj4);
  } catch (e) {
    // ignore if already registered
  }
}

function findCoordsFromLocation(name) {
  if (
    !name ||
    !Array.isArray(props.geoFeatures) ||
    props.geoFeatures.length === 0
  )
    return null;
  const key = String(name).trim().toLowerCase();
  for (const f of props.geoFeatures) {
    const fname = String(f?.properties?.name ?? "")
      .trim()
      .toLowerCase();
    if (fname === key) {
      return f?.geometry?.coordinates ?? null;
    }
  }
  return null;
}

function coordsToLatLon(coords) {
  if (!coords || !Array.isArray(coords) || coords.length < 2) return null;
  try {
    const [lonT, latT] = transform(
      [Number(coords[0]), Number(coords[1])],
      "EPSG:25831",
      "EPSG:4326"
    );
    const lat = Number(latT);
    const lon = Number(lonT);
    if (!isFinite(lat) || !isFinite(lon)) return null;
    return { lat, lon };
  } catch (e) {
    console.error("coord transform failed:", e);
    return null;
  }
}

// ---- current / minutely processing (same as before) ----
function applyCurrentData(current, units) {
  rawCurrent.value = current ?? null;
  if (!current) {
    lastUpdated.value = null;
    return;
  }
  lastUpdated.value = current.time ?? new Date().toISOString();
}

function processMinutelyData(minutely) {
  rawMinutely.value = minutely ?? null;
  lastMinutely.value = null;
  if (!minutely) return;

  const timeArr = minutely.time ?? minutely.times ?? null;
  if (!Array.isArray(timeArr) || timeArr.length === 0) return;
  const idx = timeArr.length - 1;
  const time = timeArr[idx];
  const values = {};
  for (const k of Object.keys(minutely)) {
    if (k === "time" || k === "times") continue;
    const arr = minutely[k];
    if (Array.isArray(arr) && arr.length > idx) values[k] = arr[idx];
  }
  lastMinutely.value = { time, values };
  console.log("Latest minutely_15 data:", lastMinutely.value);
}

// ---- hourly processing ----
function processHourlyData(hourly) {
  hourlyRaw.value = hourly ?? null;
  hourlyLatest.value = null;
  hourlyPreview.value = [];

  if (!hourly) return;

  const times = hourly.time ?? hourly.times ?? null;
  if (!Array.isArray(times) || times.length === 0) return;

  const lastIdx = times.length - 1;
  const lastTime = times[lastIdx];
  const lastValues = {};
  for (const k of Object.keys(hourly)) {
    if (k === "time" || k === "times") continue;
    const arr = hourly[k];
    if (Array.isArray(arr) && arr.length > lastIdx)
      lastValues[k] = arr[lastIdx];
  }
  hourlyLatest.value = { time: lastTime, values: lastValues };
  console.log("Latest hourly data:", hourlyLatest.value);

  // build preview: take last N entries or next N entries (we show up to 24 recent entries from end)
  const N = Math.min(24, times.length);
  const start = Math.max(0, lastIdx - N + 1);
  for (let i = start; i <= lastIdx; i++) {
    const row = { time: times[i], values: {} };
    for (const k of Object.keys(hourly)) {
      if (k === "time" || k === "times") continue;
      const arr = hourly[k];
      if (Array.isArray(arr) && arr.length > i) row.values[k] = arr[i];
    }
    hourlyPreview.value.push(row);
  }
}

// ---- daily processing ----
function processDailyData(daily) {
  dailyRaw.value = daily ?? null;
  dailySummary.value = [];
  if (!daily) return;

  // daily typically has arrays: time, temperature_2m_max, temperature_2m_min, ...
  const times = daily.time ?? daily.times ?? null;
  if (!Array.isArray(times) || times.length === 0) return;
  const len = times.length;
  // gather fields of interest if present
  for (let i = 0; i < len; i++) {
    const item = { date: times[i] };
    // common daily keys from your URL:
    const maybe = [
      "temperature_2m_min",
      "temperature_2m_max",
      "wind_direction_10m_dominant",
      "wind_speed_10m_max",
      "precipitation_sum",
      "precipitation_probability_max",
    ];
    for (const k of maybe) {
      const arr = daily[k];
      if (Array.isArray(arr) && arr.length > i)
        item[
          k.replace(
            /temperature_2m_/,
            k.includes("min") ? "temp_min" : "temp_max"
          )
        ] = arr[i];
      // we'll also put them under original keys if present
      if (Array.isArray(arr) && arr.length > i) item[k] = arr[i];
    }
    dailySummary.value.push(item);
  }

  // limit summary to first 7 days for display
  dailySummary.value = dailySummary.value.slice(0, 7);
  console.log("Daily summary (preview):", dailySummary.value);
}

// ---- fetch orchestration ----
async function fetchWeatherForCoordinates(coords) {
  const latlon = coordsToLatLon(coords);
  if (!latlon) {
    console.warn("No valid coords for weather request");
    applyCurrentData(null);
    processMinutelyData(null);
    processHourlyData(null);
    processDailyData(null);
    return;
  }
  const { lat, lon } = latlon;

  // current & minutely_15 as before
  const currentUrl = `https://api.open-meteo.com/v1/forecast?latitude=${encodeURIComponent(
    lat
  )}&longitude=${encodeURIComponent(
    lon
  )}&models=best_match&current=temperature_2m,wind_speed_10m,wind_direction_10m,wind_gusts_10m,rain,relative_humidity_2m,weather_code,visibility&timezone=Europe%2FAmsterdam`;
  const minutelyUrl = `https://api.open-meteo.com/v1/forecast?latitude=${encodeURIComponent(
    lat
  )}&longitude=${encodeURIComponent(
    lon
  )}&models=best_match&minutely_15=precipitation,visibility,wind_speed_10m&timezone=Europe%2FAmsterdam`;

  // hourly (for Today) - user's provided URL pattern
  const hourlyUrl = `https://api.open-meteo.com/v1/forecast?latitude=${encodeURIComponent(
    lat
  )}&longitude=${encodeURIComponent(
    lon
  )}&hourly=temperature_2m,wind_speed_10m,precipitation,precipitation_probability,visibility,weather_code&models=best_match&timezone=Europe%2FAmsterdam`;

  // daily (for Month) - user's provided URL pattern (includes hourly as well, but we specifically read daily)
  const dailyUrl = `https://api.open-meteo.com/v1/forecast?latitude=${encodeURIComponent(
    lat
  )}&longitude=${encodeURIComponent(
    lon
  )}&daily=temperature_2m_max,temperature_2m_min,wind_direction_10m_dominant,wind_speed_10m_max,precipitation_sum,precipitation_probability_max&hourly=temperature_2m,wind_speed_10m,precipitation,precipitation_probability,visibility,weather_code&models=best_match&timezone=Europe%2FAmsterdam`;

  try {
    // 并行请求：current/minutely, hourly, daily
    const [curRes, hourlyRes, dailyRes] = await Promise.all([
      axios.get(currentUrl).catch((e) => ({ data: null, error: e })),
      axios.get(hourlyUrl).catch((e) => ({ data: null, error: e })),
      axios.get(dailyUrl).catch((e) => ({ data: null, error: e })),
    ]);

    // current/minutely from curRes and minutely call
    const curData = curRes?.data ?? null;
    const current = curData?.current ?? null;
    const units = curData?.current_units ?? null;
    applyCurrentData(current, units);

    // minutely: try separate minutely endpoint as before (some endpoints keep it inside minutely)
    try {
      const minRes = await axios
        .get(minutelyUrl)
        .catch((e) => ({ data: null, error: e }));
      const minData = minRes?.data ?? null;
      const minObj = minData?.minutely_15 ?? minData?.minutely ?? null;
      processMinutelyData(minObj);
    } catch (e) {
      processMinutelyData(null);
    }

    // hourly
    const hourlyData = hourlyRes?.data ?? null;
    const hourlyObj = hourlyData?.hourly ?? null;
    processHourlyData(hourlyObj);

    // daily (daily field usually in dailyRes.data.daily)
    const dailyData = dailyRes?.data ?? null;
    const dailyObj = dailyData?.daily ?? null;
    processDailyData(dailyObj);
  } catch (err) {
    console.error("fetchWeather failed:", err);
    applyCurrentData(null);
    processMinutelyData(null);
    processHourlyData(null);
    processDailyData(null);
  }
}

function resolveCoordinatesSource() {
  if (
    Array.isArray(props.selectedCoordinates) &&
    props.selectedCoordinates.length >= 2
  ) {
    return props.selectedCoordinates;
  }
  if (
    props.data &&
    Array.isArray(props.data.selectedCoordinates) &&
    props.data.selectedCoordinates.length >= 2
  ) {
    return props.data.selectedCoordinates;
  }
  if (
    props.location &&
    Array.isArray(props.geoFeatures) &&
    props.geoFeatures.length
  ) {
    const found = findCoordsFromLocation(props.location);
    if (found && Array.isArray(found) && found.length >= 2) return found;
  }
  return null;
}

watch(
  () => props.selectedCoordinates,
  (nv) => {
    if (nv && Array.isArray(nv) && nv.length >= 2)
      fetchWeatherForCoordinates(nv);
  },
  { immediate: true, deep: true }
);

watch(
  () => props.location,
  (nv) => {
    const direct =
      Array.isArray(props.selectedCoordinates) &&
      props.selectedCoordinates.length >= 2;
    const dataDirect =
      props.data &&
      Array.isArray(props.data.selectedCoordinates) &&
      props.data.selectedCoordinates.length >= 2;
    if (direct || dataDirect) return;
    if (!nv) return;
    const coords = findCoordsFromLocation(nv);
    if (coords) fetchWeatherForCoordinates(coords);
  },
  { immediate: true }
);

onMounted(() => {
  registerProjs();
  const initial = resolveCoordinatesSource();
  if (initial) fetchWeatherForCoordinates(initial);
});

function handleClick() {
  /* placeholder */
}
</script>

<style scoped>
/* 整体宽度 */
.info-box {
  padding: 0;
  display: flex;
  flex-direction: column;
  width: 400px;
  max-width: 400px;
  box-sizing: border-box; /* 关键：包括 padding 在内算宽度 */
}

/* 去除 el-card body 默认额外 padding 并保证内容宽度一致 */
::v-deep(.info-box .el-card__body) {
  padding: 0;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  box-sizing: border-box;
}

/* 让 tabs 的内容区也使用同样的内边距规则，避免某些标签页默认内边距不同 */
::v-deep(.demo-tabs .el-tabs__content) {
  padding: 0;
}

/* 统一 tab 内部 container 的布局与间距，保证三个 tab 一致 */
.tab-pane-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;            /* 统一内边距（和 .content 的 padding 对齐） */
  box-sizing: border-box;   /* 关键 */
  width: 100%;
}

/* 预览区域：统一宽度、滚动与文本换行行为 */
.raw-pre {
  margin: 0;
  background: #f7f7f7;
  padding: 10px;
  border-radius: 6px;
  overflow: auto;
  -webkit-overflow-scrolling: touch;
  max-height: 160px;       /* 三个页面内的 raw-pre 保持一致高度 */
  width: 100%;             /* 关键：占满当前容器宽度 */
  box-sizing: border-box;  /* 关键 */
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 12px;
  line-height: 1.4;
  outline: none;
}

/* hourly / daily preview 样式统一 */
.hourly-list,
.daily-list {
  max-height: 200px;
  overflow: auto;
  border-radius: 6px;
  padding: 6px;
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.04);
  width: 100%;
  box-sizing: border-box;
}

.hour-row,
.day-row {
  padding: 6px 4px;
  border-bottom: 1px dashed rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  width: 100%;
  box-sizing: border-box;
}
.hour-row:last-child,
.day-row:last-child {
  border-bottom: none;
}

.hour-time,
.day-date {
  font-weight: 600;
  font-size: 12px;
  margin-bottom: 4px;
}
.hour-values,
.day-values {
  font-size: 12px;
  color: #333;
  word-break: break-word;
}

/* last-minutely, last-hourly 一致 */
.last-minutely,
.last-hourly {
  margin-top: 6px;
  padding: 8px;
  background: #ffffff;
  border-radius: 6px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  width: 100%;
  box-sizing: border-box;
}

/* 保留的小类 */
.label {
  font-weight: 600;
  font-size: 12px;
}
.value {
  font-size: 14px;
  margin-top: 4px;
}
</style>
