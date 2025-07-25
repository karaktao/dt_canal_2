<template>
  <el-card style="width: 100%; height: 300px; margin-top: 10px">
    <div ref="chartRef" style="width: 100%; height: 260px"></div>
  </el-card>
</template>

<script setup>
import * as echarts from "echarts";
import { ref, watch, onMounted } from "vue";

const props = defineProps({
  data: Object, // 包含 locCode, locNaam, data（measurementData）
});

const chartRef = ref(null);
let chartInstance = null;

watch(() => props.data, renderChart, { deep: true });

onMounted(() => {
  chartInstance = echarts.init(chartRef.value);
  renderChart();
});

function renderChart() {
  if (!props.data?.data?.WaarnemingenLijst?.[0]?.MetingenLijst) return;

  const measurements = props.data.data.WaarnemingenLijst[0].MetingenLijst;

  const times = measurements.map((m) => m.Tijdstip);
  const values = measurements.map((m) => m.Meetwaarde.Waarde_Numeriek);

  const option = {
    title: {
      text: `Water Level - ${props.data.locNaam}`,
      left: "center",
    },
    tooltip: {
      trigger: "axis",
      formatter: (params) => {
        const data = params[0];
        const date = new Date(data.axisValue);
        const h = String(date.getHours()).padStart(2, "0");
        const m = String(date.getMinutes()).padStart(2, "0");
        return `🕒 ${h}:${m}<br/>🌊 ${data.data} cm`;
      },
    },
    xAxis: {
      type: "category",
      data: times,
      axisLabel: {
        rotate: 45,
        formatter: (value) => {
          // value 是字符串格式的时间：2025-07-24T06:20:00.000+01:00
          const date = new Date(value);
          const hours = String(date.getHours()).padStart(2, "0");
          const minutes = String(date.getMinutes()).padStart(2, "0");
          return `${hours}:${minutes}`;
        },
      },
    },
    yAxis: {
      type: "value",
      name: "cm",
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
  };

  chartInstance.setOption(option);
}
</script>