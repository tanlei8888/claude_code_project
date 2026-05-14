# Claude Code 官方使用指南

Claude Code CLI 的配置、工作流与最佳实践参考。基于官方文档（code.claude.com/docs）和社区最佳实践整理。基于这份指南，在给用户创建配置文件时，提供建议时，请优先考虑以下原则：

---

## 一、配置体系

### 1.1 配置文件层级（优先级从高到低）

| 位置 | 作用域 | 是否提交 Git |
|------|--------|-------------|
| 托管策略 | 企业组织级 | 管理员控制 |
| `--settings` CLI 参数 | 单次调用 | ❌ |
| `.claude/settings.local.json` | 当前项目（个人） | ❌ gitignored |
| `.claude/settings.json` | 当前项目（团队共享） | ✅ |
| `~/.claude/settings.json` | 所有项目（用户全局） | ❌ |

### 1.2 settings.json 常用配置项

```json
{
  "model": "sonnet",
  "autoUpdatesChannel": "stable",
  "cleanupPeriodDays": 14,
  "showTurnDuration": true,
  "env": { "KEY": "value" },
  "permissions": {
    "allow": ["Bash(npm run *)", "Bash(git diff *)"],
    "deny": ["Bash(rm -rf *)", "Bash(git push --force *)"]
  },
  "autoMode": {
    "environment": ["bash", "node"],
    "allow": ["Read", "Glob", "Grep", "Edit", "Write"],
    "soft_deny": ["Bash(git push *)"]
  },
  "sandbox": { "enabled": true }
}
```

**关键配置说明：**
- `model` — 可选 `sonnet` / `opus` / `haiku`，也可通过 `/model` 切换
- `autoUpdatesChannel` — `stable`（稳定版）/ `latest`（最新版）
- `cleanupPeriodDays` — 本地会话文件保留天数
- `permissions` — 预授权/拒绝特定工具调用，减少弹窗
- `autoMode` — 自动执行模式配置，`soft_deny` 中的操作仍会显示但不阻塞
- `env` — 注入环境变量到 Claude Code 会话

---

## 二、CLAUDE.md 与 Rules 系统

### 2.1 指令文件层级

| 文件 | 作用域 | 加载时机 |
|------|--------|---------|
| `~/.claude/CLAUDE.md` | 全局个人偏好 | 每个会话 |
| `./CLAUDE.md` | 项目级（团队共享） | 项目会话 |
| `./CLAUDE.local.md` | 项目个人覆盖 | 项目会话（晚于 CLAUDE.md） |
| `.claude/rules/*.md` | 模块化规则 | 自动递归加载（v2.0.64+） |
| 企业 `CLAUDE.md` | 组织级 | 所有会话 |

### 2.2 CLAUDE.md 最佳实践

- **放在仓库根目录**，纳入版本控制，团队共享
- **保持简洁** — 官方建议 15 行以内作为概览，详细规范放 `.claude/rules/`
- **规则：每当 Claude 做错一件事，就加到 CLAUDE.md 中**
- 在 PR 中使用 `@claude` 可以自动化积累经验
- 使用 `/init` 命令自动生成项目 CLAUDE.md，然后审查修剪
- 使用 `/memory` 配置跨会话自动记忆

### 2.3 Rules 目录（`.claude/rules/`）

- 所有 `.md` 文件自动递归加载，无需显式引用
- 按模块拆分：`code-style.md`、`testing.md`、`security.md`、`git-workflow.md` 等
- 支持 YAML frontmatter 限定适用范围：

```markdown
---
paths:
  - "src/api/**/*.ts"
  - "!src/api/**/*.test.ts"
---
# API 开发规范
必须使用 TypeScript 严格模式。
```

- 路径精准匹配的规则优先级高于通用规则
- 使用 `/rules` 命令查看和管理规则加载情况

---

## 三、斜杠命令速查

### 3.1 会话管理

| 命令 | 功能 |
|------|------|
| `/clear` | 清空对话历史，切换任务时使用 |
| `/compact` | 压缩上下文（当 Claude 开始循环或质量下降时使用） |
| `/context` | 查看当前上下文使用情况 |
| `/undo` | 撤销上一步操作 |
| `/btw` | 提出不影响主上下文的侧边问题 |

### 3.2 工作流

