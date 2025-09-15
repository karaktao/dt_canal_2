<script setup>
import { ref, computed, onMounted, watch } from "vue";
import dayjs from "dayjs";
import { listPublish } from "@/api/transport/publish"; // 与 postcargo.vue 一致的接口

// 数据源（合并后的）
const rawRecords = ref([]);

// 控制勾选框
const cargoChecked = ref(true);   // 默认保持原来行为：cargo 是勾选的
const vesselChecked = ref(false);

// 基础请求：分别请求 cargo_to_vessel / vessel_to_cargo，并标注来源
const fetchCargo = async () => {
  try {
    const res = await listPublish({ assignmentType: "cargo_to_vessel" });
    const rows = res?.rows || (res?.data ? res.data.rows : []) || [];
    // 标注来源
    return (rows || []).map(r => ({ ...r, _source: "cargo" }));
  } catch (e) {
    console.error("fetchCargo failed", e);
    return [];
  }
};

const fetchVessel = async () => {
  try {
    const res = await listPublish({ assignmentType: "vessel_to_cargo" });
    const rows = res?.rows || (res?.data ? res.data.rows : []) || [];
    return (rows || []).map(r => ({ ...r, _source: "vessel" }));
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
    results.forEach(item => {
      const key =
        item.id ??
        item._id ??
        `${item.originPort || item.originCity || ""}::${item.destinationPort || item.destinationCity || ""}::${item.departureStart || ""}`;
      // 若已有，保留已有（不覆盖），但如果你希望以某来源优先可以调整
      if (!map.has(key)) map.set(key, item);
    });
    rawRecords.value = Array.from(map.values());
  } catch (e) {
    console.error("reloadRecords failed", e);
    rawRecords.value = [];
  }
};

// 按出发日期分组（保留原有分组行为）
const recordsByDepartureStart = computed(() => {
  return rawRecords.value.reduce((groups, item) => {
    const d = item.departureStart ? dayjs(item.departureStart).format("YYYY-MM-DD") : "unknown";
    (groups[d] ||= []).push(item);
    return groups;
  }, {});
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
        <el-checkbox v-model="cargoChecked" >Cargo</el-checkbox>
        <el-checkbox v-model="vesselChecked"  style="margin-left:8px;">Vessel</el-checkbox>
      </div>
    </div>

    <div class="info-logistic-root">
      <div v-if="Object.keys(recordsByDepartureStart).length === 0" class="empty-hint">
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
            class="record-card"
            shadow="hover"
          >
            <!-- 单行显示：时间 HH:mm  小间隔  DD-MM   出发地 → 目的地 -->
            <div class="record-line">
              <div class="time-block">
                <!-- 根据来源决定样式：vessel 为绿色 -->
                <span :class="['time-main', record._source === 'vessel' ? 'time-vessel' : '']">
                  {{
                    record.departureStart ? dayjs(record.departureStart).format("HH:mm") : "--:--"
                  }}
                </span>
                <span class="time-sep" />
                <span class="date-small">
                  {{ record.departureStart ? dayjs(record.departureStart).format("DD-MM") : "-" }}
                </span>
              </div>

              <div class="route-block">
                <span class="place">
                  {{ record.originCity || record.originPort || "Unknown" }}
                  <span class="arrow">→</span>
                  {{ record.destinationCity || record.destinationPort || "Unknown" }}
                </span>
                <!-- 可选次要信息 -->
                <!-- <div class="cargo-small" v-if="record.cargoType">{{ record.cargoType }}</div> -->
              </div>
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
  align-items: center;         /* 垂直居中所有子项 */
  justify-content: space-between; /* 左右两端布局，右侧控件靠右 */
  padding: 4px 0;              /* 顶底间距，改小点使行更紧凑 */
  width: 95%;
  box-sizing: border-box;
}
.header-controls {
  display: flex;
  align-items: center;         /* 垂直居中复选框与文字 */
  gap: 6px;                    /* 两个复选框之间的间距（改为 4 或 2 更紧凑） */
  margin-left: 0;              /* 不再使用 margin-left:auto（使用 space-between 已推右） */
}
.header-controls ::v-deep .el-checkbox {
  margin: 0;                   /* 去掉默认的外边距 */
  padding: 0;                  /* 去掉内填充，保证紧凑 */
}
.header-controls ::v-deep .el-checkbox__label {
  display: inline-flex;
  align-items: center;
  line-height: 1;
  padding-left: 4px; /* label 与勾选框图标间距 */
  font-size: 12px;   /* 与标题字号一致（可根据需要改为12/14） */
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
  height: 300px;                     /* 最大高度 200px */
  overflow-y: auto;                      /* 超出时显示纵向滚动条 */
  -webkit-overflow-scrolling: touch;     /* iOS 平滑滚动 */
  padding-right: 6px;                    /* 留出滚动条空间，避免遮挡内容 */
}
/* 可选：轻微美化滚动条（WebKit 内核） */
.record-list::-webkit-scrollbar {
  width: 2px;
}

/* 卡片样式 */
.record-card {
  margin-bottom: 2px;
  font-size: 12px;
  border: 0.1px solid rgba(250, 250, 250, 0.788);
  border-radius: 5px;
  padding: 10px 10px;
  box-sizing: border-box;
  padding: 4px 8px;
  background: #ffffff88;
}

/* 每条记录一行的容器（两列：时间 / 路线） */
.record-line {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
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

/* 路线块（右）*/
.route-block {
  flex: 1 1 auto;
  display: flex;
  flex-direction: column;
  gap: 4px;
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
}

/* 货物类型为次要信息，字体较小且颜色浅 */
.cargo-small {
  color: #8c8c8c;
  font-size: 12px;
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
