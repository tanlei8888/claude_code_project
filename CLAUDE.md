# CLAUDE.md

Hedgehog 个人博客全栈项目总控文档。

---

## 一、项目总览

```
hedgehog-project/
├── hedgehog-server/     # Spring Boot 2.7.18 后端，Java 8 / Maven / MyBatis Plus
├── hedgehog-web/        # Vue 3 + TS 后台管理系统（Element Plus + Tailwind CSS）
└── hedgehog-blog/       # Vue 3 + TS 博客前台（纯 Tailwind CSS，无 Element Plus）
```

### 三者关系

- `hedgehog-server` 提供统一 RESTful API，同时服务博客前台和后台管理
- `hedgehog-web` 是管理员后台，依赖 Element Plus，用于内容管理
- `hedgehog-blog` 是面向读者的博客前台，纯 Tailwind CSS 保持设计自由度，极简科技风

### 端口约定

| 项目 | 端口 | 说明 |
|------|------|------|
| hedgehog-server | 8080 | Spring Boot |
| hedgehog-web | 3000 | Vite dev，proxy `/api` → localhost:8080 |
| hedgehog-blog | 3001 | Vite dev，proxy `/api` → localhost:8080 |

### 统一技术栈

详细技术栈见 `.claude/rules/`，通过 paths 限定按子项目自动加载：

| 子项目 | 规则文件 |
|--------|---------|
| hedgehog-server | `tech-stack-backend.md` — Spring Boot 2.7 + Java 8 + MyBatis Plus + JWT + flexmark |
| hedgehog-web | `tech-stack-frontend.md` — Vue 3 + TS + Element Plus + Tailwind CSS |
| hedgehog-blog | `tech-stack-frontend.md` — Vue 3 + TS + Tailwind CSS（纯）+ marked |

---

## 二、数据库设计

数据库：`hedgehog_dev`（开发）/ `hedgehog_prod`（生产）

### 2.1 现有表（需扩展）

**sys_user** — 新增字段：`role`、`avatar`、`bio`

```sql
ALTER TABLE sys_user
  ADD COLUMN role VARCHAR(10) NOT NULL DEFAULT 'USER' COMMENT 'ADMIN|USER',
  ADD COLUMN avatar VARCHAR(500) COMMENT '头像URL',
  ADD COLUMN bio VARCHAR(500) COMMENT '个人简介';
```

### 2.2 新增表

**blog_category**（文章分类）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| name | VARCHAR(50) NOT NULL | 分类名称 |
| slug | VARCHAR(50) NOT NULL UNIQUE | URL 标识 |
| description | VARCHAR(200) | 分类描述 |
| sort_order | INT DEFAULT 0 | 排序 |
| create_time | DATETIME DEFAULT CURRENT_TIMESTAMP | |
| update_time | DATETIME ON UPDATE CURRENT_TIMESTAMP | |
| deleted | TINYINT DEFAULT 0 | 逻辑删除 |

**blog_tag**（标签）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| name | VARCHAR(30) NOT NULL | 标签名 |
| slug | VARCHAR(30) NOT NULL UNIQUE | URL 标识 |
| create_time | DATETIME DEFAULT CURRENT_TIMESTAMP | |
| update_time | DATETIME ON UPDATE CURRENT_TIMESTAMP | |
| deleted | TINYINT DEFAULT 0 | 逻辑删除 |

**blog_article**（文章）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| title | VARCHAR(200) NOT NULL | 标题 |
| slug | VARCHAR(200) NOT NULL UNIQUE | URL 标识 |
| summary | VARCHAR(500) | 摘要 |
| content_md | LONGTEXT NOT NULL | Markdown 原文 |
| content_html | LONGTEXT NOT NULL | 渲染后 HTML |
| cover_image | VARCHAR(500) | 封面图 URL |
| category_id | BIGINT | 外键 blog_category.id |
| status | INT DEFAULT 0 | 0=草稿 1=已发布 2=定时发布 3=私密 |
| is_top | TINYINT DEFAULT 0 | 是否置顶 |
| view_count | BIGINT DEFAULT 0 | 阅读次数 |
| like_count | BIGINT DEFAULT 0 | 点赞数（冗余） |
| comment_count | BIGINT DEFAULT 0 | 评论数（冗余） |
| publish_time | DATETIME | 定时发布时间 |
| author_id | BIGINT NOT NULL | 外键 sys_user.id |
| create_time | DATETIME DEFAULT CURRENT_TIMESTAMP | |
| update_time | DATETIME ON UPDATE CURRENT_TIMESTAMP | |
| deleted | TINYINT DEFAULT 0 | 逻辑删除 |

