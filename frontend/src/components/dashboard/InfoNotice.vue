<template>

    <div class="vessel-card" role="region" aria-label="Vessel summary">

    </div>

</template>

<script setup>

</script>

<style scoped>
.info-vessel-root { width: 100%; box-sizing: border-box; }

/* 三列按比例 2 : 4 : 2 */
.vessel-card {
  display: grid;
  gap: 4px;
  align-items: start;
  grid-template-columns: 2fr 2fr 1fr 0.05fr;
  width: 95%;
  margin: 10px auto 10px 10px;
  box-sizing: border-box;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  background: #ffffffe0;
  border-radius: 5px; 
  height: 190px;
}

</style>
