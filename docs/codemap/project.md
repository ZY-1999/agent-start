# agent-start CodeMap (project)

## 1. Orientation

- Last updated: `2026-10-23`
- Updated: `2026-10-23` — 修复 commit review 门禁从未生效：宿主把 `${CLAUDE_PLUGIN_ROOT}` 替换为 Git Bash 风格路径 `/c/...`，`-File` 无法解析导致脚本从未执行；`hooks.json` 改为 `-Command` 内先转盘符再调用并透传 `$LASTEXITCODE`（`&` 调用下脚本内 `exit 2` 不会成为进程退出码），`pre-commit-review.ps1` 改用 `ConvertFrom-Json` 取 command、路径转换失败不再直接放行；插件清单 1.4.0→1.4.1
- Updated: `2026-10-20` — `/to-spec` 新增第三入口：简单且完整的需求经入口完整性门（completeness-bar）直接拆 spec，不过门退回 `/grill-me` / `/to-prd`；`/route`（Build something）新增对应直连链 `/to-spec` → `/tdd`；两桶 README 与 issue 模板 Parent 注释同步；插件清单 1.2.0→1.3.0
- Updated: `2026-10-16` — 新增 `.zcode-plugin/plugin.json`（ZCode 插件清单，探测顺序 `.zcode-plugin` > `.claude-plugin` > `.codex-plugin`；skills 数组 23 条 + hooks 声明，版本 1.2.0；市场源复用 ZCode 兼容的 `.claude-plugin/marketplace.json`）
- Updated: `2026-10-16` — `skills/third-party/` 重命名为 `skills/third-party-zentao/`；新增 `skills/third-party-confluence/`（confluence skill，vendored 自 pchuri/confluence-cli@`6c0d4af`，v2.30.0）；`/setup-skills` 步骤 5 扩为检查 `zentao` / `confluence` 两个 CLI，Agent Notice 模板与根 `AGENTS.md` 节改为 Third-party skills (zentao / confluence)；插件清单 1.1.0→1.2.0
- Updated: `2026-10-14` — 08 由模板示例扩为完整订单管理应用（backend/frontend/单 jar 交付）；新增插件 hooks（commit review 门）；01–07 `DEMO.md` 精简；插件清单 1.0.1→1.0.2
- Updated: `2026-10-14` — 新增 `skills/third-party/` 第三方桶（zentao-cli 0.3.1 / zentao-tour 0.3.0-beta.1，vendored 自 easysoft/zentao-cli@`ce36cf5`）；`/setup-skills` 新增步骤 5（检查 `zentao` CLI 安装，凭证推荐用户在 `~/.bashrc` 自行 export 环境变量），Agent Notice 模板与根 `AGENTS.md` 增加 Zentao skills 节；插件清单 1.0.2→1.1.0
- Project: agent-start（内部 git 名 `ongoal-skills`）
- Role / responsibility: AI Coding 技术分享材料仓库——一份离线 HTML 讲义 + 8 个 Demo + 23 个 skills（20 自研 + 3 第三方 vendored）；同时是多平台插件的**发布源仓库**（`skills/` 的改动会随 zy-skills 插件分发出去）
- Main languages / frameworks: Markdown、单文件 HTML（内嵌 CSS/JS，零外部依赖）、Bash；Demo 08 另含 Java（Spring Boot 2.7 / MyBatis-Plus）与 TypeScript（React 18 + antd 5 + Vite）
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
  - Live Demo（7 个现场 + 1 个完整项目）:
    - Main modules: `docs/demos/01..08-*/`
    - Entry: 01–07 各目录 `DEMO.md`（精简为 目标/对比/演示步骤/推荐 Prompt 四节）；08 是可运行应用，入口 `docs/demos/08-project-demo/AGENTS.md` + `.scratch/2026-10-13-order-admin/`（PRD + 7 specs）
    - Feature CodeMap: pending（08 订单管理应用值得单建，见 Backlog）
    - Status: `confirmed`
  - skills 集合（自研 20 + 第三方 vendored 3）:
    - Main modules: `skills/general/`(6) `skills/dev/`(11) `skills/sdd/`(3) `skills/third-party-zentao/`(2) `skills/third-party-confluence/`(1)
    - Entry: 各 `skills/<桶>/<名>/SKILL.md`
    - Feature CodeMap: pending（见 Backlog）
    - Status: `confirmed`
  - 多平台安装与分发:
    - Main modules: `scripts/`、`.claude-plugin/`、`.codebuddy-plugin/`、`.cursor-plugin/`、`.zcode-plugin/`
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
    - Responsibility: 主讲义，865 行单文件（样式/交互/正文同体）
    - Key dependencies: 无外部资源，离线可用
    - Risk notes: 正文 id、手写 TOC、JS 里硬编码的 observe id 列表**三处需人工同步**（script 段 `['ch1',...]` 数组）
  - `docs/demos/<NN>-<slug>/`（01–07）:
    - Responsibility: 每个含 `DEMO.md`（目标 / 传统 vs AI 对比 / 演示步骤 / 推荐 Prompt，2026-10-14 起精简版）+ 演示输入 + `ai-output/` 保底
    - Risk notes: Demo 01–07 编号与讲义 2.1–2.7 强耦合，重排需双改
  - `docs/demos/08-project-demo/`:
    - Responsibility: SDD 全流程产出的**完整订单管理应用**——Spring Boot 后端（`backend/`，30 个 Java 文件，MyBatis-Plus + Flyway + MockMvc 测试）+ React 前端（`frontend/`，antd/echarts）+ `docker-compose.yml`（mysql:8@3307）+ `.scratch/2026-10-13-order-admin/`（PRD + 7 specs）；单 jar 交付（`mvn package` 自动 npm build 并把 `frontend/dist` 嵌入 static，`java -jar` 8080 即全系统）
    - Risk notes: 与仓库根部的 `AGENTS.md`/`docs/agents/` 内容相似但用途不同——改一处不代表改另一处；`pom.xml` 的 `maven.compiler.release=8` 需 JDK 9+ 编译（JDK 8 会报"无效的标记: --release"），产物仍是 Java 8 字节码
  - `hooks/`:
    - Responsibility: 插件 hooks（`hooks.json` PreToolUse matcher Bash → PowerShell 执行 `pre-commit-review.ps1`；原 bash 版在 Windows 经 cmd.exe 调用会落 WSL 必败，1.4.0 移植；1.4.1 修复三处静默放行——宿主 POSIX 插件路径致 `-File` 解析失败、`&` 调用下 `exit 2` 未成为进程退出码、脚本内正则与 `Set-Location` 失败提前 exit 0）；在跑过 `/setup-skills` 的仓库拦截 agent 的 `git commit`，直到 `/review` 批准当前 diff（标记 `$GIT_DIR/zy-skills-review-approved`，hash 一次性精确匹配）
    - Risk notes: 审批后 diff 再变即失效需重跑 review；用户自己终端 commit 不受门限
  - `skills/<general|dev|sdd>/`:
    - Responsibility: 插件发布的自研 skills 源码（fork 血脉）；每桶一份 README
    - Risk notes: 改动会经三份平台清单分发，需同步检查（见 Risk Areas）
  - `skills/third-party-zentao/`、`skills/third-party-confluence/`:
    - Responsibility: 外部 vendored skills 按上游分桶（zentao 桶：easysoft/zentao-cli 的 zentao-cli / zentao-tour；confluence 桶：pchuri/confluence-cli 的 confluence）；原样引入，各桶 README 记录上游 commit 基线与版本
    - Risk notes: **不参与** fork 上游同步映射；更新走上游整目录覆盖并更新基线；zentao 桶两个 skill 互为兄弟目录（内部相对链接依赖此布局）；运行依赖全局 `zentao` / `confluence` CLI（npm `zentao-cli` / `confluence-cli`）
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
  - 插件清单: `.claude-plugin/marketplace.json`（marketplace 名 `zy-skills`，Claude Code 与 ZCode 共用）、`.cursor-plugin/plugin.json`、`.codebuddy-plugin/`、`.zcode-plugin/plugin.json`（均 v1.4.1，逐个列出 skills 路径）；hooks 经约定路径 `hooks/hooks.json` 分发（ZCode 清单额外显式声明）
  - Demo 08 运行入口: `docker-compose.yml`（MySQL@3307，root/order_demo）→ `ORDER_DB_PASSWORD=order_demo java -jar backend/target/order-admin-1.0.0.jar`（8080，Flyway 自动建表 + 种子数据）
