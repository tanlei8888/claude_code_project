---
paths:
  - "hedgehog-server/**"
---

# 后端技术栈

## 核心框架

- Spring Boot 2.7.18 + Java 8 + Maven
- MyBatis Plus 3.5.5（逻辑删除、雪花ID）
- MySQL 8.0（远程 120.79.83.62:54231）

## 认证与安全

- JWT 认证（jjwt 0.12.5）+ BCrypt 密码加密
- JwtAuthFilter（全局）→ 解析 token，设 UserContext(userId, role)
- @AdminRequired 注解 + AdminInterceptor → 检查 role == ADMIN，否则 403
- 不使用 Spring Security

## Markdown 处理

- flexmark（Markdown → HTML）
- 启用扩展：表格、代码高亮、目录生成、emoji
- 文章保存时后端渲染 content_md 为 content_html，前端 v-html 渲染

## Slug 生成

- pinyin4j，中文标题自动生成拼音 slug，可手动覆盖

## API 响应规范

- 统一 `Result<T>` 包装：`{ code: 200, message: "success", data: ... }`

## 文件存储

- 本地 `uploads/` 目录 + Spring Boot 静态资源映射
- 仅允许 jpeg/png/gif/webp/svg，单文件最大 10MB
