<template>
  <div class="berth-info">
    <!-- Basic Info -->
    <el-card shadow="never" class="sec">
      <div class="sec-title">Basic Info</div>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="Berth Name">
          {{ show(record.name) }}
        </el-descriptions-item>
        <el-descriptions-item label="Berth ID">
          {{ show(record.id) }}
        </el-descriptions-item>
        <el-descriptions-item label="VIN Code">
          {{ show(record.vinCode) }}
        </el-descriptions-item>
        <el-descriptions-item label="Geo Type">
          {{ show(record.geoType) }}
        </el-descriptions-item>
        <el-descriptions-item label="Condition">
          {{ mapCondition(record.condition) }}
        </el-descriptions-item>
        <el-descriptions-item label="Category">
          {{ show(record.category) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- Structure & Facilities -->
    <el-card shadow="never" class="sec">
      <div class="sec-title">Structure & Facilities</div>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="Has Crane">
          {{ yesNo(record.hasCrane) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- Location -->
    <el-card shadow="never" class="sec">
      <div class="sec-title">Location</div>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="City">
          {{ show(record.city) }}
        </el-descriptions-item>
        <el-descriptions-item label="Channel Side">
          {{ sideLabel(record.widthPosition) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- Waterway -->
    <el-card shadow="never" class="sec">
      <div class="sec-title">Waterway</div>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="Waterway ID">
          {{ show(record.routeId) }}
        </el-descriptions-item>
        <el-descriptions-item label="Route km (begin)">
          {{ show(record.routeKmBegin) }}
        </el-descriptions-item>
        <el-descriptions-item label="Route km (end)">
          {{ show(record.routeKmEnd) }}
        </el-descriptions-item>
        <el-descriptions-item label="Fairway ID">
          {{ show(record.fairwayId) }}
        </el-descriptions-item>
        <el-descriptions-item label="Fairway Section ID">
          {{ show(record.fairwaySectionId) }}
        </el-descriptions-item>
        <el-descriptions-item label="ISRS ID">
          {{ show(record.isrsId) }}
        </el-descriptions-item>
      </el-descriptions>
      <div v-if="hasKm" class="km-line">
        Route range: {{ record.routeKmBegin }} – {{ record.routeKmEnd }} km
      </div>
    </el-card>

    <!-- Optional: raw object for debugging -->
    <el-card v-if="showRaw" shadow="never" class="sec">
      <div class="sec-title">Raw</div>
      <code class="code">{{ pretty(record) }}</code>
    </el-card>

    <slot name="extra" />
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  record: { type: Object, required: true },
  showRaw: { type: Boolean, default: false },
});

const title = computed(() =>
  props.record.name || props.record.berthName || "Berth"
);

const hasKm = computed(
  () => props.record.routeKmBegin != null || props.record.routeKmEnd != null
);

// ---- helpers ----
function show(v) {
  return v === null || v === undefined || v === "" ? "—" : String(v);
}
function yesNo(v) {
  const t = Number(v);
  if (t === 1) return "Yes";
  if (t === 0) return "No";
  return show(v);
}
function sideLabel(v) {
  if (!v) return "—";
  const k = String(v).toUpperCase();
  if (k === "R") return "Right side";
  if (k === "L") return "Left side";
  if (k === "C" || k === "M") return "Center";
  return `Unknown (${v})`;
}
function mapCondition(v) {
  if (!v) return "—";
  const t = String(v).toUpperCase();
  if (t === "CONSTRUCTED") return "Constructed";
  if (t === "PLANNED") return "Planned";
  if (t === "UNDER_CONSTRUCTION") return "Under construction";
  return v;
}
function pretty(obj) {
  try { return JSON.stringify(obj, null, 2); } catch { return String(obj); }
}
</script>

<style scoped>
.title { font-weight: 600; margin: 0 0 10px; }
.sec { margin-bottom: 10px; }
.sec-title { font-weight: 600; margin-bottom: 6px; }
.code { white-space: pre-wrap; word-break: break-word; display:block; padding:8px; border:1px solid #eee; border-radius:8px; background:#fafafa; }
.km-line { margin-top: 8px; font-size: 12px; color: #666; }
</style>
