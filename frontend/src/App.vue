<script setup>
import { RouterView } from 'vue-router'
</script>

<template>
  <RouterView />
</template>

<style>
/* 全局变量和字体定义 */
:root {
  --primary-color: #4f46e5;
  --primary-hover: #4338ca;
  --bg-color: #f3f4f6;
  --text-main: #1f2937;
  --text-secondary: #6b7280;
  --card-bg: rgba(255, 255, 255, 0.85);
  --border-radius-lg: 16px;
  --border-radius-md: 12px;
}

body {
  margin: 0;
  padding: 0;
  font-family: 'Inter', 'Noto Sans SC', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: var(--bg-color);
  color: var(--text-main);
}

/* 全局路由过渡动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(15px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-15px);
}

/* 自定义滚动条样式，增强高级感 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}
::-webkit-scrollbar-track {
  background: transparent; 
}
::-webkit-scrollbar-thumb {
  background: #cbd5e1; 
  border-radius: 4px;
}
::-webkit-scrollbar-thumb:hover {
  background: #94a3b8; 
}
</style>