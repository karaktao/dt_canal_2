<script setup>
// 放在 script 顶部（与其他 const/let 一起）
let _debounceTimer = null;
const DEBOUNCE_MS = 450; // 防抖时间，可调整

// 地图输入
import MapCard from "@/components/MapCard.vue";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import LineString from "ol/geom/LineString";
import Feature from "ol/Feature";
import { buffer as bufferExtent } from "ol/extent";
import View from "ol/View";
import Point from "ol/geom/Point";
import { fromLonLat } from "ol/proj";
import { Style, Fill, Stroke, Circle as CircleStyle } from "ol/style";
import { recordsByDate } from "@/assets/data/recordsByDate";
import { ElMessage } from "element-plus";
import dayjs from "dayjs";
import proj4 from "proj4";
import { register } from "ol/proj/proj4";

import enLocale from "element-plus/es/locale/lang/en";
const enLocaleRef = ref(enLocale);

import {
  listPublish,
  getPublish,
  delPublish,
  addPublish,
  updatePublish,
} from "@/api/transport/publish";
import {
  listBerth,
  getBerth,
  delBerth,
  addBerth,
  updateBerth,
} from "@/api/infrastructure/berth";

// 功能输入
import { nextTick, ref, watch, onMounted, computed } from "vue";

function cancel() {
  open.value = false;
}

// 注册EPSG:28992坐标系
proj4.defs(
  "EPSG:28992",
  "+proj=sterea +lat_0=52.1561605555556 +lon_0=5.38763888888889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +towgs84=593.16,26.15,478.54,-6.3239,-0.5008,-5.5487,4.0775 +units=m +no_defs +type=crs"
);
register(proj4);

const mapCard = ref(null);

const form = ref({
  assignmentType: "cargo_to_vessel",
  originPort: "",
  destinationPort: "",
  departureStart: "",
  departureEnd: "",
  arrivalStart: "",
  arrivalEnd: "",
  uploadTime: "",
  unloadTime: "",
  intermediatePorts: "",
  geoPath: null,
  vesselName: "",
  mmsiNumber: "",
  cargoType: "",
  tonnageDemand: null,
  containerDemand: null,
  tonnageAvailable: null,
  containerAvailable: null,
  containerId: "",
  capacityValue: null,
  capacityUnit: "",
  isEmptyVessel: false,
  isMerge: false,
  isTransshipment: false,
  returnDestinationPort: "",
  originPortId: null,
  destinationPortId: null,
  originCity: "",
  destinationCity: "",
  publishedAt: "",
  publishedBy: "",
  createdAt: "",
});

const containerTypeMap = {
  1: "20ft Standard",
  2: "40ft High Cube",
  3: "Reefer",
};
// 把 map 转成数组，方便 v-for 用
const containerTypeOptions = Object.entries(containerTypeMap).map(
  ([value, label]) => ({ value: +value, label })
);

// ✅ 新增校验相关，
const formRef = ref();
const rules = {
  originPort: [
    { required: true, message: "Origin Port is required", trigger: "blur" },
  ],
  destinationPort: [
    {
      required: true,
      message: "Destination Port is required",
      trigger: "blur",
    },
  ],
  departureStart: [
    {
      required: true,
      message: "Departure Start Time is required",
      trigger: "change",
    },
  ],
  arrivalStart: [
    {
      required: true,
      message: "Arrival Start Time is required",
      trigger: "change",
    },
  ],
  vesselName: [
    { required: true, message: "Vessel Name is required", trigger: "blur" },
  ],
  containerId: [
    {
      trigger: "change",
      validator: (_, value, callback) => {
        if (form.value.capacityUnit === "container" && !value) {
          callback(
            new Error("Container Type is required when unit is container")
          );
        } else {
          callback();
        }
      },
    },
  ],
};
// ✅ berth数据
const berthOptions = ref([]);

const loadBerths = async () => {
  const res = await listBerth(); // 调接口拿所有港口
  berthOptions.value = res.rows || []; // 存到状态里
};

// ✅ 搜索候选项
const querySearchBerths = async (queryString, cb) => {
  if (!queryString) {
    cb([]); // 为空就不显示
    return;
  }
  try {
    // 调接口把 name 传给后端，后端做 LIKE、分页
    const res = await listBerth({
      name: queryString,
      pageNum: 1,
      pageSize: 1000, // 或者后端支持 pageSize=0 => 全部
    });
    const rows = res.rows || [];
    cb(
      rows.map((item) => ({
        value: item.name, // 对应数据库 name
        berthIsrs: item.isrs,
        berthLatitude: item.latitude,
        berthLongitude: item.longitude,
      }))
    );
  } catch (err) {
    console.error("berth search error", err);
    cb([]);
  }
};

