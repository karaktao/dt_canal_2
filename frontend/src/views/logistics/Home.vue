<script setup>
// 地图输入
import MapCard from "@/components/MapCard.vue";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import Feature from "ol/Feature";
import Point from "ol/geom/Point";
import { fromLonLat } from "ol/proj";
import { Style, Fill, Stroke, Circle as CircleStyle } from "ol/style";
import { recordsByDate } from "@/assets/data/recordsByDate";
import LineString from "ol/geom/LineString";
import { getCenter } from "ol/extent";

// 功能输入
import { ref, computed, onMounted } from "vue";
import proj4 from "proj4";
import { register } from "ol/proj/proj4";
import { transform } from "ol/proj";

import polyline from "@mapbox/polyline";

// 注册EPSG:28992坐标系
proj4.defs(
  "EPSG:28992",
  "+proj=sterea +lat_0=52.1561605555556 +lon_0=5.38763888888889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +towgs84=593.16,26.15,478.54,-6.3239,-0.5008,-5.5487,4.0775 +units=m +no_defs +type=crs"
);
register(proj4);




// ========== 增加 RouteLayer ==========


// 创建路线图层
const routeLayer = new VectorLayer({
  source: new VectorSource(),
  style: function (feature) {
    return [routeStyle, dashedCenterLineStyle]; // 外边+内边，形成导航路线视觉
  },
});

// Polyline 解码函数（输出为 [lat, lng]）
function decodePolyline(str) {
  let index = 0,
    lat = 0,
    lng = 0,
    coordinates = [];

  while (index < str.length) {
    let b,
      shift = 0,
      result = 0;

    do {
      b = str.charCodeAt(index++) - 63;
      result |= (b & 0x1f) << shift;
      shift += 5;
    } while (b >= 0x20);
    const dlat = result & 1 ? ~(result >> 1) : result >> 1;
    lat += dlat;

    shift = 0;
    result = 0;
    do {
      b = str.charCodeAt(index++) - 63;
      result |= (b & 0x1f) << shift;
      shift += 5;
    } while (b >= 0x20);
    const dlng = result & 1 ? ~(result >> 1) : result >> 1;
    lng += dlng;

    coordinates.push([lat / 1e6, lng / 1e6]); // [lat, lng]
  }
  return coordinates;
}


// rd转换为墨卡托并添加图层
function addEurisPaths(paths) {
  const source = routeLayer.getSource();
  source.clear();

  paths.forEach((encoded, index) => {
    if (!encoded || encoded.trim() === "") return;
    const decoded = decodePolyline(encoded); // [y_in_km, x_in_km]
    const projected = decoded.map(([lat, lon]) => fromLonLat([lon, lat])); // ✅ WGS84 → Web Mercator
    const feature = new Feature({
      geometry: new LineString(projected),
    });
    source.addFeature(feature);
    // console.log("📍 解码前坐标:", encoded);
    // console.log("📍 解码后坐标（未经处理）:", decoded);
    // console.log("🗺️ 投影后墨卡托坐标:", projected);
    // console.log("📌 添加路径数：", source.getFeatures().length);
  });
}


</script>

<template>
  <el-row class="publish-company-container" :gutter="20">
    <!-- // ------------------------- 物流输入框------------------------- -->
    <!-- 地图输入卡片 -->
    <el-col :span="24" class="map">
      <!-- 地图卡片 -->
      <MapCard :extraLayers="[routeLayer]" />

 
    </el-col>
  </el-row>
</template>

<style scoped lang="less">
.form-float-panel {
  position: absolute;
  top: 25px;
  left: 35px;
  z-index: 2000;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-panel {
  width: 320px;
  padding: 0px;
  background-color: rgba(255, 255, 255, 0.75);
  font-size: 13px;
  :deep(.el-form-item) {
    margin-bottom: 8px;
  }
  transform: scale(1);
  transform-origin: top left;
}

.compact-form {
  .el-form-item {
    margin-bottom: 12px;
  }

  .el-input,
  .el-input-number,
  .el-select,
  .el-date-picker {
    width: 100%;
    font-size: 13px;
  }
}

.info-panel {
  padding: 10px;
  max-height: 900px;
  overflow-y: auto;
  background-color: #fff;
}

.record-card {
  margin-bottom: 10px;
  font-size: 13px;
}
</style>
 