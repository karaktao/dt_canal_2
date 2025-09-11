<script setup>
const emit = defineEmits(["refresh"]);

import { ref, computed, watch, onMounted, onBeforeUnmount } from "vue";
import axios from "axios";
import proj4 from "proj4";
import { register } from "ol/proj/proj4";
import { transform } from "ol/proj";
import * as echarts from "echarts";

/* ========== Props ========== */
const props = defineProps({
  selectedCoordinates: { type: Array, default: null },
  location: { type: String, default: "" },
  geoFeatures: { type: Array, default: () => [] },
  data: { type: Object, default: () => ({}) },
  pollIntervalMs: { type: Number, default: 60 * 1000 },
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
const windGustMs = ref(null);
const windDir = ref(null);
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
      axios.get(dailyUrl).catch((e) => ({ data: null, error: e })),
    ]);

    const curData = curRes?.data ?? null;
    const current = curData?.current ?? curData?.current_weather ?? null;
    applyCurrentData(current);

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
    windDir.value = null;
    windGustMs.value = null;
    precipitation.value = null;
    humidity.value = null;
    visibility.value = null;
    weatherCode.value = null;
    return;
  }

  const temp =
    current.temperature_2m ?? current.temperature ?? current.temp ?? null;
  const wind =
    current.wind_speed_10m ?? current.wind_speed ?? current.windspeed ?? null;
  const gust =
    current.wind_gusts_10m ?? current.wind_gust ?? current.gusts ?? null;
  const dir =
    current.wind_direction_10m ??
    current.winddirection_10m ??
    current.wind_dir ??
    null;
  const precip =
    current.precipitation ?? current.rain ?? current.rain_1h ?? null;
  const hum =
    current.relative_humidity_2m ??
    current.relativehumidity_2m ??
    current.humidity ??
    null;
  const vis = current.visibility ?? current.visibility_metres ?? null;
  const code =
    current.weather_code ?? current.weathercode ?? current.code ?? null;

  tempC.value = isFiniteNumber(temp) ? Number(temp) : null;
  windMs.value = isFiniteNumber(wind) ? Number(wind) : null;
  windKn.value = isFiniteNumber(wind) ? Number(wind) * 1.9438444924406 : null;
  windGustMs.value = isFiniteNumber(gust) ? Number(gust) : null;
  windDir.value = isFiniteNumber(dir) ? Number(dir) : null;
  precipitation.value = isFiniteNumber(precip)
    ? Number(precip)
    : precip ?? null;
  humidity.value = isFiniteNumber(hum) ? Number(hum) : hum ?? null;
  visibility.value = isFiniteNumber(vis) ? Number(vis) : vis ?? null;
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
      "precipitation_probability_max",
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
  if (
    Array.isArray(props.selectedCoordinates) &&
    props.selectedCoordinates.length >= 2
  )
    return props.selectedCoordinates;
  if (
    props.data &&
    Array.isArray(props.data.selectedCoordinates) &&
    props.data.selectedCoordinates.length >= 2
  )
    return props.data.selectedCoordinates;
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
watch(
  () => props.selectedCoordinates,
  (nv) => {
    if (nv && Array.isArray(nv) && nv.length >= 2) doFetchOnce();
  },
  { immediate: true }
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
    if (coords) doFetchOnce();
  },
  { immediate: true }
);

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
  const v =
    windKn.value ??
    (isFiniteNumber(windMs.value)
      ? Number(windMs.value) * 1.9438444924406
      : null);
  return !isFiniteNumber(v)
    ? "N/A"
    : `${(Math.round(v * 10) / 10).toFixed(1)} kn`;
});
const windMsDisplay = computed(() => {
  const v =
    windMs.value ??
    (isFiniteNumber(windKn.value)
      ? Number(windKn.value) / 1.9438444924406
      : null);
  return !isFiniteNumber(v)
    ? ""
    : `${(Math.round(v * 10) / 10).toFixed(1)} m/s`;
});
/* 阵风显示：m/s */
const windGustMsDisplay = computed(() => {
  const v = windGustMs.value;
  if (!isFiniteNumber(v)) return "";
  const ms = Number(v);
  // 保留1位小数，四舍五入
  return `${(Math.round(ms * 10) / 10).toFixed(1)} m/s`;
});

