// src/stores/mapLayers.js
import { defineStore } from 'pinia'

export const useMapLayerStore = defineStore('mapLayers', {
  state: () => ({
    layers: {
      berth: true,
      bridge: true,
      lock: true,
      waterlevel: true,
    },
    infoPanel: {
      visible: false,
      data: null
    }
  }),
  actions: {
    toggleLayer(layerName) {
      this.layers[layerName] = !this.layers[layerName];
    },
    showInfoPanel(data) {
      this.infoPanel.visible = true;
      this.infoPanel.data = data;
    },
    hideInfoPanel() {
      this.infoPanel.visible = false;
      this.infoPanel.data = null;
    }
  }
})
