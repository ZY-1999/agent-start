# SDD：Spec-Driven Development 流水线 skill

SDD 执行核心 skill，共 5 个（`/to-spec`、`/tdd`、`/sdd-flow`、`/spec-sync`、`/spec-check`），构成 **align → spec → tdd → review → maintain** 后半段闭环。前半段与父项产出（`/idea-to-prd`、`/to-prd`、`/improve-architecture`、`/diagnose-bug`）及基础设施（`/setup-skills`、`/review`、codebase-design 等）都在 [dev/](../dev/README.md)，访谈核心（grilling）在 [general/](../general/README.md)，入口索引见 `/route`。

## 流水线总览

```
/setup-skills（一次性仓库配置，见 dev/）
      │
/idea-to-prd ── grill → PRD（纯需求：AC 编号 + P0/P1）→ 人审 Gate 0 卡 → 停在批准的 PRD（见 dev/，不碰 git）
      │  （是否进入 SDD 是用户经 /route 的独立决策）
/sdd-flow ── /to-spec 双锚对齐（contracts.md + trace.md）+ 拆解 → Gate A 对齐卡（抽查一个最高风险 spec）
      │       → /tdd 逐 spec 场景驱动实现 → /review + 追溯门禁（全 P0 场景绿）→ 维护文档收尾
      │
      ├─ 需求变更 → /spec-sync 联动下游（AC/契约/场景/spec/测试/trace）
      └─ 质检看板 → /spec-check 四维审计（完整性/一致性/追溯性/分层纯度）
```

文档四层（feature 文件夹内）：`PRD（纯需求）→ contracts.md（技术契约真源，C-NN）→ specs/（执行切片，AC 投影）→ trace.md（追溯矩阵：AC→场景→契约→spec→test→commit）`。人只在两道门各看一张一屏卡片，`/tdd` 收尾免逐条确认。

旁路入口（均见 dev/）：`/improve-architecture`（架构扫描 → PRD 草稿）、`/diagnose-bug`（根因诊断，诊断即交付；转修复走 `/to-prd` → `/sdd-flow`）、`/to-prd`（已 grilled 对话 → 父项）、`/to-spec` + `/tdd`（手动驱动单个环节；简单且完整的需求可跳过 grill/PRD 直入 `/to-spec`，见下）。

## 模型调用（Model-invoked）

- **[sdd-flow](./sdd-flow/SKILL.md)** — 后半段总控：从已批准 PRD 到交付。入口把 PRD 翻转为 `ready-for-agent`；然后 `/to-spec` 对齐拆解（contracts + trace + specs）→ Gate A 对齐卡 → `/tdd` 逐 spec 场景驱动实现 → `/review` + 追溯门禁 → 维护文档收尾（implemented → closed）。
- **[to-spec](./to-spec/SKILL.md)** — 把 PRD、bug、已 grilled 对话或简单且完整的需求**翻译并拆解**成 `spec`——原子的、规划完备的单元，agent 在一次 `/tdd` 会话里实现。先双锚对齐（AC ↔ 契约点 C-NN，数字互校）并播种 trace.md 场景行（S-NN），再 best-of-N 拆分；复杂设计用 `/codebase-design` 的 DESIGN-IT-TWICE。简单完整需求经入口完整性门（completeness-bar）校验，不过门退回 `/grill-me` / `/to-prd`。
- **[tdd](./tdd/SKILL.md)** — **场景驱动**实现一个规划完备的 spec：从 trace.md 取本 spec 的 P0 场景，逐个「物化测试 → 跑红 → tracer bullet 至绿 → 重构」；收尾翻 `implemented` + 回填 trace，免逐条人确认。
- **[spec-sync](./spec-sync/SKILL.md)** — 变更联动：需求或契约改动时，以 contracts.md 为中心列出全部下游（AC/场景/spec/测试/trace 行），确认后一并更新并重跑双锚互校。
- **[spec-check](./spec-check/SKILL.md)** — 四维质检 + 状态看板：完整性/一致性/追溯性/分层纯度，P0/P1 分级报告；fresh-context 子代理只读审计。

## Issue 类型

每个被追踪的工作项都是一个 **issue**，承载且仅承载一种类型：

- **spec** — agent 编码的原子单元，`/tdd` 一次会话实现的最小单位（叶子），由 `/to-spec` 产出。
- **prd** — 产品需求文档（父项，纯需求层），`/to-spec` 把它翻译成 `contracts.md` + `trace.md` 并拆成一个或多个 `spec`。
- **bug** — bug 报告（父项），修复需要多步时由 `/to-spec` 拆成 `spec`。

只有 `spec` 会被实现（通过 `/tdd`）；`prd`/`bug` 是规划/分诊产物。issues 以本地 markdown 存于 `.scratch/<YYYY-MM-DD>-<feature-slug>/`，feature 级还带 `contracts.md` 与 `trace.md`（见 [issue-tracker-local.md](../dev/setup-skills/references/issue-tracker-local.md)）。

## 前置条件

- 首次使用前先跑 `/setup-skills`（见 dev/；若 `docs/agents/issue-tracker.md` 缺失，各 SDD skill 会停下要求先配置）。
- 与 dev/、general/ 基础 skill 互通是默认前提：用名字引用（`/grilling`、`/codebase-design`…），不假设用户装了哪个子集。
