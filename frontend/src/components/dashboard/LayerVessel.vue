<script setup>
import { defineEmits } from "vue";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import Feature from "ol/Feature";
import { Point } from "ol/geom";
import { fromLonLat, transformExtent } from "ol/proj";
import { Style, Fill, Stroke, Circle as CircleStyle, Text, Icon } from "ol/style";

const emit = defineEmits(["map-layer-ready", "feature-clicked"]);

// --- 源与图层 ---
const vesselSource = new VectorSource();
const vesselLayer = new VectorLayer({
  source: vesselSource,
  zIndex: 110,
  style: (feature) => getStyleForFeature(feature),
});
vesselLayer.set("name", "vessel");

// ========== 样式缓存机制 ==========
const styleCache = new Map(); // key -> ol/style/Style

// 颜色策略（按需调整）
const COLORS = {
  stationary: "#3b82f6", // 停靠/静止（蓝）
  slow: "#f59e0b",       // 缓慢（橙）
  moving: "#16a34a",     // 航行（绿）
  private: "#868686",    // 私密（紫）
  stroke: "#ffffff",
};

// 将 cog（度）量化到 5° 的步长，减少样式数量
function quantizeCog(cogDeg) {
  if (typeof cogDeg !== "number" || Number.isNaN(cogDeg)) return 0;
  return Math.round(cogDeg / 5) * 5;
}

// 判断 moving 是否表示“是”（兼容 true / "yes" / "Yes" / "YES"）
function isMovingFlag(data) {
  if (!data) return false;
  const mv = data.moving;
  if (mv === true) return true;
  if (typeof mv === "string" && mv.toLowerCase() === "yes") return true;
  return false;
}

// 根据数据决定颜色（优先级：privacyClass -> moving/sog -> stationary）
function decideColor(data = {}) {
  if (data?.privacyClass && Number(data.privacyClass) > 0) return COLORS.private;
  if (data?.moving === true || String(data?.moving).toLowerCase() === "yes") return COLORS.moving;
  const sog = Number(data?.sog ?? 0);
  if (!Number.isNaN(sog) && sog > 1) return COLORS.moving;
  if (!Number.isNaN(sog) && sog > 0) return COLORS.slow;
  return COLORS.stationary;
}

// 构造并缓存 Style，key 包含 shape(icon/dot), color, rotationQuant
function getStyleKey({ shape, color, rotationQuant }) {
  return `${shape}::${color}::${rotationQuant}`;
}

function createStyle({ shape, color, rotationRad, rotationQuant, labelText }) {
  const stroke = new Stroke({ color: COLORS.stroke, width: 2 });
  if (shape === "icon") {
    const icon = new Icon({
      src: "/icons/ship-arrow.svg",
      scale: 1.2,
      rotation: rotationRad,
      rotateWithView: true,
      anchor: [0.5, 0.5],
    });
    return new Style({
      image: icon,
      text: new Text({
        text: labelText || "",
        offsetY: -14,
        font: "12px sans-serif",
        stroke: new Stroke({ color: "rgba(255,255,255,0.9)", width: 3 }),
      }),
    });
  } else {
    const circle = new CircleStyle({
      radius: 5,
      fill: new Fill({ color: color }),
      stroke,
    });
    return new Style({
      image: circle,
      text: new Text({
        text: labelText || "",
        offsetY: -14,
        font: "12px sans-serif",
        stroke: new Stroke({ color: "rgba(255,255,255,0.9)", width: 3 }),
      }),
    });
  }
}

