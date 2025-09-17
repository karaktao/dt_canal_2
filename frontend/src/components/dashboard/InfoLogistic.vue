<script setup>
import { ref, computed, onMounted, watch } from "vue";
import dayjs from "dayjs";
import { listPublish } from "@/api/transport/publish"; // 与 postcargo.vue 一致的接口

// 定义 emit（和其它 map / layer 组件做法一致）
const emit = defineEmits(["feature-clicked"]);

// 数据源（合并后的）
const rawRecords = ref([]);

// 控制勾选框
const cargoChecked = ref(true); // 默认保持原来行为：cargo 是勾选的
const vesselChecked = ref(false);

// 基础请求：分别请求 cargo_to_vessel / vessel_to_cargo，并标注来源
const fetchCargo = async () => {
  try {
    const res = await listPublish({
      assignmentType: "cargo_to_vessel",
      pageSize: 1000,
    });
    const rows = res?.rows || (res?.data ? res.data.rows : []) || [];
    // 标注来源
    return (rows || []).map((r) => ({ ...r, _source: "cargo" }));
  } catch (e) {
    console.error("fetchCargo failed", e);
    return [];
  }
};

const fetchVessel = async () => {
  try {
    const res = await listPublish({
      assignmentType: "vessel_to_cargo",
      pageSize: 1000,
    });
    const rows = res?.rows || (res?.data ? res.data.rows : []) || [];
    return (rows || []).map((r) => ({ ...r, _source: "vessel" }));
  } catch (e) {
    console.error("fetchVessel failed", e);
    return [];
  }
};

// 重新加载数据：根据两个勾选项决定拉取哪些数据，合并并去重
const reloadRecords = async () => {
  try {
    const results = [];
    if (cargoChecked.value) {
      const c = await fetchCargo();
      results.push(...c);
    }
    if (vesselChecked.value) {
      const v = await fetchVessel();
      results.push(...v);
    }
    // 去重：用 key = id || _id || origin+dest+departureStart
    const map = new Map();
    results.forEach((item) => {
      const key =
        item.id ??
        item._id ??
        `${item.originPort || item.originCity || ""}::${
          item.destinationPort || item.destinationCity || ""
        }::${item.departureStart || ""}`;
      // 若已有，保留已有（不覆盖），但如果你希望以某来源优先可以调整
      if (!map.has(key)) map.set(key, item);
    });

    rawRecords.value = Array.from(map.values()).sort((a, b) => {
      if (!a?.departureStart) return 1;
      if (!b?.departureStart) return -1;
      return (
        dayjs(a.departureStart).valueOf() - dayjs(b.departureStart).valueOf()
      );
    });
  } catch (e) {
    console.error("reloadRecords failed", e);
    rawRecords.value = [];
  }
};

// 按出发日期分组（保留原有分组行为）
const recordsByDepartureStart = computed(() => {
  return rawRecords.value.reduce((groups, item) => {
    const d = item.departureStart
      ? dayjs(item.departureStart).format("YYYY-MM-DD")
      : "unknown";
    (groups[d] ||= []).push(item);
    return groups;
  }, {});
});

// 当用户点击某条记录时发出事件给父组件
function onRecordClick(record, e) {
  // 阻止事件冒泡（必要时）
  if (e && e.stopPropagation) e.stopPropagation();

  // 我们把原始 record 透传，同时添加一个 layer 类型，用于父组件区分
  const payload = { ...record, _layerType: "logistic" };
  console.log("[InfoLogistic] emit feature-clicked:", payload);
  emit("feature-clicked", payload);
}

// applySaved & defineExpose

function applySaved(saved) {
  if (!saved) return;
  const id = saved.id ?? saved._id;
  // 找到在 rawRecords 中的位置（用 id 或 _id）
  const idx = rawRecords.value.findIndex((r) => (r.id ?? r._id) == id);
  if (idx >= 0) {
    // 用 splice 替换，保持响应性；合并保留原有未提交字段
    rawRecords.value.splice(idx, 1, { ...rawRecords.value[idx], ...saved });
  } else {
    // 如果列表中没有该条，则把它插到最前（按需）
    rawRecords.value.unshift(saved);
  }

  // 可选：按 departureStart 再次排序与分组一致性
  rawRecords.value.sort((a, b) => {
    if (!a?.departureStart) return 1;
    if (!b?.departureStart) return -1;
    return (
      dayjs(a.departureStart).valueOf() - dayjs(b.departureStart).valueOf()
    );
  });
}

