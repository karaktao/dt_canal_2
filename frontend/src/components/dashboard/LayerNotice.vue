<template>
  <!-- renderless: 不渲染 UI -->
  <div style="display: none"></div>
</template>

<script setup>
/*
  LayerNotice.vue - visualization updated:
   - feature style uses icon based on limitationCode (icons placed in /public/icons)
   - no text label shown on map
   - features still store parsed data in feature.get('data')
*/

import { ref, onBeforeUnmount } from "vue";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import Feature from "ol/Feature";
import { Point } from "ol/geom";
import { Style, Fill, Stroke, Icon, Circle as CircleStyle } from "ol/style";
import { fromLonLat } from "ol/proj";

const emit = defineEmits(["map-layer-ready", "feature-clicked", "fetch-error"]);

const props = defineProps({
  soapUrl: { type: String, default: "/api/nts40" },
  soapBody: {
    type: String,
    default: `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ns="http://www.ris.eu/nts.ms/2.0.4.0">
   <soapenv:Header/>
   <soapenv:Body>
      <ns:get_messages_query>
         <ns:message_type>FTM</ns:message_type>
      </ns:get_messages_query>
   </soapenv:Body>
</soapenv:Envelope>`,
  },
  fallbackToCenter: { type: Boolean, default: false },
  refreshIntervalMs: { type: Number, default: 0 },
  fetchTimeoutMs: { type: Number, default: 15000 },
});

// icon map (public/icons/*)
const ICON_MAP = {
  DELAY: {
    icon: "/icons/delay.svg",
    label: "Delay",
    color: "#f97316",
    aria: "Delay / possible delay",
  },
  OBSTRU: {
    icon: "/icons/obstru.svg",
    label: "Obstruction",
    color: "#ef4444",
    aria: "Obstruction / blocked",
  },
  NOSERV: {
    icon: "/icons/noserv.svg",
    label: "No service",
    color: "#6b7280",
    aria: "No service / unavailable",
  },
  SERVIC: {
    icon: "/icons/service.svg",
    label: "Service",
    color: "#10b981",
    aria: "Service / available",
  },

  // 下面是可能在数据中出现的其它 limitation_code（已补齐）
  CAUTION: {
    icon: "/icons/caution.svg",
    label: "Caution",
    color: "#fde68a",
    aria: "Caution / navigate with care",
  },
  WAVWAS: {
    icon: "/icons/wavwas.svg",
    label: "Wave wash",
    color: "#3b82f6",
    aria: "Wave wash / wave effect",
  },
  PASSIN: {
    icon: "/icons/passin.svg",
    label: "Passing",
    color: "#60a5fa",
    aria: "Passing information",
  },
  OVRTAK: {
    icon: "/icons/ovrtak.svg",
    label: "No overtaking",
    color: "#f43f5e",
    aria: "Overtaking prohibited",
  },

  // fallback / 默认
  DEFAULT: {
    icon: "/icons/default.svg",
    label: "Info",
    color: "#6b7280",
    aria: "Information",
  },
};

const noticeSource = new VectorSource();
const noticeLayer = new VectorLayer({
  source: noticeSource,
  zIndex: 120,
  style: (feature) => {
  // 1) 读取并规范化 key（保证大写、去空白）
  let lim = feature.get("limitationCode");
  if (lim === undefined || lim === null) lim = "";
  lim = String(lim).trim().toUpperCase();

  // 2) 如果取到空的或占位符，再尝试其它字段（保险）
  if (!lim || lim === "___|EMPTY|___") {
    // 尝试读取 data.ftm.reason_code（以防创建时未设置）
    const data = feature.get("data");
    const rc = data?.ftm?.reason_code;
    if (rc) lim = String(rc).trim().toUpperCase();
  }

  // 3) 最终 fallback 为 DEFAULT
  if (!lim) lim = "DEFAULT";

  const entry = ICON_MAP[lim] || ICON_MAP["DEFAULT"];

  // 4) 返回对应图标 style（或回退圆点）
  if (entry && entry.icon) {
    return new Style({
      image: new Icon({
        src: entry.icon,
        scale: entry.scale || 0.6,
        anchor: entry.anchor || [0.5, 0.5],
        anchorXUnits: "fraction",
        anchorYUnits: "fraction",
      }),
    });
  }

  // fallback circle (should rarely happen)
  return new Style({
    image: new CircleStyle({
      radius: 8,
      fill: new Fill({ color: entry?.color || "rgba(30,144,255,0.95)" }),
      stroke: new Stroke({ color: "#fff", width: 2 }),
    }),
  });
}

});
noticeLayer.set("name", "notice");

let mapRef = null;
let refreshTimer = null;

