// 博客前台应用入口 — 初始化 Vue 应用，挂载 Pinia、Router、Unhead 三大插件后挂载到 #app
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createHead } from '@unhead/vue'
import App from './App.vue'
import router from './router'
import './styles/global.css'

const app = createApp(App)
const head = createHead() // Unhead 实例，用于管理 <head> 中的 title/meta/script
app.use(createPinia()) // Pinia 状态管理
app.use(router) // Vue Router 路由
app.use(head) // Unhead SEO 管理
app.mount('#app')
