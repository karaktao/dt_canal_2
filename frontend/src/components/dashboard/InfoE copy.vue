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






import axios from "axios";
const TRUNCATE_LEN = 2000; // 控制在 debug 中打印多少字符 / 元素

export default {
  data() {
    return {
      location: "Eefde Lock",
      weather: "24°C",
      windSpeed: "15 km/h",
      visibility: "10 km",
      shipsCount: 0,
      waterLevel: "3 m",
      vesselSectionCodes: ["NL00081", "NL00082", "NL00652", "NL00650", "NL00647","NL01224","NL00642","NL00644","NL00646","NL01186" ], // 示例 section codes
      refreshIntervalMs: 0,
      refreshTimer: null,
      // 用于调试：保存每个 section 最近一次的响应摘要
      debugResponses: {}, // { [sectionCode]: { ok, status, contentType, responseURL, dataType, dataSnippet } }
    };
  },








  
// 获取船只数量的值
  mounted() {
    this.updateShipsCount();
    if (this.refreshIntervalMs > 0) {
      this.refreshTimer = setInterval(this.updateShipsCount, this.refreshIntervalMs);
    }
  },

  beforeUnmount() {
    if (this.refreshTimer) clearInterval(this.refreshTimer);
  },

  methods: {
    // 将对象或字符串截断为便于打印的片段
    _snippet(x) {
      try {
        if (typeof x === "string") return x.length > TRUNCATE_LEN ? x.slice(0, TRUNCATE_LEN) + "…(truncated)" : x;
        if (Array.isArray(x)) {
          const sample = x.slice(0, 200); // 取最多 200 条示例
          return JSON.stringify(sample);
        }
        return JSON.stringify(x).slice(0, TRUNCATE_LEN) + (JSON.stringify(x).length > TRUNCATE_LEN ? "…(truncated)" : "");
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

      // 初始化 debug slot
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

        // axios 在浏览器端，response.request 是 XHR；尝试获取实际请求的最终 URL（有助判断 proxy 重写）
        const responseURL = response.request && response.request.responseURL ? response.request.responseURL : null;

        const contentType = (response.headers && response.headers["content-type"]) || "";
        const data = response.data;

        // 更新 debug 基本信息
        this.debugResponses[sectionCode].status = response.status;
        this.debugResponses[sectionCode].contentType = contentType;
        this.debugResponses[sectionCode].responseURL = responseURL;

        // 如果返回的是 HTML（错误页 / 登录页 / 404 页面），尽快提示并保存片段
        if (typeof data === "string" && (data.trim().startsWith("<!DOCTYPE") || data.trim().startsWith("<html") || contentType.includes("text/html"))) {
          console.error(`${tag} !! Received HTML instead of JSON — likely wrong proxy/rewrite or route. response.status = ${response.status}`);
          this.debugResponses[sectionCode].dataType = "html";
          this.debugResponses[sectionCode].dataSnippet = this._snippet(data);

          return 0;
        }

        // 如果返回的是空的字符串或 null
        if (data === null || data === undefined) {
          this.debugResponses[sectionCode].dataType = "null";
          this.debugResponses[sectionCode].dataSnippet = "";
          return 0;
        }

        // 正常 JSON：尝试多种解析方式（优先 array）
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
          // 尝试找到第一个 array 字段
          const possibleArray = Object.values(data).find((v) => Array.isArray(v));
          if (possibleArray) {
            count = possibleArray.length;
            this.debugResponses[sectionCode].dataType = "first-array-field";
            this.debugResponses[sectionCode].dataSnippet = this._snippet(possibleArray);
          } else {
            // 兜底：不是我们想要的数组格式
            console.warn(`${tag} parse: unknown JSON shape — not an array or known wrapper`);
            this.debugResponses[sectionCode].dataType = "unknown-object";
            this.debugResponses[sectionCode].dataSnippet = this._snippet(data);
            // 如果你期望 array，这里返回 0
            count = 0;
          }
        }

        this.debugResponses[sectionCode].ok = true;
        return count;
      } catch (err) {
        // 如果有 err.response，可以保存更多信息
        if (err && err.response) {
          this.debugResponses[sectionCode].status = err.response.status;
          this.debugResponses[sectionCode].contentType = err.response.headers && err.response.headers["content-type"];
          const raw = err.response.data;
          this.debugResponses[sectionCode].dataType = typeof raw;
          this.debugResponses[sectionCode].dataSnippet = this._snippet(raw);
        }
        return 0;
      }
    },

    async updateShipsCount() {
      const tag = "[ships][update]";
      try {
        const counts = await Promise.all(this.vesselSectionCodes.map((c) => this.fetchSectionCount(c)));
        const total = counts.reduce((acc, n) => acc + Number(n || 0), 0);
        this.shipsCount = total;
      } catch (err) {
      }
    },

    refreshNow() {
      this.updateShipsCount();
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
