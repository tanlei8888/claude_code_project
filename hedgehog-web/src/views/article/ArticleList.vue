<template>
  <div>
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-xl font-semibold text-gray-800">文章管理</h2>
      <el-button type="primary" @click="$router.push('/articles/create')">新建文章</el-button>
    </div>
    <div class="flex gap-4 mb-4">
      <el-select v-model="filterStatus" placeholder="状态筛选" clearable @change="load" class="w-32">
        <el-option label="草稿" :value="0" />
        <el-option label="已发布" :value="1" />
        <el-option label="定时发布" :value="2" />
        <el-option label="私密" :value="3" />
      </el-select>
      <el-input v-model="keyword" placeholder="搜索标题..." clearable @input="load" class="w-64" />
    </div>
    <el-table :data="articles" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="200">
        <template #default="{ row }">
          <span class="text-blue-600 cursor-pointer hover:underline" @click="$router.push(`/articles/${row.id}/edit`)">
            {{ row.title }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="100">
        <template #default="{ row }">{{ row.category?.name || '-' }}</template>
      </el-table-column>
      <el-table-column prop="viewCount" label="阅读" width="70" />
      <el-table-column prop="likeCount" label="点赞" width="70" />
      <el-table-column prop="commentCount" label="评论" width="70" />
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{ row }">{{ row.createTime?.substring(0, 16) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/articles/${row.id}/edit`)">编辑</el-button>
          <el-button v-if="row.status !== 1" size="small" type="success" @click="handlePublish(row)">发布</el-button>
          <el-button v-if="row.isTop" size="small" type="warning" @click="handleTop(row)">取消置顶</el-button>
          <el-button v-else size="small" @click="handleTop(row)">置顶</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div class="flex justify-end mt-4">
      <el-pagination v-model:current-page="page" :total="total" :page-size="size" layout="total, prev, pager, next"
        @current-change="load" />
    </div>
  </div>
</template>

<script setup lang="ts">
// 文章列表组件 — 状态筛选 + 关键词搜索 + 分页表格 + 发布/置顶/删除操作
import { ref, onMounted } from 'vue'
import { getArticlePage, deleteArticle, updateArticleStatus, toggleArticleTop, type Article } from '@/api/article'
import { ElMessage } from 'element-plus'

const articles = ref<Article[]>([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const size = 10                                                      // 每页条数
const filterStatus = ref<number | null>(null)                        // 状态筛选：0-3 或 null（全部）
const keyword = ref('')                                             // 标题搜索关键词

// 文章状态 → Element Plus tag 类型映射
function statusType(s: number) { return { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }[s] || 'info' }
// 文章状态 → 中文标签映射
function statusLabel(s: number) { return { 0: '草稿', 1: '已发布', 2: '定时', 3: '私密' }[s] || '未知' }

// 加载文章分页数据
async function load() {
  loading.value = true
  try {
    const res = await getArticlePage({
      page: page.value, size, status: filterStatus.value ?? undefined, keyword: keyword.value || undefined,
    })
    articles.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

// 快速发布（将草稿/定时/私密转为已发布）
async function handlePublish(row: Article) {
  await updateArticleStatus(row.id, 1)
  ElMessage.success('已发布')
  load()
}

// 切换置顶状态
async function handleTop(row: Article) {
  await toggleArticleTop(row.id)
  ElMessage.success(row.isTop ? '已取消置顶' : '已置顶')
  load()
}

// 删除文章（含确认弹窗）
async function handleDelete(id: number) {
  await deleteArticle(id)
  ElMessage.success('已删除')
  load()
}

onMounted(() => load())
</script>