/* 阵风显示：kn */
const windGustKnDisplay = computed(() => {
  const v = windGustMs.value;
  if (!isFiniteNumber(v)) return "";
  const kn = Number(v) * 1.9438444924406;
  return `${(Math.round(kn * 10) / 10).toFixed(1)} kn`;
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

function degreesToCardinal(deg) {
  if (!isFiniteNumber(deg)) return "";
  const d = Number(deg);
  const cards = [
    "N",
    "NNE",
    "NE",
    "ENE",
    "E",
    "ESE",
    "SE",
    "SSE",
    "S",
    "SSW",
    "SW",
    "WSW",
    "W",
    "WNW",
    "NW",
    "NNW",
    "N",
  ];
  const idx = Math.round((d % 360) / 22.5);
  return cards[idx];
}
const windDirDisplay = computed(() => {
  const d = windDir.value;
  if (!isFiniteNumber(d)) return "";
  return `${Math.round(d)}° (${degreesToCardinal(d)})`;
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

/* ============================
   折线图表
   ============================ */
const chartRef = ref(null);
let chartInstance = null;

function formatDateDDMM(dateStr) {
  // 尝试解析 ISO 字符串（例如 "2025-09-09"）
  const d = new Date(dateStr);
  if (isNaN(d)) {
    // 如果不是标准日期，直接返回原字符串（降级）
    return String(dateStr ?? "");
  }
  const dd = String(d.getDate()).padStart(2, "0");
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  return `${dd}-${mm}`;
}

/* 将 dailySummary 转成图表数据 */
function buildWeeklyOption() {
  const ds = Array.isArray(dailySummary.value) ? dailySummary.value : [];

  const dates = ds.map((d) => formatDateDDMM(d?.date ?? d?.time ?? ""));

  const precip = (dailySummary.value || []).map((d) => {
    const v = d?.precipitation_sum ?? d?.precipitation ?? null;
    return isFiniteNumber(v) ? Number(v) : null;
  });

  const windMax = (dailySummary.value || []).map((d) => {
    const v = d?.wind_speed_10m_max ?? d?.wind_speed_max ?? null;
    return isFiniteNumber(v) ? Number(v) : null;
  });

  return {
    backgroundColor: "transparent",
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "shadow" },
      textStyle: { fontSize: 10 },
      formatter: (params) => {
        // params 是数组，按 series 顺序
        let out = params[0]?.axisValueLabel ?? "";
        params.forEach((p) => {
          out += `<br/>${p.marker} ${p.seriesName}: ${
            p.value == null ? "N/A" : p.value
          }`;
          if (p.seriesName === "Wind (max)") out += " km/h";
          if (p.seriesName === "Precipitation") out += " mm";
        });
        return out;
      },
    },
    legend: {
      data: ["Precipitation", "Wind (max)"],
      top: 6,
      left: "center",
      textStyle: { fontSize: 11 },
    },
    grid: { left: 54, right: 64, top: 36, bottom: 20, height: 75 },
    xAxis: [
      {
        type: "category",
        data: dates,
        boundaryGap: false,
        axisLabel: {
          formatter: (v) => v,
          rotate: 0,
          fontSize: 10,
          color: "#6e7a7f",
        },
        axisLine: { lineStyle: { color: "#e6eef0" } },
      },
    ],
    yAxis: [
      {
        type: "value",
        name: "Precip (mm)",
        position: "left",
        offset: 10,
        axisLabel: { formatter: "{value}" },
        nameTextStyle: { fontSize: 10 },
        splitLine: { lineStyle: { color: "#f3f7f8" } },
      },
      {
        type: "value",
        name: "Wind (km/h)",
        position: "right",
        offset: 10,
        axisLabel: { formatter: "{value}" },
        nameTextStyle: { fontSize: 10 },
        splitLine: { show: false },
      },
    ],
    series: [
      {
        name: "Precipitation",
        type: "bar", // <- 改成柱状图
        yAxisIndex: 0,
        data: precip,
        barMaxWidth: 25, // 柱宽，可调整
        itemStyle: { color: "#5470C6", opacity: 0.75 },
        emphasis: { focus: "series" },
      },
      {
        name: "Wind (max)",
        type: "line",
        smooth: true,
        yAxisIndex: 1,
        data: windMax,
        showSymbol: true,
        symbolSize: 5,
        lineStyle: { width: 1.5, color: "#91CC75" },
        itemStyle: { color: '#91CC75' },
      },
    ],
  };
}

/* 初始化 / 更新 / 销毁 图表 */
function initChart() {
  if (!chartRef.value) return;
  chartInstance =
    echarts.getInstanceByDom(chartRef.value) ?? echarts.init(chartRef.value);
  const opt = buildWeeklyOption();
  chartInstance.setOption(opt, { notMerge: true });
}

function resizeChart() {
  try {
    chartInstance?.resize();
  } catch (e) {
    /* ignore */
  }
}

/* 监听 dailySummary 变化并更新图表 */
watch(
  () => dailySummary.value,
  (nv) => {
    if (!chartInstance && chartRef.value) initChart();
    if (chartInstance) {
      const opt = buildWeeklyOption();
      chartInstance.setOption(opt);
      resizeChart();
    }
  },
  { immediate: true, deep: true }
);

/* 挂载与卸载：创建图表并监听窗口 resize */
onMounted(() => {
  if (chartRef.value) initChart();
  window.addEventListener("resize", resizeChart);
});
onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeChart);
  try {
    chartInstance?.dispose();
    chartInstance = null;
  } catch (e) {}
});
</script>

