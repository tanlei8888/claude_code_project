-- =============================================
-- Hedgehog Blog 完整初始化数据
-- 包含：分类 / 标签 / 文章（含 HTML）/ 文章-标签关联 / 站点配置
-- 执行方式：在 SQLyog 中打开此文件直接执行
-- 数据库：hedgehog_dev
-- =============================================

USE hedgehog_dev;

-- =============================================
-- 一、分类（3个）
-- =============================================
INSERT IGNORE INTO blog_category (id, name, slug, description, sort_order) VALUES
(1, 'Skills', 'skills', 'Claude Code Skills 教程与实战案例，每个案例附带完整可用的配置文件', 1),
(2, 'Rules', 'rules', 'Claude Code Rules 规则系统详解，提供可直接复制的模块化规则模板', 2),
(3, 'Claude Code', 'claude-code', 'Claude Code CLI 的综合使用教程、Hooks 配置与最佳实践', 3);

-- =============================================
-- 二、标签（10个）
-- =============================================
INSERT IGNORE INTO blog_tag (id, name, slug) VALUES
(1, 'Claude Code', 'claude-code'),
(2, 'Skills', 'skills'),
(3, 'Rules', 'rules'),
(4, 'Hooks', 'hooks'),
(5, '配置', 'config'),
(6, '教程', 'tutorial'),
(7, '实战', 'practice'),
(8, 'Git', 'git'),
(9, '前端', 'frontend'),
(10, '自动化', 'automation');

-- =============================================
-- 三、文章（5篇）
-- 每篇包含 Markdown 原文和 flexmark 标准渲染的 HTML
-- author_id=1(admin), status=1(已发布)
-- =============================================

