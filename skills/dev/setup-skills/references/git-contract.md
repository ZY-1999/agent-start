# Git 契约（Git Contract）

本仓库的 git 工作流契约——分支策略、提交信息约定、什么进/不进仓库。向仓库写入文档或代码的 SDD skills 都遵循它，并且**为每次写入声明 git 语义**。

## 分支策略

- 开发前先核验分支是否正确。一个需求可能需要多人协作，优先在同一条 `feature/` 分支上协作。
- 分支类型与关联对象的映射表。本模板默认以禅道为关联对象；不接禅道的仓库把「主要关联对象」一列换成实际载体（issue slug、任务编号等）：

| 分支类型 | 命名格式 | 创建来源 | 合并去向 | 主要关联对象 | 生命周期 |
|---|---|---|---|---|---|
| `main` / `master` | 固定名称 | — | — | 版本 / 发布 | 永久 |
| `develop` | 固定名称 | `main` | — | 迭代 / 版本 | 永久 |
| `feature/*` | `feature/<需求ID>-<描述>` | `develop` | `develop` | 需求（Story） | 临时 |
| `bugfix/*` | `bugfix/<Bug-ID>-<描述>` | `develop` | `develop` | Bug | 临时 |
| `release/*` | `release/v<版本号>` | `develop` | `main` + `develop` | 版本 / 发布 | 临时 |
| `hotfix/*` | `hotfix/<Bug-ID>-<描述>` | `main` | `main` + `develop`（+ 进行中的 `release`） | Bug | 临时 |

_<部署时定稿：按仓库实际用到的分支类型裁剪本表；直接在当前分支开发的仓库只保留 `main` 一行并删掉其余行与上表的分支类型说明。>_

## 提交信息

- 历史提交可能并存 `type(scope): 描述` 冒号风格（Conventional Commits）与 `账号:姓名-描述` 形式，仅作历史参考，不强制沿用。
- 每个 commit 至少包含一个关联 ID（禅道 task / bug / story，或 issue tracker 的 slug）。没有关联对象的提交在追溯时无法落到需求—任务—代码的闭环里；确实没有现成任务时，先确认是否要新建，而不是提交一条无关联 commit。
- 模板：

```text
# <类型>/[<关联ID>]:<模块>-<主题>
# 类型: feat | fix | docs | style | refactor | test | chore
# 关联ID: task # / bug # / story #（或 issue slug）
# 示例: fix/bug#456: 支付-修复回调验签失败
```

_<部署时定稿：不接禅道的仓库把示例换成 issue slug 形式，如 `feat(login-redirect): 刷新令牌轮换`。>_

## 什么进仓库

- `AGENTS.md` —— 根配置入口，可提交。
- `docs/agents/` —— 与入口关联的契约文件，可提交。
- `.scratch/` —— issue tracker；issue 是 `spec` / `prd` / `bug` 的载体。
- `CONTEXT.md`、`docs/adr/`、`docs/codemap/` —— 领域文档与代码地形。

## 什么不进仓库

- 仓库 `.gitignore` 忽略项绝不进仓库。
- `/handoff` 输出——写到操作系统临时目录，绝不进仓库。
- 本地凭据、`.env`、API key、token。
- Agent 运行数据（`.agent-memory/`、本地 SQLite / Milvus Lite db、trace / episode / candidate / asset）。
- 不主动改动 `.gitignore`；提交与暂存忠实遵守仓库现有 `.gitignore`。

## 每次写入都要声明 git 语义

当某个 skill 向仓库写入文档或代码文件时，用一行说明该次写入的 git 语义——落在哪个提交里，或者保持未提交（及原因）。
