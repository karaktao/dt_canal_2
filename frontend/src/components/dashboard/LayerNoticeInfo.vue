<template>
  <div class="notice-info">
    <el-card shadow="never" class="sec">
      <!-- TITLE row -->
      <div class="title-row">
        <div class="title-left">
          <div class="title-main">{{ titleDisplay }}</div>
        </div>
        <div class="title-right">
          <el-tag v-if="limitation" type="warning" class="lim-tag">{{ limitation }}</el-tag>
        </div>
      </div>

      <!-- NTS row -->
      <div class="nts-row">
        <div class="nts-left">
          <strong>NTS:</strong>
          <span>{{ ntsLine }}</span>
          <el-tag size="mini" type="success" v-if="isActive">Active</el-tag>
        </div>
      </div>

      <el-divider></el-divider>

      <!-- details grid -->
      <el-row :gutter="12">
        <el-col :span="12">
          <div class="field"><strong>From:</strong> {{ show(record?.identification?.originator) }}</div>
          <div class="field"><strong>District:</strong> {{ show(record?.identification?.district) }}</div>
          <div class="field"><strong>Object:</strong> {{ show(objectLine) }}</div>
          <div class="field"><strong>Fairway:</strong> {{ show(record?.geo?.fairway_name) }}</div>
        </el-col>

        <el-col :span="12">
          <div class="field"><strong>Start:</strong> {{ formatDate(record?.ftm?.date_start) }}</div>
          <div class="field"><strong>End:</strong> {{ formatDate(record?.ftm?.date_end) }}</div>
          <div class="field"><strong>Duration:</strong> {{ durationText }}</div>
          <div class="field"><strong>Limitation:</strong> {{ show(record?.limitationCode) }}</div>
        </el-col>
      </el-row>

      <el-divider></el-divider>

      <!-- coordinates & contents -->
      <el-row :gutter="12">
        <el-col :span="12">
          <div class="coord-box">
            <div><strong>Coordinates:</strong></div>
            <div class="coord-raw">Raw: {{ show(record?.geo?.lat_str) }}{{ record?.geo?.lat_str && record?.geo?.long_str ? ',' : '' }} {{ show(record?.geo?.long_str) }}</div>
            <div class="coord-dec">Dec: {{ decLatLon }}</div>
          </div>
        </el-col>

        <el-col :span="12">
          <div class="contents-box">
            <div><strong>Contents:</strong></div>
            <div class="contents-text">{{ shortContents }}</div>
            <div v-if="moreContents">
              <el-button type="text" @click="showMore = !showMore">{{ showMore ? 'Show less' : 'Show more / language' }}</el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-divider></el-divider>

      <!-- issued & actions -->
      <div class="footer-row">
        <div class="issued-line">
          Issued: {{ formatDate(record?.identification?.date_issue) }}
          <span v-if="modifiedDate"> · Modified: {{ formatDate(modifiedDate) }}</span>
        </div>
      </div>
    </el-card>

    <!-- raw JSON (toggle) -->
    <el-card v-if="showRaw" shadow="never" class="sec">
      <pre class="code">{{ pretty(record) }}</pre>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { toLonLat } from "ol/proj";

// 接收事件： locate, subscribe, export-ics, no-coords
const emit = defineEmits(["locate", "subscribe", "export-ics", "no-coords"]);

const props = defineProps({
  record: { type: Object, required: true },
  showRaw: { type: Boolean, default: false },
});

const showMore = ref(false);

function show(v) {
  return v === undefined || v === null || v === "" ? "—" : String(v);
}

function pretty(o) {
  try {
    return JSON.stringify(o, null, 2);
  } catch {
    return String(o);
  }
}

function formatDate(v) {
  if (!v) return "—";
  try {
    const d = new Date(v);
    if (Number.isNaN(d.getTime())) return String(v);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")} ${String(d.getHours()).padStart(2, "0")}:${String(d.getMinutes()).padStart(2, "0")}`;
  } catch {
    return String(v);
  }
}

const titleDisplay = computed(() => {
  return props.record?.ftm?.contents || props.record?.geo?.name || props.record?.identification?.from || "Notice";
});

const limitation = computed(() => props.record?.limitationCode || (props.record?.ftm && props.record.ftm.reason_code) || null);

const ntsLine = computed(() => {
  if (!props.record?.ftm) return "—";
  const org = props.record.ftm.organisation || "";
  const year = props.record.ftm.year || "";
  const num = props.record.ftm.number || "";
  return `${org} · ${year}/${num}`;
});

const isActive = computed(() => {
  try {
    const s = props.record?.ftm?.date_start;
    const e = props.record?.ftm?.date_end;
    if (!s) return false;
    const now = new Date();
    const ds = new Date(s);
    const de = e ? new Date(e) : null;
    if (de) return now >= ds && now <= de;
    return now >= ds;
  } catch {
    return false;
  }
});

const objectLine = computed(() => {
  const name = props.record?.geo?.name;
  const type = props.record?.geo?.type_code;
  if (!name && !type) return "—";
  return `${name || "—"} ${type ? "(" + type + ")" : ""}`;
});

const modifiedDate = computed(() => {
  // 若没有专门的 modified 字段，可按需替换
  return props.record?.ftm?.modificationTimeInVisuRIS || null;
});

