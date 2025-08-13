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
        style="width: 100%;"
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

<script>
import axios from "axios";
import proj4 from "proj4";
import { register } from "ol/proj/proj4";
import { transform } from "ol/proj";

const TRUNCATE_LEN = 2000; // 控制在 debug 中打印多少字符 / 元素

export default {
  data() {
    return {
      location: "Eefde Lock", // 绑定到 autocomplete
      weather: "—",
      windSpeed: "—",
      visibility: "—",
      shipsCount: 0,
      waterLevel: "—", // 将由选择站点后填充
      vesselSectionCodes: [
        "NL00081",
        "NL00082",
        "NL00652",
        "NL00650",
        "NL00647",
        "NL01224",
        "NL00642",
        "NL00644",
        "NL00646",
        "NL01186",
      ],
      // 将默认每 10 分钟刷新一次（单位：毫秒）
      refreshIntervalMs: 10 * 60 * 1000,
      refreshTimer: null,
      debugResponses: {},

      // ---- 新增用于 autocomplete 的数据结构 ----
      geoFeatures: [], // 原始 features 数组
      featuresIndexByName: {}, // name(lower) -> array of features (防止重名)
      currentSuggestions: [], // 用于显示的建议项（每项 { value: displayText, name, measurement, coords, _feature }）
      selectedCoordinates: null, // 选中站点的坐标（通常 EPSG:25831 的 [x, y]）
      selectedFeature: null, // 最后选中的 feature（整个 object），用于调试/刷新水位
    };
  },

  // 把 mounted 设为 async，便于 await 初次刷新
  async mounted() {
    // 注册投影
    this.registerProjs();

    // 初次加载并刷新所有数据（测点、船只数、天气、waterLevel）
    await this.refreshAll();

    // 启动定时器（每 X 毫秒刷新一次）
    if (this.refreshIntervalMs > 0) {
      this.refreshTimer = setInterval(() => {
        // 不需要等待 setInterval 回调的 await
        this.refreshAll().catch((e) => console.warn("refreshAll error:", e));
      }, this.refreshIntervalMs);
      console.log(`🔁 已启动定时刷新：${this.refreshIntervalMs} ms`);
    }
  },

  beforeUnmount() {
    if (this.refreshTimer) {
      clearInterval(this.refreshTimer);
      this.refreshTimer = null;
    }
  },

  methods: {
    // ---------- 刷新总入口 ----------
    // refreshAll: 每次周期性触发的统一刷新逻辑
    async refreshAll() {
      console.log("↻ refreshAll 开始");
      // 1) 更新船只统计
      try {
        await this.updateShipsCount();
      } catch (err) {
        console.warn("updateShipsCount failed:", err);
      }

      // 2) 重新拉取测点列表（更新 geoFeatures / index / waterLevel 基础数据）
      //    我们在 initWaterPoints 中**只**加载测点和建立索引，不自动请求天气，
      //    后面手动发起天气请求以避免重复请求。
      try {
        await this.initWaterPoints(); // 仅刷新测点数据和索引
      } catch (err) {
        console.warn("initWaterPoints failed:", err);
      }

      // 3) 基于当前 location 更新 waterLevel（不自动触发天气）
      try {
        await this.tryAutoFillFromLocation(this.location, /*fetchWeather=*/ false);
      } catch (err) {
        console.warn("tryAutoFillFromLocation failed:", err);
      }

      // 4) 如果选中坐标存在，单独请求天气（避免在 init 中重复请求）
      if (this.selectedCoordinates && Array.isArray(this.selectedCoordinates)) {
        try {
          await this.fetchWeatherForCoordinates(this.selectedCoordinates);
        } catch (err) {
          console.warn("fetchWeatherForCoordinates failed:", err);
        }
      }

      console.log("↻ refreshAll 完成");
    },

    // ---------- 船只数量相关方法 ----------
    _snippet(x) {
      try {
        if (typeof x === "string") return x.length > TRUNCATE_LEN ? x.slice(0, TRUNCATE_LEN) + "…(truncated)" : x;
        if (Array.isArray(x)) {
          const sample = x.slice(0, 200); // 取最多 200 条示例
          return JSON.stringify(sample);
        }
        const s = JSON.stringify(x);
        return s.length > TRUNCATE_LEN ? s.slice(0, TRUNCATE_LEN) + "…(truncated)" : s;
      } catch (e) {
        return String(x).slice(0, TRUNCATE_LEN);
      }
    },

    async fetchSectionCount(sectionCode) {
      const tag = `[ships][fetch][${sectionCode}]`;
      const url = "/api/vessels/section";
      const params = {
        "getTracksBySectionRequest.sectionCode": sectionCode,
        "getTracksBySectionRequest.hectomFrom": "",
        "getTracksBySectionRequest.hectomTo": "",
      };

      this.debugResponses[sectionCode] = {
        ok: false,
        status: null,
        contentType: null,
        responseURL: null,
        dataType: null,
        dataSnippet: null,
      };

      try {
        const response = await axios.get(url, {
          params,
          headers: { Accept: "application/json" },
          timeout: 15000,
        });

        const responseURL = response.request && response.request.responseURL ? response.request.responseURL : null;
        const contentType = (response.headers && response.headers["content-type"]) || "";
        const data = response.data;

        this.debugResponses[sectionCode].status = response.status;
        this.debugResponses[sectionCode].contentType = contentType;
        this.debugResponses[sectionCode].responseURL = responseURL;

        if (typeof data === "string" && (data.trim().startsWith("<!DOCTYPE") || data.trim().startsWith("<html") || contentType.includes("text/html"))) {
          console.error(`${tag} !! Received HTML instead of JSON — likely wrong proxy/rewrite or route. response.status = ${response.status}`);
          this.debugResponses[sectionCode].dataType = "html";
          this.debugResponses[sectionCode].dataSnippet = this._snippet(data);
          return 0;
        }

        if (data === null || data === undefined) {
          this.debugResponses[sectionCode].dataType = "null";
          this.debugResponses[sectionCode].dataSnippet = "";
          return 0;
        }

        let count = 0;
        if (Array.isArray(data)) {
          count = data.length;
          this.debugResponses[sectionCode].dataType = "array";
          this.debugResponses[sectionCode].dataSnippet = this._snippet(data);
        } else if (Array.isArray(data.tracks)) {
          count = data.tracks.length;
          this.debugResponses[sectionCode].dataType = "tracks[]";
          this.debugResponses[sectionCode].dataSnippet = this._snippet(data.tracks);
        } else if (Array.isArray(data.items)) {
          count = data.items.length;
          this.debugResponses[sectionCode].dataType = "items[]";
          this.debugResponses[sectionCode].dataSnippet = this._snippet(data.items);
        } else if (typeof data.total === "number") {
          count = data.total;
          this.debugResponses[sectionCode].dataType = "total";
          this.debugResponses[sectionCode].dataSnippet = String(data.total);
        } else if (typeof data.count === "number") {
          count = data.count;
          this.debugResponses[sectionCode].dataType = "count";
          this.debugResponses[sectionCode].dataSnippet = String(data.count);
        } else {
          const possibleArray = Object.values(data).find((v) => Array.isArray(v));
          if (possibleArray) {
            count = possibleArray.length;
            this.debugResponses[sectionCode].dataType = "first-array-field";
            this.debugResponses[sectionCode].dataSnippet = this._snippet(possibleArray);
          } else {
            console.warn(`${tag} parse: unknown JSON shape — not an array or known wrapper`);
            this.debugResponses[sectionCode].dataType = "unknown-object";
            this.debugResponses[sectionCode].dataSnippet = this._snippet(data);
            count = 0;
          }
        }

        this.debugResponses[sectionCode].ok = true;
        return count;
      } catch (err) {
        if (err && err.response) {
          this.debugResponses[sectionCode].status = err.response.status;
          this.debugResponses[sectionCode].contentType = err.response.headers && err.response.headers["content-type"];
          const raw = err.response.data;
          this.debugResponses[sectionCode].dataType = typeof raw;
          this.debugResponses[sectionCode].dataSnippet = this._snippet(raw);
        } else {
          this.debugResponses[sectionCode].dataType = "network-error";
          this.debugResponses[sectionCode].dataSnippet = String(err && err.message ? err.message : err);
        }
        return 0;
      }
    },

    async updateShipsCount() {
      try {
        const counts = await Promise.all(this.vesselSectionCodes.map((c) => this.fetchSectionCount(c)));
        const total = counts.reduce((acc, n) => acc + Number(n || 0), 0);
        this.shipsCount = total;
      } catch (err) {
        console.warn("[ships] update failed:", err);
      }
    },

    refreshNow() {
      this.updateShipsCount();
    },

    // ---------- 投影、测点、下拉等原有方法（略作小调整） ----------
    registerProjs() {
      try {
        proj4.defs("EPSG:25831", "+proj=utm +zone=31 +ellps=GRS80 +units=m +no_defs");
        register(proj4);
        console.log("✅ 已注册 EPSG:25831 投影");
      } catch (err) {
        console.warn("⚠️ 注册投影时出错（可忽略如果已注册）:", err);
      }
    },

    // initWaterPoints 现在仅拉取 features 并建立 index（不自动拉天气）
    async initWaterPoints() {
      console.log("📌 WaterLevel - 使用 RWS API 加载（initWaterPoints）");
      try {
        const response = await fetch(
          "/api/waterinfo/api/point/latestmeasurement?parameterId=waterhoogte"
        );
        const geojson = await response.json();

        if (!geojson.features || !Array.isArray(geojson.features)) {
          console.error("❌ 无效数据格式", geojson);
          return;
        }

        this.geoFeatures = geojson.features;
        this.buildIndex(this.geoFeatures);
        console.log(`✅ 已加载 ${this.geoFeatures.length} 个测点（示例）`, this.geoFeatures.slice(0, 3));
      } catch (err) {
        console.error("❌ 初始化测点时出错：", err);
        throw err;
      }
    },

    buildIndex(features) {
      this.featuresIndexByName = {};
      for (const f of features) {
        const nameRaw = f?.properties?.name ?? "";
        const name = String(nameRaw).trim();
        const key = name.toLowerCase();
        if (!this.featuresIndexByName[key]) {
          this.featuresIndexByName[key] = [];
        }
        this.featuresIndexByName[key].push(f);
      }
    },

    fetchSuggestions(queryString, cb) {
      if (!queryString || !this.geoFeatures.length) {
        cb([]);
        return;
      }
      const q = queryString.trim().toLowerCase();
      const maxResults = 12;
      const matches = [];

      for (const f of this.geoFeatures) {
        const nameRaw = f?.properties?.name ?? "";
        const name = String(nameRaw).trim();
        if (!name) continue;
        if (name.toLowerCase().includes(q)) {
          const measText = this.extractMeasurementText(f?.properties);
          const display = measText ? `${name} — ${measText}` : name;
          const coords = (f?.geometry?.coordinates && [...f.geometry.coordinates]) || null;
          matches.push({
            value: display,
            name,
            measurement: measText,
            coords,
            _feature: f,
          });
          if (matches.length >= maxResults) break;
        }
      }

      this.currentSuggestions = matches;
      cb(matches.map((m) => ({ value: m.value })));
    },

    async handleSelect(selected) {
      const selValue = typeof selected === "string" ? selected : selected.value;
      const found = this.currentSuggestions.find((s) => s.value === selValue);
      if (!found) {
        const possible = this.currentSuggestions.find((s) => s.name === selValue);
        if (possible) {
          return this.applySelection(possible);
        }
        const lower = String(selValue).toLowerCase();
        const arr = this.featuresIndexByName[lower];
        if (arr && arr.length) {
          const f = arr[0];
          const measText = this.extractMeasurementText(f.properties);
          const coords = (f.geometry && [...f.geometry.coordinates]) || null;
          return this.applySelection({
            value: selValue,
            name: selValue,
            measurement: measText,
            coords,
            _feature: f,
          });
        }
        console.warn("⚠️ 选择项未在 currentSuggestions 中找到：", selected);
        return;
      }

      await this.applySelection(found);
    },

    async applySelection(item) {
      this.location = item.name;
      this.waterLevel = item.measurement || "—";
      this.selectedCoordinates = item.coords;
      this.selectedFeature = item._feature || null;
      console.log("📍 已选测点：", item.name, "测值:", item.measurement, "坐标:", item.coords);

      if (this.selectedCoordinates && Array.isArray(this.selectedCoordinates)) {
        try {
          await this.fetchWeatherForCoordinates(this.selectedCoordinates);
        } catch (err) {
          console.warn("fetchWeatherForCoordinates error:", err);
        }
      }
    },

    extractMeasurementText(properties) {
      if (!properties) return null;
      const meas = properties.measurements;
      if (meas && typeof meas === "object" && !Array.isArray(meas) && ("latestValue" in meas || "value" in meas)) {
        const val = meas.latestValue ?? meas.value ?? null;
        const unit = meas.unitCode ?? meas.unit ?? "";
        if (val === null || val === undefined) return null;
        return `${val}${unit ? " " + unit : ""}`;
      }
      if (Array.isArray(meas) && meas.length) {
        let found = meas.find((m) => m && ("latestValue" in m));
        if (!found) found = meas[0];
        const val = found?.latestValue ?? found?.value ?? null;
        const unit = found?.unitCode ?? found?.unit ?? "";
        if (val === null || val === undefined) return null;
        return `${val}${unit ? " " + unit : ""}`;
      }
      if ("latestValue" in properties) {
        const val = properties.latestValue;
        const unit = properties.unitCode ?? "";
        return `${val}${unit ? " " + unit : ""}`;
      }
      return null;
    },

    // tryAutoFillFromLocation 支持第二个参数决定是否自动请求天气
    async tryAutoFillFromLocation(locationName, fetchWeather = true) {
      if (!locationName) return;
      const key = String(locationName).trim().toLowerCase();
      const arr = this.featuresIndexByName[key];
      if (arr && arr.length) {
        const f = arr[0];
        const measText = this.extractMeasurementText(f.properties);
        this.waterLevel = measText || "—";
        this.selectedCoordinates = (f.geometry && [...f.geometry.coordinates]) || null;
        this.selectedFeature = f;
        if (fetchWeather && this.selectedCoordinates) {
          try {
            await this.fetchWeatherForCoordinates(this.selectedCoordinates);
          } catch (err) {
            console.warn("fetchWeatherForCoordinates failed in tryAutoFillFromLocation:", err);
          }
        }
      }
    },

    // 将 EPSG:25831 坐标转换为经纬度 (EPSG:4326)，并调用 Open-Meteo 获取当前天气
    async fetchWeatherForCoordinates(coords) {
      if (!coords || !Array.isArray(coords) || coords.length < 2) {
        throw new Error("无效坐标");
      }

      let lonLat;
      try {
        const transformed = transform([coords[0], coords[1]], "EPSG:25831", "EPSG:4326");
        const lon = Number(transformed[0]);
        const lat = Number(transformed[1]);
        if (!isFinite(lon) || !isFinite(lat)) {
          throw new Error("转换结果无效");
        }
        lonLat = { lat, lon };
        console.log("🔁 坐标转换 EPSG:25831 -> EPSG:4326:", coords, "=>", lonLat);
      } catch (err) {
        console.error("❌ 坐标转换失败：", err);
        throw err;
      }

      const { lat, lon } = lonLat;
      const params = {
        latitude: lat,
        longitude: lon,
        models: "best_match",
        current: "temperature_2m,wind_speed_10m,wind_direction_10m,wind_gusts_10m,rain,relative_humidity_2m,weather_code,visibility",
        timezone: "Europe%2FAmsterdam",
      };
      const url = `https://api.open-meteo.com/v1/forecast?latitude=${encodeURIComponent(params.latitude)}&longitude=${encodeURIComponent(params.longitude)}&models=${params.models}&current=${encodeURIComponent(params.current)}&timezone=${params.timezone}`;

      try {
        const res = await axios.get(url);
        const data = res.data;
        const current = data?.current ?? null;
        const units = data?.current_units ?? null;

        if (!current) {
          console.warn("⚠️ Open-Meteo 未返回 current 数据：", data);
          this.weather = "—";
          this.windSpeed = "—";
          this.visibility = "—";
          return;
        }

        const tempVal = current.temperature_2m;
        const tempUnit = units?.temperature_2m ?? "";
        this.weather = (tempVal !== undefined && tempVal !== null) ? `${tempVal}${tempUnit ? " " + tempUnit : ""}` : "—";

        const windVal = current.wind_speed_10m;
        const windUnit = units?.wind_speed_10m ?? "";
        this.windSpeed = (windVal !== undefined && windVal !== null) ? `${windVal}${windUnit ? " " + windUnit : ""}` : "—";

        const visVal = current.visibility;
        const visUnit = units?.visibility ?? "";
        this.visibility = (visVal !== undefined && visVal !== null) ? `${visVal}${visUnit ? " " + visUnit : ""}` : "—";

        console.log("🌤️ Open-Meteo current:", current);
      } catch (err) {
        console.error("❌ 请求 Open-Meteo 失败：", err);
        this.weather = "—";
        this.windSpeed = "—";
        this.visibility = "—";
        throw err;
      }
    },
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
  align-items: center; /* 垂直居中 */
  justify-content: flex-start; /* 默认左对齐 */
  width: 250px;
  height: 50%; /* 撑满 60px 容器 */
  box-sizing: border-box;
  padding: 0;
  font-size: 0.75em;
  opacity: 0.8;
}

/* 位置卡片内容居中 */
.info-box.location {
  font-weight: bold;
  justify-content: center;
  border-color: transparent !important;
}

/* 三个“small”卡片内字体和内边距稍微小一点（可选） */
.info-box.small {
  padding: 0;
  font-size: 0.75em;
  width: 200px;
}

/* 如果要去掉 el-card 自带的 body padding： */
.info-box .el-card__body {
  padding: 0 !important;
}

/* 内部两栏布局：左 label，右 value */
.info-content {
  display: grid;
  width: 100%;
  height: 100%;
  align-items: center; /* 垂直居中 */
}

.info-content.ships {
  grid-template-columns: 1.5fr 1.5fr; /* 两列布局 */
}

.info-content.water-level {
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

/* Autocomplete 的下拉样式调整（按需） */
.waterpoint-autocomplete-popper .el-autocomplete-suggestion__wrap {
  max-height: 320px;
  overflow: auto;
}
</style>