- Evidence: 各文件已读
- Next Drill-Down: 无

### Node: Domain And Data

- Type: `object`
- Status: `confirmed`
- Purpose: 本仓库的"领域模型"是**映射关系**，不是数据表
- Children:
  - 核心映射（改动时三处对齐）:
    - Demo 01–07 ↔ 讲义 2.1–2.7 场景 ↔ `demos/<NN>/DEMO.md`
    - Demo 08 订单能力 ↔ `.scratch/2026-10-13-order-admin/specs/01..07` ↔ `backend/` 实现与 `frontend/` 页面
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
  - 第三方 vendored 源: [easysoft/zentao-cli](https://github.com/easysoft/zentao-cli)（`skills/third-party-zentao/`，MIT；基线 commit `ce36cf5`）、[pchuri/confluence-cli](https://github.com/pchuri/confluence-cli)（`skills/third-party-confluence/`，MIT；基线 commit `6c0d4af`）
  - 运行时第三方依赖: 无（HTML 零外部资源）；第三方 skill 例外——zentao 需全局安装 `zentao` CLI（npm 包 `zentao-cli`），confluence 需全局安装 `confluence` CLI（npm 包 `confluence-cli`），`/setup-skills` 环境检查探测
  - 安装目标端: Claude Code / OpenCode / CodeBuddy / Cursor / ZCode
- failure surfaces: 上游演进与本地 fork 的漂移（README 声明有维护规则，细节未核实）
- Evidence: README、插件清单
- Unknowns: 上游同步的具体流程
- Next Drill-Down: 无

### Node: Cross-Module Flows

- Type: `flow`
- Status: `confirmed`
- Major Flows:
  - skills 发布流: `skills/<桶>/<名>/SKILL.md` → 四份平台清单（`.claude-plugin/` `.codebuddy-plugin/` `.cursor-plugin/` `.zcode-plugin/`）+ `scripts/link-*.sh` → 各客户端生效
    - Modules: skills/, 插件清单, scripts/
    - Effect: 新增/改名 skill 若不同步清单，Cursor 等按清单安装的平台会缺
  - commit review 门（插件 hooks 分发后）: agent `git commit` → PreToolUse `hooks/pre-commit-review.ps1` → 无有效审批则拒绝 → `/review` 通过后写 hash 标记 → 同一 diff 重试 commit 放行
    - Modules: hooks/, skills/dev/review/, 各 SDD 仓库的 `docs/agents/issue-tracker.md`（opt-in 开关）
    - Effect: 装了新版插件的客户端里，SDD 仓库的 agent 提交必须先过 review
  - Demo 08 构建交付流: `mvn -f backend/pom.xml package` → exec 调 `npm run build`（frontend/，首次需 `npm ci`）→ dist 拷入 jar static → `java -jar` 单进程提供前端 + `/api`
    - Modules: backend/, frontend/, docker-compose.yml
    - Effect: 改前端或后端都必须重打包重启才能生效
  - 分享消费流: `docs/index.html` 章节 → `demos/<NN>/DEMO.md` 排练 → 现场
  - SDD 任务流（本仓库自身开工时）: `AGENTS.md` → `docs/agents/*` 契约 → `.scratch/<feature>/` issue 载体
- Evidence: README 安装矩阵 + 契约文件
- Next Drill-Down: 无

### Node: Validation

- Type: `validation`
- Status: `confirmed`
- Validation Entry:
  - Test commands: Demo 08 后端 `mvn -f docs/demos/08-project-demo/backend/pom.xml test`（MockMvc：订单 CRUD 口径 + SPA 回退）
  - Smoke paths: 浏览器打开 `docs/index.html`（目录折叠/复制按钮/打印）；`bash scripts/list-skills.sh` 应列出 22 个 SKILL.md；Demo 08——compose/容器起 MySQL@3307 → `ORDER_DB_PASSWORD=order_demo java -jar backend/target/order-admin-1.0.0.jar` → `curl http://127.0.0.1:8080/`（入口 HTML）与 `/api/orders?pageNum=1&pageSize=5`（8 条种子）
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
  - 08 构建 JDK 陷阱:
    - Source: `backend/pom.xml` 用 `maven.compiler.release=8`，JDK 8 的 javac 不支持 `--release` 标志，需 JDK 9+ 编译
    - Affected capabilities: Demo 08 构建
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
    - Why: 23 个 skill 分五桶（含 third-party vendored 两桶），改/增 skill 是本仓库最高频操作，且牵动分发链
    - Likely entry: `skills/*/README.md`
    - Likely files: 各 `SKILL.md` + 插件清单
    - Priority: 高（下次动 skills 前建）
  - Demo 08 订单管理应用 feature 图:
    - Why: 08 已是全仓库唯一的可运行应用（前后端 + specs + 构建链），后续改动的高频目标
    - Likely entry: `docs/demos/08-project-demo/AGENTS.md` 与 `.scratch/2026-10-13-order-admin/01-order-admin-system.md`
    - Likely files: `backend/src/main/java/com/demo/order/`（web/service/dao 分层）、`frontend/src/pages/`、`OrderControllerTest`/`OrderControllerApiTest`
    - Priority: 中（下次改 08 功能前建）
- Next Drill-Down: 无

## 3. Compact Indexes

### Capability Index Table

| Capability | Main Modules | Entry | Feature CodeMap | Status |
| ---------- | ------------ | ----- | --------------- | ------ |
| 讲义 | `docs/index.html` | 浏览器打开 | pending | confirmed |
| Demo（01–07 现场） | `docs/demos/01..07-*` | 各 `DEMO.md`（精简版） | pending | confirmed |
| Demo 08 应用 | `docs/demos/08-project-demo/{backend,frontend}` | `AGENTS.md` + `.scratch/` PRD/specs | pending（Backlog） | confirmed |
| skills 集合 | `skills/{general,dev,sdd}` + `skills/third-party-{zentao,confluence}` | 各 `SKILL.md` | pending（Backlog 优先） | confirmed |
| 安装分发 | `scripts/` + 三份插件清单 + `hooks/` | `link-*.sh` | pending | confirmed |

### Module Index Table

| Module / Package | Path | Responsibility | Key Dependencies | Risk Notes |
| ---------------- | ---- | -------------- | ---------------- | ---------- |
| 讲义 | `docs/index.html` | 五章正文 + 交互 | 无 | 三处人工同步 |
| Demo（01–07） | `docs/demos/01..07-*` | 演示脚本与保底材料 | 讲义章节编号 | 编号强耦合 |
| 08 订单应用 | `docs/demos/08-project-demo/` | SDD 全流程示例：前后端 + specs + 单 jar | mysql@3307、JDK 9+ 编译 | 勿与根部配置混淆 |
| skills 源 | `skills/` | 插件发布源 | 上游 fork | 分发需同步清单 |
| hooks | `hooks/` | commit review 门（PreToolUse） | `/review` skill 审批标记 | 审批随 diff 变化失效 |
| 契约 | `docs/agents/` | 本仓库 SDD 契约 | 无 | — |
| 脚本 | `scripts/` | 链接与枚举 | 各客户端目录 | 幂等设计 |

### Cross-Module Flow Table

| Flow | Modules | Entry | Effect | Drill-Down |
| ---- | ------- | ----- | ------ | ---------- |
| skills 发布 | skills/ + 插件清单 + scripts/ | `link-*.sh` | 各平台可用 | 无 |
| commit review 门 | hooks/ + review skill | `hooks/hooks.json` | SDD 仓库 agent 提交须过 `/review` | 无 |
| 08 构建交付 | backend/ + frontend/ | `backend/pom.xml` | 单 jar 含前端，8080 全系统 | Backlog feature 图 |
| 分享消费 | index.html + demos/ | `DEMO.md` | 现场演示 | 无 |
| SDD 任务 | AGENTS.md + docs/agents/ + .scratch/ | `AGENTS.md` | issue 载体与流转 | 无 |

### Quick File Index

- `README.md`: 仓库大门与安装矩阵
- `AGENTS.md`: agent 进场规则
- `docs/index.html`: 主讲义
- `docs/demos/README.md`: Demo 总览 + 排练指南
- `docs/agents/git-contract.md`: 分支与提交契约
- `hooks/hooks.json` + `hooks/pre-commit-review.ps1`: commit review 门
- `scripts/list-skills.sh`: skills 对账命令

## 4. Maintenance Notes

- 结构性变化（模块边界、安装矩阵、契约）时刷新本地图；窄改动只动对应 Feature CodeMap。
- `.scratch/` 由 SDD 流程惰性创建，创建后无需改本图（属运行数据，不是地形）。