-- ==================== 文章 1：Skills 入门指南 ====================
INSERT INTO blog_article (id, title, slug, summary, content_md, content_html, category_id, status, is_top, author_id, publish_time) VALUES (
1,
'Claude Code Skills 入门指南 —— 打造你的第一个专属技能',
'claude-code-skills-guide',
'全面解析 Claude Code Skills 系统：什么是 Skill、如何创建、如何在实际项目中使用。以 dev-start 技能为例，展示完整的一键启动开发环境 Skill，附带完整源码可直接复用。',

'## 什么是 Skill？

Skill 是 Claude Code 中的**可复用指令模板**。当你发现自己反复让 Claude 执行同一类任务时，就可以把这个任务的执行步骤、检查清单、常见问题处理方式封装成一个 Skill。之后只需一个斜杠命令，Claude 就能按照你预设的流程自动执行。

Skill 的本质是**将人类经验编码为 AI 工作流**。它不只是告诉 Claude 做什么，更是告诉 Claude 怎么判断做得对不对。

## Skill 的文件结构

每个 Skill 是一个目录，包含一个 `SKILL.md` 文件：

```
.claude/skills/<skill-name>/
└── SKILL.md
```

`SKILL.md` 使用 YAML frontmatter 定义元数据：

```markdown
---
name: my-skill
description: 一句话描述这个 Skill 做什么
---

当用户调用此技能时，按以下步骤执行...
```

核心要素：
- **name**：技能的唯一标识，斜杠命令会用到它
- **description**：帮助 Claude 判断何时自动推荐这个 Skill
- **正文**：详细的任务执行指令、检查标准和注意事项

## 实战案例：dev-start Skill

下面是一个真实的一键启动开发环境 Skill。我们的项目有三个子项目（Spring Boot 后端 + 两个 Vue 前端），每次启动都需要逐个检查环境、启动服务。把这个流程封装成 Skill 后，一个命令就能搞定。

### 设计思路

将启动流程拆成四个阶段，每个阶段有明确的验收标准：

1. **环境检查**：验证 Java、Maven、Node.js 可用，检查端口占用，测试 MySQL 连通性
2. **依赖诊断**：检查 Maven 仓库和 node_modules，缺什么装什么
3. **启动项目**：先启后端，等端口就绪后再并行启前端
4. **自我修复**：如果启动失败，提供诊断矩阵，匹配常见问题和修复方案

### 阶段 1：环境检查

```bash
java -version 2>&1    # 检查 Java（需要 ≥ 1.8）
mvn -version 2>&1     # 检查 Maven
node -v 2>&1          # 检查 Node.js（需要 ≥ 18.0.0）
npm -v 2>&1           # 检查 npm
```

端口检查：

```bash
netstat -ano | findstr ":8080"   # 后端端口
netstat -ano | findstr ":3000"   # 后台管理前端
netstat -ano | findstr ":3001"   # 博客前台
```

将检查结果汇总为可视化表格：

```
环境检查结果：
  ✅ Java   1.8.0_401
  ✅ Maven  3.9.6
  ✅ Node   20.11.0
  ✅ 端口 8080 — 空闲
  ❌ 端口 3000 — 被占用 (PID 12345)
  ✅ MySQL  — 可达
```

### 阶段 2：依赖诊断

**关键设计决策：修复操作前必须先询问用户确认。** 不要自作主张地安装依赖或修改配置。

对于 Maven 项目，检查 `.m2/repository` 是否存在；对于 npm 项目，检查 `node_modules` 是否非空。

询问模板示例：

> 检测到 hedgehog-web 缺少依赖（node_modules 不存在），是否需要执行 `npm install` 安装依赖？

如果用户选择「全部自动修复」，则后续所有修复操作跳过询问直接执行。

### 阶段 3：启动项目

**关键原则：每个项目在独立的终端窗口中启动**，方便实时查看日志，出问题时可以快速定位。

启动顺序很重要：

1. 先启动 hedgehog-server（后端），轮询等待端口 8080 就绪（最多等 2 分钟）
2. 确认后端就绪后，并行启动两个前端项目

```bash
# 后端 — 独立窗口
powershell -Command "Start-Process powershell -NoExit -Command ''cd D:/project/hedgehog-server; mvn spring-boot:run''"

# 前端 — 并行启动
powershell -Command "Start-Process powershell -NoExit -Command ''cd D:/project/hedgehog-web; npm run dev''"
powershell -Command "Start-Process powershell -NoExit -Command ''cd D:/project/hedgehog-blog; npm run dev''"
```

### 阶段 4：自我修复

这是 Skill 最有价值的部分——**诊断矩阵**。把常见问题的症状、原因和修复方案整理成表格，让 Claude 能够像运维一样排查问题：

| 症状 | 可能原因 | 修复方案 |
|------|---------|--------|
| `mvn` 找不到 | Maven 未安装 | 提示安装 Maven |
| 端口被占用 | 上次未正常关闭 | `taskkill /PID xxx`（需确认）|
| `node_modules` 缺失 | 依赖未安装 | `npm install`（需确认）|
| MySQL 连接失败 | 数据库不可达 | 检查网络/VPN（需确认）|
| Spring Boot 启动失败 | 端口冲突/配置错误 | 读取错误日志定位根因 |

## 编写 Skill 的最佳实践

从 dev-start 的设计中，可以提炼出四个通用原则：

1. **分阶段执行**：把复杂任务拆成独立的阶段，每个阶段有明确的验收标准
2. **先检查再行动**：在修改任何东西之前，先全面了解当前状态
3. **危险操作需确认**：涉及文件系统、进程管理、配置修改的操作，必须先获得用户同意
4. **失败要可诊断**：为常见错误准备诊断矩阵，让 Claude 能自己排查问题

## 如何开始创建你的第一个 Skill

1. 找一个你最近反复让 Claude 帮忙做的任务
2. 在 `.claude/skills/<name>/SKILL.md` 创建文件
3. 写下 frontmatter（name + description）
4. 把任务拆成带验收标准的阶段
5. 列出常见失败场景和修复方法
6. 测试几次，根据实际表现迭代优化

一个好的 Skill 不是一次写成的——它是在反复使用中打磨出来的。每次 Claude 跑偏了，就把那个场景加到诊断矩阵里。久而久之，你的 Skill 会变得越来越懂你。',

'<h2>什么是 Skill？</h2>
<p>Skill 是 Claude Code 中的<strong>可复用指令模板</strong>。当你发现自己反复让 Claude 执行同一类任务时，就可以把这个任务的执行步骤、检查清单、常见问题处理方式封装成一个 Skill。之后只需一个斜杠命令，Claude 就能按照你预设的流程自动执行。</p>
<p>Skill 的本质是<strong>将人类经验编码为 AI 工作流</strong>。它不只是告诉 Claude 做什么，更是告诉 Claude 怎么判断做得对不对。</p>
<h2>Skill 的文件结构</h2>
<p>每个 Skill 是一个目录，包含一个 <code>SKILL.md</code> 文件：</p>
<pre><code>.claude/skills/&lt;skill-name&gt;/
└── SKILL.md</code></pre>
<p><code>SKILL.md</code> 使用 YAML frontmatter 定义元数据：</p>
<pre><code class="language-markdown">---
name: my-skill
description: 一句话描述这个 Skill 做什么
---

当用户调用此技能时，按以下步骤执行...</code></pre>
<p>核心要素：</p>
<ul><li><strong>name</strong>：技能的唯一标识，斜杠命令会用到它</li><li><strong>description</strong>：帮助 Claude 判断何时自动推荐这个 Skill</li><li><strong>正文</strong>：详细的任务执行指令、检查标准和注意事项</li></ul>
<h2>实战案例：dev-start Skill</h2>
<p>下面是一个真实的一键启动开发环境 Skill。我们的项目有三个子项目（Spring Boot 后端 + 两个 Vue 前端），每次启动都需要逐个检查环境、启动服务。把这个流程封装成 Skill 后，一个命令就能搞定。</p>
<h3>设计思路</h3>
<p>将启动流程拆成四个阶段，每个阶段有明确的验收标准：</p>
<ol><li><strong>环境检查</strong>：验证 Java、Maven、Node.js 可用，检查端口占用，测试 MySQL 连通性</li><li><strong>依赖诊断</strong>：检查 Maven 仓库和 node_modules，缺什么装什么</li><li><strong>启动项目</strong>：先启后端，等端口就绪后再并行启前端</li><li><strong>自我修复</strong>：如果启动失败，提供诊断矩阵，匹配常见问题和修复方案</li></ol>
<h3>阶段 1：环境检查</h3>
<pre><code class="language-bash">java -version 2&gt;&amp;1    # 检查 Java（需要 ≥ 1.8）
mvn -version 2&gt;&amp;1     # 检查 Maven
node -v 2&gt;&amp;1          # 检查 Node.js（需要 ≥ 18.0.0）
npm -v 2&gt;&amp;1           # 检查 npm</code></pre>
<p>端口检查：</p>
<pre><code class="language-bash">netstat -ano | findstr &quot;:8080&quot;   # 后端端口
netstat -ano | findstr &quot;:3000&quot;   # 后台管理前端
netstat -ano | findstr &quot;:3001&quot;   # 博客前台</code></pre>
<p>将检查结果汇总为可视化表格：</p>
<pre><code>环境检查结果：
  ✅ Java   1.8.0_401
  ✅ Maven  3.9.6
  ✅ Node   20.11.0
  ✅ 端口 8080 — 空闲
  ❌ 端口 3000 — 被占用 (PID 12345)
  ✅ MySQL  — 可达</code></pre>
<h3>阶段 2：依赖诊断</h3>
<p><strong>关键设计决策：修复操作前必须先询问用户确认。</strong> 不要自作主张地安装依赖或修改配置。</p>
<p>对于 Maven 项目，检查 <code>.m2/repository</code> 是否存在；对于 npm 项目，检查 <code>node_modules</code> 是否非空。</p>
<p>询问模板示例：</p>
<blockquote><p>检测到 hedgehog-web 缺少依赖（node_modules 不存在），是否需要执行 <code>npm install</code> 安装依赖？</p></blockquote>
<p>如果用户选择「全部自动修复」，则后续所有修复操作跳过询问直接执行。</p>
<h3>阶段 3：启动项目</h3>
<p><strong>关键原则：每个项目在独立的终端窗口中启动</strong>，方便实时查看日志，出问题时可以快速定位。</p>
<p>启动顺序很重要：</p>
<ol><li>先启动 hedgehog-server（后端），轮询等待端口 8080 就绪（最多等 2 分钟）</li><li>确认后端就绪后，并行启动两个前端项目</li></ol>
<pre><code class="language-bash"># 后端 — 独立窗口
powershell -Command &quot;Start-Process powershell -NoExit -Command ''cd D:/project/hedgehog-server; mvn spring-boot:run''&quot;

# 前端 — 并行启动
powershell -Command &quot;Start-Process powershell -NoExit -Command ''cd D:/project/hedgehog-web; npm run dev''&quot;
powershell -Command &quot;Start-Process powershell -NoExit -Command ''cd D:/project/hedgehog-blog; npm run dev''&quot;</code></pre>
<h3>阶段 4：自我修复</h3>
<p>这是 Skill 最有价值的部分——<strong>诊断矩阵</strong>。把常见问题的症状、原因和修复方案整理成表格，让 Claude 能够像运维一样排查问题：</p>
<table><thead><tr><th>症状</th><th>可能原因</th><th>修复方案</th></tr></thead><tbody><tr><td><code>mvn</code> 找不到</td><td>Maven 未安装</td><td>提示安装 Maven</td></tr><tr><td>端口被占用</td><td>上次未正常关闭</td><td><code>taskkill /PID xxx</code>（需确认）</td></tr><tr><td><code>node_modules</code> 缺失</td><td>依赖未安装</td><td><code>npm install</code>（需确认）</td></tr><tr><td>MySQL 连接失败</td><td>数据库不可达</td><td>检查网络/VPN（需确认）</td></tr><tr><td>Spring Boot 启动失败</td><td>端口冲突/配置错误</td><td>读取错误日志定位根因</td></tr></tbody></table>
<h2>编写 Skill 的最佳实践</h2>
<p>从 dev-start 的设计中，可以提炼出四个通用原则：</p>
<ol><li><strong>分阶段执行</strong>：把复杂任务拆成独立的阶段，每个阶段有明确的验收标准</li><li><strong>先检查再行动</strong>：在修改任何东西之前，先全面了解当前状态</li><li><strong>危险操作需确认</strong>：涉及文件系统、进程管理、配置修改的操作，必须先获得用户同意</li><li><strong>失败要可诊断</strong>：为常见错误准备诊断矩阵，让 Claude 能自己排查问题</li></ol>
<h2>如何开始创建你的第一个 Skill</h2>
<ol><li>找一个你最近反复让 Claude 帮忙做的任务</li><li>在 <code>.claude/skills/&lt;name&gt;/SKILL.md</code> 创建文件</li><li>写下 frontmatter（name + description）</li><li>把任务拆成带验收标准的阶段</li><li>列出常见失败场景和修复方法</li><li>测试几次，根据实际表现迭代优化</li></ol>
<p>一个好的 Skill 不是一次写成的——它是在反复使用中打磨出来的。每次 Claude 跑偏了，就把那个场景加到诊断矩阵里。久而久之，你的 Skill 会变得越来越懂你。</p>',

1, 1, 1, 1, NOW()
);