**blog_article_tag**（文章-标签关联）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| article_id | BIGINT NOT NULL | 外键 blog_article.id |
| tag_id | BIGINT NOT NULL | 外键 blog_tag.id |

UNIQUE(article_id, tag_id)

**blog_comment**（评论，2层嵌套）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| article_id | BIGINT NOT NULL | 外键 blog_article.id |
| user_id | BIGINT NOT NULL | 外键 sys_user.id |
| parent_id | BIGINT | 父评论ID（NULL=顶级，非NULL=回复） |
| reply_to_user_id | BIGINT | 回复目标用户ID |
| content | TEXT NOT NULL | 评论内容 |
| status | INT DEFAULT 0 | 0=待审核 1=已通过 2=已拒绝 |
| ip_address | VARCHAR(50) | 评论者IP |
| create_time | DATETIME DEFAULT CURRENT_TIMESTAMP | |
| update_time | DATETIME ON UPDATE CURRENT_TIMESTAMP | |
| deleted | TINYINT DEFAULT 0 | 逻辑删除 |

**blog_like**（点赞）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| article_id | BIGINT NOT NULL | 外键 blog_article.id |
| user_id | BIGINT NOT NULL | 外键 sys_user.id |
| create_time | DATETIME DEFAULT CURRENT_TIMESTAMP | |

UNIQUE(article_id, user_id)

**sys_media**（媒体资源）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| filename | VARCHAR(200) NOT NULL | 原始文件名 |
| path | VARCHAR(500) NOT NULL | 存储相对路径 |
| url | VARCHAR(500) NOT NULL | 访问 URL |
| file_size | BIGINT | 字节数 |
| mime_type | VARCHAR(100) | MIME 类型 |
| upload_user_id | BIGINT | 上传者 |
| create_time | DATETIME DEFAULT CURRENT_TIMESTAMP | |
| deleted | TINYINT DEFAULT 0 | 逻辑删除 |

**site_config**（站点配置，单行表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 固定为 1 |
| site_name | VARCHAR(100) | 博客名称 |
| site_subtitle | VARCHAR(200) | 副标题/Slogan |
| site_logo | VARCHAR(500) | Logo URL |
| site_favicon | VARCHAR(500) | Favicon URL |
| about_content_md | LONGTEXT | 关于页 Markdown |
| about_content_html | LONGTEXT | 关于页渲染 HTML |
| author_name | VARCHAR(50) | 作者名 |
| author_avatar | VARCHAR(500) | 作者头像 |
| author_bio | VARCHAR(500) | 作者简介 |
| social_github | VARCHAR(200) | |
| social_twitter | VARCHAR(200) | |
| social_zhihu | VARCHAR(200) | |
| icp_number | VARCHAR(50) | 备案号 |
| footer_text | VARCHAR(200) | 页脚文字 |
| create_time | DATETIME | |
| update_time | DATETIME | |

---

## 三、API 路由设计

所有响应用 `Result<T>` 包装：`{ code: 200, message: "success", data: ... }`

### 3.1 公开接口（无需登录）

```
POST   /api/auth/login              # 登录 → { token, userId, username, nickname, role }
POST   /api/auth/register           # 注册 → { username, password, nickname }

GET    /api/articles                # 文章列表（仅已发布） ?page=&size=&categoryId=&tagId=&keyword=
GET    /api/articles/{slug}         # 文章详情（浏览量+1，返回 tags、作者信息）

GET    /api/categories              # 全部分类列表
GET    /api/tags                    # 全部标签列表

GET    /api/comments                # 文章评论树 ?articleId=&page=&size= （仅 status=1 已通过）

GET    /api/site/config             # 站点配置（公开）
```

### 3.2 需登录（USER / ADMIN）

```
GET    /api/auth/info               # 当前用户信息（已有）
PUT    /api/auth/profile            # 修改个人信息（昵称/邮箱/头像/简介/密码）

POST   /api/comments                # 发表评论 { articleId, content, parentId?, replyToUserId? }
POST   /api/likes/{articleId}       # 点赞/取消点赞（toggle）
GET    /api/likes/status            # 批量查询点赞状态 ?articleIds=1,2,3
```