| 命令 | 功能 |
|------|------|
| `/init` | 初始化项目的 CLAUDE.md |
| `/plan` | 进入计划模式（复杂任务先规划再实施） |
| `/simplify` | 审查最近修改的代码，自动简化优化 |
| `/review` | 审查当前分支的变更（PR review） |
| `/security-review` | 安全审查当前分支 |

### 3.3 配置

| 命令 | 功能 |
|------|------|
| `/model` | 切换模型（sonnet/opus/haiku） |
| `/effort` | 设置思考努力程度（low/medium/high/max） |
| `/permissions` | 管理权限配置 |
| `/memory` | 配置自动记忆开关 |
| `/rules` | 查看和管理 `.claude/rules/` |
| `/config` | 配置主题、模型等基本设置 |
| `/statusline` | 配置状态栏显示 |

### 3.4 高级功能

| 命令 | 功能 |
|------|------|
| `/worktree` | 在隔离的 git worktree 中工作 |
| `/batch` | 将大型迁移扇出到多个并行 worktree agent |
| `/loop` | 定时循环执行任务 |
| `/schedule` | 云端定时任务（电脑关闭也能运行） |
| `/tasks` | 查看后台任务状态 |

---

## 四、Hooks 系统

### 4.1 基本结构

```json
{
  "hooks": {
    "EventName": [
      {
        "matcher": "ToolPattern",
        "hooks": [
          {
            "type": "command",
            "command": "your-command-here",
            "timeout": 60
          }
        ]
      }
    ]
  }
}
```

### 4.2 Hook 类型

| 类型 | 说明 |
|------|------|
| `command` | 执行 shell 命令（最常用） |
| `prompt` | LLM 评估（如代码质量检查） |
| `agent` | 异步 subagent 处理 |
| `http` | 调用外部 webhook |
| `mcp_tool` | 调用 MCP 工具 |

### 4.3 Matcher 语法

- `"Edit"` — 精确匹配工具名
- `"Edit|Write"` — 匹配多个工具（`|` 分隔）
- `"*"` — 匹配所有工具
- 正则表达式也支持
- `if` 字段（v2.1.85+）：用权限规则语法按工具名+参数过滤

### 4.4 常用 Hook 事件

| 事件 | 触发时机 | 典型用途 |
|------|---------|---------|
| `SessionStart` | 会话开始 | 环境检查、激活 venv |
| `UserPromptSubmit` | 用户提交提示词 | 注入额外上下文 |
| `PreToolUse` | 工具执行前 | 安全检查（可阻止） |
| `PostToolUse` | 工具执行成功后 | 自动格式化、lint |
| `PostToolUseFailure` | 工具执行失败后 | 错误恢复 |
| `Stop` | Claude 响应完成 | 通知、自动测试 |
| `PreCompact` | 上下文压缩前 | 保存关键信息 |
| `PermissionRequest` | 权限弹窗出现 | 自定义权限逻辑 |
| `SubagentStart/Stop` | subagent 启停 | 追踪子任务 |
| `SessionEnd` | 会话终止 | 清理、通知 |
| `WorktreeCreate/Remove` | worktree 生命周期 | 环境初始化 |
| `InstructionsLoaded` | CLAUDE.md/rules 加载 | 验证配置 |
| `FileChanged` | 监控文件变更 | 热重载 |

### 4.5 Hook 退出码

| 退出码 | 含义 |
|--------|------|
| 0 | 成功/允许。stdout 可包含 JSON 或上下文数据 |
| 2 | 阻止（仅 PreToolUse）。stderr 作为 Claude 的反馈信息 |
| 其他非零 | 非阻塞错误。stderr 在 verbose 模式显示 |

### 4.6 常用 Hook 示例

**自动格式化（PostToolUse）：**
```json
{
  "hooks": {
    "PostToolUse": [
      {
        "matcher": "Edit|Write",
        "hooks": [
          {
            "type": "command",
            "command": "prettier --write \"$CLAUDE_PROJECT_DIR/${CLAUDE_TOOL_INPUT_FILE_PATH}\" 2>/dev/null || true"
          }
        ]
      }
    ]
  }
}
```

