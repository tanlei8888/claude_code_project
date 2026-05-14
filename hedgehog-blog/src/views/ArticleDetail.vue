<template>
  <div class="max-w-4xl mx-auto px-6 py-8">
    <div v-if="loading" class="animate-pulse">
      <div class="h-10 bg-gray-200 rounded w-3/4 mb-4"></div>
      <div class="h-4 bg-gray-200 rounded w-1/3 mb-8"></div>
      <div class="space-y-3">
        <div class="h-4 bg-gray-200 rounded w-full"></div>
        <div class="h-4 bg-gray-200 rounded w-5/6"></div>
        <div class="h-4 bg-gray-200 rounded w-4/6"></div>
      </div>
    </div>

    <template v-else-if="article">
      <!-- 文章头部 -->
      <header class="mb-10">
        <div class="flex items-center gap-2 text-sm text-gray-400 mb-4">
          <span v-if="article.category" class="text-blue-500 bg-blue-50 px-2 py-0.5 rounded-full">{{ article.category.name }}</span>
          <span>{{ formatDate(article.createTime) }}</span>
          <span>{{ article.viewCount }} 阅读</span>
          <span>{{ article.likeCount }} 点赞</span>
        </div>
        <h1 class="text-4xl font-bold text-gray-900 mb-4 leading-tight">{{ article.title }}</h1>
        <div class="flex items-center gap-3">
          <img v-if="article.author?.avatar" :src="article.author.avatar" class="w-10 h-10 rounded-full" />
          <div>
            <div class="text-sm font-medium text-gray-900">{{ article.author?.nickname || article.author?.username }}</div>
            <div class="text-xs text-gray-400">{{ article.author?.bio || '' }}</div>
          </div>
        </div>
      </header>

      <div class="flex gap-8 relative">
        <!-- 文章内容 -->
        <div class="flex-1 min-w-0">
          <div v-if="article.coverImage" class="mb-8">
            <img :src="article.coverImage" class="w-full rounded-xl object-cover max-h-96" />
          </div>
          <MarkdownRenderer :content="article.contentHtml" />
          <!-- 标签 -->
          <div v-if="article.tags?.length" class="flex gap-2 mt-10 pt-6 border-t border-gray-100">
            <router-link v-for="tag in article.tags" :key="tag.id" :to="`/tag/${tag.slug}`"
              class="px-3 py-1 text-sm text-gray-500 bg-gray-100 rounded-full hover:bg-blue-50 hover:text-blue-600 transition-colors">
              #{{ tag.name }}
            </router-link>
          </div>
          <!-- 点赞 -->
          <div class="flex justify-center mt-10 pt-6 border-t border-gray-100">
            <button @click="handleLike"
              class="flex items-center gap-2 px-6 py-3 rounded-full border transition-all duration-200"
              :class="[liked ? 'border-red-200 bg-red-50 text-red-500' : 'border-gray-200 text-gray-500 hover:border-red-200 hover:text-red-400']">
              <svg class="w-5 h-5" :class="{ 'heart-beat': justLiked }" viewBox="0 0 24 24"
                :fill="liked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
              </svg>
              <span class="text-sm">{{ article.likeCount }}</span>
            </button>
          </div>
          <!-- 评论区 -->
          <CommentSection :article-id="article.id" :comment-count="article.commentCount" />
        </div>

        <!-- 目录侧边栏 -->
        <aside class="hidden lg:block w-56 flex-shrink-0">
          <div class="sticky top-24">
            <TocSidebar />
          </div>
        </aside>
      </div>
    </template>

    <div v-else class="text-center py-20 text-gray-400">文章不存在</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleBySlug, type Article } from '@/api/article'
import { toggleLike as toggleLikeApi } from '@/api/like'
import { useUserStore } from '@/stores/user'
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'
import TocSidebar from '@/components/TocSidebar.vue'
import CommentSection from '@/components/CommentSection.vue'

const route = useRoute()
const userStore = useUserStore()
const article = ref<Article | null>(null)
const loading = ref(true)
const liked = ref(false)
const justLiked = ref(false)

function formatDate(date: string) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

async function handleLike() {
  if (!userStore.isLoggedIn()) {
    window.location.hash = '#/login'
    return
  }
  try {
    const result = await toggleLikeApi(article.value!.id)
    liked.value = result
    if (result) {
      justLiked.value = true
      article.value!.likeCount++
      setTimeout(() => { justLiked.value = false }, 600)
    } else {
      article.value!.likeCount = Math.max(0, article.value!.likeCount - 1)
    }
  } catch {}
}

onMounted(async () => {
  try {
    const slug = route.params.slug as string
    article.value = await getArticleBySlug(slug)
    liked.value = article.value.liked
    document.title = `${article.value.title} - Hedgehog Blog`
    const meta = document.querySelector('meta[name="description"]')
    if (meta) meta.setAttribute('content', article.value.summary || article.value.title)
  } catch {
    article.value = null
  } finally {
    loading.value = false
  }
})
</script>
