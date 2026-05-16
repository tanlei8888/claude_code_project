<template>
  <div>
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-xl font-semibold text-gray-800">媒体管理</h2>
      <div class="flex items-center gap-2">
        <el-select v-model="uploadType" size="small" class="!w-24">
          <el-option label="内容" value="CONTENT" />
          <el-option label="头像" value="AVATAR" />
          <el-option label="私密" value="PRIVATE" />
        </el-select>
        <el-upload :show-file-list="false" :http-request="handleUpload" accept="image/jpeg,image/png,image/gif,image/webp,image/svg+xml">
          <el-button type="primary">上传文件</el-button>
        </el-upload>
      </div>
    </div>
    <div v-if="loading" class="text-center py-10 text-gray-400">加载中...</div>
    <el-empty v-else-if="mediaList.length === 0" description="暂无媒体文件" />
    <div v-else class="grid grid-cols-4 gap-4">
      <div v-for="item in mediaList" :key="item.id" class="border border-gray-200 rounded-lg overflow-hidden group relative">
        <img :src="item.url" class="w-full h-36 object-cover" />
        <div class="p-2 text-xs text-gray-500 truncate">{{ item.filename }}</div>
        <div class="px-2 pb-2 relative z-10">
          <el-select v-model="item.mediaType" size="small" class="!w-full" @change="(val: string) => handleTypeChange(item.id, val)">
            <el-option label="内容" value="CONTENT" />
            <el-option label="头像" value="AVATAR" />
            <el-option label="私密" value="PRIVATE" />
          </el-select>
        </div>
        <div class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2">
          <el-button size="small" @click="copyUrl(item.url)">复制 URL</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(item.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>
    </div>
    <div class="flex justify-end mt-4" v-if="total > size">
      <el-pagination v-model:current-page="page" :total="total" :page-size="size" layout="total, prev, pager, next"
        @current-change="load" />
    </div>
  </div>
</template>

<script setup lang="ts">
// 媒体管理组件 — 网格展示 + 上传（带分类选择）+ 复制 URL + 删除 + 分类更改
import { ref, onMounted } from 'vue'
import { getMediaPage, uploadFile, deleteMedia, updateMediaType } from '@/api/media'
import { ElMessage } from 'element-plus'

const mediaList = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const size = 12                                                        // 每页 12 张，适配 4 列网格
const uploadType = ref('CONTENT')                                      // 上传时默认媒体分类：CONTENT | AVATAR | PRIVATE

// 加载媒体分页数据
async function load() {
  loading.value = true
  try {
    const res = await getMediaPage({ page: page.value, size })
    mediaList.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

// 处理 Element Plus el-upload 的自定义上传请求
async function handleUpload(options: any) {
  try {
    await uploadFile(options.file, uploadType.value)
    ElMessage.success('上传成功')
    load()
  } catch {  }
}

// 复制文件 URL 到剪贴板
function copyUrl(url: string) {
  navigator.clipboard.writeText(url).then(() => ElMessage.success('已复制到剪贴板'))
}

// 删除媒体文件
async function handleDelete(id: number) {
  await deleteMedia(id)
  ElMessage.success('已删除')
  load()
}

// 更改媒体分类类型
async function handleTypeChange(id: number, mediaType: string) {
  await updateMediaType(id, mediaType)
  ElMessage.success('类型已更新')
}

onMounted(() => load())
</script>
