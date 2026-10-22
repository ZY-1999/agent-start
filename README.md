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
├── skills/               # 配套 SDD skills（fork + 扩展 + 第三方 vendored，多平台安装）
│   ├── general/          # 开发无关 skill（6 个）
│   ├── dev/              # 开发相关 skill（11 个）
│   ├── sdd/              # SDD 流水线 skill（3 个）
│   ├── qa/               # 测试侧 skill（5 个）
│   ├── third-party-zentao/    # 第三方 skill：禅道（2 个，vendored 自 easysoft/zentao-cli）
│   └── third-party-confluence/ # 第三方 skill：Confluence（1 个，vendored 自 pchuri/confluence-cli）
├── scripts/              # 多平台链接脚本（Claude CLI / OpenCode）
├── .claude-plugin/       # Claude Code plugin / marketplace 清单
├── .codebuddy-plugin/    # CodeBuddy 本地 marketplace
├── .cursor-plugin/       # Cursor 插件清单
└── .zcode-plugin/        # ZCode 插件清单（ZCode 优先探测；市场复用 .claude-plugin/marketplace.json）
```

## 讲义对应的内容框架

| 章节           | 主题                     | 核心信息                                                                                                                                                                                                                                                         |
| -------------- | ------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 一、是什么     | 定义、演进与定位         | 可控 / 可审查 / 可维护三属性；三阶段（补全 / 对话式 / Agent 与规格约束）；心智模型                                                                                                                                                                               |
| 二、能干什么   | 七个典型场景（核心篇幅） | 每个场景按"任务定义—演示—边界"展开，配可运行 Demo                                                                                                                                                                                                                |
| 三、不能干什么 | 能力边界与风险清单       | 幻觉 / 上下文 / 安全合规 / 责任归属 / 何时更慢                                                                                                                                                                                                                   |
| 四、怎么用好   | 五项实践                 | 实践一"给足上下文"（Prompt 写作 · grill 拷问式开局 · 规则文件 AGENTS.md · CodeMap 代码地图）；对话级三项：任务分解 / 代码审查 / 迭代修正；项目级一项：AI 自我验证（验收闭环）；每项标注对应解决 skills，末尾附一次任务的完整生命周期；配套 Demo 08 仓库模板 |

## 如何使用

1. **看讲义**：直接双击 `docs/index.html` 用浏览器打开，无需联网、无外部依赖。左侧目录导航，代码块可一键复制，支持打印（打印时自动隐藏导航）。
2. **排练 Demo**：阅读 [docs/demos/README.md](docs/demos/README.md)，每个 Demo 目录下有 `DEMO.md` 演示脚本（含推荐 Prompt、讲解词、失误预案）。
3. **现场分享**：建议按"讲义 40 分钟 + Live Demo 20 分钟"的节奏，Demo 07 作为收尾环节（现场对比最直观）。

## Demo 的组织方式

Demo 01–07 统一包含：

- `DEMO.md`：演示脚本——目标、传统 vs AI 耗时对比、演示步骤、推荐 Prompt、讲解要点、失误预案
- `legacy/` 或源码文件：演示输入（祖传代码、报错日志、待测代码等）
- `ai-output/`：AI 生成的参考结果（现场没网 / 模型答错时的保底材料）

Demo 08 例外：它是独立的**SDD 仓库模板**（`AGENTS.md` 索引 `docs/agents/` 下的 issue tracker、triage 标签、领域文档、git 契约与 Java / 数据库规范），作为讲义第四章实践一（项目规则 · 拷问 · CodeMap）与实践五（验收闭环）的参照，供听众会后直接取用到自己的项目，不设 `DEMO.md`、不占现场演示时长。

> 所有 Demo 代码均为演示用途的虚构样例，可自由替换为你们项目的真实代码片段（注意脱敏）。

## Skills：配套 SDD skills（多平台适配）

讲义第四章与 Demo 08 引用的 SDD skills 全量收录在 [`skills/`](./skills/)（fork 自 [mattpocock/skills](https://github.com/mattpocock/skills)，并做本地化扩展），按功能语义分为四个自研桶共 25 个；另有第三方桶收录外部 vendored skill（v1.1.0 起引入，v1.2.0 拆分为禅道 / Confluence 两桶）：

- **[`skills/general/`](./skills/general/README.md)**：6 个开发无关 skill——grill-me、grilling、teach、writing-great-skills（mattpocock）+ `/route`、`/handoff`（fork 扩展），对任何项目、任何话题都可用
- **[`skills/dev/`](./skills/dev/README.md)**：11 个开发相关 skill——grill-with-docs、prototype、domain-modeling、codebase-design（mattpocock）+ `/codemap`、`/setup-skills`、`/review`、`/idea-to-prd`、`/improve-architecture`、`/diagnose-bug`、`/to-prd`（fork 扩展），含 SDD 前半段入口、父项产出与基础设施
- **[`skills/sdd/`](./skills/sdd/README.md)**：3 个 SDD 执行核心 skill——`/to-spec`、`/tdd`、`/sdd-flow`，构成 spec → tdd → review → maintain 后半段闭环，详见 [skills/sdd/README.md](./skills/sdd/README.md)
- **[`skills/qa/`](./skills/qa/README.md)**：5 个测试侧 skill——`/qa-flow`（阶段编排）、`/qa-clarify`（需求澄清与疑问清单）、`/qa-cases`（四层用例：冒烟门槛 / 业务场景 / 功能点，含测试数据与校验 SQL 与执行清单；专项默认不出）、`/qa-regress`（修复验证与影响面回归）、`/qa-review`（验收结论 / 演示保障 / 质量复盘）（fork 扩展，v1.3.0 新增），补上 SDD 之外"需求 → 用例 → 回归 → 验收"这条测试链，按**纯手工测试团队**（无自动化、无 CI）设计。被测对象为**内部业务系统**（仅员工使用、无外部用户、当前无专项测试要求），因此性能/安全/兼容/联调不做默认交付，内部系统真正会出事的七类风险（跨组织可见性、按钮级权限、导出脱敏、万级列表、重复提交、并发审批、导出文件格式）归入 L2/L3 权限与列表场景。与 `dev/`、`sdd/` 打通而非并列：`/grill-me` 当场拷问需求、`/to-spec` 承接澄清后的断言、`/review` 提供改动清单供影响面推演、`/diagnose-bug` 接住根因不明的回归，并与 `zentao-cli`（拉 Bug、提 Bug）和 `confluence`（拉需求、发疑问清单）协作；`/tdd` 仅在将来上自动化时启用。跨桶地图与五对易混边界见 [pipeline-map.md](./skills/qa/qa-flow/references/pipeline-map.md)。
- **[`skills/third-party-zentao/`](./skills/third-party-zentao/README.md)**：2 个第三方 skill——`zentao-cli`、`zentao-tour`（[easysoft/zentao-cli](https://github.com/easysoft/zentao-cli) 官方出品，原样引入），通过 `zentao` 命令操作禅道；依赖全局安装 `npm install -g zentao-cli`，凭证推荐在 `~/.bashrc` 自行 export 环境变量（`ZENTAO_URL`/`ZENTAO_ACCOUNT`/`ZENTAO_TOKEN`|`ZENTAO_PASSWORD`），本机登录为备选
- **[`skills/third-party-confluence/`](./skills/third-party-confluence/README.md)**：1 个第三方 skill——`confluence`（[pchuri/confluence-cli](https://github.com/pchuri/confluence-cli)，原样引入），通过 `confluence` 命令读取、搜索、创建、更新、移动、删除 Confluence 页面与附件并做格式转换；依赖全局安装 `npm install -g confluence-cli`，凭证用本机配置 `~/.config/confluence-cli/config.json`（或 `confluence init`）

### 多平台安装

| 平台                    | 方式                                                                                                                                                             |
| ----------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Claude Code（plugin）   | `claude plugin marketplace add ZY-1999/agent-start` → `claude plugin install zy-skills@zy-skills`（配置见 [.claude-plugin/](./.claude-plugin/marketplace.json)） |
| Claude Code（本地链接） | `scripts/link-skills.sh` 把全部 skills 链接进 `~/.claude/skills`                                                                                                 |
| OpenCode                | `scripts/link-opencode-skills.sh` 按 `plugin.json` 注册清单链接到 `~/.config/opencode/skills`（Windows 走 junction，幂等可重跑）                                 |
| CodeBuddy               | 本地 marketplace：[.codebuddy-plugin/](./.codebuddy-plugin/marketplace.json)                                                                                     |
| Cursor                  | 插件清单：[.cursor-plugin/plugin.json](./.cursor-plugin/plugin.json)（六桶全部 28 个）                                                                            |
| ZCode                   | 插件市场添加仓库根目录（市场源为 [.claude-plugin/marketplace.json](./.claude-plugin/marketplace.json)，ZCode 兼容），安装 `zy-skills`；组件清单读 [.zcode-plugin/plugin.json](./.zcode-plugin/plugin.json)（ZCode 探测顺序：`.zcode-plugin` > `.claude-plugin` > `.codex-plugin`） |

### Hook：提交前 review 门禁（v1.0.2 新增）

插件自带一个 PreToolUse hook（[hooks/](./hooks/)）：在**跑过 `/setup-skills` 的仓库**里，agent 通过 Bash 执行 `git commit` 时会被拦截，要求先对未提交变更跑 `/review`；两个轴都无硬性违规时 `/review` 会把当前 diff 的哈希写入 `.git/zy-skills-review-approved`，随后重试同一提交即可放行（标记一次性消费，diff 一变即失效需重新 review）。

- 只约束 ZCode / Claude Code 会话内 agent 发起的提交；你在自己终端手动 `git commit` 不受影响，是天然的逃生通道。
- 未跑过 `/setup-skills` 的仓库（没有 `docs/agents/issue-tracker.md`）完全不生效，插件可安全全局安装。

完整的 fork 维护规则与源仓库见 [ZY-1999/skills](https://github.com/ZY-1999/skills)。
