<template>
  <el-card
    style="width: 100%; height: 300px; margin-top: 10px"
  >
    <div style="margin-bottom: 10px">
      <el-button-group>
        <el-button
          v-for="key in valuesList"
          :key="key"
          :type="currentKey === key ? 'primary' : 'default'"
          @click="renderChart(chartData[key], key)"
        >
          {{ labelMap[key] }}
        </el-button>
      </el-button-group>
    </div>
    <div ref="chartRef" style="width: 100%; height: 240px"></div>
  </el-card>
</template>

<script setup>
import axios from "axios";
import { ref, watch, onMounted } from "vue";
import * as echarts from "echarts";

const props = defineProps({
  data: Object, // 必须包含 locCode, locNaam
});
const currentData = computed(() => chartData.value[currentKey.value]);

// 时间段列表和对应标签
const valuesList = ["-6,0", "-48,0", "-216,0", "-672,0"];
const labelMap = {
  "-6,0": "–6 Hours",
  "-48,0": "–2 Days",
  "-216,0": "–11 Days",
  "-672,0": "	–28 Days",
};

const chartRef = ref(null);
let chartInstance = null;

const chartData = ref({
  "-6,0": null,
  "-48,0": null,
  "-216,0": null,
  "-672,0": null,
});
const currentKey = ref("-6,0"); // 默认2天

// 监听传入点位变化，自动请求所有时间段
watch(
  () => props.data,
  async () => {
    console.log("📡 接收到 props.data:", props.data); // ← 添加这行
    if (!props.data || !props.data.locCode) return;

    // 清空旧数据
    for (const key of valuesList) chartData.value[key] = null;

    const locCode = props.data.locCode;

    // 并行请求
    const requests = valuesList.map((values) =>
      axios
        .get("/api/waterinfo/api/chart/get", {
          params: {
            mapType: "watertemperatuur",
            locationCodes: props.data.locCode,
            values,
          },
          headers: {
            Accept: "application/json", // 👈 添加这个请求头
          },
        })
        .then((res) => {
          const key = values;
          const parsed = res.data;
          return { key, data: parsed };
        })
    );

    console.log("📡 props.data:", props.data);
    console.log("📊 chartData:", chartData.value);

    try {
      const results = await Promise.all(requests);
      results.forEach((result) => {
        chartData.value[result.key] = result.data;
      });
      // 默认渲染2天数据
      renderChart(chartData.value["-6,0"], "-6,0");
      currentKey.value = "-6,0";
    } catch (err) {
      console.error("获取水位图数据失败", err);
    }
  },
  { immediate: true, deep: true }
);

function renderChart(data, key = currentKey.value) {
  currentKey.value = key;

  if (!data?.series || !Array.isArray(data.series)) {
    console.warn("⚠️ 数据结构不合法，缺少 series 数组");
    return;
  }

  // 提取背景颜色区域信息
  const backgroundAreas = (data.limits || []).map((limit) => ({
    yAxis: [
      limit.from !== null ? limit.from : data.extremesY?.min ?? 0,
      limit.to !== null ? limit.to : data.extremesY?.max ?? 2000,
    ],
    itemStyle: {
      color: limit.softColor || "rgba(0,0,0,0.05)",
    },
    label: {
      show: false,
    },
  }));

  // 1️⃣ 合并所有时间点并对齐数据
  const timeValueMap = new Map(); // Map<timeStr, { real: value | null, pred: value | null }>

  for (const s of data.series) {
    if (!Array.isArray(s.data)) continue;
    for (const d of s.data) {
      const t = d.dateTime?.slice(0, 16); // 去掉秒
      if (!t) continue;

      if (!timeValueMap.has(t)) {
        timeValueMap.set(t, { real: null, pred: null });
      }
      const entry = timeValueMap.get(t);
      if (s.isPrediction) {
        entry.pred = d.value ?? null;
      } else {
        entry.real = d.value ?? null;
      }
    }
  }

  // 2️⃣ 排序时间点
  const sortedTimes = Array.from(timeValueMap.keys()).sort();
  const xAxisLabels = sortedTimes;

  // 3️⃣ 构建 Y 数据数组
  const realValues = [];
  const predValues = [];

  for (const t of sortedTimes) {
    const entry = timeValueMap.get(t);
    realValues.push(entry.real);
    predValues.push(entry.pred);
  }

  // 4️⃣ 构建 series
  const series = [
    {
      name: "Measured",
      type: "line",
      data: realValues,
      smooth: true,
      symbol: "circle",
      connectNulls: true,
      lineStyle: { color: "#409EFF" },
      itemStyle: { color: "#409EFF" },
      areaStyle: { opacity: 0.2 },
    },
  ];

  // ✅ 添加背景分段 markArea 到图例
  (data.limits || []).forEach((limit, idx) => {
    const from = limit.from !== null ? limit.from : data.extremesY?.min ?? 0;
    const to = limit.to !== null ? limit.to : data.extremesY?.max ?? 2000;
    const label = limit.label || `区间 ${idx + 1}`;
    const color = limit.softColor || "rgba(0,0,0,0.05)";

    series.push({
      name: label,
      type: "line",
      data: [],
      markArea: {
        silent: true,
        itemStyle: { color, opacity: 0.3 },
        data: [[{ yAxis: from }, { yAxis: to }]],
      },
    });
  });

  // 5️⃣ 初始化图表并设置配置项
  if (chartInstance && chartRef.value) chartInstance.dispose();
  chartInstance = echarts.init(chartRef.value);

  chartInstance.setOption({
    title: {
      text: `Water Temperature - ${props.data.locNaam || ""}`,
      left: "center",
    },
    tooltip: {
      trigger: "axis",
      formatter: (params) => {
        const rawTime = params[0].axisValue; // e.g., "2025-12-25 10:00"
        const date = new Date(rawTime.replace(" ", "T"));

        const day = String(date.getDate()).padStart(2, "0");
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const year = date.getFullYear();
        const hour = String(date.getHours()).padStart(2, "0");
        const minute = String(date.getMinutes()).padStart(2, "0");

        const timeStr = `${day}-${month}-${year} ${hour}:${minute}`;

        let content = `🕒 ${timeStr}<br/>`;
        params.forEach((item) => {
          content += `${item.seriesName}: ${item.data ?? "—"} cm<br/>`;
        });
        return content;
      },
    },
    xAxis: {
      type: "category",
      data: xAxisLabels,
      axisLabel: {
        rotate: 0,
        fontSize: 12,
        interval: Math.ceil(xAxisLabels.length / 9), // 每9个标签显示一次
        formatter: function (value) {
          // 假设 value 是 "2025-08-07 12:00"
          const date = new Date(value.replace(" ", "T")); // 转换为 Date 对象
          const day = date.getDate();
          const month = date.toLocaleString("en-US", { month: "short" }); // "Aug"
          const hour = String(date.getHours()).padStart(2, "0");
          const minute = String(date.getMinutes()).padStart(2, "0");
          return `${day}.${month} ${hour}:${minute}`;
        },
      },
    },
    yAxis: {
      type: "value",
      name: "( °C )",
      min: data.extremesY?.min ?? "auto",
      max: data.extremesY?.max ?? "auto",
      axisLabel: {
        fontSize: 10,
      },
    },
    series,
    grid: {
      left: "5%",
      right: "5%",
      bottom: "15%",
      top: "15%",
    },
    markArea: {
      silent: true,
      itemStyle: {
        opacity: 0.25,
      },
      data: backgroundAreas,
    },
  });
}
</script>
