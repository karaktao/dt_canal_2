<template>
  <el-card shadow="never" class="route-match-card">
    <div class="rm-head">
      <!-- 切换：false=SHORTEST，true=FASTEST -->
      <div class="rm-toggle">
        <el-switch
          v-model="showMore"
          active-text="FASTEST"
          inactive-text="SHORTEST"
          size="small"
        />
      </div>
    </div>

    <!-- 条件不足 -->
    <el-alert
      v-if="!canQuery"
      type="info"
      show-icon
      :closable="false"
      title="Waiting for origin/destination to be available…"
      class="mb8"
    />

    <!-- 正在加载 -->
    <el-skeleton v-if="loading" :rows="3" animated />

    <!-- 加载完成但无结果 -->
    <el-empty
      v-else-if="canQuery && !current"
      description="No route found for the current record."
    />

    <!-- 结果展示 -->
    <div v-else class="rm-body">
      <!-- 第一行：Path + 右侧显示当前类型 -->
      <div class="rm-row rm-title-line">
        <div class="left">Path</div>
        <div class="right">
          <el-tag size="small" type="info">{{ current.ComputationType }}</el-tag>
        </div>
      </div>

      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="TotalLength">
          {{ numberWithUnit(current.TotalLength, "m") }}
        </el-descriptions-item>
        <el-descriptions-item label="TotalDuration">
          {{ secondsToHMS(current.TotalDuration) }}
        </el-descriptions-item>

        <el-descriptions-item label="NumberOfLocks">
          {{ current.NumberOfLocks ?? "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="TideDependent">
          {{ current.TideDependent === true ? "Yes" : current.TideDependent === false ? "No" : "-" }}
        </el-descriptions-item>

        <el-descriptions-item label="Allowed Height">
          {{ dim("Height") }}
        </el-descriptions-item>
        <el-descriptions-item label="Allowed Width">
          {{ dim("Width") }}
        </el-descriptions-item>
        <el-descriptions-item label="Allowed Draught">
          {{ dim("Draught") }}
        </el-descriptions-item>
        <el-descriptions-item label="Allowed Length">
          {{ dim("Length") }}
        </el-descriptions-item>
        <el-descriptions-item label="CEMT">
          {{ current.AllowedDimensions?.CEMT ?? "-" }}
        </el-descriptions-item>
      </el-descriptions>
    </div>
  </el-card>
</template>

<script setup>
import { computed, ref, toRef, watch } from "vue";

const props = defineProps({
  record: { type: Object, default: () => ({}) },
});
const r = toRef(props, "record");

const loading  = ref(false);
const showMore = ref(false);   // false = SHORTEST, true = FASTEST
const shortest = ref(null);    // SHORTEST 的 itinerary
const fastest  = ref(null);    // FASTEST  的 itinerary

const canQuery = computed(() => {
  const v = r.value || {};
  return !!(v.originPortId || v.originPortIdString || v.originPort)
      && !!(v.destinationPortId || v.destinationPortIdString || v.destinationPort);
});

// 当前展示的结果
const current = computed(() => (showMore.value ? fastest.value : shortest.value));

// ======== 工具：格式化 ========
function numberWithUnit(v, unit) {
  if (v === null || v === undefined) return "-";
  const n = Number(v);
  return Number.isFinite(n) ? `${n.toLocaleString()} ${unit}` : String(v);
}
function secondsToHMS(s) {
  if (s == null) return "-";
  const n = Number(s);
  if (!Number.isFinite(n)) return String(s);
  const h = Math.floor(n / 3600);
  const m = Math.floor((n % 3600) / 60);
  const sec = n % 60;
  return `${h}h ${m}m ${sec}s`;
}
function dim(key) {
  const d = current.value?.AllowedDimensions;
  if (!d) return "-";
  const val = d[key];
  if (val == null) return "-";
  const unit = key === "Length" || key === "Width" || key === "Height" || key === "Draught" ? "dm" : "";
  return `${val}${unit}${d[`${key}Source`] ? ` (${d[`${key}Source`]})` : ""}`;
}


// ----------------------叠加地图--------------------------
const emit = defineEmits([
  'show-route-paths' // 新增：把可叠加的 paths 发给父
]);
// 提取 paths 的小工具 & 发事件
function extractPathsFromItinerary(itin) {
  if (!itin) return [];
  const g = itin.Geometry;
  if (g && Array.isArray(g.paths) && g.paths.length > 0) {
    return g.paths.filter(p => p && p.trim() !== '');
  }
  // 兜底：从 Legs -> Segments 收集 CompressedGeometry
  const out = [];
  (itin.Legs || []).forEach(leg => {
    (leg.Segments || []).forEach(seg => {
      if (seg.CompressedGeometry) out.push(seg.CompressedGeometry);
    });
  });
  return out.filter(p => p && p.trim() !== '');
}

function emitCurrentPaths() {
  const itin = current.value;
  const paths = extractPathsFromItinerary(itin);
  if (paths.length) {
    emit('show-route-paths', {
      computation: itin?.ComputationType || (showMore.value ? 'FASTEST' : 'SHORTEST'),
      paths
    });
  } else {
    // 发一个清空指令可选
    emit('show-route-paths', { clear: true });
  }
}
// 在数据加载完 & 切换时触发


// 当用户切换 SHORTEST/FASTEST 时，也抛一次
watch(showMore, () => emitCurrentPaths());



// ======== 构造请求体（参考 postcargo） ========
function toISO(ts) {
  try { return ts ? new Date(ts).toISOString() : new Date().toISOString(); }
  catch { return new Date().toISOString(); }
}
function buildBody(startIsrs, endIsrs, computation = "SHORTEST") {
  const departAt = toISO(r.value?.departureStart);
  return {
    StartISRS: startIsrs,
    EndISRS: endIsrs,
    ShipCategory: 0,
    ShipDimensions: { Height: 4.5, Width: 11, Draught: 3, Length: 111, CEMT: "IV" },
    ShipSpeed: 0,
    DepartAt: departAt,
    CalculationOptions: {
      ComputationType: computation, // "SHORTEST" | "FASTEST"
      UseSailingSpeeds: true,
      UsePassageDuration: true,
      UseReducedDimensions: true,
      UseSmartReducedDimensions: true,
      ReturnWaypointTypes: 0,
    },
    ResultFormatting: {
      SplitGeometry: true,
      HideViaPoints: true,
      ResultLanguage: "EN",
      TimezoneOffset: 0,
      ReturnTranslatedNames: true,
    },
  };
}

// ======== 请求逻辑 ========
async function fetchRoute(computation) {
  const start = r.value.originPortId || r.value.originPortIdString || r.value.originPort;
  const end   = r.value.destinationPortId || r.value.destinationPortIdString || r.value.destinationPort;
  if (!start || !end) return null;

  const body = buildBody(start, end, computation);
  const res = await fetch("https://www.eurisportal.eu/api/RouteCalculatorV2/Calculate", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  const data = await res.json();

  const its = Array.isArray(data?.Itineraries) ? data.Itineraries : [];
  const found = its.find(i => i?.ComputationType?.toUpperCase() === computation) || its[0] || null;
  return found;
}

async function loadBoth() {
  if (!canQuery.value) {
    emit('show-route-paths', { clear: true }); // 条件不足时让父清线
    return;
  }
  loading.value = true;
  try {
    const [shortestItin, fastestItin] = await Promise.allSettled([
      fetchRoute("SHORTEST"),
      fetchRoute("FASTEST"),
    ]);
    shortest.value = shortestItin.status === "fulfilled" ? shortestItin.value : null;
    fastest.value  = fastestItin.status  === "fulfilled" ? fastestItin.value  : null;

    if (showMore.value && !fastest.value && shortest.value) showMore.value = false;
    if (!showMore.value && !shortest.value && fastest.value) showMore.value = true;

    // ✅ 结果回来后，把当前模式的路径抛给父
    emitCurrentPaths();
  } catch (e) {
    console.error("[RouteMatch] fetch error", e);
    emit('show-route-paths', { clear: true });
  } finally {
    loading.value = false;
  }
}

// 关键字段变化时重新请求
watch(
  () => [
    r.value?.originPortId, r.value?.originPortIdString, r.value?.originPort,
    r.value?.destinationPortId, r.value?.destinationPortIdString, r.value?.destinationPort,
    r.value?.departureStart,
  ],
  () => loadBoth(),
  { immediate: true }
);
</script>

<style scoped>
.route-match-card { margin-top: 12px; padding: 8px 12px; }
.rm-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.rm-title { font-weight: 600; font-size: 14px; }
.rm-toggle { display: flex; align-items: center; gap: 8px; }
.mb8 { margin-bottom: 8px; }
.rm-body { margin-top: 4px; }
.rm-row { display: flex; align-items: center; justify-content: space-between; }
.rm-title-line { font-weight: 600; margin-bottom: 6px; }
</style>
