<script setup>
import { defineEmits, onMounted } from "vue";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import Feature from "ol/Feature";
import { Point, Polygon } from "ol/geom";
import { fromLonLat } from "ol/proj";
import { Style, Fill, Stroke, Circle as CircleStyle } from "ol/style";
import { listVNDSbridge } from "@/api/infrastructure/VNDSbridge";

const bridgeSource = new VectorSource();
const bridgeLayer = new VectorLayer({
  source: bridgeSource,
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
bridgeLayer.set("name", "bridge");

const emit = defineEmits(["map-layer-ready", "feature-clicked"]);

// attachMapEvents 提供给 index.vue 调用
async function attachMapEvents(map) {
  const features = [];
const pageSize = 200;
  const firstPage = await listVNDSbridge({ pageNum: 1, pageSize });
  const total = firstPage.total;
  const totalPages = Math.ceil(total / pageSize);

  // 并发请求后续分页数据
  const pagePromises = [];
  for (let i = 2; i <= totalPages; i++) {
    pagePromises.push(listVNDSbridge({ pageNum: i, pageSize }));
  }
  const results = await Promise.all(pagePromises);

  // 合并所有数据
  const allRows = [...firstPage.rows];
  for (const res of results) {
    allRows.push(...res.rows);
  }
  // console.log("✅ 全部泊位数据获取完成，共：", allRows.length);

  // 解析 GeoJSON 并创建 Feature
  for (const item of allRows) {
    let geo = item.geoJSON;

    // 如果字段是字符串，尝试转为对象
    if (typeof geo === "string") {
      try {
        if (!geo || typeof geo !== "string") continue; // ⭐关键判断
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

  bridgeSource.clear();
  bridgeSource.addFeatures(features);

  // …保持原有的鼠标悬浮/点击事件绑定…
  emit("map-layer-ready", bridgeLayer);
}

defineExpose({
  attachMapEvents,
  getLayer: () => bridgeLayer,
});
</script>