<template>
  <div class="info-weather-root" v-bind="$attrs">
    <div class="weather-card" role="region" aria-label="Weather summary">
      <div class="left">
        <div class="location" v-if="location">{{ location }}</div>
        <div class="temp-block" aria-hidden="false">
          <div class="temp-icon">
            <i :class="thermIconClass" aria-hidden="true"></i>
          </div>
          <div class="temp-values">
            <div class="temp-main">{{ tempCDisplay }}</div>
            <div class="temp-sub">{{ tempFDisplay }}</div>
          </div>
        </div>
      </div>

      <div class="right" role="list" aria-label="Other weather stats">
        <!-- 右侧网格：两列，每列两项 -->
        <div class="right-grid">
          <div class="stat" role="listitem" aria-label="Windspeed">
            <div class="stat-icon"><i :class="windIconClass"></i></div>
            <div class="stat-body">
              <div class="stat-label">Windspeed</div>
              <div class="stat-main">{{ windKnDisplay }}</div>
              <div class="stat-sub">{{ windDirDisplay }}</div>
            </div>
          </div>

          <div class="stat" role="listitem" aria-label="Visibility">
            <div class="stat-icon"><i :class="visibilityIconClass"></i></div>
            <div class="stat-body">
              <div class="stat-label">Visibility</div>
              <div class="stat-main">{{ visibilityDisplay }}</div>
              <div class="stat-sub">{{ weatherCodeLabel }}</div>
            </div>
          </div>

          <div class="stat" role="listitem" aria-label="Windgust">
            <div class="stat-icon"><i :class="windIconClass"></i></div>
            <div class="stat-body">
              <div class="stat-label">Windgust</div>
              <div class="stat-main">{{ windGustKnDisplay }}</div>
              <div class="stat-sub">{{ windGustMsDisplay }}</div>
            </div>
          </div>

          <div class="stat" role="listitem" aria-label="Precipitation">
            <div class="stat-icon"><i :class="precipIconClass"></i></div>
            <div class="stat-body">
              <div class="stat-label">Precipitation</div>
              <div class="stat-main">{{ precipitationDisplay }}</div>
              <div class="stat-sub">{{ humidityDisplay }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div ref="chartRef" class="weather-chart" aria-hidden="false"></div>
  </div>
</template>


<style scoped>
/* 宽度固定为 400px，左右两列并排 */
.weather-card {
  display: flex !important;
  flex-direction: row !important;
  flex-wrap: nowrap !important;
  width: 450px !important;
  box-sizing: border-box !important;
  padding: 8px !important;
  align-items: flex-start !important;
  gap: 8px !important;
  margin-top: -10px !important;
  border-radius: 8px !important;
  /* background: #fff !important; */
  overflow: visible !important;
  outline: 1px dashed rgba(200, 80, 80, 0.08); /* 临时可视化边框，方便调试 */
}



/* 左侧区域 */
.left {
  display: flex;
  flex-direction: column; /* 改为列方向，确保 location 在上 */
  align-items: flex-start; /* 左对齐内容 */
  padding: 5px 10px;
  flex: 0 0 160px;
  box-sizing: border-box;
  /* background: linear-gradient(180deg, #f7fbfd 0, #ffffff 100%); */
}
.location {
  font-size: 13px;
  color: #7a9096;
  margin-top: 4px;
  margin-bottom: 25px;
}

/* 温度块布局 */
.temp-block {
  display: flex;
  gap: 12px;
  align-items: center;
}
.temp-icon .wi {
  font-size: 45px;
  color: #2f98c5;
  line-height: 1;
}
.temp-values {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  text-align: left;
}
.temp-values .location {
  font-size: 12px;
  color: #4b5a5f;
  margin-bottom: 6px;
}
.temp-main {
  font-size: 22px;
  font-weight: 800;
  color: #0f2930;
  line-height: 1;
  margin-top: 0px;
}
.temp-sub {
  font-size: 15px;
  color: #7a9096;
  margin-top: 4px;
}

/* 右侧整体（占剩余宽度） */
.right {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0px 0px;
  flex: 1 1 auto;
  box-sizing: border-box;
}

/* 右侧网格：两列布局 */
.right-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 两列 */
  gap: 6px 10px; /* row-gap 6px, column-gap 10px */
  align-items: start;
  width: 100%;
  box-sizing: border-box;
}

