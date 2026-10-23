# agent-start

本仓库的 agent 入口规则；人类读者的大门是 [README.md](README.md)。

## 工程概述

- 仓库名称：agent-start；形态：skill 资产 + 讲义 + 演示样例的单仓库，同时也是可安装的插件包。
- 目录与职责：
  - `skills/` —— 六桶 skill 资产（general / dev / sdd / qa / third-party-zentao / third-party-confluence）
  - `docs/` —— 讲义 `index.html`、`demos/` 现场演示样例、`codemap/` 代码地形、`agents/` 本套配置
  - `scripts/` —— 多平台链接脚本（Claude Code / OpenCode）
  - `hooks/` —— 提交前 review 门禁（PreToolUse）
  - `.claude-plugin/` `.codebuddy-plugin/` `.cursor-plugin/` `.zcode-plugin/` —— 各平台插件与市场清单
- 技术栈：Markdown skill 资产（无运行时依赖）+ 零外部依赖的 HTML 讲义 + Bash / PowerShell 脚本；Node 仅用于链接脚本。

## 语言

- 请使用简体中文与我交流，避免机翻，有特定含义的词语可以不翻译；
- 范围包括聊天对话，spec / prd / bug 等 issue，git 提交 message，代码注释；
- 例外：`SKILL.md` 的指令正文沿用英文（与既有 skill 桶一致），其中部署到目标仓库的模板与 `docs/agents/` 产物一律中文。

## 构建与验证

- 环境要求：无构建步骤；Node 22+（跑 `scripts/`）、git。
- 本仓库无自动化测试，改动验证 = 手工检查 + 实际安装一次。
- skill 改动：核对 frontmatter `name` 与目录名一致、`description` 写清触发场景；同步四份插件清单与版本号（漏一处，该平台拿到的就是旧内容）。
- 讲义改动：浏览器打开 `docs/index.html` 核对渲染与左侧目录导航。
- 插件改动：按 README 的多平台安装表实际安装一次，确认 skill 可见。
- 改了 skill 结构或清单：同步 `docs/codemap/project.md`，否则下次探索拿到过期索引。

## Agent Notice

### Clarify, plan, then execute

接到需求先澄清、再计划、最后执行，三步不跳：

1. **澄清** —— 用 `/grill-me` 拷问需求，直到目标、边界、验收标准、影响面都明确；当决策需要沉淀为 ADR / 词汇表时改用 `/grill-with-docs`。上下文不全就直接提问，不要猜。
2. **计划** —— 给出分阶段计划：改哪些文件、按什么顺序、怎么验证。等用户确认。
3. **执行** —— 确认后才动手；执行中计划要变，先说明再改。

需求本身简单且完整时，澄清可以很短，但"给计划 + 等确认"这两步保留。

### Issue tracker

Local markdown under `.scratch/` at the repo root — one directory per feature, specs in `specs/`. See `docs/agents/issue-tracker.md`.

### Triage labels

`needs-triage` / `needs-info` / `ready-for-agent` / `ready-for-human` / `wontfix`. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: `CONTEXT.md` + `docs/adr/` at the repo root. See `docs/agents/domain.md`.

### Developer rules

开发规范，适用于人类与 AI：通用编码与文档规范 + skill 资产规范。见 `docs/agents/developer.md`。

### Checklist

代码评审清单，`/review` 的 Standards 轴以它为覆盖地图，也是完成前的自检表。见 `docs/agents/checklist.md`。

### CodeMap

Start here when exploring the codebase: a project-level terrain index under `docs/codemap/`; refresh or add feature maps via `/codemap`; when unsure whether a map is still trustworthy, run `/codemap` drift-check — if it reports drift, update the affected map.

### Git contract

Solo on the current branch; commit messages use conventional-commits prefixes (`docs:` / `feat:` / `fix:` …) with Chinese subjects allowed. See `docs/agents/git-contract.md`.

### QA skills

`skills/qa/` holds the test-side pipeline: `qa-flow` (stage router), `qa-clarify` (requirement → verifiable assertions + question list), `qa-cases` (four layers — smoke gate, end-to-end scenarios, single-feature checks, and specialty checks which are **off by default**; test data & verification SQL included), `qa-regress` (fix verification + blast radius), `qa-review` (release verdict + demo readiness + retro). Route any test work there. QA assets live in the target project's `docs/qa/` (layout in `skills/qa/qa-flow/references/project-layout.md`); if absent, ask once and record the path in that project's `AGENTS.md`. Zentao work routes to `zentao-cli`, Confluence work to `confluence`.

**Testing is manual-only here** — no automation, no CI test runs. Every case must be executable by hand: preconditions reproducible (steps or seed SQL), verification SQL non-optional, ≤ 12 steps per case, per-case duration estimate on L2/L3, and an execution checklist with batches, owners and totals. `/tdd` is not part of this pipeline today; `[规则型]` tags only mark cases that would be easiest to automate later.

**All systems under test are internal business systems** — employees only, no external users, and there is currently no specialty-testing requirement. L4 (performance / security / compatibility / integration) is therefore **not produced by default**; say "no specialty testing this round" in the hand-off instead of building an empty section. The seven risks that still bite internal systems (cross-org data visibility, button-level permission bypass, sensitive fields in exports, list performance at ~10k rows, double submit, concurrent approval of the same document, exported files opening in WPS/Office) are functional correctness — they belong to L2/L3 permission and list scenarios, not to L4. Only build L4 when the user explicitly asks (customer test report, pre-release security review, going external) or the requirement clearly signals it.

The QA pipeline is **not** isolated from the rest of this repo. It borrows four general skills: `/grill-me` (or `/grill-with-docs`) when the requirement owner is present, `/to-spec` (assertions become spec acceptance criteria), `/review` (diff informs the regression blast radius), `/diagnose-bug` (root cause unknown). `/tdd` stays out of this pipeline until the team adopts automation. Full cross-bucket map, the three hand-off interfaces, and the five confusable pairs are in `skills/qa/qa-flow/references/pipeline-map.md`. In particular `/review` (code-diff gate) and `/qa-review` (release business gate) are different gates — both must pass before shipping.

### Third-party skills (zentao / confluence)

The plugin ships vendored third-party skills in two buckets. `skills/third-party-zentao/` holds `zentao-cli` and `zentao-tour` (vendored from easysoft/zentao-cli) for operating Zentao (禅道) data: products, projects, stories, tasks, bugs, personal todos — route Zentao work to them; prerequisites: the `zentao` CLI (`npm install -g zentao-cli`) plus credentials — **environment variables recommended** (`ZENTAO_URL` / `ZENTAO_ACCOUNT` / `ZENTAO_TOKEN`|`ZENTAO_PASSWORD` exported in your own `~/.bashrc`; they outrank local profiles and password mode avoids token expiry), local-profile login (`zentao login --web`) as fallback. `skills/third-party-confluence/` holds `confluence` (vendored from pchuri/confluence-cli) for reading, searching, creating, updating, moving, deleting and converting Confluence pages and attachments — route Confluence work to it; prerequisites: the `confluence` CLI (`npm install -g confluence-cli`) with a profile in `~/.config/confluence-cli/config.json` (or `confluence init`).

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
