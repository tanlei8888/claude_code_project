// 应用入口 — 挂载 Vue 实例，注册全局插件（Pinia、Router、Element Plus、v-md-editor）

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import './styles/global.css'
import Prism from 'prismjs'
import VMdEditor from '@kangc/v-md-editor'
import vuepressTheme from '@kangc/v-md-editor/lib/theme/vuepress.js'
import '@kangc/v-md-editor/lib/style/base-editor.css'
import '@kangc/v-md-editor/lib/theme/style/vuepress.css'

// 激活 vuepress 主题，集成 Prism 代码高亮
VMdEditor.use(vuepressTheme, { Prism })

const app = createApp(App)

app.use(createPinia())                         // 状态管理
app.use(router)                                // 路由
app.use(ElementPlus, { locale: zhCn })          // Element Plus 组件库（中文）
app.use(VMdEditor)                              // Markdown 编辑器

// 全局注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
