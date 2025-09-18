<!-- components/dashboard/RouteMatch.vue -->
<template>
  <div>
    <el-card shadow="never" class="route-match-card">
      <div class="rm-head">
        <div class="rm-title">Matched Logistics</div>
        <div class="rm-info" v-if="targetAssignmentType">
          Searching: <strong>{{ targetAssignmentType }}</strong>
        </div>
      </div>

      <el-alert
        v-if="!targetAssignmentType"
        type="info"
        show-icon
        title="No assignmentType on current record"
        class="mb8"
      />

      <el-skeleton v-else-if="loading" :rows="3" animated />

      <div v-else>
        <div v-if="items.length === 0" class="empty-hint">
          No matches found.
        </div>

        <div v-else class="record-list">
          <div
            v-for="it in items"
            :key="uniqueKey(it)"
            class="record-card"
          >
            <!-- 点击整行展开 / 收起详情 -->
            <div
              class="record-line clickable"
              @click="toggleExpand(it, $event)"
              role="button"
              :aria-expanded="isExpanded(it)"
            >
              <div class="time-block">
                <span class="time-main">
                  {{
                    it.departureStart
                      ? dayjs(it.departureStart).format("HH:mm")
                      : "--:--"
                  }}
                </span>
                <span class="time-sep" />
                <span class="date-small">
                  {{
                    it.departureStart
                      ? dayjs(it.departureStart).format("DD-MM")
                      : "-"
                  }}
                </span>
              </div>

              <div class="route-block">
                <span class="place">
                  {{ it.originCity || it.originPort || "Unknown" }}
                  <span class="arrow">→</span>
                  {{ it.destinationCity || it.destinationPort || "Unknown" }}
                </span>

                <div class="cargo-small" style="margin-left: 8px">
                  {{ it.cargoType || it.cargo_type || it.goods || "-" }}
                </div>
              </div>

              <div class="right-meta">
                <!-- 显示 match score（timeRatio + distanceRatio） -->
                <div class="score-badge">{{ displayMatchScore(it) }}</div>

                <span class="expand-indicator">
                  <template v-if="isExpanded(it)">▾</template>
                  <template v-else>▸</template>
                </span>
              </div>
            </div>

            <!-- 展开详情 -->
            <transition name="fade">
              <div
                v-if="isExpanded(it)"
                class="record-details"
                @click.stop
              >
                <el-row :gutter="12" class="details-grid">
                  <el-col :span="11">
                    <div class="detail-row"><strong>Origin Port:</strong> {{ it.originPort || it.originCity || '-' }}</div>
                    <div class="detail-row"><strong>Destination Port:</strong> {{ it.destinationPort || it.destinationCity || '-' }}</div>
                    <div class="detail-row"><strong>Load / Upload:</strong> {{ formatDateTime(it.uploadTime || it.loadTime || it.load_at) }}</div>
                  </el-col>
                  <el-col :span="8">
                    <div class="detail-row"><strong>Departure:</strong> {{ formatDateTime(it.departureStart) }}</div>
                    <div class="detail-row"><strong>ETA / Arrival:</strong> {{ formatDateTime(it.arrivalEstimate || it.arrivalStart || it.arrivalEnd) }}</div>
                    <div class="detail-row"><strong>Remarks:</strong> {{ it.remarks || it.remark || '-' }}</div>
                  </el-col>
                  <el-col :span="5">
                    <div class="detail-row"><strong>Container:</strong> {{ it.containerId || it.container_id || '-' }}</div>
                    <div class="detail-row"><strong>Cargo:</strong> {{ it.cargoType || it.goods || '-' }}</div>
                    <div class="detail-row"><strong>Tonnage:</strong> {{ fmtNumber(it.tonnageDemand || it.tonnageAvailable) }}</div>
                  </el-col>
                </el-row>

                <!-- --- ROUTE COMPARISON AREA --- -->
                <div class="route-comparison">
                  <div v-if="routeLoadingFor(uniqueKey(it))" style="padding:8px 0">
                    <el-skeleton :rows="2" animated />
                  </div>

                  <div v-else-if="routeResults[uniqueKey(it)]">
                    <div class="leg-table">
                      <div
                        v-for="legKey in ['cd','ca','ab','bd']"
                        :key="legKey"
                        class="leg-row"
                      >
                        <div class="leg-name">{{ legLabels[legKey] }}</div>
                        <div class="leg-stats">
                          <div>Distance: {{ formatLength(routeResults[uniqueKey(it)].legs?.[legKey]?.length) }}</div>
                          <div>Duration: {{ formatDuration(routeResults[uniqueKey(it)].legs?.[legKey]?.duration) }}</div>
                        </div>
                      </div>
                    </div>

                    <div class="ratio-area" style="margin-top:8px">
                      <div><strong>Time ratio (cd / (ca+ab+bd)):</strong> {{ formatRatio(routeResults[uniqueKey(it)].ratios?.timeRatio) }}</div>
                      <div><strong>Distance ratio (cd / (ca+ab+bd)):</strong> {{ formatRatio(routeResults[uniqueKey(it)].ratios?.distanceRatio) }}</div>
                    </div>
                  </div>

                  <div v-else class="route-actions" style="margin-top:8px">
                    <div style="color:#666; font-size:13px; margin-bottom:6px">Compute route legs & ratios</div>
                    <el-button size="mini" type="primary" @click="computeForItem(it)">Compute</el-button>
                  </div>
                </div>

                <div class="details-actions">
                  <el-button size="mini" type="primary" @click="openFull(it)">Contact</el-button>
                  <el-button size="mini" @click="copyToClipboard(it)">Copy</el-button>
                </div>
              </div>
            </transition>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, toRef, watch } from "vue";
