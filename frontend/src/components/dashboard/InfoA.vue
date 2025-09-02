<template>
  <div class="info-panel">
    <div class="section-header">
      <span class="title">Weather</span>
    </div>
    <InfoWeather
      class="panel top"
      :data="weatherData"
      :selectedCoordinates="selectedCoordinates"
      :location="location"
      :geoFeatures="geoFeatures"
      @refresh="handleWeatherRefresh"
    />
    <div class="section-header">
      <span class="title">Water Level</span>
    </div>
    <InfoWaterLevel
      class="panel bottom"
      :data="waterLevelData"
      @refresh="handleWaterRefresh"
    />
  </div>
</template>

<script setup>
import { toRefs } from "vue";
import InfoWeather from "./InfoWeather.vue";
import InfoWaterLevel from "./InfoWaterLevel.vue";

// 接收来自 index.vue 的共享 state + 可能的回调和数据
const props = defineProps({
  selectedCoordinates: { type: [Array, null], default: null },
  location: { type: String, default: "" },
  geoFeatures: { type: Array, default: () => [] },

  // 新增：外部传入的 weather / water 数据（给默认值避免 undefined）
  weatherData: { type: Object, default: () => ({}) },
  waterLevelData: { type: Object, default: () => ({}) },

  // 新增：可选的刷新回调（父组件可以传入函数），默认为 null
  onWeatherRefresh: { type: Function, default: null },
  onWaterRefresh: { type: Function, default: null },
});

// 解构成 refs，方便模板直接使用（模板会自动解包 ref）
const { selectedCoordinates, location, geoFeatures, weatherData, waterLevelData } = toRefs(props);

// 包装函数：当 InfoWeather 或 InfoWaterLevel 发出 refresh 事件时调用
function handleWeatherRefresh(...args) {
  // 如果父组件传入了回调则调用
  props.onWeatherRefresh?.(...args);
}

function handleWaterRefresh(...args) {
  props.onWaterRefresh?.(...args);
}
</script>

<style scoped>
.info-panel {
  width: 400px;
  height: 800px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0;
  box-sizing: border-box;
}

.panel {
  flex: 1 1 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
</style>
