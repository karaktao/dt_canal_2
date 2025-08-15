<template>
  <!-- 整体容器：底部信息栏 -->
  <div class="info-container">
    <!-- 1. 位置（可输入 + 下拉选择） -->
    <el-card class="info-box location">
      <el-autocomplete
        v-model="location"
        :fetch-suggestions="fetchSuggestions"
        placeholder="Enter the location"
        @select="handleSelect"
        :trigger-on-focus="true"
        clearable
        :popper-class="'waterpoint-autocomplete-popper'"
        :debounce="200"
        style="width: 100%"
      />
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

<script src="./mergedInfoE.js"></script>

<style src="./infoE.css"></style>
