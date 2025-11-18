<template>
  <div class="info-notice-root">
    
    <div v-if="loading" class="loading-block">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else>
      <div v-if="notices.length === 0" class="empty-hint">
        No NtS messages found.
      </div>

      <!-- 列表容器（ref 用于滚动） -->
      <div
        class="notice-list"
        ref="listRef"
        @mouseenter="onListMouseEnter"
        @mouseleave="onListMouseLeave"
        @wheel.passive="onListWheel"
      >
        <el-card
          v-for="notice in notices"
          :key="notice.NtSHeaderId || notice._cacheKey"
          class="notice-card"
          shadow="hover"
          :data-focus="notice._focus ? 'true' : 'false'"
          :data-cache-key="notice.NtSHeaderId || notice._cacheKey"
        >
          <!-- 行：左侧标题/摘要，中间简短信息，右侧下拉控件 -->
          <div class="notice-row">
            <div class="notice-left">
              <div class="notice-title">
                {{ notice.NtSNumber || notice.NtSHeaderId || notice._cacheKey }}
              </div>

              <!-- 摘要 -->
              <div class="summary-and-action">
                <div class="notice-summary">
                  <span class="summary-item">{{ notice.NtSType || "-" }}</span>
                  <span class="summary-sep">•</span>
                  <span class="summary-item">{{
                    notice.WaterwayName || notice.SegmentName || "-"
                  }}</span>
                  <span class="summary-sep">•</span>
                  <span class="summary-item">{{
                    formatDateShort(notice.NtSEndTime)
                  }}</span>
                </div>
              </div>
            </div>

            <!-- 中央简短信息（可以扩展） -->
            <div class="notice-centre">
              <div v-if="notice.SegmentName">{{ notice.SegmentName }}</div>
              <div v-else class="muted">—</div>
            </div>

            <!-- 右侧：下拉 / 弹出详情 -->
            <div class="notice-action">
              <el-popover
                v-model:visible="popoverVisible[notice._cacheKey]"
                placement="bottom-end"
                width="420"
                trigger="click"
                :popper-class="'nts-popover-' + (notice._cacheKey || '')"
                @show="onPopoverShow(notice)"
              >
                <div class="detail-grid" style="min-width: 380px">
                  <div>
                    <strong>FromLeg:</strong> {{ notice.FromLeg || "-" }}
                  </div>
                  <div>
                    <strong>FairwaySectionId:</strong>
                    {{ notice.FairwaySectionId || "-" }}
                  </div>
                  <div>
                    <strong>NtSNumber:</strong> {{ notice.NtSNumber || "-" }}
                  </div>
                  <div>
                    <strong>NtSStartTime:</strong>
                    {{ formatDate(notice.NtSStartTime) }}
                  </div>
                  <div>
                    <strong>NtSEndTime:</strong>
                    {{ formatDate(notice.NtSEndTime) }}
                  </div>
                  <div>
                    <strong>NtSType:</strong> {{ notice.NtSType || "-" }}
                  </div>
                  <div style="grid-column: span 2">
                    <strong>NtSValue:</strong> {{ notice.NtSValue || "-" }}
                  </div>
                </div>

                <div
                  style="
                    margin-top: 8px;
                    display: flex;
                    gap: 8px;
                    justify-content: flex-end;
                  "
                >
                  <el-button
                    size="small"
                    type="text"
                    @click="onDropdownCommand(notice, 'openRaw')"
                    >Open raw</el-button
                  >
                  <el-button
                    size="small"
                    type="text"
                    @click="onDropdownCommand(notice, 'copyId')"
                    >Copy ID</el-button
                  >
                </div>

                <template #reference>
                  <el-button
                    size="small"
                    plain
                    @click.stop="togglePopover(notice._cacheKey)"
                  >
                    <ArrowDown v-if="!popoverVisible[notice._cacheKey]" />
                    <ArrowUp v-else />
                  </el-button>
                </template>
              </el-popover>
            </div>
          </div>
        </el-card>
      </div>

      <div style="margin-top: 8px; font-size: 12px; color: #888">
        Last updated: {{ lastUpdated || "-" }}
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  ref,
  reactive,
  onMounted,
  onBeforeUnmount,
  nextTick,
  watch,
} from "vue";
import dayjs from "dayjs";
import { ArrowDown, ArrowUp } from "@element-plus/icons-vue";
import { listPublish } from "@/api/transport/publish";

