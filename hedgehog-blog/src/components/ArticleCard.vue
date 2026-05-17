<template>
  <router-link :to="`/article/${article.slug}`" class="block group">
    <article class="py-8 border-b border-gray-100 transition-all duration-200 hover:shadow-lg hover:-translate-y-0.5 hover:bg-gray-50/50 hover:px-4 hover:-mx-4 rounded-lg">
      <div class="flex items-center gap-2 text-xs text-gray-400 mb-3">
        <span v-if="article.category" class="text-blue-500 bg-blue-50 px-2 py-0.5 rounded-full">
          {{ article.category.name }}
        </span>
        <span v-if="article.isTop" class="text-orange-500 bg-orange-50 px-2 py-0.5 rounded-full">置顶</span>
        <span>{{ formatDate(article.createTime) }}</span>
        <span class="ml-auto flex items-center gap-3">
          <span>{{ article.viewCount }} 阅读</span>
          <span>{{ article.likeCount }} 赞</span>
          <span>{{ article.commentCount }} 评论</span>
        </span>
      </div>
      <h2 class="text-xl font-semibold text-gray-900 mb-2 group-hover:text-blue-600 transition-colors">
        {{ article.title }}
      </h2>
      <p v-if="article.summary" class="text-gray-500 text-sm leading-relaxed mb-3 line-clamp-2">
        {{ article.summary }}
      </p>
      <div class="flex items-center gap-3 text-xs text-gray-400">
        <span v-if="article.author" class="flex items-center gap-1">
          <img v-if="article.author.avatar" :src="article.author.avatar" class="w-5 h-5 rounded-full" />
          {{ article.author.nickname || article.author.username }}
        </span>
        <span v-if="article.tags" class="flex gap-1">
          <span v-for="tag in article.tags" :key="tag.id" class="text-gray-400 hover:text-blue-500">
            #{{ tag.name }}
          </span>
        </span>
      </div>
    </article>
  </router-link>
</template>

<script setup lang="ts">
// 文章卡片组件 — 展示文章摘要、分类、标签、作者信息，支持悬停微动效
import type { Article } from '@/api/article'

// 文章对象，包含标题、摘要、分类、标签、作者等字段
defineProps<{ article: Article }>()

// 格式化日期为中文短格式（YYYY/MM/DD）
function formatDate(date: string) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}
</script>