### 3.3 管理员接口（/api/admin/*，需 role=ADMIN）

```
# 文章管理
GET    /api/admin/articles          # 全部文章（含草稿/定时/私密） ?status=&page=&size=
POST   /api/admin/articles          # 创建文章
PUT    /api/admin/articles/{id}     # 更新文章
DELETE /api/admin/articles/{id}     # 删除文章
PUT    /api/admin/articles/{id}/status   # 修改状态
PUT    /api/admin/articles/{id}/top      # 切换置顶

# 分类管理
POST   /api/admin/categories        # 创建分类
PUT    /api/admin/categories/{id}   # 更新分类
DELETE /api/admin/categories/{id}   # 删除分类（分类下有文章时拒绝）

# 标签管理
POST   /api/admin/tags              # 创建标签
PUT    /api/admin/tags/{id}         # 更新标签
DELETE /api/admin/tags/{id}         # 删除标签

# 评论管理
GET    /api/admin/comments          # 全部评论 ?status=&page=&size=
PUT    /api/admin/comments/{id}     # 审核（通过/拒绝）
DELETE /api/admin/comments/{id}     # 删除评论

# 用户管理（已有）
GET    /api/users                   # 分页查询用户
GET    /api/users/{id}              # 用户详情
POST   /api/users                   # 新增用户
PUT    /api/users/{id}              # 修改用户
DELETE /api/users/{id}              # 删除用户

# 文件上传
POST   /api/admin/upload            # 上传文件（multipart/form-data）
GET    /api/admin/media             # 媒体列表 ?page=&size=
DELETE /api/admin/media/{id}        # 删除媒体

# 站点配置
PUT    /api/admin/site/config       # 更新站点配置（含关于页）
```

### 3.4 权限实现

```
JwtAuthFilter（全局）→ 解析 token，设 UserContext(userId, role)
    ↓
AdminInterceptor（@AdminRequired 注解）→ 检查 role == ADMIN，否则 403
```

