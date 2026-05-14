# Hedgehog Admin — 项目结构文档

本文件描述项目的完整结构，可根据此文档重新生成项目。

---

## 一、项目概述

| 项目 | 说明 |
|------|------|
| **项目名称** | hedgehog-web |
| **版本** | 0.0.1 |
| **类型** | Vue 3 + TypeScript 后台管理系统 |
| **包管理器** | npm |
| **模块规范** | ESM (`"type": "module"`) |

---

## 二、技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue 3 | ^3.4.21 |
| 语言 | TypeScript | ~5.4.0 |
| 构建工具 | Vite | ^5.2.0 |
| 路由 | Vue Router | ^4.3.0 |
| 状态管理 | Pinia | ^2.1.7 |
| HTTP 请求 | Axios | ^1.6.8 |
| UI 组件库 | Element Plus | ^2.7.0 |
| 图标 | @element-plus/icons-vue | ^2.3.1 |
| CSS 方案 | Tailwind CSS | ^3.4.0 |
| 代码检查 | ESLint + Prettier | - |

### 开发工具链

| 类别 | 技术 | 版本 |
|------|------|------|
| ESLint 配置 | @eslint/js, eslint-plugin-vue, @vue/eslint-config-typescript, @vue/eslint-config-prettier | ^8/^9 |
| Prettier | prettier | ^3.2.0 |
| 类型检查 | vue-tsc | ^2.0.0 |
| 自动导入（组件） | unplugin-vue-components | ^28 |
| 自动导入（API） | unplugin-auto-import | ^19 |

---

## 三、完整目录结构

```
hedgehog-web/
├── index.html                       # 入口 HTML
├── package.json                     # 项目配置与依赖
├── vite.config.ts                   # Vite 构建配置
├── tsconfig.json                    # TypeScript 配置（src）
├── tsconfig.node.json               # TypeScript 配置（Vite/Node）
├── tailwind.config.js               # Tailwind CSS 配置
├── postcss.config.js                # PostCSS 配置
├── eslint.config.js                 # ESLint 扁平化配置
├── .eslintignore                    # ESLint 忽略规则
├── .prettierrc                      # Prettier 格式化规则
├── .gitignore                       # Git 忽略规则
├── CLAUDE.md                        # LLM 行为指南
│
└── src/
    ├── main.ts                      # 应用入口
    ├── App.vue                      # 根组件
    ├── env.d.ts                     # Vite/Vue 环境类型声明
    │
    ├── api/                         # API 接口层
    │   ├── request.ts               # Axios 实例 + 拦截器
    │   ├── auth.ts                  # 认证相关接口
    │   └── user.ts                  # 用户管理接口
    │
    ├── router/
    │   └── index.ts                 # 路由配置 + 守卫
    │
    ├── stores/
    │   └── user.ts                  # 用户状态（Pinia）
    │
    ├── utils/
    │   └── auth.ts                  # Token 工具函数
    │
    ├── styles/
    │   └── global.css               # 全局样式（Tailwind 指令）
    │
    ├── types/
    │   ├── auto-imports.d.ts        # 自动导入 API 类型声明（自动生成）
    │   └── components.d.ts          # 自动导入组件类型声明（自动生成）
    │
    └── views/                       # 页面组件
        ├── Login.vue                # 登录页
        ├── Layout.vue               # 布局框架
        └── user/
            └── UserList.vue         # 用户管理列表页
```

---

## 四、文件详细说明

### 4.1 根目录配置文件

#### index.html

- 语言设为 `zh-CN`
- 标题为 `Hedgehog Admin`
- 挂载点 `<div id="app">`
- 入口脚本 `/src/main.ts`

#### package.json — 关键字段

| 字段 | 值 |
|------|-----|
| `name` | `"hedgehog-web"` |
| `private` | `true` |
| `type` | `"module"` |

npm scripts:

| 命令 | 操作 |
|------|------|
| `dev` | `vite` |
| `build` | `vue-tsc --noEmit && vite build` |
| `preview` | `vite preview` |
| `lint` | `eslint . --ext .vue,.ts,.tsx --fix` |
| `format` | `prettier --write src/` |

