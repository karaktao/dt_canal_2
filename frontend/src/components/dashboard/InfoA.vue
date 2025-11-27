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
    <div class="section-header">
      <span class="title">Lock Status</span>
    </div>
    <InfoLock class="panel bottom" />
  </div>
</template>

<script setup>
import { toRefs } from "vue";
import InfoWeather from "./InfoWeather.vue";
import InfoWaterLevel from "./InfoWaterLevel.vue";
import InfoLock from './InfoLock.vue';

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
  width: 450px;
  height: 780px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.5);

}

/* 取消外部间距，确保 header 与 panel 紧贴 */
.section-header {
  margin: 0;
  padding: 8px 0;          /* 如果想更紧，设为 2px 或 0 */
  margin-bottom: -10px;     /* 与 panel 间距 */
  line-height: 1;
}

.section-header .title {
  font-size: 14px;
  font-weight: 600;
  display: inline-block;
  padding: 0;
  margin: 0;
  margin-left: 10px;
  margin-bottom: -15px; /* 紧贴上方 panel */
  color: #333333 !important;
}

</style>
