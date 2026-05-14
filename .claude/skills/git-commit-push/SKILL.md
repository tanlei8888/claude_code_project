---
name: git-commit-push
description: 提取当前变更文件，结合 git diff 内容生成规范的 commit message，提交并推送到远程仓库。
---

当用户调用此技能时，按以下步骤自动执行：

## 1. 检查变更

首先并行执行以下命令：
- `git status` — 查看所有变更文件（不含 -uall）
- `git diff --staged` — 查看已暂存的变更
- `git diff` — 查看未暂存的变更
- `git log --oneline -5` — 查看最近提交风格

## 2. 确认是否继续

如果没有任何变更（工作区干净），直接告知用户并退出。

如果有未暂存的变更，先执行 `git add -A` 暂存所有变更文件。

## 3. 生成 Commit Message

分析 git diff 内容，生成规范的 commit message：

**格式：**
```
<type>: <简短中文描述>

<详细说明（可选，仅复杂变更需要）>

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>
```

**type 类型：**
- `feat` — 新功能
- `fix` — 修复 bug
- `refactor` — 重构（不改变功能）
- `style` — 样式/格式调整
- `docs` — 文档变更
- `chore` — 构建/依赖/配置
- `perf` — 性能优化
- `test` — 测试相关

**提交信息规则：**
- 标题不超过 50 个汉字
- 用中文描述
- 说明 "为什么" 而非 "是什么"（代码 diff 已经说明了是什么）
- 不包含 Co-Authored-By 以外的尾部签名

## 4. 创建 Commit

使用 HEREDOC 格式执行 commit，确保多行消息正确：

```bash
git commit -m "$(cat <<'EOF'
<commit message>

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>
EOF
)"
```

## 5. 推送到远程

```bash
git push
```

**注意：** 如果 push 失败（如需要先 pull），告知用户具体原因，不要强制推送。

## 6. 输出摘要

简洁报告：
- 变更文件数量
- commit hash（短格式）
- push 结果
