<template>
  <div class="info-panel">
    
    <InfoWeather
      class="panel top"
      :data="weatherData"
      :selectedCoordinates="props.selectedCoordinates"
      :location="props.location"
      :geoFeatures="props.geoFeatures"
      @refresh="onWeatherRefresh"
    />
    <InfoWaterLevel
      class="panel bottom"
      :data="waterLevelData"
      @refresh="onWaterRefresh"
    />
  </div>
</template>

<script setup>
import { ref } from "vue";
import InfoWeather from "./InfoWeather.vue";
import InfoWaterLevel from "./InfoWaterLevel.vue";

// 新增：接收来自 index.vue 的共享 state
const props = defineProps({
  selectedCoordinates: { type: [Array, null], default: null },
  location: { type: String, default: "" },
  geoFeatures: { type: Array, default: () => [] },
});

// 示例数据（真实项目中从 API / store 获取）
const weatherData = ref({
  temp: "18°C",
  windSpeed: "5 m/s",
  visibility: "10 km",
  forecast: [], // 可传数组用于图表
});

const waterLevelData = ref({
  level: "0.45 m",
  trend: "rising",
  history: [], // 可传数组用于图表
});

function onWeatherRefresh() {
  // 父组件接收到子组件的刷新事件可以在这里处理（例如重新请求数据）
  console.log("weather refresh requested");
  // 模拟更新
  weatherData.value.temp = "19°C";
}

function onWaterRefresh() {
  console.log("water-level refresh requested");
  waterLevelData.value.level = "0.48 m";
}
</script>

<style scoped>
.info-panel {
  width: 400px; /* 和你原来一致 */
  height: 500px; /* 总高度 */
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0;
  box-sizing: border-box;
}

/* 两个子面板各自占一半高度，可按需改成比例或固定高度 */
.panel {
  flex: 1 1 0;
  min-height: 0; /* 允许内部滚动 */
  display: flex;
  flex-direction: column;
}
</style>
