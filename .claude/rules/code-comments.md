---
paths:
  - "hedgehog-server/**"
  - "hedgehog-web/**"
  - "hedgehog-blog/**"
---

# 后端代码注释规范

编写 Java 代码时必须自动添加注释。本规范适用于 hedgehog-server 所有 Java 源文件。

## 通用原则

1. **必须使用中文注释**
2. **注释必须准确反映代码意图**，不得猜测或编造
3. **简洁优先**：一句话能说清的不写两句
4. **不自解释的不注释**：如 `getUserId()` 不需要"获取用户ID"
5. **所有新编写的 Java 文件必须包含注释**

## 类注释（必须）

每个类、接口、枚举、注解必须包含类级别 Javadoc：

```java
/**
 * [一句话类职责描述]。
 *
 * <p>[可选补充说明]。
 */
```

## 方法注释（必须）

所有 public/protected 方法必须有 Javadoc：

```java
/**
 * [方法功能描述]。
 *
 * @param paramName [参数说明]
 * @return [返回值说明]
 * @throws XxxException [异常触发条件]
 */
```

以下情况可省略方法注释：
- `@Override` 方法且父类/接口契约已充分说明
- 简单的 getter/setter
- MyBatis Plus Mapper 接口的空方法（由框架自动生成实现）

## 字段注释（按需）

字段含义不明显时必须注释：

```java
/** 文章状态：0=草稿 1=已发布 2=定时发布 3=私密 */
private Integer status;

/** 非数据库字段：关联标签列表 */
@TableField(exist = false)
private List<BlogTag> tags;
```

枚举常量必须逐项注释含义。

## 行内注释

仅用于非显而易见的逻辑、特殊处理或算法步骤：

```java
// 未登录用户跳过点赞状态查询
if (userId == null) return;
```

## 各层级注释要求

| 层级 | 类注释 | 方法注释 | 字段注释 |
|------|:------:|:------:|:------:|
| common / util / exception | 必须 | public 方法必须 | 必须 |
| config | 必须 | @Bean 方法 / 关键逻辑必须 | 注入字段按需 |
| entity（DB 实体） | 必须 | 不适用 | 每个字段必须（说明数据库含义） |
| DTO / Request | 必须 | 不适用 | 每个字段必须 |
| mapper | 必须 | 不适用 | 不适用 |
| service 接口 | 必须 | 必须（契约定义） | 不适用 |
| service 实现 | 必须 | 非覆盖方法必须，覆盖方法按需 | 注入字段按需 |
| controller | 必须 | 必须（标注 HTTP 方法+路径） | 注入字段按需 |

## 反例（禁止的注释风格）

```java
// ❌ 废话注释
/** 设置用户名 */
public void setUsername(String username) { ... }

// ❌ 英文注释（本项目统一使用中文）
/** Get user by id */
public User getById(Long id) { ... }

// ❌ 猜测性注释
/** 可能用于权限检查？ */
private String role;
```

---

# 前端代码注释规范

编写 Vue 3 + TypeScript 代码时必须自动添加注释。本规范适用于 hedgehog-web 和 hedgehog-blog 所有 `.vue`、`.ts` 源文件。

## 通用原则

1. **必须使用中文注释**
2. **注释必须准确反映代码意图**，不得猜测或编造
3. **简洁优先**：一句话能说清的不写两句
4. **不自解释的不注释**：如 `const userName = ref('')` 不需要"用户名"
5. **所有新编写的 Vue 组件和 TypeScript 模块必须包含注释**

## Vue 组件注释（必须）

每个 `.vue` 组件的 `<script>` 顶部必须有组件职责描述：

```typescript
// 文章卡片组件 — 展示文章摘要、标签、作者信息，支持悬停动效
```

```typescript
// 全局页脚 — 显示备案号、社交链接和版权信息
```

## 组合式函数（Composable）注释（必须）

每个 `src/composables/` 下的函数必须有注释：

```typescript
// 统一管理页面 SEO 标签：title、description、keywords、Open Graph
export function useSeo(input: SeoInput) { ... }
```

```typescript
// 注入 JSON-LD 结构化数据到 <head>，供百度/Google 富文本搜索结果使用
export function useJsonLd(json: Record<string, unknown>) { ... }
```

## 路由配置注释（必须）

路由定义中关键路径必须注释其用途：

```typescript
{ path: '/article/:slug', name: 'ArticleDetail', component: () => import('@/views/ArticleDetail.vue') },
// 分类筛选页 — 复用 Home 组件，通过路由参数 slug 过滤
{ path: '/category/:slug', name: 'CategoryArticles', ... },
```

## 响应式状态注释（按需）

状态含义不明显时必须注释：

```typescript
const activeFilter = ref<string | null>(null) // 'category' | 'tag' | null，当前激活的筛选类型
const liked = ref(false)                       // 当前用户是否已点赞
```

## Props 与 Emits 注释（必须）

所有 props 和 emits 定义必须逐项注释：

```typescript
interface Props {
  article?: Article      // 文章对象，undefined 时显示骨架屏
  loading?: boolean       // 是否显示加载态
}

const emit = defineEmits<{
  like: [id: number]     // 点赞事件，携带文章 ID
}>()
```

## Store 注释（必须）

Pinia store 的 state、getter、action 必须注释用途：

```typescript
export const useUserStore = defineStore('user', () => {
  const token = ref('')                      // JWT 令牌，持久化在 localStorage
  const info = ref<UserInfo | null>(null)    // 当前用户信息，登录后填充

  /** 验证 token 是否有效，返回 true 表示已登录 */
  function isLoggedIn(): boolean { ... }
})
```

## API 模块注释（必须）

请求函数和拦截器关键逻辑必须注释：

```typescript
// 响应拦截器：code 非 200 统一弹窗提示，401 清空 token 并跳转登录
request.interceptors.response.use(...)
```

## 模板注释（按需）

仅当 DOM 结构存在特殊设计意图时在模板中使用 `<!-- -->`：

```html
<!-- 移动端下拉菜单，仅在 sm 断点以下显示 -->
<div v-if="menuOpen" class="sm:hidden ...">
```

## 各文件类型注释要求

| 文件类型 | 文件头注释 | 导出函数/组件 | 内部逻辑 |
|----------|:--------:|:--------:|:------:|
| `.vue` 组件 | 必须（组件职责） | 必须（props/emits） | 关键逻辑按需 |
| `src/composables/*.ts` | 必须（函数用途） | 必须 | 算法/特殊处理按需 |
| `src/stores/*.ts` | 必须 | state/getter/action 必须 | 关键逻辑按需 |
| `src/api/*.ts` | 必须（模块用途） | 必须 | 拦截器逻辑必须 |
| `src/router/*.ts` | 必须 | 关键路由必须 | 导航守卫逻辑必须 |
| `src/utils/*.ts` | 必须（工具用途） | 每个导出函数必须 | 算法按需 |
| `src/types/*.ts` | 必须 | interface/enum 字段按需 | 不适用 |

## 反例（禁止的注释风格）

```typescript
// ❌ 废话注释
const count = ref(0) // 计数器

// ❌ 英文注释（本项目统一使用中文）
// fetch user info from server

// ❌ 猜测性注释
// 这个字段大概和权限有关

// ❌ 大段注释掉的代码（用 git 找回，不要留在代码里）
// const oldFn = () => { ... }
```
