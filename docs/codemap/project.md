# agent-start CodeMap (project)

## 1. Orientation

- Last updated: `2026-10-13`
- Project: agent-start（内部 git 名 `ongoal-skills`）
- Role / responsibility: AI Coding 技术分享材料仓库——一份离线 HTML 讲义 + 8 个 Demo + 20 个 skills；同时是多平台插件的**发布源仓库**（`skills/` 的改动会随 zy-skills 插件分发出去）
- Main languages / frameworks: Markdown、单文件 HTML（内嵌 CSS/JS，零外部依赖）、Bash
- Runtime / deployment shape: 无运行时。讲义浏览器直接打开；skills 经插件清单/链接脚本安装到各客户端
- Primary entry types: 人类入口 `README.md` / `docs/index.html`；agent 入口 `AGENTS.md`；安装入口 `scripts/`
- Confidence:
  - confirmed: 目录结构、插件清单、Demo↔章节映射（README + index.html 正文核对）
  - inferred: 无（结构简单，全部直接核实）
  - unknown: 上游 fork 同步流程的具体操作（README 指向 ZY-1999/skills，未验证细节）

## 2. Context Tree

```text
Node: agent-start
  -> Node: Capability Index
  -> Node: Module Index
  -> Node: Entry Index
  -> Node: Domain And Data
  -> Node: External Dependencies
  -> Node: Cross-Module Flows
  -> Node: Validation
  -> Node: Risk Areas
  -> Node: Feature CodeMap Backlog
```

### Node: agent-start

- Type: `project`
- Status: `confirmed`
- Purpose: 分享材料 + skills 发布源，双职责
- Read First:
  - `README.md`: 仓库大门，含目录树、章节框架、安装矩阵
  - `AGENTS.md`: agent 进场规则（Grill first / issue tracker / 契约索引）
- Edges / Children: 见上下各节点
- Unknowns: 上游同步流程细节
- Next Drill-Down: 改 skills 前读对应桶的 `skills/<桶>/README.md`

### Node: Capability Index

- Type: `capability`
- Status: `confirmed`
- Children:
  - 讲义（五章 + 附录）:
    - Main modules: `docs/index.html`
    - Entry: 浏览器打开；锚点 `#ch1`–`#ch5` / `#s<N>-<M>` / `#appendix`
    - Feature CodeMap: pending
    - Status: `confirmed`
  - Live Demo（7 个现场 + 1 个模板）:
    - Main modules: `docs/demos/01..08-*/`
    - Entry: 各目录 `DEMO.md`（08 例外，入口是 `AGENTS.md`）
    - Feature CodeMap: pending
    - Status: `confirmed`
  - SDD skills 集合（20 个）:
    - Main modules: `skills/general/`(6) `skills/dev/`(11) `skills/sdd/`(3)
    - Entry: 各 `skills/<桶>/<名>/SKILL.md`
    - Feature CodeMap: pending（见 Backlog）
    - Status: `confirmed`
  - 多平台安装与分发:
    - Main modules: `scripts/`、`.claude-plugin/`、`.codebuddy-plugin/`、`.cursor-plugin/`
    - Entry: `scripts/link-skills.sh`（Claude 本地链接）、`scripts/link-opencode-skills.sh`（OpenCode，Windows 走 junction、幂等）
    - Status: `confirmed`
- Evidence: README 结构树与目录逐一核对
- Validation: `scripts/list-skills.sh` 输出全部 SKILL.md 路径
- Next Drill-Down: 动 skills 时先 `list-skills.sh` 对账清单

### Node: Module Index

