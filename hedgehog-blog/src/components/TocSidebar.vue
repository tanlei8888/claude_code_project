<template>
  <nav v-if="headings.length > 0" class="toc text-sm">
    <h4 class="font-semibold text-gray-900 mb-3">目录</h4>
    <ul class="space-y-1.5 border-l border-gray-200">
      <li v-for="h in headings" :key="h.id" :style="{ paddingLeft: (h.level - 1) * 16 + 'px' }">
        <a :href="`#${h.id}`" class="block py-0.5 text-gray-500 hover:text-blue-600 transition-colors truncate"
          :class="{ 'text-blue-600 font-medium': h.id === activeId }">
          {{ h.text }}
        </a>
      </li>
    </ul>
  </nav>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

interface Heading {
  id: string
  text: string
  level: number
}

const headings = ref<Heading[]>([])
const activeId = ref('')

function extractHeadings() {
  const hs = document.querySelectorAll('.markdown-body h1, .markdown-body h2, .markdown-body h3, .markdown-body h4')
  headings.value = Array.from(hs).map((h) => ({
    id: h.id || h.textContent?.replace(/\s+/g, '-').toLowerCase() || '',
    text: h.textContent || '',
    level: parseInt(h.tagName[1]),
  }))
}

let observer: IntersectionObserver | null = null

onMounted(() => {
  extractHeadings()
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
})

onUnmounted(() => {
  observer?.disconnect()
})
</script>
