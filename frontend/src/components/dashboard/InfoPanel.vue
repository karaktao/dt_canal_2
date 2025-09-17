<template>
  <transition name="fade-scale">
    <el-card
      v-if="visible"
      ref="panelRef"
      class="info-panel"
      shadow="hover"
      @click.stop
      :style="{ height: panelHeight }"   >  
  
      <div
        class="resize-handle"
        @pointerdown.prevent="startDrag"
      ></div>



      <!-- 主体内容：默认渲染 item 的字段，也可通过 slot 覆盖 -->
      <slot name="content">
        <div class="content-body">
          <p v-for="(val, key) in item" :key="key">
            <b>{{ key }}:</b> {{ val }}
          </p>
        </div>
      </slot>

      <!-- 底部操作区：预留 slot -->
      <slot name="footer"></slot>
    </el-card>
  </transition>
</template>

<script setup>
import { ref, onBeforeUnmount } from 'vue';

const props = defineProps({
  visible: Boolean,
  title: { type: String, default: 'Info Panel' },
  item: { type: Object, default: () => ({}) },
});

// 面板高度可调节功能
const panelRef    = ref(null);
const panelHeight = ref('33vh');    // 初始高度，可以按需改

let dragging = false;
let startY = 0;
let startH = 0;
let panelEl = null;

function startDrag(e) {
  dragging = true;
  startY = e.clientY;
  // 兼容 panelRef 指向组件实例或 DOM 元素
  panelEl = (panelRef.value && (panelRef.value.$el || panelRef.value));
  startH = (panelEl && panelEl.getBoundingClientRect)
    ? panelEl.getBoundingClientRect().height
    : parseInt(panelHeight.value, 10) || 0;
  panelEl && panelEl.classList && panelEl.classList.add('dragging');
  document.body.style.userSelect = 'none';
  document.addEventListener('pointermove', onDrag);
  document.addEventListener('pointerup', stopDrag);
}

function onDrag(e) {
  if (!dragging) return;
  const dy = startY - e.clientY; // 向上拖动应增高
  let newH = startH + dy;
  const min = 120; // 最小高度 px
  const max = Math.max(200, window.innerHeight - 100); // 最大高度 px
  newH = Math.min(Math.max(newH, min), max);
  panelHeight.value = newH + 'px';
}

function stopDrag() {
  dragging = false;
  document.body.style.userSelect = '';
  panelEl && panelEl.classList && panelEl.classList.remove('dragging');
  document.removeEventListener('pointermove', onDrag);
  document.removeEventListener('pointerup', stopDrag);
}

onBeforeUnmount(() => {
  document.removeEventListener('pointermove', onDrag);
  document.removeEventListener('pointerup', stopDrag);
});
</script>

<style scoped>
.info-panel {
  position: fixed;
  bottom: 55px;
  left: 27.5%;
  width: 48%;
  /* 关键：使用 flex 布局让 el-card 内部 header/body/footer 布局可伸展 */
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  backdrop-filter: blur(5px);
  z-index: 1002;
  font-size: 0.8em;
  opacity: 0.9;
  transition: backdrop-filter .2s;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 8px;
}

/* 拖动时视觉反馈（可选） */
.info-panel.dragging {
  backdrop-filter: none;
}



/* 调整 el-card 内部 body，让其占满剩余空间并可滚动 */
:deep(.info-panel .el-card__body) {
  /* el-card__header 占据的高度会自动计算，body 占剩余 */
  flex: 1 1 auto;
  overflow-y: auto;
  padding: 12px;
  box-sizing: border-box;
}

.title {
  font-weight: 600;
  font-size: 1rem;
}

/* 如果 slot 默认内容包裹，请确保高度自适应 */
.content-body {
  /* 内容顶部靠上，下面空白会显现出来 */
}

/* 拖拽条：放在顶部，z-index 足够高以捕获 pointerdown */
.resize-handle {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 8px;
  cursor: ns-resize;
  z-index: 1010;
  background: linear-gradient(90deg, rgba(0,0,0,0.06), rgba(0,0,0,0.03));
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}
.fade-scale-enter-active,
.fade-scale-leave-active {
  transition: all .18s ease;
}
.fade-scale-enter-from,
.fade-scale-leave-to {
  opacity: 0;
  transform: scale(0.9);
}
</style>
