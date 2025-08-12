<template>
  <div class="bridge-info">
    <!-- Basic Info -->
    <el-card shadow="never" class="sec">
      <div class="sec-title">Basic Info</div>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="Bridge Name">{{ show(r.name) }}</el-descriptions-item>
        <el-descriptions-item label="Bridge ID">{{ show(r.id) }}</el-descriptions-item>
        <el-descriptions-item label="Geo Type">{{ show(r.geoType) }}</el-descriptions-item>
        <el-descriptions-item label="Condition">{{ mapCondition(r.condition) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- Location -->
    <el-card shadow="never" class="sec">
      <div class="sec-title">Location</div>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="Coordinates (lat, lon)">
          {{ coordsText || "—" }}
        </el-descriptions-item>
        <el-descriptions-item label="City">{{ show(r.city) }}</el-descriptions-item>
        <el-descriptions-item label="Foreign Code">{{ show(r.foreignCode) }}</el-descriptions-item>
        <el-descriptions-item label="Reference Level">{{ show(r.referencelevel) }}</el-descriptions-item>
        <el-descriptions-item label="MHW Ref. Level">{{ show(r.mhwReferenceLevel) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- Waterway -->
    <el-card shadow="never" class="sec">
      <div class="sec-title">Waterway</div>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="Waterway ID">{{ show(r.routeId) }}</el-descriptions-item>
        <el-descriptions-item label="Route km (begin)">{{ show(r.routeKmBegin) }}</el-descriptions-item>
        <el-descriptions-item label="Route km (end)">{{ show(r.routeKmEnd) }}</el-descriptions-item>
        <el-descriptions-item label="Fairway ID">{{ show(r.fairwayId) }}</el-descriptions-item>
        <el-descriptions-item label="Fairway Section ID">{{ show(r.fairwaySectionId) }}</el-descriptions-item>
        <el-descriptions-item label="ISRS ID">{{ show(r.isrsId) }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="hasKm" class="km-line">
        Route range: {{ r.routeKmBegin }} – {{ r.routeKmEnd }} km
      </div>
    </el-card>

    <!-- Functional Params -->
    <el-card shadow="never" class="sec">
      <div class="sec-title">Functional Params</div>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="Can Open">{{ yesNo(r.canOpen) }}</el-descriptions-item>
        <el-descriptions-item label="Opening on Other Fairway">{{ yesNo(r.hasOpeningOnOtherFairway) }}</el-descriptions-item>
        <el-descriptions-item label="Voyage Planning Point">{{ yesNo(r.voyagePlanningPoint) }}</el-descriptions-item>
        <el-descriptions-item label="Remote Controlled">{{ yesNo(r.isRemoteControlled) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- Management -->
    <el-card shadow="never" class="sec">
      <div class="sec-title">Management</div>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="Administration ID">{{ show(r.administrationId) }}</el-descriptions-item>
        <el-descriptions-item label="Operating Times ID">{{ show(r.operatingTimesId) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- Time -->
    <el-card shadow="never" class="sec">
      <div class="sec-title">Time</div>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="Created At">{{ dateLike(r.CreatedAt || r.createdAt) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({ record: { type: Object, required: true } });
const r = computed(() => props.record || {});

const hasKm = computed(() => r.value.routeKmBegin != null || r.value.routeKmEnd != null);

// ---- coords (Point) from geoJSON / WKT ----
function parseMaybeJSON(v) {
  if (typeof v !== "string") return v;
  const s = v.trim();
  if ((s.startsWith("{") && s.endsWith("}")) || (s.startsWith("[") && s.endsWith("]"))) {
    try { return JSON.parse(s); } catch { return v; }
  }
  return v;
}
function parseGeoJSONPoint(g) {
  const gj = parseMaybeJSON(g);
  return gj && gj.type === "Point" && Array.isArray(gj.coordinates) ? gj.coordinates : null;
}
function parsePointWKT(wkt) {
  if (typeof wkt !== "string") return null;
  const m = wkt.match(/POINT\s*\(\s*([-\d\.]+)\s+([-\d\.]+)\s*\)/i);
  return m ? [Number(m[1]), Number(m[2])] : null; // [lon, lat]
}
const coordsText = computed(() => {
  const fromGJ = parseGeoJSONPoint(r.value.geoJSON);
  const fromWKT = parsePointWKT(r.value.geometry);
  const pt = fromGJ || fromWKT;
  return pt ? `${pt[1].toFixed(6)}, ${pt[0].toFixed(6)}` : null; // lat, lon
});

// ---- helpers ----
function show(v) { return v === null || v === undefined || v === "" ? "—" : String(v); }
function yesNo(v) {
  const t = Number(v);
  if (t === 1) return "Yes";
  if (t === 0) return "No";
  return show(v);
}
function mapCondition(v) {
  if (!v) return "—";
  const t = String(v).toUpperCase();
  if (t === "CONSTRUCTED") return "Constructed";
  if (t === "PLANNED") return "Planned";
  if (t === "UNDER_CONSTRUCTION") return "Under construction";
  return v;
}
function dateLike(v) {
  if (!v) return "—";
  const d = new Date(v);
  if (!isNaN(d)) return d.toISOString().slice(0, 10);
  return String(v);
}
</script>

<style scoped>
.sec { margin-bottom: 10px; }
.sec-title { font-weight: 600; margin-bottom: 6px; }
.km-line { margin-top: 8px; font-size: 12px; color: #666; }
</style>
