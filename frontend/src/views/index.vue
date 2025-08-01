<script setup>
// 地图输入
import {
  ref,
  watch,
  computed,
  onMounted,
  defineAsyncComponent,
  nextTick,
} from "vue";
import { ElButton, ElButtonGroup, ElCard, ElDivider } from "element-plus";

// 异步加载 WaterLevel 组件
const WaterLevel = defineAsyncComponent(() =>
  import("@/components/dashboard/waterlevel.vue")
);
const LayerBerth = defineAsyncComponent(() =>
  import("@/components/dashboard/LayerBerth.vue")
);
const LayerLock = defineAsyncComponent(() =>
  import("@/components/dashboard/LayerLock.vue")
);
const LayerBridge = defineAsyncComponent(() =>
  import("@/components/dashboard/LayerBridge.vue")
);

import WaterLevelChart from "@/components/dashboard/WaterLevelChart.vue";


import InfoPanel from "@/components/dashboard/InfoPanel.vue";

// --------- OpenLayers & 投影支持 ---------
import "ol/ol.css";
import Map from "ol/Map";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import OSM from "ol/source/OSM";
import XYZ from "ol/source/XYZ";
import Zoom from "ol/control/Zoom";
import proj4 from "proj4";
import { register } from "ol/proj/proj4";
import { get as getProjection } from "ol/proj";

const InfoA = defineAsyncComponent(() =>
  import("@/components/dashboard/InfoA.vue")
);
const InfoB = defineAsyncComponent(() =>
  import("@/components/dashboard/InfoB.vue")
);
const InfoC = defineAsyncComponent(() =>
  import("@/components/dashboard/InfoC.vue")
);
const InfoD = defineAsyncComponent(() =>
  import("@/components/dashboard/InfoD.vue")
);
const InfoE = defineAsyncComponent(() =>
  import("@/components/dashboard/InfoE.vue")
);

// 注册投影
proj4.defs(
  "EPSG:28992",
  "+proj=sterea +lat_0=52.15616 +lon_0=5.38764 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +towgs84=593.16,26.15,478.54,-6.3239,-0.5008,-5.5487,4.0775 +units=m +no_defs"
);
proj4.defs("EPSG:25831", "+proj=utm +zone=31 +ellps=GRS80 +units=m +no_defs");
register(proj4);

// --------- 组件状态 ---------
const map = ref(null); // OpenLayers 地图实例
const view = ref(null); // OpenLayers 视图实例
const osmLayer = ref(null);
const esriLayer = ref(null);

const layers = ref([]); // 存储所有 prop 方式管理的图层
const selectedItems = ref([]); // 信息导航栏勾选的选项
const activeTab = ref("Water"); // 当前激活的标签页
const panelVisible = ref(false); // 控制信息面板的显示隐藏
const panelRoot = ref(null);

const waterLevelRef = ref(null); // WaterLevel 的实例
const selectedItem = ref(null); // 当前选中的地理要素
const measurementData = ref(null); // 测量数据
const berthRef = ref(null);
const lockref = ref(null);
const bridgeref = ref(null);

// --------- 通用信息面板 ---------
// 关闭面板时，清空选中要素
function closePanel() {
  selectedItem.value = null;
}
function doSomething() {
  // TODO: 你要的“查看详情”逻辑
}

// --------- 地图初始化 ---------
onMounted(() => {
  // 创建视图
  view.value = new View({
    center: [694118.1989, 6829154.6003],
    zoom: 13,
    projection: "EPSG:3857",
  });

  // 底图
  osmLayer.value = new TileLayer({ source: new OSM(), visible: true });
  esriLayer.value = new TileLayer({
    source: new XYZ({
      url: "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}",
    }),
    visible: false,
  });

  // 地图实例
  map.value = new Map({
    target: "map",
    view: view.value,
    layers: [osmLayer.value, esriLayer.value],
  });

  // 在 map.value 初始化之后
  map.value.getControls().clear();

  // 如果有额外图层（来自 waterLevel 等），添加到地图
  layers.value.forEach((layer) => {
    if (!map.value.getLayers().getArray().includes(layer)) {
      map.value.addLayer(layer);
    }
  });

  const storedItems = localStorage.getItem("selectedItems");
  if (storedItems) {
    try {
      selectedItems.value = JSON.parse(storedItems);
    } catch (e) {
      console.warn("❌ 无法解析存储的图层状态");
    }
  }

  console.log("🗺️ 地图已就绪：", map.value);
});

// --------- 地图操作方法 ---------
function toggleLayer(type) {
  osmLayer.value.setVisible(type === "street");
  esriLayer.value.setVisible(type === "satellite");
}

// --------- 组件方法 ---------

// 地图初始化完成时触发
function onMapReady(map) {
  console.log("🗺️ 地图已准备好：", map);
  // 公共交互可放这里
}

