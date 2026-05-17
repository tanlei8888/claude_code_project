# CLAUDE.md

Hedgehog 个人博客全栈项目总控文档。默认使用中文对话交流。

---

## 一、项目总览

```
hedgehog-project/
├── hedgehog-server/     # Spring Boot 2.7.18 / Java 8 / Maven / MyBatis Plus 3.5.5
├── hedgehog-web/        # Vue 3.4 + TS 后台管理（Element Plus 2.7 + Tailwind CSS 3.4）
└── hedgehog-blog/       # Vue 3.4 + TS 博客前台（纯 Tailwind CSS，无 UI 库）
```

| 项目 | 端口 | 说明 |
|------|------|------|
| hedgehog-server | 8080 | 统一 RESTful API，服务前后台 |
| hedgehog-web | 3000 | 管理员后台，Vite proxy `/api` → 8080 |
| hedgehog-blog | 3001 | 博客前台，Vite proxy `/api` → 8080 |

**规则文件：** `.claude/rules/` 按子项目自动加载（spring-boot-claude-stack.md / vue-claude-stack.md / tech-stack-*.md / code-comments.md）

---

## 二、后端架构

### 2.1 功能分包结构（Package by Feature）

```
com.hedgehog/
├── HedgehogApplication.java    # 启动类（无需 @MapperScan，@Mapper 注解自动发现）
├── common/         Result, ResultCode
├── config/         JwtAuthFilter, AdminInterceptor, UserContext, WebConfig,
│                   AppProperties, DataInitializer, SecurityConfig, MyBatisPlusConfig
├── exception/      BusinessException, GlobalExceptionHandler
├── util/           JwtUtil, MarkdownUtil, SlugUtil
├── annotation/     @AdminRequired
├── auth/           AuthController, AuthService/Impl, LoginRequest/Response
├── article/        ArticleController, BlogArticleService/Impl, BlogArticle,
│                   ArticleSaveRequest, BlogArticleTag, BlogArticleMapper, BlogArticleTagMapper
├── category/       CategoryController, BlogCategoryService/Impl, BlogCategory, BlogCategoryMapper
├── tag/            TagController, BlogTagService/Impl, BlogTag, BlogTagMapper
├── comment/        CommentController, BlogCommentService/Impl, BlogComment, BlogCommentMapper
├── like/           LikeController, BlogLikeService/Impl, BlogLike, BlogLikeMapper
├── user/           UserController, UserService/Impl, User, UserMapper
├── media/          MediaController, SysMediaService/Impl, SysMedia, SysMediaMapper
├── site/           SiteConfigController, SiteConfigService/Impl, SiteConfig, SiteConfigMapper
└── dashboard/      DashboardController
```

### 2.2 数据库

- `hedgehog_dev`（开发）/ `hedgehog_prod`（生产）
- Flyway 迁移脚本：`db/migration/V1__initial_schema.sql`（替代 spring.sql.init）
- 表：sys_user, blog_category, blog_tag, blog_article, blog_article_tag, blog_comment, blog_like, sys_media, site_config
- sys_media.media_type：`CONTENT`（文章配图）| `AVATAR`（用户头像）| `PRIVATE`（私密）
- 全表使用 utf8mb4 + 逻辑删除（deleted=0/1）

### 2.3 关键设计决策