// ✅ 点击选择港口
const handleOriginPortSelect = (selected) => {
  form.value.originPort = selected.value;
  form.value.originPortId = selected.berthIsrs;
  form.value.originLat = selected.berthLatitude;
  form.value.originLon = selected.berthLongitude;
  form.value.originCity = selected.city || "";
};
// 选中 Destination Port 时把值和对应 ISRS/坐标写回表单
const handleDestinationPortSelect = (selected) => {
  form.value.destinationPort = selected.value;
  form.value.destinationPortId = selected.berthIsrs;
  form.value.destLat = selected.berthLatitude;
  form.value.destLon = selected.berthLongitude;
  form.value.destinationCity = selected.city || "";
};

// 选中 Return Destination 时
const handleReturnDestinationPortSelect = (selected) => {
  form.value.returnDestinationPort = selected.value;
  form.value.returnDestinationPortId = selected.berthIsrs;
  form.value.returnLat = selected.berthLatitude;
  form.value.returnLon = selected.berthLongitude;
};

// 选中 Intermediate Ports 时（如果允许多选，你可以把它累加到数组里）
const handleIntermediatePortsSelect = (selected) => {
  form.value.intermediatePorts = selected.value;
  form.value.intermediatePortsId = selected.berthIsrs;
  form.value.intermediateLat = selected.berthLatitude;
  form.value.intermediateLon = selected.berthLongitude;
};

// ========== 增加 RouteLayer ==========

// 创建导航风格样式
const routeStyle = new Style({
  stroke: new Stroke({
    color: "rgba(0, 123, 255, 0.8)", // 主体蓝色，带透明度
    width: 5,
    lineCap: "round",
  }),
});

// 白色虚线（中心线）
const dashedCenterLineStyle = new Style({
  stroke: new Stroke({
    color: "rgba(255, 255, 255, 0.9)", // 白色中心线，稍带透明
    width: 1,
    lineDash: [30, 10, 5, 10],
    lineCap: "round",
  }),
});

// 创建路线图层
const routeLayer = new VectorLayer({
  source: new VectorSource(),
  style: function (feature) {
    return [routeStyle, dashedCenterLineStyle]; // 外边+内边，形成导航路线视觉
  },
});

// Polyline 解码函数（输出为 [lat, lng]）
function decodePolyline(str) {
  let index = 0,
    lat = 0,
    lng = 0,
    coordinates = [];

  while (index < str.length) {
    let b,
      shift = 0,
      result = 0;

    do {
      b = str.charCodeAt(index++) - 63;
      result |= (b & 0x1f) << shift;
      shift += 5;
    } while (b >= 0x20);
    const dlat = result & 1 ? ~(result >> 1) : result >> 1;
    lat += dlat;

    shift = 0;
    result = 0;
    do {
      b = str.charCodeAt(index++) - 63;
      result |= (b & 0x1f) << shift;
      shift += 5;
    } while (b >= 0x20);
    const dlng = result & 1 ? ~(result >> 1) : result >> 1;
    lng += dlng;

    coordinates.push([lat / 1e6, lng / 1e6]); // [lat, lng]
  }
  return coordinates;
}

// rd转换为墨卡托并添加图层
function addEurisPaths(paths) {
  const source = routeLayer.getSource();
  // 不清空（如果需要每次清空请在调用处 clear），这里我们先 clear（和上游逻辑一致）
  source.clear();

  for (const encoded of paths) {
    if (!encoded || !encoded.trim()) continue;
    try {
      const decoded = decodePolyline(encoded); // 你的 decode 函数，返回 [lat, lon] 对数组
      // decoded 里可能是 [lat, lon]（你已有实现是 lat then lon），确保投影顺序正确
      const projected = decoded.map(([lat, lon]) => fromLonLat([lon, lat]));
      const feat = new Feature({
        geometry: new LineString(projected),
      });
      source.addFeature(feat);
    } catch (e) {
      console.warn("[Route] polyline 解码失败，跳过：", encoded, e);
    }
  }

  // fit map view 到 routes（保留你原有 fit 流程）
  const map = mapCard.value?.map;
  if (!map) {
    console.warn("[Route] 未获取到 map 实例，无法 fit");
    return;
  }
  map.updateSize();
  map.renderSync();

  let extent = source.getExtent();
  // 若 extent 无效或单点则扩展
  if (!extent || extent.some((c) => !Number.isFinite(c))) {
    console.warn("[Route] extent 无效，跳过 fit");
    return;
  }
  if (extent[0] === extent[2] && extent[1] === extent[3]) {
    // 单点 -> buffer
    extent = bufferExtent(extent, 10000);
  }
  const size = map.getSize();
  if (Array.isArray(size)) {
    map.getView().fit(extent, {
      padding: [80, 80, 80, 80],
      size,
      duration: 400,
    });
  }
}