function registerLayer(layer) {
  if (!layer) {
    console.warn("❌ 图层为空，未注册");
    return;
  }

  console.log("📌 尝试注册图层：", layer);
  if (!layers.value.includes(layer)) {
    layers.value.push(layer);
    console.log("✅ 图层已加入 layers.value");
    map.value.addLayer(layer);
  }
}

// 可选：从侧栏移除时 unregister
function unregisterLayer(layerName) {
  const arr = map.value.getLayers().getArray();
  const target = arr.find((l) => l.get("name") === layerName);
  if (target) map.value.removeLayer(target);
  layers.value = layers.value.filter((l) => l.get("name") !== layerName);
}

// ✅ 监听组件 ref 初始化完成后执行 map 事件绑定
watch(
  () => waterLevelRef.value,
  async (val) => {
    if (val && selectedItems.value.includes("waterLevel")) {
      console.log("🟢 waterLevelRef 已就绪，绑定 map 事件");
      await nextTick(); // 等待 DOM 更新完成
      val.attachMapEvents(map.value);
    }
  }
);

// ✅ 监听组件 ref 初始化完成后执行 map 事件绑定
watch(
  () => berthRef.value,
  async (val) => {
    if (val && selectedItems.value.includes("berth")) {
      console.log("🟢 berthRef 已就绪，绑定 map 事件");
      await nextTick(); // 等待 DOM 更新完成
      val.attachMapEvents(map.value);
    }
  }
);

watch(
  () => lockref.value,
  async (val) => {
    if (val && selectedItems.value.includes("lock")) {
      console.log("🟢 lockRef 已就绪，绑定 map 事件");
      await nextTick(); // 等待 DOM 更新完成
      val.attachMapEvents(map.value);
    }
  }
);

watch(
  () => bridgeref.value,
  async (val) => {
    if (val && selectedItems.value.includes("bridge")) {
      console.log("🟢 bridgeRef 已就绪，绑定 map 事件");
      await nextTick(); // 等待 DOM 更新完成
      val.attachMapEvents(map.value);
    }
  }
);




// 监听 checkbox 勾选变化
watch(selectedItems, (newVal, oldVal) => {
  if (!map.value) return;

  // 勾选 waterLevel 时注册并绑定
  if (newVal.includes("waterLevel") && !oldVal.includes("waterLevel")) {
    waterLevelRef.value?.attachMapEvents(map.value);
    registerLayer(waterLevelRef.value?.getLayer?.());
  }

  // 取消勾选waterLevel 时卸载图层
  if (!newVal.includes("waterLevel") && oldVal.includes("waterLevel")) {
    unregisterLayer("waterLevel"); // 修改点6: 卸载图层
  }

  // ✅ 注册 berth 图层
  // 勾选 berth 时，如果组件实例已准备好则直接绑定并注册；
  // 若尚未加载完成，则在上面的 berthRef watcher 中处理
  if (newVal.includes('berth') && !oldVal.includes('berth')) {
    if (berthRef.value) {
      berthRef.value.attachMapEvents(map.value);
      registerLayer(berthRef.value.getLayer?.());
    }
  }
  // 取消勾选时卸载
  if (!newVal.includes('berth') && oldVal.includes('berth')) {
    unregisterLayer('berth');
  }

  // ✅ 注册 lock 图层
  // 勾选 lock 时，如果组件实例已准备好则直接绑定并注册；
  // 若尚未加载完成，则在上面的 lockref watcher 中处理
  if (newVal.includes('lock') && !oldVal.includes('lock')) {
    if (lockref.value) {
      lockref.value.attachMapEvents(map.value);
      registerLayer(lockref.value.getLayer?.());
    }
  }
  // 取消勾选时卸载
  if (!newVal.includes('lock') && oldVal.includes('lock')) {
    unregisterLayer('lock');
  }

  // ✅ 注册 bridge 图层
  // 勾选 bridge 时，如果组件实例已准备好则直接绑定并注册；
  // 若尚未加载完成，则在上面的 bridgeref watcher 中处理
  if (newVal.includes('bridge') && !oldVal.includes('bridge')) {
    if (bridgeref.value) {
      bridgeref.value.attachMapEvents(map.value);
      registerLayer(bridgeref.value.getLayer?.());
    }
  }
  // 取消勾选时卸载
  if (!newVal.includes('bridge') && oldVal.includes('bridge')) {
    unregisterLayer('bridge');
  }




  // 存储勾选的图层设置
  localStorage.setItem("selectedItems", JSON.stringify(newVal));
});









