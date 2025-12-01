<script setup>
import { defineEmits } from "vue";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import Feature from "ol/Feature";
import { Point, Polygon } from "ol/geom";
import { fromLonLat } from "ol/proj";
import { Style, Fill, Stroke, Circle as CircleStyle } from "ol/style";
import { listVNDSberth } from "@/api/infrastructure/VNDSberth";

const MIN_ZOOM = 13;      // ⭐ zoom < 13 不加载 / 不显示泊位
const PAGE_SIZE = 200;    // ⭐ 每页 200 条
const MAX_PAGES = 14;     // ⭐ 最多只拉前 14 页

const berthSource = new VectorSource();
const berthLayer = new VectorLayer({
  source: berthSource,
  zIndex: 100,
  style: (feature) => {
    const type = feature.getGeometry().getType();
    return type === "Point"
      ? new Style({
          image: new CircleStyle({
            radius: 5,
            fill: new Fill({ color: "rgba(255,165,0,0.6)" }),
            stroke: new Stroke({ color: "#fff", width: 2 }),
          }),
        })
      : new Style({
          fill: new Fill({ color: "rgba(255,165,0,0.3)" }),
          stroke: new Stroke({ color: "#FFA500", width: 2 }),
        });
  },
});

// 给图层设置 name，方便查找/卸载
berthLayer.set("name", "berth");

const emit = defineEmits(["map-layer-ready", "feature-clicked"]);

// 加载状态，避免重复打接口
let berthLoaded = false;
let berthLoading = false;

/** 把接口 rows 解析成 Feature 加到图层中 */
function addBerthFeatures(rows = []) {
  const features = [];

  for (const item of rows) {
    let geo = item.geoJSON;

    // 如果字段是字符串，尝试转为对象
    if (typeof geo === "string") {
      try {
        if (!geo || typeof geo !== "string") continue;
        geo = JSON.parse(geo);
      } catch (e) {
        console.warn("❌ JSON 解析失败，跳过该条数据：", item.Id, item.geoJSON);
        continue;
      }
    }
    if (!geo || !geo.type || !geo.coordinates) {
      console.warn("❌ GeoJSON 不完整，跳过：", item.Id, item.geoJSON);
      continue;
    }

    let feature = null;
    if (geo.type === "Point") {
      feature = new Feature({
        geometry: new Point(fromLonLat(geo.coordinates)),
        data: item,
      });
    } else if (geo.type === "Polygon") {
      const ring = geo.coordinates[0].map((coord) => fromLonLat(coord));
      feature = new Feature({
        geometry: new Polygon([ring]),
        data: item,
      });
    } else {
      console.warn("❌ 不支持的类型：", geo.type);
      continue;
    }

    if (feature) {
      features.push(feature);
    }
  }

  berthSource.addFeatures(features);
}

/** 顺序分页加载，只拉前 MAX_PAGES 页 */
async function loadBerthsSequential() {
  if (berthLoading || berthLoaded) return;
  berthLoading = true;

  try {
    // 1. 先拉第一页，拿 total
    const firstPage = await listVNDSberth({ pageNum: 1, pageSize: PAGE_SIZE });
    const firstRows = firstPage?.rows || firstPage?.data?.rows || [];
    const total = firstPage?.total || firstPage?.data?.total || 0;

    berthSource.clear();
    addBerthFeatures(firstRows);

    if (!total || total <= PAGE_SIZE) {
      berthLoaded = true;
      return;
    }

    // 2. 计算总页数，限制最多 MAX_PAGES
    const totalPages = Math.min(Math.ceil(total / PAGE_SIZE), MAX_PAGES);

    // 3. 从第 2 页开始顺序请求，避免 Promise.all 并发打爆接口
    for (let page = 2; page <= totalPages; page++) {
      try {
        const res = await listVNDSberth({ pageNum: page, pageSize: PAGE_SIZE });
        const rows = res?.rows || res?.data?.rows || [];
        addBerthFeatures(rows);
      } catch (err) {
        console.error("❌ 加载 VNDSberth 第 " + page + " 页失败", err);
        break; // 某一页失败就先停掉
      }
    }

    berthLoaded = true;
  } catch (e) {
    console.error("❌ 初次加载 VNDSberth 失败：", e);
  } finally {
    berthLoading = false;
  }
}

// attachMapEvents 提供给 index.vue 调用
async function attachMapEvents(map) {
  const view = map.getView();

  const updateByZoom = async () => {
    const zoom = view.getZoom();

    // zoom < 13：不显示也不加载
    if (zoom < MIN_ZOOM) {
      berthSource.clear();
      berthLoaded = false; // 下次放大再重新加载
      return;
    }

    // zoom >= 13：如尚未加载，则顺序拉前 MAX_PAGES 页
    if (!berthLoaded) {
      await loadBerthsSequential();
    }
  };

  // 初始化时跑一次
  await updateByZoom();

  // 缩放变化时更新
  view.on("change:resolution", () => {
    updateByZoom();
  });

  // …如果你原来有鼠标悬浮 / 点击事件，就放在这里…

  emit("map-layer-ready", berthLayer);
}

defineExpose({
  attachMapEvents,
  getLayer: () => berthLayer,
});
</script>

<template>
  <div style="display:none"></div>
</template>