const CACHE_KEY = "infoNotice_cache_v1";
const CACHE_DATE_FORMAT = "YYYY-MM-DD";
const loading = ref(false);
const notices = ref([]);
const popoverVisible = reactive({});
const lastUpdated = ref(null);
const listRef = ref(null);
const autoLoadRoutes = ref(true);
const ROUTE_API = "https://www.eurisportal.eu/api/RouteCalculatorV2/Calculate";


nextTick(() => {
  const root = listRef.value;
  console.log(
    "clientHeight=",
    root?.clientHeight,
    "scrollHeight=",
    root?.scrollHeight
  );
  console.log('clientHeight=', root?.clientHeight, 'scrollHeight=', root?.scrollHeight)
});

// --- Autoplay config ---
const autoplayEnabled = ref(true); // 是否启用自动滚动（默认启用）
const autoplaySpeed = ref(30); // 像素/秒，默认 30 px/s（可通过滑块调整）
const pauseOnHover = ref(true); // 鼠标悬停时暂停
let rafId = null;
let lastTimestamp = null;
let manualPause = false; // 手动暂停（例如用户按了开关）
let resumeAutoplayTimer = null;

// helper：判断是否有 popover 打开（若有则暂停自动滚动）
function anyPopoverOpen() {
  return Object.values(popoverVisible).some(Boolean);
}

// 自动滚动动画函数（平滑）
function animateScroll(timestamp) {
  if (!lastTimestamp) lastTimestamp = timestamp;
  const elapsed = timestamp - lastTimestamp;
  lastTimestamp = timestamp;

  // 条件：只有 autoplayEnabled && !manualPause && !anyPopoverOpen()
  if (autoplayEnabled.value && !manualPause && !anyPopoverOpen()) {
    const speed = autoplaySpeed.value; // px per second
    const deltaPx = (elapsed / 1000) * speed;
    const root = listRef.value;
    if (root) {
      // 进行滚动（平滑，但我们手动计算像素）
      const maxScroll = root.scrollHeight - root.clientHeight;
      if (maxScroll <= 0) {
        // 没有可滚动内容
      } else {
        let next = root.scrollTop + deltaPx;
        if (next >= maxScroll - 1) {
          // 到底 -> 回到顶部以实现循环
          root.scrollTop = 0;
        } else {
          root.scrollTop = next;
        }
      }
    }
  }

  rafId = requestAnimationFrame(animateScroll);
}

// 控制函数
function startAutoplay() {
  if (rafId) cancelAnimationFrame(rafId);
  lastTimestamp = null;
  rafId = requestAnimationFrame(animateScroll);
}
function stopAutoplay() {
  if (rafId) {
    cancelAnimationFrame(rafId);
    rafId = null;
    lastTimestamp = null;
  }
}
function toggleAutoplay() {
  // 切换 autoplayEnabled
  autoplayEnabled.value = !autoplayEnabled.value;
  if (autoplayEnabled.value) {
    manualPause = false;
    startAutoplay();
  } else {
    manualPause = true;
    stopAutoplay();
  }
}

// 修改 onListMouseEnter / onListMouseLeave：
function onListMouseEnter() {
  // 鼠标移入：如果设置为悬停就暂停自动滚动
  if (pauseOnHover.value) {
    manualPause = true;
    stopAutoplay(); // 停止 RAF，释放对 scrollTop 的写入
  }
}

function onListMouseLeave() {
  // 鼠标移出：恢复（在没有 popover 打开、并且 autoplayEnabled 的情况下）
  if (pauseOnHover.value) {
    manualPause = false;
    // 小心不要在有 popover 时重启
    if (autoplayEnabled.value && !anyPopoverOpen()) {
      startAutoplay();
    }
  }
}

