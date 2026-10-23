# setup-skills 改造为 aiCoding 六件套

Type: prd
Status: ready-for-human

## Problem Statement

`/setup-skills` 目前向目标仓库部署四件套（issue-tracker / triage-labels / domain / git-contract），全部英文。它解决了 SDD skills 需要的配置，但缺 AI 编码工具进场所需的另一半：仓库是什么、怎么构建验证、什么不该进仓库、知识该沉淀到哪，以及可执行的开发规范与评审清单。结果是 `/review` 的 Standards 轴只能泛化地找 `CODING_STANDARDS.md`，多数仓库找不到，退化为"通用工程判断"。

参照 `ongoal-generator` 的 gittemplate（AGENTS.md + docs/agents/{developer,checklist,git-contract}.md）已验证这套骨架对 AI 编码有效。

## Solution

把 `/setup-skills` 从「四件套 + 仅 Agent Notice block」升级为「AGENTS.md 六节骨架 + docs/agents 六件套」，产物全部中文；`/review` 的 Standards 轴改为优先消费新增的 `developer.md` 与 `checklist.md`；本仓库自身按同一套改造回灌。

## Implementation Decisions

- **只借骨架不借条文**：gittemplate 的 001-016 端点表、080/081 字段后缀、禅道分支映射是 Java 业务工程专属，不移植；只取工程概述 / 语言 / 构建与验证 / 默认边界 / 最小记忆能力五节结构与 developer/checklist 两文件的形态。
- **文件名不可变**：`hooks/pre-commit-review.ps1` 以 `docs/agents/issue-tracker.md` 是否存在判定门禁生效；`idea-to-prd`、`review`、`sdd-flow`、`tdd` 均按路径引用。中文化只改内容。
- **机器可读 token 不翻译**：`Type:` / `Status:` 行名、五个 triage 标签字符串、所有路径。
- **Clarify, plan, then execute 取代 Grill first**：以 gittemplate 的三步块为主（澄清 → 计划 → 执行），澄清步内嵌 `/grill-me`（决策要沉淀为 ADR / 词汇表时用 `/grill-with-docs`）作为具体手段；简单且完整的需求可缩短澄清，但"给计划 + 等确认"保留。
- **developer.md 两段式**：通用编码与文档规范 + skill 资产规范；目标仓库无 skill 资产时删第二节（步骤 2 的第三个决策项）。
- **`/review` 接入新文件**：Standards 来源改为 developer.md → checklist.md → 其他约定文档，避免新文件成为死文件。
- **版本 1.4.0 → 1.5.0**：新增文件与行为变化属 minor。
- **Git Contract 以 gittemplate 为主**：种子模板改以 `gittemplate/docs/agents/git-contract.md` 为骨架——七类分支映射表（main / develop / feature / bugfix / release / hotfix）+ 提交信息必带关联 ID（禅道 task/bug/story，或 issue slug）+ 进/不进仓库清单；部署时按仓库实际裁剪分支表、替换关联对象载体。本仓库自身契约保持不变（solo + conventional-commits），因其不接禅道。
- **不做**：08-project-demo 模板未同步（另开一次）；不改 `.gitignore`；不引入 gittemplate 的 Java 端点编号 / 字段后缀条文。

## 变更清单

- `skills/dev/setup-skills/SKILL.md` — description、Explore、defaults（5→8 项）、草稿清单（4→6）、部署模板升级为完整 AGENTS.md 骨架、收尾话术
- `skills/dev/setup-skills/references/` — 四件套中文化（`git-contract.md` 改以 gittemplate 为骨架重写：分支映射表 + 关联 ID 提交模板）；新增 `developer.md`、`checklist.md`、`agents-md.md`（AGENTS.md 骨架，从 SKILL.md 内联抽出）
- `skills/dev/review/SKILL.md` — Standards 轴来源改为 developer.md / checklist.md
- `AGENTS.md` — 补工程概述 / 语言 / 构建与验证 / 默认边界 / 最小记忆能力，Agent Notice 增 Developer rules、Checklist
- `docs/agents/` — 四件套中文化；新增 `developer.md`、`checklist.md`
- 五份插件清单版本 1.5.0；`skills/dev/README.md`、`docs/codemap/project.md` 同步

## 验证

- 代码块围栏配对：`grep -c '^```' SKILL.md` = 2
- 骨架章节顺序：工程概述 → 语言 → 构建与验证 → Agent Notice（含 Developer rules / Checklist）→ 默认边界 → 最小记忆能力
- `docs/agents` 六文件齐全，无英文正文残留
- 引用一致性：hooks 与四个 skill 的 `docs/agents/*.md` 路径全部命中现有文件
- 五处清单版本号一致为 1.5.0

## Comments

- 2026-10-22 — 计划经用户确认（"ok"），按 Phase 1→4 执行完毕；实际验证为静态核对，未跑真实目标仓库的 `/setup-skills` 冒烟。
- 2026-10-23 — 用户裁决：Git Contract 以生成器仓库（gittemplate）的模板为主。落点限定为种子模板；本仓库 `docs/agents/git-contract.md` 保持 solo + conventional-commits。`SKILL.md` 的分支模型表述、三决策项、部署定稿说明与 Agent Notice 占位同步改写。
- 2026-10-23 — 用户裁决：Agent Notice 首节由 `Grill first` 改为 `Clarify, plan, then execute` 三步块，并结合既有 grill 内容（`/grill-me`、`/grill-with-docs`）。骨架模板与本仓库 `AGENTS.md` 同步；`docs/demos/08-project-demo/AGENTS.md` 仍保留旧标题，未同步。
- 2026-10-23 — 用户要求：AGENTS.md 内容模板从 `SKILL.md` 抽出为独立文件 `references/agents-md.md`（含合并规则与 81 行骨架），`SKILL.md` 三处改为链接引用，195→113 行、无内联代码块。
