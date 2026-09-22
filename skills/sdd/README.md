# SDD：Spec-Driven Development 流水线 skill

SDD 执行核心 skill，共 3 个（`/to-spec`、`/tdd`、`/sdd-flow`），构成 **spec → tdd → review → maintain** 后半段闭环。前半段与父项产出（`/idea-to-prd`、`/to-prd`、`/improve-architecture`、`/diagnose-bug`）及基础设施（`/setup-skills`、`/review`、codebase-design 等）都在 [dev/](../dev/README.md)，访谈核心（grilling）在 [general/](../general/README.md)，入口索引见 `/route`。

## 流水线总览

```
/setup-skills（一次性仓库配置，见 dev/）
      │
/idea-to-prd ── grill → PRD → 人审 Gate 0 → 停在批准的 PRD（见 dev/，不碰 git）
      │  （是否进入 SDD 是用户经 /route 的独立决策）
/sdd-flow ── /to-spec 拆解 → Gate A → /tdd 逐 spec 实现 → /review 复核（见 dev/）→ 维护文档 → /handoff
                （后半段，从已批准 PRD 到交付）
```

旁路入口（均见 dev/）：`/improve-architecture`（架构扫描 → PRD 草稿）、`/diagnose-bug`（根因诊断，诊断即交付；转修复走 `/to-prd` → `/sdd-flow`）、`/to-prd`（已 grilled 对话 → 父项）、`/to-spec` + `/tdd`（手动驱动单个环节）。

## 模型调用（Model-invoked）

- **[sdd-flow](./sdd-flow/SKILL.md)** — 后半段：从已批准 PRD 到交付（spec → build → review → maintain）。入口把 PRD 翻转为 `ready-for-agent`；然后 `/to-spec` 拆解 → Gate A → `/tdd` 逐 spec 实现 → `/review` 复核 → 维护文档，收尾调 `/handoff`。
- **[to-spec](./to-spec/SKILL.md)** — 把 PRD、bug 或已 grilled 对话拆成 `spec`——原子的、规划完备的单元，agent 在一次 `/tdd` 会话里实现。拆分走 best-of-N + judge；复杂设计用 `/codebase-design` 的 DESIGN-IT-TWICE。
- **[tdd](./tdd/SKILL.md)** — 用测试先行（red-green-refactor）实现一个规划完备的 spec。纯实现：spec 已带接口与按优先级排列的行为，直接从 tracer bullet 开始。

## Issue 类型

每个被追踪的工作项都是一个 **issue**，承载且仅承载一种类型：

- **spec** — agent 编码的原子单元，`/tdd` 一次会话实现的最小单位（叶子），由 `/to-spec` 产出。
- **prd** — 产品需求文档（父项），`/to-spec` 把它拆成一个或多个 `spec`。
- **bug** — bug 报告（父项），修复需要多步时由 `/to-spec` 拆成 `spec`。

只有 `spec` 会被实现（通过 `/tdd`）；`prd`/`bug` 是规划/分诊产物。issues 以本地 markdown 存于 `.scratch/<YYYY-MM-DD>-<feature-slug>/`，见 [issue-tracker-local.md](../dev/setup-skills/references/issue-tracker-local.md)。

## 前置条件

- 首次使用前先跑 `/setup-skills`（见 dev/；若 `docs/agents/issue-tracker.md` 缺失，各 SDD skill 会停下要求先配置）。
- 与 dev/、general/ 基础 skill 互通是默认前提：用名字引用（`/grilling`、`/codebase-design`…），不假设用户装了哪个子集。