// 状态到 CSS 类的简单映射（用于不同颜色显示）
function statusClass(status) {
  const s = (status ?? "").toString().trim().toLowerCase();

  if (!s) return "status-unassigned"; // 没有状态时显示灰色（或改为 status-unknown）

  // 可用 / 新发布
  if (/(open|available|published|new)/.test(s)) return "status-published";

  // 已分配
  if (/(assigned|booked|reserved)/.test(s)) return "status-assigned";

  // 运输中 / 进行中 / shipping
  if (
    /(in[-_\s]?progress|inprogress|ongoing|shipping|transporting|moving)/.test(
      s
    )
  )
    return "status-shipping";

  // 已完成 / 关闭
  if (/(closed|done|completed|finished)/.test(s)) return "status-completed";

  // 未分配 / 取消 等（fallback）
  if (/(unassigned|cancel|cancelled)/.test(s)) return "status-unassigned";

  // 默认回退到一个“未知”类（建议在 CSS 中添加 .status-unknown）
  return "status-unassigned";
}

// 暴露给父组件调用
defineExpose({
  reloadRecords,
  applySaved,
});

// 当勾选变化时自动刷新
watch([cargoChecked, vesselChecked], () => {
  reloadRecords();
});

// 首次载入
onMounted(async () => {
  await reloadRecords();
});
</script>

<template>
  <div>
    <!-- 标题行：左侧 Logitics，右侧两个勾选框 -->
    <div class="section-header header-with-controls">
      <span class="title">Logitics</span>

      <div class="header-controls">
        <el-checkbox v-model="cargoChecked">Cargo</el-checkbox>
        <el-checkbox v-model="vesselChecked" style="margin-left: 8px"
          >Vessel</el-checkbox
        >
      </div>
    </div>

    <div class="info-logistic-root">
      <div
        v-if="Object.keys(recordsByDepartureStart).length === 0"
        class="empty-hint"
      >
        No logistics records.
      </div>

      <div class="record-list" v-else>
        <div
          v-for="(records, date) in recordsByDepartureStart"
          :key="date"
          class="daily-section"
        >
          <el-card
            v-for="record in records"
            :key="
              record.id ||
              record._id ||
              record.originPort + record.destinationPort + record.departureStart
            "
            class="record-card clickable"
            shadow="hover"
            @click="onRecordClick(record, $event)"
          >
            <!-- 单行显示：时间 HH:mm  小间隔  DD-MM   出发地 → 目的地 -->
            <!-- 单条记录（替换原 record-line 内部） -->
            <div class="record-line">
              <div class="time-block">
                <span
                  :class="[
                    'time-main',
                    record._source === 'vessel' ? 'time-vessel' : '',
                  ]"
                >
                  {{
                    record.departureStart
                      ? dayjs(record.departureStart).format("HH:mm")
                      : "--:--"
                  }}
                </span>
                <span class="time-sep" />
                <span class="date-small">
                  {{
                    record.departureStart
                      ? dayjs(record.departureStart).format("DD-MM")
                      : "-"
                  }}
                </span>
              </div>

              <!-- 路线块：出发地 → 目的地，下面显示货物种类（灰色小字） -->
              <div class="route-block">
                <span class="place">
                  {{ record.originCity || record.originPort || "Unknown" }}
                  <span class="arrow">→</span>
                  {{
                    record.destinationCity ||
                    record.destinationPort ||
                    "Unknown"
                  }}
                </span>
              </div>

              <div class="meta-block">
                  <!-- 货物种类：灰色小字显示（不使用蓝色 badge） -->
                  <span
                    class="cargo-small"
                    :title="
                      record.cargo_type ||
                      record.cargoType ||
                      record.cargoTypeName ||
                      ''
                    "
                  >
                    {{
                      record.cargo_type ||
                      record.cargoType ||
                      record.cargoTypeName ||
                      "—"
                    }}
                  </span>
              </div>

              <!-- 状态放到行尾（靠右） -->
              <span
                :class="[
                  'status-badge',
                  statusClass(
                    record.status || record.transportStatus || record.state
                  ),
                ]"
                role="status"
                aria-label="transport status"
              >
                {{
                  record.status || record.transportStatus || record.state || "—"
                }}
              </span>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* header 布局：标题在左，控件靠右 */
