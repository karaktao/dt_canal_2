<template>
  <!-- 整体容器：底部信息栏 -->
  <div class="info-container">
    <!-- 1. 位置 -->
    <el-card class="info-box location">
      <span>{{ location }}</span>
    </el-card>

    <!-- 2. 船只数量 -->
    <el-card class="info-box">
      <div class="info-content ships">
        <div class="label">Ships On Canal</div>
        <div class="value">{{ shipsCount }}</div>
      </div>
    </el-card>

    <!-- 3. 当前水位 -->
    <el-card class="info-box">
      <div class="info-content water-level">
        <div class="label">Water Level</div>
        <div class="value">{{ waterLevel }}</div>
      </div>
    </el-card>

    <!-- 4. 天气 -->
    <el-card class="info-box small">
      <div class="info-content weather">
        <div class="label">Weather</div>
        <div class="value">{{ weather }}</div>
      </div>
    </el-card>
    <!-- 5. 风速 -->
    <el-card class="info-box small">
      <div class="info-content wind-speed">
        <div class="label">Wind Speed</div>
        <div class="value">{{ windSpeed }}</div>
      </div>
    </el-card>
    <!-- 6. 能见度 -->
    <el-card class="info-box small">
      <div class="info-content visibility">
        <div class="label">Visibility</div>
        <div class="value">{{ visibility }}</div>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      // 地点名称
      location: "Eefde Lock",
      // 当前天气
      weather: "24°C",
      // 当前风速
      windSpeed: "15 km/h",
      // 当前能见度
      visibility: "10 km",
      // 船只数量
      shipsCount: 5,
      // 当前水位
      waterLevel: "3 m",
    };
  },
};
</script>

<style>
/* 容器样式：使用 CSS Grid，6 列等分 */
.info-container {
  display: grid;
  /* 6 列等宽 */
  grid-template-columns: 1fr 1fr 1fr 0.5fr 0.5fr 0.5fr 0.1fr;
  /* 控制行/列间距 */
  column-gap: 8px; 
  justify-items: center;
  align-items: center;
  width: 100%;
  height: 60px;
  padding: 0 10px;
  background-color: rgba(255, 255, 255, 0.75);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 基础卡片样式：等高、水平居中 */
.info-box {
  display: flex;
  align-items: center;        /* 垂直居中 */
  justify-content: flex-start;/* 默认左对齐 */
  width: 250px;
  height: 50%;               /* 撑满 60px 容器 */
  box-sizing: border-box;
  padding: 0;                 
  font-size: 0.75em;
  opacity: 0.8;
}

/* 位置卡片内容居中 */
.info-box.location {
    font-weight: bold;
  justify-content: center;
}

/* 三个“small”卡片内字体和内边距稍微小一点（可选） */
.info-box.small {
  padding: 0;
  font-size: 0.75em;
  width: 200px;

}

/* 如果要去掉 el-card 自带的 body padding： */
.info-box .el-card__body  {
  padding: 0 !important;
}

/* 内部两栏布局：左 label，右 value */
.info-content {
  display: grid;
  width: 100%;
  height: 100%;
  align-items: center;  /* 垂直居中 */
}

.info-content.ships {
    grid-template-columns: 1.5fr 1.5fr; /* 两列布局 */
}

.info-content.water-level{
    grid-template-columns: 1fr 1.5fr; /* 两列布局 */
}

.info-content.weather {
    grid-template-columns: 1fr 2fr; /* 两列布局 */
}

.info-content.wind-speed {
    grid-template-columns: 1.5fr 1.5fr; /* 两列布局 */
}

.info-content.visibility {
    grid-template-columns: 1fr 2fr; /* 两列布局 */
}



/* 名称（左侧）加粗、左对齐、带右侧分割线 */
.label {
  font-weight: bold;
  text-align: left;
  padding-left: 12px; 
  padding-right: 8px;
  border-right: 1px solid #ccc;
}

/* 数值（右侧）居中 */
.value {
  text-align: center;
}

</style>
