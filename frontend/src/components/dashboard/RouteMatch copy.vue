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

                 <div class="cargo-small" style="margin-left: 8px">90%</div>

                <span class="expand-indicator">
                  <!-- simple caret -->
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
                    <div class="detail-row"><strong>Destination Port:</strong> {{ it.destinationPort || it.destinationPort || '-' }}</div>
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

                <div class="details-actions">
                  <el-button size="mini" type="primary" @click="openFull(it)">Contect</el-button>
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
    items.value = rows;
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
  // 点击卡片展开/收起（阻止事件冒泡不会影响，因为 parent .record-card 不有别的 click）
  const key = uniqueKey(it);
  if (expandedKeys.value.has(key)) {
    expandedKeys.value.delete(key);
  } else {
    expandedKeys.value.add(key);
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
    // 反馈
    // 如果你使用了 ElementPlus 的 ElMessage，可以展示成功提示：
    // ElMessage.success('Copied');
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
  max-height: 320px;
  overflow-y: auto;
  padding-right: 6px;
}
.record-card {
  margin-bottom: 6px;
  border-radius: 6px;
  background: #eeeded79;
  overflow: hidden;
  transition: box-shadow .12s ease;
}
.record-card:hover { box-shadow: 0 2px 4px rgba(26, 39, 63, 0.178); }

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

/* status badge */
.status-badge {
  padding: 3px 8px;
  border-radius: 12px;
  color: #fff;
  font-size: 11px;
  min-width: 56px;
  text-align: center;
}
.status-published { background:#2ea44fb9; }
.status-assigned { background:#f59f0bb7; }
.status-shipping { background:#40a0ffb9; }
.status-unassigned { background:#6b7280; }
.status-completed { background:#999999; }

/* animations */
.fade-enter-active, .fade-leave-active { transition: all .18s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-6px); }

.empty-hint {
  padding: 12px;
  color: #888;
  font-size: 13px;
}
</style>