- Type: `module`
- Status: `confirmed`
- Children:
  - `docs/index.html`:
    - Responsibility: 主讲义，848 行单文件（样式/交互/正文同体）
    - Key dependencies: 无外部资源，离线可用
    - Risk notes: 正文 id、手写 TOC、JS 里硬编码的 observe id 列表**三处需人工同步**（script 段 `['ch1',...]` 数组）
  - `docs/demos/<NN>-<slug>/`:
    - Responsibility: 每个含 `DEMO.md`（脚本/推荐 Prompt/讲解词/失误预案）+ 演示输入 + `ai-output/` 保底
    - Risk notes: Demo 01–07 编号与讲义 2.1–2.7 强耦合，重排需双改
  - `docs/demos/08-project-demo/`:
    - Responsibility: 给听众取用的 SDD 仓库**模板示例**（AGENTS.md + docs/agents/）
    - Risk notes: 与仓库根部的 `AGENTS.md`/`docs/agents/` 内容相似但用途不同——改一处不代表改另一处
  - `skills/<general|dev|sdd>/`:
    - Responsibility: 插件发布的 skills 源码；每桶一份 README
    - Risk notes: 改动会经三份平台清单分发，需同步检查（见 Risk Areas）
  - `docs/agents/`:
    - Responsibility: 本仓库契约（issue-tracker / triage-labels / domain / git-contract），`/setup-skills` 于 2026-10-13 部署
  - `scripts/`:
    - Responsibility: 多平台链接与枚举（`link-skills.sh`、`link-opencode-skills.sh`、`list-skills.sh`）
- Evidence: `ls` + 各文件头核对
- Next Drill-Down: 无

### Node: Entry Index

- Type: `entry`
- Status: `confirmed`
- Entries:
  - UI / 文档: `docs/index.html`（浏览器直接打开）
  - CLI / commands: `scripts/link-skills.sh`、`scripts/link-opencode-skills.sh`（安装）、`scripts/list-skills.sh`（枚举）
  - Agent 配置入口: `AGENTS.md`（根目录，`## Agent Notice`）
  - 插件清单: `.claude-plugin/marketplace.json`（marketplace 名 `zy-skills`）、`.cursor-plugin/plugin.json`（v1.0.1，逐个列出 skills 路径）、`.codebuddy-plugin/`
- Evidence: 各文件已读
- Next Drill-Down: 无

### Node: Domain And Data

- Type: `object`
- Status: `confirmed`
- Purpose: 本仓库的"领域模型"是**映射关系**，不是数据表
- Children:
  - 核心映射（改动时三处对齐）:
    - Demo 01–07 ↔ 讲义 2.1–2.7 场景 ↔ `demos/<NN>/DEMO.md`
    - 讲义四章实践 ↔ 相关skills 标注 ↔ `skills/` 源目录
  - 状态机: issue 生命周期 `needs-triage`/`needs-info` → `ready-for-human` → `ready-for-agent` / `wontfix`（见 `docs/agents/triage-labels.md`）
  - Config namespaces: 无
- Evidence: index.html 正文与 README 表格核对
- Validation: 改映射任一侧后，grep 对侧核对
- Next Drill-Down: 无

### Node: External Dependencies

