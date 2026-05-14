<template>
  <div>
    <h2 class="text-xl font-semibold mb-6 text-gray-800">仪表盘</h2>
    <el-row :gutter="20" class="mb-6">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover" class="text-center">
          <div class="text-gray-400 text-sm mb-2">{{ card.label }}</div>
          <div class="text-3xl font-bold" :class="card.color">{{ card.value }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="hover">
      <template #header><span class="font-semibold">最近文章</span></template>
      <el-table :data="recentArticles" stripe>
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读" width="80" />
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="{ row }">{{ row.createTime?.substring(0, 10) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboard, type DashboardData } from '@/api/dashboard'

const recentArticles = ref<DashboardData['recentArticles']>([])
const statCards = ref([
  { label: '文章总数', value: 0, color: 'text-blue-600' },
  { label: '分类数量', value: 0, color: 'text-green-600' },
  { label: '标签数量', value: 0, color: 'text-orange-600' },
  { label: '评论总数', value: 0, color: 'text-purple-600' },
])

function statusType(s: number) {
  const map: Record<number, string> = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }
  return map[s] || 'info'
}
function statusLabel(s: number) {
  const map: Record<number, string> = { 0: '草稿', 1: '已发布', 2: '定时', 3: '私密' }
  return map[s] || '未知'
}

onMounted(async () => {
  const res = await getDashboard()
  const d = res.data
  statCards.value[0].value = d.articleCount
  statCards.value[1].value = d.categoryCount
  statCards.value[2].value = d.tagCount
  statCards.value[3].value = d.commentCount
  recentArticles.value = d.recentArticles
})
</script>
