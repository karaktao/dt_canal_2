<template></template>

<script setup>
import { ref, onMounted } from "vue";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import Feature from "ol/Feature";
import Point from "ol/geom/Point";
import { Style, Fill, Stroke, Circle as CircleStyle } from "ol/style";
import { transform } from "ol/proj";
import proj4 from "proj4";
import { register } from "ol/proj/proj4";


// 注册投影
proj4.defs(
  "EPSG:28992",
  "+proj=sterea +lat_0=52.15616 +lon_0=5.38764 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +towgs84=593.16,26.15,478.54,-6.3239,-0.5008,-5.5487,4.0775 +units=m +no_defs"
);
proj4.defs("EPSG:25831", "+proj=utm +zone=31 +ellps=GRS80 +units=m +no_defs");
register(proj4);


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
        // fill: new Fill({ color: "rgba(0,123,255,0.6)" }),
        stroke: new Stroke({ color: "#fff", width: 2 }),
      }),
    });
  },
});



function attachMapEvents(map) {
  map.on("pointermove", (evt) => {
    const hit = map.hasFeatureAtPixel(evt.pixel, {
      layerFilter: (layer) => layer.get("name") === "waterLevel",
    });
    map.getTargetElement().style.cursor = hit ? "pointer" : "";
  });

  map.on("singleclick", (evt) => {
    const feat = map.forEachFeatureAtPixel(evt.pixel, (f) => f, {
      layerFilter: (layer) => layer.get("name") === "waterLevel",
    });

    if (!feat) {
      emit("feature-clicked", null);
      return;
    }

    const locCode = feat.get("locCode");
    const locNaam = feat.get("locNaam");
    const latestValue = feat.get("latestValue");
    const label = feat.get("label");

    emit("feature-clicked", {
      locCode,
      locNaam,
      latestValue,
      label,
    });
  });
}


onMounted(async () => {
  console.log("📌 WaterLevel - 使用 RWS API 加载");

  try {
    const response = await fetch(
      "/api/waterinfo/api/point/latestmeasurement?parameterId=waterhoogte"
    );
    const geojson = await response.json();

    if (!geojson.features || !Array.isArray(geojson.features)) {
      console.error("❌ 无效数据格式", geojson);
      return;
    }

    geojson.features.forEach((feature) => {
      const props = feature.properties || {};
      const coords = feature.geometry?.coordinates || [];

      if (coords.length !== 2) return;

      // EPSG:25831 → EPSG:3857
      const [mercatorX, mercatorY] = transform(
        coords,
        "EPSG:25831",
        "EPSG:3857"
      );

      const measurement = props.measurements?.[0] || {};
      const color = measurement.measurementColor || "#007BFF";

      const feat = new Feature({
        geometry: new Point([mercatorX, mercatorY]),
        locCode: props.locationCode,
        locNaam: props.name,
        latestValue: measurement.latestValue,
        label: measurement.measurementLabel,
      });

      feat.setStyle(
        new Style({
          image: new CircleStyle({
            radius: 6,
            fill: new Fill({ color }),
            stroke: new Stroke({ color: "#fff", width: 2 }),
          }),
        })
      );

      waterLevelSource.addFeature(feat);
    });

    waterLevelLayer.set("name", "waterLevel");
    emit("map-layer-ready", waterLevelLayer);

    console.log("✅ 加载完成，总点数：", waterLevelSource.getFeatures().length);
  } catch (err) {
    console.error("❌ 获取水位信息失败", err);
  }
});


defineExpose({
  attachMapEvents,
  getLayer: () => waterLevelLayer, // ✅ 暴露图层
});
</script>