- Type: `dependency`
- Status: `confirmed`
- Children:
  - 上游 fork: [mattpocock/skills](https://github.com/mattpocock/skills)，fork 维护规则见 ZY-1999/skills（README 尾注）
  - 运行时第三方依赖: 无（HTML 零外部资源）
  - 安装目标端: Claude Code / OpenCode / CodeBuddy / Cursor / ZCode
- failure surfaces: 上游演进与本地 fork 的漂移（README 声明有维护规则，细节未核实）
- Evidence: README、插件清单
- Unknowns: 上游同步的具体流程
- Next Drill-Down: 无

### Node: Cross-Module Flows

- Type: `flow`
- Status: `confirmed`
- Major Flows:
  - skills 发布流: `skills/<桶>/<名>/SKILL.md` → 三份平台清单（`.claude-plugin/` `.codebuddy-plugin/` `.cursor-plugin/`）+ `scripts/link-*.sh` → 各客户端生效
    - Modules: skills/, 插件清单, scripts/
    - Effect: 新增/改名 skill 若不同步清单，Cursor 等按清单安装的平台会缺
  - 分享消费流: `docs/index.html` 章节 → `demos/<NN>/DEMO.md` 排练 → 现场
  - SDD 任务流（本仓库自身开工时）: `AGENTS.md` → `docs/agents/*` 契约 → `.scratch/<feature>/` issue 载体
- Evidence: README 安装矩阵 + 契约文件
- Next Drill-Down: 无

### Node: Validation

- Type: `validation`
- Status: `confirmed`
- Validation Entry:
  - Test commands: 无测试套件
  - Smoke paths: 浏览器打开 `docs/index.html`（目录折叠/复制按钮/打印）；`bash scripts/list-skills.sh` 应列出 20 个 SKILL.md
  - Local run: 链接脚本幂等可重跑（README 声明，OpenCode 版 Windows 走 junction）
  - Logs / metrics: 无
- proves: 页面可打开、skills 清单完整
- does not prove: 讲义内容事实正确性、各平台安装后实际可用性
- Next Drill-Down: 无

### Node: Risk Areas

- Type: `risk`
- Status: `confirmed`
- Risks:
  - 插件分发不同步:
    - Source: 新增/改名/删除 skill 时三份平台清单 + 链接脚本需同步
    - Affected capabilities: skills 集合、安装分发
    - Suggested Feature CodeMap: pending（skills 目录地图）
  - 08 模板与根配置混淆:
    - Source: `docs/demos/08-project-demo/` 与根部 `AGENTS.md`/`docs/agents/` 结构相同
    - Affected capabilities: 契约、Demo
  - index.html 三处人工同步:
    - Source: 正文 id / TOC 手写列表 / JS observe 数组
    - Affected capabilities: 讲义
  - 讲义事实性内容:
    - Source: 3.1 节曾出现 `StringUtils.truncate` 反例错误（已发现待修）；20%–40% 调研数据无出处
    - Affected capabilities: 讲义
- Validation: 改动后按 Cross-Module Flows 逐环节对账
- Next Drill-Down: 无

### Node: Feature CodeMap Backlog

- Type: `capability`
- Status: `confirmed`
- Backlog:
  - skills 目录地形图:
    - Why: 20 个 skill 分三桶，改/增 skill 是本仓库最高频操作，且牵动分发链
    - Likely entry: `skills/*/README.md`
    - Likely files: 各 `SKILL.md` + 插件清单
    - Priority: 高（下次动 skills 前建）
- Next Drill-Down: 无

## 3. Compact Indexes

### Capability Index Table

| Capability | Main Modules | Entry | Feature CodeMap | Status |
| ---------- | ------------ | ----- | --------------- | ------ |
| 讲义 | `docs/index.html` | 浏览器打开 | pending | confirmed |
| Demo | `docs/demos/01..08-*` | 各 `DEMO.md` | pending | confirmed |
| skills 集合 | `skills/{general,dev,sdd}` | 各 `SKILL.md` | pending（Backlog 优先） | confirmed |
| 安装分发 | `scripts/` + 三份插件清单 | `link-*.sh` | pending | confirmed |

### Module Index Table

| Module / Package | Path | Responsibility | Key Dependencies | Risk Notes |
| ---------------- | ---- | -------------- | ---------------- | ---------- |
| 讲义 | `docs/index.html` | 五章正文 + 交互 | 无 | 三处人工同步 |
| Demo | `docs/demos/` | 演示脚本与保底材料 | 讲义章节编号 | 编号强耦合 |
| 08 模板 | `docs/demos/08-project-demo/` | 听众取用的 SDD 示例 | 无 | 勿与根部配置混淆 |
| skills 源 | `skills/` | 插件发布源 | 上游 fork | 分发需同步清单 |
| 契约 | `docs/agents/` | 本仓库 SDD 契约 | 无 | — |
| 脚本 | `scripts/` | 链接与枚举 | 各客户端目录 | 幂等设计 |

### Cross-Module Flow Table

| Flow | Modules | Entry | Effect | Drill-Down |
| ---- | ------- | ----- | ------ | ---------- |
| skills 发布 | skills/ + 插件清单 + scripts/ | `link-*.sh` | 各平台可用 | 无 |
| 分享消费 | index.html + demos/ | `DEMO.md` | 现场演示 | 无 |
| SDD 任务 | AGENTS.md + docs/agents/ + .scratch/ | `AGENTS.md` | issue 载体与流转 | 无 |

### Quick File Index

- `README.md`: 仓库大门与安装矩阵
- `AGENTS.md`: agent 进场规则
- `docs/index.html`: 主讲义
- `docs/demos/README.md`: Demo 总览 + 排练指南
- `docs/agents/git-contract.md`: 分支与提交契约
- `scripts/list-skills.sh`: skills 对账命令

## 4. Maintenance Notes

- 结构性变化（模块边界、安装矩阵、契约）时刷新本地图；窄改动只动对应 Feature CodeMap。
- `.scratch/` 由 SDD 流程惰性创建，创建后无需改本图（属运行数据，不是地形）。
