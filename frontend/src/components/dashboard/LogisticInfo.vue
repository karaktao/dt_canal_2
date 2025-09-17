<!-- components/dashboard/LogisticInfo.vue -->
<template>
  <el-card shadow="never" class="logistic-info-card">
    <div class="header">
      <div class="left">
        <h3>Logistic Details</h3>
        <div class="sub">{{ formatSubtitle }}</div>
      </div>

      <div class="controls">
        <el-tooltip content="切换显示全部物流字段" placement="top">
          <el-switch
            v-model="showMore"
            active-text="Show more"
            inactive-text="Summary"
            size="small"
          />
        </el-tooltip>

        <el-tooltip
          v-if="showMore && !editMode"
          content="Edit record"
          placement="top"
        >
          <el-button size="small" type="primary" icon="Edit" @click="enterEdit">
            Edit
          </el-button>
        </el-tooltip>

        <template v-if="editMode">
          <el-button
            size="small"
            type="success"
            @click="onSave"
            :loading="saving"
          >
            Save
          </el-button>
          <el-button size="small" @click="cancelEdit">Cancel</el-button>
        </template>
      </div>
    </div>

    <!-- 动态摘要信息（根据 assignmentType） -->
    <el-descriptions :column="2" border size="small" class="summary">
      <el-descriptions-item
        v-for="(lbl, idx) in summaryFields"
        :key="'summary-' + lbl + '-' + idx"
        :label="labelMap[lbl] || lbl"
      >
        <div v-html="fieldHtml(lbl)"></div>
      </el-descriptions-item>
    </el-descriptions>

    <!-- 编辑/更多区域 -->
    <transition name="fade">
      <div v-if="showMore" class="more-section">
        <div
          style="
            margin-bottom: 8px;
            display: flex;
            justify-content: space-between;
            align-items: center;
          "
        >
          <div style="font-weight: 600">All logistic fields</div>
        </div>

        <!-- 只读视图（非编辑） -->
        <el-descriptions
          v-if="!editMode"
          :column="2"
          border
          size="small"
          class="logistic-descriptions"
        >
          <el-descriptions-item
            v-for="(item, idx) in allFields"
            :key="item.label + '-' + idx"
            :label="labelMap[item.label] || item.label"
          >
            <div class="desc-value" :title="stripHtml(item.html)">
              {{ plainText(item.html) }}
            </div>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 编辑视图 -->
        <el-form
          v-if="editMode"
          ref="editFormRef"
          :model="editForm"
          :rules="editRules"
          label-position="top"
          class="edit-form"
        >
          <el-descriptions
            :column="2"
            border
            size="small"
            class="logistic-descriptions"
          >
            <el-descriptions-item
              v-for="(item, idx) in allFields"
              :key="'edit-' + item.label + '-' + idx"
              :label="labelMap[item.label] || item.label"
            >
              <template v-if="isEditableField(item.label)">
                <div
                  v-if="
                    [
                      'originPort',
                      'destinationPort',
                      'returnDestinationPort',
                    ].includes(item.label)
                  "
                >
                  <el-form-item
                    :prop="mapLabelToProp(item.label)"
                    style="margin: 0"
                  >
                    <el-autocomplete
                      v-model="editForm[mapLabelToProp(item.label)]"
                      :fetch-suggestions="querySearchBerths"
                      placeholder="Enter port"
                      @select="onPortSelectForLabel(item.label, $event)"
                      clearable
                    />
                  </el-form-item>
                </div>

                <div v-else-if="isDateField(item.label)">
                  <el-form-item
                    :prop="mapLabelToProp(item.label)"
                    style="margin: 0"
                  >
                    <el-date-picker
                      v-model="editForm[mapLabelToProp(item.label)]"
                      type="datetime"
                      value-format="YYYY-MM-DD HH:mm:ss"
                      placeholder="Select date/time"
                      clearable
                    />
                  </el-form-item>
                </div>

                <div v-else-if="isNumberField(item.label)">
                  <el-form-item
                    :prop="mapLabelToProp(item.label)"
                    style="margin: 0"
                  >
                    <el-input-number
                      v-model="editForm[mapLabelToProp(item.label)]"
                      :step="0.01"
                      style="width: 100%"
                    />
                  </el-form-item>
                </div>

                <div
                  v-else-if="['prioritySetting', 'status'].includes(item.label)"
                >
                  <el-form-item
                    :prop="mapLabelToProp(item.label)"
                    style="margin: 0"
                  >
                    <el-select
                      v-model="editForm[mapLabelToProp(item.label)]"
                      placeholder="Select"
                    >
                      <el-option
                        v-for="opt in optionsForField(item.label)"
                        :key="opt.value"
                        :label="opt.label"
                        :value="opt.value"
                      />
                    </el-select>
                  </el-form-item>
                </div>

                <div
                  v-else-if="
                    [
                      'cargoType',
                      'vesselName',
                      'containerId',
                      'containerDemand',
                    ].includes(item.label)
                  "
                >
                  <el-form-item
                    :prop="mapLabelToProp(item.label)"
                    style="margin: 0"
                  >
                    <el-input
                      v-model="editForm[mapLabelToProp(item.label)]"
                      clearable
                    />
                  </el-form-item>
                </div>

                <div v-else-if="item.label === 'remarks'">
                  <el-form-item prop="remarks" style="margin: 0">
                    <el-input
                      type="textarea"
                      v-model="editForm.remarks"
                      :rows="2"
                      clearable
                    />
                  </el-form-item>
                </div>

                <div v-else>
                  <el-form-item
                    :prop="mapLabelToProp(item.label)"
                    style="margin: 0"
                  >
                    <el-input
                      v-model="editForm[mapLabelToProp(item.label)]"
                      clearable
                    />
                  </el-form-item>
                </div>
              </template>

              <template v-else>
                <div class="desc-value" :title="stripHtml(item.html)">
                  {{ plainText(item.html) }}
                </div>
              </template>
            </el-descriptions-item>
          </el-descriptions>
        </el-form>
      </div>
    </transition>
  </el-card>
