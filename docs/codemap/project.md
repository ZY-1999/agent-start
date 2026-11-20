# agent-start CodeMap (project)

## 1. Orientation

- Last updated: `2026-11-16`
- Updated: `2026-11-16`（第二次）— `reverse-prd` 增加**前后端代码基线探测**：step 0 由「锁定代码版本」改为「锁定代码基线（两侧）」——先探测工作区是只有后端（有 controller/entity/mapper、无 page/router/i18n）还是只有前端，**只有一侧时停下来问用户要另一侧的路径或 git 地址**，并给出四分支决策表（两边都有 → 三方对齐划分模块；用户说前端拿不到 → 只用现有代码生成，入口/菜单/标签/文案类结论降 `低` 并开 Q，总览覆盖度写明「仅后端」；只有前端 → 先说明规则与数据结构会大面积缺失，确认后才生成，缺失面进 A02；都不可读 → 停）。理由是后端给规则/状态/数据/接口，前端给入口/菜单/字段标签/前端校验/用户看到的文案——单侧生成的典型症状是从接口路径反推「何地（入口）」、把能从前端 i18n 抄到的文案写成「平台统一维护」。配套：step 1 双仓库各建一份项目图（前端图只需页面/路由树与页面调用的接口）；step 3 模块划分改为「前端菜单 ↔ 后端 controller ↔ 数据表」三方对齐并记录不一致处；step 4 场景描述「何地」取前端菜单路径不取接口路径；incremental 模式补「后到的仓库（通常是前端）单独走一次增量，回填入口与文案」；conventions 版本块支持双基线（`后端 dev@x / 前端 dev@y`，单边写明「前端未提供，入口与文案未核实」）、溯源路径加 `前端:`/`后端:` 前缀；overview 文档信息加「代码基线」行；module-template 增「入口与提示文案取自前端」规则；business-voice 文案来源改为先取前端；review-brief Pass 1 增前端仓库核查行（入口层级、i18n 文案、前端校验、页面↔接口对应）、Pass 2 把「何地写成接口路径」列为失败；A02 增「整侧代码缺失也登记在此」；插件清单 1.9.0→1.10.0
- Updated: `2026-11-16`（第三次）— `reverse-prd` 补两类**实现侧交付物**（用户确认需给接手/联调/外包读者承载技术口径，但仍与业务正文隔离）：模块 §6 对外接口改为「业务要素表 + 结构代码块」双层——§6.1 提供的接口与 §6.2 依赖的外部接口各附 `jsonc` 请求/响应结构（字段名照抄代码、业务含义写注释、存疑标 `// 待确认`），删掉原「方法名参数类型不进正文」的冲突表述；common/ 新增 `07-表结构DDL.sql`（从代码反推的全系统 DDL，按业务对象分段 `CREATE TABLE`，头注释指向 C02、取值编码指向 C03/A03 §4，存疑标 `-- 待确认`，明示非权威、上线/迁移前须与真实库核对）。配套把「物理名/代码位置唯一位置」从一处（A03）扩为三处：模块 §6 结构块、`common/07-表结构DDL.sql`、`appendix/03`，并同步 SKILL（Core Position 例外声明 + Output Shape + Authority + Quality Gate）、business-voice（禁用清单边界）、conventions（权威归属表 + 溯源三处）、overview（附工程侧资料加 DDL 入口）、appendix-templates（A03 改称「断言溯源唯一处」）、common-templates（开头六份→六份产品文档 + DDL，物理结构进 DDL 不进六份）、module-template（写前段补例外说明）；插件清单 1.10.0→1.11.0
- Updated: `2026-11-16`（第四次）— `reverse-prd` step 0 增加 **DDL 提问环节**：锁定代码基线时同步问用户「能否提供数据库 DDL（建表 SQL / schema 导出 / migration）？」，提供则以真实 DDL 为权威来源（C07 基于它、C02/C03 属性与编码对照它核对），不提供才从实体类 / Mapper / migration 脚本反推并整份标「反推、未与真实库核对」、存疑字段标 `-- 待确认` 开 Q；Completion condition 增「DDL 来源已定」；step 2 「Read schema」改为「提供 DDL 时以它为准，否则反推并标反推」；common-templates C07 增两种来源（文件头写「用户提供（权威）｜反推（非权威）」）；插件清单 1.11.0→1.12.0
- Updated: `2026-11-18`（第一次）— `reverse-prd` 场景详述由「七子项」重构为「输入 → 处理规则 → 输出 → 异常处理」四段式（对齐团队 PRD 功能描述格式）：原「场景描述」七要素表拆成「输入」（谁/何时/何地/做什么/前置）与「输出」（后置结果/业务价值），「场景流程」步骤表删除（顺序隐含进规则、数据变化写进输出），「业务规则」并入「处理规则」、「异常与边界」并入「异常处理」，「功能拆解」「权限与可见性」「验收标准」三个支撑子项保留；「何地取前端菜单」约束保留在输入段。同步：SKILL step 4 与 Quality Gate/References、conventions 权威归属表（场景内规则→§3.x.2 处理规则、异常→「异常处理」）、review-brief Pass 1/2/3（四段式检查 + 输入/输出站得住 + §3.x.5 功能拆解）、overview（流程→处理规则）；插件清单 1.12.0→1.13.0
- Updated: `2026-11-20`（第一次）— `reverse-prd` 场景详述由七子项并为六子项：合并「场景流程」与「功能拆解」（两者「系统响应/处理逻辑」「数据变化/输出」高度重叠，同一件事写两遍），功能点 `F` 编号从独立子项改挂在场景流程（§3.x.2）的对应步骤行上，输入/关键校验并进流程表两列，纯系统等待/数据流转步骤留空 F；子项顺延为 描述→流程（含功能点）→规则→异常→权限→验收 六项。同步：SKILL step 4 六子项 + Quality Gate、module-template 场景模板、conventions 编号说明（F 挂流程步骤）、review-brief Pass 1/2/3（六子项检查 + 流程步骤业务口径）、business-voice（功能拆解→场景流程步骤）；插件清单 1.13.0→1.14.0
- Updated: `2026-11-20`（第二次）— `reverse-prd` 接口入参/返回值从模块 §6 正文抽到**独立文件**承载：新增 `interfaces/` 目录，每模块一份 `M<模块号>-接口结构.md`（「对外接口」「依赖接口」两节），结构用 jsonc 注释块（字段名照抄代码、`//` 写业务含义、存疑标 `// 待确认`），来源二选一（用户提供接口文档/swagger 为准，否则从代码反推标「推导」）；模块 §6 正文只留业务要素表，结构块换成链接指向独立文件。新增 `references/interface-template.md`。同步「物理名唯一位置」从「模块 §6 结构块 + DDL + 台账」改为「interfaces/ 结构文件 + DDL + 台账」（三处），牵动 SKILL（Core Position 例外声明三处 + Output Shape 加 interfaces/ + Authority + Quality Gate + References）、business-voice 边界、conventions 权威归属与溯源、appendix-templates、module-template §6 与「写之前」、overview 工程侧资料；插件清单 1.14.0→1.15.0
- Updated: `2026-11-20`（第三次）— `reverse-prd` 接口处理增加**已废弃接口识别**：代码带 `/** @ignore */` 标记**且**前端无页面/路由调用的接口判定为已废弃（两个条件**同时**满足才废弃，仅标记但仍有页面调用、或仅无页面都不算），废弃接口不进「对外接口/依赖接口」两节，收进接口结构文件末尾新增的「已废弃接口」节，一行一条（接口标识 + 废弃证据 + 认定理由），不写入参/返回值结构。同步：interface-template 加判定规则与「已废弃接口」节模板、SKILL step 4 接口部分加判定说明 + Output Shape 接口文件注释改为三节、review-brief Pass 1 加 `interfaces/` 核查行（废弃接口识别正确、未混入正常接口）；插件清单 1.15.0→1.16.0
- Updated: `2026-11-16` — `reverse-prd` 删除模块级「§7 非功能与业务约束」：该节填的时效、浏览器兼容、脱敏都是**全系统通用要求**，每个模块抄一遍既重复又必然漂移，统一归 `common/05-非功能需求.md`；只有**改变某个场景行为**的约束才保留，写成 §4.1 的 `G` 规则或该场景的异常行（例：并发同改后覆盖 → `G01-R5`），§8 待确认顺延为 §7。同步：module-template 删节 + §4.1 增「并发/重复提交/降级不单开非功能节」说明；conventions 权威归属表把 C05 行改为「不设模块级非功能节」；SKILL step 4 与 Quality Gate 改为显式禁止模块级非功能节；review-brief 可读性检查把「§7 影响场景与兼容性」换成「有没有偷偷长出非功能节」；总览 C05 摘要补「兼容与安全」；试点 M01 删节重编号并把并发事实升为 `G01-R5`，A03 台账 `M01 §8`→`§7`；插件清单 1.8.0→1.9.0
- Updated: `2026-11-10`（第三次）— **以业务场景为组织主线重构 `reverse-prd`**（对齐用户定稿的《PRD 文档模板（业务场景驱动版）》）：模块降级为容器，场景成为文档单元——新增 §2 场景地图（2.1 角色与职责 / 2.2 场景清单含**优先级 P0-P2** / 2.3 场景关系），§3 场景详述固定**七个子项**（场景描述七要素：谁/何时/何地/做什么/为什么/前置/后置 → 场景流程 → 业务规则 → **功能拆解**（输入业务要素/处理/输出/关键校验 + 本场景字段说明）→ 异常与边界（触发/系统处理/**用户提示**/业务影响）→ 权限与可见性（只写差异）→ **两层验收标准**（场景级端到端 + 功能点/异常级）），新增 §4 跨场景通用规则（`G` 前缀 + 状态机含流转性质），§7 非功能表带**影响场景**列，§8 待确认带**关联场景**列；编号体系随之改为**场景作用域**：`S02-01-R1` / `-F1` / `-E1` / `AC-S02-01-1` / `G02-R1`，conventions 增「规则只在一层：场景内 → 跨场景 → 全局，只升不复制」；总览新增**业务全景**（跨域端到端主线）与场景数列，场景清单下沉到各模块 §2；A01 增关联场景列、A02 覆盖口径扩到场景级；review-brief 可读性检查重写为场景驱动 15 项（七子项齐不齐、规则有没有分层、功能拆解是不是业务口径、验收有没有两层、§8 挂没挂场景等），完整性检查补 AC/功能拆解/消费方/范围外/规则分层覆盖；business-voice 增「功能拆解用业务要素不写参数名」；试点 M01 重写为 v3.0（8 场景 × 7 子项、4 条 G 规则、20 条场景级 AC、9 条带场景的待确认），A03 台账同步为场景作用域编号；插件清单 1.7.0→1.8.0
- Updated: `2026-11-10` — `skills/dev/reverse-prd/` 从「开发可读的逆向说明」改为**产品可读的 PRD**：新增 `references/business-voice.md`（语言契约——禁用清单含表列名/类名方法名/端点编号当功能名/注解框架名/消息码/配置与缓存 key/SQL 条件/日志/代码统计；白名单只有外部系统名与配业务名的接口标识；四类高危内容改写对照：软删→业务后果、事务锁幂等补偿→业务连续性、错误码→异常清单、端点→场景）；模块模板改为**场景为主干**（§3 场景 S 编号：角色/前置/操作序列含数据变化/结果/异常与业务后果/手工验收要点；§4 规则 R 编号须业务可争论并写违反表现；§7 接口分「对外提供」与「依赖外部」双向且各带失败约定；执行语义表改为业务后果表）；**证据下沉**——正文删除全部证据列与 CodeMap 链接，新增 `appendix/03-溯源台账.md`（断言编号→依据位置→核对结论，含物理结构映射与取值编码映射，明确标注非产品内容），固定文档数 9→10；common 六份全部业务化（C02 物理表列→业务对象/属性/生命周期/数据主责与一致性，C04 错误码→触发情况/用户看到的提示/业务后果 + 静默失败清单，C05 →外部依赖与运行约束含自动执行动作与对账）；总览改为业务能力地图、CodeMap 与台账收进「附：工程侧资料」；review-brief 两遍→三遍（准确性→**可读性**含禁用模式机械扫描与 LEAK/PASS/UNUSABLE 判定、LEAK 必须给出改写→完整性）；conventions 新增「语言契约」节并把代码位置限定在 A03；Quality Gate 重写为 16 条（零实现泄漏、场景驱动、双向接口、台账全覆盖、静默失败必列、范围外/权限拆轴/AC 可执行等）；插件清单 1.6.1→1.7.0。**同日二次优化（参考团队单文档 PRD 模板）**：模块模板增 §1「本模块不负责什么（范围外）」、§2 权限拆「可查看 / 可操作 / 明确不能做」三列、场景表增「一句话（用户故事）」行、异常表增「用户看到什么」列、验收标准改 `AC<模块号>-<序号>` + `- [ ]` 复选框（给定/当/则，一条一结果）、§5 增「流转性质」（正常/逆向/异常）、§6 增「字段级校验」与「数据被谁消费」、§8 增「兼容性」与「改动影响面」；C02 增「数据消费方」节（原待确认顺延为 §7）、C05 增「兼容性与安全约束」节；总览文档信息增文档负责人/评审人/关联文档与「范围外」；conventions 编号表增 `AC`，硬约束补「验收标准挂在场景下但独立编号」；review-brief 可读性检查补 7 条结构项（范围外、权限拆轴、用户提示列、验收可执行、字段校验、消费方、影响面）、完整性检查补 AC/字段校验/消费方/范围外覆盖；business-voice 增「文案与验收标准的写法」（提示文案照抄、验收标准句式、用户故事只写一句）；ongoal-sdc 的 M01 试点同步升到 v2.1（9 个场景 / 16 条规则 / 20 条 AC / 9 个待确认）。**随后全量复查精简**：质量门 26→16 条（结构项合并、禁用清单改为指向 business-voice）；conventions §2 收敛为「零实现 + 指向 business-voice」，删掉与之重复的禁用枚举、改写对照表与白名单（原先三处同源，属第二个真相）；C04 删「异常的对外表现」节（与异常清单重复且是通用套话），小节顺延并修 A01 锚点；business-voice 删重复的判断标准段与 §四.3 的文案说明（已在 §五）；review-brief 修「none of the three」——结论实为五种，并合并「执行语义」与「删除与状态」两条检查、合并 LEAK 改写要求；SKILL 删掉与 business-voice/quality gate 重复的四类改写表、两处格言式句子，修「Two things」与实际三小节的矛盾、完整性清单补 `AC`；A03 与 SKILL 各删一处重复的探针验证说明；总览删泛化阅读建议
- Updated: `2026-11-04` — 参考《让AI接手老项目的五步法》增强 `skills/dev/reverse-prd/`：模块模板 §4 新增**执行语义**表（事务边界 / 锁与并发 / 幂等 / 补偿与降级，功能涉及多步写库、发消息、调外部系统或补偿任务时条件必填，简单功能标"无"）；置信度对齐三分类（`高`=代码明确实现 / `中`=根据代码推断 / `低`=无法确认——规则可能在配置、数据、外部系统或人脑里，且必须同步开 Q 条目）；review-brief Pass 1 增"测试断言为最强证据、文档与测试不一致本身是高价值发现"与执行语义核查行、Pass 2 分支枚举补事务边界与补偿两类；SKILL.md 加全程只读约束（仅写 `docs/reverse-prd/` 与 `docs/codemap/`）、step 4 与 Quality Gate 同步执行语义、新增 After the PRD tree 小节（A01 → `/qa-clarify`//`/qa-cases` 探针验证、低风险小需求 → `/to-spec`//`/sdd-flow`、目标仓库 AGENTS.md 索引）；overview 模板加运行形态行、A01 加探针验证说明；另增**文件命名约定**——文件名与内容主体语言一致，不做"英文文件名 + 中文内容"混搭，common/appendix/总览的模板文件名与全部链接改中文（`00-总览.md`、`01-角色与权限.md`、`01-全局待确认问题.md` 等），目录名保持英文，conventions.md 相应新增"三、文件命名"节；插件清单 1.6.0→1.6.1
- Updated: `2026-10-24` — 新增 `skills/dev/reverse-prd/`（逆向 PRD：从代码反推按模块拆分的 PRD 体系，全局层 common/ 只写一次 + 模块层 modules/ 9 节 + 总览层 00-overview 只索引文档；代码地形全交 `/codemap`，每模块一份 feature 图且写前校验；不设 changelog；step 6 为两遍对抗式复核——逐条准确性、业务规则完整性）；README、`skills/dev/README.md` 与本文计数同步 12 / 26 / 29；四份 plugin.json 与 `.codebuddy-plugin/marketplace.json` 1.5.1→1.6.0
- Updated: `2026-10-23` — 修复 commit review 门禁从未生效：宿主把 `${CLAUDE_PLUGIN_ROOT}` 替换为 Git Bash 风格路径 `/c/...`，`-File` 无法解析导致脚本从未执行；`hooks.json` 改为 `-Command` 内先转盘符再调用并透传 `$LASTEXITCODE`（`&` 调用下脚本内 `exit 2` 不会成为进程退出码），`pre-commit-review.ps1` 改用 `ConvertFrom-Json` 取 command、路径转换失败不再直接放行；插件清单 1.4.0→1.4.1
- Updated: `2026-10-22` — `/setup-skills` 由四件套扩为 aiCoding 六件套：新增 `docs/agents/developer.md`（通用规范 + skill 资产规范）与 `checklist.md`；部署模板由「仅 Agent Notice block」升级为完整 AGENTS.md 骨架（工程概述 / 语言 / 构建与验证 / Agent Notice 含 Developer rules + Checklist / 默认边界 / 最小记忆能力），四件套 references 全部中文化（`Type:`/`Status:` 行名与五个 triage 标签字符串保留）；`/review` 的 Standards 轴改为优先读 `developer.md` + `checklist.md`；根 `AGENTS.md` 与 `docs/agents/` 同步改造；插件清单 1.4.0→1.5.0（2026-10-23 追加：`references/git-contract.md` 改以 `ongoal-generator/gittemplate` 的 Git Contract 为骨架——七类分支映射表 + 关联 ID 提交模板，部署时按仓库实际裁剪；本仓库自身契约仍为 solo + conventional-commits；Agent Notice 首节由 `Grill first` 改为 `Clarify, plan, then execute` 三步块，内嵌 `/grill-me` 与 `/grill-with-docs`；AGENTS.md 骨架从 SKILL.md 内联抽出为 `references/agents-md.md`，SKILL.md 只留引用，195→113 行）
- Updated: `2026-10-20` — `/to-spec` 新增第三入口：简单且完整的需求经入口完整性门（completeness-bar）直接拆 spec，不过门退回 `/grill-me` / `/to-prd`；`/route`（Build something）新增对应直连链 `/to-spec` → `/tdd`；两桶 README 与 issue 模板 Parent 注释同步；插件清单 1.2.0→1.3.0
- Updated: `2026-10-16` — 新增 `.zcode-plugin/plugin.json`（ZCode 插件清单，探测顺序 `.zcode-plugin` > `.claude-plugin` > `.codex-plugin`；skills 数组 23 条 + hooks 声明，版本 1.2.0；市场源复用 ZCode 兼容的 `.claude-plugin/marketplace.json`）
- Updated: `2026-10-16` — `skills/third-party/` 重命名为 `skills/third-party-zentao/`；新增 `skills/third-party-confluence/`（confluence skill，vendored 自 pchuri/confluence-cli@`6c0d4af`，v2.30.0）；`/setup-skills` 步骤 5 扩为检查 `zentao` / `confluence` 两个 CLI，Agent Notice 模板与根 `AGENTS.md` 节改为 Third-party skills (zentao / confluence)；插件清单 1.1.0→1.2.0
- Updated: `2026-10-14` — 08 由模板示例扩为完整订单管理应用（backend/frontend/单 jar 交付）；新增插件 hooks（commit review 门）；01–07 `DEMO.md` 精简；插件清单 1.0.1→1.0.2
- Updated: `2026-10-14` — 新增 `skills/third-party/` 第三方桶（zentao-cli 0.3.1 / zentao-tour 0.3.0-beta.1，vendored 自 easysoft/zentao-cli@`ce36cf5`）；`/setup-skills` 新增步骤 5（检查 `zentao` CLI 安装，凭证推荐用户在 `~/.bashrc` 自行 export 环境变量），Agent Notice 模板与根 `AGENTS.md` 增加 Zentao skills 节；插件清单 1.0.2→1.1.0
- Project: agent-start（内部 git 名 `ongoal-skills`）
- Role / responsibility: AI Coding 技术分享材料仓库——一份离线 HTML 讲义 + 8 个 Demo + 29 个 skills（26 自研 + 3 第三方 vendored）；同时是多平台插件的**发布源仓库**（`skills/` 的改动会随 zy-skills 插件分发出去）
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
  - `AGENTS.md`: agent 进场规则（Clarify-plan-execute / issue tracker / 契约索引）
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
  - skills 集合（自研 26 + 第三方 vendored 3）:
    - Main modules: `skills/general/`(6) `skills/dev/`(12) `skills/sdd/`(3) `skills/qa/`(5) `skills/third-party-zentao/`(2) `skills/third-party-confluence/`(1)
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
  - `skills/<general|dev|sdd|qa>/`:
    - Responsibility: 插件发布的自研 skills 源码（fork 血脉）；每桶一份 README
    - Risk notes: 改动会经四份平台清单分发，需同步检查（见 Risk Areas）
  - `skills/third-party-zentao/`、`skills/third-party-confluence/`:
    - Responsibility: 外部 vendored skills 按上游分桶（zentao 桶：easysoft/zentao-cli 的 zentao-cli / zentao-tour；confluence 桶：pchuri/confluence-cli 的 confluence）；原样引入，各桶 README 记录上游 commit 基线与版本
    - Risk notes: **不参与** fork 上游同步映射；更新走上游整目录覆盖并更新基线；zentao 桶两个 skill 互为兄弟目录（内部相对链接依赖此布局）；运行依赖全局 `zentao` / `confluence` CLI（npm `zentao-cli` / `confluence-cli`）
  - `docs/agents/`:
    - Responsibility: 本仓库契约六件（issue-tracker / triage-labels / domain / git-contract / developer / checklist），中文，`/setup-skills` 部署；`developer.md` 与 `checklist.md` 是 `/review` Standards 轴的标准来源与覆盖地图
    - Risk notes: 文件名是硬契约——`hooks/pre-commit-review.ps1` 以 `docs/agents/issue-tracker.md` 是否存在判定门禁是否生效；`Type:` / `Status:` 行名与五个 triage 标签字符串不可翻译
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
  - 插件清单: `.claude-plugin/plugin.json` 与 `.claude-plugin/marketplace.json`（marketplace 名 `zy-skills`，Claude Code 与 ZCode 共用）、`.cursor-plugin/plugin.json`、`.codebuddy-plugin/plugin.json` 与 `.codebuddy-plugin/marketplace.json`、`.zcode-plugin/plugin.json`（四份 plugin.json 均 v1.16.0，逐个列出 skills 路径；`.codebuddy-plugin/marketplace.json` 同为 v1.16.0，`.claude-plugin/marketplace.json` 本身无 version 字段）；hooks 经约定路径 `hooks/hooks.json` 分发（ZCode 清单额外显式声明）
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
  - Smoke paths: 浏览器打开 `docs/index.html`（目录折叠/复制按钮/打印）；`bash scripts/list-skills.sh` 应列出 29 个 SKILL.md；Demo 08——compose/容器起 MySQL@3307 → `ORDER_DB_PASSWORD=order_demo java -jar backend/target/order-admin-1.0.0.jar` → `curl http://127.0.0.1:8080/`（入口 HTML）与 `/api/orders?pageNum=1&pageSize=5`（8 条种子）
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
    - Source: 新增/改名/删除 skill 时四份平台清单 + 链接脚本需同步
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
    - Why: 29 个 skill 分六桶（含 third-party vendored 两桶），改/增 skill 是本仓库最高频操作，且牵动分发链
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
| skills 集合 | `skills/{general,dev,sdd,qa}` + `skills/third-party-{zentao,confluence}` | 各 `SKILL.md` | pending（Backlog 优先） | confirmed |
| 安装分发 | `scripts/` + 四份插件清单 + `hooks/` | `link-*.sh` | pending | confirmed |

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
- `docs/agents/developer.md` + `docs/agents/checklist.md`: 开发规范与评审清单（`/review` Standards 轴来源）
- `hooks/hooks.json` + `hooks/pre-commit-review.ps1`: commit review 门
- `scripts/list-skills.sh`: skills 对账命令

## 4. Maintenance Notes

- 结构性变化（模块边界、安装矩阵、契约）时刷新本地图；窄改动只动对应 Feature CodeMap。
- `.scratch/` 由 SDD 流程惰性创建，创建后无需改本图（属运行数据，不是地形）。