// 对外：根据 feature 返回 style（会尝试复用缓存）
// 修改点：只有当 isMovingFlag(data) 为 true 时才使用 icon，否则始终使用 dot
function getStyleForFeature(feature) {
  const data = feature.get("data") || {};
  const label = data?.name || data?.trackID ? String(data?.name || data?.trackID) : "";
  const color = decideColor(data);
  const cog = Number(data?.cog ?? 0); // degree

  // 新逻辑：以 moving 字段为准（兼容 boolean true 或字符串 "yes"）
  const movingFlag = isMovingFlag(data);

  // 若 movingFlag 为 false，则强制使用圆点（即使 cog 有值）
  const useDot = !movingFlag;

  const rotationQuant = quantizeCog(cog);
  const rotationRad = (rotationQuant * Math.PI) / 180;

  const shape = useDot ? "dot" : "icon";
  const key = getStyleKey({ shape, color, rotationQuant });
  if (styleCache.has(key)) {
    return styleCache.get(key);
  }
  const style = createStyle({
    shape: shape === "icon" ? "icon" : "dot",
    color,
    rotationRad,
    rotationQuant,
    // labelText: label,
  });
  styleCache.set(key, style);
  return style;
}

// ========== 拉取 / 转换 / 刷新 逻辑（保持你原来的逻辑，只稍微调整） ==========
let refreshTimer = null;
let currentAbortController = null;
const DEBOUNCE_MS = 600;

async function fetchVesselsByBBox(minLon, minLat, maxLon, maxLat) {
  if (currentAbortController) {
    currentAbortController.abort();
    currentAbortController = null;
  }
  currentAbortController = new AbortController();
  const signal = currentAbortController.signal;

  const params = new URLSearchParams({
    minLon: String(minLon),
    minLat: String(minLat),
    maxLon: String(maxLon),
    maxLat: String(maxLat),
  });
  const url = `/api/vessels?${params.toString()}`;

  try {
    const resp = await fetch(url, {
      method: "GET",
      headers: { Accept: "application/json" },
      signal,
    });

    if (!resp.ok) {
      const txt = await resp.text().catch(() => "");
      throw new Error(`HTTP ${resp.status} ${resp.statusText} ${txt}`);
    }
    return await resp.json();
  } catch (err) {
    if (err.name === "AbortError") return null;
    throw err;
  } finally {
    currentAbortController = null;
  }
}

function toFeatures(rows = []) {
  const features = [];
  for (const item of rows) {
    const lon = item?.lon;
    const lat = item?.lat;
    if (typeof lon !== "number" || typeof lat !== "number") continue;
    const f = new Feature({
      geometry: new Point(fromLonLat([lon, lat])),
      data: item, // 保留完整记录
    });
    features.push(f);
  }
  return features;
}

async function refreshByMapView(map) {
  if (!map) return;
  const view = map.getView();
  if (!view) return;
  const extent3857 = view.calculateExtent(map.getSize());
  const [minX, minY, maxX, maxY] = transformExtent(extent3857, "EPSG:3857", "EPSG:4326");
  const minLon = Math.min(minX, maxX);
  const maxLon = Math.max(minX, maxX);
  const minLat = Math.min(minY, maxY);
  const maxLat = Math.max(minY, maxY);

  try {
    const rows = await fetchVesselsByBBox(minLon, minLat, maxLon, maxLat);
    if (!rows) return;
    const feats = toFeatures(Array.isArray(rows) ? rows : (rows.data || rows.items || []));
    vesselSource.clear(true);
    vesselSource.addFeatures(feats);
  } catch (e) {
    console.error("❌ 拉取船舶数据失败：", e);
  }
}

// 绑定 map 事件
function attachMapEvents(map) {
  if (!map) return;

  if (refreshTimer) clearTimeout(refreshTimer);
  refreshTimer = setTimeout(() => refreshByMapView(map), 200);

  const handler = () => {
    if (refreshTimer) clearTimeout(refreshTimer);
    refreshTimer = setTimeout(() => refreshByMapView(map), DEBOUNCE_MS);
  };
  map.on("moveend", handler);

  // 点击事件：把 feature 的 data 传给父组件
  map.on("singleclick", (evt) => {
    let hit = false;
    map.forEachFeatureAtPixel(
      evt.pixel,
      (feature, layer) => {
        if (layer === vesselLayer) {
          const data = feature.get("data");
          emit("feature-clicked", data);
          hit = true;
          return true;
        }
        return false;
      },
      { hitTolerance: 5 }
    );
  });

  emit("map-layer-ready", vesselLayer);
}

defineExpose({
  attachMapEvents,
  getLayer: () => vesselLayer,
});
</script>