JWT payload 扩展 `role` 字段。白名单路径包括：/api/auth/login、/api/auth/register、/api/articles、/api/categories、/api/tags、/api/comments、/api/site/config、/api/admin/upload/**（图片访问）。

---

## 四、前端路由设计

### 4.1 hedgehog-blog（博客前台）

```
/                   首页 — 文章列表 + 分类/标签筛选 + 分页
/article/:slug      文章详情 — Markdown 渲染 + 目录导航 + 评论区 + 点赞
/category/:slug     分类文章列表
/tag/:slug          标签文章列表
/about              关于页
/login              登录页（简洁，用户名+密码）
/register           注册页（用户名+密码+昵称）
```

**组件树：** App.vue → TheHeader + `<router-view/>` + TheFooter
**核心组件：** ArticleCard、MarkdownRenderer、CommentSection、TocSidebar

**设计要点：**
- 极简科技风：白色底、大量留白、精致排版
- 字体栈：`"PingFang SC", "Noto Sans SC", "Microsoft YaHei", sans-serif`
- 文章详情标题 32px+，正文 16px，行高 1.8
- 代码块：highlight.js + github-dark 主题
- 文章卡片：悬停微动效（阴影加深 + 轻微上移）
- 点赞按钮：心形图标 + 点击动画

### 4.2 hedgehog-web（后台管理）

```
/login             登录页（已有）

/                  主布局（已有 Layout.vue）
/dashboard          仪表盘 — 4 统计卡片 + 最近文章
/articles           文章列表 — 表格 + 状态筛选
/articles/create    创建文章 — Markdown 编辑器 + 发布设置
/articles/:id/edit  编辑文章
/categories         分类管理 — 表格 + 弹窗
/tags               标签管理 — 表格 + 弹窗
/users              用户管理（已有 UserList.vue）
/comments           评论管理 — 表格 + 审核
/media              媒体管理 — 网格 + 上传
/site               站点配置 — 表单 + 关于页编辑器
```

**侧边栏菜单结构：**
```
Hedgehog
├── 仪表盘      /dashboard
├── 内容管理
│   ├── 文章管理  /articles
│   ├── 分类管理  /categories
│   └── 标签管理  /tags
├── 用户管理    /users
├── 评论管理    /comments
├── 媒体管理    /media
└── 站点配置    /site
```

---

## 五、实施阶段

### 第一阶段：后端基础设施
- 执行 DDL 创建所有新表，扩展 sys_user 表
- 创建 Entity + Mapper（文章/分类/标签/评论/点赞/媒体/配置）
- 实现分类/标签基础 CRUD（Service + Controller）
- 实现文章基础 CRUD（含分类-标签关联、flexmark Markdown→HTML 转换）
- pom.xml 添加 flexmark 依赖
- **验证：** Postman 调 CRUD 接口，数据库记录正确

### 第二阶段：认证权限升级
- JWT payload 扩展 `role` 字段
- UserContext 增加 role 存取
- 新增 `@AdminRequired` 注解 + `AdminInterceptor`
- 新增 POST /api/auth/register 和 PUT /api/auth/profile
- 扩展白名单 + 配置拦截器注册
- DataInitializer 中管理员设 role=ADMIN
- **验证：** 普通用户访问管理接口 403，管理员正常

### 第三阶段：博客前台 MVP（hedgehog-blog）
- Vite 创建项目（参考 hedgehog-web 配置，去 Element Plus）
- 安装 marked + highlight.js + tailwind + pinia + vue-router + axios
- 搭建路由 + 导航栏 + 页脚
- 首页文章列表（ArticleCard + 分页）
- 文章详情页（MarkdownRenderer + TocSidebar）
- 登录/注册页
- Vite proxy 配置（端口 3001，代理 /api → 8080）
- **验证：** 首页展示文章，点击进详情，Markdown 渲染正常

### 第四阶段：评论 + 点赞
- 后端：评论发表 + 审核 + 评论树查询（分页取顶级 + 批量取回复）
- 后端：点赞 toggle + 同步更新 article.like_count
- 博客前台：CommentSection 组件（评论列表 + 发表 + 回复）
- 博客前台：点赞按钮（心形动画 + 未登录跳转登录）
- **验证：** 登录后可评论/回复/点赞，数据同步正确

### 第五阶段：后台管理全面实现
- 文章管理（列表 + Markdown 编辑器 + 状态管理 + 定时发布 + 置顶）
- 分类/标签管理（表格 + 弹窗）
- 评论管理（列表 + 审核操作）
- 媒体管理（上传 + 网格展示 + 复制 URL）
- 站点配置（表单 + 关于页编辑器）
- 仪表盘（统计卡片 + 最近文章）
- 定时发布：`@Scheduled` 每分钟检查 status=2 且 publish_time ≤ now 的文章
- **验证：** 全部 CRUD 正常，定时发布自动生效

### 第六阶段：打磨优化
- 博客前台响应式（手机端可阅读）
- 文章详情页 SEO（动态 title + meta description）
- 骨架屏/空状态/加载状态/错误状态
- CSS 细节打磨（过渡动画、hover 效果、间距一致）
- 修复已知 Bug
- **验证：** 375px 宽度正常浏览，所有空状态友好提示

---

## 六、关键设计决策

### 认证方案
沿用现有 JWT，扩展 role 字段，用 `@AdminRequired` 注解 + `AdminInterceptor` 区分权限等级。不使用 Spring Security。

### 文件存储
本地 `uploads/` 目录 + Spring Boot 静态资源映射 `/api/admin/upload/**` → `file:uploads/`。
仅允许 jpeg/png/gif/webp/svg，单文件最大 10MB。

### Markdown 渲染
**后端渲染为主：** 文章保存时 flexmark 将 content_md 转 content_html，前端直接用 v-html 渲染（已 XSS 过滤）。flexmark 启用：表格、代码高亮、目录生成、emoji 扩展。

### Slug 生成
引入 pinyin4j，从中文标题自动生成拼音 slug，用户可手动覆盖。

### 评论层级
仅支持 2 层（顶级评论 + 二级回复），不支持深层嵌套。
查询方式：分页取 parent_id IS NULL 的顶级评论，再批量查其所有回复。

### 代码规范
遵循 `.claude/rules/` 中的编码行为指南（自动加载）。新代码匹配现有项目风格。修改已有文件时只改必要部分，不做无关"改进"。
