<template>
  <div class="weather-bar" role="region" aria-label="Weather summary">
    <div class="wb-items">
      <!-- Temperature (使用 Weather Icons via CDN) -->
      <div class="wb-item">
        <div class="icon-wrap" aria-hidden="true">
          <i :class="thermIconClass" aria-hidden="true"></i>
        </div>
        <div class="val">
          <div class="main">{{ tempCDisplay }}</div>
          <div class="sub">{{ tempFDisplay }}</div>
        </div>
      </div>

      <!-- Wind -->
      <div class="wb-item">
        <div class="icon-wrap" aria-hidden="true">
          <i :class="windIconClass" aria-hidden="true"></i>
        </div>
        <div class="val">
          <div class="main">{{ windKnDisplay }}</div>
          <div class="sub">{{ windMsDisplay }}</div>
        </div>
      </div>

      <!-- precipitation -->
      <div class="wb-item">
        <div class="icon-wrap" aria-hidden="true">
          <i :class="precipIconClass" aria-hidden="true"></i>
        </div>
        <div class="val">
          <div class="main">{{ precipitationDisplay }}</div>
          <div class="sub">{{ humidityDisplay }}</div>
        </div>
      </div>

      <!-- visibility -->
      <div class="wb-item">
        <div class="icon-wrap" aria-hidden="true">
          <i :class="visibilityIconClass" aria-hidden="true"></i>
        </div>
        <div class="val">
          <div class="main">{{ visibilityDisplay }}</div>
          <div class="sub">{{ weatherCodeLabel }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount } from "vue";
import axios from "axios";
import proj4 from "proj4";
import { register } from "ol/proj/proj4";
import { transform } from "ol/proj";

/* ========== Props ========== */
const props = defineProps({
  selectedCoordinates: { type: Array, default: null },
  location: { type: String, default: "" },
  geoFeatures: { type: Array, default: () => [] },
  data: { type: Object, default: () => ({}) },
  pollIntervalMs: { type: Number, default: 60 * 1000 }
});

/* ========== State ========== */
const rawCurrent = ref(null);
const lastMinutely = ref(null);
const hourlyPreview = ref([]);
const dailySummary = ref([]);

const pollTimer = ref(null);
let running = false;

/* fields used in UI */
const tempC = ref(null);
const windMs = ref(null);
const windKn = ref(null);
const precipitation = ref(null);
const humidity = ref(null);
const visibility = ref(null);
const weatherCode = ref(null);

/* ========== proj registration & helpers ========== */
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
registerProjs();

/* ---------- coord helpers (unchanged) ---------- */
function findCoordsFromLocation(name) {
  if (!name || !Array.isArray(props.geoFeatures) || props.geoFeatures.length === 0)
    return null;
  const key = String(name).trim().toLowerCase();
  for (const f of props.geoFeatures) {
    const fname = String(f?.properties?.name ?? "").trim().toLowerCase();
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
    if (!isFinite(lat) || !isFinite(lon)) {
      const lon2 = Number(coords[0]);
      const lat2 = Number(coords[1]);
      if (isFinite(lon2) && isFinite(lat2)) return { lat: lat2, lon: lon2 };
      return null;
    }
    return { lat, lon };
  } catch (e) {
    const lon = Number(coords[0]);
    const lat = Number(coords[1]);
    if (isFinite(lon) && isFinite(lat)) return { lat, lon };
    console.error("coord transform failed:", e);
    return null;
  }
}

/* ========== Weather fetch/parsing (robust) ========== */
/* 保持你已有的 fetch / parse 逻辑不变 */
function weatherCodeToLabel(code) {
  const c = Number(code);
  if (Number.isNaN(c)) return String(code || "");
  if (c === 0) return "Clear";
  if (c >= 1 && c <= 3) return "Partly cloudy";
  if (c === 45 || c === 48) return "Fog";
  if (c >= 51 && c <= 67) return "Rain";
  if (c >= 71 && c <= 77) return "Snow";
  if (c >= 80 && c <= 82) return "Showers";
  if (c >= 95 && c <= 99) return "Thunder";
  return String(code);
}

