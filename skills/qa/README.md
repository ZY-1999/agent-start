# QA：测试专家 skill

面向测试交付全流程的 skill，共 5 个。解决的是开发侧 SDD 覆盖不到的那一半：**需求模糊、没有产品经理、工期紧的条件下，怎么让"到底验什么、过了没有、能不能上"三个问题有答案**。

与 `dev/`、`sdd/` 的分工：SDD 管**代码怎么被正确写出来**（PRD → spec → TDD → review），QA 管**业务怎么被验证过**（需求澄清 → 用例 → 回归 → 验收）。这两条不是隔离并行——它们在同一条链路上按产出物分工，qa 在五处借通用 skill，接口见下面的「跨桶协作」。完整地图：[pipeline-map.md](./qa-flow/references/pipeline-map.md)。

## 模型调用（Model-invoked）

全部为模型可触发（触发短语见各 skill 的 description，关键词索引见 `metadata.keywords`）。

| Skill | 一句话 | 触发场景 |
| :--- | :--- | :--- |
| **[qa-flow](./qa-flow/SKILL.md)** | 四阶段编排，判断现在该做哪一步 | "这个模块要测了" / "测试怎么排" |
| **[qa-clarify](./qa-clarify/SKILL.md)** | 需求拆成可验证断言，出《需求疑问清单》 | "需求文档不全" / "业务就说了这么一句" |
| **[qa-cases](./qa-cases/SKILL.md)** | 用例四层：冒烟门槛 / 业务场景 / 功能点（含测试数据与校验 SQL）；专项默认不出 | "写用例" / "转测前要测什么" |
| **[qa-regress](./qa-regress/SKILL.md)** | 修复验证 + 影响面推断 + 回归清单 | "修好了帮我看下" / "影响范围多大" |
| **[qa-review](./qa-review/SKILL.md)** | 验收结论 + 演示保障 + 质量复盘 | "能不能上线" / "明天要演示" / "做下复盘" |

## 流水线

```
/qa-flow（判断阶段）
    │
    ├─ 一、澄清 ── /qa-clarify ──→ 疑问清单（断层五类 + [推断]/[待确认]）
    │                    │
    │                    └─ 无 [待确认] 的断言 ──┐
    │                                            ▼
    ├─ 二、用例 ── /qa-cases ──→ L1 冒烟（转测门槛）
    │                            L2 场景（业务流程端到端 + 数据 + SQL）← 主体
    │                            L3 功能（单点正反与边界）
    │                            L4 专项（性能 / 安全 / 兼容 / 联调）——默认不出
    │                            + 执行清单（批次 / 责任人 / 预估时长）
    │                            + 执行清单（批次 / 责任人 / 预估时长）
    │
    ├─ 三、回归 ── /qa-regress ──→ 直接验证 + 关联回归 + 结论（不通过不关单）
    │
    └─ 四、收口 ── /qa-review ──→ 验收结论 + 风险清单 + 演示保障 + 复盘
```

两个硬门：**澄清没过不进用例**（`[待确认]` 超过 1/3 时）、**冒烟没过不转测**。其余可并行。

## 跨桶协作

四阶段不是封闭环。有五处必须借通用 skill，别自己重造：

| 情形 | 借谁 | 带过去什么 |
| :--- | :--- | :--- |
| 需求方就在对话里，能当场补全半句话 | `/grill-me`；要留 ADR / 领域术语用 `/grill-with-docs` | 现有理解 + 待定点 |
| 需求要留档、跨迭代、多人接手 | `/to-prd` 或 `/idea-to-prd` → `/to-spec` | `/qa-clarify` 的断言 |
| 纯规则判定的用例将来落成单测 | `/tdd`（**当前不做**，团队纯手工） | 标 `[规则型]` 的用例 |
| 回归时原问题仍在、根因不明 | `/diagnose-bug` | 复现步骤 + 日志 |
| 划影响面要先知道改了哪些文件 | `/review` | 起点 commit / 分支 |

三条硬接口：

1. **断言 → spec**：`/qa-clarify` 的断言直接作为 `/to-spec` 的验收标准，不另写一套；措辞冲突以断言为准
2. **用例 → 单测（待启用）**：`[规则型]` 条目是将来接 `/tdd` 的候选；**当前团队纯手工测试，这条不启用**，业务链路用例也不要硬塞
3. **diff → 影响面**：`/review` 的改动清单是 `/qa-regress` 第 1 层推演的依据，比"就改了一点"可靠

五对容易混的边界（`grill-me` vs `qa-clarify`、`to-spec` vs `qa-cases`、`tdd` vs `qa-cases`、**`review` vs `qa-review`**、`diagnose-bug` vs `qa-regress`）见 [pipeline-map.md](./qa-flow/references/pipeline-map.md)。其中 `/review` 与 `/qa-review` 名字最像、职责最远：前者是代码门，后者是业务门。

### 与第三方桶

- **Confluence**：需求源（`/qa-clarify` 读页面 + 子页 + 附件）；疑问清单发布页（业务方在页面回帖，比发 Word 易追溯）；团队用 Wiki 管用例时作为归档处
- **禅道 story**：需求源之一，`/qa-clarify` 读需求单正文与变更记录
- **禅道 bug**：`/qa-regress` 拉详情与变更记录、回写验证结论、不通过另开单；`/qa-review` 查列表做统计
- **禅道用例**：**接不进**——`zentao-cli` 只覆盖 product/project/story/task/bug/todo，没有测试用例对象。用例留在 `docs/qa/cases/`

## 共享参考

各 skill 的 `references/` 按"谁主要用谁持有"分配，跨 skill 用兄弟目录相对路径引用（这是本仓库既有做法，装成 `~/.workbuddy/skills/` 扁平结构后同样可解析）：

| 文件 | 持有者 | 被谁引用 |
| :--- | :--- | :--- |
| `pipeline-map.md` — 跨桶全链路、三条接口、五对边界、外部系统落点 | qa-flow | qa-review、route |
| `project-layout.md` — `docs/qa/` 布局、术语表格式、换项目策略 | qa-flow | qa-cases、qa-cases/domain-chain |
| `question-list.md` — 疑问清单表 + 五类断层追问句式 | qa-clarify | — |
| `case-template.md` — 四层用例表、执行清单、合并规则、回归表、命名 | qa-cases | qa-regress |
| `special-checks.md` — 内部系统常备七条（归入 L2/L3）+ 专项扩展清单（按需取料） | qa-cases | — |
| `domain-chain.md` — 业务链路交接点、术语基线、六个跨模块检查点 | qa-cases | qa-clarify、qa-regress |
| `bug-report.md` — 结构化 Bug + 反例正例 + 提交流程 | qa-regress | — |
| `severity.md` — 严重等级、优先级、验收门槛 | qa-review | qa-regress |

## 前置

不强制前置，但强烈建议项目里有 `docs/qa/`（布局见 [`qa-flow/references/project-layout.md`](./qa-flow/references/project-layout.md)）。没有时 `/qa-flow` 会问一次资产位置并记进项目 `AGENTS.md`，之后不再重复问。
