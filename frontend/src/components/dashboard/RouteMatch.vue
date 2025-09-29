<!-- components/dashboard/RouteMatch.vue -->
<template>
  <div>
    <el-card shadow="never" class="route-match-card">
            <div class="rm-head">
        <div class="rm-title">Matched Logistics</div>

        <!-- 新按钮：Start matching -->
        <div style="margin-left:12px">
          <el-button
            size="mini"
            type="primary"
            @click="startMatching"
            :disabled="started || loading"
          >
            Start matching
          </el-button>
        </div>

        <div class="rm-info" v-if="targetAssignmentType">
          Searching: <strong>{{ targetAssignmentType }}</strong>
        </div>
      </div>


      <!-- 只有点击 Start matching 后才会开始拉取并显示结果 -->
      <el-alert
        v-if="!targetAssignmentType"
        type="info"
        show-icon
        title="No assignmentType on current record"
        class="mb8"
      />

      <!-- 如果已点击开始并且正在加载：显示骨架 -->
      <el-skeleton v-if="started && loading" :rows="3" animated />

      <div v-else-if="started">
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
                <!-- <div class="route-comparison">
                  <div v-if="routeLoadingFor(uniqueKey(it))" style="padding:8px 0">
                    <el-skeleton :rows="2" animated />
                  </div>

                  <div v-else-if="routeResults[uniqueKey(it)]">
                    <div class="leg-table">
                      <div
                        v-for="legKey in ['ab','ca','cd','bd']"
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
                      <div><strong>Time ratio ((ca+ab+bd - cd) / cd):</strong> {{ formatRatio(routeResults[uniqueKey(it)].ratios?.timeRatio) }}</div>
                      <div><strong>Distance ratio ((ca+ab+bd - cd) / cd):</strong> {{ formatRatio(routeResults[uniqueKey(it)].ratios?.distanceRatio) }}</div>
                    </div>
                  </div>

                  <div v-else class="route-actions" style="margin-top:8px">
                    <div style="color:#666; font-size:13px; margin-bottom:6px">Compute route legs & ratios</div>
                    <el-button size="mini" type="primary" @click="computeForItem(it)">Compute</el-button>
                  </div>
                </div> -->

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

// 匹配开关
// 在已有的 loading/items 后新增
const started = ref(false);

// 点击按钮触发：开始匹配（拉取并计算）
function startMatching() {
  if (started.value) return;
  started.value = true;
  // 立即触发加载与计算
  loadMatches();
}





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
    () => started.value, // 也监听 started
  ],
  () => {
    // 只有用户点击 Start matching（started === true）后才自动触发
    if (started.value) {
      loadMatches();
    }
  },
  { immediate: false } // 不在组件挂载时自动调用
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

// // 显示 match score（timeRatio + distanceRatio）
// function displayMatchScore(it) {
//   const key = uniqueKey(it);
//   // 优先使用已挂载到 item 的 _routeScore（attachScoreForKey 会写入）
//   const sc = typeof it._routeScore !== "undefined" ? it._routeScore : getScoreFromKey(key);
//   if (sc === null || sc === undefined || sc === Number.NEGATIVE_INFINITY) return "—";
//   return `${(sc * 100).toFixed(1)}%`;
// }

// // 从 routeResults 计算 score = timeRatio + distanceRatio（null 当作 0）
// function getScoreFromKey(key) {
//   const rres = routeResults.value[key];
//   if (!rres || !rres.ratios) return null;
//   const t = rres.ratios.timeRatio;
//   const d = rres.ratios.distanceRatio;
//   if (t == null && d == null) return null;
//   return (t || 0) + (d || 0);
// }

/* ---------------------------
   Ratio -> normalized score helper
   --------------------------- */
