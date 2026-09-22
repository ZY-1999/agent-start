# Triage Labels

SDD skills 以五个规范 triage 角色交流。本文件记录本仓库 issue 文件中实际使用的字符串。

| 角色 | 本仓库标签 | 含义 |
| --- | --- | --- |
| `needs-triage` | `needs-triage` | 维护者需要评估该 issue |
| `needs-info` | `needs-info` | 不完整 —— 等待补齐缺失信息/设计 |
| `ready-for-human` | `ready-for-human` | 已完成，等待人工 review |
| `ready-for-agent` | `ready-for-agent` | 已获人工批准；AFK agent 可执行 |
| `wontfix` | `wontfix` | 不予处理 |

**生命周期（单向）**：新创建且**完整**的文档为 `ready-for-human`（待 review）；**不完整**的为 `needs-info`（待补齐）。人工 review 通过后翻转为 `ready-for-agent`。**只有 `/sdd-flow` 设置 `ready-for-agent`** —— 在入口（PRD）和 Gate A（specs）时。`/tdd` 把已实现的 spec 翻回 `ready-for-human`（待代码 review）。`needs-design` 是 `/to-spec` 的内部状态（骨架已落盘、设计待做），不属于这些 triage 角色。

当 skill 提到某个角色（如 "apply the AFK-ready triage label"）时，使用右列对应的标签字符串。