import dayjs from "dayjs";
import { listPublish } from "@/api/transport/publish";

const props = defineProps({
  record: { type: Object, default: () => ({}) },
});
const r = toRef(props, "record");

const emit = defineEmits(["feature-clicked"]); // 点击“查看完整信息”时向上传递

const loading = ref(false);
const items = ref([]);

// 支持多条同时展开：使用 Set 存 id keys
const expandedKeys = ref(new Set());

// cache 计算结果：key -> { legs: { cd, ca, ab, bd }, ratios: { timeRatio, distanceRatio } }
const routeResults = ref({}); // 使用对象以便模板响应
const routeLoadingKeys = ref(new Set());

// 判断要查找的目标 assignmentType：如果当前是 vessel_to_cargo 则查 cargo_to_vessel，反之亦然
const targetAssignmentType = computed(() => {
  const t = (r.value?.assignmentType || "").toString().toLowerCase();
  if (!t) return null;
  if (t === "vessel_to_cargo") return "cargo_to_vessel";
  if (t === "cargo_to_vessel") return "vessel_to_cargo";
  return null;
});

// 简单的状态映射（同 infologistic 的映射，可复用）
function statusClass(status) {
  const s = (status ?? "").toString().trim().toLowerCase();
  if (!s) return "status-unassigned";
  if (/(open|available|published|new)/.test(s)) return "status-published";
  if (/(assigned|booked|reserved)/.test(s)) return "status-assigned";
  if (/(in[-_\s]?progress|inprogress|ongoing|shipping|transporting|moving)/.test(s))
    return "status-shipping";
  if (/(closed|done|completed|finished)/.test(s)) return "status-completed";
  if (/(unassigned|cancel|cancelled)/.test(s)) return "status-unassigned";
  return "status-unassigned";
}

// 拉取对方类型的发布（pageSize 可按需调整）
async function loadMatches() {
  const atype = targetAssignmentType.value;
  if (!atype) {
    items.value = [];
    return;
  }
  loading.value = true;
  try {
    const res = await listPublish({
      assignmentType: atype,
      pageSize: 100,
    });
    const rows = res?.rows || (res?.data ? res.data.rows : []) || [];
    items.value = rows.map((it) => ({ ...it })); // shallow copy

    // 清掉已缓存的 routeResults（避免旧数据干扰）
    routeResults.value = {};
    routeLoadingKeys.value = new Set();

    // 自动批量计算分数（可按需注释）
    computeScoresForAll();
  } catch (e) {
    console.error("RouteMatch load error", e);
    items.value = [];
  } finally {
    loading.value = false;
  }
}

watch(
  [
    () => r.value?.assignmentType,
    () => r.value?.originPort,
    () => r.value?.destinationPort,
  ],
  () => {
    loadMatches();
  },
  { immediate: true }
);

