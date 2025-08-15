<template>
  <el-card class="info-box">

    <div class="content" style="padding:12px;">
      <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
        <el-tab-pane label="Current" name="first">
          <div class="tab-pane-content">
            <div style="display:grid; grid-template-columns: 1fr 1fr; gap:8px;">
              <div>
                <div class="label">Temperature</div>
                <div class="value">{{ temp }}</div>
              </div>
              <div>
                <div class="label">Wind Speed</div>
                <div class="value">{{ windSpeed }}</div>
              </div>

              <div>
                <div class="label">Visibility</div>
                <div class="value">{{ visibility }}</div>
              </div>
              <div>
                <div class="label">Humidity</div>
                <div class="value">{{ humidity }}</div>
              </div>

              <div style="grid-column: 1 / -1;">
                <div class="label">Weather</div>
                <div class="value">{{ weatherCodeText }}</div>
              </div>

              <div style="grid-column: 1 / -1; text-align:right; color:#666; font-size:12px;">
                Last: {{ lastUpdated ? new Date(lastUpdated).toLocaleString() : '—' }}
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="Today" name="second">
          <div class="tab-pane-content">
            <div class="blank-placeholder" aria-hidden="true">(Today view - placeholder)</div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="Month" name="third">
          <div class="tab-pane-content">
            <div class="blank-placeholder" aria-hidden="true">(Month view - placeholder)</div>
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
  // 优先接收父组件传来的三样东西（任选其一或多项）：
  // 1) selectedCoordinates: 父组件已经解析好的坐标（通常是 EPSG:25831 的 [x,y]）
  // 2) location: 父组件的 location 字符串（infoE 的 location）
  // 3) geoFeatures: 父组件的 geoFeatures 数组（用于从 location 找到对应 feature）
  selectedCoordinates: { type: Array, default: null },
  location: { type: String, default: "" },
  geoFeatures: { type: Array, default: () => [] },

  // 向后兼容：如果父组件把整个 data 对象传过来（旧模式）
  data: { type: Object, default: () => ({}) },

  title: { type: String, default: "" }
});

const activeName = ref("first");
const temp = ref("—");
const windSpeed = ref("—");
const visibility = ref("—");
const humidity = ref("—");
const weatherCode = ref(null);
const weatherCodeText = ref("—");
const lastUpdated = ref(null);

// 注册 proj（EPSG:25831 用于将 infoE 的坐标转换为经纬度）
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

// 从 geoFeatures 中按 name 找到第一个匹配 feature 并返回其 geometry.coordinates（未转换）
function findCoordsFromLocation(name) {
  if (!name || !Array.isArray(props.geoFeatures) || props.geoFeatures.length === 0) return null;
  const key = String(name).trim().toLowerCase();
  for (const f of props.geoFeatures) {
    const fname = String(f?.properties?.name ?? "").trim().toLowerCase();
    if (fname === key) {
      return f?.geometry?.coordinates ?? null;
    }
  }
  return null;
}

// 把 coords（通常 EPSG:25831 的 [x,y]）转换为 {lat, lon} (EPSG:4326)
function coordsToLatLon(coords) {
  if (!coords || !Array.isArray(coords) || coords.length < 2) return null;
  try {
    const [lonT, latT] = transform([Number(coords[0]), Number(coords[1])], "EPSG:25831", "EPSG:4326");
    const lat = Number(latT);
    const lon = Number(lonT);
    if (!isFinite(lat) || !isFinite(lon)) return null;
    return { lat, lon };
  } catch (e) {
    console.error("coord transform failed:", e);
    return null;
  }
}

function weatherCodeToText(code) {
  if (code === undefined || code === null) return "—";
  const map = {
    0: "Clear sky",
    1: "Mainly clear",
    2: "Partly cloudy",
    3: "Overcast",
    45: "Fog",
    48: "Depositing rime fog",
    51: "Drizzle: Light",
    53: "Drizzle: Moderate",
    55: "Drizzle: Dense",
    61: "Rain: Slight",
    63: "Rain: Moderate",
    65: "Rain: Heavy",
    80: "Rain showers: Slight",
    81: "Rain showers: Moderate",
    95: "Thunderstorm: Slight or moderate"
  };
  return map[code] ?? `Code ${code}`;
}

