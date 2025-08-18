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

<script>
import axios from "axios";
import proj4 from "proj4";
import { register } from "ol/proj/proj4";
import { transform } from "ol/proj";

const TRUNCATE_LEN = 2000;
const STORAGE_MAP_KEY = "infoE_state_map_v1";

export default {
  name: "InfoE",
  emits: [
    "update:selectedCoordinates",
    "update:location",
    "waterpoints-loaded",
    "selected-feature",
  ],
  data() {
    return {
      // UI state
      location: "Eefde beneden",
      weather: "—",
      windSpeed: "—",
      visibility: "—",
      shipsCount: 0,
      waterLevel: "—",

      // ship section codes (used to compute shipsCount)
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

      // refresh control
      refreshIntervalMs: 10 * 60 * 1000,
      refreshTimer: null,

      // features / autocomplete / selection
      geoFeatures: [],
      featuresIndexByName: {},
      currentSuggestions: [],
      selectedCoordinates: null, // usually [x, y] (may be EPSG:25831 or lon/lat)
      selectedFeature: null,

      // debug/caching
      debugResponses: {},

      // guards
      preventAutoSave: false, // 在初始化/刷新期间防止写入占位值覆盖缓存
    };
  },

  async mounted() {
    this.registerProjs();

    // 在初始化阶段阻止自动保存（避免把占位符写回 localStorage）
    this.preventAutoSave = true;

    // 尝试从 localStorage 恢复（优先）
    try {
      this._loadStateForLocation(this.location);
    } catch (e) {
      console.warn("infoE: initial load error", e);
    }

    // 首次刷新：加载 waterpoints，计算 shipsCount，并尝试基于 location 填充 waterLevel/coords
    try {
      await this.refreshAll();
    } catch (e) {
      console.warn("infoE: refreshAll error", e);
    }

    // 再次尝试从缓存恢复（防止 refreshAll 覆盖）
    try {
      this._loadStateForLocation(this.location);
    } catch (e) {
      /* ignore */
    }

    // 允许保存
    this.preventAutoSave = false;

    // 周期刷新（仅当你希望定期更新时）
    if (this.refreshIntervalMs > 0) {
      this.refreshTimer = setInterval(() => {
        this.refreshAll().catch((err) =>
          console.warn("infoE periodic refresh error", err)
        );
      }, this.refreshIntervalMs);
    }
  },

  beforeUnmount() {
    if (this.refreshTimer) {
      clearInterval(this.refreshTimer);
      this.refreshTimer = null;
    }
  },

  watch: {
    // 当 location 改变，尝试恢复缓存并通知父组件
    location(newVal, oldVal) {
      if (!newVal) return;
      try {
        this._loadStateForLocation(newVal);
      } catch (e) {
        console.warn("infoE.watch.location load error", e);
      }
      this.$emit && this.$emit("update:location", newVal);
    },

    // 当 selectedCoordinates 改变，通知父组件并保存
    selectedCoordinates: {
      handler(newVal) {
        if (newVal && Array.isArray(newVal) && newVal.length >= 2) {
          this.$emit && this.$emit("update:selectedCoordinates", newVal);
        }
        this._saveStateForLocation();
      },
      deep: true,
    },

    // 保存显示字段，避免刷新后丢失（但是 _saveState 会受 preventAutoSave 控制）
    waterLevel() {
      this._saveStateForLocation();
    },
    weather() {
      this._saveStateForLocation();
    },
    windSpeed() {
      this._saveStateForLocation();
    },
    visibility() {
      this._saveStateForLocation();
    },
  },

  methods: {
    // ---------------- localStorage helpers ----------------
    _readStorageMap() {
      try {
        const raw = localStorage.getItem(STORAGE_MAP_KEY);
        if (!raw) return {};
        return JSON.parse(raw) || {};
      } catch (e) {
        console.warn("infoE: read storage failed", e);
        return {};
      }
    },

    _writeStorageMap(mapObj) {
      try {
        localStorage.setItem(STORAGE_MAP_KEY, JSON.stringify(mapObj));
      } catch (e) {
        console.warn("infoE: write storage failed", e);
      }
    },

    _normalizeKey(loc) {
      if (!loc) return "";
      return String(loc).trim();
    },

    // 智能保存：如果 preventAutoSave 为 true，则跳过写入；
    // 如果所有字段都是占位符并且无 coords，也跳过（避免覆盖已有缓存）
    _saveStateForLocation() {
      try {
        if (this.preventAutoSave) return;
        const rawKey = this.location || "";
        const key = this._normalizeKey(rawKey);
        if (!key) return;

        const isPlaceholder = (v) =>
          v === null ||
          v === undefined ||
          String(v).trim() === "" ||
          String(v).trim() === "—";
        const allPlaceholders =
          isPlaceholder(this.weather) &&
          isPlaceholder(this.windSpeed) &&
          isPlaceholder(this.visibility) &&
          isPlaceholder(this.waterLevel);
        const noCoords =
          !this.selectedCoordinates ||
          !Array.isArray(this.selectedCoordinates) ||
          this.selectedCoordinates.length < 2;
        if (allPlaceholders && noCoords) return;

        const map = this._readStorageMap();
        map[key] = {
          weather: this.weather,
          windSpeed: this.windSpeed,
          visibility: this.visibility,
          waterLevel: this.waterLevel,
          selectedCoordinates: this.selectedCoordinates || null,
          updatedAt: Date.now(),
        };
        this._writeStorageMap(map);
      } catch (e) {
        console.warn("infoE._saveStateForLocation error", e);
      }
    },

    _loadStateForLocation(loc) {
      try {
        const key = this._normalizeKey(loc);
        if (!key) return false;
        const map = this._readStorageMap();
        const entry = map[key];
        if (!entry) return false;

        if (entry.weather !== undefined) this.weather = entry.weather;
        if (entry.windSpeed !== undefined) this.windSpeed = entry.windSpeed;
        if (entry.visibility !== undefined) this.visibility = entry.visibility;
        if (entry.waterLevel !== undefined) this.waterLevel = entry.waterLevel;
        if (entry.selectedCoordinates)
          this.selectedCoordinates = entry.selectedCoordinates;

        // emit to parent so other cards can react
        if (
          this.selectedCoordinates &&
          Array.isArray(this.selectedCoordinates)
        ) {
          this.$emit &&
            this.$emit("update:selectedCoordinates", this.selectedCoordinates);
        }
        if (this.location) {
          this.$emit && this.$emit("update:location", this.location);
        }

        return true;
      } catch (e) {
        console.warn("infoE._loadStateForLocation error", e);
        return false;
      }
    },

    // ---------------- main refresh flow ----------------
    async refreshAll() {
      // update ships count (keeps original behavior)
      try {
        await this.updateShipsCount();
      } catch (e) {
        console.warn(e);
      }

      // load waterpoints (index them)
      try {
        await this.initWaterPoints();
      } catch (e) {
        console.warn(e);
      }

      // try to auto-fill waterLevel/coords from the index (but don't auto-fetch weather here)
      try {
        await this.tryAutoFillFromLocation(this.location, false);
      } catch (e) {
        console.warn(e);
      }
    },

    // ---------------- ships count (kept) ----------------
    _snippet(x) {
      try {
        if (typeof x === "string")
          return x.length > TRUNCATE_LEN
            ? x.slice(0, TRUNCATE_LEN) + "…(truncated)"
            : x;
        if (Array.isArray(x)) return JSON.stringify(x.slice(0, 200));
        const s = JSON.stringify(x);
        return s.length > TRUNCATE_LEN
          ? s.slice(0, TRUNCATE_LEN) + "…(truncated)"
          : s;
      } catch (e) {
        return String(x).slice(0, TRUNCATE_LEN);
      }
    },

    async fetchSectionCount(sectionCode) {
      const url = "/api/vessels/section";
      const params = {
        "getTracksBySectionRequest.sectionCode": sectionCode,
        "getTracksBySectionRequest.hectomFrom": "",
        "getTracksBySectionRequest.hectomTo": "",
      };
      this.debugResponses[sectionCode] = {
        ok: false,
        status: null,
        dataSnippet: null,
      };

      try {
        const resp = await axios.get(url, {
          params,
          headers: { Accept: "application/json" },
          timeout: 15000,
        });
        const data = resp.data;
        this.debugResponses[sectionCode].status = resp.status;
        if (Array.isArray(data)) {
          this.debugResponses[sectionCode].dataSnippet = this._snippet(data);
          return data.length;
        }
        if (Array.isArray(data.tracks)) {
          this.debugResponses[sectionCode].dataSnippet = this._snippet(
            data.tracks
          );
          return data.tracks.length;
        }
        if (Array.isArray(data.items)) {
          this.debugResponses[sectionCode].dataSnippet = this._snippet(
            data.items
          );
          return data.items.length;
        }
        if (typeof data.total === "number") return data.total;
        if (typeof data.count === "number") return data.count;
        // fallback: find first array field
        const arr = Object.values(data).find((v) => Array.isArray(v));
        if (arr) return arr.length;
        return 0;
      } catch (err) {
        if (err && err.response) {
          this.debugResponses[sectionCode].status = err.response.status;
          this.debugResponses[sectionCode].dataSnippet = this._snippet(
            err.response.data
          );
        } else {
          this.debugResponses[sectionCode].dataSnippet = String(
            err && err.message ? err.message : err
          );
        }
        return 0;
      }
    },

    async updateShipsCount() {
      try {
        const counts = await Promise.all(
          this.vesselSectionCodes.map((c) => this.fetchSectionCount(c))
        );
        this.shipsCount = counts.reduce((a, b) => a + Number(b || 0), 0);
      } catch (e) {
        console.warn("infoE.updateShipsCount error", e);
      }
    },

    // ---------------- projections & waterpoints ----------------
    registerProjs() {
      try {
        proj4.defs(
          "EPSG:25831",
          "+proj=utm +zone=31 +ellps=GRS80 +units=m +no_defs"
        );
        register(proj4);
      } catch (err) {
        // ignore if already registered
      }
    },

    async initWaterPoints() {
      try {
        const resp = await fetch(
          "/api/waterinfo/api/point/latestmeasurement?parameterId=waterhoogte"
        );
        const geojson = await resp.json();
        if (!geojson || !Array.isArray(geojson.features)) {
          console.warn("infoE.initWaterPoints: invalid response", geojson);
          return;
        }
        this.geoFeatures = geojson.features;
        this.buildIndex(this.geoFeatures);
        this.$emit && this.$emit("waterpoints-loaded", this.geoFeatures);
      } catch (e) {
        console.error("infoE.initWaterPoints error", e);
        throw e;
      }
    },

    buildIndex(features) {
      this.featuresIndexByName = {};
      for (const f of features) {
        const nameRaw = f?.properties?.name ?? "";
        const name = String(nameRaw).trim();
        const key = name.toLowerCase();
        if (!this.featuresIndexByName[key]) this.featuresIndexByName[key] = [];
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
          const meas = this.extractMeasurementText(f.properties);
          const coords = (f.geometry && [...f.geometry.coordinates]) || null;
          matches.push({
            value: name,
            name,
            measurement: meas,
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
        // try index fallback
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
        console.warn("infoE.handleSelect: not found", selected);
        return;
      }
      await this.applySelection(found);
    },

    async applySelection(item) {
      // prevent auto save while selecting to avoid blank overwrite
      this.preventAutoSave = true;

      this.location = item.name;
      this.waterLevel = item.measurement || "—";
      this.selectedCoordinates = item.coords;
      this.selectedFeature = item._feature || null;

      if (this.selectedCoordinates && Array.isArray(this.selectedCoordinates)) {
        this.$emit &&
          this.$emit("update:selectedCoordinates", this.selectedCoordinates);
      }
      if (this.location) {
        this.$emit && this.$emit("update:location", this.location);
      }
      this.$emit && this.$emit("selected-feature", this.selectedFeature);

      // try load cached weather/wind/visibility for this location
      const restored = this._loadStateForLocation(this.location);
      if (!restored) {
        // if no cache: try to fetch weather immediately (if we have usable coords)
        try {
          if (
            this.selectedCoordinates &&
            Array.isArray(this.selectedCoordinates) &&
            this.selectedCoordinates.length >= 2
          ) {
            await this._maybeFetchWeatherForCoords(this.selectedCoordinates);
          }
        } catch (e) {
          console.warn("infoE.applySelection fetchWeather error", e);
        }
      }

      // allow saves again and persist current state
      this.preventAutoSave = false;
      this._saveStateForLocation();
    },

    extractMeasurementText(properties) {
      if (!properties) return null;
      const meas = properties.measurements;
      if (
        meas &&
        typeof meas === "object" &&
        !Array.isArray(meas) &&
        ("latestValue" in meas || "value" in meas)
      ) {
        const val = meas.latestValue ?? meas.value ?? null;
        const unit = meas.unitCode ?? meas.unit ?? "";
        if (val === null || val === undefined) return null;
        return `${val}${unit ? " " + unit : ""}`;
      }
      if (Array.isArray(meas) && meas.length) {
        let found = meas.find((m) => m && "latestValue" in m);
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

    // tryAutoFillFromLocation: 基于已有索引填充 waterLevel/coords；可选择是否同时 fetch weather（默认 false）
    async tryAutoFillFromLocation(
      locationName,
      fetchWeather = false,
      forceUpdate = false
    ) {
      if (!locationName) return;
      const key = String(locationName).trim().toLowerCase();
      const arr = this.featuresIndexByName[key];
      if (arr && arr.length) {
        const f = arr[0];
        const measText = this.extractMeasurementText(f.properties);
        if (forceUpdate || !this.waterLevel || this.waterLevel === "—") {
          this.waterLevel = measText || "—";
        }
        this.selectedFeature = f;
        this.selectedCoordinates =
          (f.geometry && [...f.geometry.coordinates]) || null;

        if (
          this.selectedCoordinates &&
          Array.isArray(this.selectedCoordinates)
        ) {
          this.$emit &&
            this.$emit("update:selectedCoordinates", this.selectedCoordinates);
        }
        this.$emit && this.$emit("update:location", this.location);
        this.$emit && this.$emit("selected-feature", this.selectedFeature);

        // 先尝试从缓存恢复天气等
        const restored = this._loadStateForLocation(this.location);
        if (!restored && fetchWeather) {
          // 如果没有缓存但需要 fetch，则请求天气
          await this._maybeFetchWeatherForCoords(
            this.selectedCoordinates
          ).catch((e) => {
            console.warn("infoE.tryAutoFillFromLocation.fetchWeather error", e);
          });
        }
      } else {
        // if not found in index, still try to load cached state
        this._loadStateForLocation(locationName);
      }
    },

    // 判断 coords 是否是 lon/lat（范围判断）；若不是则尝试按 EPSG:25831 转换到 4326
    _coordsLookLikeLonLat(coords) {
      if (!coords || !Array.isArray(coords) || coords.length < 2) return false;
      const lon = Number(coords[0]),
        lat = Number(coords[1]);
      if (!isFinite(lon) || !isFinite(lat)) return false;
      return lon >= -180 && lon <= 180 && lat >= -90 && lat <= 90;
    },

    async _maybeFetchWeatherForCoords(coords) {
      if (!coords || !Array.isArray(coords) || coords.length < 2) return;
      let lon = Number(coords[0]),
        lat = Number(coords[1]);
      if (!this._coordsLookLikeLonLat(coords)) {
        try {
          const t = transform(
            [coords[0], coords[1]],
            "EPSG:25831",
            "EPSG:4326"
          );
          lon = Number(t[0]);
          lat = Number(t[1]);
        } catch (e) {
          console.warn(
            "infoE._maybeFetchWeatherForCoords transform failed, skip weather",
            e
          );
          return;
        }
      }

      try {
        // 请求同时包含 current_weather 和 hourly.visibility
        const url = `https://api.open-meteo.com/v1/forecast?latitude=${encodeURIComponent(
          lat
        )}&longitude=${encodeURIComponent(
          lon
        )}&current_weather=true&hourly=visibility&timezone=Europe%2FAmsterdam`;
        const res = await axios.get(url);
        const data = res.data || {};
        // debug：若看不到 visibility，把整个响应打印一次（上线可以注释）
        // console.debug("open-meteo response:", data);

        const current = data.current_weather || data.current || null;
        if (!current) {
          this.weather = "—";
          this.windSpeed = "—";
          this.visibility = "—";
          this._saveStateForLocation();
          return;
        }

        // 填 temperature / wind
        const temp = current.temperature;
        this.weather = temp !== undefined && temp !== null ? `${temp}°C` : "—";
        const wsp = current.windspeed ?? current.wind_speed ?? null;
        this.windSpeed = wsp !== undefined && wsp !== null ? `${wsp} m/s` : "—";

        // 1) 尝试从 hourly.visibility（以 current_weather.time 为基准）获取
        let vis = null;
        try {
          const hourly = data.hourly;
          if (
            hourly &&
            Array.isArray(hourly.time) &&
            Array.isArray(hourly.visibility) &&
            current.time
          ) {
            const idx = hourly.time.indexOf(current.time);
            if (idx >= 0 && hourly.visibility[idx] !== undefined) {
              vis = hourly.visibility[idx];
            }
          }
        } catch (e) {
          // ignore
        }

        // 2) fallback：检查 data.current.visibility / data.visibility
        if (vis === null || vis === undefined) {
          vis = data?.current?.visibility ?? data?.visibility ?? null;
        }

        // 显示格式（如果是米, 超过1000m显示为 km）
        if (vis !== null && vis !== undefined) {
          const num = Number(vis);
          if (!isNaN(num)) {
            if (num >= 1000) {
              this.visibility = `${(num / 1000).toFixed(1)} km`;
            } else {
              this.visibility = `${Math.round(num)} m`;
            }
          } else {
            this.visibility = String(vis);
          }
        } else {
          this.visibility = "—";
        }

        // 保存
        this._saveStateForLocation();
      } catch (e) {
        console.error("infoE._maybeFetchWeatherForCoords error", e);
        this._saveStateForLocation();
      }
    },
  },
};
</script>

<style>
/* 容器样式：使用 CSS Grid，6 列等分 */
.info-container {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 0.5fr 0.5fr 0.5fr 0.1fr;
  column-gap: 8px;
  justify-items: center;
  align-items: center;
  width: 100%;
  height: 60px;
  padding: 0 10px;
  background-color: rgba(255, 255, 255, 0.75);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.info-box {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 250px;
  height: 50%;
  box-sizing: border-box;
  padding: 0;
  font-size: 0.75em;
  opacity: 0.8;
}

.info-box.location {
  font-weight: bold;
  justify-content: center;
  border-color: transparent !important;
}

.info-box.small {
  padding: 0;
  font-size: 0.75em;
  width: 200px;
}

.info-box .el-card__body {
  padding: 0 !important;
}

.info-content {
  display: grid;
  width: 100%;
  height: 100%;
  align-items: center;
}

.info-content.ships {
  grid-template-columns: 1.5fr 1.5fr;
}
.info-content.water-level {
  grid-template-columns: 1fr 1.5fr;
}
.info-content.weather {
  grid-template-columns: 1fr 2fr;
}
.info-content.wind-speed {
  grid-template-columns: 1.5fr 1.5fr;
}
.info-content.visibility {
  grid-template-columns: 1fr 2fr;
}

.label {
  font-weight: bold;
  text-align: left;
  padding-left: 12px;
  padding-right: 8px;
  border-right: 1px solid #ccc;
}
.value {
  text-align: center;
}

.waterpoint-autocomplete-popper .el-autocomplete-suggestion__wrap {
  max-height: 320px;
  overflow: auto;
}
</style>