</template>

<script setup>
import { ref, computed, reactive, toRef, nextTick, watch } from "vue";
import dayjs from "dayjs";
import { ElMessage } from "element-plus";

import { listBerth } from "@/api/infrastructure/berth";
import { updatePublish } from "@/api/transport/publish";

const props = defineProps({
  record: { type: Object, default: () => ({}) },
});
const record = toRef(props, "record");

const showMore = ref(false);
const editMode = ref(false);
const saving = ref(false);
const editFormRef = ref(null);
const editForm = reactive({});

// -------------- assignmentType-based field definitions --------------
// 你可以按需在这里调整每种类型要显示的字段顺序与内容
const FIELDS_CARGO_TO_VESSEL = [
  "tenantId",
  "assignmentType",
  "originPort",
  "destinationPort",
  "originCity",
  "destinationCity",
  "departureStart",
  "departureEnd",
  "arrivalEstimate",
  "cargoType",
  "tonnageDemand",
  // "volumeDemand",
  "containerDemand",
  "tonnageAvailable",
  "isReturnTripAvailable",
  "returnDestinationPort",
  "prioritySetting",
  "status",
  "remarks",
];

const FIELDS_VESSEL_TO_CARGO = [
  "tenantId",
  "assignmentType",
  "vesselName",
  "tonnageAvailable",
  // "volumeAvailable",
  "containerAvailable",
  "vesselAvailabilityStart",
  "vesselAvailabilityEnd",
  "originPort",
  "destinationPort",
  "originCity",
  "destinationCity",
  // "departureStart",
  // "arrivalStart",
  // "arrivalEnd",
  // "prioritySetting",
  "status",
  "isEmptyVessel",
  "remarks",
];

// default fallback: show union of both
const FIELDS_ALL_FALLBACK = Array.from(
  new Set([...FIELDS_CARGO_TO_VESSEL, ...FIELDS_VESSEL_TO_CARGO])
);

// 显示在摘要（summary）中的字段：按类型区分、可自定义顺序
const SUMMARY_CARGO = [
  "originPort",
  "destinationPort",
  "Departure",
  "ETA",
  "tonnageDemand",
  "cargoType",
  "Priority",
  "status",
];
const SUMMARY_VESSEL = [
  "originPort",
  "destinationPort",
  "vesselName",
  "tonnageAvailable",
  "Departure",
  "ETA",
  "status",
];

// label 人性化映射（显示名）
const labelMap = {
  tenantId: "Tenant",
  assignmentType: "Type",
  cargoType: "Cargo Type",
  tonnageDemand: "Tonnage Demand",
  volumeDemand: "Volume Demand",
  containerDemand: "Container Demand",
  vesselName: "Vessel Name",
  isEmptyVessel: "Is Empty Vessel",
  tonnageAvailable: "Available Tonnage",
  volumeAvailable: "Available Volume",
  containerAvailable: "Available Containers",
  vesselAvailabilityStart: "Vessel Avail Start",
  vesselAvailabilityEnd: "Vessel Avail End",
  originPort: "Origin Port",
  destinationPort: "Destination Port",
  originCity: "Origin City",
  destinationCity: "Destination City",
  departureStart: "Departure",
  departureEnd: "Departure End",
  arrivalStart: "Arrival Start",
  arrivalEnd: "Arrival End",
  arrivalEstimate: "ETA",
  returnDestinationPort: "Return Destination",
  isReturnTripAvailable: "Return Trip?",
  prioritySetting: "Priority",
  status: "Status",
  remarks: "Notes",
};

