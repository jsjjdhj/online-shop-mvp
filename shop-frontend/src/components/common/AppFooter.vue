<template>
  <footer class="app-footer">
    <div class="footer-content">
      <span>© 2026 在线购物平台 MVP</span>
    </div>
    <CourseBadge />
  </footer>
</template>

<script setup>
import CourseBadge from './CourseBadge.vue'
</script>

<style scoped>
.app-footer {
  margin-top: auto;
}
.footer-content {
  text-align: center;
  padding: 12px 0;
  font-size: 14px;
  color: #909399;
  background-color: #fff;
}
</style>
