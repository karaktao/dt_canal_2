<!-- components/dashboard/LogisticInfo.vue (with inline edit/save) -->
<template>
  <el-card shadow="never" class="logistic-info-card">
    <div class="header">
      <div class="left">
        <h3>Logistic Details</h3>
        <div class="sub">{{ formatSubtitle }}</div>
      </div>

      <!-- 右侧控制：显示更多开关 + 编辑按钮 -->
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

    <!-- 基础摘要信息（只读） -->
    <el-descriptions :column="3" border size="small" class="summary">
      <el-descriptions-item label="ID">{{
        fmt(record.id)
      }}</el-descriptions-item>
      <el-descriptions-item label="Source">{{
        fmt(record._source)
      }}</el-descriptions-item>

      <el-descriptions-item label="Origin">{{
        fmt(record.originCity || record.originPort)
      }}</el-descriptions-item>
      <el-descriptions-item label="Destination">{{
        fmt(record.destinationCity || record.destinationPort)
      }}</el-descriptions-item>

      <el-descriptions-item label="Departure">{{
        formatTime(record.departureStart)
      }}</el-descriptions-item>
      <el-descriptions-item label="ETA">{{
        formatTime(
          record.arrivalEstimate || record.arrivalEnd || record.departureEnd
        )
      }}</el-descriptions-item>

      <el-descriptions-item label="Cargo Type">{{
        fmt(record.cargoType || record.goods)
      }}</el-descriptions-item>
      <el-descriptions-item label="Tonnage Demand">{{
        fmtNumber(record.tonnageDemand)
      }}</el-descriptions-item>

      <el-descriptions-item label="Available Tonnage">{{
        fmtNumber(record.tonnageAvailable)
      }}</el-descriptions-item>
      <el-descriptions-item label="Priority">{{
        fmt(record.prioritySetting)
      }}</el-descriptions-item>

      <el-descriptions-item label="Status">{{
        fmt(record.status)
      }}</el-descriptions-item>
      <el-descriptions-item label="Notes">{{
        fmt(record.remarks || record.remark)
      }}</el-descriptions-item>
    </el-descriptions>

    <!-- 编辑模式：在 InfoPanel 里直接呈现表单 -->
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
          :column="3"
          border
          size="small"
          class="logistic-descriptions"
        >
          <el-descriptions-item
            v-for="(item, idx) in allFields"
            :key="item.label + '-' + idx"
            :label="item.label"
          >
            <div class="desc-value" :title="stripHtml(item.html)">
              {{ plainText(item.html) }}
            </div>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 编辑表单（直接在 InfoPanel 编辑） -->
        <el-form
          v-if="editMode"
          ref="editFormRef"
          :model="editForm"
          :rules="editRules"
          label-position="top"
          class="edit-form"
        >
          <el-row :gutter="12">
            <!-- Origin / Destination with autocomplete -->
            <el-col :span="12">
              <el-form-item label="Origin Port" prop="originPort">
                <el-autocomplete
                  v-model="editForm.originPort"
                  :fetch-suggestions="querySearchBerths"
                  placeholder="Enter origin port"
                  @select="handleOriginPortSelect"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Destination Port" prop="destinationPort">
                <el-autocomplete
                  v-model="editForm.destinationPort"
                  :fetch-suggestions="querySearchBerths"
                  placeholder="Enter destination port"
                  @select="handleDestinationPortSelect"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item label="Departure Start" prop="departureStart">
                <el-date-picker
                  v-model="editForm.departureStart"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="Departure start"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Departure End" prop="departureEnd">
                <el-date-picker
                  v-model="editForm.departureEnd"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="Departure end"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item label="Arrival Start" prop="arrivalStart">
                <el-date-picker
                  v-model="editForm.arrivalStart"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="Arrival start"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Arrival End" prop="arrivalEnd">
                <el-date-picker
                  v-model="editForm.arrivalEnd"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="Arrival end"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="12">
            <el-col :span="8">
              <el-form-item label="Cargo Type" prop="cargoType">
                <el-input v-model="editForm.cargoType" />
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item label="Tonnage Demand" prop="tonnageDemand">
                <el-input-number
                  v-model="editForm.tonnageDemand"
                  :step="0.01"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item label="Available Tonnage" prop="tonnageAvailable">
                <el-input-number
                  v-model="editForm.tonnageAvailable"
                  :step="0.01"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="12">
            <el-col :span="8">
              <el-form-item label="Priority" prop="prioritySetting">
                <el-select
                  v-model="editForm.prioritySetting"
                  placeholder="Select"
                >
                  <el-option label="low" value="low" />
                  <el-option label="med" value="med" />
                  <el-option label="high" value="high" />
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item label="Status" prop="status">
                <el-select v-model="editForm.status" placeholder="Select">
                  <el-option label="open" value="open" />
                  <el-option label="assigned" value="assigned" />
                  <el-option label="closed" value="closed" />
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item label="Vessel Name" prop="vesselName">
                <el-input v-model="editForm.vesselName" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="12">
            <el-col :span="24">
              <el-form-item label="Remarks" prop="remarks">
                <el-input type="textarea" v-model="editForm.remarks" :rows="2" />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 可继续扩展更多可编辑字段 -->
        </el-form>
      </div>
    </transition>
  </el-card>
