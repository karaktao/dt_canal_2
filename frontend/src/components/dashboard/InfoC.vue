<template>
  <el-card class="info-panel-c">
    <div class="section-header">
      <span class="title">Ship Status</span>
    </div>

    <InfoVessel class="panel vessel-panel" />

    <div class="section-header">
      <span class="title">Logitics</span>
    </div>

    <div class="section-header">
      <span class="title">Notices</span>
    </div>
  </el-card>
</template>

<script setup>
import InfoVessel from "./InfoVessel.vue";


</script>

<style >
.info-panel-c {
  width: 450px;
  height: 780px;
  display: flex;
  flex-direction: column;
  gap: 0px;
  padding: 0;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.5);
}

.info-panel-c .el-card__body {
  padding: 0 !important;
}

/* 取消外部间距，确保 header 与 panel 紧贴 */
.section-header {
  margin: 0;
  padding: 8px 0; /* 如果想更紧，设为 2px 或 0 */
  margin-bottom: -10px; /* 与 panel 间距 */
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
}
.vessel-panel { padding: 8px 10px; }
</style>