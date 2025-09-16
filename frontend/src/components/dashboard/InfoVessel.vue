<template>
  <div class="info-vessel-root" v-bind="$attrs">
    <div class="vessel-card" role="region" aria-label="Vessel summary">
      <!-- 左列：基础信息 -->
      <div class="v-left">
        <div class="v-name">{{ keyFields.name }}</div>
        <div class="v-meta">
          <div class="row"><span class="k">MMSI:</span><span class="v">{{ keyFields.mmsi }}</span></div>
          <div class="row"><span class="k">SOG:</span><span class="v">{{ keyFields.sog }}</span></div>
          <div class="row"><span class="k">COG:</span><span class="v">{{ keyFields.cog }}</span></div>
          <div class="row"><span class="k">Status:</span><span class="v">{{ keyFields.nav }}</span></div>
          <div class="row"><span class="k">Destination</span><span class="v">{{ keyFields.dest }}</span></div>
          <div class="row"><span class="k">ETA:</span><span class="v">{{ keyFields.eta }}</span></div>
          <div class="row"><span class="k">Nearest lock:</span><span class="v">{{ keyFields.lock }}</span></div>
        </div>
      </div>

      <!-- 中间列：方形地图卡（含船只图标） -->
      <div class="v-middle">
        <div class="map-card">
          <div ref="mapDiv" class="map-container" />
        </div>
      </div>

      <!-- 右列：两个仪表盘（上：速度，下：吃水） -->
      <div class="v-right">
        <div class="gauge-card">
          <div ref="speedDiv" class="gauge-container" />
        </div>

        <div class="gauge-card">
          <div ref="draughtDiv" class="gauge-container" />
        </div>
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
import Feature from "ol/Feature";
import Point from "ol/geom/Point";
import VectorSource from "ol/source/Vector";
import VectorLayer from "ol/layer/Vector";
import Style from "ol/style/Style";
import Icon from "ol/style/Icon";
import { fromLonLat } from "ol/proj";

import { ref, computed, onMounted, onBeforeUnmount, watch } from "vue";

/* ECharts */
import * as echarts from "echarts";

/* ICON 路径：确保该文件位于 public/icons/ship-arrow.svg 或按需修改路径 */
const ICON_SRC = "/icons/ship-arrow.svg";