-- ==================== 文章 2：Skills 进阶 ====================
INSERT INTO blog_article (id, title, slug, summary, content_md, content_html, category_id, status, is_top, author_id, publish_time) VALUES (
2,
'Claude Code Skills 进阶 —— git-commit-push 与 frontend-design 实战解析',
'claude-code-skills-advanced',
'深入解析两个可直接复用的实用 Skill：git-commit-push 将规范化 Git 工作流封装为 Skill，frontend-design 引导 Claude 进行创意性前端设计。对比流程型与创意型 Skill 的不同设计哲学。',

'上一篇文章介绍了 Skill 的基本概念和 dev-start 的设计思路。本篇深入解析另外两个风格截然不同的 Skill——流程自动化的 `git-commit-push` 和创意引导的 `frontend-design`，对比它们的设计哲学。

## git-commit-push：流程型 Skill 的典范

这个 Skill 的目标是把一次完整的 Git 提交流程自动化：**检查变更 → 暂存 → 生成规范 commit message → 提交 → 推送**。每个步骤环环相扣，不需要 Claude 做创意判断，只需要严格执行流程。

### 核心流程

**第 1 步：并行收集信息**

```bash
git status        # 查看变更文件
git diff --staged  # 查看已暂存变更
git diff           # 查看未暂存变更
git log --oneline -5  # 查看最近提交风格
```

四条命令并行执行，一次性获取全部上下文。这是 Skill 设计的一个重要技巧——把可以并行收集的信息一次性拿回来，减少等待时间。

**第 2 步：生成规范的 Commit Message**

格式定义：

```
<type>: <简短中文描述>

<详细说明（可选）>
```

Type 类型规范：

| type | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | 修复 bug |
| `refactor` | 重构 |
| `style` | 样式/格式调整 |
| `docs` | 文档变更 |
| `chore` | 构建/依赖/配置 |
| `perf` | 性能优化 |

关键规则：
- 标题不超过 50 个汉字
- 描述「为什么」改，而非「改了什么」（diff 已经说明了改了什么）
- 使用中文描述，让团队成员一目了然

**第 3 步：安全执行**

```bash
git commit -m "$(cat <<''EOF''
feat: 后台集成 v-md-editor 编辑器

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>
EOF
)"
```

使用 HEREDOC 格式确保多行 commit message 正确传递。Push 失败时不做强制推送，而是告知用户具体原因。

### 设计精妙之处

这个 Skill 的出色之处在于**细节考量**：
- 先读 `git log` 了解项目提交风格，生成一致的 message
- 如果没有变更就直接退出，不浪费时间
- Push 失败不强制，保护远程仓库安全
- 输出简洁摘要（变更文件数 + commit hash + push 结果）

## frontend-design：创意型 Skill 的标杆

与 `git-commit-push` 的精确流程不同，`frontend-design` 是一份**设计哲学指南**。它不告诉 Claude 具体做什么，而是告诉 Claude **什么样的设计是好的**。

### 设计思维框架

在执行任何代码之前，先明确四个维度：

- **目的**：这个界面解决什么问题？谁会使用它？
- **风格基调**：选择一个极端——极致极简、极繁混沌、复古未来、粗野主义……
- **约束条件**：技术限制、性能要求、无障碍访问
- **差异化**：是什么让它「过目不忘」？

关键洞察：**大胆的极繁主义和高精度的极简主义都行得通——关键在于意图明确，而非强度。**

### 美学标准清单

排版：选择独特的字体，避免 Arial、Inter 等「AI 风格字体」。将一款展示字体与精致的正文字体配对使用。

色彩：主导色配锐利点缀色的效果远胜于平均分配的调色板。使用 CSS 变量保持一致性。

动效：聚焦高光时刻。一次精心编排的页面加载动画带来的愉悦感远胜于零散的微交互。

空间：打破常规布局。不对称、重叠、对角线流动、宽裕的负空间。

背景：营造氛围感和层次感。使用渐变网格、噪点纹理、几何图案、装饰性边框。

### 反模式清单

Skill 中还明确列出了需要避免的「AI 流水线风格」：
- 烂大街的字体族（Inter、Roboto、Arial、系统字体）
- 陈词滥调的配色方案（尤其是白色背景上的紫色渐变）
- 毫无惊喜的布局和组件模式
- 缺乏上下文特色的模板化设计

## 两种设计哲学的对比

| 维度 | git-commit-push | frontend-design |
|------|----------------|-----------------|
| 类型 | 流程型 | 创意型 |
| 目标 | 零错误执行 | 激发创造力 |
| 指令风格 | 精确步骤 + 条件分支 | 原则 + 美学标准 |
| 验证方式 | 退出码检查 | 视觉评估 |
| 失败处理 | 明确的错误处理逻辑 | 美学方向的迭代反馈 |
| 适用场景 | 自动化工作流 | 设计/创意任务 |

## 如何选择你的 Skill 风格

问自己一个问题：**这个任务的结果是可客观判定的，还是主观感受的？**

可客观判定（编译是否通过、测试是否通过、格式是否规范）→ 采用流程型设计，精确步骤 + 验收标准 + 诊断矩阵。

主观感受（设计好不好看、文案好不好读、体验顺不顺手）→ 采用创意型设计，提供美学原则和反模式清单，而非具体步骤。

大多数 Skill 介于两者之间。关键是明确你的 Skill 偏向哪一端，然后用对应的设计语言来写它。',

'<p>上一篇文章介绍了 Skill 的基本概念和 dev-start 的设计思路。本篇深入解析另外两个风格截然不同的 Skill——流程自动化的 <code>git-commit-push</code> 和创意引导的 <code>frontend-design</code>，对比它们的设计哲学。</p>
<h2>git-commit-push：流程型 Skill 的典范</h2>
<p>这个 Skill 的目标是把一次完整的 Git 提交流程自动化：<strong>检查变更 → 暂存 → 生成规范 commit message → 提交 → 推送</strong>。每个步骤环环相扣，不需要 Claude 做创意判断，只需要严格执行流程。</p>
<h3>核心流程</h3>
<p><strong>第 1 步：并行收集信息</strong></p>
<pre><code class="language-bash">git status        # 查看变更文件
git diff --staged  # 查看已暂存变更
git diff           # 查看未暂存变更
git log --oneline -5  # 查看最近提交风格</code></pre>
<p>四条命令并行执行，一次性获取全部上下文。这是 Skill 设计的一个重要技巧——把可以并行收集的信息一次性拿回来，减少等待时间。</p>
<p><strong>第 2 步：生成规范的 Commit Message</strong></p>
<p>格式定义：</p>
<pre><code>&lt;type&gt;: &lt;简短中文描述&gt;

&lt;详细说明（可选）&gt;</code></pre>
<p>Type 类型规范：</p>
<table><thead><tr><th>type</th><th>说明</th></tr></thead><tbody><tr><td><code>feat</code></td><td>新功能</td></tr><tr><td><code>fix</code></td><td>修复 bug</td></tr><tr><td><code>refactor</code></td><td>重构</td></tr><tr><td><code>style</code></td><td>样式/格式调整</td></tr><tr><td><code>docs</code></td><td>文档变更</td></tr><tr><td><code>chore</code></td><td>构建/依赖/配置</td></tr><tr><td><code>perf</code></td><td>性能优化</td></tr></tbody></table>
<p>关键规则：</p>
<ul><li>标题不超过 50 个汉字</li><li>描述「为什么」改，而非「改了什么」（diff 已经说明了改了什么）</li><li>使用中文描述，让团队成员一目了然</li></ul>
<p><strong>第 3 步：安全执行</strong></p>
<pre><code class="language-bash">git commit -m &quot;$(cat &lt;&lt;''EOF''
feat: 后台集成 v-md-editor 编辑器

Co-Authored-By: Claude Opus 4.7 &lt;noreply@anthropic.com&gt;
EOF
)&quot;</code></pre>
<p>使用 HEREDOC 格式确保多行 commit message 正确传递。Push 失败时不做强制推送，而是告知用户具体原因。</p>
<h3>设计精妙之处</h3>
<p>这个 Skill 的出色之处在于<strong>细节考量</strong>：</p>
<ul><li>先读 <code>git log</code> 了解项目提交风格，生成一致的 message</li><li>如果没有变更就直接退出，不浪费时间</li><li>Push 失败不强制，保护远程仓库安全</li><li>输出简洁摘要（变更文件数 + commit hash + push 结果）</li></ul>
<h2>frontend-design：创意型 Skill 的标杆</h2>
<p>与 <code>git-commit-push</code> 的精确流程不同，<code>frontend-design</code> 是一份<strong>设计哲学指南</strong>。它不告诉 Claude 具体做什么，而是告诉 Claude <strong>什么样的设计是好的</strong>。</p>
<h3>设计思维框架</h3>
<p>在执行任何代码之前，先明确四个维度：</p>
<ul><li><strong>目的</strong>：这个界面解决什么问题？谁会使用它？</li><li><strong>风格基调</strong>：选择一个极端——极致极简、极繁混沌、复古未来、粗野主义……</li><li><strong>约束条件</strong>：技术限制、性能要求、无障碍访问</li><li><strong>差异化</strong>：是什么让它「过目不忘」？</li></ul>
<p>关键洞察：<strong>大胆的极繁主义和高精度的极简主义都行得通——关键在于意图明确，而非强度。</strong></p>
<h3>美学标准清单</h3>
<p>排版：选择独特的字体，避免 Arial、Inter 等「AI 风格字体」。将一款展示字体与精致的正文字体配对使用。</p>
<p>色彩：主导色配锐利点缀色的效果远胜于平均分配的调色板。使用 CSS 变量保持一致性。</p>
<p>动效：聚焦高光时刻。一次精心编排的页面加载动画带来的愉悦感远胜于零散的微交互。</p>
<p>空间：打破常规布局。不对称、重叠、对角线流动、宽裕的负空间。</p>
<p>背景：营造氛围感和层次感。使用渐变网格、噪点纹理、几何图案、装饰性边框。</p>
<h3>反模式清单</h3>
<p>Skill 中还明确列出了需要避免的「AI 流水线风格」：</p>
<ul><li>烂大街的字体族（Inter、Roboto、Arial、系统字体）</li><li>陈词滥调的配色方案（尤其是白色背景上的紫色渐变）</li><li>毫无惊喜的布局和组件模式</li><li>缺乏上下文特色的模板化设计</li></ul>
<h2>两种设计哲学的对比</h2>
<table><thead><tr><th>维度</th><th>git-commit-push</th><th>frontend-design</th></tr></thead><tbody><tr><td>类型</td><td>流程型</td><td>创意型</td></tr><tr><td>目标</td><td>零错误执行</td><td>激发创造力</td></tr><tr><td>指令风格</td><td>精确步骤 + 条件分支</td><td>原则 + 美学标准</td></tr><tr><td>验证方式</td><td>退出码检查</td><td>视觉评估</td></tr><tr><td>失败处理</td><td>明确的错误处理逻辑</td><td>美学方向的迭代反馈</td></tr><tr><td>适用场景</td><td>自动化工作流</td><td>设计/创意任务</td></tr></tbody></table>
<h2>如何选择你的 Skill 风格</h2>
<p>问自己一个问题：<strong>这个任务的结果是可客观判定的，还是主观感受的？</strong></p>
<p>可客观判定（编译是否通过、测试是否通过、格式是否规范）→ 采用流程型设计，精确步骤 + 验收标准 + 诊断矩阵。</p>
<p>主观感受（设计好不好看、文案好不好读、体验顺不顺手）→ 采用创意型设计，提供美学原则和反模式清单，而非具体步骤。</p>
<p>大多数 Skill 介于两者之间。关键是明确你的 Skill 偏向哪一端，然后用对应的设计语言来写它。</p>',

1, 1, 0, 1, NOW()
);