// ---------  图层控件 ---------
// 信息导航栏数据
const categories = [
  {
    name: "Water",
    options: [
      { label: "Water level", value: "waterLevel" },
      { label: "Flow rate", value: "flowRate" },
      { label: "Discharge", value: "discharge" },
    ],
  },
  {
    name: "Infra",
    options: [
      { label: "Lock", value: "lock" },
      { label: "Berth", value: "berth" },
      { label: "Bridge", value: "bridge" },
    ],
  },
  {
    name: "Ship",
    options: [
      { label: "Vessel", value: "vessel" },
      { label: "Logistics", value: "logistics" },
    ],
  },
];
// —— 新增：点击 Tab header 时切换选项卡并展开面板
function handleTabClick(tab) {
  activeTab.value = tab.name;
  panelVisible.value = true;
}

// —— 全局监听：点击面板以外区域时收起面板
function onClickOutside(e) {
  if (panelRoot.value && !panelRoot.value.contains(e.target)) {
    panelVisible.value = false;
  }
}

const currentOptions = computed(() => {
  const cat = categories.find((c) => c.name === activeTab.value);
  return cat ? cat.options : [];
});

onMounted(() => {
  document.addEventListener("click", onClickOutside);
});
onBeforeUnmount(() => {
  document.removeEventListener("click", onClickOutside);
});

// ---------  处理子组件事件 ---------
// 信息导航栏点击事件
function handleFeatureClick(feature) {
  if (feature) {
    selectedItem.value = {
      locCode: feature.locCode,
      locNaam: feature.locNaam,
      locX: feature.locX,
      locY: feature.locY,
    };
  } else {
    selectedItem.value = null;
  }
}
// 处理测量数据加载事件
function handleMeasurementLoaded(data) {
  measurementData.value = data;
}
watch(measurementData, (newVal) => {
  console.log("📊 measurementData 已更新", newVal);
});
</script>


<template>
  <el-row class="dashboard" :gutter="0" style="height: 100vh; margin: 0">
    <!-- 中部地图 -->
    <el-col
      :span="24"
      class="map"
      style="
        position: relative;
        display: flex;
        flex-direction: column;
        height: 100%;
      "
    >
      <el-card class="map-card" body-style="padding: 0;" shadow="never">
        <div id="map" class="map-container"></div>

        <!-- 只有勾选了 waterLevel 时才加载 WaterLevel 图层组件 -->
        <component
          :is="WaterLevel"
          v-if="selectedItems.includes('waterLevel')"
          ref="waterLevelRef"
          @map-layer-ready="registerLayer"
          @feature-clicked="handleFeatureClick"
          @measurement-loaded="handleMeasurementLoaded"
        />

        <component
          :is="LayerBerth"
          v-if="selectedItems.includes('berth')"
          ref="berthRef"
          @map-layer-ready="registerLayer"
          @feature-clicked="handleFeatureClick"
        />

        <component
          :is="LayerLock"
          v-if="selectedItems.includes('lock')"
          ref="lockref"
          @map-layer-ready="registerLayer"
          @feature-clicked="handleFeatureClick"
        />

        <component
          :is="LayerBridge"
          v-if="selectedItems.includes('bridge')"
          ref="bridgeref"
          @map-layer-ready="registerLayer"
          @feature-clicked="handleFeatureClick"
        />  






        <!-- 图层切换按钮组 -->
        <div class="btn-group">
          <el-button size="small" @click="toggleLayer('street')"
            >Street</el-button
          >
          <el-button size="small" @click="toggleLayer('satellite')"
            >Satellite</el-button
          >
        </div>
      </el-card>

      <!-- 图层控制 -->
      <div class="layer-switcher-wrapper" ref="panelRoot" @click.stop>
        <el-card class="layer-switcher">
          <el-tabs
            v-model="activeTab"
            @tab-click="handleTabClick"
            stretch
            type="card"
            class="layer-tabs"
          >
            <el-tab-pane
              v-for="cat in categories"
              :key="cat.name"
              :label="cat.name"
              :name="cat.name"
            />
          </el-tabs>

          <!-- 2. 只有 panelVisible 为 true 时，才显示下面的复选框组 -->
          <div v-if="panelVisible" class="dropdown-panel">
            <el-checkbox-group v-model="selectedItems">
              <el-checkbox
                v-for="opt in currentOptions"
                :key="opt.value"
                :value="opt.value"
              >
                {{ opt.label }}
              </el-checkbox>
            </el-checkbox-group>
          </div>
        </el-card>
      </div>

      <!-- 左侧两块 -->
      <div class="overlay left">
        <component :is="InfoA" />
        <component :is="InfoB" />
      </div>

      <!-- 右侧两块 -->
      <div class="overlay right">
        <component :is="InfoC" />
        <component :is="InfoD" />
      </div>

      <!-- 底部一排 -->
      <div class="overlay bottom">
        <component :is="InfoE" />
      </div>

      <InfoPanel
        :visible="!!selectedItem"
        :title="selectedItem ? selectedItem.locNaam : '信息面板'"
        :item="selectedItem || {}"
        @close="closePanel"
      >
        <!-- 如果想自定义内容，可用 slot -->
        <template #content>
          <WaterLevelChart v-if="measurementData" :data="measurementData" />
        </template>

        <!-- footer 插槽：未来放按钮 -->
        <!-- <template #footer>
          <el-button size="mini" @click="doSomething">Details</el-button>
        </template> -->
      </InfoPanel>
    </el-col>
  </el-row>
