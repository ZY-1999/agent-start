# Git 契约（Git Contract）

本仓库的 git 工作流契约——分支策略、提交信息约定、什么进/不进仓库。向仓库写入文档或代码的 SDD skills 都遵循它，并且**为每次写入声明 git 语义**。

## 分支策略

- 开发前先核验分支是否正确。
- **Solo** —— 直接在当前分支开发，不创建 feature 分支。

## 提交信息

- 本仓库约定：**none** —— 提交信息无额外强制约定（不强制 conventional-commits 前缀、sign-off、关联 ID / issue key）。
- 参考（不强制）：`feat(login-redirect): 刷新令牌轮换` 这类 issue slug 形式。

## 什么进仓库

- `AGENTS.md` —— 根配置入口，可提交。
- `docs/agents/` —— 与入口关联的契约文件，可提交；`/setup-skills` 运行或被编辑时提交。
- `.scratch/` —— issue tracker；issue 是 `spec` / `prd` / `bug` 的载体。
- `CONTEXT.md`、`docs/adr/`、`docs/codemap/` —— 领域文档与代码地形；随相关 spec（`/tdd` 关闭时）提交，或在 `/sdd-flow` 的 summarize 步骤提交。

## 什么不进仓库

- 仓库 `.gitignore` 忽略项绝不进仓库。
- `/handoff` 输出——写到操作系统临时目录，绝不进仓库。
- 本地凭据、`.env`、API key、token。
- Agent 运行数据（`.agent-memory/`、本地 SQLite / Milvus Lite db、trace / episode / candidate / asset）。
- 不主动改动 `.gitignore`；提交与暂存忠实遵守仓库现有 `.gitignore`。

## 每次写入都要声明 git 语义

当某个 skill 向仓库写入文档或代码文件时，用一行说明该次写入的 git 语义——落在哪个提交里，或者保持未提交（及原因）。示例：

- `/to-prd` 发布 `prd`：_"把 PRD 写入 `.scratch/`；不提交 —— 待 Gate 0 批准后落入 `/sdd-flow` 入口处的提交。"_
- `/to-spec` 落盘 spec 骨架：_"把 specs 写入 `.scratch/.../specs/`；不提交 —— Gate A 后随 `/sdd-flow` 的提交一起落入。"_
- `/tdd` 关闭 spec：_"写入代码 + 测试 + 翻转状态，并在 spec 关闭时提交。"_
- `/codemap` 中途刷新地图：_"写入 `docs/codemap/<map>.md`；搭当前 spec 的关闭提交。"_

只读、或只写仓库之外（如 `/handoff` 写临时目录）的 skill 不声明。
