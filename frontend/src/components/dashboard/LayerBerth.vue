<script setup>
import { defineEmits, onMounted } from "vue";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import Feature from "ol/Feature";
import { Point, Polygon } from "ol/geom";
import { fromLonLat } from "ol/proj";
import { Style, Fill, Stroke, Circle as CircleStyle } from "ol/style";
import { listVNDSberth } from "@/api/infrastructure/VNDSberth";

const emit = defineEmits(["map-layer-ready", "feature-clicked"]);

const berthSource = new VectorSource();
const berthLayer = new VectorLayer({
  source: berthSource,
  zIndex: 100,
  style: (feature) => {
    const type = feature.getGeometry().getType();
    return type === "Point"
      ? new Style({
          image: new CircleStyle({
            radius: 6,
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

// attachMapEvents 提供给 index.vue 调用
async function attachMapEvents(map) {
  const res = await listVNDSberth();
  const features = [];

  for (const item of res.rows) {
    if (!item.GeoJSON) continue;

    let feature;
    if (item.GeoJSON.startsWith("POINT")) {
      const coords = item.GeoJSON.match(/POINT\s*\(([^)]+)\)/)[1]
        .split(" ")
        .map(parseFloat);
      feature = new Feature({
        geometry: new Point(fromLonLat(coords)),
        data: item,
      });
    } else if (item.GeoJSON.startsWith("POLYGON")) {
      const coordStrs = item.GeoJSON.match(/POLYGON\s*\(\(([^)]+)\)\)/)[1]
        .split(",")
        .map((pt) => pt.trim().split(" ").map(parseFloat));
      const transformed = coordStrs.map(fromLonLat);
      feature = new Feature({
        geometry: new Polygon([transformed]),
        data: item,
      });
    }

    if (feature) {
      features.push(feature);
    }
  }

  berthSource.clear();
  berthSource.addFeatures(features);

  // 绑定交互
  map.on("pointermove", (evt) => {
    const hit = map.hasFeatureAtPixel(evt.pixel, {
      layerFilter: (l) => l === berthLayer,
    });
    map.getTargetElement().style.cursor = hit ? "pointer" : "";
  });

  map.on("singleclick", (evt) => {
    const feat = map.forEachFeatureAtPixel(evt.pixel, (f) => f, {
      layerFilter: (l) => l === berthLayer,
    });
    if (feat) {
      emit("feature-clicked", feat.get("data"));
    }
  });

  emit("map-layer-ready", berthLayer);
}

defineExpose({
  attachMapEvents,
  getLayer: () => berthLayer,
});
</script>
