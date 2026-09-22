# General：开发无关 skill

与写代码本身解耦的通用 skill：访谈、教学、会话交接与 skill 路由——对任何项目、任何话题都可用。共 6 个：4 个来自 [mattpocock/skills](https://github.com/mattpocock/skills)，2 个为 fork 扩展。

## 用户调用（User-invoked）

仅显式调用时可达（`disable-model-invocation: true`）。

来自 mattpocock：

- **[grill-me](./grill-me/SKILL.md)** — 对一个计划或设计做穷追式访谈，直到决策树每个分支都有结论。
- **[teach](./teach/SKILL.md)** — 以当前目录为有状态教学工作区，跨多次会话教用户一个概念。
- **[writing-great-skills](./writing-great-skills/SKILL.md)** — 写好 / 改好一个 skill 的参考：让 skill 可预测的词汇与原则。

fork 扩展：

- **[route](./route/SKILL.md)** — 意图 → skill 路由器：说出你想做什么，拿到处理它的那一个 skill（或一条短链）。覆盖本仓库 general/ + dev/ + sdd/ 全部 skills，并承接各 skill 之间的接力决策（批准的 PRD → `/sdd-flow`；诊断结果 → `/to-prd` → `/sdd-flow`）。

## 模型调用（Model-invoked）

模型或用户均可触发（触发短语丰富，模型可自行选用）。

来自 mattpocock：

- **[grilling](./grilling/SKILL.md)** — 对计划或设计做穷追式访谈，一次一问，每问附推荐答案。话题不限（产品方案、技术设计、写作大纲皆可）；`/idea-to-prd`、`/grill-with-docs` 复用其访谈核心。

fork 扩展：

- **[handoff](./handoff/SKILL.md)** — 把当前对话压缩成 OS 临时目录的 handoff 文件，并输出一条可直接粘贴的下个会话启动 prompt。