const shortContents = computed(() => {
  const c = props.record?.ftm?.contents || "";
  if (!c) return "—";
  if (showMore.value) return c;
  return c.length > 120 ? c.slice(0, 120) + "…" : c;
});

const moreContents = computed(() => {
  const c = props.record?.ftm?.contents || "";
  return c.length > 120;
});

// **唯一的 decLatLon 计算属性（避免重复声明）**
const decLatLon = computed(() => {
  const lat = props.record?.geo?.lat;
  const lon = props.record?.geo?.lon;
  if (Number.isFinite(lat) && Number.isFinite(lon)) {
    return `${Number(lat).toFixed(6)}, ${Number(lon).toFixed(6)}`;
  }
  return "—";
});

// Duration text (简单估算)
const durationText = computed(() => {
  try {
    const s = props.record?.ftm?.date_start;
    const e = props.record?.ftm?.date_end;
    if (!s) return "—";
    const ds = new Date(s);
    const de = e ? new Date(e) : null;
    if (!de) return "—";
    const diff = Math.max(0, de - ds);
    const hours = Math.floor(diff / (1000 * 60 * 60));
    const mins = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    return `${hours} h ${mins} m`;
  } catch {
    return "—";
  }
});

function copyCoords() {
  const lat = props.record?.geo?.lat;
  const lon = props.record?.geo?.lon;
  const text = Number.isFinite(lat) && Number.isFinite(lon) ? `${lat}, ${lon}` : `${props.record?.geo?.lat_str || ""} ${props.record?.geo?.long_str || ""}`;
  if (navigator.clipboard && text && text !== "—") {
    navigator.clipboard.writeText(text).catch(() => {});
  }
}

function onLocate() {
  const lat = props.record?.geo?.lat;
  const lon = props.record?.geo?.lon;
  if (Number.isFinite(lat) && Number.isFinite(lon)) {
    // 发给父组件（父组件负责 fromLonLat -> map.center）
    emit("locate", { lon: Number(lon), lat: Number(lat) });
  } else {
    emit("no-coords", props.record);
  }
}

function downloadJson() {
  const blob = new Blob([JSON.stringify(props.record, null, 2)], { type: "application/json" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `notice_${(props.record?.ftm?.year || "na")}_${(props.record?.ftm?.number || "na")}.json`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

function onExportIcs() {
  const start = props.record?.ftm?.date_start || props.record?.identification?.date_issue;
  const end = props.record?.ftm?.date_end || start;
  if (!start) {
    emit("export-ics", null);
    return;
  }
  const toICSDate = (s) => {
    try {
      const d = new Date(s);
      const YYYY = d.getFullYear();
      const MM = String(d.getMonth() + 1).padStart(2, "0");
      const DD = String(d.getDate()).padStart(2, "0");
      const hh = String(d.getHours()).padStart(2, "0");
      const mm = String(d.getMinutes()).padStart(2, "0");
      const ss = String(d.getSeconds()).padStart(2, "0");
      return `${YYYY}${MM}${DD}T${hh}${mm}${ss}Z`;
    } catch {
      return "";
    }
  };
  const uid = `notice-${Math.random().toString(36).slice(2)}@local`;
  const ics = [
    "BEGIN:VCALENDAR",
    "VERSION:2.0",
    "PRODID:-//YourApp//Notice//EN",
    "BEGIN:VEVENT",
    `UID:${uid}`,
    `DTSTAMP:${toICSDate(new Date().toISOString())}`,
    `DTSTART:${toICSDate(start)}`,
    `DTEND:${toICSDate(end)}`,
    `SUMMARY:${(props.record?.ftm?.contents || titleDisplay.value).replace(/\n/g, " ")}`,
    `DESCRIPTION:${(props.record?.ftm?.contents || "").replace(/\n/g, "\\n")}`,
    "END:VEVENT",
    "END:VCALENDAR",
  ].join("\r\n");

  const blob = new Blob([ics], { type: "text/calendar;charset=utf-8" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `notice_${(props.record?.ftm?.year || "na")}_${(props.record?.ftm?.number || "na")}.ics`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

function onSubscribe() {
  emit("subscribe", props.record);
}
</script>

<style scoped>
.notice-info { width: 100%; }
.sec { margin-bottom: 10px; }
.title-row { display:flex; justify-content:space-between; align-items:center; gap:12px; }
.title-main { font-weight:700; font-size:14px; }
.lim-tag { margin-left:8px; }
.nts-row { display:flex; justify-content:space-between; align-items:center; padding:6px 0; }
.nts-left { display:flex; align-items:center; gap:8px; }
.nts-actions { display:flex; gap:8px; align-items:center; }
.field { padding:4px 0; }
.coord-box, .contents-box { padding:8px; background:#fafafa; border-radius:6px; }
.coord-raw, .coord-dec { margin:6px 0; font-family:monospace; }
.contents-text { white-space:pre-wrap; max-height:100px; overflow:auto; }
.footer-row { display:flex; justify-content:space-between; align-items:center; margin-top:8px; }
.code { white-space: pre-wrap; word-break: break-word; display:block; padding:8px; border:1px solid #eee; border-radius:8px; background:#fafafa; }
</style>