// reactive computed: 所有字段（用于 more-section）
// 根据 record.assignmentType 选择不同的字段集并构造 {label, html}
const allFields = computed(() => {
  const r = record.value || {};
  const assignment = (r.assignmentType || "").toLowerCase();
  let fieldKeys = FIELDS_ALL_FALLBACK;
  if (assignment === "cargo_to_vessel") fieldKeys = FIELDS_CARGO_TO_VESSEL;
  else if (assignment === "vessel_to_cargo") fieldKeys = FIELDS_VESSEL_TO_CARGO;

  // 生成字段对象
  return fieldKeys.map((k) => {
    return { label: k, html: escapeHtml(computeFieldHtml(k, r)) };
  });
});

// summary fields computed（动态）
const summaryFields = computed(() => {
  const r = record.value || {};
  const assignment = (r.assignmentType || "").toLowerCase();
  if (assignment === "cargo_to_vessel") return SUMMARY_CARGO;
  if (assignment === "vessel_to_cargo") return SUMMARY_VESSEL;
  // fallback: pick some common fields
  return [
    "originPort",
    "destinationPort",
    "departureStart",
    "arrivalEstimate",
    "status",
  ];
});

// helper to compute single field html (string) for label
function computeFieldHtml(label, r) {
  // special labels for human-friendly "Departure"/"ETA" in summary: keep names as in SUMMARY arrays
  if (label === "Departure" || label === "ETA") {
    // map to actual props
    if (label === "Departure")
      return formatTime(
        r.departureStart || r.vesselAvailabilityStart || r.departureEnd
      );
    if (label === "ETA")
      return formatTime(r.arrivalEstimate || r.arrivalEnd || r.arrivalStart);
  }

  // standard mapping from label to record property
  switch (label) {
    case "tenantId":
      return fmt(r.tenantId);
    case "assignmentType":
      return fmt(r.assignmentType);
    case "cargoType":
      return fmt(r.cargoType || r.goods);
    case "tonnageDemand":
      return fmtNumber(r.tonnageDemand);
    case "volumeDemand":
      return fmtNumber(r.volumeDemand);
    case "containerDemand":
      return fmt(r.containerDemand);
    case "vesselName":
      return fmt(r.vesselName);
    case "isEmptyVessel":
      return fmt(r.isEmptyVessel);
    case "tonnageAvailable":
      return fmtNumber(r.tonnageAvailable);
    case "volumeAvailable":
      return fmtNumber(r.volumeAvailable);
    case "containerAvailable":
      return fmt(r.containerAvailable);
    case "vesselAvailabilityStart":
      return formatTime(r.vesselAvailabilityStart);
    case "vesselAvailabilityEnd":
      return formatTime(r.vesselAvailabilityEnd);
    case "originPort":
      return fmt(r.originPort);
    case "destinationPort":
      return fmt(r.destinationPort);
    case "originCity":
      return fmt(r.originCity);
    case "destinationCity":
      return fmt(r.destinationCity);
    case "departureStart":
      return formatTime(r.departureStart);
    case "departureEnd":
      return formatTime(r.departureEnd);
    case "arrivalStart":
      return formatTime(r.arrivalStart);
    case "arrivalEnd":
      return formatTime(r.arrivalEnd);
    case "arrivalEstimate":
      return formatTime(r.arrivalEstimate);
    case "returnDestinationPort":
      return fmt(r.returnDestinationPort);
    case "isReturnTripAvailable":
      return fmt(r.isReturnTripAvailable);
    case "prioritySetting":
      return fmt(r.prioritySetting);
    case "status":
      return fmt(r.status);
    case "remarks":
      return fmt(r.remarks || r.remark);
    case "Priority":
      return fmt(r.prioritySetting);
    default:
      // fallback: try to output property with same name
      return fmt(r[label]);
  }
}

// 用于 summary template 获取字段 html（会被 escapeHtml 再次包裹）
function fieldHtml(label) {
  const r = record.value || {};
  return escapeHtml(computeFieldHtml(label, r));
}

/* helper format / utils (保留你原来的实现) */
function escapeHtml(str) {
  if (str === null || str === undefined) return "—";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}
