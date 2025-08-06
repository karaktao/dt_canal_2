<template></template>

<script setup>
import { ref, onMounted } from "vue";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import Feature from "ol/Feature";
import Point from "ol/geom/Point";
import { Style, Fill, Stroke, Circle as CircleStyle } from "ol/style";

import { listRwsCatalogus } from "@/api/infrastructure/rwsCatalogus";

// ✅ 用于向外传递图层 + 要素点击信息 + 数据
const emit = defineEmits([
  "map-layer-ready",
  "feature-clicked",
  "measurement-loaded",
]);

const measurementData = ref(null);
const loading = ref(false);

const waterLevelSource = new VectorSource();
const waterLevelLayer = new VectorLayer({
  source: waterLevelSource,
  zIndex: 100, // ✅ 确保叠加在底图上方
  opacity: 1.0, // ✅ 不透明
  style: (feature) => {
    return new Style({
      image: new CircleStyle({
        radius: 5,
        fill: new Fill({ color: "rgba(0,123,255,0.6)" }),
        stroke: new Stroke({ color: "#fff", width: 2 }),
      }),
    });
  },
});

// ✅ 访问时间设置
function getAdjustedPeriod() {
  const now = new Date();
  // 舍入到最近的过去20分钟整点
  const minutes = now.getMinutes();
  const flooredMinutes = Math.floor(minutes / 20) * 20;
  now.setMinutes(flooredMinutes);
  now.setSeconds(0);
  now.setMilliseconds(0);
  const endDate = new Date(now); // 结束时间为当前整点

  const startDate = new Date(endDate.getTime() - 8 * 60 * 60 * 1000); // 往前推3小时

  // 格式化为 ISO 字符串（带时区偏移）
  const toISOStringWithOffset = (d) =>
    new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString();

  return {
    Begindatumtijd: toISOStringWithOffset(startDate),
    Einddatumtijd: toISOStringWithOffset(endDate),
  };
}


onMounted(async () => {
  console.log("📌 WaterLevel 加载");
  try {
    const res = await listRwsCatalogus({
      hasWaterLevel: "1",
      metaCompartimentCode: "ow",
      metaGrootheidCode: "WATHTE",
      pageNum: 1,
      pageSize: 9999,
    });

    const list = res.rows || [];
    console.log("📌 有效数据数量：", list.length);
    list.forEach((item, idx) => {
      const mercatorX = parseFloat(item.mercator_x || item.mercatorX);
      const mercatorY = parseFloat(item.mercator_y || item.mercatorY);
      const locX = parseFloat(item.loc_x || item.locX);
      const locY = parseFloat(item.loc_y || item.locY);

      // 用于地图渲染（墨卡托必须存在）
      if (!isFinite(mercatorX) || !isFinite(mercatorY)) {
        console.warn("❌ 缺失或非法 mercator 坐标：", item);
        return;
      }

      // 用于请求原始数据（可选，但建议检查合法性）
      if (!isFinite(locX) || !isFinite(locY)) {
        console.warn("⚠️ loc 坐标非法，点击可能无法发起请求", item);
      }

      const feat = new Feature({
        geometry: new Point([mercatorX, mercatorY]), // 地图渲染使用墨卡托坐标
        locCode: item.locCode ?? item.loc_code,
        locNaam: item.locNaam ?? item.loc_naam,
        locX: locX, // 保存原始坐标用于 API
        locY: locY,
      });
      //  console.log("✅ 添加要素（已是墨卡托坐标）:", feat);
      waterLevelSource.addFeature(feat);
    });
    waterLevelLayer.set("name", "waterLevel");
    emit("map-layer-ready", waterLevelLayer);
  } catch (err) {
    console.error("❌ 加载水位图层失败", err);
  }

  console.log(
    "✅ 图层已准备，添加 Feature 总数：",
    waterLevelSource.getFeatures().length
  );
});

// ✅ 外部调用
function attachMapEvents(map) {
  map.on("pointermove", (evt) => {
    // 简化：用 hasFeatureAtPixel + 按 name 过滤
    const hit = map.hasFeatureAtPixel(evt.pixel, {
      layerFilter: (layer) => layer.get("name") === "waterLevel",
    });
    map.getTargetElement().style.cursor = hit ? "pointer" : "";
  });

  map.on("singleclick", async (evt) => {
    const feat = map.forEachFeatureAtPixel(evt.pixel, (f) => f, {
      layerFilter: (l) => {
        return l.get("name") === "waterLevel";
      },
    });

    if (!feat) {
      console.warn("⚠️ 未点击任何 Feature");
      emit("feature-clicked", null);
      return;
    }

    const locCode = feat.get("locCode");
    const locNaam = feat.get("locNaam");
    const locX = feat.get("locX");
    const locY = feat.get("locY");

    emit("feature-clicked", {
      locCode,
      locNaam,
      locX,
      locY,
    });

    const payload = {
      Locatie: {
        Code: locCode,
        X: locX,
        Y: locY,
      },
      AquoPlusWaarnemingMetadata: {
        AquoMetadata: {
          Compartiment: { Code: "OW" },
          Grootheid: { Code: "WATHTE" },
        },
      },
       Periode: getAdjustedPeriod(),
    };

    loading.value = true;
    try {
      const response = await fetch(
        "/api/rijks/ONLINEWAARNEMINGENSERVICES_DBO/OphalenWaarnemingen",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        }
      );
      const result = await response.json();

      measurementData.value = result;

      emit("measurement-loaded", {
        locCode,
        locNaam,
        locX,
        locY,
        data: result,
      });
    } catch (err) {
      console.error("❌ 获取测量数据失败", err);
      measurementData.value = null;
    } finally {
      loading.value = false;
    }
  });
}

defineExpose({
  attachMapEvents,
  getLayer: () => waterLevelLayer, // ✅ 暴露图层
});
</script>
