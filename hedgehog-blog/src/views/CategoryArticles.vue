<template>
  <!-- 分类筛选页：复用 Home 组件，按路由 slug 过滤分类 -->
  <Home />
</template>

<script setup lang="ts">
// 分类文章列表页 — 直接复用 Home 组件，Home 内部根据路由 slug 筛选分类
import Home from './Home.vue'
</script>