**dependencies:**
```
vue: ^3.4.21
vue-router: ^4.3.0
pinia: ^2.1.7
axios: ^1.6.8
element-plus: ^2.7.0
@element-plus/icons-vue: ^2.3.1
```

**devDependencies:**
```
@vitejs/plugin-vue: ^5.0.4
vite: ^5.2.0
typescript: ~5.4.0
vue-tsc: ^2.0.0
tailwindcss: ^3.4.0
autoprefixer: ^10.4.19
postcss: ^8.4.38
eslint: ^8.57.1
@eslint/js: ^8.57.1
eslint-plugin-vue: ^9.24.0
@vue/eslint-config-typescript: ^13.0.0
@vue/eslint-config-prettier: ^9.0.0
prettier: ^3.2.0
unplugin-vue-components: ^28
unplugin-auto-import: ^19
```

#### vite.config.ts

```ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router', 'pinia'],
      dts: 'src/types/auto-imports.d.ts',
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/types/components.d.ts',
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
```

功能要点：
- `@` 别名指向 `src/` 目录
- 开发服务器端口 `3000`
- `/api` 请求代理到 `http://localhost:8080`
- Element Plus 组件和 API 按需自动导入
- `vue`、`vue-router`、`pinia` API 自动导入（无需手动 `import { ref } from 'vue'`）

