<script setup >
// 地图输入
import MapCard from "@/components/MapCard.vue";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import Feature from "ol/Feature";
import Point from "ol/geom/Point";
import { fromLonLat } from "ol/proj";
import { Style, Fill, Stroke, Circle as CircleStyle, Text } from "ol/style";
import { recordsByDate } from "@/assets/data/recordsByDate";
import LineString from "ol/geom/LineString";
import { getCenter } from "ol/extent";
import Overlay from "ol/Overlay";

// 功能输入
import { ref, computed, onMounted, nextTick, watch } from "vue";
import proj4 from "proj4";
import { register } from "ol/proj/proj4";
import { transform } from "ol/proj";
import polyline from "@mapbox/polyline";

// API
import { listRwsCatalogus } from "@/api/infrastructure/rwsCatalogus";

// 注册EPSG:28992坐标系
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

// ========== 水位点图层准备 ==========

// 1. 创建水位点图层：只显示圆点
const waterLevelSource = new VectorSource();
const waterLevelLayer = new VectorLayer({
  source: waterLevelSource,
  style: (feature) => {
    // 每个 feature 带有 locCode/locNaam 属性
    const code = feature.get("locCode");
    const name = feature.get("locNaam");
    return new Style({
      image: new CircleStyle({
        radius: 6,
        fill: new Fill({ color: "rgba(0,123,255,0.6)" }),
        stroke: new Stroke({ color: "#fff", width: 2 }),
      }),
    });
  },
});

// ========== 修改：新增 refs ==========
const mapCardRef = ref(null); // 拿到 MapCard 组件实例
// 保存被点击的要素信息
/**
 * @type {{ locCode: string, locNaam: string } | null}
 */
const selectedWaterLevel = ref(null);

onMounted(async () => {
  // 1) 拉数据、打点逻辑不变
  try {
    const res = await listRwsCatalogus({
      metaCompartimentCode: "ow",
      metaGrootheidCode: "WATHTE",
      pageSize: 9999,
    });
    const list = res.rows || [];
    const src = waterLevelLayer.getSource();
    list.forEach((item, idx) => {
      const xRaw = item.locX ?? item.loc_x;
      const yRaw = item.locY ?? item.loc_y;

      if (!xRaw || !yRaw) {
        console.warn(`[❌跳过] 第 ${idx} 个点坐标为空`, item);
        return;
      }

      const x = parseFloat(xRaw);
      const y = parseFloat(yRaw);
      if (!isFinite(x) || !isFinite(y)) {
        console.warn(`[❌跳过] 第 ${idx} 个点坐标无效`, { x, y, item });
        return;
      }

      const merc = transform([x, y], "EPSG:25831", "EPSG:3857");
      const feat = new Feature({
        geometry: new Point(merc),
        locCode: item.locCode ?? item.loc_code,
        locNaam: item.locNaam ?? item.loc_naam,
        locX: x, // ✅ 添加原始 x
        locY: y, // ✅ 添加原始 y
      });

      // console.log(`[✅添加] 第 ${idx} 个点`, feat.getProperties());
      waterLevelSource.addFeature(feat);
    });
  } catch (err) {
    console.error("拉水位列表失败：", err);
  }
});

// ===== api申请水位信息=====

const measurementData = ref(null); // 存放返回结果
const loadingMeasurement = ref(false); // 是否正在加载

// ===== 新增 initMap 函数：在 map-ready 时调用 =====
function initMap(map) {
  // 2. hover 时切换手型
  map.on("pointermove", (evt) => {
    const hit = map.forEachFeatureAtPixel(evt.pixel, (f) => f, {
      layerFilter: (l) => l === waterLevelLayer,
    });
    map.getViewport().style.cursor = hit ? "pointer" : "";
  });

  map.on("singleclick", async (evt) => {
    const feat = map.forEachFeatureAtPixel(evt.pixel, (f) => f, {
      layerFilter: (l) => l === waterLevelLayer,
    });
    if (feat) {
      const locCode = feat.get("locCode");
      const locNaam = feat.get("locNaam");

      // 假设你保存了原始坐标 x/y 到 feature 属性中（建议在 addFeature 时也加上）
      const coordinates = feat.getGeometry().getCoordinates();
      const transformed = transform(coordinates, "EPSG:3857", "EPSG:25831");
      const locX = feat.get("locX");
      const locY = feat.get("locY");

      selectedWaterLevel.value = { locCode, locNaam, locX, locY };

      console.log("[🟢点击要素]", {
        code: locCode,
        name: locNaam,
        x: locX,
        y: locY,
      });

      // 构造 JSON 请求体
      const payload = {
        Locatie: {
          Code: locCode,
          X: locX,
          Y: locY,
        },
        AquoPlusWaarnemingMetadata: {
          AquoMetadata: {
            Compartiment: {
              Code: "OW",
            },
            Grootheid: {
              Code: "WATHTE",
            },
          },
        },
        Periode: {
          Begindatumtijd: "2025-07-16T01:20:00.000+01:00",
          Einddatumtijd: "2025-07-16T01:40:00.000+01:00",
        },
      };

      // ✅ 打印请求体 JSON
      console.log("[📤发送测量数据请求]", JSON.stringify(payload, null, 2));

      // 请求远程服务
      loadingMeasurement.value = true;
      try {
        const response = await fetch(
          "/api/rijks/ONLINEWAARNEMINGENSERVICES_DBO/OphalenWaarnemingen",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify(payload),
          }
        );
        const result = await response.json();
        measurementData.value = result;
      } catch (e) {
        console.error("❌ 获取测量数据失败", e);
        measurementData.value = null;
      } finally {
        loadingMeasurement.value = false;
      }
    } else {
      selectedWaterLevel.value = null;
      measurementData.value = null;
    }
  });
}