// ========== helper / UI actions ==========

function uniqueKey(it) {
  return it.id || it._id || `${it.originPort || ''}::${it.destinationPort || ''}::${it.departureStart || ''}`;
}

function isExpanded(it) {
  return expandedKeys.value.has(uniqueKey(it));
}

function toggleExpand(it, evt) {
  const key = uniqueKey(it);
  if (expandedKeys.value.has(key)) {
    expandedKeys.value.delete(key);
  } else {
    expandedKeys.value.add(key);
    // expand 时自动触发计算（若未缓存）
    if (!routeResults.value[key]) computeForItem(it);
  }
  // Force Vue to notice Set change
  expandedKeys.value = new Set([...expandedKeys.value]);
}

// 点击“查看完整信息”：把记录发给父组件，让父打开 InfoPanel
function openFull(it) {
  const payload = { ...it, _layerType: "logistic" };
  emit("feature-clicked", payload);
}

// 复制条目为 JSON（小工具）
function copyToClipboard(it) {
  try {
    const txt = JSON.stringify(it, null, 2);
    navigator.clipboard?.writeText(txt);
  } catch (e) {
    console.warn("copy failed", e);
  }
}

function formatDateTime(v) {
  if (!v) return "-";
  try {
    return dayjs(v).format("YYYY-MM-DD HH:mm");
  } catch {
    return String(v);
  }
}
function fmtNumber(v) {
  if (v === null || v === undefined || v === "") return "-";
  const n = Number(v);
  return Number.isFinite(n) ? n.toLocaleString() : String(v);
}

// 显示 match score（timeRatio + distanceRatio）
function displayMatchScore(it) {
  const key = uniqueKey(it);
  // 优先使用已挂载到 item 的 _routeScore（attachScoreForKey 会写入）
  const sc = typeof it._routeScore !== "undefined" ? it._routeScore : getScoreFromKey(key);
  if (sc === null || sc === undefined || sc === Number.NEGATIVE_INFINITY) return "—";
  return `${(sc * 100).toFixed(1)}%`;
}

/* ==========================
   ROUTE CALC HELPERS
   ========================== */

// 将记录转换成 API 能识别的 ISRS id（优先 id 字段）
function getIsrs(obj, isOrigin = true) {
  if (!obj) return null;
  if (isOrigin) {
    return obj.originPortId || obj.originPortIdString || obj.originPort || null;
  } else {
    return obj.destinationPortId || obj.destinationPortIdString || obj.destinationPort || null;
  }
}

