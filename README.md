# Hedgehog Blog

基于 Spring Boot + Vue 3 的现代化个人博客全栈系统，前后端分离架构。

## 项目结构

```
hedgehog-project/
├── hedgehog-server/    # Spring Boot 2.7 后端 — RESTful API，统一服务博客前台与后台管理
├── hedgehog-web/       # Vue 3 后台管理系统 — Element Plus + Tailwind CSS，内容管理与站点配置
└── hedgehog-blog/      # Vue 3 博客前台 — 纯 Tailwind CSS 极简科技风，面向读者展示
```

| 项目 | 端口 | 技术栈 |
|------|:----:|--------|
| hedgehog-server | 8080 | Spring Boot 2.7 / Java 8 / MyBatis Plus 3.5 / MySQL 8.0 / JWT / Flyway |
| hedgehog-web | 3000 | Vue 3.4 + TS / Element Plus 2.7 / Tailwind CSS 3.4 / Pinia / Vite |
| hedgehog-blog | 3001 | Vue 3.4 + TS / marked + highlight.js / Tailwind CSS 3.4 / Pinia / Vite |

## 功能概览

### 博客前台 (hedgehog-blog)

- 文章列表 — 分类/标签筛选 + 搜索 + 分页
- 文章详情 — Markdown 渲染 + 目录导航 + 代码高亮
- 评论系统 — 二层嵌套评论，登录后发表与回复
- 点赞 — toggle 点赞/取消 + 心形动画
- 用户体系 — 注册 / 登录 / 个人中心
- 关于页 — 站点作者与简介展示
- SEO — @unhead/vue 动态 title / meta description

### 后台管理 (hedgehog-web)

- 仪表盘 — 统计卡片 + 最近文章
- 文章管理 — Markdown 编辑器(v-md-editor) + 草稿/已发布/定时发布/置顶
- 分类管理 — 增删改，有文章时拒绝删除
- 标签管理 — 增删改 + 与文章多对多关联
- 用户管理 — CRUD + ADMIN/USER 角色区分
- 评论管理 — 审核通过/拒绝 + 删除
- 媒体管理 — 图片上传 + 网格展示 + 一键复制 URL
- 站点配置 — 博客名称/Logo/作者信息/社交链接/关于页编辑

### 后端 (hedgehog-server)

- JWT 认证 — 全局过滤器解析 token，`@AdminRequired` 注解 + 拦截器做权限分级
- RESTful API — 统一 `Result<T>` 响应格式
- Markdown 渲染 — flexmark 后端渲染为 HTML，启用表格/代码高亮/emoji/TOC
- Slug 生成 — pinyin4j 中文标题自动转拼音 slug
- 文件上传 — 本地存储，仅允许图片格式，单文件 ≤ 10MB
- 数据库迁移 — Flyway 版本化管理 DDL
- API 文档 — Springdoc OpenAPI 自动生成

## 数据库表

| 表名 | 说明 |
|------|------|
| sys_user | 用户（ADMIN/USER 角色） |
| blog_category | 文章分类 |
| blog_tag | 标签 |
| blog_article | 文章（含草稿/已发布/定时发布/私密状态） |
| blog_article_tag | 文章-标签关联 |
| blog_comment | 评论（二层嵌套，待审核/已通过/已拒绝） |
| blog_like | 点赞记录 |
| sys_media | 媒体资源 |
| site_config | 站点配置（单行表） |

## 快速开始

### 环境要求

- JDK 8+ / Maven 3.6+
- Node.js 18+ / pnpm（或 npm）
- MySQL 8.0

### 1. 数据库

创建 `hedgehog_dev` 数据库，Flyway 启动时自动执行迁移脚本。默认数据库连接配置见 `hedgehog-server/src/main/resources/application-dev.yml`。

### 2. 启动后端

```bash
cd hedgehog-server
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

服务启动后访问 http://localhost:8080，Swagger 文档 http://localhost:8080/swagger-ui.html。

### 3. 启动后台管理

```bash
cd hedgehog-web
pnpm install
pnpm dev
```

访问 http://localhost:3000，Vite 自动代理 `/api` → `localhost:8080`。

### 4. 启动博客前台

```bash
cd hedgehog-blog
pnpm install
pnpm dev
```

访问 http://localhost:3001，Vite 自动代理 `/api` → `localhost:8080`。

## API 设计

### 公开接口（无需登录）

```
POST /api/auth/login              # 登录
POST /api/auth/register           # 注册
GET  /api/articles                # 文章列表（仅已发布，支持分类/标签/关键词筛选）
GET  /api/articles/{slug}         # 文章详情
GET  /api/categories              # 分类列表
GET  /api/tags                    # 标签列表
GET  /api/comments                # 文章评论树（仅已通过）
GET  /api/site/config             # 站点配置
```

### 需登录接口

```
GET  /api/auth/info               # 当前用户信息
PUT  /api/auth/profile            # 修改个人信息
POST /api/comments                # 发表评论
POST /api/likes/{articleId}       # 点赞/取消点赞（toggle）
GET  /api/likes/status            # 批量查询点赞状态
```

### 管理员接口（`/api/admin/*`，需 ADMIN 角色）

```
文章：    CRUD + 修改状态 + 切换置顶
分类/标签：CRUD
评论：    列表 + 审核 + 删除
用户：    CRUD
媒体：    上传 + 列表 + 删除
站点配置：更新
```

所有响应格式：`{ code: 200, message: "success", data: ... }`

## 设计决策

- **认证**：JWT 扩展 role 字段 + `@AdminRequired` 注解实现权限分级，不使用 Spring Security
- **Markdown**：后端 flexmark 渲染为 HTML 存储，前端 v-html 直接展示
- **评论**：仅支持二层嵌套（顶级评论 + 二级回复）
- **文件存储**：本地 `uploads/` 目录 + Spring Boot 静态资源映射
- **代码规范**：后端按功能分包 + 构造器注入；前端 Composition API + Pinia setup 语法

## 许可证

MIT