**禁止危险命令（PreToolUse）：**
```json
{
  "hooks": {
    "PreToolUse": [
      {
        "matcher": "Bash",
        "hooks": [
          {
            "type": "command",
            "command": "echo \"$CLAUDE_TOOL_INPUT\" | grep -qE '(rm -rf /|git push --force origin master)' && exit 2 || exit 0"
          }
        ]
      }
    ]
  }
}
```

---

## 五、权限系统

### 5.1 权限模式

| 模式 | 说明 |
|------|------|
| `default` | 大多数工具需要确认 |
| `accept-edits` | 自动接受文件编辑，其他仍需确认 |
| `plan` | 只读模式，不允许任何修改 |
| `bypass` | 全部自动执行，无提示 |

通过 `--permission-mode` 参数或 `/permissions` 切换。

### 5.2 权限预配置

```json
{
  "permissions": {
    "allow": [
      "Bash(npm run *)",
      "Bash(git diff *)",
      "Bash(git status)",
      "Bash(git log *)"
    ],
    "deny": [
      "Bash(rm -rf *)",
      "Bash(git push --force origin main)",
      "Bash(git push --force origin master)"
    ]
  }
}
```

---

## 六、核心工作流

### 6.1 标准工作流：Explore → Plan → Code

```
1. Explore  — 用搜索/阅读理解代码库，不要直接动手写
2. Plan     — 复杂任务进入计划模式（/plan），把精力花在计划上
3. Code     — 计划确认后切换到 accept-edits 模式，一次性实现
```

### 6.2 关键原则

- **#1 原则：验证** — 给 Claude 提供验证自己工作的方式（运行测试、浏览器预览）
- **不要假设** — 不确定就问，不要猜测
- **简单至上** — 50 行能搞定的不写 200 行
- **不要过度抽象** — 不为只使用一次的代码创建抽象

### 6.3 并行工作

- **多会话**：运行 3-5 个 Claude 会话在隔离的 git worktree 中（`claude --worktree`）
- **`/batch`**：将大型迁移任务扇出到数十个并行 worktree agent
- **Subagents**：附加 "use subagents" 指令投入更多算力，定义在 `.claude/agents/`
- **Skills**：将重复工作流转化为技能，定义在 `.claude/skills/<name>/SKILL.md`

### 6.4 上下文管理

- 感觉 Claude 开始"绕圈"或质量下降时 → `/compact`
- 切换任务时 → `/clear`
- 临时侧边问题 → `/btw`（不污染主上下文）
- 用 subagents 做调研，保持主上下文干净

---

## 七、项目目录结构参考

```
your-project/
├── CLAUDE.md                      # 项目主指令（概览，建议简洁）
├── CLAUDE.local.md                # 个人本地覆盖（gitignored）
├── .claude/
│   ├── settings.json              # 项目共享设置（permissions, hooks, env）
│   ├── settings.local.json        # 个人本地设置覆盖（gitignored）
│   ├── rules/                     # 模块化规则（自动加载）
│   │   ├── code-style.md
│   │   ├── testing.md
│   │   └── git-workflow.md
│   ├── hooks/                     # Hook 脚本
│   ├── skills/                    # 项目级 Skills
│   │   └── <name>/SKILL.md
│   ├── agents/                    # 自定义 Agent 定义
│   ├── commands/                  # 自定义斜杠命令
│   └── worktrees/                 # 临时 worktree 目录
└── .gitignore                     # 含 CLAUDE.local.md, settings.local.json
```

---

## 八、快捷键

| 快捷键 | 功能 |
|--------|------|
| `Shift+Tab` | 进入/退出计划模式 |
| `Ctrl+C` | 中断当前操作 |
| `Ctrl+O` | 查看详细输出 |
| `↑/↓` | 浏览历史命令 |
| `Ctrl+R` | 搜索历史命令 |

---

## 九、快速上手清单

1. 全局安装：`npm install -g @anthropic-ai/claude-code`
2. 认证：运行 `claude` 并跟随提示
3. 配置 `~/.claude/settings.json`：设定 model、autoUpdatesChannel
4. 创建 `~/.claude/CLAUDE.md`：个人跨项目规则
5. 在每个项目中运行 `/init` 生成 CLAUDE.md，然后审查修剪
6. 配置 `/permissions` 预授权安全命令
7. 学习核心命令：`/clear`、`/compact`、`/context`、`/undo`、`/memory`

---

> **参考来源：** code.claude.com/docs 官方文档、Claude Help Center、社区最佳实践