// ------------ helper functions (same as before) ------------
function parseLatLonString(s) {
  if (!s || typeof s !== "string") return null;
  const str = s.trim().replace(/\s+/g, " ");
  const re = /([0-9]{1,3})\s+([0-9]+(?:\.[0-9]+)?)\s*([NnSsEeWw])/;
  const m = str.match(re);
  if (m) {
    const deg = parseFloat(m[1]);
    const min = parseFloat(m[2]);
    const hemi = m[3].toUpperCase();
    let dec = deg + min / 60.0;
    if (hemi === "S" || hemi === "W") dec = -dec;
    return dec;
  }
  const re2 = /([-+]?[0-9]+(?:\.[0-9]+)?)\s*([NnSsEeWw])?/;
  const m2 = str.match(re2);
  if (m2) {
    let val = parseFloat(m2[1]);
    if (!Number.isFinite(val)) return null;
    const hemi = (m2[2] || "").toUpperCase();
    if (hemi === "S" || hemi === "W") val = -Math.abs(val);
    return val;
  }
  return null;
}

function textFrom(node, localTagName) {
  if (!node) return null;
  const byTag = node.getElementsByTagName(localTagName);
  if (byTag && byTag.length > 0) return byTag[0].textContent?.trim() ?? null;
  const elems = node.getElementsByTagName("*");
  for (let i = 0; i < elems.length; i++) {
    const e = elems[i];
    if (e.localName === localTagName) return e.textContent?.trim() ?? null;
  }
  return null;
}

function findElementByLocalName(root, localName) {
  if (!root) return null;
  const all = root.getElementsByTagName("*");
  for (let i = 0; i < all.length; i++) {
    const el = all[i];
    if (el.localName === localName) return el;
  }
  return null;
}

function parseResultMessage(node) {
  if (!node) return null;

  const identification = {
    from: textFrom(node, "from"),
    originator: textFrom(node, "originator"),
    country_code: textFrom(node, "country_code"),
    language_code: textFrom(node, "language_code"),
    district: textFrom(node, "district"),
    date_issue: textFrom(node, "date_issue"),
  };

  const ftm = {
    organisation: textFrom(node, "organisation"),
    year: textFrom(node, "year"),
    number: textFrom(node, "number"),
    serial_number: textFrom(node, "serial_number"),
    subject_code: textFrom(node, "subject_code"),
    date_start: textFrom(node, "date_start"),
    date_end: textFrom(node, "date_end"),
    contents: textFrom(node, "contents"),
    source: textFrom(node, "source"),
    reason_code: textFrom(node, "reason_code"),
  };

  let geo = null;
  const geoNode = findElementByLocalName(node, "geo_object");
  if (geoNode) {
    const coordNode =
      findElementByLocalName(geoNode, "coordinate") ||
      findElementByLocalName(geoNode, "coordinates");
    const latStr =
      (coordNode && textFrom(coordNode, "lat")) ||
      textFrom(geoNode, "lat") ||
      (coordNode && textFrom(coordNode, "latitude")) ||
      textFrom(geoNode, "latitude") ||
      null;
    const longStr =
      (coordNode &&
        (textFrom(coordNode, "long") ||
          textFrom(coordNode, "lon") ||
          textFrom(coordNode, "longitude"))) ||
      textFrom(geoNode, "long") ||
      textFrom(geoNode, "lon") ||
      textFrom(geoNode, "longitude") ||
      null;

    geo = {
      id: textFrom(geoNode, "id"),
      name: textFrom(geoNode, "name"),
      type_code: textFrom(geoNode, "type_code"),
      fairway_name: textFrom(geoNode, "fairway_name"),
      lat_str: latStr,
      long_str: longStr,
      lat: latStr ? parseLatLonString(latStr) : null,
      lon: longStr ? parseLatLonString(longStr) : null,
    };
  } else {
    const anyCoord =
      findElementByLocalName(node, "coordinate") ||
      findElementByLocalName(node, "coordinates");
    if (anyCoord) {
      const latStr =
        textFrom(anyCoord, "lat") || textFrom(anyCoord, "latitude");
      const longStr =
        textFrom(anyCoord, "long") ||
        textFrom(anyCoord, "lon") ||
        textFrom(anyCoord, "longitude");
      geo = {
        id: null,
        name: null,
        type_code: null,
        fairway_name: null,
        lat_str: latStr,
        long_str: longStr,
        lat: latStr ? parseLatLonString(latStr) : null,
        lon: longStr ? parseLatLonString(longStr) : null,
      };
    }
  }

  const limitationCode =
    textFrom(node, "limitation_code") || textFrom(node, "limitation");

  return { identification, ftm, geo, limitationCode };
}