// 鼠标滚轮处理：允许原生滚动，短暂暂停 autoplay，若用户停止滚动则恢复
function onListWheel(e) {
  // 若没有滚动容器就返回
  const root = listRef.value;
  if (!root) return;

  // 停止自动滚动（以免覆盖用户手势）
  manualPause = true;
  stopAutoplay();

  // 这里我们不 preventDefault，让浏览器做原生滚动（因为用了 .passive）
  // 设置一个延迟：用户停止滚动后恢复自动滚动
  if (resumeAutoplayTimer) {
    clearTimeout(resumeAutoplayTimer);
  }
  resumeAutoplayTimer = setTimeout(() => {
    resumeAutoplayTimer = null;
    manualPause = false;
    if (autoplayEnabled.value && !anyPopoverOpen()) {
      // 等待一帧再启动以避免冲突
      requestAnimationFrame(() => startAutoplay());
    }
  }, 1500); // 1.5s 无滚动后恢复 —— 可按需调整
}

// 清理定时器（onBeforeUnmount 已有 stopAutoplay 调用，补充清理）
onBeforeUnmount(() => {
  stopAutoplay();
  if (resumeAutoplayTimer) {
    clearTimeout(resumeAutoplayTimer);
    resumeAutoplayTimer = null;
  }
});

// 其余保持原有逻辑：format、fetch、cache、parse 等
function formatDate(ts) {
  return ts ? dayjs(ts).format("YYYY-MM-DD HH:mm") : "-";
}
function formatDateShort(ts) {
  return ts ? dayjs(ts).format("DD/MM/YYYY") : "-";
}
function pretty(obj) {
  try {
    return JSON.stringify(obj, null, 2);
  } catch {
    return String(obj);
  }
}
function toISO(ts) {
  try {
    return ts ? new Date(ts).toISOString() : new Date().toISOString();
  } catch {
    return new Date().toISOString();
  }
}

