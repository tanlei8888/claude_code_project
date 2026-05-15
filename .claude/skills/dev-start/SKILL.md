---
name: dev-start
description: 一键检查并启动开发环境的三个项目。支持自动诊断依赖缺失、端口占用等常见问题，修复操作前会先询问用户确认。
---

当用户调用此技能时，按以下阶段逐步执行。

## 阶段 1：环境检查

静默执行以下检查（无需确认），汇总后告知用户。

### 1.1 工具链检查

并行运行以下命令：
- `java -version 2>&1` — 检查 Java 可用性
- `mvn -version 2>&1` — 检查 Maven 可用性
- `node -v 2>&1` — 检查 Node.js 可用性
- `npm -v 2>&1` — 检查 npm 可用性

验收标准：
- Java 版本号 ≥ 1.8
- Node 版本号 ≥ 18.0.0
- Maven 和 npm 命令存在

### 1.2 端口占用检查

在 Windows 上检查以下端口：

```
netstat -ano | findstr ":8080"
netstat -ano | findstr ":3000"
netstat -ano | findstr ":3001"
```

### 1.3 MySQL 连通性检查

使用项目配置的连接信息验证 MySQL 是否可达。用 PowerShell 测试：

```
powershell -Command "Test-NetConnection -ComputerName 120.79.83.62 -Port 54231"
```

### 1.4 汇总报告

将以上检查结果汇总为表格告知用户：

```
环境检查结果：
  ✅ Java   1.8.0_xxx
  ✅ Maven  3.x.x
  ✅ Node   20.x.x
  ✅ npm    10.x.x
  ✅ 端口 8080 — 空闲
  ❌ 端口 3000 — 被占用 (PID 12345)
  ✅ 端口 3001 — 空闲
  ✅ MySQL  — 可达
```

## 阶段 2：依赖诊断

检查各子项目的依赖是否就绪。**每项修复操作必须先使用 AskUserQuestion 询问用户，获得同意后才执行。**

### 2.1 hedgehog-server（Maven）

检查方式：确认 `pom.xml` 存在，检查 `~/.m2/repository` 目录是否存在（Maven 本地仓库）。

- 如果 `pom.xml` 存在但 Maven 本地仓库为空/不存在 → 提议 `cd hedgehog-server && mvn dependency:resolve`
- 如果项目之前从未编译过 → 提议 `cd hedgehog-server && mvn compile -DskipTests`

### 2.2 hedgehog-web / hedgehog-blog（npm）

检查方式：确认 `node_modules` 目录存在且非空。

- 如果 `node_modules` 不存在 → 提议 `npm install`
- 如果 `node_modules` 存在但 `package.json` 的 dependencies 有更新（对比 node_modules 时间戳）→ 提示用户，建议 `npm install`

**询问模板：**
> 检测到 hedgehog-web 缺少依赖（node_modules 不存在），是否需要执行 `npm install` 安装依赖？
>
> - 选项 1：是，执行安装
> - 选项 2：跳过，我稍后手动处理
> - 选项 3：全部自动修复（跳过本次后续所有询问）

**重要：** 如果用户选择「全部自动修复」，则后续的修复操作不再询问，直接执行。

## 阶段 3：启动项目

**关键原则：每个项目在独立的 PowerShell 窗口中启动**，这样用户可以实时看到控制台输出，方便调试。

使用 PowerShell `Start-Process` 打开独立 PowerShell 窗口，命令格式：
```bash
powershell -Command "Start-Process powershell -ArgumentList '-NoExit', '-Command', '<启动命令>'"
```

`-NoExit` 参数确保命令执行完后窗口不关闭，方便查看输出和手动重启。

### 3.1 启动顺序

1. **hedgehog-server 先启动**（前端依赖后端 API，必须最先启动）

```bash
powershell -Command "Start-Process powershell -ArgumentList '-NoExit', '-Command', 'cd D:/student/claude-code-project/hedgehog-server; mvn spring-boot:run'"
```

2. **等待 server 启动** — 轮询检查端口 8080

每 5 秒检查一次，最多等待 2 分钟（首次编译可能较慢）：
```
powershell -Command "Test-NetConnection -ComputerName localhost -Port 8080 -WarningAction SilentlyContinue | Select-Object -ExpandProperty TcpTestSucceeded"
```

端口 8080 监听后继续。

3. **并行启动两个前端项目**

```bash
powershell -Command "Start-Process powershell -ArgumentList '-NoExit', '-Command', 'cd D:/student/claude-code-project/hedgehog-web; npm run dev'"
```

```bash
powershell -Command "Start-Process powershell -ArgumentList '-NoExit', '-Command', 'cd D:/student/claude-code-project/hedgehog-blog; npm run dev'"
```

### 3.2 启动后验证

等待 5-8 秒后检查端口 3000 和 3001：

```
powershell -Command "Test-NetConnection -ComputerName localhost -Port 3000 ..."
powershell -Command "Test-NetConnection -ComputerName localhost -Port 3001 ..."
```

### 3.3 汇总结果

```
启动结果（三个独立 PowerShell 窗口已打开）：
  ✅ hedgehog-server   http://localhost:8080  [窗口 1 — hedgehog-server]
  ✅ hedgehog-web      http://localhost:3000  [窗口 2 — hedgehog-web]
  ✅ hedgehog-blog     http://localhost:3001  [窗口 3 — hedgehog-blog]
```

不要用 `run_in_background`，因为用户看不到控制台输出，不利于调试。

如果某项目启动失败（端口未监听），尝试查看对应窗口的错误信息或读取项目日志文件。

## 阶段 4：自我修复

当用户调用此技能并提到"修复"、"出错了"、"排查"等关键词，或启动失败后自动进入此阶段。

### 4.1 常见问题诊断矩阵

| 症状 | 可能原因 | 修复方案 | 需确认 |
|------|---------|---------|--------|
| `mvn` 找不到 | Maven 未安装或未配 PATH | 提示安装 Maven | — |
| `node` 找不到 | Node.js 未安装 | 提示安装 Node.js 18+ | — |
| 端口被占用 | 上次未正常关闭 | `taskkill /PID xxx` 释放端口 | ✅ 询问 |
| `node_modules` 缺失 | 首次运行或依赖被清理 | `npm install` | ✅ 询问 |
| Maven 编译失败 | 依赖下载失败或代码错误 | 检查 `.m2/repository`，清理后重新 `mvn compile` | ✅ 询问 |
| MySQL 连接失败 | 数据库不可达或密码错误 | 检查网络/VPN，核对 `application-dev.yml` 配置 | ✅ 询问 |
| 前端 404 API | server 未启动或端口不对 | 检查 8080 端口，确认 Vite proxy 配置正确 | — |
| Spring Boot 启动失败 | 端口冲突 / 配置错误 / 数据库连接失败 | 读取错误日志定位根因 | ✅ 询问 |

### 4.2 修复流程

1. 读取相关日志（后台任务输出、Spring Boot 控制台日志、npm 错误输出）
2. 分析根因，匹配上述矩阵
3. 使用 AskUserQuestion 提出修复方案
4. 获得确认后执行修复
5. 重新尝试启动
6. 重复直到全部启动成功或用户放弃

## 注意事项

- **不修改项目配置文件**（application.yml、vite.config.ts、package.json 等），除非用户明确要求且经过确认。
- **不修改系统环境变量**（PATH、JAVA_HOME 等）。
- **不强制结束进程**，除非用户确认（告知 PID 和进程名）。
- Windows 环境下使用 Power Shell 命令进行端口和网络检查。
- 所有路径使用正斜杠 `/`（Git Bash 兼容）。
