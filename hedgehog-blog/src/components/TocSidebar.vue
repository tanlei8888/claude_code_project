<template>
  <nav v-if="headings.length > 0" class="toc text-sm">
    <h4 class="font-semibold text-gray-900 mb-3">目录</h4>
    <ul class="space-y-1.5 border-l border-gray-200">
      <li v-for="h in headings" :key="h.id" :style="{ paddingLeft: (h.level - 1) * 16 + 'px' }">
        <a href="#" @click.prevent="scrollTo(h.id)" class="block py-0.5 text-gray-500 hover:text-blue-600 transition-colors truncate"
          :class="{ 'text-blue-600 font-medium': h.id === activeId }">
          {{ h.text }}
        </a>
      </li>
    </ul>
  </nav>
</template>

<script setup lang="ts">
// 文章目录侧边栏 — 从 DOM 中提取标题层级并渲染为可点击导航，滚动高亮当前标题
import { ref, onMounted, onUnmounted, nextTick } from 'vue'

interface Heading {
  id: string    // 标题元素 ID，用于锚点跳转
  text: string  // 标题显示文本
  level: number // 标题级别（1-4，对应 h1-h4）
}

const headings = ref<Heading[]>([]) // 提取到的所有标题
const activeId = ref('')            // 当前可见区域对应的标题 ID

// 根据标题文本生成唯一 ID，若冲突则追加数字后缀
function generateId(text: string, used: Set<string>): string {
  let id = text.replace(/[^\w一-鿿]+/g, '-').replace(/^-|-$/g, '').toLowerCase()
  if (!id) id = 'heading'
  let unique = id
  let i = 1
  while (used.has(unique)) unique = `${id}-${i++}`
  return unique
}

// 从 .markdown-body 中提取 h1-h4 标题并构建目录数据
function extractHeadings() {
  const used = new Set<string>()
  const hs = document.querySelectorAll('.markdown-body h1, .markdown-body h2, .markdown-body h3, .markdown-body h4')
  headings.value = Array.from(hs).map((h) => {
    let id = h.id
    if (!id) {
      id = generateId(h.textContent || '', used)
      h.id = id
    }
    used.add(id)
    return {
      id,
      text: h.textContent || '',
      level: parseInt(h.tagName[1]),
    }
  })
}

// 平滑滚动到指定标题
function scrollTo(id: string) {
  const el = document.getElementById(id)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

// 使用 IntersectionObserver 监听标题可见性，自动高亮当前标题
function setupObserver() {
  observer?.disconnect()
  observer = new IntersectionObserver(
    (entries) => {
      for (const entry of entries) {
        if (entry.isIntersecting) {
          activeId.value = entry.target.id
        }
      }
    },
    { rootMargin: '-80px 0px -80% 0px' },
  )
  document.querySelectorAll('.markdown-body h1[id], .markdown-body h2[id], .markdown-body h3[id], .markdown-body h4[id]')
    .forEach((h) => observer?.observe(h))
}

let observer: IntersectionObserver | null = null

onMounted(() => {
  // 等待 v-html 渲染完成（文章数据异步加载后 DOM 才更新）
  nextTick(() => {
    extractHeadings()
    setupObserver()
  })
})

onUnmounted(() => {
  observer?.disconnect()
})
</script>
