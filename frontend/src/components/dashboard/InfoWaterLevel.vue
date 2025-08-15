<template>
  <el-card class="info-box">
    <div class="header">
      <h4>Water Level</h4>
      <el-button size="mini" @click="$emit('refresh')">Refresh</el-button>
    </div>

    <div class="content">
      <div class="row"><strong>Level:</strong> {{ data?.level ?? "—" }}</div>
      <div class="row"><strong>Trend:</strong> {{ data?.trend ?? "—" }}</div>

      <!-- 占位：历史曲线/小组件 -->
      <div class="chart" ref="chartEl">
        <small v-if="!hasHistory">No history data</small>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { toRef, onMounted } from "vue";
const props = defineProps({
  data: { type: Object, default: () => ({}) }
});
const data = toRef(props, "data");
const hasHistory = Boolean(data.value?.history && data.value.history.length);

onMounted(() => {
  // 在这里挂载水位历史图（如果需要）
});
</script>

<style scoped>
.info-box {
  width: 400px;
  height: 100%;
  box-sizing: border-box;
  background-color: rgba(255,255,255,0.5);
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
  display:flex;
  flex-direction: column;
}

.header{
  display:flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.content {
  overflow: auto;
  flex: 1 1 auto;
}
.row {
  margin: 6px 0;
}
.chart {
  margin-top: 8px;
  height: calc(100% - 80px);
  min-height: 80px;
  border-radius: 6px;
  background: rgba(255,255,255,0.06);
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
