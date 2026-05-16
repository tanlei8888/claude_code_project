<template>
  <!-- 标签筛选页：复用 Home 组件，按路由 slug 过滤标签 -->
  <Home />
</template>

<script setup lang="ts">
// 标签文章列表页 — 直接复用 Home 组件，Home 内部根据路由 slug 筛选标签
import Home from './Home.vue'
</script>
