# Review brief — accuracy first, completeness second

Hand this brief to a **fresh-context** sub-agent together with the generated `docs/reverse-prd/` tree and repo access. Never give it the generation chat: the agent that wrote the docs will defend them.

Two passes, strictly in order. Accuracy first — a complete rule set built on invented behavior is worse than a gap.

## Pass 1 — Accuracy: is every line true?

Walk each document **row by row**. Not "does this look right" — open the code and check the claim.

| Document | What to verify row by row | Where to look |
|---|---|---|
| `common/01-roles-permission` | every role, every permission check position, every visible-scope claim, the check order | middleware, guards, decorators, resource-ownership queries |
| `common/02-data-dictionary` | every field's type, nullability, default, constraint; every index name | entity/model definitions, migrations, schema |
| `common/03-enums-states` | every enum value and its code value; every legal transition | enum/constant definitions, status-change call sites |
| `common/04-errors` | every error code's trigger condition and throw site; the handler chain | `throw` sites, exception classes, global error handler |
| `common/05-nfr` | every config key, rate limit, TTL, cron schedule, third-party call | config files, job definitions, integration clients |
| `common/06-glossary` | every term's code spelling exists; every name collision in code is listed | constants, class and field names |
| `modules/M<nn>` | every `F` function's trigger and main flow; every `R` rule; every error code; every API row; every field in §6; every transition in §5 | controllers → services → jobs → validators |
| `appendix/01-open-questions` | every item still exists in its source doc; the blast-radius ranking and the assignee | module §9, common `待确认` |
| `appendix/02-uncovered` | every uncovered route really has no module; the reason and risk level hold | route table, controller list |
| `00-overview` | module↔route mapping, coverage counts, CodeMap links | route table, `docs/codemap/` |

Verdict per row — one of three:

- `CONFIRMED` — the code says exactly this. Cite `file#function`.
- `WRONG` — the code says otherwise. State the correct value and its evidence.
- `UNVERIFIABLE` — you could not find evidence either way. The doc must demote it: drop the confidence to `低` or move it to §9 as an open question. An unmarked guess is a defect.

Also check the meta-claims, which are the easiest to fake: does every module header link to a CodeMap that actually exists? Does every document link in `00-overview` resolve, and does every module row point at an existing module doc? Does the version block name a real commit?

## Pass 2 — Completeness: is the rule set whole?

For each function listed in §3/§4, read the actual implementation and enumerate its branches:

- **Conditional branches** — `if/else`, `switch`, early returns, ternaries on business state.
- **Validation** — parameter checks, format checks, range checks, required-field checks.
- **Permission** — role checks, ownership checks, tenant/org scoping.
- **State guards** — status precondition asserts before an action.
- **Exception paths** — `throw`, `try/catch`, error-code returns, fallbacks.
- **Boundaries** — null/empty, zero, upper limit, duplicate submit, idempotency, concurrency.
- **Side effects** — stock/balance changes, notifications, delayed jobs, audit logs.

Each branch must resolve to one of:

- a numbered rule (`R<module>-<n>`) in the module doc, and where user-visible, an error code in the module's exception table;
- an open question in §9, when the branch's intent is genuinely unclear;
- an explicit "out of scope" note, when the branch is dead or unreachable code.

A branch with none of the three is `MISSING` — name it and propose the rule id and wording.

Then check the cross-cutting rules that per-function reading misses: state machine rows in §5 (every state must have an exit, every transition a driver), and API rows in §7 (every controller method must appear).

**Global docs carry rules too** — check their completeness the same way, not just their accuracy:

- `C01` — every role × operation pair that the code permits or denies has a matrix row; every check in the auth chain appears as a judgement position.
- `C02` — every validation the code performs has a field constraint; every index and unique key in the migrations is listed.
- `C03` — every enum value in code appears; every status-change call site appears as a transition.
- `C04` — every business `throw` has a code row; every code row has a real throw site.
- `C05` — every config key the code reads is listed; every scheduled job, rate limit and cache TTL is accounted for.
- `C06` — every listed term's code spelling exists; every synonym collision found in code is recorded.

## Report format

```markdown
## Accuracy
| # | 文档 | 位置 | 断言 | 结论 | 证据 / 正确值 |
|---|---|---|---|---|---|
| 1 | M02 §4 R02-01 | 上限 5 单 | WRONG | `constants.ts#MAX_PENDING_ORDERS` = 3 |

## Completeness
| # | 模块 | 功能 | 缺失的分支 / 规则 | 建议 |
|---|---|---|---|---|
| 1 | M02 | F02-01 创建订单 | 未覆盖"优惠券已过期"分支 | 补 R02-04，错误码 E2005 |

## Verdict
PASS | FAIL
（FAIL 时列出必须修的条目，按影响面排序）
```

## Rules for the reviewer

- Report only. Do not edit the docs, do not rewrite the code.
- Never infer a rule from a function name. If the branch is not in the code, it is not a rule.
- Do not report style issues, wording, or formatting — this review judges truth and coverage only.
- If a whole document is unverifiable (wrong module, missing source), say so at the top; that outranks individual row findings.