-- ==================== 文章 3：Rules 系统详解 ====================
INSERT INTO blog_article (id, title, slug, summary, content_md, content_html, category_id, status, is_top, author_id, publish_time) VALUES (
3,
'Claude Code Rules 系统详解 —— 用模块化规则精准控制 AI 行为',
'claude-code-rules-guide',
'深度解析 Rules 系统的层级结构、YAML frontmatter 的 paths 限定语法和优先级规则。附带可直接复制的编码行为指南和技术栈规则模板，教你如何让 Claude 在不同子项目中自动加载不同规则。',

'## 为什么需要 Rules？

Claude Code 很聪明，但它不了解你的项目。每次对话开始时，它的知识是通用的。Rules 系统的作用就是给 Claude 「注入项目记忆」——告诉它这个项目用什么技术栈、遵循什么编码规范、有哪些特殊约定。

Rules 的核心价值在于**精准度**。一个通用的 Claude 可以做任何事，但只有加载了 Rules 的 Claude 才能以你想要的方式做这件事。

## Rules 的四层金字塔

```
         ┌─────────────┐
         │ 企业组织级   │ ← 管理员控制，所有项目
         ├─────────────┤
         │ 全局个人     │ ← ~/.claude/CLAUDE.md
         ├─────────────┤
         │ 项目级       │ ← ./CLAUDE.md
         ├─────────────┤
         │ 模块化规则   │ ← .claude/rules/*.md
         └─────────────┘
```

加载顺序：企业级 → 全局 → 项目根 CLAUDE.md → rules/*.md。**后面的会追加到前面的之上**，所以项目级规则可以覆盖全局设置。

### CLAUDE.md vs .claude/rules/

一个常见的疑问：什么时候放 CLAUDE.md，什么时候拆成 rules？

**CLAUDE.md** 适合放项目概览和顶层原则：
- 项目是什么、有哪些子项目
- 核心架构决策
- 团队统一的工作流约定

**rules/*.md** 适合放模块化、可复用的规则：
- 特定语言/框架的编码规范
- 特定目录的约束条件
- 可跨项目复用的行为指南

官方建议：CLAUDE.md 保持在 15 行以内作为概览，详细规范全部放入 rules/。

## Paths 限定：让规则精准生效

这是 Rules 系统最强大的功能。通过 YAML frontmatter 中的 `paths` 字段，你可以让一条规则只在特定文件或目录下生效：

```markdown
---
paths:
  - "hedgehog-server/**/*.java"
  - "!hedgehog-server/**/*Test.java"
---
# 后端开发规范

使用 Spring Boot 2.7.18 + Java 8 + MyBatis Plus。
所有响应用 Result<T> 包装。
```

这个规则只会在编辑 `hedgehog-server` 下的 Java 文件时加载，编辑前端代码或测试文件时不会加载。

路径匹配规则：
- `path/**/*.ts` — glob 模式匹配
- `!path/**/*.test.ts` — 叹号表示排除
- 路径越精准的规则，优先级越高

## 实战模板：编码行为指南

以下是一份可直接复制的编码行为指南模板。把它保存为 `.claude/rules/guidelines.md`，所有项目通用：

```markdown
# 编码行为指南

## 1. 先思考再编码
不要假设。不要隐藏困惑。明确说出取舍。
如果存在多种理解方式，把它们都列出来。

## 2. 简单至上
用最少的代码解决问题。不要做推测性的工作。
不为只使用一次的代码创建抽象。

## 3. 精准修改
只改必须改的。匹配现有风格。
不改进相邻的代码、注释或格式。

## 4. 目标驱动执行
将任务转化为可验证的目标：
- "添加验证" → "为无效输入编写测试，然后让测试通过"
- "修复 bug" → "编写一个可复现的测试，然后让它通过"
```

## 实战模板：技术栈规则

对于多子项目的 monorepo，可以为每个子项目创建独立的技术栈规则。例如后端规则 `tech-stack-backend.md`：

```markdown
---
paths:
  - "hedgehog-server/**/*.java"
  - "hedgehog-server/**/*.xml"
---
# 后端技术栈

- Spring Boot 2.7.18 + Java 8 + Maven
- MyBatis Plus 3.5.5（逻辑删除、雪花ID）
- JWT 认证（jjwt 0.12.5）+ BCrypt 密码加密
- API 响应：Result<T> 包装 { code, message, data }
- 不使用 Spring Security
```

将这个文件保存到 `.claude/rules/tech-stack-backend.md`，Claude 在编辑后端代码时会自动加载这些约束。

## 迭代你的 Rules

最重要的原则：**每当 Claude 做错一件事，就加到 Rules 中。** 这不是一次性的工作，而是一个持续积累的过程。

举个例子：你发现 Claude 总是喜欢在改 bug 的时候顺手「优化」相邻的代码。那就加一条规则：

> 编辑已有代码时，不要改进相邻的代码、注释或格式。只改必须改的。

久而久之，你的 Rules 会越来越精准地反映你和团队的编码偏好。Claude 不再是一个通用助手，而是一个了解你项目所有约定的专属开发者。',

'<h2>为什么需要 Rules？</h2>
<p>Claude Code 很聪明，但它不了解你的项目。每次对话开始时，它的知识是通用的。Rules 系统的作用就是给 Claude 「注入项目记忆」——告诉它这个项目用什么技术栈、遵循什么编码规范、有哪些特殊约定。</p>
<p>Rules 的核心价值在于<strong>精准度</strong>。一个通用的 Claude 可以做任何事，但只有加载了 Rules 的 Claude 才能以你想要的方式做这件事。</p>
<h2>Rules 的四层金字塔</h2>
<pre><code>         ┌─────────────┐
         │ 企业组织级   │ ← 管理员控制，所有项目
         ├─────────────┤
         │ 全局个人     │ ← ~/.claude/CLAUDE.md
         ├─────────────┤
         │ 项目级       │ ← ./CLAUDE.md
         ├─────────────┤
         │ 模块化规则   │ ← .claude/rules/*.md
         └─────────────┘</code></pre>
<p>加载顺序：企业级 → 全局 → 项目根 CLAUDE.md → rules/*.md。<strong>后面的会追加到前面的之上</strong>，所以项目级规则可以覆盖全局设置。</p>
<h3>CLAUDE.md vs .claude/rules/</h3>
<p>一个常见的疑问：什么时候放 CLAUDE.md，什么时候拆成 rules？</p>
<p><strong>CLAUDE.md</strong> 适合放项目概览和顶层原则：</p>
<ul><li>项目是什么、有哪些子项目</li><li>核心架构决策</li><li>团队统一的工作流约定</li></ul>
<p><strong>rules/*.md</strong> 适合放模块化、可复用的规则：</p>
<ul><li>特定语言/框架的编码规范</li><li>特定目录的约束条件</li><li>可跨项目复用的行为指南</li></ul>
<p>官方建议：CLAUDE.md 保持在 15 行以内作为概览，详细规范全部放入 rules/。</p>
<h2>Paths 限定：让规则精准生效</h2>
<p>这是 Rules 系统最强大的功能。通过 YAML frontmatter 中的 <code>paths</code> 字段，你可以让一条规则只在特定文件或目录下生效：</p>
<pre><code class="language-markdown">---
paths:
  - &quot;hedgehog-server/**/*.java&quot;
  - &quot;!hedgehog-server/**/*Test.java&quot;
---
# 后端开发规范

使用 Spring Boot 2.7.18 + Java 8 + MyBatis Plus。
所有响应用 Result&lt;T&gt; 包装。</code></pre>
<p>这个规则只会在编辑 <code>hedgehog-server</code> 下的 Java 文件时加载，编辑前端代码或测试文件时不会加载。</p>
<p>路径匹配规则：</p>
<ul><li><code>path/**/*.ts</code> — glob 模式匹配</li><li><code>!path/**/*.test.ts</code> — 叹号表示排除</li><li>路径越精准的规则，优先级越高</li></ul>
<h2>实战模板：编码行为指南</h2>
<p>以下是一份可直接复制的编码行为指南模板。把它保存为 <code>.claude/rules/guidelines.md</code>，所有项目通用：</p>
<pre><code class="language-markdown"># 编码行为指南

## 1. 先思考再编码
不要假设。不要隐藏困惑。明确说出取舍。
如果存在多种理解方式，把它们都列出来。

## 2. 简单至上
用最少的代码解决问题。不要做推测性的工作。
不为只使用一次的代码创建抽象。

## 3. 精准修改
只改必须改的。匹配现有风格。
不改进相邻的代码、注释或格式。

## 4. 目标驱动执行
将任务转化为可验证的目标：
- &quot;添加验证&quot; → &quot;为无效输入编写测试，然后让测试通过&quot;
- &quot;修复 bug&quot; → &quot;编写一个可复现的测试，然后让它通过&quot;</code></pre>
<h2>实战模板：技术栈规则</h2>
<p>对于多子项目的 monorepo，可以为每个子项目创建独立的技术栈规则。例如后端规则 <code>tech-stack-backend.md</code>：</p>
<pre><code class="language-markdown">---
paths:
  - &quot;hedgehog-server/**/*.java&quot;
  - &quot;hedgehog-server/**/*.xml&quot;
---
# 后端技术栈

- Spring Boot 2.7.18 + Java 8 + Maven
- MyBatis Plus 3.5.5（逻辑删除、雪花ID）
- JWT 认证（jjwt 0.12.5）+ BCrypt 密码加密
- API 响应：Result&lt;T&gt; 包装 { code, message, data }
- 不使用 Spring Security</code></pre>
<p>将这个文件保存到 <code>.claude/rules/tech-stack-backend.md</code>，Claude 在编辑后端代码时会自动加载这些约束。</p>
<h2>迭代你的 Rules</h2>
<p>最重要的原则：<strong>每当 Claude 做错一件事，就加到 Rules 中。</strong> 这不是一次性的工作，而是一个持续积累的过程。</p>
<p>举个例子：你发现 Claude 总是喜欢在改 bug 的时候顺手「优化」相邻的代码。那就加一条规则：</p>
<blockquote><p>编辑已有代码时，不要改进相邻的代码、注释或格式。只改必须改的。</p></blockquote>
<p>久而久之，你的 Rules 会越来越精准地反映你和团队的编码偏好。Claude 不再是一个通用助手，而是一个了解你项目所有约定的专属开发者。</p>',

2, 1, 0, 1, NOW()
);

-- ==================== 文章 4：Hooks 实战 ====================
INSERT INTO blog_article (id, title, slug, summary, content_md, content_html, category_id, status, is_top, author_id, publish_time) VALUES (
4,
'Claude Code Hooks 实战 —— 事件驱动的自动化工作流',
'claude-code-hooks-guide',
'全面掌握 Hooks 系统的五大类型与完整生命周期事件。通过三个实战案例（自动格式化代码、拦截危险命令、启动时激活环境），教你构建事件驱动的自动化开发工作流，每个案例附带完整配置代码。',

'## 什么是 Hooks？

Hooks 是 Claude Code 的事件驱动自动化系统。它允许你在 Claude 的**生命周期事件**发生时自动执行自定义逻辑——就像 Git Hooks 之于 Git，或者 Webhooks 之于 Web 服务。

Hooks 让你可以在不改变 Claude 核心行为的前提下，插入自己的质量门禁、安全策略和工作流自动化。

## Hooks 体系架构

### 五大 Hook 类型

| 类型 | 说明 | 典型场景 |
|------|------|--------|
| `command` | 执行 shell 命令 | 自动格式化、lint 检查 |
| `prompt` | LLM 评估 | 代码质量审查 |
| `agent` | 异步 subagent | 后台任务处理 |
| `http` | 调用外部 webhook | CI/CD 触发 |
| `mcp_tool` | 调用 MCP 工具 | 数据库查询、API 调用 |

`command` 是最常用也最灵活的类型，本文的重点就在它身上。

### 完整的生命周期事件

Claude Code 暴露了 14 个生命周期事件，覆盖了从会话开始到结束的完整链路：

```
SessionStart → UserPromptSubmit → PreToolUse → PostToolUse → Stop → SessionEnd
                                      ↓
                              PostToolUseFailure
```

最常用的四个事件：

| 事件 | 触发时机 | 用途 |
|------|---------|------|
| `SessionStart` | 会话开始时 | 激活虚拟环境、检查环境 |
| `PreToolUse` | 工具执行前 | 安全检查（可阻止危险操作）|
| `PostToolUse` | 工具执行成功后 | 自动格式化、lint、通知 |
| `Stop` | Claude 响应完成 | 自动测试、通知 |

### Matcher 语法

Matcher 决定一个 Hook 在哪些工具调用上触发：

```json
"matcher": "Edit"           // 精确匹配
"matcher": "Edit|Write"      // 匹配多个
"matcher": "*"               // 匹配所有
"matcher": "Bash"            // 支持 if 字段做高级过滤
```

### 退出码

| 退出码 | 含义 |
|--------|------|
| 0 | 成功/允许。stdout 可返回上下文数据 |
| 2 | 阻止操作（仅 PreToolUse）。stderr 作为反馈 |
| 其他 | 非阻塞错误，stderr 在 verbose 模式显示 |

## 实战案例一：PostToolUse 自动格式化代码

最实用的 Hook——每次文件编辑后自动调用 Prettier 格式化：

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

关键细节：
- 使用 `$CLAUDE_PROJECT_DIR` 和 `$CLAUDE_TOOL_INPUT_FILE_PATH` 环境变量定位文件
- `|| true` 确保即使格式化失败也不会阻塞 Claude
- `Edit|Write` matcher 确保只在文件修改时触发

## 实战案例二：PreToolUse 拦截危险命令

安全第一——防止 Claude 执行不可逆的破坏性操作：

```json
{
  "hooks": {
    "PreToolUse": [
      {
        "matcher": "Bash",
        "hooks": [
          {
            "type": "command",
            "command": "echo \"$CLAUDE_TOOL_INPUT\" | grep -qE ''(rm -rf /|git push --force origin master)'' && exit 2 || exit 0"
          }
        ]
      }
    ]
  }
}
```

工作原理：
1. 每次 Bash 工具调用前，检查命令内容
2. 如果匹配危险模式（`rm -rf /`、`git push --force origin master`），exit 2 阻止操作
3. Claude 会收到 stderr 中的反馈信息，向用户说明操作被阻止的原因

你可以扩展这个模式，加入更多需要拦截的命令。

## 实战案例三：SessionStart 激活虚拟环境

确保每个会话都在正确的开发环境中运行：

```json
{
  "hooks": {
    "SessionStart": [
      {
        "hooks": [
          {
            "type": "command",
            "command": "source ./venv/bin/activate 2>/dev/null && echo ''virtualenv activated'' || echo ''no virtualenv found''"
          }
        ]
      }
    ]
  }
}
```

## Hooks 配置位置

Hooks 配置在 `settings.json` 中，按配置层级生效：

```
~/.claude/settings.json              # 全局 hooks（所有项目）
.claude/settings.json                # 项目 hooks（团队共享，提交 Git）
.claude/settings.local.json          # 个人 hooks（不提交 Git）
```

建议：安全类 hooks（如危险命令拦截）放在全局配置，格式化类 hooks 放在项目配置。

## 调试 Hooks

如果 Hook 不生效，从以下方向排查：
1. 检查 JSON 语法是否正确（逗号、括号匹配）
2. 确认 matcher 模式是否匹配目标工具
3. 在 verbose 模式下查看 stderr 输出
4. 先用简单的 `echo` 命令测试 Hook 是否被触发',

'<h2>什么是 Hooks？</h2>
<p>Hooks 是 Claude Code 的事件驱动自动化系统。它允许你在 Claude 的<strong>生命周期事件</strong>发生时自动执行自定义逻辑——就像 Git Hooks 之于 Git，或者 Webhooks 之于 Web 服务。</p>
<p>Hooks 让你可以在不改变 Claude 核心行为的前提下，插入自己的质量门禁、安全策略和工作流自动化。</p>
<h2>Hooks 体系架构</h2>
<h3>五大 Hook 类型</h3>
<table><thead><tr><th>类型</th><th>说明</th><th>典型场景</th></tr></thead><tbody><tr><td><code>command</code></td><td>执行 shell 命令</td><td>自动格式化、lint 检查</td></tr><tr><td><code>prompt</code></td><td>LLM 评估</td><td>代码质量审查</td></tr><tr><td><code>agent</code></td><td>异步 subagent</td><td>后台任务处理</td></tr><tr><td><code>http</code></td><td>调用外部 webhook</td><td>CI/CD 触发</td></tr><tr><td><code>mcp_tool</code></td><td>调用 MCP 工具</td><td>数据库查询、API 调用</td></tr></tbody></table>
<p><code>command</code> 是最常用也最灵活的类型，本文的重点就在它身上。</p>
<h3>完整的生命周期事件</h3>
<p>Claude Code 暴露了 14 个生命周期事件，覆盖了从会话开始到结束的完整链路：</p>
<pre><code>SessionStart → UserPromptSubmit → PreToolUse → PostToolUse → Stop → SessionEnd
                                      ↓
                              PostToolUseFailure</code></pre>
<p>最常用的四个事件：</p>
<table><thead><tr><th>事件</th><th>触发时机</th><th>用途</th></tr></thead><tbody><tr><td><code>SessionStart</code></td><td>会话开始时</td><td>激活虚拟环境、检查环境</td></tr><tr><td><code>PreToolUse</code></td><td>工具执行前</td><td>安全检查（可阻止危险操作）</td></tr><tr><td><code>PostToolUse</code></td><td>工具执行成功后</td><td>自动格式化、lint、通知</td></tr><tr><td><code>Stop</code></td><td>Claude 响应完成</td><td>自动测试、通知</td></tr></tbody></table>
<h3>Matcher 语法</h3>
<p>Matcher 决定一个 Hook 在哪些工具调用上触发：</p>
<pre><code class="language-json">&quot;matcher&quot;: &quot;Edit&quot;           // 精确匹配
&quot;matcher&quot;: &quot;Edit|Write&quot;      // 匹配多个
&quot;matcher&quot;: &quot;*&quot;               // 匹配所有
&quot;matcher&quot;: &quot;Bash&quot;            // 支持 if 字段做高级过滤</code></pre>
<h3>退出码</h3>
<table><thead><tr><th>退出码</th><th>含义</th></tr></thead><tbody><tr><td>0</td><td>成功/允许。stdout 可返回上下文数据</td></tr><tr><td>2</td><td>阻止操作（仅 PreToolUse）。stderr 作为反馈</td></tr><tr><td>其他</td><td>非阻塞错误，stderr 在 verbose 模式显示</td></tr></tbody></table>
<h2>实战案例一：PostToolUse 自动格式化代码</h2>
<p>最实用的 Hook——每次文件编辑后自动调用 Prettier 格式化：</p>
<pre><code class="language-json">{
  &quot;hooks&quot;: {
    &quot;PostToolUse&quot;: [
      {
        &quot;matcher&quot;: &quot;Edit|Write&quot;,
        &quot;hooks&quot;: [
          {
            &quot;type&quot;: &quot;command&quot;,
            &quot;command&quot;: &quot;prettier --write \\&quot;$CLAUDE_PROJECT_DIR/${CLAUDE_TOOL_INPUT_FILE_PATH}\\&quot; 2&gt;/dev/null || true&quot;
          }
        ]
      }
    ]
  }
}</code></pre>
<p>关键细节：</p>
<ul><li>使用 <code>$CLAUDE_PROJECT_DIR</code> 和 <code>$CLAUDE_TOOL_INPUT_FILE_PATH</code> 环境变量定位文件</li><li><code>|| true</code> 确保即使格式化失败也不会阻塞 Claude</li><li><code>Edit|Write</code> matcher 确保只在文件修改时触发</li></ul>
<h2>实战案例二：PreToolUse 拦截危险命令</h2>
<p>安全第一——防止 Claude 执行不可逆的破坏性操作：</p>
<pre><code class="language-json">{
  &quot;hooks&quot;: {
    &quot;PreToolUse&quot;: [
      {
        &quot;matcher&quot;: &quot;Bash&quot;,
        &quot;hooks&quot;: [
          {
            &quot;type&quot;: &quot;command&quot;,
            &quot;command&quot;: &quot;echo \\&quot;$CLAUDE_TOOL_INPUT\\&quot; | grep -qE ''(rm -rf /|git push --force origin master)'' &amp;&amp; exit 2 || exit 0&quot;
          }
        ]
      }
    ]
  }
}</code></pre>
<p>工作原理：</p>
<ol><li>每次 Bash 工具调用前，检查命令内容</li><li>如果匹配危险模式（<code>rm -rf /</code>、<code>git push --force origin master</code>），exit 2 阻止操作</li><li>Claude 会收到 stderr 中的反馈信息，向用户说明操作被阻止的原因</li></ol>
<p>你可以扩展这个模式，加入更多需要拦截的命令。</p>
<h2>实战案例三：SessionStart 激活虚拟环境</h2>
<p>确保每个会话都在正确的开发环境中运行：</p>
<pre><code class="language-json">{
  &quot;hooks&quot;: {
    &quot;SessionStart&quot;: [
      {
        &quot;hooks&quot;: [
          {
            &quot;type&quot;: &quot;command&quot;,
            &quot;command&quot;: &quot;source ./venv/bin/activate 2&gt;/dev/null &amp;&amp; echo ''virtualenv activated'' || echo ''no virtualenv found''&quot;
          }
        ]
      }
    ]
  }
}</code></pre>
<h2>Hooks 配置位置</h2>
<p>Hooks 配置在 <code>settings.json</code> 中，按配置层级生效：</p>
<pre><code>~/.claude/settings.json              # 全局 hooks（所有项目）
.claude/settings.json                # 项目 hooks（团队共享，提交 Git）
.claude/settings.local.json          # 个人 hooks（不提交 Git）</code></pre>
<p>建议：安全类 hooks（如危险命令拦截）放在全局配置，格式化类 hooks 放在项目配置。</p>
<h2>调试 Hooks</h2>
<p>如果 Hook 不生效，从以下方向排查：</p>
<ol><li>检查 JSON 语法是否正确（逗号、括号匹配）</li><li>确认 matcher 模式是否匹配目标工具</li><li>在 verbose 模式下查看 stderr 输出</li><li>先用简单的 <code>echo</code> 命令测试 Hook 是否被触发</li></ol>',

3, 1, 0, 1, NOW()
);

-- ==================== 文章 5：配置体系全解析 ====================
INSERT INTO blog_article (id, title, slug, summary, content_md, content_html, category_id, status, is_top, author_id, publish_time) VALUES (
5,
'Claude Code 配置体系全解析 —— settings、CLAUDE.md 与项目最佳实践',
'claude-code-config-guide',
'从零掌握 Claude Code 的完整配置体系：五层配置优先级、settings.json 核心选项详解、项目目录结构规范、权限模式选择策略。附带快速上手清单和新项目配置模板。',

'## 配置体系总览

Claude Code 的配置体系由三个支柱构成：**settings.json**（行为控制）、**CLAUDE.md**（知识注入）、**.claude/rules/**（模块化规则）。理解三者的分工是高效使用 Claude Code 的基础。

## 配置文件的五层优先级

```
高 ↑
│  1. 企业托管策略        （管理员强制，最高权限）
│  2. CLI --settings 参数  （单次调用临时覆盖）
│  3. .claude/settings.local.json  （个人本地，不提交 Git）
│  4. .claude/settings.json         （团队共享，提交 Git）
│  5. ~/.claude/settings.json       （全局默认）
低 ↓
```

记住一条规则：**越靠近具体项目的配置，优先级越高。** 个人的 local 设置可以覆盖团队共享设置，团队共享设置可以覆盖全局默认。

## settings.json 核心配置详解

### 基础设置

```json
{
  "model": "sonnet",
  "autoUpdatesChannel": "stable",
  "cleanupPeriodDays": 14,
  "showTurnDuration": true
}
```

- **model**：可选 `sonnet` / `opus` / `haiku`，也可通过 `/model` 命令切换
- **autoUpdatesChannel**：`stable`（稳定版）或 `latest`（最新版）
- **cleanupPeriodDays**：本地会话文件的保留天数
- **showTurnDuration**：显示每次对话的耗时

### 权限配置

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

`allow` 预授权：这些命令自动执行，无需用户确认。适合高频安全操作（如 npm run、git status）。

`deny` 黑名单：这些命令**永远不执行**，即使 Claude 想执行也会被拦截。适合不可逆的破坏性操作。

### 自动模式

```json
{
  "autoMode": {
    "environment": ["bash", "node"],
    "allow": ["Read", "Glob", "Grep", "Edit", "Write"],
    "soft_deny": ["Bash(git push *)"]
  }
}
```

`soft_deny` 中的操作仍会显示但不阻塞——这是一种「警告但不拦截」的策略。

## 权限模式选择指南

| 模式 | 说明 | 适用场景 |
|------|------|--------|
| `default` | 大部分工具需要确认 | 日常开发，最推荐 |
| `accept-edits` | 自动接受文件编辑，其他需确认 | 信任度高的重构任务 |
| `plan` | 完全只读，不允许修改 | 代码审查、调研分析 |
| `bypass` | 全部自动执行，无提示 | 高度受控的自动化脚本 |

通过 `--permission-mode` 或 `/permissions` 命令切换。日常使用建议保持 `default`，在执行大范围确定性重构时切换到 `accept-edits`。

## 项目目录结构参考

```
your-project/
├── CLAUDE.md                  # 项目主指令（概览，建议简洁）
├── CLAUDE.local.md            # 个人本地覆盖（gitignored）
├── .claude/
│   ├── settings.json          # 项目共享设置（permissions, hooks, env）
│   ├── settings.local.json    # 个人设置覆盖（gitignored）
│   ├── rules/                 # 模块化规则（自动递归加载）
│   │   ├── code-style.md
│   │   ├── testing.md
│   │   └── tech-stack.md
│   ├── skills/                # 项目级 Skills
│   │   └── <name>/SKILL.md
│   ├── agents/                # 自定义 Agent 定义
│   ├── commands/              # 自定义斜杠命令
│   └── hooks/                 # Hook 脚本
├── .gitignore                 # 含 CLAUDE.local.md, settings.local.json
```

## 环境变量注入

```json
{
  "env": {
    "DATABASE_URL": "jdbc:mysql://localhost:3306/mydb",
    "DEBUG": "true"
  }
}
```

在 settings.json 中通过 `env` 字段注入环境变量，这些变量在整个会话期间可用。适合注入 API keys、数据库连接串等。

## 快速上手清单

如果你是从零开始配置 Claude Code，按以下顺序操作：

1. 全局安装：`npm install -g @anthropic-ai/claude-code`
2. 认证：运行 `claude` 并跟随提示登录
3. 配置 `~/.claude/settings.json`：设定 model、autoUpdatesChannel
4. 创建 `~/.claude/CLAUDE.md`：写入你的个人跨项目偏好
5. 在项目中运行 `/init` 自动生成 CLAUDE.md，然后审查修剪
6. 配置 `/permissions` 预授权安全的高频命令
7. 学习核心命令：`/clear`、`/compact`、`/context`、`/undo`
8. 当你发现反复执行的任务时，创建你的第一个 Skill

好的配置不是一次性写成的——它随着项目的演进而不断打磨。每个项目都是独特的，花时间调好你的配置，后续的开发效率会有质的提升。',

'<h2>配置体系总览</h2>
<p>Claude Code 的配置体系由三个支柱构成：<strong>settings.json</strong>（行为控制）、<strong>CLAUDE.md</strong>（知识注入）、<strong>.claude/rules/</strong>（模块化规则）。理解三者的分工是高效使用 Claude Code 的基础。</p>
<h2>配置文件的五层优先级</h2>
<pre><code>高 ↑
│  1. 企业托管策略        （管理员强制，最高权限）
│  2. CLI --settings 参数  （单次调用临时覆盖）
│  3. .claude/settings.local.json  （个人本地，不提交 Git）
│  4. .claude/settings.json         （团队共享，提交 Git）
│  5. ~/.claude/settings.json       （全局默认）
低 ↓</code></pre>
<p>记住一条规则：<strong>越靠近具体项目的配置，优先级越高。</strong> 个人的 local 设置可以覆盖团队共享设置，团队共享设置可以覆盖全局默认。</p>
<h2>settings.json 核心配置详解</h2>
<h3>基础设置</h3>
<pre><code class="language-json">{
  &quot;model&quot;: &quot;sonnet&quot;,
  &quot;autoUpdatesChannel&quot;: &quot;stable&quot;,
  &quot;cleanupPeriodDays&quot;: 14,
  &quot;showTurnDuration&quot;: true
}</code></pre>
<ul><li><strong>model</strong>：可选 <code>sonnet</code> / <code>opus</code> / <code>haiku</code>，也可通过 <code>/model</code> 命令切换</li><li><strong>autoUpdatesChannel</strong>：<code>stable</code>（稳定版）或 <code>latest</code>（最新版）</li><li><strong>cleanupPeriodDays</strong>：本地会话文件的保留天数</li><li><strong>showTurnDuration</strong>：显示每次对话的耗时</li></ul>
<h3>权限配置</h3>
<pre><code class="language-json">{
  &quot;permissions&quot;: {
    &quot;allow&quot;: [
      &quot;Bash(npm run *)&quot;,
      &quot;Bash(git diff *)&quot;,
      &quot;Bash(git status)&quot;,
      &quot;Bash(git log *)&quot;
    ],
    &quot;deny&quot;: [
      &quot;Bash(rm -rf *)&quot;,
      &quot;Bash(git push --force origin main)&quot;,
      &quot;Bash(git push --force origin master)&quot;
    ]
  }
}</code></pre>
<p><code>allow</code> 预授权：这些命令自动执行，无需用户确认。适合高频安全操作（如 npm run、git status）。</p>
<p><code>deny</code> 黑名单：这些命令<strong>永远不执行</strong>，即使 Claude 想执行也会被拦截。适合不可逆的破坏性操作。</p>
<h3>自动模式</h3>
<pre><code class="language-json">{
  &quot;autoMode&quot;: {
    &quot;environment&quot;: [&quot;bash&quot;, &quot;node&quot;],
    &quot;allow&quot;: [&quot;Read&quot;, &quot;Glob&quot;, &quot;Grep&quot;, &quot;Edit&quot;, &quot;Write&quot;],
    &quot;soft_deny&quot;: [&quot;Bash(git push *)&quot;]
  }
}</code></pre>
<p><code>soft_deny</code> 中的操作仍会显示但不阻塞——这是一种「警告但不拦截」的策略。</p>
<h2>权限模式选择指南</h2>
<table><thead><tr><th>模式</th><th>说明</th><th>适用场景</th></tr></thead><tbody><tr><td><code>default</code></td><td>大部分工具需要确认</td><td>日常开发，最推荐</td></tr><tr><td><code>accept-edits</code></td><td>自动接受文件编辑，其他需确认</td><td>信任度高的重构任务</td></tr><tr><td><code>plan</code></td><td>完全只读，不允许修改</td><td>代码审查、调研分析</td></tr><tr><td><code>bypass</code></td><td>全部自动执行，无提示</td><td>高度受控的自动化脚本</td></tr></tbody></table>
<p>通过 <code>--permission-mode</code> 或 <code>/permissions</code> 命令切换。日常使用建议保持 <code>default</code>，在执行大范围确定性重构时切换到 <code>accept-edits</code>。</p>
<h2>项目目录结构参考</h2>
<pre><code>your-project/
├── CLAUDE.md                  # 项目主指令（概览，建议简洁）
├── CLAUDE.local.md            # 个人本地覆盖（gitignored）
├── .claude/
│   ├── settings.json          # 项目共享设置（permissions, hooks, env）
│   ├── settings.local.json    # 个人设置覆盖（gitignored）
│   ├── rules/                 # 模块化规则（自动递归加载）
│   │   ├── code-style.md
│   │   ├── testing.md
│   │   └── tech-stack.md
│   ├── skills/                # 项目级 Skills
│   │   └── &lt;name&gt;/SKILL.md
│   ├── agents/                # 自定义 Agent 定义
│   ├── commands/              # 自定义斜杠命令
│   └── hooks/                 # Hook 脚本
├── .gitignore                 # 含 CLAUDE.local.md, settings.local.json</code></pre>
<h2>环境变量注入</h2>
<pre><code class="language-json">{
  &quot;env&quot;: {
    &quot;DATABASE_URL&quot;: &quot;jdbc:mysql://localhost:3306/mydb&quot;,
    &quot;DEBUG&quot;: &quot;true&quot;
  }
}</code></pre>
<p>在 settings.json 中通过 <code>env</code> 字段注入环境变量，这些变量在整个会话期间可用。适合注入 API keys、数据库连接串等。</p>
<h2>快速上手清单</h2>
<p>如果你是从零开始配置 Claude Code，按以下顺序操作：</p>
<ol><li>全局安装：<code>npm install -g @anthropic-ai/claude-code</code></li><li>认证：运行 <code>claude</code> 并跟随提示登录</li><li>配置 <code>~/.claude/settings.json</code>：设定 model、autoUpdatesChannel</li><li>创建 <code>~/.claude/CLAUDE.md</code>：写入你的个人跨项目偏好</li><li>在项目中运行 <code>/init</code> 自动生成 CLAUDE.md，然后审查修剪</li><li>配置 <code>/permissions</code> 预授权安全的高频命令</li><li>学习核心命令：<code>/clear</code>、<code>/compact</code>、<code>/context</code>、<code>/undo</code></li><li>当你发现反复执行的任务时，创建你的第一个 Skill</li></ol>
<p>好的配置不是一次性写成的——它随着项目的演进而不断打磨。每个项目都是独特的，花时间调好你的配置，后续的开发效率会有质的提升。</p>',

3, 1, 0, 1, NOW()
);

-- =============================================
-- 四、文章-标签关联
-- =============================================
INSERT IGNORE INTO blog_article_tag (article_id, tag_id) VALUES
-- 文章 1：Skills 入门 — 标签：Claude Code, Skills, 教程
(1, 1), (1, 2), (1, 6),
-- 文章 2：Skills 进阶 — 标签：Claude Code, Skills, 实战, Git, 前端
(2, 1), (2, 2), (2, 7), (2, 8), (2, 9),
-- 文章 3：Rules 详解 — 标签：Claude Code, Rules, 教程
(3, 1), (3, 3), (3, 6),
-- 文章 4：Hooks 实战 — 标签：Claude Code, Hooks, 实战, 自动化
(4, 1), (4, 4), (4, 7), (4, 10),
-- 文章 5：配置全解析 — 标签：Claude Code, 配置, 教程
(5, 1), (5, 5), (5, 6);

-- =============================================
-- 五、站点配置
-- =============================================
UPDATE site_config SET
    site_name = 'Hedgehog Blog',
    site_subtitle = '探索 AI 辅助开发的无限可能',
    author_name = 'Hedgehog',
    author_bio = '全栈开发者，AI 编程深度用户，热爱探索人工智能在软件开发中的最佳实践。',
    footer_text = 'Powered by Hedgehog & AI'
WHERE id = 1;
