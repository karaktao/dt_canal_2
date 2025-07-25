<template>
  <transition name="fade-scale">
    <el-card
      v-if="visible"
      ref="panelRef" 
      class="info-panel"
      shadow="hover"
      @click.stop
    :style="{ maxHeight: panelHeight }"
    >

<!-- 拖拽条 -->
         <div
        class="resize-handle"
        @pointerdown.prevent="startDrag"     
      ></div>

      <!-- 头部：标题 + 关闭按钮 -->
      <template #header>
        <div class="header">
          <span class="title">{{ title }}</span>
          <el-button
            icon="el-icon-close"
            type="text"
            circle
            @click="$emit('close')"
            aria-label="关闭"
          />
        </div>
      </template>

      <!-- 主体内容：默认渲染 item 的字段，也可通过 slot 覆盖 -->
      <slot name="content">
        <p v-for="(val, key) in item" :key="key">
          <b>{{ key }}:</b> {{ val }}
        </p>
      </slot>

      <!-- 底部操作区：预留 slot -->
      <slot name="footer"></slot>
    </el-card>
  </transition>
</template>

<script setup>
import { ref, onBeforeUnmount } from 'vue';
import { ElButton, ElCard } from 'element-plus';

const props = defineProps({
  visible: Boolean,
  title: { type: String, default: '信息面板' },
  item: { type: Object, default: () => ({}) },
});


 // 面板高度可调节功能

const panelRef    = ref(null);               // ← 修改：新增 panelRef，用于获取 DOM
const panelHeight = ref('20vh');             // ← 修改：面板高度状态


let dragging = false;
let startY = 0;
let startH = 0; 
let frameId  = null; 
let panelEl  = null;  

function startDrag(e) {                     
  dragging = true;
  startY    = e.clientY;
  panelEl    = panelRef.value.$el; 
  startH     = panelEl.getBoundingClientRect().height;
  panelEl.classList.add('dragging');  
  document.body.style.userSelect = 'none'; 
  document.addEventListener('pointermove', onDrag); 
  document.addEventListener('pointerup',   stopDrag); 
}

function onDrag(e) {                         
  if (!dragging) return;
  const dy = startY - e.clientY;
  let newH = startH + dy;
  const min = 100, max = window.innerHeight - 100;
  newH = Math.min(Math.max(newH, min), max);
  panelHeight.value = newH + 'px';
}

function stopDrag() {                       
  dragging = false;
  document.body.style.userSelect = '';
  panelEl.classList.remove('dragging'); 
  document.removeEventListener('pointermove', onDrag);
  document.removeEventListener('pointerup',   stopDrag);
}

onBeforeUnmount(() => {                     
  document.removeEventListener('pointermove', onDrag);
  document.removeEventListener('pointerup',   stopDrag);
});


</script>

<style scoped>
.info-panel {
  position: fixed;
  bottom: 55px;
  left: 25%;
  width: 53%;
  overflow-y: auto;
  backdrop-filter: blur(5px);
  z-index: 1002;
  font-size: 0.8em;
  opacity: 0.9;
  transition: backdrop-filter .2s; 
}
.info-panel.dragging {                   
  backdrop-filter: none;
}

/* 拖拽条 */
.resize-handle {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  cursor: ns-resize;
  /* 可视化小条 */
  background: rgba(0,0,0,0.1);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title {
  font-weight: 600;
}
.fade-scale-enter-active,
.fade-scale-leave-active {
  transition: all .2s ease;
}
.fade-scale-enter-from,
.fade-scale-leave-to {
  opacity: 0;
  transform: scale(0.9);
}
</style>