</template>

<script setup>
import {
  ref,
  computed,
  reactive,
  toRef,
  nextTick,
  getCurrentInstance,
} from "vue";
import dayjs from "dayjs";
import { ElMessage } from "element-plus";

// 引入后端 API（与 postcargo 一致）
import { listBerth } from "@/api/infrastructure/berth";
import { updatePublish } from "@/api/transport/publish";

// props
const props = defineProps({
  record: { type: Object, default: () => ({}) }, // 当前展示的记录（只读）
});
const record = toRef(props, "record");

// state
const showMore = ref(false);
const editMode = ref(false);
const saving = ref(false);
const editFormRef = ref(null);

// 编辑表单模型（复制 record）
const editForm = reactive({});

// 当进入编辑时把 record 复制到 editForm
function enterEdit() {
  // shallow copy fields we care about (or copy all)
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
  // 确保显示更多已经打开
  showMore.value = true;
  nextTick(() => {
    // focus first field if needed
  });
}

// 取消编辑：恢复并退出
function cancelEdit() {
  editMode.value = false;
}

// 表单规则（简单示例）
const editRules = {
  originPort: [{ required: true, message: "Origin required", trigger: "blur" }],
  destinationPort: [
    { required: true, message: "Destination required", trigger: "blur" },
  ],
  departureStart: [
    { required: true, message: "Departure start required", trigger: "change" },
  ],
  arrivalStart: [
    { required: true, message: "Arrival start required", trigger: "change" },
  ],
};

// 使用 defineEmits（script setup 专用）
const emit = defineEmits(['saved']);

// 保存（调用 updatePublish）
// LogisticInfo.vue - 替换 onSave() 实现为：
async function onSave() {
  try {
    await editFormRef.value.validate();
  } catch (e) {
    return;
  }

  saving.value = true;
  try {
    const payload = { ...editForm };
    payload.id = editForm.id;

    // 等待后端返回
    const res = await updatePublish(payload);

    // 后端返回的记录优先使用 res.data（根据你的后端返回结构调整）
    const savedRecord = res?.data ?? res ?? payload;

    ElMessage.success("保存成功");
    editMode.value = false;

    // 把保存后的记录发给父组件
    emit("saved", savedRecord);
  } catch (err) {
    console.error("保存失败", err);
    ElMessage.error("保存失败");
  } finally {
    saving.value = false;
  }
}



/* ========== 港口模糊搜索（参考 postcargo） ========== */
const berthOptions = ref([]);
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

function handleOriginPortSelect(selected) {
  editForm.originPort = selected.value;
  editForm.originPortId = selected.berthIsrs;
  editForm.originLat = selected.berthLatitude;
  editForm.originLon = selected.berthLongitude;
  editForm.originCity = selected.city || "";
}

function handleDestinationPortSelect(selected) {
  editForm.destinationPort = selected.value;
  editForm.destinationPortId = selected.berthIsrs;
  editForm.destLat = selected.berthLatitude;
  editForm.destLon = selected.berthLongitude;
  editForm.destinationCity = selected.city || "";
}

/* ========== 其它辅助函数 ========== */
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

/* 生成 allFields（只读展示用）-- 保持原先 escapeHtml 处理 */
function escapeHtml(str) {
  if (str === null || str === undefined) return "—";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}