/* 每个 stat 作为 grid 单元 */
.stat {
  display: flex;
  gap: 8px;
  align-items: center;
  padding: 6px 6px; /* 紧凑内边距 */
  border-radius: 6px;
  transition: background 0.12s ease, transform 0.08s ease;
  background: transparent;
}

/* hover 效果可选 */
.stat:hover,
.stat:focus-within {
  background: rgba(47, 152, 197, 0.03);
  transform: translateY(-1px);
}

/* 右侧 icon 与文字 */
.stat-icon .wi {
  font-size: 18px;
  color: #5ca6bd;
  min-width: 20px;
  text-align: center;
}
.stat-body {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-width: 0;
}
.stat-label {
  font-size: 11px;
  color: #3d5763;
  margin-bottom: 2px;
  font-weight: 600;
}
.stat-main {
  font-size: 13px;
  font-weight: 700;
  color: #11292f;
  line-height: 1;
}
.stat-sub {
  font-size: 11px;
  color: #7f959a;
  margin-top: 3px;
}

/* 天气折线图样式：放在卡片下方，占宽度并有固定高度 */
.weather-chart {
  width: 100%;
  height: 135px;
  margin-top: -15px;
  box-sizing: border-box;
  border-radius: 6px;
  margin-bottom: -15px;
  /* 你可以加上背景/边框以区分： */
  /* background: #fbfeff; border: 1px solid rgba(8,20,30,0.04); padding:6px; */
}
</style>