async function fetchCargo() {
  try {
    const res = await listPublish({
      assignmentType: "cargo_to_vessel",
      pageSize: 500,
    });
    const rows = res?.rows || res?.data?.rows || res?.items || [];
    return (rows || []).map((r) => ({ ...r, _source: "cargo" }));
  } catch (e) {
    console.error("fetchCargo failed", e);
    window?.$message?.error?.("拉取 cargo 失败，请查看控制台");
    return [];
  }
}
function buildBody(startIsrs, endIsrs, computation = "SHORTEST", record = {}) {
  const departAt = toISO(record.departureStart);
  const dims = record.shipDimensions || {
    Height: 4.5,
    Width: 11,
    Draught: 3,
    Length: 111,
    CEMT: "IV",
  };
  return {
    StartISRS: startIsrs,
    EndISRS: endIsrs,
    ShipCategory: 0,
    ShipDimensions: dims,
    ShipSpeed: 0,
    DepartAt: departAt,
    CalculationOptions: {
      ComputationType: computation,
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
async function fetchRoute(start, end, computation, record) {
  if (!start || !end) return null;
  const body = buildBody(start, end, computation, record);
  try {
    const res = await fetch(ROUTE_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const data = await res.json();
    const its = Array.isArray(data?.Itineraries) ? data.Itineraries : [];
    const found =
      its.find((i) => i?.ComputationType?.toUpperCase() === computation) ||
      its[0] ||
      null;
    return found;
  } catch (e) {
    console.error("fetchRoute error", e);
    return null;
  }
}
function extractNtsFromItinerary(itin, computationType, record) {
  const out = [];
  if (!itin) return out;
  (itin.Legs || []).forEach((leg) => {
    (leg.Segments || []).forEach((seg) => {
      const sp = seg.SegmentProperties || {};
      const keys = Object.keys(sp || {});
      const hasNts = keys.some((k) => k.toLowerCase().startsWith("nts"));
      if (hasNts) {
        out.push({
          ComputationType: computationType,
          FromLeg: leg.Message || "",
          SegmentName: seg.SegmentName,
          WaterwayName: seg.WaterwayName,
          FairwaySectionId: seg.FairwaySectionId,
          SegmentProperties: sp,
          NtSHeaderId:
            sp.NtSHeaderId ||
            sp.NTSHeaderId ||
            sp.ntSHeaderId ||
            sp.ntSHeader ||
            null,
          NtSSectionId: sp.NtSSectionId || sp.NTSSectionId || null,
          NtSType: sp.NtSType || sp.NtStype || sp.NtS || null,
          NtSNumber: sp.NtSNumber || sp.NtSNumber || null,
          NtSValue: sp.NtSValue || null,
          NtSStartTime: sp.NtSStartTime || sp.NtSStart || null,
          NtSEndTime: sp.NtSEndTime || sp.NtSEnd || null,
          RecordRef: record,
        });
      }
    });
  });
  return out;
}
function readCache() {
  try {
    const raw = localStorage.getItem(CACHE_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw);
    if (!parsed || !parsed.date) return null;
    return parsed;
  } catch (e) {
    console.warn("readCache failed", e);
    return null;
  }
}
function writeCache(dataArr) {
  try {
    const payload = {
      date: dayjs().format(CACHE_DATE_FORMAT),
      lastUpdatedAt: new Date().toISOString(),
      data: dataArr,
    };
    localStorage.setItem(CACHE_KEY, JSON.stringify(payload));
  } catch (e) {
    console.warn("writeCache failed", e);
  }
}

async function loadAll(force = false) {
  loading.value = true;
  try {
    if (!force) {
      const cache = readCache();
      const today = dayjs().format(CACHE_DATE_FORMAT);
      if (cache && cache.date === today && Array.isArray(cache.data)) {
        notices.value = cache.data.map((n, idx) => ({
          ...n,
          _cacheKey: n.NtSHeaderId || `c${idx}`,
          _focus: n._focus || false,
        }));
        lastUpdated.value = cache.lastUpdatedAt
          ? dayjs(cache.lastUpdatedAt).format("YYYY-MM-DD HH:mm")
          : today;
        notices.value.forEach((n) => {
          if (!popoverVisible[n._cacheKey]) popoverVisible[n._cacheKey] = false;
        });
        loading.value = false;
        return;
      }
    }

    const cargos = await fetchCargo();
    const ntsMap = new Map();

    async function processCargo(cargo) {
      const start =
        cargo.originPortId ||
        cargo.originPortIdString ||
        cargo.originPort ||
        cargo.originISRS;
      const end =
        cargo.destinationPortId ||
        cargo.destinationPortIdString ||
        cargo.destinationPort ||
        cargo.destinationISRS;
      if (!start || !end) return;
      const computations = autoLoadRoutes.value
        ? ["SHORTEST", "FASTEST"]
        : ["SHORTEST"];
      for (const c of computations) {
        const itin = await fetchRoute(start, end, c, cargo);
        if (!itin) continue;
        const found = extractNtsFromItinerary(itin, c, cargo);
        found.forEach((n) => {
          const key =
            n.NtSHeaderId ||
            (n.NtSNumber
              ? `${n.NtSNumber}::${n.FairwaySectionId}`
              : `${n.FairwaySectionId}::${n.SegmentName}`);
          if (!key) return;
          if (!ntsMap.has(key)) ntsMap.set(key, n);
          else {
            const old = ntsMap.get(key);
            ntsMap.set(key, {
              ...old,
              ...n,
              SegmentProperties: {
                ...(old.SegmentProperties || {}),
                ...(n.SegmentProperties || {}),
              },
            });
          }
        });
      }
    }

    const concurrency = 6;
    const queue = [...cargos];
    const workers = new Array(concurrency).fill(0).map(async () => {
      while (queue.length) {
        const c = queue.shift();
        try {
          await processCargo(c);
        } catch (e) {
          console.error("processCargo failed", e);
        }
      }
    });
    await Promise.all(workers);

    const arr = Array.from(ntsMap.values()).map((n, i) => ({
      ...n,
      NtSStartTime:
        n.NtSStartTime ||
        (n.SegmentProperties && n.SegmentProperties.NtSStartTime) ||
        null,
      NtSEndTime:
        n.NtSEndTime ||
        (n.SegmentProperties && n.SegmentProperties.NtSEndTime) ||
        null,
    }));
    arr.sort((a, b) => {
      const ta = a.NtSStartTime ? new Date(a.NtSStartTime).getTime() : 0;
      const tb = b.NtSStartTime ? new Date(b.NtSStartTime).getTime() : 0;
      return ta - tb;
    });
    const serializable = arr.map((n, i) => ({
      ...n,
      _cacheKey: n.NtSHeaderId || `n-${i}`,
      _focus: false,
    }));
    notices.value = serializable;
    notices.value.forEach((n) => {
      if (!popoverVisible[n._cacheKey]) popoverVisible[n._cacheKey] = false;
    });
    writeCache(serializable);
    lastUpdated.value = dayjs().format("YYYY-MM-DD HH:mm");
    serializable.forEach((n, idx) => console.log(`NtS[${idx}]`, n));
  } catch (e) {
    console.error("loadAll failed", e);
    notices.value = [];
  } finally {
    loading.value = false;
  }
}

function reload(force = false) {
  loadAll(!!force);
}

// popover / action commands
function togglePopover(key) {
  if (!key) return;
  const currently = !!popoverVisible[key];
  Object.keys(popoverVisible).forEach((k) => {
    popoverVisible[k] = false;
  });
  popoverVisible[key] = !currently;
}

function onPopoverShow(notice) {
  // 保证查看详情时暂停自动滚动
  // popoverVisible 会驱动 anyPopoverOpen -> animateScroll 会检测并暂停
  console.log(
    "Showing popover for",
    notice ? notice.NtSHeaderId || notice._cacheKey : "unknown"
  );
}

function onDropdownCommand(notice, cmd) {
  if (cmd === "copyId") {
    navigator.clipboard
      ?.writeText(notice.NtSHeaderId || notice._cacheKey || "")
      ?.then(() => {
        window?.$message?.success?.("Copied");
      });
  } else if (cmd === "openRaw") {
    const w = window.open();
    w.document.write(
      "<pre style='white-space:pre-wrap; word-break:break-all; font-family:monospace;'>" +
        pretty(notice) +
        "</pre>"
    );
    popoverVisible[notice._cacheKey] = false;
  } else if (cmd === "showTime") {
    notices.value = notices.value.map((n) => ({
      ...n,
      _focus: n.NtSHeaderId === notice.NtSHeaderId,
    }));
  }
}

onMounted(() => {
  loadAll(false);
  // 启动 autoplay（如果启用）
  if (autoplayEnabled.value) startAutoplay();
});

// 结束时清理 RAF
onBeforeUnmount(() => {
  stopAutoplay();
});





</script>

<style scoped>
.info-notice-root {
  width: 95%;
  margin: 8px auto;
}
.auto-controls {
  margin-bottom: 6px;
}

.notice-list {
  /* 现有规则保持不变 */
  height: 420px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 8px;
  max-height: 520px;
  overflow-y: auto;
  padding-right: 6px;
  scroll-behavior: smooth;

  /* 新增：防止边界滚动“冒泡”到外层页面，移动端/桌面都更稳 */
  overscroll-behavior: contain;
  -webkit-overflow-scrolling: touch;
}

.notice-card {
  padding: 10px;
  cursor: default;
}
.notice-row {
  display: grid;
  grid-template-columns: 2fr 1fr 60px;
  gap: 8px;
  align-items: center;
}

.notice-title {
  font-weight: 600;
  font-size: 14px;
}
.notice-summary {
  color: #666;
  font-size: 12px;
  margin-top: 6px;
  margin-bottom: 4px;
  display: flex;
  gap: 6px;
  align-items: center;
}
.summary-item {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 220px;
  display: inline-block;
}
.summary-sep {
  color: #bbb;
}
.notice-centre {
  color: #333;
  font-size: 13px;
}
.notice-action {
  display: flex;
  justify-content: flex-end;
}

/* 详情网格样式（popover 内） */
.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  margin-top: 8px;
}
.raw-block {
  background: #f6f8fa;
  padding: 8px;
  border-radius: 4px;
  overflow: auto;
  max-height: 240px;
}
.empty-hint {
  padding: 12px;
  color: #888;
}
.notice-card[data-focus="true"] {
  border: 1px solid #e6a23c;
  box-shadow: 0 0 0 2px rgba(230, 162, 60, 0.06);
}

@media (max-width: 640px) {
  .notice-row {
    grid-template-columns: 1fr auto;
  }
  .notice-centre {
    display: none;
  }
}
</style>
