<template>
  <div class="max-w-4xl mx-auto px-6 py-8">
    <!-- 分类/标签筛选 -->
    <div class="mb-8 flex flex-wrap items-center gap-3">
      <button @click="activeFilter = null; currentSlug = ''; router.push('/'); loadArticles()"
        :class="['px-3 py-1.5 rounded-full text-sm transition-colors', !activeFilter ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
        全部
      </button>
      <span class="text-gray-300">|</span>
      <button v-for="cat in categories" :key="cat.id" @click="filterByCategory(cat)"
        :class="['px-3 py-1.5 rounded-full text-sm transition-colors', activeFilter === 'category' && currentSlug === cat.slug ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
        {{ cat.name }}
      </button>
      <span class="text-gray-300">|</span>
      <button v-for="tag in tags" :key="tag.id" @click="filterByTag(tag)"
        :class="['px-3 py-1.5 rounded-full text-sm transition-colors', activeFilter === 'tag' && currentSlug === tag.slug ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
        #{{ tag.name }}
      </button>
    </div>

    <!-- 搜索 -->
    <div class="mb-8">
      <input v-model="keyword" @keyup.enter="search" placeholder="搜索文章..."
        class="w-full max-w-md px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
    </div>

    <!-- 文章列表 -->
    <div v-if="loading" class="space-y-4">
      <div v-for="i in 3" :key="i" class="animate-pulse py-8 border-b border-gray-100">
        <div class="h-4 bg-gray-200 rounded w-1/4 mb-3"></div>
        <div class="h-6 bg-gray-200 rounded w-3/4 mb-2"></div>
        <div class="h-4 bg-gray-200 rounded w-full mb-1"></div>
      </div>
    </div>
    <div v-else-if="articles.length === 0" class="text-center py-20 text-gray-400">
      暂无文章
    </div>
    <div v-else>
      <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
      <!-- 分页 -->
      <div class="flex justify-center gap-3 mt-10">
        <button :disabled="page === 1" @click="loadArticles(page - 1)"
          class="px-4 py-2 text-sm border border-gray-200 rounded-lg hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors">
          上一页
        </button>
        <span class="px-4 py-2 text-sm text-gray-500">第 {{ page }} / {{ totalPages }} 页</span>
        <button :disabled="page >= totalPages" @click="loadArticles(page + 1)"
          class="px-4 py-2 text-sm border border-gray-200 rounded-lg hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors">
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticles, getCategories, getTags, type Article, type Category, type Tag } from '@/api/article'
import ArticleCard from '@/components/ArticleCard.vue'

const route = useRoute()
const router = useRouter()

const articles = ref<Article[]>([])
const categories = ref<Category[]>([])
const tags = ref<Tag[]>([])
const loading = ref(false)
const page = ref(1)
const totalPages = ref(1)
const keyword = ref('')
const activeFilter = ref<string | null>(null)
const currentSlug = ref('')
let filterId: number | null = null

async function loadArticles(p = 1) {
  loading.value = true
  try {
    const params: any = { page: p, size: 10 }
    if (activeFilter.value === 'category') params.categoryId = filterId
    if (activeFilter.value === 'tag') params.tagId = filterId
    if (keyword.value) params.keyword = keyword.value
    const data = await getArticles(params)
    articles.value = data.records
    page.value = data.current
    totalPages.value = Math.ceil(data.total / data.size) || 1
  } finally {
    loading.value = false
  }
}

function filterByCategory(cat: Category) {
  activeFilter.value = 'category'
  currentSlug.value = cat.slug
  filterId = cat.id
  keyword.value = ''
  router.push(`/category/${cat.slug}`)
  loadArticles()
}

function filterByTag(tag: Tag) {
  activeFilter.value = 'tag'
  currentSlug.value = tag.slug
  filterId = tag.id
  keyword.value = ''
  router.push(`/tag/${tag.slug}`)
  loadArticles()
}

function search() {
  activeFilter.value = null
  currentSlug.value = ''
  filterId = null
  loadArticles()
}

onMounted(async () => {
  const [cats, tagsData] = await Promise.all([getCategories(), getTags()])
  categories.value = cats
  tags.value = tagsData
  // 检查路由是否是分类/标签页
  if (route.params.slug) {
    const cat = cats.find(c => c.slug === route.params.slug)
    if (cat) {
      activeFilter.value = 'category'
      currentSlug.value = cat.slug
      filterId = cat.id
    } else {
      const tag = tagsData.find(t => t.slug === route.params.slug)
      if (tag) {
        activeFilter.value = 'tag'
        currentSlug.value = tag.slug
        filterId = tag.id
      }
    }
  }
  loadArticles()
})
</script>
