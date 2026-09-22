# agent-start：面向传统程序员的 AI Coding 分享

一套完整的分享材料：**一份 HTML 讲义 + 7 个可现场运行的 Live Demo + 1 个 SDD 仓库模板**。

> 讲义标题：《什么是 AI Coding？从代码补全到 Agent 开发：能力边界、典型场景与工程实践》

## 内容结构

```
agent-start/
├── README.md             # 本文件
├── docs/                 # 讲义与演示材料
│   ├── index.html        # 主讲义（双击打开，离线可用）
│   └── demos/            # 配套 Live Demo
│       ├── README.md     # Demo 总览 + 排练指南 + 时间分配
│       ├── 01-scaffold/  # 脚手架代码：CRUD / DTO / 单元测试
│       ├── 02-legacy/    # 遗留代码理解：无注释祖传代码
│       ├── 03-refactor/  # 重构与迁移：JDK 8→17 + 慢 SQL 优化
│       ├── 04-debug/     # 排障：报错日志分析
│       ├── 05-testing/   # 写测试：边界用例生成
│       ├── 06-docs/      # 文档：接口文档 / README / Commit Message
│       ├── 07-regex-shell/ # 正则 / Shell / Sed：语法查询类任务（建议收尾）
│       └── 08-project-demo/ # SDD 仓库模板（AGENTS.md + docs/agents/）
├── skills/               # 配套 SDD skills（fork + 扩展，多平台安装）
│   ├── general/          # 开发无关 skill（6 个）
│   ├── dev/              # 开发相关 skill（11 个）
│   └── sdd/              # SDD 流水线 skill（3 个）
├── scripts/              # 多平台链接脚本（Claude CLI / OpenCode）
├── .claude-plugin/       # Claude Code plugin / marketplace 清单
├── .codebuddy-plugin/    # CodeBuddy 本地 marketplace
└── .cursor-plugin/       # Cursor 插件清单
```

## 讲义对应的内容框架

| 章节           | 主题                     | 核心信息                                                                                                                                                                                                                                                         |
| -------------- | ------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 一、是什么     | 定义、演进与定位         | 可控 / 可审查 / 可维护三属性；三阶段（补全 / 对话式 / Agent 与规格约束）；心智模型                                                                                                                                                                               |
| 二、能干什么   | 七个典型场景（核心篇幅） | 每个场景按"任务定义—演示—边界"展开，配可运行 Demo                                                                                                                                                                                                                |
| 三、不能干什么 | 能力边界与风险清单       | 幻觉 / 上下文 / 安全合规 / 责任归属 / 何时更慢                                                                                                                                                                                                                   |
| 四、怎么用好   | 七项实践                 | 实践一"给足上下文"：对话级 Prompt 写作 + 项目级规则文件（AGENTS.md）；对话级三项：任务分解 / 代码审查 / 迭代修正；项目级三项：CodeMap 指路 / grill-with-docs 拷问式开局 / AI 自我验证（验收闭环），末尾附一次任务的完整生命周期；项目级实践配套 Demo 08 仓库模板 |

## 如何使用

1. **看讲义**：直接双击 `docs/index.html` 用浏览器打开，无需联网、无外部依赖。左侧目录导航，代码块可一键复制，支持打印（打印时自动隐藏导航）。
2. **排练 Demo**：阅读 [docs/demos/README.md](docs/demos/README.md)，每个 Demo 目录下有 `DEMO.md` 演示脚本（含推荐 Prompt、讲解词、失误预案）。
3. **现场分享**：建议按"讲义 40 分钟 + Live Demo 20 分钟"的节奏，Demo 07 作为收尾环节（现场对比最直观）。

## Demo 的组织方式

Demo 01–07 统一包含：

- `DEMO.md`：演示脚本——目标、传统 vs AI 耗时对比、演示步骤、推荐 Prompt、讲解要点、失误预案
- `legacy/` 或源码文件：演示输入（祖传代码、报错日志、待测代码等）
- `ai-output/`：AI 生成的参考结果（现场没网 / 模型答错时的保底材料）

Demo 08 例外：它是独立的**SDD 仓库模板**（`AGENTS.md` 索引 `docs/agents/` 下的 issue tracker、triage 标签、领域文档、git 契约与 Java / 数据库规范），作为讲义第四章项目级实践（实践一的项目规则、实践五与实践六）的参照，供听众会后直接取用到自己的项目，不设 `DEMO.md`、不占现场演示时长。

> 所有 Demo 代码均为演示用途的虚构样例，可自由替换为你们项目的真实代码片段（注意脱敏）。

## Skills：配套 SDD skills（多平台适配）

讲义第四章与 Demo 08 引用的 SDD skills 全量收录在 [`skills/`](./skills/)（fork 自 [mattpocock/skills](https://github.com/mattpocock/skills)，并做本地化扩展），按功能语义分为两个桶，共 20 个：

- **[`skills/general/`](./skills/general/README.md)**：6 个开发无关 skill——grill-me、grilling、teach、writing-great-skills（mattpocock）+ `/route`、`/handoff`（fork 扩展），对任何项目、任何话题都可用
- **[`skills/dev/`](./skills/dev/README.md)**：11 个开发相关 skill——grill-with-docs、prototype、domain-modeling、codebase-design（mattpocock）+ `/codemap`、`/setup-skills`、`/review`、`/idea-to-prd`、`/improve-architecture`、`/diagnose-bug`、`/to-prd`（fork 扩展），含 SDD 前半段入口、父项产出与基础设施
- **[`skills/sdd/`](./skills/sdd/README.md)**：3 个 SDD 执行核心 skill——`/to-spec`、`/tdd`、`/sdd-flow`，构成 spec → tdd → review → maintain 后半段闭环，详见 [skills/sdd/README.md](./skills/sdd/README.md)

### 多平台安装

| 平台                    | 方式                                                                                                                                                             |
| ----------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Claude Code（plugin）   | `claude plugin marketplace add ZY-1999/agent-start` → `claude plugin install zy-skills@zy-skills`（配置见 [.claude-plugin/](./.claude-plugin/marketplace.json)） |
| Claude Code（本地链接） | `scripts/link-skills.sh` 把全部 skills 链接进 `~/.claude/skills`                                                                                                 |
| OpenCode                | `scripts/link-opencode-skills.sh` 按 `plugin.json` 注册清单链接到 `~/.config/opencode/skills`（Windows 走 junction，幂等可重跑）                                 |
| CodeBuddy               | 本地 marketplace：[.codebuddy-plugin/](./.codebuddy-plugin/marketplace.json)                                                                                     |
| Cursor                  | 插件清单：[.cursor-plugin/plugin.json](./.cursor-plugin/plugin.json)（含 base/ + sdd/ 全部 20 个）                                                                |

完整的 fork 维护规则与源仓库见 [ZY-1999/skills](https://github.com/ZY-1999/skills)。
