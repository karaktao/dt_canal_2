<template>
  <el-card class="info-panel-c">
    <div class="section-header">
      <span class="title">Ship Status</span>
    </div>

    <InfoVessel class="panel vessel-panel" />

    <InfoLogistic
      ref="infoListRef"
      class="panel logistic-panel"
      @feature-clicked="$emit('feature-clicked', $event)"
      @edit="onEditRecord"
    />

    <div class="section-header">
      <span class="title">Notices</span>
    </div>
    <InfoNotice class="panel notice-panel" />

    <!-- LogisticInfo 放在父组件里，由父组件控制显示/关闭 -->
    <!-- 这里用 v-if 控制显示；你也可以改为用 <el-dialog> 包裹 LogisticInfo -->
    <LogisticInfo
      v-if="showLogisticEditor"
      v-model:record="editingRecord"
      :record="editingRecord"
      @saved="onLogisticSaved"
      @close="closeLogisticEditor"
    />

  </el-card>
</template>

<script setup>
import { ref } from "vue";
import InfoVessel from "./InfoVessel.vue";
import InfoLogistic from "./InfoLogistic.vue";
import InfoNotice from "./InfoNotice.vue";
import LogisticInfo from "./LogisticInfo.vue";
import RouteInfo from "@/components/dashboard/RouteInfo.vue";  
import RouteMatch from "@/components/dashboard/RouteMatch.vue";


// ref 指向 InfoLogistic 以便调用其 reloadRecords/applySaved
const infoListRef = ref(null);

// 用于控制 LogisticInfo 编辑器显示与传入 record
const showLogisticEditor = ref(false);
const editingRecord = ref(null);

// 当 InfoLogistic 中某条记录请求编辑时触发（InfoLogistic 应 emit('edit', record)）
function onEditRecord(record) {
  editingRecord.value = record || null;
  showLogisticEditor.value = true;
}

// 当 LogisticInfo 保存成功后触发：刷新 InfoLogistic
function onLogisticSaved(savedRecord) {
  // 推荐方式：重新拉取列表（更保险）
  infoListRef.value?.reloadRecords?.();

  // 如果你更喜欢局部更新（后端返回完整 savedRecord），也可以：
  // infoListRef.value?.applySaved?.(savedRecord);

  // 关闭编辑器
  showLogisticEditor.value = false;
}

// 关闭编辑器（LogisticInfo 可 emit 'close'）
function closeLogisticEditor() {
  showLogisticEditor.value = false;
}

// 在 infoc.vue 的 script setup 中加入（放在现有函数定义后）
defineExpose({
  onLogisticSaved,   // 允许父组件调用 infoc.vue 的 onLogisticSaved(savedRecord)
});
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
    border: none !important;      /* 去掉黑色描边 */
  box-shadow: none !important; 
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
  color: #333333 !important;
}
.vessel-panel {
  padding: 8px 10px;
}
</style>