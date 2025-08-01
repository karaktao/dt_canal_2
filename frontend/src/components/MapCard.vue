<script setup>
// ------------------------- 引入依赖与初始化投影支持 -------------------------
import "ol/ol.css";
import Map from "ol/Map";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import OSM from "ol/source/OSM";
import XYZ from "ol/source/XYZ";
import Overlay from "ol/Overlay";
import Feature from "ol/Feature";
import Point from "ol/geom/Point";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import GeoJSON from "ol/format/GeoJSON";
import proj4 from "proj4";
import { register } from "ol/proj/proj4";
import { get as getProjection } from "ol/proj";
import Zoom from "ol/control/Zoom";
import { ref, onMounted, watch, defineEmits } from 'vue'


// // ❶ 定义一个 ref 来接 `<MapCard>` 实例
// const mapCard = ref(null)
const emit = defineEmits(['map-ready']);

// 添加 props 接收图层
const props = defineProps({
  extraLayers: {
    type: Array, // OpenLayers Layer[] 类型
    default: () => [],
  },
});

// 注册 EPSG:28992 投影 123456
proj4.defs(
  "EPSG:28992",
  "+proj=sterea +lat_0=52.1561605555556 +lon_0=5.38763888888889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +towgs84=593.16,26.15,478.54,-6.3239,-0.5008,-5.5487,4.0775 +units=m +no_defs +type=crs"
);
// 定义 ETRS89 / UTM zone 31N
proj4.defs(
  "EPSG:25831",
  "+proj=utm +zone=31 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs +type=crs"
);
register(proj4);
// console.log("✅ EPSG:28992 已注册:", getProjection("EPSG:28992"));

let map, view, osmLayer, esriLayer;

// ------------------------- 初始化地图 -------------------------
onMounted(() => {
  view = new View({
    center: [694118.1989320087, 6829154.600371235],
    zoom: 14,
    projection: "EPSG:3857",
  });

  // 初始化两个底图图层
  osmLayer = new TileLayer({ source: new OSM(), visible: true });
  esriLayer = new TileLayer({
    source: new XYZ({
      url: "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}",
    }),
    visible: false,
  });

  // 创建地图实例
  map = new Map({
    target: "map",
    view,
    layers: [osmLayer, esriLayer],
  });

  // 缩放控件
  const zoomControl = new Zoom();
  map.addControl(zoomControl);

  // ✅ 添加传入的图层
  props.extraLayers.forEach((layer) => {
    if (!map.getLayers().getArray().includes(layer)) {
      map.addLayer(layer);
    }
  });

  // ✅ emit map 对象给父组件
  emit('map-ready', map);
  console.log('[MapCard] 已经 emit map-ready');

});

// console.log('[MapCard] 即将 emit map-ready, map 对象：', map)
// emit('map-ready', map)
// console.log('[MapCard] 已经 emit map-ready')

// ------------------------- 地图跳转功能 -------------------------
function goTo(place) {
  const coords = {
    eefde: [694118.1989, 6829154.6003],
    delden: [743345.6427, 6844779.7188],
    hengelo: [757391.5712, 6844752.2938],
  };
  view.setCenter(coords[place]);
  view.setZoom(18);
}

// ------------------------- 底图图层切换功能 -------------------------
function toggleLayer(type) {
  osmLayer.setVisible(type === "street");
  esriLayer.setVisible(type === "satellite");
}

defineExpose({ map });

</script>


<template>
  <el-card class="map-card" body-style="padding: 0; height: 100%;" shadow="never">
    <!-- 地图显示容器 -->
    <div id="map" class="map-container" style="display:flex; flex-direction:column; padding:0;"></div>

    <!-- 地图控制按钮组 -->
    <div class="btn-group">
      <!-- 地点跳转按钮：Eefde -->
      <div class="btnsEefde">
        <el-button @click="goTo('eefde')">Eefde</el-button>
      </div>
      <!-- 地点跳转按钮：Delden -->
      <div class="btnsDelden">
        <el-button @click="goTo('delden')">Delden</el-button>
      </div>
      <!-- 地点跳转按钮：Hengelo -->
      <div class="btnsHengelo">
        <el-button @click="goTo('hengelo')">Hengelo</el-button>
      </div>
      <!-- 切换街道图层 -->
      <div class="btnsStreetmap">
        <el-button @click="toggleLayer('street')">Street</el-button>
      </div>
      <!-- 切换卫星图层 -->
      <div class="btnsSatellitemap">
        <el-button @click="toggleLayer('satellite')">Satellite</el-button>
      </div>
    </div>
  </el-card>
</template>

<style scoped>


/* 1）el-col 拉满父容器 */
.map {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0; /* 如果你之前用 gutter，可酌情去掉 */
}

/* 2）map-card 本身也成为 flex 容器 并拉伸 */
.map-card {
  display: flex !important;
  flex-direction: column;
  flex: 1 !important;
  border: none !important;
  padding: 0 !important;
}

/* 覆盖 el-card__body 的默认 padding/背景 */
::v-deep .map-card .el-card__body {
  padding: 0 !important;
  height: 100% !important;
  display: flex;
  flex-direction: column;
}

/* 3）让 map-container 填满 map-card 剩余空间 */
.map-container {
  flex: 1;
  width: 100%;
  height: calc(100% - 48px); 
}



.btn-group {
  display: flex;
  gap: 6px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.custom-zoom-position {
  position: absolute;
  right: 10px;
  bottom: 10px;
  z-index: 1000;
}

/* 定位默认 Zoom 控件到右下角 */
::v-deep .ol-zoom {
  position: absolute;
  right: 10px;
  bottom: 15px;
  left: auto;
  top: auto;
  z-index: 1000;
}




</style>
