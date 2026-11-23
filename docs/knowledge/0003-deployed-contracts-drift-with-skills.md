---
date: 2026-11-22
source: session 2026-11-22 为本仓部署 knowledge 契约的同步实践
scope: 本仓——它既是 zy-skills 的 fork 源码仓，又是 /setup-skills 的部署样例
---

# 部署产物随 skill 演进需手动同步

本仓根 `AGENTS.md` 的 `## Agent Notice` 区块和 `docs/agents/*.md` 是 `/setup-skills` 的**部署产物**；skill 源码演进后它们不会自动更新，必须手动同步，否则样例与源码漂移。

**Why:** setup 是一次性脚手架，部署物归仓库所有；源码里的 seed 模板（`skills/dev/setup-skills/references/`）与已部署文件之间没有任何自动机制。

**Boundary:** 只适用于"源码仓兼部署样例"的本仓；普通仓库重跑 `/setup-skills` 即可重建。

**Evidence:** 2026-11-22 给 setup-skills 增加第 6 项 knowledge 契约时，同步更新了本仓 AGENTS.md 与 `docs/agents/knowledge.md`；此前 1.1.0 重构（commit f47022b）也同样动过部署物。

**Action:** 每次改 `skills/dev/setup-skills/` 下的模板或 Agent Notice 区块定义，对照更新本仓 `AGENTS.md` 与 `docs/agents/` 对应文件。
