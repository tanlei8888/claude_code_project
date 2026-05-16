# Vue 项目 — Claude Code 指令

## 技术栈
- Vue 3.4+，使用 Composition API（`<script setup>`）
- 非 Nuxt 项目（普通 Vite + Vue SPA）
- TypeScript 5.4（非严格模式）
- Pinia 2.1 状态管理
- Vue Router 4.3（Hash 模式）/ Nuxt 文件路由
- Axios 1.6（HTTP 请求）
- Tailwind CSS 3.4
- 两个子项目：hedgehog-web（含 Element Plus 2.7）、hedgehog-blog（纯 Tailwind，无 UI 库）

## 代码风格

### 组件
- 始终使用 `<script setup lang="ts">`，禁止 Options API
- 单文件单组件，文件名 PascalCase 与组件名一致
- Props：使用 `defineProps<T>()` 配合 TypeScript 接口，不用运行时声明
- Emits：使用 `defineEmits<T>()` 配合类型化事件
- v-model 绑定使用 `defineModel()`
- 模板顺序：`<script>`、`<template>`、`<style>`

### Composables
- 命名前缀 `use`（如 `useAuth`、`useCart`）
- 返回响应式 ref 和函数，不返回裸值
- 支持 `MaybeRef` 参数以增强灵活性
- 放在 `composables/` 目录下

### 状态管理（Pinia）
- 使用 `defineStore` 的 setup 语法（非 options 写法）
- Store 命名遵循 `use___Store` 模式
- 每个 Store 聚焦一个业务域
- 解构时使用 `storeToRefs()`

### TypeScript
- 禁止 `any`，用 `unknown` 然后类型收窄
- 接口可定义在 `api/` 目录（与请求函数同文件）或 `types/` 目录
- 检查对象字面量可使用 `satisfies`
- 优先用 `type`，需要扩展时才用 `interface`

### 文件结构
```
hedgehog-web / hedgehog-blog/
├── src/
│   ├── api/          # API 请求函数 + 接口类型定义
│   ├── components/   # 公共组件
│   ├── composables/  # 组合式函数
│   ├── stores/       # Pinia Store
│   ├── types/        # 全局 TypeScript 类型
│   ├── utils/        # 纯工具函数
│   ├── views/        # 页面组件（按路由分组）
│   ├── router/       # 路由配置
│   ├── App.vue       # 根组件
│   └── main.ts       # 应用入口
├── vite.config.ts
└── tailwind.config.js
```

## 命名规范

| 对象 | 规范 | 示例 |
|------|------|------|
| 组件 | PascalCase | `UserProfile.vue` |
| Composables | camelCase + `use` 前缀 | `useAuth.ts` |
| Store | camelCase + `useStore` | `useCartStore.ts` |
| 工具函数 | camelCase | `formatDate.ts` |
| 类型 | PascalCase | `UserProfile.ts` |
| API 接口 | kebab-case | `user-profile.ts` |
| 页面 | kebab-case 或 PascalCase | `user-profile.vue` |
| CSS 类名 | kebab-case（Tailwind） | `text-primary` |

## 常用模式

### API 调用（Axios）
```ts
// src/api/request.ts — Axios 实例，含 baseURL=/api + 拦截器
import request from './request'

// GET 请求 — 参数类型 + 返回类型明确
export function getUserList(params: { page: number; size: number }): Promise<ApiResponse<User[]>> {
  return request.get('/users', { params })
}

// POST 请求
export function createUser(data: UserSaveParams): Promise<ApiResponse<null>> {
  return request.post('/users', data)
}
```

### 错误处理
```ts
// 全局错误由 Axios 响应拦截器统一处理（toast / message 提示）
// 业务代码仅需 catch 以阻止异常传播和恢复局部状态
try {
  await someApi()
  // 成功路径：更新状态
} catch {
  // 错误消息已由拦截器展示，此处仅防止状态错乱（不清空输入、不递增计数等）
} finally {
  loading.value = false
}

// 避免以下写法：
// - try...finally 无 catch（异常会变成未处理 rejection）
// - catch {} 空块（静默吞掉错误，用户无感知）
```

## 禁止事项
- 禁止 Options API 和 mixins
- 禁止在 `<script setup>` 中使用 `this`
- 禁止直接修改 props
- 禁止对用户输入使用 `v-html`，markdowmn 渲染使用安全的库（如 marked + highlight.js）
- 禁止将业务逻辑写在组件中 — 提取到 composables
- 禁止使用 `any` 类型
- 禁止新文件不写 TypeScript
- 禁止为只使用一次的代码创建抽象
