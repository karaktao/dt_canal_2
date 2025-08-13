<template>
  <el-card shadow="never" class="vessel-info-card">
    <div class="header">
    </div>

    <!-- Basic information -->
    <el-descriptions title="Basic Information" :column="2" border size="small">
      <el-descriptions-item label="trackID">{{ formatVal(record?.trackID) }}</el-descriptions-item>
      <el-descriptions-item label="name">{{ formatVal(record?.name) }}</el-descriptions-item>
      <el-descriptions-item label="callSign">{{ formatVal(record?.callSign) }}</el-descriptions-item>
      <el-descriptions-item label="mmsi">{{ formatVal(record?.mmsi) }}</el-descriptions-item>
      <el-descriptions-item label="eni">{{ formatVal(record?.eni) }}</el-descriptions-item>
      <el-descriptions-item label="imo">{{ formatVal(record?.imo) }}</el-descriptions-item>
      <el-descriptions-item label="ern">{{ formatVal(record?.ern) }}</el-descriptions-item>
    </el-descriptions>

    <!-- Time information -->
    <el-descriptions title="Time Information" :column="2" border size="small">
      <el-descriptions-item label="recTS">{{ formatTime(record?.recTS) }}</el-descriptions-item>
      <el-descriptions-item label="posTS">{{ formatTime(record?.posTS) }}</el-descriptions-item>
    </el-descriptions>

    <!-- Position information -->
    <el-descriptions title="Position Information" :column="2" border size="small">
      <el-descriptions-item label="lat">{{ formatCoord(record?.lat) }}</el-descriptions-item>
      <el-descriptions-item label="lon">{{ formatCoord(record?.lon) }}</el-descriptions-item>
      <el-descriptions-item label="positionISRS">{{ formatVal(record?.positionISRS) }}</el-descriptions-item>
      <el-descriptions-item label="positionISRSName">{{ formatVal(record?.positionISRSName) }}</el-descriptions-item>
    </el-descriptions>

    <!-- Movement status -->
    <el-descriptions title="Movement Status" :column="2" border size="small">
      <el-descriptions-item label="cog (°)">{{ formatNumber(record?.cog) }}</el-descriptions-item>
      <el-descriptions-item label="sog (kn)">{{ formatNumber(record?.sog) }}</el-descriptions-item>
      <el-descriptions-item label="direction">{{ formatVal(record?.direction) }}</el-descriptions-item>
      <el-descriptions-item label="moving">{{ formatBool(record?.moving) }}</el-descriptions-item>
    </el-descriptions>

    <!-- Dimension info -->
    <el-descriptions title="Dimension Information" :column="2" border size="small">
      <el-descriptions-item label="dimA">{{ formatVal(record?.dimA) }}</el-descriptions-item>
      <el-descriptions-item label="dimB">{{ formatVal(record?.dimB) }}</el-descriptions-item>
      <el-descriptions-item label="dimC">{{ formatVal(record?.dimC) }}</el-descriptions-item>
      <el-descriptions-item label="dimD">{{ formatVal(record?.dimD) }}</el-descriptions-item>
      <el-descriptions-item label="inlen (m)">{{ formatVal(record?.inlen) }}</el-descriptions-item>
      <el-descriptions-item label="inbm (m)">{{ formatVal(record?.inbm) }}</el-descriptions-item>
    </el-descriptions>

    <!-- Status flags -->
    <el-descriptions title="Status Flags" :column="2" border size="small">
      <el-descriptions-item label="st">{{ formatVal(record?.st) }}</el-descriptions-item>
      <el-descriptions-item label="rt (real-time)">{{ formatBool(record?.rt) }}</el-descriptions-item>
      <el-descriptions-item label="lt (long-term)">{{ formatBool(record?.lt) }}</el-descriptions-item>
      <el-descriptions-item label="owned">{{ formatBool(record?.owned) }}</el-descriptions-item>
      <el-descriptions-item label="privacyClass">{{ formatVal(record?.privacyClass) }}</el-descriptions-item>
      <el-descriptions-item label="defaultPrivacyClass">{{ formatVal(record?.defaultPrivacyClass) }}</el-descriptions-item>
    </el-descriptions>

    <!-- Other -->
    <el-descriptions title="Other" :column="1" border size="small">
      <el-descriptions-item label="matchID">{{ formatVal(record?.matchID) }}</el-descriptions-item>
      <el-descriptions-item label="activeVoyageCommonDenominator">{{ formatVal(record?.activeVoyageCommonDenominator) }}</el-descriptions-item>
      <el-descriptions-item label="linkID">{{ formatVal(record?.linkID) }}</el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<script setup>
import { toRef } from "vue";
const props = defineProps({
  record: { type: Object, default: () => ({}) },
});

// reactive ref to prop
const record = toRef(props, "record");

// Format helpers (English)
function formatVal(v) {
  if (v === null || v === undefined || v === "") return "No data";
  return String(v);
}
function formatBool(v) {
  if (v === null || v === undefined) return "No data";
  return v ? "Yes" : "No";
}
function formatNumber(v) {
  if (v === null || v === undefined || v === "") return "No data";
  if (typeof v === "number") return Number.isInteger(v) ? v : Number(v).toFixed(2);
  const n = Number(v);
  return Number.isNaN(n) ? String(v) : (Number.isInteger(n) ? n : n.toFixed(2));
}
function formatCoord(v) {
  if (v === null || v === undefined || v === "") return "No data";
  const n = Number(v);
  if (Number.isNaN(n)) return String(v);
  return n.toFixed(6);
}
function formatTime(v) {
  if (!v) return "No data";
  try {
    const dt = new Date(v);
    if (isNaN(dt.getTime())) return String(v);
    if (dt.getFullYear() <= 1900) return "Invalid / not updated";
    return dt.toLocaleString("en-GB", { hour12: false });
  } catch (e) {
    return String(v);
  }
}
</script>

<style scoped>


/* header 行样式 */
.header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}
.header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}
.header .sub {
  color: #6b7280;
  font-size: 13px;
}

/* el-descriptions spacing */
.vessel-info-card .el-descriptions {
  margin-bottom: 12px;
}

/* 强制 label 宽度以便列对齐（element-plus 的类名） */
.vessel-info-card .el-descriptions__label {
  min-width: 140px;                   /* label 列宽，保持列对齐 */
  color: #4b5563;
  font-weight: 500;
  white-space: nowrap;
  padding-right: 8px;
  box-sizing: border-box;
}

/* 内容列 */
.vessel-info-card .el-descriptions__content {
  color: #111827;
  font-size: 13px;
  word-break: break-word;
}

/* 小屏/窄面板时，让描述项垂直排列更漂亮 */
@media (max-width: 720px) {
  .vessel-info-card {
    margin: 10px;
    padding: 12px;
    max-width: calc(100vw - 24px);
  }
  .vessel-info-card .el-descriptions__label {
    min-width: 110px;
    font-size: 12px;
  }
}

/* 如果你希望面板四周与其它面板（例如左侧栏）进一步错位，可在父容器设置额外 margin 或定位 */
</style>