</template>

<style scoped lang="less">
// ---------  图层切换按钮组样式 ---------
.layer-switcher-wrapper {
  position: absolute;
  top: 5px;
  left: 10px;
  width: 400px;
  padding: 0;
  margin: 0;
  z-index: 2000;

  /* 去掉 el-card 自带的阴影和边框 */
  ::v-deep .el-card {
    box-shadow: none !important;
    border: none !important;
    background: transparent !important;
  }

  // 2. header 区（tab 那条）高度 40px，并且半透明
  ::v-deep .el-card.is-always-shadow.layer-switcher .el-card__header {
    padding: 0 !important;
    border-bottom: none !important;
    line-height: 40px !important;
    background: rgba(255, 255, 255, 0.8) !important;
  }

  // 3. body 区（下拉 panel 放这里）也透明
  ::v-deep .el-card.is-always-shadow.layer-switcher .el-card__body {
    padding: 0 !important;
    background: transparent !important;
    border: none !important;
  }

  // 4. 每个 tab 项目也要半透明白
  ::v-deep .el-card.is-always-shadow.layer-switcher .el-tabs__item {
    background: rgba(255, 255, 255, 0.9) !important;
    backdrop-filter: blur(2px);
    border: none !important;
    height: 30px !important;
  }

  // 5. 你的下拉面板
  .dropdown-panel {
    margin-top: -20px; /* 让下拉面板紧贴 header */
    background: rgba(255, 255, 255, 0.6) !important;
    backdrop-filter: blur(5px);
    width: 400px !important;
    border: none;
    box-shadow: none;
    padding: 8px;
  }
}

.checkbox-item {
  margin-bottom: 6px;
}

// ---------    信息导航栏样式 -------
/* —— 添加这段基础 overlay 样式 —— */
.overlay {
  position: absolute;
  display: flex;
  gap: 10px;
  z-index: 1001; /* 比地图控件要高 */
}

/* 左侧一列 */
.overlay.left {
  top: 100px;
  left: 10px;
  max-height: calc(100vh - 250px);
  flex-direction: column;
  backdrop-filter: blur(5px);
}

/* 右侧一列 */
.overlay.right {
  top: 100px;
  right: 10px;
  max-height: calc(100vh - 250px);
  flex-direction: column;
  backdrop-filter: blur(5px);
}

/* 底部一排，拉满宽度 */
.overlay.bottom {
  position: fixed;
  bottom: 0px; /* 或者你想要的距离底部的距离 */
  left: 20; /* 从父容器最左侧开始 */
  width: 100%; /* 占满父容器的全部宽度 */
  /* 如果你想占满整个视口宽度，可以用 width: 100vw; */
  /*  width: 100vw;  */

  /* 居中内容 */
  display: flex;
  justify-content: center; /* 水平居中所有子组件 */
  gap: 10px;

  height: 50px; /* 根据 InfoE 需要的高度设 */
  z-index: 1001;
  backdrop-filter: blur(2px);
}

.map-card {
  height: calc(100vh - 91px); // 减去顶部导航栏高度（根据实际高度调整）
  margin: 0; // 去除 el-card 默认外边距
  padding: 0;
  border: none;
  border-radius: 0;
  position: relative;
  box-shadow: none;
  overflow: visible;
}
.map {
  position: relative;
  overflow: visible;
}
.map-container {
  height: 100%;
  width: 100%;
}
.btn-group {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 1000;
  display: flex;
  gap: 5px;
}

.dashboard {
  display: flex;
  position: absolute;
  top: 5px;
  bottom: 0;
  left: 0;
  right: 0;
  overflow-y: auto;
}

.navbar-panel {
  background: #f5f7fa;
}

.info-checkbox-group {
  display: block;
  margin-left: -15px;
}

.info-category {
  margin-bottom: 12px;
}

.category-title {
  font-weight: bold;
  margin-bottom: 6px;
}

.btn-group {
  display: flex;
  gap: 6px;
  margin-top: 12px;
  flex-wrap: wrap;
}
::v-deep .map-card .el-card__body {
  padding: 0 !important; /* 去掉内边距 */
  display: flex !important; /* 关键：变成 flex 容器 */
  flex-direction: column; /* 垂直方向拉伸 */
  height: 100% !important; /* 撑满父元素 el-card */
}
</style>
