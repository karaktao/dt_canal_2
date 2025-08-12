<template>
  <div class="lock-info">
    <!-- Basic Info -->
    <el-card shadow="never" class="sec">
      <div class="sec-title">Basic Info</div>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="Lock Name">{{ show(r.name) }}</el-descriptions-item>
        <el-descriptions-item label="Lock ID">{{ show(r.id) }}</el-descriptions-item>
        <el-descriptions-item label="VIN Code">{{ show(r.vinCode) }}</el-descriptions-item>
        <el-descriptions-item label="Geo Type">{{ show(r.geoType) }}</el-descriptions-item>
        <el-descriptions-item label="Condition">{{ mapCondition(r.condition) }}</el-descriptions-item>
        <el-descriptions-item label="Related Complex">{{ show(r.relatedBuildingComplexName) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- Location -->
    <el-card shadow="never" class="sec">
      <div class="sec-title">Location</div>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="Address">{{ show(r.address) }}</el-descriptions-item>
        <el-descriptions-item label="Postal Code">{{ show(r.postalCode) }}</el-descriptions-item>
        <el-descriptions-item label="City">{{ show(r.city) }}</el-descriptions-item>
        <el-descriptions-item label="Phone">{{ show(r.phoneNumber) }}</el-descriptions-item>
        <el-descriptions-item label="Geometry (Polygon)">
          <span v-if="geomSummary">{{ geomSummary }}</span>
          <span v-else>—</span>
        </el-descriptions-item>
      </el-descriptions>
      <details v-if="r.geoJSON || r.geometry" class="raw-geom">
        <summary>Show raw geometry</summary>
        <code class="code">{{ pretty(r.geoJSON || r.geometry) }}</code>
      </details>
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
        <el-descriptions-item label="Number of Chambers">{{ show(r.numberOfChambers) }}</el-descriptions-item>
        <el-descriptions-item label="Rotation">{{ show(r.rotation) }}°</el-descriptions-item>
        <el-descriptions-item label="Remote Controlled">{{ yesNo(r.isRemoteControlled) }}</el-descriptions-item>
        <el-descriptions-item label="Voyage Planning Point">{{ yesNo(r.voyagePlanningPoint) }}</el-descriptions-item>
        <el-descriptions-item label="Upstream Ref. Level">{{ show(r.referenceLevelBeBu) }}</el-descriptions-item>
        <el-descriptions-item label="Downstream Ref. Level">{{ show(r.referenceLevelBoBi) }}</el-descriptions-item>
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

// ---- geometry summary (polygon rings / points) ----
function parseMaybeJSON(v) {
  if (typeof v !== "string") return v;
  const s = v.trim();
  if ((s.startsWith("{") && s.endsWith("}")) || (s.startsWith("[") && s.endsWith("]"))) {
    try { return JSON.parse(s); } catch { return v; }
  }
  return v;
}
const geomSummary = computed(() => {
  const gj = parseMaybeJSON(r.value.geoJSON);
  if (gj && gj.type === "Polygon" && Array.isArray(gj.coordinates)) {
    const rings = gj.coordinates.length;
    const pts = gj.coordinates.reduce((acc, ring) => acc + (Array.isArray(ring) ? ring.length : 0), 0);
    return `Polygon • ${rings} ring(s), ${pts} points`;
  }
  if (typeof r.value.geometry === "string" && r.value.geometry.toUpperCase().startsWith("POLYGON")) {
    // Very light WKT count
    const m = r.value.geometry.match(/\((.+)\)/);
    if (m) {
      const rings = m[1].split(")").filter(s => s.includes("(")).length || 1;
      const pts = (m[1].match(/[\d\-\.]+\s+[\d\-\.]+/g) || []).length;
      return `Polygon (WKT) • ${rings} ring(s), ${pts} points`;
    }
  }
  return null;
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
function pretty(obj) { try { return JSON.stringify(obj, null, 2); } catch { return String(obj); } }
</script>

<style scoped>
.sec { margin-bottom: 10px; }
.sec-title { font-weight: 600; margin-bottom: 6px; }
.km-line { margin-top: 8px; font-size: 12px; color: #666; }
.code { white-space: pre-wrap; word-break: break-word; display:block; padding:8px; border:1px solid #eee; border-radius:8px; background:#fafafa; }
.raw-geom { margin-top: 8px; }
</style>