function mapRatioToScore(r, S = 1.5) {
  if (r == null) return null;
  const num = Number(r);
  if (!Number.isFinite(num)) return null;
  const pos = Math.max(0, num); // 只惩罚正值
  const score = 1 / (1 + pos / S);
  return Math.max(0, Math.min(1, score));
}

/* ============
   displayMatchScore: 统一格式化并保证 0..100% 显示
   （只保留一个实现）
   ============ */
function displayMatchScore(it) {
  const key = uniqueKey(it);
  // 优先使用 item 上已有的 _routeScore（attachScoreForKey 会写）
  const raw = typeof it._routeScore !== "undefined" ? it._routeScore : getScoreFromKey(key);
  if (raw === null || raw === undefined || raw === Number.NEGATIVE_INFINITY) return "—";

  const s = Number(raw);
  if (!Number.isFinite(s)) return "—";
  const clamped = Math.max(0, Math.min(1, s)); // 确保 0..1
  return `${(clamped * 100).toFixed(1)}%`;
}

/* ===========================
   getScoreFromKey: 将 ratio 映射为 0..1 的综合得分
   替换掉原先的简单相加实现
   =========================== */

function getScoreFromKey(key) {
  const rres = routeResults.value[key];
  if (!rres || !rres.ratios) return null;

  const t = rres.ratios.timeRatio;
  const d = rres.ratios.distanceRatio;
  // 如果都不存在，只能依赖时间分数（如果能算）
  // 获取对应 item（candidate）
  const it = items.value.find(x => uniqueKey(x) === key);
  // cargo record 在 r.value 中（当前记录）
  const cargoTime = extractDepartTime(r.value);
  const vesselAvailableTime = extractAvailableStart(it);

  // 计算时间得分（0..1 或 null）
  const timeScoreTemporal = computeTemporalScore(cargoTime, vesselAvailableTime, {
    hourScale: 6,
    dayMultipliers: [1.0, 0.6, 0.3, 0.0],
  });

  // 现有 route score 逻辑（tScore, dScore -> combined）
  const S_time = 1.5;
  const S_dist = 1.5;
  const wTimeRoute = 0.5;
  const wDistRoute = 0.5;

  const tScore = (t == null ? null : mapRatioToScore(t, S_time));
  const dScore = (d == null ? null : mapRatioToScore(d, S_dist));

  let routeScore = null;
  if (tScore == null && dScore == null) routeScore = null;
  else if (tScore == null) routeScore = Math.max(0, Math.min(1, dScore));
  else if (dScore == null) routeScore = Math.max(0, Math.min(1, tScore));
  else routeScore = Math.max(0, Math.min(1, (wTimeRoute * tScore + wDistRoute * dScore)));

  // 若 routeScore 与 timeScoreTemporal 都存在 -> 合并
  // 可调参数：timeWeight 表示时间得分在最终结果中的占比（0..1）
  const timeWeight = 0.35; // 建议 0.25 ~ 0.4 之间调整
  const routeWeight = 1 - timeWeight;

  if (routeScore == null && (timeScoreTemporal == null || Number.isNaN(timeScoreTemporal))) return null;
  if (routeScore == null) return timeScoreTemporal; // 只有时间分数可用
  if (timeScoreTemporal == null) return routeScore; // 只有路线分数可用

  const finalCombined = Math.max(0, Math.min(1, routeWeight * routeScore + timeWeight * timeScoreTemporal));
  return finalCombined;
}







/* ===========================
   时间匹配（temporal score）实现
   =========================== */

/**
 * 从记录里抽取“装货/离开时间”（优先字段顺序可根据你数据改）
 * 支持：uploadTime, loadTime, load_at, departureStart, departureAt, departAt, depart_at
 */
function extractDepartTime(obj) {
  if (!obj) return null;
  const candidates = [
    "uploadTime", "loadTime", "load_at",
    "departureStart", "departureAt", "departAt", "depart_at",
    "availableStart", "available_from", "availableFrom"
  ];
  for (const k of candidates) {
    if (obj[k]) return dayjs(obj[k]).isValid() ? dayjs(obj[k]) : null;
  }
  // 若对象里没有上述字段但有 date/time 组合，可扩展
  return null;
}

