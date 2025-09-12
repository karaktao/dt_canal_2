<template>
  <div class="info-vessel-root" v-bind="$attrs">
    <div class="vessel-card" role="region" aria-label="Vessel summary">
      <!-- 左列：基础信息 -->
      <div class="v-left">
        <div class="v-title">Vessel</div>
        <div class="v-name">{{ keyFields.name }}</div>
        <div class="v-meta">
          <div class="row"><span class="k">MMSI:</span><span class="v">{{ keyFields.mmsi }}</span></div>
          <div class="row"><span class="k">SOG:</span><span class="v">{{ keyFields.sog }}</span></div>
          <div class="row"><span class="k">COG:</span><span class="v">{{ keyFields.cog }}</span></div>
          <div class="row"><span class="k">Status:</span><span class="v">{{ keyFields.nav }}</span></div>
          <div class="row"><span class="k">Destination:</span><span class="v">{{ keyFields.dest }}</span></div>
          <div class="row"><span class="k">ETA:</span><span class="v">{{ keyFields.eta }}</span></div>
          <div class="row"><span class="k">Nearest lock:</span><span class="v">{{ keyFields.lock }}</span></div>
        </div>
      </div>

      <!-- 中间列：方形地图卡（只渲染地图，不叠加信息） -->
      <div class="v-middle">
        <div class="map-card">
          <div ref="mapDiv" class="map-container" />
        </div>
      </div>

      <!-- 右列：占位（你后续放方向/速度卡） -->
      <div class="v-right">
        <!-- 这里暂时空着，后续放 direction / speed 小卡 -->
      </div>
    </div>
  </div>
</template>

<script setup>
/* 地图依赖（OpenLayers）*/
import "ol/ol.css";
import Map from "ol/Map";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import OSM from "ol/source/OSM";
import { fromLonLat } from "ol/proj";

import { ref, computed, onMounted, onBeforeUnmount } from "vue";

/* 示例 vessel JSON（直接内嵌） */
const vessel = ref({
  "mmsi": 244100001,
  "imo": 0,
  "name": "TWENTE BARGE 1",
  "callsign": "PA001",
  "ship_type": "Cargo barge",
  "lat": 52.130000,
  "lon": 6.550000,
  "timestamp": "2025-09-12T13:40:00Z",
  "sog_knots": 2.0,
  "cog_deg": 80.0,
  "heading_deg": 80,
  "nav_status": "Under way using engine",
  "length_m": 85,
  "beam_m": 11,
  "draught_m": 2.4,
  "destination": "Zutphen",
  "eta": "2025-09-12T15:40:00Z",
  "eta_delta_min": -5,
  "distance_to_destination_m": 7600,
  "nearest_lock": "Eefde Voorsluis",
  "water_level_m": 1.25,
  "wind_speed_ms": 5.2,
  "position_accuracy": 1,
  "ais_message_type": 1,
  "last_report_minutes": 0.5,
  "alerts": ["none"]
});

const prettyJson = computed(() => {
  try { return JSON.stringify(vessel.value, null, 2); }
  catch (e) { return String(vessel.value); }
});

const keyFields = computed(() => {
  const v = vessel.value || {};
  return {
    name: v.name ?? "—",
    mmsi: v.mmsi ?? "—",
    sog: (v.sog_knots !== undefined && v.sog_knots !== null) ? `${v.sog_knots} kn` : "—",
    cog: (v.cog_deg !== undefined && v.cog_deg !== null) ? `${v.cog_deg}°` : "—",
    nav: v.nav_status ?? "—",
    dest: v.destination ?? "—",
    eta: v.eta ?? "—",
    lock: v.nearest_lock ?? "—",
  };
});

/* Map setup */
const mapDiv = ref(null);
let map = null;

onMounted(() => {
  // 防护：如果没有经纬度，使用一个默认中心
  const lon = Number(vessel.value.lon ?? 6.55);
  const lat = Number(vessel.value.lat ?? 52.13);
  const center = fromLonLat([lon, lat]);

  map = new Map({
    target: mapDiv.value,
    layers: [
      new TileLayer({
        source: new OSM()
      })
    ],
    view: new View({
      center,
      zoom: 14,
      projection: 'EPSG:3857'
    }),
    controls: []
  });
});

onBeforeUnmount(() => {
  if (map) {
    map.setTarget(null);
    map = null;
  }
});
</script>

<style scoped>
.info-vessel-root { width: 100%; box-sizing: border-box; }

/* 三列按比例 2 : 4 : 2，同时加入 minmax 保证最小宽度 */
.vessel-card {
  display: grid;
  gap: 12px;
  align-items: start;
  grid-template-columns: minmax(160px, 2fr) minmax(300px, 4fr) minmax(120px, 2fr);
  width: 100%;
  box-sizing: border-box;
}

/* 左列样式 */
.v-left { padding: 6px 4px; }
.v-title { font-size: 12px; color: #5e7176; font-weight: 600; margin-bottom: 4px; }
.v-name { font-size: 14px; font-weight: 800; color: #0f2930; margin-bottom: 8px; }
.v-meta .row { display:flex; justify-content:space-between; gap:8px; margin: 3px 0; font-size: 12px; color:#2f4850; }
.v-meta .k { color:#6d8387; font-weight:600; }
.v-meta .v { color:#0e2a2f; text-align:right; }

/* 中间列：方形地图卡片 */
.v-middle { display:flex; align-items: center; justify-content:center; padding: 6px 4px; }
.map-card {
  width: 100%;
  /* 方形：长宽相等 */
  aspect-ratio: 1 / 1;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 6px rgba(0,0,0,0.06);
  background: #fff;
  display:flex;
  flex-direction:column;
}

/* 地图容器填满卡片 */
.map-container {
  width: 100%;
  height: 100%;
}

/* 右列占位样式（后续放方向/速度卡） */
.v-right { padding: 6px 4px; display:flex; flex-direction:column; gap:8px; }

/* 响应式：窄屏时堆叠为单列 */
@media (max-width: 900px) {
  .vessel-card {
    grid-template-columns: 1fr;
  }
  .map-card { aspect-ratio: auto; height: 320px; } /* 小屏下取消正方形，给固定高度 */
}
</style>