// core fetch & render (keeps previous structure)
async function doFetchAndRender(map) {
  try {
    const controller = new AbortController();
    const timer = setTimeout(
      () => controller.abort(),
      props.fetchTimeoutMs || 15000
    );

    let resp;
    try {
      resp = await fetch(props.soapUrl, {
        method: "POST",
        headers: {
          "Content-Type": "text/xml;charset=UTF-8",
          SOAPAction: "http://www.ris.eu/nts.ms/get_message",
          Accept: "text/xml, application/xml",
        },
        body: props.soapBody,
        signal: controller.signal,
      });
    } catch (err) {
      clearTimeout(timer);
      emit("fetch-error", err);
      console.error("LayerNotice fetch error", err);
      return;
    }
    clearTimeout(timer);

    if (!resp.ok) {
      emit("fetch-error", new Error(`HTTP ${resp.status}`));
      console.warn("LayerNotice SOAP 请求失败", resp.status, resp.statusText);
      return;
    }

    const text = await resp.text();
    const parser = new DOMParser();
    const doc = parser.parseFromString(text, "text/xml");

    let nodes = Array.from(doc.getElementsByTagName("result_message"));
    if (!nodes.length) {
      const all = Array.from(doc.getElementsByTagName("*"));
      nodes = all.filter(
        (n) => n.localName === "result_message" || n.localName === "result"
      );
    }
    if (!nodes.length) {
      const ftmNodes = Array.from(doc.getElementsByTagName("ftm")).concat(
        Array.from(doc.getElementsByTagNameNS("*", "ftm"))
      );
      if (ftmNodes.length) nodes = ftmNodes;
    }

    const items = [];
    for (let i = 0; i < nodes.length; i++) {
      const parsed = parseResultMessage(nodes[i]);
      if (parsed) items.push(parsed);
    }

    console.log("LayerNotice: total parsed items:", items.length);
    const withCoords = items.filter(
      (it) =>
        it.geo && Number.isFinite(it.geo.lon) && Number.isFinite(it.geo.lat)
    );
    console.log("LayerNotice: items with coords:", withCoords.length);
    if (withCoords.length) {
      console.log(
        "LayerNotice: sample coords:",
        withCoords
          .slice(0, 6)
          .map((it) => ({
            name: it.geo?.name,
            lat: it.geo?.lat,
            lon: it.geo?.lon,
          }))
      );
    } else {
      console.log("LayerNotice: some parsed items sample:", items.slice(0, 5));
      // print first node HTML for debugging
      if (nodes.length) {
        console.log(
          "LayerNotice: sample node outerHTML:",
          nodes[0].outerHTML.slice(0, 800)
        );
      }
    }

    const view = map.getView();
    const center = view.getCenter ? view.getCenter() : [0, 0];

    const features = [];
    for (let i = 0; i < items.length; i++) {
      const item = items[i];
      let coord = null;
      if (
        item.geo &&
        Number.isFinite(item.geo.lon) &&
        Number.isFinite(item.geo.lat)
      ) {
        try {
          coord = fromLonLat([Number(item.geo.lon), Number(item.geo.lat)]);
        } catch (e) {
          coord = null;
        }
      }
      if (!coord) {
        if (props.fallbackToCenter) {
          const dx = ((i % 10) - 5) * 1200;
          const dy =
            (Math.floor(i / 10) - Math.floor(items.length / 20)) * 1200;
          coord = [center[0] + dx, center[1] + dy];
        } else {
          continue;
        }
      }
      const pt = new Point(coord);
      const f = new Feature({ geometry: pt });
      f.set("data", item);

      // 规范化 limitation code：优先 item.limitationCode，再尝试 ftm.reason_code。
      // 处理占位符 (例如 "___|empty|___")、去除空白、转为大写。
      // 如果最终为空或只含占位符，设为 'DEFAULT'。
      (function setLim() {
        let raw = null;
        if (item.limitationCode !== undefined && item.limitationCode !== null)
          raw = String(item.limitationCode);
        else if (
          item.ftm &&
          item.ftm.reason_code !== undefined &&
          item.ftm.reason_code !== null
        )
          raw = String(item.ftm.reason_code);
        else raw = "";

        // 常见占位符处理（你在示例中有 "___|empty|___"）
        raw = raw.replace(/___\|empty\|___/gi, "").trim();

        // 统一大写，便于映射键匹配
        const key = raw ? raw.toUpperCase() : "DEFAULT";
        f.set("limitationCode", key);
      })();
      features.push(f);
    }

    noticeSource.clear();
    if (features.length) noticeSource.addFeatures(features);

    emit("map-layer-ready", noticeLayer);
    // console.log("LayerNotice: 添加 features:", features.length);
  } catch (err) {
    console.error("LayerNotice: 请求或解析出错", err);
    emit("fetch-error", err);
  }
}

async function attachMapEvents(map) {
  if (!map) throw new Error("attachMapEvents requires a map instance");
  mapRef = map;
  await doFetchAndRender(mapRef);

  if (props.refreshIntervalMs && props.refreshIntervalMs > 0) {
    if (refreshTimer) clearInterval(refreshTimer);
    refreshTimer = setInterval(() => {
      doFetchAndRender(mapRef).catch((e) =>
        console.warn("LayerNotice refresh error", e)
      );
    }, props.refreshIntervalMs);
  }
}

function refresh() {
  if (!mapRef) return;
  return doFetchAndRender(mapRef);
}

function destroy() {
  if (refreshTimer) {
    clearInterval(refreshTimer);
    refreshTimer = null;
  }
  noticeSource.clear();
  mapRef = null;
}

onBeforeUnmount(() => {
  if (refreshTimer) clearInterval(refreshTimer);
  noticeSource.clear();
});

defineExpose({
  attachMapEvents,
  getLayer: () => noticeLayer,
  refresh,
  destroy,
  getFeatures: () => noticeSource.getFeatures(),
});
</script>
