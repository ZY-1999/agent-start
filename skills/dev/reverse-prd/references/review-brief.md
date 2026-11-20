# Review brief — accuracy, readability, completeness

Hand this brief to a **fresh-context, read-only, code-capable sub-agent** (use the environment's code-exploration type — e.g. `code-explorer`) together with the generated `docs/reverse-prd/` tree and repo access. Never give it the generation chat: the agent that wrote the docs will defend them.

Three passes, strictly in order. **Accuracy first** — you cannot judge coverage against claims that might be false. **Readability second** — an accurate document that product cannot read has failed its only job.

## Pass 1 — Accuracy: is every line true?

Walk each document **row by row**. Not "does this look right" — open the code and check the claim. Locate each claim through `appendix/03-溯源台账.md`, then verify the cited position really says that.

| Document | What to verify row by row | Where to look |
|---|---|---|
| `common/01-角色与权限` | every role, every permission boundary, every visible-scope claim, the check order | middleware, guards, decorators, resource-ownership queries |
| `common/02-数据字典` | every business object's attributes, requiredness, defaults, constraints, lifecycle, source-of-truth | entity definitions, DDL, migrations, dictionary tables |
| `common/03-枚举与状态机` | every state value and its business meaning; every legal transition; every action allowed per state | enum definitions, status-change call sites |
| `common/04-错误码` | every trigger condition, the user-facing message, the business consequence; every silent failure is really silent | throw sites, exception handling, catch blocks that only log |
| `common/05-非功能需求` | every external system and its failure impact; every timeout / retention / scheduled action | config, job definitions, integration clients |
| `common/06-术语表` | every term's meaning matches usage in the module docs; every synonym collision is real | UI labels, field comments, module text |
| `modules/M<nn>` | every scenario in §2 exists and is reachable from a real entry; each scenario's six sub-items (描述 / 流程（含功能点） / 规则 / 异常 / 权限 / 验收) are complete and consistent; every `S…-R` rule and its violation behavior; every `S…-F` function's input/processing/output/validation; every `S…-E` exception's trigger, user-visible message and impact; both acceptance layers are executable and match real behavior; §4 state rows including 流转性质; §5 attributes and data consumers; §6 interface rows both directions; §7 questions carry a real scenario | controllers → services → jobs → validators → DDL → downstream consumers |
| `appendix/01` | every item still exists in its source doc with the same scenario tag; the ranking and assignee | module §7, common `待确认` |
| `appendix/02` | every uncovered capability really has no module; the risk level holds | route table, menu tree, controller list |
| `appendix/03` | every assertion has a row; every cited position exists; every `核对结论` matches what the code actually shows; the physical mapping table is correct | the cited files themselves |
| `interfaces/M<nn>` | every provided/depended interface has request/response structure; every `/** @ignore */` interface with no page/route call is in the 「已废弃接口」 section, and no deprecated interface is listed among live ones | controllers, DTO/VO, router/menu config |
| `00-总览` | module↔capability mapping, coverage counts, the one-line engineering pointer | route table, menu tree, `docs/codemap/` |
| **frontend repo** (when in scope) | every 「何地（入口）」 exists in the router / menu config with that hierarchy; every user-facing message matches the i18n value or the page text; every client-side validation the doc claims is really there; the page↔endpoint mapping in §6 holds | router config, menu tree, page components, i18n files |

Verdict per row:

- `CONFIRMED` — the code says exactly this. Cite `file#function`.
- `WRONG` — the code says otherwise. State the correct value and its evidence.
- `UNVERIFIABLE` — no evidence either way. The doc must demote it: confidence to `低`, and a Q entry. An unmarked guess is a defect.

**Silent failures are the highest-value findings.** A `catch` block that only logs, a return that swallows a null, a validation that never fires — each one is a business risk the doc must either describe in C04 §2 (静默失败清单) or raise as a Q. Check them one by one.

**Tests outrank reading.** Where the repo has a test, its assertions are the strongest evidence: a rule that contradicts them is `WRONG` even if the code path looks plausible. A doc-vs-test mismatch is itself a finding — report it explicitly rather than silently picking a side.

## Pass 2 — Readability: can product actually use this?

This pass judges one thing: **does the document read as a business document?** It fails on any implementation detail in the body.

Mechanical sweep first — flag every hit in `modules/`, `common/`, `00-总览`:

| Pattern | What it catches |
|---|---|
| `\|` cells containing `#`, `()`, `=` | `AaadaaServiceImpl#save`, `adaa084='T'` |
| Backticked tokens matching `[A-Z][a-z]+(Service|Controller|Mapper|Impl|Entity|Enum|VO|Api)` | class names |
| Backticked tokens matching `[a-z]{2,}[0-9]{3,}` or `[a-z]+[0-9]{2,}[a-z]?` | obfuscated table/column names |
| `@Transactional`, `@Lock`, `Result.`, `log.`, `wrapper`, `Mapper`, `Feign`, `Redis`, `Nacos`, `JNPF` | framework leakage |
| Codes like `i001`, `w101`, `pingtai06`, `E2001` in the body | message codes |
| 「端点」「接口编号」「表列」「字段名」「空壳」「硬编码」「兜底」「吞异常」 | implementation narration |
| URLs of `docs/codemap/` inside module bodies | code terrain leaking into the PRD |
| 「N 个端点」「M 张表」「grep 命中 K 处」 | code statistics posing as facts |

Then judge the substance, which no regex can catch:

| Check | Fail looks like |
|---|---|
| **场景是不是主线** — §2 有场景清单（角色/触发/目标/优先级），§3 按场景逐个展开，不是按接口或功能点罗列 | 每个接口一节，标题是「F01-01 标准维护套件」，内容是「调用 001 分页、002 列表…」 |
| **六个子项齐不齐** — 描述 / 流程（含功能点） / 规则 / 异常 / 权限 / 验收 | 只有流程和规则，没有验收；或权限一节空着；或流程步骤漏挂 F 编号 |
| **场景描述能不能站住** — 谁 / 何时 / 何地 / 做什么 / 为什么 / 前置 / 后置 | 「何地」写成"后台"或接口路径，没有菜单与按钮；「为什么」写成"实现该功能" |
| **规则有没有分层** — 场景内 `S…-R`、跨场景 `G…-R`、全局 `common/`，一条只在一层 | 同一条规则在三个场景里各抄一遍，或全局规则在模块里又写一份 |
| **流程步骤是不是业务口径** — 输入是业务要素（商品、数量、地址），不是参数名 | 输入写 `skuId`、`couponCode`；处理写"调用库存服务"；有独立业务动作的步骤漏挂 F 编号 |
| **异常表有没有"用户看到什么"** — 单列提示文案，静默的写「无提示」 | 只有"系统处理"，或空白 |
| **验收标准有没有两层** — 场景级端到端（含后置结果）+ 功能点/异常级，一条一个结果 | 只有场景级；或一条里塞三个结果，测失败时不知道哪条没过 |
| **§1 有没有范围外** — 明确写出本域不负责什么 | 只写"负责什么"。"这个功能算谁的"正是老系统最容易吵起来的地方 |
| **权限是不是只写差异** — 与默认不同的才列，可查看 / 可操作分列 | 把 C01 的全局矩阵抄一遍，或合成一列"能做什么" |
| **实现维度是否翻译成业务后果** — 失败留下什么、重复提交怎样、并发怎样、删了还在不在、谁补 | 表格维度是「事务边界 / 锁与并发 / 幂等 / 补偿与降级」原样搬进来，或「逻辑删 `xx084='T'`」 |
| **状态表有没有标流转性质** — 正常 / 逆向 / 异常分得开 | 一条平铺的流转清单，回退与超时混在里面 |
| **§5 有没有数据消费方** — 谁在用这些数据，改动会波及谁 | 不知道改了会炸谁，影响面评估靠猜 |
| **有没有偷偷长出非功能节** — 时效、兼容、并发这类通用要求归 C05；只有改变场景行为的才写成规则或异常 | 模块里单开「非功能与业务约束」表，抄一遍浏览器版本与响应时间 |
| **§7 问题挂没挂场景** — 每条待确认指向一个具体场景 | 全是"系统层面"的问题，落不到谁做什么 |
| **通篇有无"读不懂也没用的段落"** — 产品读完不能做任何决策 | 大段调用链描述、参数逐个解释 |

Verdict per section: `PASS` / `LEAK`（实现细节泄漏）/ `UNUSABLE`（业务语言不成立，需重写整节）。`LEAK` 必须给出改写，不只是抱怨——修的人要能直接粘贴。

## Pass 3 — Completeness: is the rule set whole?

For each scenario and rule area, read the actual implementation and enumerate its branches:

- **Conditional branches** — `if/else`, `switch`, early returns, ternaries on business state.
- **Validation** — required fields, format, range, duplicate checks.
- **Permission** — role checks, ownership checks, org scoping.
- **State guards** — status preconditions before an action.
- **Exception paths** — throws, error returns, silent catches.
- **Boundaries** — null/empty, zero, upper limits, duplicate submit, concurrency.
- **Multi-step writes** — which steps share a transaction; what remains when a later step fails.
- **Compensation & fallback** — retries, manual re-push entries, reconciliation when external calls fail.
- **Side effects** — stock/balance changes, notifications, status cascades, audit trail.

Each branch must resolve to one of:

- a scenario rule (`S<module>-<n>-R<k>`) or a promoted cross-scenario rule (`G<module>-R<k>`), and where the user sees it, an exception row (`S<module>-<n>-E<k>`);
- a step in that scenario's flow, or a function point (`S<module>-<n>-F<k>`) that performs it;
- an acceptance criterion (`AC-…`) the tester can execute by hand;
- an open question in §7, tagged with its scenario;
- an explicit "out of scope" note, when the branch is dead or unreachable.

A branch with none of these is `MISSING` — name it and propose the rule id and wording, in business language.

Then check the cross-cutting items per-scenario reading misses:

- §2 scenario list — every scenario reachable from a real entry appears with role, trigger, goal and priority; scenario relations make sense;
- §4.2 state rows — every state has an exit, every transition a driver and a 流转性质;
- §6 interface rows — every user-reachable operation appears, both directions;
- acceptance criteria — every scenario has at least one scenario-level `AC`; every rule whose violation is user-visible has an `AC` or an exception row; each `AC` states one checkable result;
- §3.x.2 function points — every validation the code performs has a step row with the violation behavior;
- §5 data consumers — every external system, report, or downstream module that reads this data appears;
- §1 out-of-scope — every dead endpoint, unimplemented feature, or externally-owned rule found in the code is named as out of scope rather than silently dropped;
- rule levels — no rule is duplicated across scenario / cross-scenario / global;
- `appendix/03` — every assertion has a row.

**Global docs carry rules too** — check their completeness, not just accuracy:

- `C01` — every role × operation pair the system permits or denies has a matrix row; every boundary (person / org / role) is stated.
- `C02` — every business object the system stores appears; every attribute that carries business meaning has a row; every uniqueness constraint is listed; delete semantics and retention are stated.
- `C03` — every state value in the system appears with a business name; every status-change site appears as a transition.
- `C04` — every user-visible failure has a row; every silent failure is in §2.
- `C05` — every external system, every scheduled action, every retention/timeout commitment is accounted for.
- `C06` — every domain term used in module docs is defined; every real synonym collision is recorded.

## Report format

```markdown
## Accuracy
| # | 文档 | 位置 | 断言 | 结论 | 证据 / 正确值 |
|---|---|---|---|---|---|
| 1 | M02 §3.1.3 | S02-01-R1 上限 5 单 | WRONG | `constants.ts#MAX_PENDING_ORDERS` = 3 |

## Readability
| # | 文档 | 位置 | 问题 | 类型 | 建议改写 |
|---|---|---|---|---|---|
| 1 | M01 §3 | 「aaadaa005 新建 / 006 修改」 | LEAK | 「新建系列 / 修改系列」 |

## Completeness
| # | 模块 | 场景 / 规则 | 缺失的分支 | 建议 |
|---|---|---|---|---|
| 1 | M02 | S02-01 下单 | 未覆盖「优惠券已过期」 | 补 S02-01-R4 + S02-01-E5 |

## Verdict
PASS | FAIL
（FAIL 时列出必须修的条目，按影响面排序）
```

## Rules for the reviewer

- Report only. Do not edit the docs, do not rewrite the code.
- Never infer a rule from a function name. If the branch is not in the code, it is not a rule.
- Do not report wording polish, table formatting, or typo-level style — this review judges truth, readability as a business document, and coverage. A prettier sentence that still says `adaa084='T'` is not a fix.
- When readability and accuracy conflict, accuracy wins — but say so; an accurate sentence product cannot read is still a defect to be rewritten, not kept.
- If a whole document is unverifiable (wrong module, missing source), say so at the top; that outranks individual row findings.
