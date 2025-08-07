/*
 * New composable for managing OpenLayers layers based on selected items.
 *
 * This file centralises the logic of registering and unregistering map layers
 * when the user's selection changes. It accepts a reactive array of selected
 * layer names and a ref pointing to the OpenLayers `Map` instance. Whenever
 * the selection changes it ensures that layers which are no longer selected
 * are removed from the map, and that layers which remain selected have
 * their map events attached. Use this composable in your root map view
 * rather than manually writing `watch` logic for each individual layer.
 */

import { watch } from 'vue';

/**
 * Manage layer registration and deregistration against a map instance.
 *
 * @param {Ref<Array<string>>} selectedItems  A reactive array of layer names
 *        representing the current selection (e.g. ['waterLevel', 'berth']).
 * @param {Ref<ol/Map|null>} mapRef  A ref containing the OpenLayers map.
 * @returns {Object} An object exposing `register` and `unregister` functions.
 */
export function useMapLayers(selectedItems, mapRef) {
  // A map from layer name -> component instance. Each instance should expose
  // `getLayer()` returning an OpenLayers layer and `attachMapEvents(map)`.
  const registeredLayers = new Map();

  /**
   * Register a layer and add it to the map. If the layer is already
   * registered it does nothing.
   *
   * @param {string} layerName Name of the layer as used in `selectedItems`.
   * @param {Object} instance The component instance exposing `getLayer()` and
   *                          optionally `attachMapEvents(map)`.
   */
  function register(layerName, instance) {
    if (!layerName || !instance) return;
    if (registeredLayers.has(layerName)) return;
    const map = mapRef.value;
    if (!map) return;
    registeredLayers.set(layerName, instance);
    // Add layer to map if getLayer() is provided
    const layer = instance.getLayer?.();
    if (layer && !map.getLayers().getArray().includes(layer)) {
      map.addLayer(layer);
    }
    // Immediately attach any map events
    if (typeof instance.attachMapEvents === 'function') {
      instance.attachMapEvents(map);
    }
  }

  /**
   * Unregister a layer and remove it from the map.
   *
   * @param {string} layerName Name of the layer.
   */
  function unregister(layerName) {
    const map = mapRef.value;
    const instance = registeredLayers.get(layerName);
    if (!map || !instance) return;
    const layer = instance.getLayer?.();
    if (layer) {
      map.removeLayer(layer);
    }
    registeredLayers.delete(layerName);
  }

  // Watch the selected items. When deselected, remove layers; when selected,
  // ensure events are bound. Adding new instances is the responsibility of
  // callers via `register()`.
  watch(
    selectedItems,
    (newVal) => {
      const map = mapRef.value;
      if (!map) return;
      // Remove layers that are no longer selected
      for (const [name, instance] of registeredLayers.entries()) {
        if (!newVal.includes(name)) {
          const layer = instance.getLayer?.();
          if (layer) map.removeLayer(layer);
          registeredLayers.delete(name);
        }
      }
      // For selected layers, ensure events are bound
      newVal.forEach((name) => {
        const inst = registeredLayers.get(name);
        if (inst && typeof inst.attachMapEvents === 'function') {
          inst.attachMapEvents(map);
        }
      });
    },
    { immediate: true }
  );

  return { register, unregister, registeredLayers };
}