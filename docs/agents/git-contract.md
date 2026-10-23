# Git 契约（Git Contract）

本仓库的 git 工作流契约——分支策略、提交信息约定、什么进/不进仓库。向仓库写入文档或代码的 SDD skills 都遵循它，并且**为每次写入声明 git 语义**。

## 分支策略

- **Solo** —— 直接在当前分支开发，不建特性分支。

## 提交信息

- 引用 issue slug（例如 `feat(login-redirect): 刷新令牌轮换`）。
- 仓库特定约定：conventional-commits 前缀（`docs:` / `feat:` / `fix:` / `chore:` …），标题可用中文。

## 什么进仓库

- `.scratch/` —— issue tracker；issue 是 `spec` / `prd` / `bug` 的载体。
- `CONTEXT.md`、`docs/adr/`、`docs/codemap/` —— 领域文档与代码地形。
- `docs/agents/` —— 本套配置。`/setup-skills` 运行时或内容被编辑时提交。

## 什么不进仓库

- `/handoff` 输出——写到操作系统临时目录，绝不进仓库。
- 本地凭据、`.env`、API key、token。
- Agent 运行数据（`.agent-memory/`、本地 SQLite/Milvus Lite 数据库、trace/episode/candidate/asset）。

## 每次写入都要声明 git 语义

当某个 skill 向仓库写入文档或代码文件时，用一行说明该次写入的 git 语义——落在哪个提交里，或者保持未提交（及原因）。
