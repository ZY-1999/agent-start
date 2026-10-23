# AGENTS.md 骨架

`/setup-skills` 写入仓库入口文件（`AGENTS.md`，或已存在的 `CLAUDE.md`）的骨架。产物为中文；`<…>` 占位符在部署时按 Explore 结果填写。

合并规则：目标文件已有部分章节时**逐节合并**——`## Agent Notice` 就地更新，只补缺失的顶层章节，不覆盖用户已有编辑，不追加重复的 `## Agent Notice`。

```markdown
# <repo name>

<one line: what this repo is and who reads it>

## 工程概述

- 仓库名称：<name>；形态：<single app / monorepo / skills & docs repo …>
- 目录与职责：<top-level directories and what each owns>
- 技术栈：<languages, frameworks, build tool>

## 语言

- 请使用简体中文与我交流，避免机翻，有特定含义的词语可以不翻译；
- 范围包括聊天对话，spec / prd / bug 等 issue，git 提交 message，代码注释等

## 构建与验证

- 环境要求：<runtime + version, package manager>
- 构建：`<build command>`
- 改动后验证：`<targeted build / test command>`
- 运行 / 启动：`<how to start it locally>`
- 测试现状：<automated suite? manual-only? if manual, state what "verified" means>

## Agent Notice

### Clarify, plan, then execute

接到需求先澄清、再计划、最后执行，三步不跳：

1. **澄清** —— 用 `/grill-me` 拷问需求，直到目标、边界、验收标准、影响面都明确；当决策需要沉淀为 ADR / 词汇表时改用 `/grill-with-docs`。上下文不全就直接提问，不要猜。
2. **计划** —— 给出分阶段计划：改哪些文件、按什么顺序、怎么验证。等用户确认。
3. **执行** —— 确认后才动手；执行中计划要变，先说明再改。

需求本身简单且完整时，澄清可以很短，但"给计划 + 等确认"这两步保留。

### Issue tracker

[one-line summary — local markdown under `.scratch/`]. See `docs/agents/issue-tracker.md`.

### Triage labels

[one-line summary of the label vocabulary]. See `docs/agents/triage-labels.md`.

### Domain docs

[one-line summary of layout — "single-context" or "multi-context"]. See `docs/agents/domain.md`.

### Developer rules

开发规范，适用于人类与 AI。见 `docs/agents/developer.md`。

### Checklist

代码评审 Checklist，用于 review 或需求完成后的检查。见 `docs/agents/checklist.md`。

### CodeMap

[one-line summary — start here when exploring the codebase: a project-level terrain index under `docs/codemap/`; refresh or add feature maps via `/codemap`; when unsure whether a map is still trustworthy, run `/codemap` drift-check — if it reports drift, update the affected map].

### Git contract

[one-line summary — active branch model (the branch types in use) + the association-ID source for commit messages; See `docs/agents/git-contract.md`.

### Third-party skills (zentao / confluence)

[one-line summary — optional section: the plugin's vendored `zentao-cli` / `zentao-tour` skills operate Zentao (禅道) through the `zentao` command (`npm install -g zentao-cli`; credentials recommended as environment variables `ZENTAO_URL` / `ZENTAO_ACCOUNT` / `ZENTAO_TOKEN`|`ZENTAO_PASSWORD` exported in the user's own `~/.bashrc`, local-profile login as fallback), and the vendored `confluence` skill operates Confluence through the `confluence` command (`npm install -g confluence-cli`; profile in `~/.config/confluence-cli/config.json` or `confluence init`). Drop this section if the repo has neither a Zentao nor a Confluence connection.]

## 默认边界

- 项目可以同时拥有 feature 级、spec 级和 project 级知识入口。
- 不要把 agent 运行数据（`.agent-memory/`、本地 SQLite / Milvus Lite db、trace / episode / candidate / asset）提交到仓库。
- 不要提交 `.env`、API key、token 或其他本地凭据。
- 不主动改动 `.gitignore`，包括主动恢复 `.gitignore`；提交和暂存文件时忠实遵守仓库现有 `.gitignore`。

## 最小记忆能力

- 对话中如果感知到稳定、可复用、跨任务会再次影响判断的项目信息或用户偏好，不要只留在聊天里。
- 优先沉淀到项目已有长期知识入口（不存在时自动创建）：本文件明确索引的等价文件。
- 若知识属于当前任务过程，先写入 feature 级 / spec 级知识文档；若属于项目长期规则或反复踩坑，再作为 Project Sync Candidate，经确认后同步到长期知识。
- 记录时保持最小、可审查、可溯源：写清事实、来源、适用边界和验证证据；不要写入密钥、私人信息、未验证猜测或整段聊天记录。
- 触发 new chat、handoff、resume pack、上下文压缩或任务收尾时，必须扫描是否有可沉淀知识；没有也要说明"无可同步长期知识"。
```