- **认证：** JWT（jjwt 0.12.5）+ BCrypt。JwtAuthFilter 全局解析 token → UserContext → @AdminRequired + AdminInterceptor 检查角色。不使用 Spring Security
- **白名单路径：** /api/auth/**, /api/articles, /api/categories, /api/tags, GET /api/comments, /api/site/config, /api/admin/upload/**
- **密码加密：** BCryptPasswordEncoder 注册为 Bean，构造器注入，禁止 `new`
- **配置属性：** AppProperties（@ConfigurationProperties 前缀 `app`），禁止 @Value 散落
- **Markdown：** flexmark 后端渲染 content_md → content_html，前端 v-html
- **Slug：** pinyin4j 中文标题自动生成拼音 slug，手动优先
- **评论：** 2 层嵌套（顶级 + 二级回复），分页取顶级 + 批量取子回复
- **文件上传：** 本地 uploads/，仅 jpeg/png/gif/webp/svg，上限 10MB
- **API 响应：** 统一 `Result<T>` 包装 `{ code, message, data }`
- **API 文档：** Springdoc OpenAPI，启动后访问 `/swagger-ui.html`
- **异常处理：** 禁止空 catch；Stream 只做转换，副作用用 for 循环

---

## 三、API 路由

### 3.1 公开接口

```
POST   /api/auth/login              # 登录 → token/userId/username/nickname/role
POST   /api/auth/register           # 注册
GET    /api/articles                # 已发布文章列表 ?page=&size=&categoryId=&tagId=&keyword=
GET    /api/articles/{slug}         # 文章详情
GET    /api/categories              # 分类列表
GET    /api/tags                    # 标签列表
GET    /api/comments                # 评论树 ?articleId=&page=&size=
GET    /api/site/config             # 站点配置
```

### 3.2 需登录

```
GET    /api/auth/info               # 当前用户信息
PUT    /api/auth/profile            # 修改资料（nickname/email/phone/avatar/bio/password）
GET    /api/auth/avatars            # 可选头像列表（media_type=AVATAR）
POST   /api/comments                # 发表评论
POST   /api/likes/{articleId}       # 点赞 toggle
GET    /api/likes/status            # 点赞状态 ?articleIds=1,2,3
```

### 3.3 管理员接口（/api/admin/*，需 role=ADMIN）

```
# 仪表盘
GET    /api/admin/dashboard

# 文章
GET    /api/admin/articles          # 全部文章（含草稿/定时/私密）
POST   /api/admin/articles          # 创建
PUT    /api/admin/articles/{id}     # 更新
DELETE /api/admin/articles/{id}     # 删除
PUT    /api/admin/articles/{id}/status  # 修改状态
PUT    /api/admin/articles/{id}/top     # 切换置顶

# 分类 / 标签 CRUD
POST|PUT|DELETE  /api/admin/categories[/{id}]
POST|PUT|DELETE  /api/admin/tags[/{id}]

# 评论管理
GET    /api/admin/comments          # 全部评论
PUT    /api/admin/comments/{id}     # 审核
DELETE /api/admin/comments/{id}     # 删除

# 用户管理
GET|POST      /api/users
GET|PUT|DELETE /api/users/{id}

# 媒体管理 + 上传
GET    /api/admin/media             # 媒体列表
POST   /api/admin/upload            # 上传（multipart，可选 mediaType 参数）
PUT    /api/admin/media/{id}        # 修改媒体类型
DELETE /api/admin/media/{id}        # 删除

# 站点配置
PUT    /api/admin/site/config
```

---

## 四、前端路由

### 4.1 hedgehog-blog（博客前台）

```
/                  首页 — 文章列表 + 分类/标签筛选 + 分页 + 搜索
/article/:slug     文章详情 — Markdown + 代码高亮 + 目录 + 评论 + 点赞
/category/:slug    分类文章列表（复用 Home）
/tag/:slug         标签文章列表（复用 Home）
/about             关于页
/login /register   登录/注册
/profile           个人中心 — 头像选择器 + 资料编辑
```

组件树：App.vue → TheHeader（头像+昵称可点击） + `<router-view/>` + TheFooter  
核心组件：ArticleCard, MarkdownRenderer, CommentSection, TocSidebar  
设计：极简科技风，白色底留白，标题 32px+，正文 16px 行高 1.8

### 4.2 hedgehog-web（后台管理）

```
/login                 登录
/                      主布局 Layout
/dashboard             仪表盘 — 统计卡片 + 最近文章
/articles              文章列表 — 表格 + 状态筛选
/articles/create       创建文章 — v-md-editor
/articles/:id/edit     编辑文章
/categories /tags      分类/标签管理 — 表格 + 弹窗
/users                 用户管理 — 表格含角色/头像/简介
/comments              评论管理 — 表格 + 审核
/media                 媒体管理 — 网格 + 上传带分类选择（内容/头像/私密）
/site                  站点配置 — 表单 + 关于页
```

侧边栏：仪表盘 → 内容管理(文章/分类/标签) → 用户管理 → 评论管理 → 媒体管理 → 站点配置

---

## 五、编码约定

- **前端：** 禁止 Options API，统一 `<script setup lang="ts">`；接口类型与 API 函数同文件；错误在 Axios 拦截器统一 toast 提示，业务代码只做状态恢复不做额外弹窗
- **后端：** 禁止 @Autowired 字段注入，统一构造器注入；禁止空 catch；Stream 用于转换不用副作用
- **注释：** Java 用中文 Javadoc，Vue/TS 用中文单行注释（详见 `.claude/rules/code-comments.md`）
- **修改原则：** 只改必须改的，匹配现有风格，不为一次使用创建抽象