async function fetchWeatherForCoordinates(coords) {
  const latlon = coordsToLatLon(coords);
  if (!latlon) {
    console.warn("No valid coords for weather request");
    applyEmpty();
    return;
  }
  const { lat, lon } = latlon;

  const currentUrl = `https://api.open-meteo.com/v1/forecast?latitude=${encodeURIComponent(
    lat
  )}&longitude=${encodeURIComponent(
    lon
  )}&models=best_match&current=temperature_2m,precipitation,wind_speed_10m,wind_direction_10m,wind_gusts_10m,rain,relative_humidity_2m,weather_code,visibility&timezone=Europe%2FAmsterdam`;
  const minutelyUrl = `https://api.open-meteo.com/v1/forecast?latitude=${encodeURIComponent(
    lat
  )}&longitude=${encodeURIComponent(
    lon
  )}&models=best_match&minutely_15=precipitation,visibility,wind_speed_10m&timezone=Europe%2FAmsterdam`;
  const hourlyUrl = `https://api.open-meteo.com/v1/forecast?latitude=${encodeURIComponent(
    lat
  )}&longitude=${encodeURIComponent(
    lon
  )}&hourly=temperature_2m,wind_speed_10m,precipitation,precipitation_probability,visibility,weather_code&models=best_match&timezone=Europe%2FAmsterdam`;
  const dailyUrl = `https://api.open-meteo.com/v1/forecast?latitude=${encodeURIComponent(
    lat
  )}&longitude=${encodeURIComponent(
    lon
  )}&daily=temperature_2m_max,temperature_2m_min,wind_direction_10m_dominant,wind_speed_10m_max,precipitation_sum,precipitation_probability_max&hourly=temperature_2m,wind_speed_10m,precipitation,precipitation_probability,visibility,weather_code&models=best_match&timezone=Europe%2FAmsterdam`;

  try {
    const [curRes, hourlyRes, dailyRes] = await Promise.all([
      axios.get(currentUrl).catch((e) => ({ data: null, error: e })),
      axios.get(hourlyUrl).catch((e) => ({ data: null, error: e })),
      axios.get(dailyUrl).catch((e) => ({ data: null, error: e }))
    ]);

    const curData = curRes?.data ?? null;
    const current = curData?.current ?? curData?.current_weather ?? null;
    applyCurrentData(current);

    try {
      const minRes = await axios.get(minutelyUrl).catch((e) => ({ data: null, error: e }));
      const minData = minRes?.data ?? null;
      const minObj = minData?.minutely_15 ?? minData?.minutely ?? null;
      processMinutelyData(minObj);
    } catch (e) {
      processMinutelyData(null);
    }

    const hourlyData = hourlyRes?.data ?? null;
    const hourlyObj = hourlyData?.hourly ?? null;
    processHourlyData(hourlyObj);

    const dailyData = dailyRes?.data ?? null;
    const dailyObj = dailyData?.daily ?? null;
    processDailyData(dailyObj);
  } catch (err) {
    console.error("fetchWeather failed:", err);
    applyEmpty();
  }
}

function applyCurrentData(current) {
  rawCurrent.value = current ?? null;
  if (!current) {
    tempC.value = null;
    windMs.value = null;
    windKn.value = null;
    precipitation.value = null;
    humidity.value = null;
    visibility.value = null;
    weatherCode.value = null;
    return;
  }

  const temp =
    current.temperature_2m ??
    current.temperature ??
    current.temp ??
    null;
  const wind =
    current.wind_speed_10m ??
    current.wind_speed ??
    current.windspeed ??
    null;
  const precip =
    current.precipitation ??
    current.rain ??
    current.rain_1h ??
    null;
  const hum =
    current.relative_humidity_2m ??
    current.relativehumidity_2m ??
    current.humidity ??
    null;
  const vis =
    current.visibility ??
    current.visibility_metres ??
    null;
  const code =
    current.weather_code ??
    current.weathercode ??
    current.code ??
    null;

  tempC.value = isFiniteNumber(temp) ? Number(temp) : null;
  windMs.value = isFiniteNumber(wind) ? Number(wind) : null;
  windKn.value = isFiniteNumber(wind) ? Number(wind) * 1.9438444924406 : null;
  precipitation.value = isFiniteNumber(precip) ? Number(precip) : (precip ?? null);
  humidity.value = isFiniteNumber(hum) ? Number(hum) : (hum ?? null);
  visibility.value = isFiniteNumber(vis) ? Number(vis) : (vis ?? null);
  weatherCode.value = code ?? null;
}

