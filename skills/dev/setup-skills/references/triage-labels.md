# Triage 标签

SDD skills 用规范 triage 角色说话。本文件记录本仓库 issue 文件里实际使用的标签字符串。

| 角色              | 本仓库标签         | 含义                                                       |
| ----------------- | ------------------ | ---------------------------------------------------------- |
| `needs-triage`    | `needs-triage`     | 待维护者评估                                               |
| `needs-info`      | `needs-info`       | 不完整——等待补齐缺失的信息/设计                           |
| `ready-for-human` | `ready-for-human`  | 已完整，等待人工评审                                       |
| `ready-for-agent` | `ready-for-agent`  | 已人工批准，可交由 agent 执行                              |
| `implemented`     | `implemented`      | 已构建、附证据——不再逐 spec 等人确认；阶段门与 `/spec-check` 兜底 |
| `closed`          | `closed`           | 已在阶段门验收（或判定 wontfix）；终态                     |
| `wontfix`         | `wontfix`          | 不予处理                                                   |

**生命周期（单向）：** 新建且**完整**的文档为 `ready-for-human`（待评审）；**不完整**的为 `needs-info`（待补）。人工评审通过后翻转为 `ready-for-agent`（此时 agent 可执行）。已构建的 spec 进入 `implemented`（**不回到** `ready-for-human`——那个标签的意思是"待评审"，已构建状态由 `/review` 与 `/spec-check` 兜底）。阶段门最终验收翻转为 `closed`。

当某个 skill 提到某个角色（例如"打上可执行的 triage 标签"）时，取右列对应的标签字符串。

spec 有一个专属状态：`/to-spec` 落盘 spec 骨架时标 `needs-design`（设计尚未填充）——这是介于 `needs-info` 与 `ready-for-human` 之间的 spec 专用状态，不是全局 triage 角色。

右列请按你实际使用的词汇修改。
