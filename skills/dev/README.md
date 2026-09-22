# Dev：开发相关 skill

面向代码库与开发流程的 skill：设计/领域建模、代码地形、SDD 前半段入口与父项产出、双轴复核、仓库配置。共 11 个：4 个来自 [mattpocock/skills](https://github.com/mattpocock/skills)，7 个为 fork 扩展。可独立使用，也可被 `sdd/` 流水线引用（如 `/to-spec` 用 `/codebase-design` 词汇、`/sdd-flow` 调 `/review` 复核）。

## 用户调用（User-invoked）

仅显式调用时可达（`disable-model-invocation: true`）。

来自 mattpocock：

- **[grill-with-docs](./grill-with-docs/SKILL.md)** — 边 grill 边构建项目领域模型的拷问式开局，同步更新 `CONTEXT.md` 与 ADR。
- **[prototype](./prototype/SKILL.md)** — 构建一次性原型回答一个可运行的问题：状态/逻辑题出终端 app，UI 题出多方案可切换变体。

fork 扩展：

- **[setup-skills](./setup-skills/SKILL.md)** — 为 SDD skills 配置仓库：本地 markdown issue 追踪器、triage 标签词汇、domain 文档布局、初始项目级 CodeMap。在其他 SDD skills 之前运行一次。
- **[improve-architecture](./improve-architecture/SKILL.md)** — 架构发现入口：扫描代码库寻找深化机会，发布可视化 HTML 报告，并把每个候选（含 Top recommendation）摘录为 `needs-info` 的轻量 `prd` 草稿。只做发现不做设计，草稿留给后续 grill 完善。

## 模型调用（Model-invoked）

模型或用户均可触发（触发短语丰富，模型可自行选用）。

来自 mattpocock：

- **[domain-modeling](./domain-modeling/SKILL.md)** — 主动构建和锐化项目领域模型：挑战术语、用边界场景压测、落盘 glossary 与 ADR。
- **[codebase-design](./codebase-design/SKILL.md)** — 设计深模块的共享纪律与词汇：小接口、干净 seam、可通过接口测试。

fork 扩展：

- **[codemap](./codemap/SKILL.md)** — 生成、更新或 drift-check agent 可读的 CodeMaps：渐进式代码地形索引，把 agent 引到带源码链接的证据。喂给 `/to-spec` 的 "reduce chaos" 步骤，以及任何需要先看地形的 skill。
- **[review](./review/SKILL.md)** — 自固定点（commit、branch、tag 或 merge-base）对 diff 做双轴复核：Standards（编码规范）与 Spec（是否忠实实现原始 issue/PRD）。两轴各跑独立 fresh-context 子代理，并排报告，绝不跨轴合并。`/sdd-flow` 在 review 阶段调用它，也可直接复核分支/PR/WIP 改动。
- **[idea-to-prd](./idea-to-prd/SKILL.md)** — SDD 前半段：grill 对齐设计（`/grilling` + `/domain-modeling` 核心）→ `/to-prd` 综合 PRD → 人审 Gate 0，**停在批准的 PRD 落盘**。只拥有 grill + PRD + Gate 0，不碰 git，也不自动接力——是否继续进入 SDD 由用户经 `/route` 决策。也可补全已有 `needs-info` PRD 草稿的缺口。
- **[to-prd](./to-prd/SKILL.md)** — 把当前对话综合成父项 issue（feature `prd` / bug `bug` / 架构深化 `prd`）并发布到 issue 追踪器。不做访谈，只做综合；发布前由 fresh-context sub-agent 对抗式审查（先真实性、后可行性）。产出供 `/to-spec` 拆解的父项。
- **[diagnose-bug](./diagnose-bug/SKILL.md)** — 顽固 bug 与性能回归的纪律化诊断循环：反馈循环 → 复现最小化 → 假设 → 插桩锁定根因。**诊断本身即交付物**（根因 + 证据 + 最小复现 + 修复方向），不修复、不自动交接；要把诊断转成被追踪的修复，经 `/route` 走 `/to-prd` → `/sdd-flow`。
