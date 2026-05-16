<template>
  <div>
    <h2 class="text-xl font-semibold mb-6 text-gray-800">{{ isEdit ? '编辑文章' : '新建文章' }}</h2>
    <el-form :model="form" label-width="80px" class="max-w-4xl">
      <el-form-item label="标题" required>
        <el-input v-model="form.title" placeholder="文章标题" />
      </el-form-item>
      <el-form-item label="标识">
        <el-input v-model="form.slug" placeholder="URL 标识（留空自动生成拼音）" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="form.categoryId" placeholder="选择分类" clearable>
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="标签">
        <el-select v-model="form.tagIds" multiple placeholder="选择标签">
          <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="封面图">
        <el-input v-model="form.coverImage" placeholder="封面图 URL" />
      </el-form-item>
      <el-form-item label="摘要">
        <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="文章摘要（留空自动截取）" />
      </el-form-item>
      <el-form-item label="内容" required>
        <v-md-editor v-model="form.contentMd" height="500px" class="w-full" />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :value="0">草稿</el-radio>
          <el-radio :value="1">立即发布</el-radio>
          <el-radio :value="2">定时发布</el-radio>
          <el-radio :value="3">私密</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="form.status === 2" label="定时时间">
        <el-date-picker v-model="form.publishTime" type="datetime" placeholder="选择发布时间" value-format="YYYY-MM-DD HH:mm:ss" />
      </el-form-item>
      <el-form-item label="置顶">
        <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
// 文章编辑组件 — 新建/编辑文章，含 v-md-editor Markdown 编辑器 + 分类/标签/状态/置顶/定时发布
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createArticle, updateArticle, getArticleById } from '@/api/article'
import { getCategories } from '@/api/category'
import { getTags } from '@/api/tag'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)              // 是否为编辑模式（否则为新建模式）
const saving = ref(false)              // 保存按钮 loading 态
const categories = ref<any[]>([])      // 分类下拉数据源
const tags = ref<any[]>([])            // 标签多选数据源

const form = ref({
  id: null as number | null,           // 新建时为 null，编辑时为文章 ID
  title: '',
  slug: '',                            // URL 标识，留空后端自动生成拼音
  summary: '',                         // 摘要，留空后端自动截取正文前段
  contentMd: '',                       // Markdown 原文
  coverImage: '',
  categoryId: null as number | null,
  tagIds: [] as number[],              // 选中的标签 ID 数组
  status: 0,                           // 0=草稿 1=立即发布 2=定时发布 3=私密
  isTop: 0,                            // 0=否 1=置顶
  publishTime: '',                     // 定时发布时间，仅 status=2 时有效
})

// 加载分类和标签数据；编辑模式下回填文章信息
async function loadData() {
  const [cats, tagsData] = await Promise.all([getCategories(), getTags()])
  categories.value = cats.data
  tags.value = tagsData.data
  const id = route.params.id
  if (id) {
    isEdit.value = true
    const res = await getArticleById(Number(id))
    const a = res.data
    form.value = {
      id: a.id,
      title: a.title,
      slug: a.slug,
      summary: a.summary || '',
      contentMd: a.contentMd || '',
      coverImage: a.coverImage || '',
      categoryId: a.categoryId,
      tagIds: a.tags?.map((t: any) => t.id) || [],
      status: a.status,
      isTop: a.isTop,
      publishTime: a.publishTime || '',
    }
  }
}

// 保存文章：基本校验后走创建或更新 API
async function handleSave() {
  if (!form.value.title || !form.value.contentMd) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  saving.value = true
  try {
    if (isEdit.value && form.value.id) {
      await updateArticle(form.value.id, form.value)
    } else {
      await createArticle(form.value)
    }
    ElMessage.success('保存成功')
    router.push('/articles')
  } finally { saving.value = false }
}

onMounted(() => loadData())
</script>
