<template>
  <div class="markdown-body" v-html="html"></div>
</template>

<script setup lang="ts">
// Markdown 渲染器 — 接收服务端已转 HTML 的内容，通过 v-html 直接渲染
import { computed } from 'vue'

// 服务端 flexmark 转换后的 HTML 字符串
const props = defineProps<{ content: string }>()

const html = computed(() => props.content)
</script>
