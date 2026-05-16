<template>
  <div>
    <h2 class="text-xl font-semibold mb-6 text-gray-800">站点配置</h2>
    <el-tabs>
      <el-tab-pane label="基本设置">
        <el-form :model="form" label-width="100px" class="max-w-2xl">
          <el-form-item label="站点名称">
            <el-input v-model="form.siteName" />
          </el-form-item>
          <el-form-item label="副标题">
            <el-input v-model="form.siteSubtitle" />
          </el-form-item>
          <el-form-item label="Logo URL">
            <el-input v-model="form.siteLogo" />
          </el-form-item>
          <el-form-item label="Favicon URL">
            <el-input v-model="form.siteFavicon" />
          </el-form-item>
          <el-form-item label="页脚文字">
            <el-input v-model="form.footerText" />
          </el-form-item>
          <el-form-item label="备案号">
            <el-input v-model="form.icpNumber" />
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="作者信息">
        <el-form :model="form" label-width="100px" class="max-w-2xl">
          <el-form-item label="作者名">
            <el-input v-model="form.authorName" />
          </el-form-item>
          <el-form-item label="作者头像">
            <el-input v-model="form.authorAvatar" />
          </el-form-item>
          <el-form-item label="作者简介">
            <el-input v-model="form.authorBio" type="textarea" :rows="3" />
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="社交链接">
        <el-form :model="form" label-width="100px" class="max-w-2xl">
          <el-form-item label="GitHub">
            <el-input v-model="form.socialGithub" />
          </el-form-item>
          <el-form-item label="Twitter">
            <el-input v-model="form.socialTwitter" />
          </el-form-item>
          <el-form-item label="知乎">
            <el-input v-model="form.socialZhihu" />
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="关于页">
        <v-md-editor v-model="form.aboutContentMd" height="400px" class="w-full" />
      </el-tab-pane>
    </el-tabs>
    <div class="mt-6">
      <el-button type="primary" @click="handleSave" :loading="saving">保存配置</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
// 站点配置组件 — 选项卡表单（基本设置/作者信息/社交链接/关于页 Markdown 编辑器）
import { ref, onMounted } from 'vue'
import { getSiteConfig, updateSiteConfig, type SiteConfig } from '@/api/site'
import { ElMessage } from 'element-plus'

// 站点配置表单数据，初始为空，挂载后从后端回填
const form = ref<SiteConfig>({
  id: 1, siteName: '', siteSubtitle: '', siteLogo: '', siteFavicon: '',
  aboutContentMd: '', aboutContentHtml: '', authorName: '', authorAvatar: '', authorBio: '',
  socialGithub: '', socialTwitter: '', socialZhihu: '', icpNumber: '', footerText: '',
})

const saving = ref(false)              // 保存按钮 loading 态

// 提交保存全部站点配置
async function handleSave() {
  saving.value = true
  try {
    await updateSiteConfig(form.value)
    ElMessage.success('配置已保存')
  } finally { saving.value = false }
}

onMounted(async () => {
  const res = await getSiteConfig()
  if (res.data) {
    // 合并后端数据，保证 null 字段转为空字符串
    form.value = {
      ...res.data,
      aboutContentMd: res.data.aboutContentMd ?? '',
      aboutContentHtml: res.data.aboutContentHtml ?? '',
    }
  }
})
</script>