function isFiniteNumber(v) {
  return v !== null && v !== undefined && v !== "" && isFinite(Number(v));
}

function processMinutelyData(minutely) {
  if (!minutely) {
    lastMinutely.value = null;
    return;
  }
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
}

function processHourlyData(hourly) {
  hourlyPreview.value = [];
  if (!hourly) return;
  const times = hourly.time ?? hourly.times ?? null;
  if (!Array.isArray(times) || times.length === 0) return;
  const lastIdx = times.length - 1;
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
function processDailyData(daily) {
  dailySummary.value = [];
  if (!daily) return;
  const times = daily.time ?? daily.times ?? null;
  if (!Array.isArray(times) || times.length === 0) return;
  for (let i = 0; i < Math.min(7, times.length); i++) {
    const item = { date: times[i] };
    const maybe = [
      "temperature_2m_min",
      "temperature_2m_max",
      "wind_direction_10m_dominant",
      "wind_speed_10m_max",
      "precipitation_sum",
      "precipitation_probability_max"
    ];
    for (const k of maybe) {
      const arr = daily[k];
      if (Array.isArray(arr) && arr.length > i) item[k] = arr[i];
    }
    dailySummary.value.push(item);
  }
}

/* empty state setter */
function applyEmpty() {
  tempC.value = null;
  windMs.value = null;
  windKn.value = null;
  precipitation.value = null;
  humidity.value = null;
  visibility.value = null;
  weatherCode.value = null;
  lastMinutely.value = null;
  hourlyPreview.value = [];
  dailySummary.value = [];
}

/* resolve coordinates */
function resolveCoordinatesSource() {
  if (Array.isArray(props.selectedCoordinates) && props.selectedCoordinates.length >= 2) return props.selectedCoordinates;
  if (props.data && Array.isArray(props.data.selectedCoordinates) && props.data.selectedCoordinates.length >= 2) return props.data.selectedCoordinates;
  if (props.location && Array.isArray(props.geoFeatures) && props.geoFeatures.length) {
    const found = findCoordsFromLocation(props.location);
    if (found && Array.isArray(found) && found.length >= 2) return found;
  }
  return null;
}

/* polling control */
async function doFetchOnce() {
  const coords = resolveCoordinatesSource();
  if (coords) {
    await fetchWeatherForCoordinates(coords);
  } else {
    applyEmpty();
  }
}
function startPolling() {
  if (running) return;
  running = true;
  doFetchOnce();
  pollTimer.value = setInterval(doFetchOnce, props.pollIntervalMs);
}
function stopPolling() {
  if (!running) return;
  running = false;
  if (pollTimer.value) {
    clearInterval(pollTimer.value);
    pollTimer.value = null;
  }
}

/* lifecycle */
onMounted(() => {
  startPolling();
});
onBeforeUnmount(() => {
  stopPolling();
});
watch(() => props.selectedCoordinates, (nv) => {
  if (nv && Array.isArray(nv) && nv.length >= 2) doFetchOnce();
}, { immediate: true });
watch(() => props.location, (nv) => {
  const direct = Array.isArray(props.selectedCoordinates) && props.selectedCoordinates.length >= 2;
  const dataDirect = props.data && Array.isArray(props.data.selectedCoordinates) && props.data.selectedCoordinates.length >= 2;
  if (direct || dataDirect) return;
  if (!nv) return;
  const coords = findCoordsFromLocation(nv);
  if (coords) doFetchOnce();
}, { immediate: true });

/* ========== computed displays ========== */
const tempCDisplay = computed(() => {
  const v = tempC.value;
  if (!isFiniteNumber(v)) return "N/A";
  return `${Math.round(Number(v))} °C`;
});
const tempFDisplay = computed(() => {
  const v = tempC.value;
  if (!isFiniteNumber(v)) return "";
  const f = Math.round((Number(v) * 9) / 5 + 32);
  return `${f} °F`;
});
const windKnDisplay = computed(() => {
  const v = windKn.value ?? (isFiniteNumber(windMs.value) ? Number(windMs.value) * 1.9438444924406 : null);
  return !isFiniteNumber(v) ? "N/A" : `${(Math.round(v * 10) / 10).toFixed(1)} kn`;
});
const windMsDisplay = computed(() => {
  const v = windMs.value ?? (isFiniteNumber(windKn.value) ? Number(windKn.value) / 1.9438444924406 : null);
  return !isFiniteNumber(v) ? "" : `${(Math.round(v * 10) / 10).toFixed(1)} m/s`;
});
const precipitationDisplay = computed(() => {
  const v = precipitation.value;
  if (!isFiniteNumber(v)) return "N/A";
  return `${Number(v)} mm/h`;
});
const humidityDisplay = computed(() => {
  const v = humidity.value;
  if (!isFiniteNumber(v)) return "";
  return `${Math.round(Number(v))} %`;
});
const visibilityDisplay = computed(() => {
  const v = visibility.value;
  if (!isFiniteNumber(v)) return "N/A";
  const n = Number(v);
  if (n > 50) return `${(n / 1000).toFixed(1)} km`;
  return `${n} km`;
});
const weatherCodeLabel = computed(() => {
  return weatherCode.value == null ? "" : weatherCodeToLabel(weatherCode.value);
});

/* ============================
   Weather Icons (CDN) 映射
   使用 weather-icons 的 class (例如: "wi wi-day-sunny")
   ============================ */

/* weather_code -> weather-icons class */
function weatherCodeToWiClass(code) {
  const c = Number(code);
  if (Number.isNaN(c)) return "wi wi-day-cloudy";
  if (c === 0) return "wi wi-day-sunny";
  if (c >= 1 && c <= 3) return "wi wi-day-cloudy";
  if (c === 45 || c === 48) return "wi wi-fog";
  if (c >= 51 && c <= 67) return "wi wi-rain";
  if ((c >= 71 && c <= 77) || c === 85 || c === 86) return "wi wi-snow";
  if (c >= 80 && c <= 82) return "wi wi-showers";
  if (c >= 95 && c <= 99) return "wi wi-thunderstorm";
  return "wi wi-day-cloudy";
}

/* 温度图标类（根据 weatherCode） */
const thermIconClass = computed(() => weatherCodeToWiClass(weatherCode.value));

/* 风速/风向图标：简单根据风速判断强风或普通风 */
const windIconClass = computed(() => {
  const v = windMs.value;
  if (!isFiniteNumber(v)) return "wi wi-windy";
  if (Number(v) >= 10) return "wi wi-strong-wind";
  return "wi wi-windy";
});

/* 降水图标：有降水显示雨滴图标，否则显示水滴/湿度图标 */
const precipIconClass = computed(() => {
  const v = precipitation.value;
  if (!isFiniteNumber(v)) return "wi wi-humidity";
  if (Number(v) > 0) return "wi wi-raindrops";
  return "wi wi-humidity";
});

/* 能见度图标：低能见度显示雾，高则显示晴/多云（基于 visibility 值，单位假设为米） */
const visibilityIconClass = computed(() => {
  const v = visibility.value;
  if (!isFiniteNumber(v)) return "wi wi-day-cloudy";
  const n = Number(v);
  if (n < 500) return "wi wi-fog";
  if (n < 2000) return "wi wi-day-cloudy";
  return "wi wi-day-sunny";
});
</script>

<style scoped>
/* (保持你的 style，不变) */
.weather-bar {
  width: 500px;
  border-radius: 6px;
  overflow: hidden;
  font-family: Inter, "Helvetica Neue", Arial, sans-serif;
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.04);
  background: #ffffff;
  border: 1px solid rgba(8, 20, 30, 0.06);
}

.wb-items {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 6px 10px;
  background: #fff;
}

/* each item */
.wb-item {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  flex: 1 1 0;
}

/* icon container */
.icon-wrap {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* Weather Icons 字体大小与颜色控制（确保你已在 index.html 引入 CDN） */
.icon-wrap .wi {
  font-size: 28px;
  line-height: 1;
  display: inline-block;
  color: #2F98C5; /* 默认蓝色，可按需修改 */
}

/* value layout */
.val {
  display: flex;
  flex-direction: column;
  line-height: 1;
  min-width: 0;
}
.val .main {
  font-weight: 700;
  font-size: 14px;
  color: #152D33;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
.val .sub {
  font-size: 11px;
  color: #627D86;
  margin-top: 2px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

/* responsive */
@media (max-width: 420px) {
  .weather-bar { width: 100%; }
  .val .main { font-size: 12px; }
  .val .sub { font-size: 10px; }
}
</style>