// 构建请求体（参考 RouteInfo）
function toISO(ts) {
  try { return ts ? new Date(ts).toISOString() : new Date().toISOString(); }
  catch { return new Date().toISOString(); }
}
function buildBody(startIsrs, endIsrs, computation = "FASTEST") {
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

async function fetchRouteBetween(startIsrs, endIsrs, computation = "FASTEST") {
  if (!startIsrs || !endIsrs) return null;
  const body = buildBody(startIsrs, endIsrs, computation);
  try {
    const res = await fetch("https://www.eurisportal.eu/api/RouteCalculatorV2/Calculate", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    if (!res.ok) {
      console.warn("Route API HTTP error", res.status);
      return null;
    }
    const data = await res.json();
    const its = Array.isArray(data?.Itineraries) ? data.Itineraries : [];
    const found = its.find(i => i?.ComputationType?.toUpperCase() === computation) || its[0] || null;
    if (!found) return null;
    return {
      duration: found.TotalDuration ?? null,
      length: found.TotalLength ?? null,
      raw: found,
    };
  } catch (e) {
    console.error("fetchRouteBetween error", e);
    return null;
  }
}

/**
 * computeForItem(it)
 * - it: matched item (c->d)
 * 计算 cd, ca, ab, bd 的 duration/length 并写入 routeResults[ key ]
 */
async function computeForItem(it) {
  const key = uniqueKey(it);
  if (routeResults.value[key]) return routeResults.value[key];
  if (routeLoadingKeys.value.has(key)) return;
  routeLoadingKeys.value.add(key);
  routeLoadingKeys.value = new Set([...routeLoadingKeys.value]);

  try {
    const A = getIsrs(r.value, true); // a (current origin)
    const B = getIsrs(r.value, false); // b (current dest)
    const C = getIsrs(it, true); // c (matched origin)
    const D = getIsrs(it, false); // d (matched dest)

    const missing = [];
    if (!C) missing.push("c");
    if (!D) missing.push("d");
    if (!A) missing.push("a");
    if (!B) missing.push("b");

    if (missing.length) {
      routeResults.value = {
        ...routeResults.value,
        [key]: {
          legs: {},
          ratios: { timeRatio: null, distanceRatio: null },
          note: `Missing IDs: ${missing.join(", ")}`,
        },
      };
      // attach score as -Infinity so it goes to bottom
      attachScoreForKey(key);
      return routeResults.value[key];
    }

    // 并发请求四条路径（默认 FASTEST）
    const [cdR, caR, abR, bdR] = await Promise.all([
      fetchRouteBetween(C, D, "FASTEST"),
      fetchRouteBetween(C, A, "FASTEST"),
      fetchRouteBetween(A, B, "FASTEST"),
      fetchRouteBetween(B, D, "FASTEST"),
    ]);

    const legs = {
      cd: { duration: cdR?.duration ?? null, length: cdR?.length ?? null, raw: cdR?.raw ?? null },
      ca: { duration: caR?.duration ?? null, length: caR?.length ?? null, raw: caR?.raw ?? null },
      ab: { duration: abR?.duration ?? null, length: abR?.length ?? null, raw: abR?.raw ?? null },
      bd: { duration: bdR?.duration ?? null, length: bdR?.length ?? null, raw: bdR?.raw ?? null },
    };

    const sumDuration = [legs.ca.duration, legs.ab.duration, legs.bd.duration]
      .map(n => (n == null ? 0 : Number(n)))
      .reduce((s, x) => s + (Number.isFinite(x) ? x : 0), 0);
    const sumLength = [legs.ca.length, legs.ab.length, legs.bd.length]
      .map(n => (n == null ? 0 : Number(n)))
      .reduce((s, x) => s + (Number.isFinite(x) ? x : 0), 0);

    const timeRatio = (legs.cd.duration != null && sumDuration > 0)
      ? Number(legs.cd.duration) / sumDuration
      : null;
    const distanceRatio = (legs.cd.length != null && sumLength > 0)
      ? Number(legs.cd.length) / sumLength
      : null;

    routeResults.value = {
      ...routeResults.value,
      [key]: {
        legs,
        ratios: {
          timeRatio,
          distanceRatio,
        },
      },
    };

    // 计算完单条后，挂上 score 并重新排序
    attachScoreForKey(key);

    return routeResults.value[key];
  } catch (e) {
    console.error("computeForItem error", e);
    routeResults.value = {
      ...routeResults.value,
      [key]: { legs: {}, ratios: { timeRatio: null, distanceRatio: null }, error: String(e) },
    };
    attachScoreForKey(key);
    return routeResults.value[key];
  } finally {
    routeLoadingKeys.value.delete(key);
    routeLoadingKeys.value = new Set([...routeLoadingKeys.value]);
  }
}

function routeLoadingFor(key) {
  return routeLoadingKeys.value.has(key);
}

/* ========== SCORE / SORT helpers ========== */

// 从 routeResults 计算 score = timeRatio + distanceRatio（null 当作 0）
function getScoreFromKey(key) {
  const rres = routeResults.value[key];
  if (!rres || !rres.ratios) return null;
  const t = rres.ratios.timeRatio;
  const d = rres.ratios.distanceRatio;
  if (t == null && d == null) return null;
  return (t || 0) + (d || 0);
}

// 把 score 附到 items 中对应项，并触发排序
function attachScoreForKey(key) {
  const idx = items.value.findIndex(it => uniqueKey(it) === key);
  const score = getScoreFromKey(key);
  if (idx !== -1) {
    // 未计算到 score 的用 -Infinity（会排到后面）
    items.value[idx]._routeScore = (score == null ? Number.NEGATIVE_INFINITY : score);
    sortItemsByScore();
  }
}

// 根据 _routeScore 对 items 降序排序
function sortItemsByScore() {
  items.value.sort((a, b) => {
    const sa = (typeof a._routeScore !== "undefined") ? a._routeScore : Number.NEGATIVE_INFINITY;
    const sb = (typeof b._routeScore !== "undefined") ? b._routeScore : Number.NEGATIVE_INFINITY;
    return sb - sa; // 降序
  });
  // 强制响应式更新
  items.value = [...items.value];
}

// 批量触发每条记录计算（并行）。若担心并发可改为分批。
async function computeScoresForAll() {
  if (!items.value || items.value.length === 0) return;
  const promises = items.value.map(it => computeForItem(it).catch(() => {}));
  await Promise.allSettled(promises);
  // 最后统一排序
  sortItemsByScore();
}

/* helper 显示格式 */
function formatLength(n) {
  if (n == null) return "-";
  const num = Number(n);
  if (!Number.isFinite(num)) return String(n);
  return `${num.toLocaleString()} m`;
}
function formatDuration(n) {
  if (n == null) return "-";
  const num = Number(n);
  if (!Number.isFinite(num)) return String(n);
  const h = Math.floor(num / 3600);
  const m = Math.floor((num % 3600) / 60);
  const s = Math.floor(num % 60);
  return `${h}h ${m}m ${s}s`;
}
function formatRatio(x) {
  if (x == null) return "-";
  return `${(x * 100).toFixed(1)}% (${x.toFixed(3)})`;
}

const legLabels = {
  cd: "C → D (matched origin → matched dest)",
  ca: "C → A (matched origin → current origin)",
  ab: "A → B (current origin → current dest)",
  bd: "B → D (current dest → matched dest)",
};
</script>

<style scoped>
.route-match-card {
  margin-top: 12px;
  padding: 8px 12px;
}
.rm-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.rm-title {
  font-weight: 600;
  font-size: 14px;
}
.rm-info {
  color: #6b7280;
  font-size: 12px;
}
.mb8 {
  margin-bottom: 8px;
}
.record-list {
  max-height: 420px;
  overflow-y: auto;
  padding-right: 6px;
}
.record-card {
  margin-bottom: 6px;
  border-radius: 6px;
  background: #f6f6f6;
  overflow: hidden;
  transition: box-shadow .12s ease;
}
.record-card:hover { box-shadow: 0 2px 6px rgba(26,39,63,0.12); }

.record-line {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 8px;
  cursor: pointer;
}
.time-block {
  min-width: 84px;
  display: flex;
  align-items: baseline;
  gap: 6px;
}
.time-main {
  color: #409eff;
  font-weight: 700;
  font-size: 12px;
}
.date-small {
  color: #777;
  font-size: 12px;
}
.route-block {
  flex: 1 1 auto;
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}
.place {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
}
.arrow {
  margin: 0 6px;
}
.cargo-small {
  color: #8c8c8c;
  font-size: 11px;
}
.right-meta {
  display:flex;
  align-items:center;
  gap:8px;
}
.expand-indicator { color:#999; font-size:14px }

/* score badge */
.score-badge {
  background: linear-gradient(90deg,#ffd54d,#ff8a65);
  padding: 4px 8px;
  border-radius: 12px;
  color: #1b1b1b;
  font-weight: 700;
  font-size: 12px;
}

/* details */
.record-details {
  padding: 10px 12px 12px;
  background: rgba(255,255,255,0.98);
  border-top: 1px solid rgba(0,0,0,0.04);
}
.details-grid { margin-top: 6px; }
.detail-row { padding: 4px 0; color: #333; font-size: 13px; }
.details-actions {
  display:flex;
  gap:8px;
  margin-top:10px;
  justify-content:flex-end;
}

/* route comparison */
.route-comparison { margin-top: 8px; }
.leg-table { display:flex; flex-direction:column; gap:6px; }
.leg-row { display:flex; justify-content:space-between; align-items:center; padding:6px; background:#fafafa; border-radius:4px }
.leg-name { font-weight:600; font-size:13px; color:#333; }
.leg-stats { text-align:right; font-size:12px; color:#444; }

/* animations */
.fade-enter-active, .fade-leave-active { transition: all .18s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-6px); }

.empty-hint {
  padding: 12px;
  color: #888;
  font-size: 13px;
}
</style>