const allFields = computed(() => {
  const r = record.value || {};
  // 与原版保持一致（简化部分长 JSON）
  return [
    { label: "createBy", html: escapeHtml(fmt(r.createBy)) },
    { label: "createTime", html: escapeHtml(formatTime(r.createTime)) },
    { label: "updateBy", html: escapeHtml(fmt(r.updateBy)) },
    { label: "updateTime", html: escapeHtml(formatTime(r.updateTime)) },
    { label: "remark", html: escapeHtml(fmt(r.remark)) },

    { label: "id", html: escapeHtml(fmt(r.id)) },
    { label: "tenantId", html: escapeHtml(fmt(r.tenantId)) },
    { label: "assignmentType", html: escapeHtml(fmt(r.assignmentType)) },
    { label: "publishedBy", html: escapeHtml(fmt(r.publishedBy)) },
    { label: "publishedAt", html: escapeHtml(formatTime(r.publishedAt)) },

    { label: "createdAt", html: escapeHtml(formatTime(r.createdAt)) },
    { label: "assignedAt", html: escapeHtml(formatTime(r.assignedAt)) },
    { label: "cargoType", html: escapeHtml(fmt(r.cargoType)) },
    { label: "tonnageDemand", html: escapeHtml(fmtNumber(r.tonnageDemand)) },
    { label: "volumeDemand", html: escapeHtml(fmtNumber(r.volumeDemand)) },

    { label: "containerDemand", html: escapeHtml(fmt(r.containerDemand)) },
    { label: "vesselId", html: escapeHtml(fmt(r.vesselId)) },
    { label: "mmsiNumber", html: escapeHtml(fmt(r.mmsiNumber)) },
    { label: "vesselName", html: escapeHtml(fmt(r.vesselName)) },
    { label: "isEmptyVessel", html: escapeHtml(fmt(r.isEmptyVessel)) },

    {
      label: "tonnageAvailable",
      html: escapeHtml(fmtNumber(r.tonnageAvailable)),
    },
    {
      label: "volumeAvailable",
      html: escapeHtml(fmtNumber(r.volumeAvailable)),
    },
    {
      label: "containerAvailable",
      html: escapeHtml(fmt(r.containerAvailable)),
    },
    {
      label: "isReturnTripAvailable",
      html: escapeHtml(fmt(r.isReturnTripAvailable)),
    },

    {
      label: "vesselAvailabilityStart",
      html: escapeHtml(formatTime(r.vesselAvailabilityStart)),
    },
    {
      label: "vesselAvailabilityEnd",
      html: escapeHtml(formatTime(r.vesselAvailabilityEnd)),
    },

    {
      label: "returnDestinationPort",
      html: escapeHtml(fmt(r.returnDestinationPort)),
    },
    {
      label: "returnDestinationPortId",
      html: escapeHtml(fmt(r.returnDestinationPortId)),
    },

    { label: "originPort", html: escapeHtml(fmt(r.originPort)) },
    { label: "originPortId", html: escapeHtml(fmt(r.originPortId)) },
    { label: "destinationPort", html: escapeHtml(fmt(r.destinationPort)) },
    { label: "destinationPortId", html: escapeHtml(fmt(r.destinationPortId)) },

    { label: "departureStart", html: escapeHtml(formatTime(r.departureStart)) },
    { label: "departureEnd", html: escapeHtml(formatTime(r.departureEnd)) },
    { label: "arrivalStart", html: escapeHtml(formatTime(r.arrivalStart)) },
    { label: "arrivalEnd", html: escapeHtml(formatTime(r.arrivalEnd)) },
    {
      label: "arrivalEstimate",
      html: escapeHtml(formatTime(r.arrivalEstimate)),
    },

    { label: "uploadTime", html: escapeHtml(formatTime(r.uploadTime)) },
    { label: "unloadTime", html: escapeHtml(formatTime(r.unloadTime)) },
    { label: "prioritySetting", html: escapeHtml(fmt(r.prioritySetting)) },
    { label: "status", html: escapeHtml(fmt(r.status)) },

    { label: "intermediatePorts", html: escapeHtml(fmt(r.intermediatePorts)) },
    {
      label: "intermediatePortsId",
      html: escapeHtml(fmt(r.intermediatePortsId)),
    },

    { label: "originCity", html: escapeHtml(fmt(r.originCity)) },
    { label: "destinationCity", html: escapeHtml(fmt(r.destinationCity)) },
    { label: "isMerge", html: escapeHtml(fmt(r.isMerge)) },
    { label: "isTransshipment", html: escapeHtml(fmt(r.isTransshipment)) },

    { label: "containerId", html: escapeHtml(fmt(r.containerId)) },
    { label: "remarks", html: escapeHtml(fmt(r.remarks)) },
    { label: "_source", html: escapeHtml(fmt(r._source)) },
    { label: "_layerType", html: escapeHtml(fmt(r._layerType)) },
  ];
});

const formatSubtitle = computed(() => {
  const from = record.value?.originCity || record.value?.originPort || "";
  const to =
    record.value?.destinationCity || record.value?.destinationPort || "";
  return `${from} → ${to}`;
});

// helpers to strip html/return plain text (we use escapeHtml above)
function stripHtml(s) {
  if (!s) return "";
  return String(s)
    .replace(/&lt;/g, "<")
    .replace(/&gt;/g, ">")
    .replace(/&amp;/g, "&");
}
function plainText(s) {
  // our values have been escaped; replace HTML entities
  return stripHtml(s);
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

/* descriptions 样式 */
.logistic-descriptions {
  width: 100%;
  table-layout: fixed; /* 固定表格布局，按宽度分配列 */
}

.logistic-descriptions .el-descriptions__cell {
  padding: 8px 12px;
  vertical-align: middle;
  box-sizing: border-box;
  min-height: 48px; /* 防止单行高度不一致 */
}

.logistic-descriptions .el-descriptions__label {
  width: 35%;
  display: inline-block;
  font-weight: 600;
  white-space: nowrap; /* label 保持一行 */
  overflow: hidden;
  text-overflow: ellipsis;
}

.logistic-descriptions .el-descriptions__content {
  width: 65%;
  white-space: normal; /* 允许内容折行 */
  word-break: break-word; /* 长单词/JSON 会换行 */
}

/* 针对非常长的 json/text，限制高度并滚动 */
.logistic-descriptions .long-json {
  max-height: 120px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 12px;
}

/* 编辑表单 */
.edit-form {
  background: rgba(255, 255, 255, 0.6);
  padding: 10px;
  border-radius: 6px;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