/**
 * 从候选项中抽取“可用开始时间”（船只可用时间）
 * 你可以在这里将你的实际字段名补上：availableStart, availStart, availableFrom, startAvailable...
 */
function extractAvailableStart(obj) {
  if (!obj) return null;
  const candidates = [
    "availableStart", "available_from", "availableFrom",
    "availStart", "available_time_start",
    "startTime", "start_at", "availableAt",
    // 最后退回到 departureStart（某些数据把船开始时间放在这里）
    "departureStart", "departureAt", "departAt"
  ];
  for (const k of candidates) {
    if (obj[k]) return dayjs(obj[k]).isValid() ? dayjs(obj[k]) : null;
  }
  return null;
}

/**
 * 根据货物离开时间 cargoT 和 船只可用时间 vesselT 计算 temporal score (0..1)
 * 规则：
 *  - dayGap = floor((vesselT - cargoT) / 24h)
 *  - 若 vesselT <= cargoT 则 dayGap = 0（视为同天或早于）
 *  - dayGap 映射 multiplier: [1.0, 0.6, 0.3, 0.0] 分别对应 day0, day1, day2, day>=3
 *  - 在同天（day0）内，用小时差做细粒度惩罚：hourScore = 1 / (1 + (absHours / hourScale))
 *    默认 hourScale = 6（6小时差会把得分降到 0.5 量级）
 *  - 对 day1/day2 也可用小时调整（可选，下面实现用较弱的小时衰减）
 */
