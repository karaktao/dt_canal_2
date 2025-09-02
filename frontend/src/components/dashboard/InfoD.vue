<template>
  <div class="info-boxC">
    <h3 class="title">Weathercode → 图标 & 说明</h3>

    <!-- 可传 isDay 控制 day/night (默认 true) -->
    <div class="legend-list" role="list">
      <div
        class="legend-item"
        v-for="item in weatherList"
        :key="item.code"
        :title="`${item.code} — ${item.en} / ${item.cn}`"
        role="listitem"
        aria-label="weather-code-item"
      >
        <div class="icon-wrap" aria-hidden="true">
          <!-- 标准非自闭合写法；getIconClass 会返回 ['wi', 'wi-xxx'] -->
          <i :class="getIconClass(item.icon)"></i>
        </div>

        <div class="text-wrap">
          <div class="code">{{ item.code }}</div>
          <div class="desc-cn">{{ item.cn }}</div>
          <div class="desc-en">{{ item.en }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "WeatherCodeLegend",
  props: {
    // 控制是否显示白天图标；如果你有 sunrise/sunset，也可以传入计算后的布尔值
    isDay: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      weatherList: [
        { code: 0,  icon: "day-sunny",            cn: "晴朗",                         en: "Clear sky" },
        { code: 1,  icon: "day-sunny-overcast",   cn: "主要晴/少云",                  en: "Mainly clear / partly cloudy" },
        { code: 2,  icon: "day-cloudy",          cn: "多云/局部多云",                  en: "Partly cloudy" },
        { code: 3,  icon: "cloudy",              cn: "阴天/多云",                      en: "Overcast" },
        { code: 45, icon: "fog",                 cn: "雾",                            en: "Fog" },
        { code: 48, icon: "fog",                 cn: "凝霜雾",                        en: "Depositing rime fog" },

        { code: 51, icon: "sprinkle",            cn: "细雨/毛毛雨（小）",              en: "Drizzle: light" },
        { code: 53, icon: "sprinkle",            cn: "细雨（中）",                     en: "Drizzle: moderate" },
        { code: 55, icon: "sprinkle",            cn: "细雨（浓）",                     en: "Drizzle: dense" },
        { code: 56, icon: "sleet",               cn: "冻雨（小）",                     en: "Freezing drizzle: light" },
        { code: 57, icon: "sleet",               cn: "冻雨（重）",                     en: "Freezing drizzle: dense" },

        { code: 61, icon: "rain",                cn: "小雨",                          en: "Rain: slight" },
        { code: 63, icon: "rain",                cn: "中雨",                          en: "Rain: moderate" },
        { code: 65, icon: "rain",                cn: "大雨",                          en: "Rain: heavy" },
        { code: 66, icon: "rain-mix",            cn: "冻雨/雨雪混合（小）",             en: "Freezing rain: light" },
        { code: 67, icon: "rain-mix",            cn: "冻雨/雨雪混合（重）",             en: "Freezing rain: heavy" },

        { code: 71, icon: "snow",                cn: "小雪",                          en: "Snow fall: slight" },
        { code: 73, icon: "snow",                cn: "中雪",                          en: "Snow fall: moderate" },
        { code: 75, icon: "snow",                cn: "大雪",                          en: "Snow fall: heavy" },
        { code: 77, icon: "snow-wind",           cn: "雪粒/雪屑",                      en: "Snow grains" },

        { code: 80, icon: "showers",             cn: "阵雨（小）",                     en: "Rain showers: slight" },
        { code: 81, icon: "showers",             cn: "阵雨（中）",                     en: "Rain showers: moderate" },
        { code: 82, icon: "showers",             cn: "阵雨（强）",                     en: "Rain showers: violent" },

        { code: 85, icon: "snow",                cn: "阵雪（小）",                     en: "Snow showers: slight" },
        { code: 86, icon: "snow",                cn: "阵雪（重）",                     en: "Snow showers: heavy" },

        { code: 95, icon: "thunderstorm",        cn: "雷暴（小/中）",                  en: "Thunderstorm: moderate" },
        { code: 96, icon: "thunderstorm",        cn: "雷暴伴轻冰雹",                    en: "Thunderstorm with slight hail" },
        { code: 99, icon: "thunderstorm",        cn: "雷暴伴重冰雹",                    en: "Thunderstorm with heavy hail" }
      ]
    };
  },
  methods: {
    /**
     * 根据传入的 icon 名和 isDay 返回最终的 class 数组
     * - 支持把 "day-xxx" 切换为 "night-xxx"（若 isDay=false）
     * - 若 icon 本身没有 day/night 前缀，则保持不变
     */
    getIconClass(iconName) {
      if (!iconName) return ['wi', 'wi-na'];
      let base = String(iconName);

      if (this.isDay) {
        // 如果用户强制 day，优先把 night- 替换为 day-（如果存在）
        base = base.replace(/^night-/, 'day-');
      } else {
        // 如果是夜间，优先把 day- 替换成 night-
        base = base.replace(/^day-/, 'night-');
      }
      return ['wi', `wi-${base}`];
    }
  }
};
</script>

<style scoped>
/* 直接沿用你之前的样式 */
.info-boxC {
  width: 400px;
  height: 500px;
  padding: 12px;
  background-color: rgba(255, 255, 255, 0.92);
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  border-radius: 8px;
  box-sizing: border-box;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.title { margin: 0 0 8px 0; font-size: 16px; font-weight: 600; color: #222; }
.legend-list { overflow-y: auto; padding-right: 6px; display: flex; flex-direction: column; gap: 8px; }
.legend-item { display:flex; align-items:center; gap:12px; padding:8px; border-radius:6px; transition:background-color .12s ease; }
.legend-item:hover { background: rgba(0,0,0,0.03); }
.icon-wrap { width:48px; height:48px; display:flex; align-items:center; justify-content:center; }
.icon-wrap .wi { font-size:28px; line-height:1; }
.text-wrap { display:flex; flex-direction:column; }
.code { font-weight:700; font-size:13px; color:#111; }
.desc-cn { font-size:12px; color:#333; }
.desc-en { font-size:11px; color:#666; }
.legend-list::-webkit-scrollbar { width: 8px; height: 8px; }
.legend-list::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.12); border-radius: 6px; }
</style>