/* 示例 vessel JSON（直接内嵌） */
const vessel = ref({
  "mmsi": 244100001,
  "imo": 0,
  "name": "TWENTE BARGE 1",
  "callsign": "PA001",
  "ship_type": "Cargo barge",
  "lat": 52.182579,
  "lon": 6.178605,
  "timestamp": "2025-09-12T13:40:00Z",
  "sog_knots": 8.0,
  "cog_deg": 319.50,
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

/* Map and marker setup */
const mapDiv = ref(null);
let map = null;
let markerLayer = null;
let markerFeature = null;

function degToRad(d) { return (d ?? 0) * Math.PI / 180; }

/* ECharts DOM refs + instances */
const speedDiv = ref(null);
const draughtDiv = ref(null);
let speedChart = null;
let draughtChart = null;


/* make gauge option, 支持自定义主/次色 */
function makeGaugeOption(value, max = 60, unit = '°', primaryColor = '#FFAB91', secondaryColor = '#FD7347') {
  return {
    series: [
      {
        type: 'gauge',
        radius: 25, 
        center: ['50%', '60%'],
        startAngle: 200,
        endAngle: -20,
        min: 0,
        max: max,
        splitNumber: 6,
        itemStyle: { color: primaryColor },
        progress: { show: true, width: 30 },
        pointer: { show: false },
        axisLine: { lineStyle: { width: 38 } },
        axisTick: {
          distance: -45,
          splitNumber: 5,
          lineStyle: { width: 1, color: '#999' }
        },
        splitLine: {
          distance: -52,
          length: 10,
          lineStyle: { width: 1.5, color: '#999' }
        },
        axisLabel: { show: false }, 
        anchor: { show: false },
        title: { show: false },
        detail: {
          valueAnimation: true,
          width: '60%',
          lineHeight: 40,
          borderRadius: 8,
          offsetCenter: [0, '80%'],
          fontSize: 18,
          fontWeight: 'bolder',
          formatter: `{value} ${unit}`,
          color: 'inherit'
        },
        data: [{ value }]
      },
      {
        type: 'gauge',
        center: ['50%', '60%'],
        startAngle: 200,
        endAngle: -20,
        min: 0,
        max: max,
        itemStyle: { color: secondaryColor },
        progress: { show: true, width: 8 },
        pointer: { show: false },
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { show: false },
        axisLabel: { show: false },
        detail: { show: false },
        data: [{ value }]
      }
    ]
  };
}

onMounted(() => {
  // --- Map init ---
  const lon = Number(vessel.value.lon ?? 6.55);
  const lat = Number(vessel.value.lat ?? 52.13);
  const center = fromLonLat([lon, lat]);

  map = new Map({
    target: mapDiv.value,
    layers: [
      new TileLayer({ source: new OSM() })
    ],
    view: new View({
      center,
      zoom: 14,
      projection: 'EPSG:3857'
    }),
    controls: []
  });

  markerFeature = new Feature({ geometry: new Point(center) });

  const iconStyle = new Style({
    image: new Icon({
      src: ICON_SRC,
      anchor: [0.5, 0.5],
      rotateWithView: false,
      rotation: degToRad(vessel.value.cog_deg ?? 0),
      scale: 1.3
    })
  });
  markerFeature.setStyle(iconStyle);

  markerLayer = new VectorLayer({
    source: new VectorSource({ features: [markerFeature] }),
    zIndex: 300
  });

  map.addLayer(markerLayer);

  // --- ECharts init ---
  if (speedDiv.value) {
    speedChart = echarts.init(speedDiv.value);
    const speedVal = Number(vessel.value.sog_knots ?? 0);
    // 速度使用绿色（primary: 绿色, secondary: 深绿色）
    const speedOpt = makeGaugeOption(speedVal, 60, 'kn', '#2ecc71', '#16a34a');
    speedChart.setOption(speedOpt);
  }

  if (draughtDiv.value) {
    draughtChart = echarts.init(draughtDiv.value);
    const draughtVal = Number(vessel.value.draught_m ?? 0);
    // 吃水使用蓝色（primary: 蓝色, secondary: 深蓝）
    // 吃水最大设为 10m（可根据需要调整）
    const draughtOpt = makeGaugeOption(draughtVal, 10, 'm', '#3498db', '#1e6fb8');
    draughtOpt.series[0].axisLabel.fontSize = 10;
    draughtChart.setOption(draughtOpt);
  }

  // responsive
  const onResize = () => {
    if (speedChart) speedChart.resize();
    if (draughtChart) draughtChart.resize();
  };
  window.addEventListener('resize', onResize);

  onBeforeUnmount(() => {
    window.removeEventListener('resize', onResize);
  });
});

/* watch vessel: 更新位置、方向以及两个图表数值 */
watch(vessel, (nv) => {
  // map marker update
  if (map && markerFeature) {
    const lon = Number(nv.lon ?? 0);
    const lat = Number(nv.lat ?? 0);
    const coord = fromLonLat([lon, lat]);
    markerFeature.setGeometry(new Point(coord));
    const newStyle = new Style({
      image: new Icon({
        src: ICON_SRC,
        anchor: [0.5, 0.5],
        rotateWithView: false,
        rotation: degToRad(nv.cog_deg ?? 0),
        scale: 0.8
      })
    });
    markerFeature.setStyle(newStyle);

    const view = map.getView();
    if (view) view.animate({ center: coord, duration: 400 });
  }

  // charts update
  const sogVal = Number(nv.sog_knots ?? 0);
  const draughtVal = Number(nv.draught_m ?? 0);

  if (speedChart) {
    try {
      speedChart.setOption({
        series: [
          { data: [{ value: sogVal }] },
          { data: [{ value: sogVal }] }
        ]
      });
    } catch (e) { /* ignore */ }
  }

  if (draughtChart) {
    try {
      draughtChart.setOption({
        series: [
          { data: [{ value: draughtVal }] },
          { data: [{ value: draughtVal }] }
        ]
      });
    } catch (e) { /* ignore */ }
  }
}, { immediate: true, deep: true });

onBeforeUnmount(() => {
  // cleanup echarts
  if (speedChart) { speedChart.dispose(); speedChart = null; }
  if (draughtChart) { draughtChart.dispose(); draughtChart = null; }

  // cleanup map
  if (map) {
    map.setTarget(null);
    map = null;
  }
  markerLayer = null;
  markerFeature = null;
});
</script>

<style scoped>
.info-vessel-root { width: 100%; box-sizing: border-box; }

/* 三列按比例 2 : 4 : 2 */
.vessel-card {
  display: grid;
  gap: 4px;
  align-items: start;
  grid-template-columns: 2fr 2fr 1fr 0.05fr;
  width: 100%;
  box-sizing: border-box;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  background: #ffffff27;
  border-radius: 10px; 
  border: 0.1px solid rgba(250, 250, 250, 0.6);
}

/* 左列样式 */
.v-left { padding: 6px 8px; }
.v-title { font-size: 12px; color: #5e7176; font-weight: 600; margin-bottom: 4px; }
.v-name { font-size: 12px; font-weight: 800; color: #0f2930; margin-bottom: 8px; }
.v-meta .row { display:flex; justify-content:space-between; gap:8px; margin: 3px 0; font-size: 11px; color:#2f4850; }
.v-meta .k { color:#6d8387; font-weight:600; }
.v-meta .v { color:#0e2a2f; text-align:right; }

/* 中间列：方形地图卡片 */
.v-middle { display:flex; align-items: center; justify-content:center; padding: 6px 4px; }
.map-card {
  width: 100%;
  aspect-ratio: 1 / 1;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 6px rgba(0,0,0,0.15);
  background: #fff;
  display:flex;
  flex-direction:column;
}
.map-container { width: 100%; height: 100%; }

/* 右列：两个仪表盘 */
.v-right { padding: 4px 0px; display:flex; flex-direction:column; gap:4px; align-items:stretch; }

/* 单个仪表卡 */
.gauge-card {
  background: #ffffff2a;
  border-radius: 8px;
  padding: 0px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.15);
  display:flex;
  flex-direction:column;
  align-items:stretch;
  justify-content:center;
  height:78px;
}

/* 标题 */
.gauge-title { font-size: 12px; color:#6d8387; margin-bottom: 6px; font-weight:600; text-align:left; padding-left:6px; }

/* 仪表容器高度（根据右侧宽度自适应） */
.gauge-container {
  width: 100%;
  height: 140px; /* 可微调 */
}

/* 响应式 */
@media (max-width: 900px) {
  .vessel-card {
    grid-template-columns: 1fr;
  }
  .map-card { aspect-ratio: auto; height: 320px; }
  .gauge-container { height: 160px; }
}
</style>
