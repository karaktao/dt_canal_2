<template>
  <el-card style="width: 100%; height: 300px; margin-top: 10px">
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

// 时间段列表和对应标签
const valuesList = ["-6,3", "-48,48", "-216,0", "-672,0"];
const labelMap = {
  "-6,3": "±6小时",
  "-48,48": "±2天",
  "-216,0": "±11天",
  "-672,0": "28天",
};

const chartRef = ref(null);
let chartInstance = null;

const chartData = ref({
  "-6,3": null,
  "-48,48": null,
  "-216,0": null,
  "-672,0": null,
});
const currentKey = ref("-48,48"); // 默认2天

// 监听传入点位变化，自动请求所有时间段
watch(
  () => props.data,
  async () => {
     console.log("📡 接收到 props.data:", props.data);  // ← 添加这行
    if (!props.data || !props.data.locCode) return;

    // 清空旧数据
    for (const key of valuesList) chartData.value[key] = null;

    const locCode = props.data.locCode;

    // 并行请求
 const requests = valuesList.map((values) =>
  axios
    .get("/api/waterinfo/api/chart/get", {
      params: {
        mapType: "waterhoogte",
        locationCodes: props.data.locCode,  
        values,
      },
    })
    .then((res) => ({ key: values, data: res.data }))
);

    try {
      const results = await Promise.all(requests);
      results.forEach((result) => {
        chartData.value[result.key] = result.data;
      });
      // 默认渲染2天数据
      renderChart(chartData.value["-48,48"], "-48,48");
      currentKey.value = "-48,48";
    } catch (err) {
      console.error("获取水位图数据失败", err);
    }
  },
  { immediate: true, deep: true }
);

function renderChart(data, key = currentKey.value) {
  currentKey.value = key;
  if (!data?.WaarnemingenLijst?.[0]?.MetingenLijst) return;

  const measurements = data.WaarnemingenLijst[0].MetingenLijst;
  const times = measurements.map((m) => m.Tijdstip);
  const values = measurements.map((m) => m.Meetwaarde?.Waarde_Numeriek ?? m.Waarde ?? null);

  if (chartInstance && chartRef.value) {
    chartInstance.dispose();
  }
  chartInstance = echarts.init(chartRef.value);

  chartInstance.setOption({
    title: {
      text: `Water Level - ${props.data.locNaam || ""}`,
      left: "center",
    },
    tooltip: {
      trigger: "axis",
      formatter: (params) => {
        const data = params[0];
        const time = data.axisValue;
        return `🕒 ${time}<br/>🌊 ${data.data} cm`;
      },
    },
    xAxis: {
      type: "category",
      data: times.map((t) => t.substring(0, 16)), // 如果你想显示完整日期时间
      axisLabel: {
        rotate: 45,
      },
    },
    yAxis: {
      type: "value",
      name: "水位 (cm)",
    },
    series: [
      {
        data: values,
        type: "line",
        areaStyle: {},
        smooth: true,
        symbol: "circle",
        color: "#409EFF",
      },
    ],
  });
}

onMounted(() => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value);
  }
});
</script>