function stripHtml(s) {
  if (!s) return "";
  return String(s)
    .replace(/&lt;/g, "<")
    .replace(/&gt;/g, ">")
    .replace(/&amp;/g, "&");
}
function plainText(s) {
  return stripHtml(s);
}
function fmt(v) {
  if (v === null || v === undefined || v === "") return "—";
  return String(v);
}
function fmtNumber(v) {
  if (v === null || v === undefined || v === "") return "—";
  if (typeof v === "number") return v.toLocaleString();
  const n = Number(v);
  return isNaN(n) ? String(v) : n.toLocaleString();
}
function formatTime(v) {
  if (!v) return "—";
  try {
    const dt = new Date(v);
    if (isNaN(dt.getTime())) return String(v);
    return dt.toLocaleString();
  } catch (e) {
    return String(v);
  }
}

/* ========== 编辑相关 ========== */
// enterEdit: 把 record 的字段复制到 editForm（覆盖并包含两类字段）
function enterEdit() {
  const r = record.value || {};
  Object.assign(editForm, {
    id: r.id,
    tenantId: r.tenantId,
    assignmentType: r.assignmentType,
    publishedBy: r.publishedBy,
    publishedAt: r.publishedAt,
    createdAt: r.createdAt,
    assignedAt: r.assignedAt,
    cargoType: r.cargoType,
    tonnageDemand: r.tonnageDemand,
    volumeDemand: r.volumeDemand,
    containerDemand: r.containerDemand,
    vesselId: r.vesselId,
    mmsiNumber: r.mmsiNumber,
    vesselName: r.vesselName,
    isEmptyVessel: r.isEmptyVessel,
    tonnageAvailable: r.tonnageAvailable,
    volumeAvailable: r.volumeAvailable,
    containerAvailable: r.containerAvailable,
    isReturnTripAvailable: r.isReturnTripAvailable,
    vesselAvailabilityStart: r.vesselAvailabilityStart,
    vesselAvailabilityEnd: r.vesselAvailabilityEnd,
    returnDestinationPort: r.returnDestinationPort,
    returnDestinationPortId: r.returnDestinationPortId,
    originPort: r.originPort,
    originPortId: r.originPortId,
    destinationPort: r.destinationPort,
    destinationPortId: r.destinationPortId,
    departureStart: r.departureStart,
    departureEnd: r.departureEnd,
    arrivalStart: r.arrivalStart,
    arrivalEnd: r.arrivalEnd,
    arrivalEstimate: r.arrivalEstimate,
    uploadTime: r.uploadTime,
    unloadTime: r.unloadTime,
    prioritySetting: r.prioritySetting,
    status: r.status,
    intermediatePorts: r.intermediatePorts,
    intermediatePortsId: r.intermediatePortsId,
    cargoProperty: r.cargoProperty,
    originCity: r.originCity,
    destinationCity: r.destinationCity,
    isMerge: r.isMerge,
    isTransshipment: r.isTransshipment,
    containerId: r.containerId,
    remarks: r.remarks,
  });
  editMode.value = true;
  showMore.value = true;
  nextTick(() => {});
}
function cancelEdit() {
  editMode.value = false;
}

const editRules = {
  originPort: [{ required: true, message: "Origin required", trigger: "blur" }],
  destinationPort: [
    { required: true, message: "Destination required", trigger: "blur" },
  ],
  departureStart: [
    { required: true, message: "Departure start required", trigger: "change" },
  ],
};

const emit = defineEmits(["saved"]);

async function onSave() {
  try {
    await editFormRef.value.validate();
  } catch (e) {
    return;
  }
  saving.value = true;
  try {
    const payload = { ...editForm, id: editForm.id };
    const res = await updatePublish(payload);
    const savedRecord = res?.data ?? res ?? payload;
    ElMessage.success("Save Successful");
    editMode.value = false;
    emit("saved", savedRecord);
  } catch (err) {
    console.error("Save failed", err);
    ElMessage.error("Save failed");
  } finally {
    saving.value = false;
  }
}

/* 港口模糊搜索（保留） */
async function querySearchBerths(queryString, cb) {
  if (!queryString) {
    cb([]);
    return;
  }
  try {
    const res = await listBerth({
      name: queryString,
      pageNum: 1,
      pageSize: 1000,
    });
    const rows = res.rows || [];
    cb(
      rows.map((item) => ({
        value: item.name,
        berthIsrs: item.isrs,
        berthLatitude: item.latitude,
        berthLongitude: item.longitude,
        city: item.city,
      }))
    );
  } catch (err) {
    console.error("berth search error", err);
    cb([]);
  }
}