#### tsconfig.json

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "module": "ESNext",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",
    "strict": true,
    "noUnusedLocals": false,
    "noUnusedParameters": false,
    "noFallthroughCasesInSwitch": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"]
    }
  },
  "include": ["src/**/*.ts", "src/**/*.d.ts", "src/**/*.vue", "src/types/**/*.d.ts"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

#### tsconfig.node.json

```json
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "ESNext",
    "lib": ["ES2023"],
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "strict": true
  },
  "include": ["vite.config.ts"]
}
```

#### tailwind.config.js

```js
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: { extend: {} },
  plugins: [],
}
```

#### postcss.config.js

```js
export default {
  plugins: {
    tailwindcss: {},
    autoprefixer: {},
  },
}
```

#### eslint.config.js

使用 ESLint 扁平化配置（flat config），包含 `eslint-plugin-vue`、`@vue/eslint-config-typescript`、`@vue/eslint-config-prettier`。

#### .eslintignore

```
public
dist
*.d.ts
package.json
```

#### .prettierrc

```json
{
  "semi": false,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "all",
  "printWidth": 100,
  "arrowParens": "always",
  "endOfLine": "lf"
}
```

#### .gitignore

```
node_modules
```

#### CLAUDE.md

LLM 的行为指南文件，包含对话语言默认为中文、先思考再编码、简单至上、精准修改、目标驱动执行等规则。

---

### 4.2 src/ 源码目录

#### src/main.ts — 应用入口

```ts
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import './styles/global.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
```

功能要点：
- 完整引入 Element Plus（中文语言包）+ 全局注册所有图标
- 注册 Pinia（状态管理）
- 注册 Vue Router
- 导入全局样式

#### src/App.vue — 根组件

```vue
<template>
  <router-view />
</template>
```

最简单的根组件，仅包含 `<router-view />`。

#### src/env.d.ts — 环境类型声明

```ts
/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<object, object, unknown>
  export default component
}
```

---

#### src/api/request.ts — HTTP 请求封装

封装 Axios 实例，包含：
- **baseURL**: `/api`
- **timeout**: 15000ms
- **请求拦截器**：自动携带 `Bearer` token（从 localStorage 读取）
- **响应拦截器**：
  - `code !== 200` → ElMessage 错误提示
  - `401` → 清除 token → 跳转登录页 → 提示 "登录已过期"
  - 网络异常 → ElMessage 提示
- 导出泛型接口 `ApiResponse<T>`：`{ code, message, data }`

#### src/api/auth.ts — 认证接口

| 函数 | 方法 | URL | 说明 |
|------|------|-----|------|
| `login(data)` | POST | `/auth/login` | 登录，参数 `{ username, password }` |
| `getInfo()` | GET | `/auth/info` | 获取当前用户信息 |

类型定义：
- `LoginParams`: `{ username, password }`
- `LoginResult`: `{ token, userId, username, nickname }`
- `UserInfo`: `{ id, username, nickname, email, phone, status }`

#### src/api/user.ts — 用户管理接口

| 函数 | 方法 | URL | 说明 |
|------|------|-----|------|
| `getUserPage(params)` | GET | `/users` | 分页查询用户 |
| `getUserById(id)` | GET | `/users/{id}` | 根据 ID 查询 |
| `saveUser(data)` | POST | `/users` | 新增用户 |
| `updateUser(id, data)` | PUT | `/users/{id}` | 更新用户 |
| `deleteUser(id)` | DELETE | `/users/{id}` | 删除用户 |

类型定义：
- `UserPageParams`: `{ page, size, keyword? }`
- `UserRecord`: `{ id, username, nickname, email, phone, status, createTime }`
- `UserPageResult`: `{ records: UserRecord[], total }`
- `UserSaveParams`: `{ id?, username, password?, nickname?, email?, phone?, status? }`

---

#### src/router/index.ts — 路由配置

| 路径 | 名称 | 组件 | 说明 |
|------|------|------|------|
| `/login` | Login | `views/Login.vue` | 登录页，无需认证 |
| `/` | - | `views/Layout.vue` | 主布局，需认证 |
| `/users` | UserList | `views/user/UserList.vue` | 用户管理（子路由） |

路由模式：**Hash 模式** (`createWebHashHistory`)

路由守卫逻辑：
- 访问 `/login`：有 token → 跳转 `/`；无 token → 放行
- 访问其他页面：有 token → 放行；无 token → 跳转 `/login`

---

#### src/stores/user.ts — 用户状态（Pinia）

```ts
export const useUserStore = defineStore('user', {
  state: () => ({ info: null }),
  actions: {
    fetchInfo()  // 获取用户信息
    logout()     // 清除 token，清空 info，跳转 /login
  },
})
```

---

#### src/utils/auth.ts — Token 工具

| 函数 | 说明 |
|------|------|
| `getToken()` | 从 localStorage 读取 token，key 为 `hedgehog-token` |
| `setToken(token)` | 写入 token 到 localStorage |
| `removeToken()` | 从 localStorage 移除 token |

---

#### src/styles/global.css — 全局样式

```css
@tailwind base;
@tailwind components;
@tailwind utilities;

html, body, #app {
  height: 100%;
}
```

---

#### src/types/*.d.ts — 自动生成的类型声明

由 `unplugin-auto-import` 和 `unplugin-vue-components` 自动生成，提供全局 API 和组件的 TypeScript 类型支持。可以直接提交到 Git 仓库。

---

#### src/views/Login.vue — 登录页

功能：
- 居中卡片式登录表单（400px 宽）
- 用户名 + 密码输入框（带 User/Lock 图标）
- 表单验证（用户名和密码必填）
- Enter 键快捷登录
- 调用 `login` API → 保存 token → 跳转首页
- 默认账号：`admin / admin123`
- 登录按钮带 loading 状态

#### src/views/Layout.vue — 主布局

功能：
- 左侧可折叠侧边栏（el-aside），宽度 200px / 64px
- 侧边栏包含菜单（el-menu），路由模式
- 深色背景 `#304156`，文字 `#bfcbd9`，激活色 `#409EFF`
- 顶部 Header（el-header）：折叠按钮 + 用户下拉菜单（退出登录）
- 主内容区（el-main）：`<router-view />` 嵌套渲染子路由
- 用户信息从 `userStore` 获取

#### src/views/user/UserList.vue — 用户管理页

功能：
- **搜索栏**：关键字输入框 + 搜索/重置按钮（内联表单）
- **数据表格**（el-table）：
  - 列：ID、用户名、昵称、邮箱、手机号、状态（tag 显示）、创建时间、操作
  - 带 loading 加载状态、边框、斑马纹
  - 操作列：编辑（link 按钮）、删除（link 按钮，红色）
- **分页组件**（el-pagination）：支持切换页码、每页条数（10/20/50）
- **新增/编辑弹窗**（el-dialog）：
  - 表单字段：用户名、密码（编辑时选填）、昵称、邮箱、手机号、状态（radio）
  - 表单验证：用户名必填；新增时密码必填，编辑时密码选填
  - 编辑模式：用户名 disabled，密码 placeholder "留空则不修改"
  - 提交带 loading 状态
  - 关闭弹窗时重置表单
- 删除操作：ElMessageBox 二次确认

---

## 五、如何根据此文档重建项目

### 步骤 1：初始化项目

```bash
mkdir hedgehog-web && cd hedgehog-web
npm init -y
```

修改 `package.json`，将 `name` 设为 `hedgehog-web`，添加 `"type": "module"`, `"private": true`。

### 步骤 2：安装依赖

```bash
# 生产依赖
npm install vue@^3.4.21 vue-router@^4.3.0 pinia@^2.1.7 axios@^1.6.8 element-plus@^2.7.0 @element-plus/icons-vue@^2.3.1

# 开发依赖
npm install -D @vitejs/plugin-vue@^5.0.4 vite@^5.2.0 typescript@~5.4.0 vue-tsc@^2.0.0 \
  tailwindcss@^3.4.0 autoprefixer@^10.4.19 postcss@^8.4.38 \
  eslint@^8.57.1 @eslint/js@^8.57.1 eslint-plugin-vue@^9.24.0 \
  @vue/eslint-config-typescript@^13.0.0 @vue/eslint-config-prettier@^9.0.0 prettier@^3.2.0 \
  unplugin-vue-components unplugin-auto-import
```

### 步骤 3：创建配置文件

按顺序创建以下文件，内容参照第四章各文件说明：

1. `vite.config.ts`
2. `tsconfig.json`
3. `tsconfig.node.json`
4. `tailwind.config.js`
5. `postcss.config.js`
6. `eslint.config.js`
7. `.eslintignore`
8. `.prettierrc`
9. `.gitignore`
10. `index.html`

### 步骤 4：创建源码目录和文件

```bash
mkdir -p src/{api,router,stores,utils,styles,types,views/user}
```

按顺序创建以下文件，内容参照第四章：

| 序号 | 文件 | 说明 |
|------|------|------|
| 1 | `src/main.ts` | 应用入口 |
| 2 | `src/App.vue` | 根组件 |
| 3 | `src/env.d.ts` | 类型声明 |
| 4 | `src/styles/global.css` | 全局样式 |
| 5 | `src/utils/auth.ts` | Token 工具 |
| 6 | `src/api/request.ts` | HTTP 封装 |
| 7 | `src/api/auth.ts` | 认证接口 |
| 8 | `src/api/user.ts` | 用户接口 |
| 9 | `src/router/index.ts` | 路由配置 |
| 10 | `src/stores/user.ts` | 用户状态 |
| 11 | `src/views/Login.vue` | 登录页 |
| 12 | `src/views/Layout.vue` | 主布局 |
| 13 | `src/views/user/UserList.vue` | 用户管理 |

### 步骤 5：生成自动导入类型声明

```bash
npx vite build
```

这会自动在 `src/types/` 下生成 `auto-imports.d.ts` 和 `components.d.ts`。

### 步骤 6：启动开发服务器

```bash
npm run dev
```

访问 `http://localhost:3000`。

---

## 六、路由与页面关系图

```
/login              →  Login.vue        (独立页面)
/                   →  Layout.vue       (主布局框架)
  /users            →  UserList.vue     (用户管理，嵌套在 Layout 中)
```

---

## 七、数据流图

```
Login.vue
  └─ login() ──→ api/auth.ts ──→ POST /api/auth/login
                    ↓
              setToken() ──→ localStorage
                    ↓
              跳转 /users

Layout.vue
  └─ onMounted: userStore.fetchInfo() ──→ api/auth.ts ──→ GET /api/auth/info
                    ↓
              显示用户昵称

UserList.vue
  ├─ fetchData() ──→ api/user.ts ──→ GET /api/users
  ├─ saveUser() ──→ api/user.ts ──→ POST /api/users
  ├─ updateUser() ──→ api/user.ts ──→ PUT /api/users/{id}
  └─ deleteUser() ──→ api/user.ts ──→ DELETE /api/users/{id}
```

---

*本文档由项目自动分析生成，日期：2026-05-13*