// —— helper: 构建请求 body（使用用户给定模板） ——
function buildEurisRequestBody(startIsrs, endIsrs, priority = "SHORTEST") {
  // DepartAt 使用表单的 departureStart（ISO），若没有则使用当前时间 ISO
  const departAtISO = form.value.departureStart
    ? dayjs(form.value.departureStart).toISOString()
    : dayjs().toISOString();

  return {
    StartISRS: startIsrs,
    EndISRS: endIsrs,
    // ViaPoints: [], // 如需插入中途 ISRS，可扩展
    ShipCategory: 0,
    ShipDimensions: {
      Height: 4.5,
      Width: 11,
      Draught: 3,
      Length: 111,
      CEMT: "IV",
    },
    ShipSpeed: 0,
    DepartAt: departAtISO,
    CalculationOptions: {
      ComputationType: priority, // 动态
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

// —— 防抖：避免短时间内频繁请求（简单实现） ——
let _routeDebounceTimer = null;
function debounceFetchRoute(startIsrs, endIsrs, priority) {
  if (_routeDebounceTimer) clearTimeout(_routeDebounceTimer);
  _routeDebounceTimer = setTimeout(() => {
    fetchAndDisplayRoute(startIsrs, endIsrs, priority);
  }, 400); // 400ms debounce，可按需调整
}


async function fetchAndDisplayRoute(startIsrs, endIsrs, priority = null) {
  if (!startIsrs || !endIsrs) return;
  // 清空以前的路线
  routeLayer.getSource().clear();

  // 若未传入 priority，则读取表单的（可能为 undefined）
  const computationType = priority || form.value.priority || "SHORTEST";

  const body = buildEurisRequestBody(startIsrs, endIsrs, computationType);

  try {
    // 注意：你之前的示例中 endpoint 有两个版本，这里按你要求使用 /Calculate
    const url = "https://www.eurisportal.eu/api/RouteCalculatorV2/Calculate";

    const res = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    });

    if (!res.ok) {
      const text = await res.text();
      console.error("[Route] HTTP error:", res.status, text);
      ElMessage.error(`路由计算接口返回错误：${res.status}`);
      return;
    }

    const data = await res.json();
    // 成功标志由 API 返回字段决定（示例中有 Success:true）
    if (!data || data.Success === false) {
      console.error("[Route] API returned failure:", data);
      ElMessage.error("路由计算返回失败（请检查请求参数或优先级设置）");
      return;
    }

    // 解析路径：优先选择 Itineraries[0].Geometry.paths（示例里存在）
    let paths = [];
    if (Array.isArray(data.Itineraries) && data.Itineraries.length > 0) {
      const itin = data.Itineraries[0];
      const g = itin.Geometry;
      if (g && Array.isArray(g.paths) && g.paths.length > 0) {
        paths = g.paths.slice(); // 复制
      } else {
        // fallback: 合并 legs -> segments 的 CompressedGeometry（可能更细粒度）
        const legs = itin.Legs || [];
        for (const leg of legs) {
          if (Array.isArray(leg.Segments)) {
            for (const seg of leg.Segments) {
              if (seg.CompressedGeometry) paths.push(seg.CompressedGeometry);
            }
          }
        }
      }
    } else {
      // 更下层 fallback：尝试直接从 data.Itineraries（若结构不同）
      console.warn("[Route] Itineraries empty, raw data:", data);
    }

    // 过滤空串并去重（有些结果含 "" 分隔）
    paths = (paths || []).filter((p) => p && p.trim() !== "");

    if (!paths.length) {
      console.warn("[Route] 没有可用的路径字符串，raw response:", data);
      ElMessage.warning("No stackable paths returned");
      return;
    }

    // 调用已有的路径绘制函数（你原来的 addEurisPaths），但确保它在 scope 中可用
    addEurisPaths(paths);



  } catch (err) {
    console.error("[Route] exception", err);
    ElMessage.error("Route calculation failure (network or interface anomaly)");
  }
}

// 添加 watch，监听 originPortId 和 destinationPortId
watch(
  () => [form.value.originPortId, form.value.destinationPortId, form.value.priority],
  ([start, end, priority]) => {
    if (start && end) {
      // 使用防抖版本
      debounceFetchRoute(start, end, priority);
    } else {
      // 若任一为空则清除图层
      routeLayer.getSource().clear();
    }
  }
);

// 新增：点击记录时隐藏表单并绘制线路
function onRecordClick(record) {
  showForm.value = false;
  // 改用防抖 + 带当前 priority 的调用（避免旧请求覆盖新请求）
  debounceFetchRoute(record.originPortId, record.destinationPortId, form.value.priority);
}

// 定义一个方法，用来拉最新的物流列表
const reloadRecords = async () => {
  try {
    const res = await listPublish({
      assignmentType: "cargo_to_vessel", pageSize: 1000
    });
    if (res.code === 200) {
      rawRecords.value = res.rows;
    } else {
      console.error("接口返回异常", res);
    }
  } catch (e) {
    console.error("拉取失败", e);
  }
};

// ✅ 从后端接口拉的物流列表数据

const rawRecords = ref([]); // 原始数据

const recordsByDepartureStart = computed(() => {
  return rawRecords.value
    .filter((item) => item.assignmentType === "cargo_to_vessel")
    .reduce(
      (groups, item) => {
        const d = dayjs(item.departureStart).format("YYYY-MM-DD");
        (groups[d] ||= []).push(item);
        return groups;
      },
      /** 初始值 */ {}
    );
});

onMounted(async () => {
  try {
    await loadBerths();
    await reloadRecords();
  } catch (e) {
    console.error("加载失败", e);
  }
});

// 弹窗和表单状态
const { proxy } = getCurrentInstance();
const open = ref(false);
const title = ref("");
const recordForm = ref({}); // ← 改名
const recordFormRef = ref(null); // 对应 <el-form ref="recordFormRef">

function reset() {
  // 把旧数据清空到初始值
  recordForm.value = {
    /* …所有字段的初始值… */
  };
  // 再清除 Element Plus 校验提示
  recordFormRef.value?.clearValidate();
  recordFormRef.value?.resetFields();
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value;

  getPublish(_id).then((response) => {
    // 先打印 response 本身
    console.log("🎉 getPublish 返回 response =", response);

    // 如果你拦截器剥了外层 AjaxResult，这里 response 就是 record 对象
    // 如果没有剥，就可能需要 response.data
    console.log("🎉 getPublish._data_ =", response.data);

    form.value = response.data;
    open.value = true;
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal
    .confirm("Are you sure you want to delete the record？")
    .then(function () {
      return delPublish(_ids);
    })
    .then(() => {
      proxy.$modal.msgSuccess("Deleted successfully");
      reloadRecords(); // ✅ 拉取最新数据
    })
    .catch(() => {});
}

/** 提交编辑/新增表单 */
async function submitRecordForm() {
  if (recordForm.value.id) {
    await updatePublish(recordForm.value);
    ElMessage.success("修改成功");
  } else {
    await addPublish(recordForm.value);
    ElMessage.success("新增成功");
  }
  open.value = false;
  getList();
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加物流发布";
}

// 控制表单显示
const showForm = ref(false);

// 表单数据模型
function resetFormFields() {
  form.value = {
    assignmentType: "cargo_to_vessel",
    originPort: "",
    destinationPort: "",
    departureStart: "",
    departureEnd: "",
    arrivalStart: "",
    arrivalEnd: "",
    uploadTime: "",
    unloadTime: "",
    intermediatePorts: "",
    geoPath: null,
    vesselName: "",
    mmsiNumber: "",
    cargoType: "",
    tonnageDemand: null,
    containerDemand: null,
    tonnageAvailable: null,
    containerAvailable: null,
    containerId: "",
    capacityValue: null,
    capacityUnit: "",
    isEmptyVessel: "0",
    isReturnTripAvailable: "0",
    isMerge: "0",
    isTransshipment: "0",
    vesselAvailabilityStart: "",
    vesselAvailabilityEnd: "",
    returnDestinationPort: "",
  };
  // 清除校验提示
  formRef.value?.clearValidate();
}

// ✅ 日期格式化函数
function formatDate(dateStr) {
  if (!dateStr) return "-";
  return dayjs(dateStr).format("DD-MM-YYYY HH:mm:ss");
}

// ✅ 加入校验,集装箱单位
function mapCapacityFields() {
  if (form.value.capacityUnit === "ton") {
    form.value.tonnageDemand = form.value.capacityValue;
    form.value.containerDemand = null;
    form.value.containerId = "";
  } else if (form.value.capacityUnit === "container") {
    form.value.containerDemand = form.value.capacityValue;
    form.value.tonnageDemand = null;
  }
}

function convertBooleansToIntegers() {
  form.value.isEmptyVessel = form.value.isEmptyVessel ? 1 : 0;
  form.value.isReturnTripAvailable = form.value.isReturnTripAvailable ? 1 : 0;
}

// ✅ 修改提交逻辑：加入校验
function submitForm() {
  formRef.value.validate((valid) => {
    if (valid) {
      mapCapacityFields(); // 映射 tonnage/container 字段
      convertBooleansToIntegers(); // ✅ 添加布尔转整数

      // ✅ 提交到后端
      addPublish(form.value)
        .then(() => {
          ElMessage.success("Form submitted successfully!");
          resetFormFields(); // ✅ 清空表单
          reloadRecords(); // ✅ 拉取最新数据
        })
        .catch(() => {
          ElMessage.error("Submission failed!");
        });
    } else {
      console.log("❌ 表单验证失败");
    }
  });
}
</script>

<template>
  <el-row class="publish-company-container" :gutter="20" style="margin:0; height:100%">
    <!-- // ------------------------- 物流输入框------------------------- -->
    <!-- 地图输入卡片 -->
    <el-col :span="16" class="map" >
      <MapCard ref="mapCard" :extraLayers="[routeLayer]" class="map-card"  style="flex:1; display:flex; flex-direction:column;"/>

      <!-- 悬浮输入表单 -->
      <div class="form-float-panel">
        <!-- Publish 按钮始终显示 -->
        <el-button size="small" @click="showForm = !showForm" type="primary">
          {{ showForm ? "Hide Form" : "Publish Demand" }}
        </el-button>

        <!-- 表单仅在 showForm 为 true 时显示 -->
        <el-card v-if="showForm" class="map-card form-panel" shadow="always">
          <el-form
            :model="form"
            :rules="rules"
            ref="formRef"
            label-position="top"
            class="compact-form"
          >
            <!-- 行 1：起运港口 / 目的港口 -->
            <el-row :gutter="10">
              <el-col :span="12">
                <el-form-item label="Origin Port" prop="originPort" required>
                  <el-autocomplete
                    v-model="form.originPort"
                    :fetch-suggestions="querySearchBerths"
                    placeholder="Enter port name"
                    @select="handleOriginPortSelect"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item
                  label="Destination Port"
                  prop="destinationPort"
                  required
                >
                  <el-autocomplete
                    v-model="form.destinationPort"
                    :fetch-suggestions="querySearchBerths"
                    placeholder="Enter port name"
                    @select="handleDestinationPortSelect"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 行 2：起运时间 / 到达时间 -->
            <el-row :gutter="10">
              <el-col :span="12">
                <el-form-item
                  label="Departure Window"
                  prop="departureStart"
                  required
                >
                  <el-config-provider :locale="enLocale">
                    <el-date-picker
                      v-model="form.departureStart"
                      type="datetime"
                      format="HH:mm DD-MM"
                      value-format="YYYY-MM-DD HH:mm:ss"
                      placeholder="Start"
                    />
                  </el-config-provider>
                </el-form-item>
                <el-form-item>
                  <el-config-provider :locale="enLocale">
                    <el-date-picker
                      v-model="form.departureEnd"
                      type="datetime"
                      format="HH:mm DD-MM"
                      value-format="YYYY-MM-DD HH:mm:ss"
                      placeholder="End"
                    />
                  </el-config-provider>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item
                  label="Arrival Window"
                  prop="arrivalStart"
                  required
                >
                  <el-config-provider :locale="enLocale">
                    <el-date-picker
                      v-model="form.arrivalStart"
                      type="datetime"
                      format="HH:mm DD-MM"
                      value-format="YYYY-MM-DD HH:mm:ss"
                      placeholder="Start"
                    />
                  </el-config-provider>
                </el-form-item>
                <el-form-item>
                  <el-config-provider :locale="enLocale">
                    <el-date-picker
                      v-model="form.arrivalEnd"
                      type="datetime"
                      format="HH:mm DD-MM"
                      value-format="YYYY-MM-DD HH:mm:ss"
                      placeholder="End"
                    />
                  </el-config-provider>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 行 3：装货时间 / 卸货时间 -->
            <el-row :gutter="10">
              <el-col :span="12">
                <el-form-item label="Upload Time">
                  <el-config-provider :locale="enLocale">
                    <el-date-picker
                      v-model="form.uploadTime"
                      type="datetime"
                      format="HH:mm DD-MM"
                      value-format="YYYY-MM-DD HH:mm:ss"
                    />
                  </el-config-provider>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Unload Time">
                  <el-config-provider :locale="enLocale">
                    <el-date-picker
                      v-model="form.unloadTime"
                      type="datetime"
                      format="HH:mm DD-MM"
                      value-format="YYYY-MM-DD HH:mm:ss"
                    />
                  </el-config-provider>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 行 4：中途港口 / 路径 -->
            <el-row :gutter="10">
              <el-col :span="12">
                <el-form-item label="Intermediate Ports">
                  <el-autocomplete
                    v-model="form.intermediatePorts"
                    :fetch-suggestions="querySearchBerths"
                    placeholder="Enter port name"
                    @select="handleIntermediatePortsSelect"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Geo Path">
                  <el-input
                    v-model="form.geoPath"
                    placeholder="Choose on the map"
                  />
                </el-form-item>
              </el-col>

              <!-- 行 ：货物种类 -->
            </el-row>
            <el-row :gutter="10">
              <el-col :span="12">
                <el-form-item label="Cargo Type">
                  <el-input v-model="form.cargoType" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Priority">
                  <el-select v-model="form.priority" placeholder="Select">
                    <el-option label="Shortest" value="SHORTEST" />
                    <el-option label="Fastest " value="FASTEST " />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 行 6：是否空船 / 可用载重和箱位 -->
            <el-row :gutter="10">
              <el-col :span="24">
                <el-form-item label="Capacity ">
                  <el-input
                    v-model="form.capacityValue"
                    placeholder="Enter value"
                    type="number"
                  >
                    <template #append>
                      <el-select
                        v-model="form.capacityUnit"
                        placeholder="Select Unit"
                        style="width: 100px"
                      >
                        <el-option label="Ton" value="ton" />
                        <el-option label="Container" value="container" />
                      </el-select>
                    </template>
                  </el-input>
                </el-form-item>
                <el-form-item
                  label="Container Type"
                  v-if="form.capacityUnit === 'container'"
                  prop="containerId"
                >
                  <el-select
                    v-model="form.containerId"
                    placeholder="Select Container Type"
                  >
                    <el-option
                      v-for="opt in containerTypeOptions"
                      :key="opt.value"
                      :label="opt.label"
                      :value="opt.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 行 7：返程信息 -->
            <el-row :gutter="10">
              <el-col :span="12">
                <el-form-item label="Allow Merge?">
                  <el-switch
                    v-model="form.isMerge"
                    :active-value="1"
                    :inactive-value="0"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Allow Transshipment?">
                  <el-switch
                    v-model="form.isTransshipment"
                    :active-value="1"
                    :inactive-value="0"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 提交按钮 -->
            <el-form-item>
              <el-button type="primary" @click="submitForm">Submit</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </el-col>

    <!-- // ------------------------- 物流显示框------------------------- -->

    <el-col :span="8" class="info-panel">
      <div
        v-for="(records, date) in recordsByDepartureStart"
        :key="date"
        class="daily-section"
      >
        <h3>{{ dayjs(date).format("DD-MM-YYYY" || date) }}</h3>
        <el-card
          v-for="record in records"
          :key="record.id"
          class="record-card"
          shadow="hover"
        >
          <el-collapse :style="{ marginTop: '-5px', marginBottom: '-10px' }">
            <el-collapse-item>
              <template #title>
                <div
                  style="
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    width: 100%;
                  "
                  @click.stop="onRecordClick(record)"
                >
                  <!-- 左侧：时间 + 路线 -->
                  <div style="display: flex; align-items: center">
                    <span style="color: #409eff; font-weight: bold">
                      {{
                        dayjs(record.departureStart).format("HH:mm") || "--:--"
                      }}
                    </span>
                    <span style="margin-left: 12px">
                      {{ record.originCity || "Unknown" }} →
                      {{ record.destinationCity || "Unknown" }}
                    </span>
                  </div>
                  <!-- 右侧：编辑 / 删除 -->
                  <div style="display: flex; align-items: center">
                    <el-button
                      link
                      type="primary"
                      icon="Delete"
                      @click.stop="handleDelete(record)"
                      v-hasPermi="['transport:publish:remove']"
                    />

                    <el-button
                      link
                      type="primary"
                      icon="Edit"
                      size="small"
                      @click.stop="handleUpdate(record)"
                      v-hasPermi="['transport:publish:edit']"
                    />
                  </div>
                </div>
              </template>

              <!-- 第一行 -->
              <el-row :gutter="20" class="mb-2">
                <el-col :span="24">Origin Port: {{ record.originPort }}</el-col>
              </el-row>
              <!-- 第二行 -->
              <el-row :gutter="20" class="mb-2">
                <el-col :span="24"
                  >Destination Port: {{ record.destinationPort }}</el-col
                >
              </el-row>

              <!-- 第三行 -->
              <el-row :gutter="20">
                <el-col :span="24"
                  >Cargo Available: {{ record.cargoType || "-" }}</el-col
                >
              </el-row>

              <!-- 第四行 -->
              <el-row :gutter="20" class="mb-2">
                <el-col :span="12"
                  >Departure Time:
                  {{
                    dayjs(record.departureStart).format("HH:mm DD-MM") || "-"
                  }}</el-col
                >
                <el-col :span="12"
                  >Unload Time:
                  {{
                    dayjs(record.unloadTime).format("HH:mm DD-MM") || "-"
                  }}</el-col
                >
              </el-row>

              <!-- 第五行 -->
              <el-row :gutter="20">
                <el-col :span="24">
                  Capacity Available :
                  <span v-if="record.tonnageDemand">
                    {{ record.tonnageAvailable }} Ton
                  </span>
                  <span v-else-if="record.containerAvailable">
                    {{ record.containerAvailable }} ×
                    {{ containerTypeMap[record.containerId] || "Container" }}
                  </span>
                  <span v-else>-</span>
                </el-col>
              </el-row>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </div>
    </el-col>

    <!-- 添加或修改物流发布对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="1000px"
      append-to-body
      class="publish-dialog"
    >
      <template #header>
        <span style="font-weight: 700">{{
          "Modify Logistics Information"
        }}</span>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="150px"
        label-position="top"
        class="publish-form"
      >
        <!-- 行 1：起运港口 / 目的港口 -->
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="Origin Port" prop="originPort" required>
              <el-autocomplete
                v-model="form.originPort"
                :fetch-suggestions="querySearchBerths"
                placeholder="Enter port name"
                @select="handleOriginPortSelect"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              label="Destination Port"
              prop="destinationPort"
              required
            >
              <el-autocomplete
                v-model="form.destinationPort"
                :fetch-suggestions="querySearchBerths"
                placeholder="Enter port name"
                @select="handleDestinationPortSelect"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 行 2：起运时间 / 到达时间 -->
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item
              label="Departure Window"
              prop="departureStart"
              required
            >
              <el-config-provider :locale="enLocale">
                <el-date-picker
                  v-model="form.departureStart"
                  type="datetime"
                  format="HH:mm DD-MM"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="Start"
                />
              </el-config-provider>
            </el-form-item>
            <el-form-item>
              <el-config-provider :locale="enLocale">
                <el-date-picker
                  v-model="form.departureEnd"
                  type="datetime"
                  format="HH:mm DD-MM"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="End"
                />
              </el-config-provider>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Arrival Window" prop="arrivalStart" required>
              <el-config-provider :locale="enLocale">
                <el-date-picker
                  v-model="form.arrivalStart"
                  type="datetime"
                  format="HH:mm DD-MM"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="Start"
                />
              </el-config-provider>
            </el-form-item>
            <el-form-item>
              <el-config-provider :locale="enLocale">
                <el-date-picker
                  v-model="form.arrivalEnd"
                  type="datetime"
                  format="HH:mm DD-MM"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="End"
                />
              </el-config-provider>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 行 3：装货时间 / 卸货时间 -->
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="Upload Time">
              <el-config-provider :locale="enLocale">
                <el-date-picker
                  v-model="form.uploadTime"
                  type="datetime"
                  format="HH:mm DD-MM"
                  value-format="YYYY-MM-DD HH:mm:ss"
                />
              </el-config-provider>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Unload Time">
              <el-config-provider :locale="enLocale">
                <el-date-picker
                  v-model="form.unloadTime"
                  type="datetime"
                  format="HH:mm DD-MM"
                  value-format="YYYY-MM-DD HH:mm:ss"
                />
              </el-config-provider>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 行 4：中途港口 / 路径 -->

        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="Intermediate Ports">
              <el-autocomplete
                v-model="form.intermediatePorts"
                :fetch-suggestions="querySearchBerths"
                placeholder="Enter port name"
                @select="handleIntermediatePortsSelect"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Geo Path">
              <el-input
                v-model="form.geoPath"
                placeholder="Choose on the map"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 行 ：货物种类 -->
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="Cargo Type">
              <el-input v-model="form.cargoType" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Priority">
              <el-select v-model="form.priority" placeholder="Select">
                <el-option label="Shortest" value="SHORTEST" />
                <el-option label="Fastest" value="FASTEST" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 行 6：是否空船 / 可用载重和箱位 -->
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="Capacity ">
              <el-input
                v-model="form.capacityValue"
                placeholder="Enter value"
                type="number"
              >
                <template #append>
                  <el-select
                    v-model="form.capacityUnit"
                    placeholder="Select Unit"
                    style="width: 100px"
                  >
                    <el-option label="Ton" value="ton" />
                    <el-option label="Container" value="container" />
                  </el-select>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              label="Container Type"
              v-if="form.capacityUnit === 'container'"
              prop="containerId"
            >
              <el-select
                v-model="form.containerId"
                placeholder="Select Container Type"
              >
                <el-option
                  v-for="opt in containerTypeOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 行 7：返程信息 -->
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="Allow Merge?">
              <el-switch
                v-model="form.isMerge"
                :active-value="1"
                :inactive-value="0"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Allow Transshipment?">
              <el-switch
                v-model="form.isTransshipment"
                :active-value="1"
                :inactive-value="0"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">Confirm</el-button>
          <el-button @click="cancel">Cancel</el-button>
        </div>
      </template>
    </el-dialog>
  </el-row>
</template>

<style scoped lang="less">

.publish-company-container {
  position: absolute;   /* 或者 fixed，看你的需求 */
  top: 5px;            /* 离页面顶部 20px */
  bottom: 0;            /* 底部贴合页面底部 */
  left: 0;              /* 根据需要调整左右 */
  right: 0;
  overflow-y: auto;     /* 内容过多时出现滚动条 */
   background-color: #f5f7fa;

}
html.dark .publish-company-container {
  background-color: #1e1e1e;
}

/* 左侧地图区：自动撑满剩余空间 */
.publish-company-container .map {
  flex: 2;                     /* 2:1 比例，也可以直接写 flex:1 */
  display: flex;
  flex-direction: column;
  padding: 0 !important;       /* 去掉 gutter padding */
}


.form-float-panel {
  position: absolute;
  top: 25px;
  left: 35px;
  z-index: 2000;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-panel {
  width: 350px;
  padding: 0px;
  background-color: rgba(255, 255, 255, 0.6);
  font-size: 13px;
  :deep(.el-form-item) {
    margin-bottom: 6px;
    .el-form-item__label {
      font-weight: normal;
    }
    backdrop-filter: blur(2px);
    
  }
  transform: scale(1);
  transform-origin: top left;
}

.compact-form {
  .el-form-item {
    margin-bottom: 4px;
  }

  .el-input,
  .el-input-number,
  .el-select,
  .el-date-picker {
    width: 100%;
    font-size: 13px;
  }
}

.info-panel {
  position: absolute;
  top: 5px;            /* 与左侧保持一致 */
  bottom: 0;
  right: 0;
  width: 100%;         /* 或者你想要的固定/百分比宽度 */
  overflow-y: auto;
  
}

.record-card {
  margin-bottom: 10px;
  font-size: 13px;
}
</style>
 