// ===== 信息导航栏 =====

// —— 侧边导航栏数据 ——
const selectedItems = ref([]);
const categories = [
  {
    name: "Weather",
    options: [
      { label: "current weather", value: "realTime" },
      { label: "15-min forecast ", value: "forecast15" },
      { label: "Hourly forecast", value: "forecast24" },
      { label: "Daily forecast", value: "forecast1d" },
    ],
  },
  {
    name: "Hydrological Info",
    options: [
      { label: "Water level", value: "waterLevel" },
      { label: "Flow rate", value: "flowRate" },
      { label: "Discharge", value: "discharge" },
    ],
  },
  {
    name: "Basic Controls",
    options: [
      { label: "Sluice Monitoring", value: "sluice" },
      { label: "Berth", value: "berth" },
    ],
  },
  {
    name: "Vessel Monitor",
    options: [
      { label: "Vessel status", value: "vesselStatus" },
      { label: "Logistics info", value: "logistics" },
    ],
  },
];
</script>

<template>
  <el-row class="dashboard" :gutter="5">
    <el-col :span="2" class="navbar-panel">
      <el-card class="record-card">
        <el-checkbox-group v-model="selectedItems" class="info-checkbox-group">
          <div
            v-for="(cat, idx) in categories"
            :key="cat.name"
            class="info-category"
          >
            <p class="category-title">{{ cat.name }}</p>
            <el-checkbox
              v-for="opt in cat.options"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            >
              {{ opt.label }}
            </el-checkbox>
            <el-divider v-if="idx < categories.length - 1" />
          </div>
        </el-checkbox-group>
      </el-card>
    </el-col>

    <!-- 地图输入卡片 -->
    <el-col :span="16" class="map">
      <!-- 修改：加上 ref -->
      <MapCard
        ref="mapCardRef"
        :extraLayers="[waterLevelLayer]"
        @map-ready="initMap"
      />

      <!-- 修改：Popup DOM 保持子组件外层同级 -->
      <div ref="popupContainer" class="ol-popup" style="display: none">
        <div ref="popupContent" class="ol-popup-content"></div>
      </div>
    </el-col>

    <el-col :span="6" class="info-panel">
      <el-card class="record-card">
        <h3>Water Level</h3>

        <template v-if="selectedWaterLevel">
          <p><strong>Code:</strong> {{ selectedWaterLevel.locCode }}</p>
          <p><strong>Name:</strong> {{ selectedWaterLevel.locNaam }}</p>
          <p><strong>X:</strong> {{ selectedWaterLevel.locX }}</p>
          <p><strong>Y:</strong> {{ selectedWaterLevel.locY }}</p>

          <el-divider />

          <template v-if="loadingMeasurement">
            <el-skeleton animated />
          </template>
          <template v-else-if="measurementData">
            <p><strong>Returned data：</strong></p>
            <pre style="white-space: pre-wrap">{{ measurementData }}</pre>
          </template>
          <template v-else>
            <p>data</p>
          </template>
        </template>
      </el-card>
    </el-col>
  </el-row>
</template>

<style scoped lang="less">
.dashboard {
  position: absolute; /* 或者 fixed，看你的需求 */
  top: 5px; /* 离页面顶部 20px */
  bottom: 0; /* 底部贴合页面底部 */
  left: 0; /* 根据需要调整左右 */
  right: 0;
  overflow-y: auto; /* 内容过多时出现滚动条 */
}

/* 简单弹窗样式，跟上面示例一致 */
.ol-popup {
  position: absolute;
  background: white;
  padding: 6px 8px;
  border-radius: 4px;
  border: 1px solid #888;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
  white-space: nowrap;
  transform: translate(-50%, -100%);
  pointer-events: auto;
}
.ol-popup:after {
  content: "";
  position: absolute;
  bottom: -8px;
  left: 50%;
  margin-left: -8px;
  border-width: 8px 8px 0;
  border-style: solid;
  border-color: white transparent;
}
.ol-popup:before {
  content: "";
  position: absolute;
  bottom: -9px;
  left: 50%;
  margin-left: -9px;
  border-width: 9px 9px 0;
  border-style: solid;
  border-color: #888 transparent;
}

// ===== 信息导航栏 =====
.navbar-container {
  // padding: 10px;
  // margin-left: 10px;
  display: flex;
  background: #f5f7fa;
}

.info-checkbox-group {
  display: block;
  margin-left: -15px;
}

.info-category {
  margin-bottom: 12px;
}

.category-title {
  font-weight: bold;
  margin-bottom: 6px;
}

.navbar-container {
  padding: 10px;
  background: #f5f7fa;
}
.info-checkbox-group {
  display: block;
}
.info-category {
  margin-bottom: 12px;
}
.category-title {
  font-weight: bold;
  margin-bottom: 6px;
}
</style>
 