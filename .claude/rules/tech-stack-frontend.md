---
paths:
  - "hedgehog-web/**"
  - "hedgehog-blog/**"
---

# 前端技术栈

## 共用技术栈

- Vue 3.4 + TypeScript 5.4 + Vite 5.2
- Pinia 2.1（状态管理）+ Vue Router 4.3（Hash 模式）
- Axios 1.6（HTTP）+ Tailwind CSS 3.4（样式）
- ESLint 8（flat config）+ Prettier 3.2

## hedgehog-web（后台管理）

- Element Plus 2.7 + @element-plus/icons-vue
- @kangc/v-md-editor（Markdown 编辑器，CodeMirror 内核）
- 端口 3000，Vite proxy `/api` → localhost:8080

## hedgehog-blog（博客前台）

- marked + highlight.js（Markdown 渲染 + 代码高亮）
- 纯 Tailwind CSS，无 Element Plus
- 端口 3001，Vite proxy `/api` → localhost:8080

## 设计规范（博客前台）

- 极简科技风：白色底、大量留白、精致排版
- 字体栈：`"PingFang SC", "Noto Sans SC", "Microsoft YaHei", sans-serif`
- 文章详情标题 32px+，正文 16px，行高 1.8
- 代码块：highlight.js + github-dark 主题
- 文章卡片：悬停微动效（阴影加深 + 轻微上移）
- 点赞按钮：心形图标 + 点击动画