function onPortSelectForLabel(label, selected) {
  const prop = mapLabelToProp(label);
  if (!prop) return;
  editForm[prop] = selected.value;
  if (label === "originPort") {
    editForm.originPortId = selected.berthIsrs;
    editForm.originLat = selected.berthLatitude;
    editForm.originLon = selected.berthLongitude;
    editForm.originCity = selected.city || "";
  } else if (label === "destinationPort") {
    editForm.destinationPortId = selected.berthIsrs;
    editForm.destLat = selected.berthLatitude;
    editForm.destLon = selected.berthLongitude;
    editForm.destinationCity = selected.city || "";
  } else if (label === "returnDestinationPort") {
    editForm.returnDestinationPortId = selected.berthIsrs;
  }
}

/* 编辑字段映射 & 类型判断辅助（保留并可扩展） */
const editableSet = new Set([
  "originPort",
  "destinationPort",
  "returnDestinationPort",
  "departureStart",
  "departureEnd",
  "arrivalStart",
  "arrivalEnd",
  "arrivalEstimate",
  "vesselAvailabilityStart",
  "vesselAvailabilityEnd",
  "tonnageDemand",
  "tonnageAvailable",
  "volumeDemand",
  "volumeAvailable",
  "containerDemand",
  "cargoType",
  "vesselName",
  "containerId",
  "containerDemand",
  "remarks",
  "prioritySetting",
  "status",
]);

function isEditableField(label) {
  return editableSet.has(label);
}
function isDateField(label) {
  return [
    "departureStart",
    "departureEnd",
    "arrivalStart",
    "arrivalEnd",
    "arrivalEstimate",
    "vesselAvailabilityStart",
    "vesselAvailabilityEnd",
  ].includes(label);
}
function isNumberField(label) {
  return [
    "tonnageDemand",
    "tonnageAvailable",
    "volumeDemand",
    "volumeAvailable",
    "containerDemand",
  ].includes(label);
}

function mapLabelToProp(label) {
  const map = {
    tenantId: "tenantId",
    assignmentType: "assignmentType",
    cargoType: "cargoType",
    tonnageDemand: "tonnageDemand",
    volumeDemand: "volumeDemand",
    containerDemand: "containerDemand",
    vesselName: "vesselName",
    tonnageAvailable: "tonnageAvailable",
    volumeAvailable: "volumeAvailable",
    prioritySetting: "prioritySetting",
    status: "status",
    originPort: "originPort",
    destinationPort: "destinationPort",
    departureStart: "departureStart",
    departureEnd: "departureEnd",
    arrivalStart: "arrivalStart",
    arrivalEnd: "arrivalEnd",
    arrivalEstimate: "arrivalEstimate",
    vesselAvailabilityStart: "vesselAvailabilityStart",
    vesselAvailabilityEnd: "vesselAvailabilityEnd",
    returnDestinationPort: "returnDestinationPort",
    containerId: "containerId",
    remarks: "remarks",
  };
  return map[label] ?? label;
}

function optionsForField(label) {
  if (label === "prioritySetting") {
    return [
      { label: "shortest", value: "Shortest" },
      { label: "fatest", value: "Fatest" },
    ];
  }
  if (label === "status") {
    return [
      { label: "Published", value: "published" },
      { label: "Assigned", value: "assigned" },
      { label: "Completed", value: "completed" },
      { label: "Unassigned", value: "unassigned" },
      { label: "Shipping", value: "shipping" },
    ];
  }
  return [];
}
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.header .left {
  display: flex;
  flex-direction: column;
}
.header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}
.header .sub {
  color: #6b7280;
  font-size: 13px;
}

.controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.more-section {
  margin-top: 12px;
}

/* descriptions 样式 保持原来布局 */
.logistic-descriptions {
  width: 100%;
  table-layout: fixed;
}
.logistic-descriptions .el-descriptions__cell {
  padding: 8px 12px;
  vertical-align: middle;
  box-sizing: border-box;
  min-height: 48px;
}
.logistic-descriptions .el-descriptions__label {
  width: 35%;
  display: inline-block;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.logistic-descriptions .el-descriptions__content {
  width: 65%;
  white-space: normal;
  word-break: break-word;
}
.edit-form {
  background: rgba(255, 255, 255, 0.02);
  padding: 6px;
  border-radius: 4px;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
.summary .el-descriptions__label {
  font-weight: 600;
}
.desc-value {
  white-space: normal;
  word-break: break-word;
}
</style>