function computeTemporalScore(cargoT, vesselT, opts = {}) {
  if (!cargoT || !vesselT) return null;
  const hourScale = opts.hourScale ?? 6; // 同天内的小时尺度
  const dayMultipliers = opts.dayMultipliers ?? [1.0, 0.6, 0.3, 0.0]; // day0, day1, day2, day>=3

  const diffMs = vesselT.valueOf() - cargoT.valueOf();
  // 如果船可用时间早于货物离开（diffMs <= 0）当作 dayGap = 0（同天或更早）
  let dayGap = Math.floor(diffMs / 86400000);
  if (diffMs <= 0) dayGap = 0;
  if (dayGap >= 3) return 0; // 第四天以上等于 0

  // hour difference (absolute hours), 用于细粒度缩放
  const absHours = Math.abs(diffMs) / 3600000;

  // 细粒度小时得分 - 同天更敏感，后几天敏感度更低
  let hourScore;
  if (dayGap === 0) {
    hourScore = 1 / (1 + absHours / hourScale); // 0h -> 1, 6h -> ~0.5, 12h -> ~0.333
  } else {
    // day1/day2: 减弱小时敏感性（例如用更大的尺度）
    const dayHourScale = hourScale * (1 + dayGap); // day1:12h scale, day2:18h scale
    hourScore = 1 / (1 + Math.max(0, absHours - (24 * dayGap)) / dayHourScale);
    // 注意：这里把 absHours - 24*dayGap 用于衡量在那一天内的小时偏移，确保合理
  }

  const multiplier = dayMultipliers[Math.min(dayGap, dayMultipliers.length - 1)];
  const temporalScore = Math.max(0, Math.min(1, multiplier * hourScore));
  return temporalScore;
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
      ComputationType: "FASTEST", // "SHORTEST" | "FASTEST"
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

// helper: sleep
function sleep(ms) {
  return new Promise((res) => setTimeout(res, ms));
}


async function fetchRouteBetween(startIsrs, endIsrs, computation = "FASTEST", attempts = 3, baseBackoff = 500) {
  if (!startIsrs || !endIsrs) return null;
  const body = buildBody(startIsrs, endIsrs, computation);

  let attempt = 0;
  while (attempt < attempts) {
    try {
      const res = await fetch("https://www.eurisportal.eu/api/RouteCalculatorV2/Calculate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });

      // 当出现 429/503 等表示限流/服务不可用时，等待并重试
      if (res.status === 429 || res.status === 503) {
        const retryAfter = res.headers.get("Retry-After");
        const retryMs = retryAfter ? Number(retryAfter) * 1000 : (baseBackoff * Math.pow(2, attempt) + Math.random() * 200);
        console.warn(`Route API returned ${res.status}, retrying after ${retryMs}ms (attempt ${attempt + 1}/${attempts})`);
        await sleep(retryMs);
        attempt++;
        continue;
      }

      if (!res.ok) {
        console.warn("Route API HTTP error", res.status);
        // 对 4xx 中非 429 的错误直接返回 null（或根据需要特殊处理）
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
      console.warn("fetchRouteBetween network/error", e, `attempt ${attempt + 1}/${attempts}`);
      // 若还有尝试次数，等待后重试
      if (attempt < attempts - 1) {
        const wait = baseBackoff * Math.pow(2, attempt) + Math.random() * 200;
        await sleep(wait);
        attempt++;
        continue;
      } else {
        // exhausted attempts
        return null;
      }
    }
  }
  return null;
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

        // 之前已计算出 legs 对象与 sumDuration / sumLength
    const sumDuration = [legs.ca.duration, legs.ab.duration, legs.bd.duration]
      .map(n => (n == null ? 0 : Number(n)))
      .reduce((s, x) => s + (Number.isFinite(x) ? x : 0), 0);
    const sumLength = [legs.ca.length, legs.ab.length, legs.bd.length]
      .map(n => (n == null ? 0 : Number(n)))
      .reduce((s, x) => s + (Number.isFinite(x) ? x : 0), 0);

    // NEW formula: ratio = ((ca+ab+bd) - cd) / cd
    // i.e. (sumDuration - cd.duration) / cd.duration
    // protect against cd null/0 and keep null if divisor invalid
    const cdDuration = (legs.cd.duration == null) ? null : Number(legs.cd.duration);
    const cdLength = (legs.cd.length == null) ? null : Number(legs.cd.length);

    const timeRatio = (cdDuration != null && cdDuration > 0)
      ? ((Number(sumDuration) - cdDuration) / cdDuration)
      : null;

    const distanceRatio = (cdLength != null && cdLength > 0)
      ? ((Number(sumLength) - cdLength) / cdLength)
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
async function computeScoresForAll(batchSize = 8, batchDelay = 200, retryConfig = { attempts: 3, baseBackoff: 500 }) {
  if (!items.value || items.value.length === 0) return;

  // 可选：进度显示（在 UI 中可以读这个变量）
  // 定义一个 reactive progress 变量（如果你想展示进度，在上面声明 const progress = ref({ total: 0, done: 0 })）
  if (typeof progress !== "undefined" && progress) {
    progress.total = items.value.length;
    progress.done = 0;
  }

  const list = items.value.slice(); // snapshot
  for (let i = 0; i < list.length; i += batchSize) {
    const batch = list.slice(i, i + batchSize);
    // 对每个 batch 内并发计算（fetchRouteBetween 已经包含限流重试）
    await Promise.all(
      batch.map((it) =>
        // 通过 computeForItem 调用，computeForItem 内会调用 fetchRouteBetween（现在带重试）
        computeForItem(it).catch((err) => {
          console.warn("computeForItem failed for", uniqueKey(it), err);
        })
      )
    );

    // 更新进度（如果定义了 progress）
    if (typeof progress !== "undefined" && progress) {
      progress.done = Math.min(progress.total, progress.done + batch.length);
    }

    // 批次间短暂延迟，缓解峰值
    if (i + batchSize < list.length) {
      await sleep(batchDelay);
    }
  }

  // 全部完成后排序
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
