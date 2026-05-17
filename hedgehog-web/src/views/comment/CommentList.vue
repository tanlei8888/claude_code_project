<template>
  <div>
    <h2 class="text-xl font-semibold mb-4 text-gray-800">评论管理</h2>
    <div class="flex gap-4 mb-4">
      <el-select v-model="filterStatus" placeholder="审核状态" clearable @change="load" class="w-32">
        <el-option label="待审核" :value="0" />
        <el-option label="已通过" :value="1" />
        <el-option label="已拒绝" :value="2" />
      </el-select>
    </div>
    <el-table :data="comments" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="user" label="用户" width="100">
        <template #default="{ row }">{{ row.user?.nickname || row.user?.username || '-' }}</template>
      </el-table-column>
      <el-table-column prop="content" label="内容" min-width="250" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="160">
        <template #default="{ row }">{{ row.createTime?.substring(0, 16) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" size="small" type="success" @click="handleAudit(row.id, 1)">通过</el-button>
          <el-button v-if="row.status === 0" size="small" type="warning" @click="handleAudit(row.id, 2)">拒绝</el-button>
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
// 评论管理组件 — 审核状态筛选 + 分页表格 + 通过/拒绝/删除操作
import { ref, onMounted } from 'vue'
import { getCommentPage, auditComment, deleteComment } from '@/api/comment'
import { ElMessage } from 'element-plus'

const comments = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const size = 10                                                      // 每页条数
const filterStatus = ref<number | null>(null)                        // 审核状态筛选：0=待审核 1=已通过 2=已拒绝

// 评论状态 → Element Plus tag 类型映射
function statusType(s: number): 'info' | 'success' | 'warning' | 'danger' {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' } as const
  return map[s as keyof typeof map] || 'info'
}
// 评论状态 → 中文标签映射
function statusLabel(s: number) { return { 0: '待审核', 1: '已通过', 2: '已拒绝' }[s] || '未知' }

// 加载评论分页数据
async function load() {
  loading.value = true
  try {
    const res = await getCommentPage({ page: page.value, size, status: filterStatus.value ?? undefined })
    comments.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

// 审核评论：通过(1) 或 拒绝(2)
async function handleAudit(id: number, status: number) {
  await auditComment(id, status)
  ElMessage.success('操作成功')
  load()
}

// 删除评论（含确认弹窗）
async function handleDelete(id: number) {
  await deleteComment(id)
  ElMessage.success('已删除')
  load()
}

onMounted(() => load())
</script>