.section-header.header-with-controls {
  display: flex;
  align-items: center; /* 垂直居中所有子项 */
  justify-content: space-between; /* 左右两端布局，右侧控件靠右 */
  padding: 2px 0; /* 顶底间距，改小点使行更紧凑 */
  width: 95%;
  box-sizing: border-box;
}
.header-controls {
  display: flex;
  align-items: center; /* 垂直居中复选框与文字 */
  gap: 6px; /* 两个复选框之间的间距（改为 4 或 2 更紧凑） */
  margin-left: 0; /* 不再使用 margin-left:auto（使用 space-between 已推右） */
}
.header-controls ::v-deep .el-checkbox {
  margin: 0; /* 去掉默认的外边距 */
  padding: 0; /* 去掉内填充，保证紧凑 */
}
.header-controls ::v-deep .el-checkbox__label {
  display: inline-flex;
  align-items: center;
  line-height: 1;
  padding-left: 4px; /* label 与勾选框图标间距 */
  font-size: 12px; /* 与标题字号一致（可根据需要改为12/14） */
}
.section-header .title {
  margin-bottom: 1px; /* 紧贴上方 panel */
}

.info-logistic-root {
  width: 95%;
  margin-left: 10px;
  margin-top: 8px;
}

.record-list {
  height: 300px; /* 最大高度 300px */
  overflow-y: auto; /* 超出时显示纵向滚动条 */
  -webkit-overflow-scrolling: touch; /* iOS 平滑滚动 */
  padding-right: 6px; /* 留出滚动条空间，避免遮挡内容 */
}
/* 可选：轻微美化滚动条（WebKit 内核） */
.record-list::-webkit-scrollbar {
  width: 2px;
}

/* 卡片样式 */
.record-card {
  margin-bottom: 4px;
  font-size: 12px;
  border: 0.1px solid rgba(250, 250, 250, 0.788);
  border-radius: 6px;
  box-sizing: border-box;
  padding: 8px;
  background: #ffffffbe;
}

/* 每条记录一行的容器（三列：时间 / 路线 / meta） */
.record-line {
  display: flex;
  align-items: center;
  gap: 0px;
  width: 101%;
}

/* 新：可点击样式 */
.record-card.clickable {
  cursor: pointer;
}
.record-card.clickable:hover {
  transform: translateY(-2px);
  transition: transform 120ms ease;
  box-shadow: 0 3px 14px rgba(0, 0, 0, 0.12);
}

/* 时间块（左）*/
.time-block {
  min-width: 84px; /* 固定宽度，保证列对齐 */
  display: flex;
  align-items: baseline;
  gap: 0px;
}

/* 主要时间（HH:mm） */
.time-main {
  color: #409eff; /* 与 element 主色一致，醒目 */
  font-weight: 700;
  font-size: 12px;
}

/* vessel 来源时间为绿色 */
.time-vessel {
  color: #2ea44f !important; /* 绿色 */
}

/* 用作小间隔的占位（可视为短竖线或空白） */
.time-sep {
  display: inline-block;
  width: 8px;
  height: 1px;
}

/* 日期（DD-MM） */
.date-small {
  color: #777;
  font-size: 12px;
}

/* 路线块（中）*/
.route-block {
  flex: 1 1 auto;
  display: flex;
  gap: 0px;
  min-width: 0;
}

/* 出发地 → 目的地 一行显示，超出以省略号处理 */
.place {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #222222d3;
  font-weight: 500;
}

/* 箭头间距 */
.arrow {
  margin: 0 6px;
  color: #444;
}dt_users_skipper_profile

/* 右侧 meta 区 */
.meta-block {
  width: auto;          /* 不要把它强制定宽成 150px（可选） */
  min-width: 120px;
  display: flex;
  gap: 8px;             /* 原来 50px 太大，改小 */
  justify-content: flex-start; /* 左对齐内容 */
  align-items: center;
}

/* 小标签行 */
.cargo-row,
.status-row {
  display: flex;
  align-items: center;
  gap: 6px;
}



/* 状态小标签（颜色随类变化） */
.status-badge {
  display: inline-block;
  padding: 3px 4px;
  border-radius: 12px;
  font-size: 10px;
  color: white;
  min-width: 56px;
  text-align: center;
  margin: 0 0 0 8px; /* 改成向左偏移 8px，去掉原先的 margin-right: -20px */
}

/* 状态颜色映射 */
.status-published {
  background: #2ea44fb9; /* 绿色 = 可用/新发布 */
}
.status-assigned {
  background: #f59f0bb7; /* 橙色 = 已分配 */
}
.status-shipping {
  background: #40a0ffb9; /* 蓝色 = 进行中 */
}
.status-unassigned {
  background: #6b7280; /* 灰色 = 关闭/完成 */
}
.status-completed {
  background: #999999; /* 未知 */
}

/* 货物次要信息，字体较小且颜色浅（兼容旧注释） */
.cargo-small {
  color: #8c8c8c;
  font-size: 11px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 空提示 */
.empty-hint {
  padding: 12px;
  color: #888;
  font-size: 13px;
}

/* 小间距工具类 */
.mb-2 {
  margin-bottom: 8px;
}
</style>