function applyCurrentData(current, units) {
  if (!current) {
    temp.value = windSpeed.value = visibility.value = humidity.value = "—";
    weatherCode.value = null;
    weatherCodeText.value = "—";
    lastUpdated.value = null;
    return;
  }
  const t = current.temperature_2m;
  temp.value = t !== undefined && t !== null ? `${t}${units?.temperature_2m ? " " + units.temperature_2m : ""}` : "—";

  const w = current.wind_speed_10m;
  windSpeed.value = w !== undefined && w !== null ? `${w}${units?.wind_speed_10m ? " " + units.wind_speed_10m : ""}` : "—";

  const vis = current.visibility;
  visibility.value = vis !== undefined && vis !== null ? `${vis}${units?.visibility ? " " + units.visibility : ""}` : "—";

  const rh = current.relative_humidity_2m;
  humidity.value = rh !== undefined && rh !== null ? `${rh}${units?.relative_humidity_2m ? " " + units.relative_humidity_2m : ""}` : "—";

  weatherCode.value = current.weather_code ?? null;
  weatherCodeText.value = weatherCodeToText(weatherCode.value);
  lastUpdated.value = current.time ?? new Date().toISOString();
}

// 调用 Open-Meteo（同 infoE 的 current 请求字段）
async function fetchWeatherForCoordinates(coords) {
  const latlon = coordsToLatLon(coords);
  if (!latlon) {
    console.warn("No valid coords for weather request");
    applyCurrentData(null, null);
    return;
  }
  const { lat, lon } = latlon;
  const params = {
    latitude: lat,
    longitude: lon,
    models: "best_match",
    current: "temperature_2m,wind_speed_10m,wind_direction_10m,wind_gusts_10m,rain,relative_humidity_2m,weather_code,visibility",
    timezone: "Europe%2FAmsterdam"
  };
  const url = `https://api.open-meteo.com/v1/forecast?latitude=${encodeURIComponent(params.latitude)}&longitude=${encodeURIComponent(params.longitude)}&models=${params.models}&current=${encodeURIComponent(params.current)}&timezone=${params.timezone}`;

  try {
    const res = await axios.get(url);
    const data = res.data;
    const current = data?.current ?? null;
    const units = data?.current_units ?? null;
    applyCurrentData(current, units);
  } catch (err) {
    console.error("fetchWeather failed:", err);
    applyCurrentData(null, null);
  }
}

// 决定用哪个坐标源（优先级：prop.selectedCoordinates > prop.data.selectedCoordinates > 从 location+geoFeatures 查找）
function resolveCoordinatesSource() {
  if (Array.isArray(props.selectedCoordinates) && props.selectedCoordinates.length >= 2) {
    return props.selectedCoordinates;
  }
  if (props.data && Array.isArray(props.data.selectedCoordinates) && props.data.selectedCoordinates.length >= 2) {
    return props.data.selectedCoordinates;
  }
  // 尝试从 geoFeatures 和 location 匹配（与 infoE 中 tryAutoFillFromLocation 的思路一致）
  if (props.location && Array.isArray(props.geoFeatures) && props.geoFeatures.length) {
    const found = findCoordsFromLocation(props.location);
    if (found && Array.isArray(found) && found.length >= 2) return found;
  }
  return null;
}

// manual refresh（按钮/外部触发）
function manualRefresh() {
  const src = resolveCoordinatesSource();
  if (!src) {
    console.warn("No coords available to refresh weather");
    return;
  }
  fetchWeatherForCoordinates(src);
}

// 监听：如果父组件传来的 selectedCoordinates 改变，立即请求
watch(
  () => props.selectedCoordinates,
  (nv) => {
    if (nv && Array.isArray(nv) && nv.length >= 2) {
      fetchWeatherForCoordinates(nv);
    }
  },
  { immediate: true, deep: true }
);

// 监听 location：当 location 改变且没有 selectedCoordinates 时，尝试从 geoFeatures 查找并请求
watch(
  () => props.location,
  (nv) => {
    // 仅当没有直接的 selectedCoordinates 时才用 location 查找（避免覆盖）
    const direct = Array.isArray(props.selectedCoordinates) && props.selectedCoordinates.length >= 2;
    const dataDirect = props.data && Array.isArray(props.data.selectedCoordinates) && props.data.selectedCoordinates.length >= 2;
    if (direct || dataDirect) return;
    if (!nv) return;
    const coords = findCoordsFromLocation(nv);
    if (coords) {
      fetchWeatherForCoordinates(coords);
    }
  },
  { immediate: true }
);

// mounted 时注册投影并根据现有数据尝试初次拉取
onMounted(() => {
  registerProjs();
  // 首次尝试解析坐标源（优先选项详见 resolveCoordinatesSource）
  const initial = resolveCoordinatesSource();
  if (initial) fetchWeatherForCoordinates(initial);
});

function handleClick() { /* placeholder */ }
</script>

<style scoped>
.info-box { padding: 0; display:flex; flex-direction:column; width:100%; max-width:420px; }
::v-deep(.info-box .el-card__body) { padding:0; display:flex; flex-direction:column; flex:1; min-height:0; }
.label { font-weight:600; font-size:12px; }
.value { font-size:14px; margin-top:4px; }
</style>
