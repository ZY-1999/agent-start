---
name: reverse-prd
description: "Reverse-engineer a multi-document PRD from an existing codebase, split by business capability and written for product, not for developers. Use it when a legacy or undocumented system needs a PRD before it can be maintained, tested, or replaced, when taking over someone else's code, when preparing handoff docs for product, QA, new joiners, or an outsourcing team, or when the user says 逆向PRD / 从代码反推PRD / 给这个系统补文档 / 按模块拆分PRD / 这份PRD太像开发文档. For a code-terrain index alone, use /codemap instead."
---

# Reverse PRD

## Core Position

Turn an existing codebase into a **product PRD system**: multi-document, split by business domain, organized by business scenario, written for the people who decide what the system should do — not for the people who built it.

Four things define this skill: who reads it, how it is organized, how it is split, and how it stays honest.

### 1. Reader contract — code is evidence, not content

The document's only readers are product managers, business owners, and QA. The code is the **source you verify against**; it is never the **subject you write about**.

The test for every paragraph: **if the implementation were rewritten tomorrow, would this paragraph still be true?** A paragraph that dies with the rewrite is describing implementation, not business.

What belongs in the body: **业务** (what the system does for whom, under what rules), **场景** (end-to-end business lines and what happens when they go wrong), **对外接口** (who calls in, what they get, what to do when it fails; which external systems this depends on and what their failure means), **数据存储结构** (which business objects exist, their attributes, relations, origin, lifetime, owner).

What does not, in business prose: implementation detail of any kind — table and column names, class and method names, annotations, framework names, message codes, config keys, SQL, log lines, endpoint numbers used as feature names. The ban list, whitelist, and the four mandatory rewrites (transaction/lock/idempotency/compensation → business continuity; soft-delete flag → what the user can still see; error code → condition + message + consequence; endpoint list → scenario) are in [business-voice](./references/business-voice.md). Load it before writing any body text.

Three structured, implementation-side artifacts are the deliberate exception to that ban — they exist precisely so the physical facts stay *out* of the prose: (1) the per-module **interface structure file** under `interfaces/` (request/response structures, provided + depended), (2) the derived **DDL SQL file** in `common/`, and (3) the **traceability ledger** in `appendix/03`. Physical names live only in those three artifacts; they never leak back into scenario, rule, or acceptance text.

### 2. Organize by business scenario, not by feature or endpoint

A module is only a **container**; the unit of the document is the **scenario** — who, under what condition, wants to accomplish what, under which rules, and how you know it worked. Everything else hangs off it:

- rules, function points, exceptions and acceptance criteria are numbered under their scenario (`S02-01-R1`, `S02-01-F1`, `S02-01-E1`, `AC-S02-01-1`) and live in that scenario's section;
- a rule used by two or more scenarios is promoted to the module's cross-scenario table (`G02-R1`); a rule used by the whole system goes up to `common/`. **Promote, never copy.**
- a scenario is the unit testers design end-to-end cases against, and then drill into function points.

### 3. Split by business domain, with one truth per fact

Splitting creates the dominant failure mode: **duplication and drift**. A permission matrix, a data dictionary, or a term table copied into ten module docs means ten places to edit. Defend with a three-layer split:

- **Global layer** (`common/`) — written once, referenced from everywhere.
- **Module layer** (`modules/`) — a domain holding its scenarios; only what is unique to it.
- **Overview layer** (`00-总览.md`) — navigation, the end-to-end business flow across domains, coverage accounting, reading order. It indexes documents, not code.

### 4. Traceability lives in a ledger, not in the prose

Reverse-engineering needs proof: every business claim must be checkable against the code. But proof columns in the body turn a PRD into a code report. So the proof moves to `appendix/03-溯源台账.md` — one row per assertion (scenario, `S…-R` / `-F` / `-E`, `G` rule, `Q`, attribute group) mapping to a file + function. The body stays clean; the ledger keeps the chain intact for the next incremental update and for the reviewer.

Deleting the ledger must leave a perfectly readable PRD. That is the design constraint.

Code terrain is **not** this skill's job. `/codemap` owns it: this skill consumes CodeMaps to verify facts and never grows a second, competing code index. Every module gets its own feature CodeMap — used for verification, never quoted into the document.

## When To Use

- A legacy or undocumented system needs a PRD before it can be maintained, tested, or replaced.
- Taking over someone else's code and needing to know what it actually does, in business terms.
- Handing a system to product, QA, new joiners, or an outsourcing team and needing a document they can actually read.
- The user says: 逆向 PRD、从代码反推 PRD、给这个系统补文档、按模块拆分 PRD、这份 PRD 太像开发文档 / 我要给产品看的版本.

## When Not To Use

- The requirement is not built yet → `/idea-to-prd`.
- Only the design needs sharpening, no document artifact → `/improve-architecture` or `/codebase-design`.
- One module, one question → read the code, answer it, skip the doc system.
- `docs/reverse-prd/` already exists → incremental update (Workflow, Incremental mode), not a fresh build.

## Output Shape

```text
docs/reverse-prd/
├── 00-总览.md                  # PRD-01: business flow, domain map, scenario counts, coverage
├── common/                     # global layer, written once
│   ├── 01-角色与权限.md        # PRD-C01
│   ├── 02-数据字典.md          # PRD-C02: business objects, attributes, lifecycle
│   ├── 03-枚举与状态机.md      # PRD-C03: states, transitions, allowed actions
│   ├── 04-错误码.md            # PRD-C04: conditions, messages, consequences, silent failures
│   ├── 05-非功能需求.md        # PRD-C05: external systems, timeliness, scheduled actions
│   ├── 06-术语表.md            # PRD-C06
│   └── 07-表结构DDL.sql        # implementation-side derived DDL (NOT product content, no PRD id)
├── modules/
│   ├── M01-用户认证.md          # PRD-M01
│   └── M02-订单.md              # PRD-M02
├── interfaces/                 # implementation-side request/response structures, one file per module
│   └── M02-订单-接口结构.md     # 对外接口 + 依赖接口 + 已废弃接口，三节（NOT product content, no PRD id）
└── appendix/
    ├── 01-全局待确认问题.md    # PRD-A01: every §7 + every common 待确认, ranked by blast radius
    ├── 02-未覆盖模块.md         # PRD-A02: capabilities with no module
    └── 03-溯源台账.md           # PRD-A03: assertion → position → verdict (NOT product content)
```

File names follow the content's language: Chinese content gets Chinese file names (`01-角色与权限.md`, `M02-订单.md`) — never an English name over Chinese content. The ordering prefix (`00-`, `01-`, `M02-`) stays; directory names (`common/`, `modules/`, `interfaces/`, `appendix/`) stay English. Full rule in [conventions](./references/conventions.md).

## Workflow

**Read-only rule**: the whole run writes only under `docs/reverse-prd/` and `docs/codemap/`. Never touch business code or config — this skill produces documents, not patches.

### 0. Pin the code baseline — both sides, and stop to ask if one is missing

A PRD needs both halves of the system. **Backend** gives rules, states, data and interfaces; **frontend** gives entries, menu hierarchy, field labels, client-side validation and the exact messages users see. Documenting from one half alone produces a document that is confidently wrong about the other — the tell-tale symptoms are an 「何地（入口）」 guessed from an endpoint path, and 「文案由平台统一维护」 printed over a message that was sitting in an i18n file.

So, before writing anything:

1. **Record what you were given.** `git rev-parse --short HEAD` for each repo in scope. It lands in every version block and in the ledger header — a real commit, never a placeholder.
2. **Detect the missing side.** Backend-only signs: controllers, entities, mappers, no pages, no router, no i18n. Frontend-only signs: pages, router, store calling APIs, with no server code in the workspace.
3. **Ask — do not guess.** When exactly one side is present, stop and ask for the other repo (path or git URL) before writing a single document. Ask once, concretely: 「这套代码只有后端，前端在哪个仓库？给路径或地址。如果前端不在我们手里（外部团队做、已下线、拿不到），我就只用现有代码生成，并把入口与文案类结论标成未核实。」 The frontend-only case is symmetric — ask for the backend repo, and say up front that rules, states and data structures cannot be recovered without it.
4. **Ask for the database DDL — the real thing outranks reverse-engineering.** `common/07-表结构DDL.sql` and the physical half of C02 have two possible sources, and the difference is trust. Ask once, concretely: 「能否提供数据库 DDL（建表 SQL / schema 导出 / migration 文件）？能提供我就以它为准逐字段核对；提供不了我再从实体类、Mapper 与 migration 脚本反推，并把整份 DDL 标成『反推、未与真实库核对』。」 Provided DDL is the authoritative source — C07 is based on it, and C02/C03 attributes, states and encodings are verified against it instead of inferred. Without it, every uncertain field or constraint gets a `-- 待确认` marker and a Q entry, and the derived DDL must never be mistaken for the real schema.

| Situation | What to do |
|---|---|
| Both repos available | Explore both. Derive module boundaries by aligning **frontend menu/route tree ↔ backend controllers ↔ data tables**. Take scenario entries and user-facing messages from the frontend. |
| User says the frontend is unavailable | Generate from the backend only. Every entry, menu-level, field-label and message claim drops to `低` with a Q in §7; state 「仅后端代码，前端入口与文案未核实」 in the overview's coverage row. |
| Frontend only | Say the consequence first: rules, states and data structures will be largely missing, so the docs can only cover entries and interaction. Proceed only after the user agrees; park the missing half in `appendix/02`. |
| Neither side readable (only build artifacts) | Stop. Ask for readable source. |

Completion condition: a baseline is recorded for every repo in scope, the missing side is either supplied or explicitly declared unavailable (with the consequence written into the overview's coverage row), and the DDL source is settled — provided, or explicitly declared unavailable and the derived DDL marked 「反推」.

### Incremental mode

`docs/reverse-prd/` already existing means an incremental update, not a fresh build. After pinning the new code baseline (step 0): drift-check the existing CodeMaps instead of rebuilding them; rewrite only the module and common documents whose facts moved; refresh the overview's coverage accounting and `appendix/` last; re-sync the ledger rows for everything you touched. Re-verify untouched documents against the maps and move their version block to the new baseline — the block moves even when the content did not.

Step 2's order is a fresh-run rule: never regenerate C01–C06 wholesale in incremental mode.

If a document written before this skill's product-facing convention carries implementation detail in its body (table names, method names, endpoint numbers), rewriting it into business language is part of the update — not cosmetics. The same pass renames English file names over Chinese content and fixes the links.

A repo that arrived after the first pass (typically the frontend) is an incremental update of its own: re-derive entries and messages, then re-check every scenario's 「何地」 and exception prompts against it.

Stop and ask the user when the drift check cannot tell whether a fact moved (map node missing, or the code moved in a way the map does not cover) — do not guess between "unchanged" and "rewrite".

Completion condition: every touched document's version block names the new commit(s); untouched documents re-verified against their maps; overview coverage, ledger, and `appendix/` refreshed.

### 1. Build and verify the project CodeMap

Terrain first, documents second — the global layer is built from the same code the map describes.

- No map yet → `/codemap` in `project` mode → `docs/codemap/<project>.md`.
- Map exists → `/codemap` `drift-check` first.
- Both repos in scope → one project map per repo. The frontend map only needs its page/route tree and the API calls each page makes; the page↔endpoint mapping belongs in each module's feature map.

Then **verify before you write**. Spot-check the nodes you are about to rely on against the actual code — entry pages, controllers, table names, config keys. Fix the map (or read the source directly) before step 2.

This step is verification, not documentation: the map never gets quoted into the PRD body.

Completion condition: a project map exists for every repo in scope, its entry / capability / dependency nodes match the code, and every node you plan to rely on is confirmed.

### 2. Global layer first — the order is not negotiable (fresh runs)

Produce `common/` C01–C06 **before** any module doc: roles and visibility, business objects and attributes, states, exceptions and messages, external systems and timeliness, terms. Modules reference these; reversing the order guarantees rework.

Read schema, permission middleware, enums, error classes, and config as **sources**. If the user provided the DDL in step 0, it is the authoritative schema source — verify C02 attributes, C03 encodings, and C07 against it; otherwise reverse-engineer from entities / mappers / migration scripts and mark the derived DDL 「反推」. Write the results in business language: a column becomes an attribute with a business meaning, a thrown code becomes a condition plus what the user sees, a transaction annotation becomes a statement about what survives a failure.

Where the business meaning cannot be recovered, write 「待确认属性 N」 and open a Q entry — never the physical name.

Completion condition: C01–C06 exist, each has its version block, every attribute has a business name or an explicit 待确认 marker, and every table row that states a rule has a ledger row.

### 3. Split by business domain, inventory the scenarios, occupy the overview

Grab the menu tree and pages, map them to `M01`–`M0N` **using business domain names** (系列管理, 产品配置与编码) — never route prefixes or controller names (`/aaadaa/*`, AaadaaController). Routes are the evidence that the domain exists; they are not the label.

With both repos in hand, the split is decided by a three-way alignment — frontend menu entry ↔ backend controllers ↔ data tables — written down as a mapping table before any module doc. Where the three disagree (a menu with no endpoint, an endpoint with no page), that gap is a finding, not something to smooth over: it usually means a dead feature or a capability reachable only from somewhere you have not found yet.

Then, for each domain, list the scenarios you expect to find: name, role, trigger condition, business goal, priority. Write the **domain list plus each domain's expected scenario count** into `00-总览.md` with status `占位` — coverage stays visible at domain granularity; the per-scenario inventory lives in each module's §2 scenario map, so the overview never duplicates it (a second copy is a second truth).

Completion condition: every user-reachable capability carries an `M` id (or is parked for `appendix/02`), every expected scenario carries an `S` id in its module's §2 map, and every domain is marked `占位` in the overview.

### 4. Close each module — scenario by scenario

Each module gets its own feature CodeMap (`/codemap` feature mode → `docs/codemap/M<nn>-<模块名>.md`). Verify it the same way as step 1. No shared or cross-module "code index" document: N modules means N maps.

Then write the module doc **scenario by scenario**, six sub-items per scenario:

1. **场景描述** — 谁 / 何时 / 何地（入口）/ 做什么 / 为什么（业务价值）/ 前置条件 / 后置结果. Take 「何地」 from the frontend route or menu path, never from an endpoint path; if only the backend is available, say so and mark the entry as unverified. If you cannot name the entry at all, you have not found the business line yet.
2. **场景流程（含功能点）** — ordered steps: who acts, what the system does, what changes in the data. Each step is a business action; a function point `S<module>-<n>-F<k>` hangs off the step that performs it (input and key validation as two columns), rather than a separate section. Branch exits point at an exception id, not at a second diagram.
3. **业务规则** — `S<module>-<n>-R<k>`, only this scenario's. Two or more scenarios sharing a rule → promote to §4.1 `G<module>-R<k>`.
4. **异常与边界** — `S<module>-<n>-E<k>`: trigger, system behavior, **what the user sees** (write 「无提示」 when it is silent), business impact.
5. **权限与可见性** — only where it differs from the domain default; can-view and can-operate stay separate columns.
6. **验收标准** — two layers: scenario-level `AC-<scenario>-<n>` proving the end-to-end line and its post-condition, then function-point / exception level `AC-<scenario>-F<k>` / `-E<k>`.

Then the module-level sections: §1 domain positioning with out-of-scope, §2 scenario map (roles, scenario list with priority, scenario relations), §4 cross-scenario rules and the state machine with 流转性质, §5 business data with consumers, §6 interfaces both directions, §7 open questions tagged with their scenario. Non-functional constraints are **not** a module section — they live in C05; only the ones that change a scenario's behavior become a rule or an exception row there.

When writing the interface structure file, separate **deprecated interfaces** from live ones: an interface carrying a `/** @ignore */` marker **and** no frontend page/route calling it is deprecated — list it in the file's 「已废弃接口」 section (interface id + evidence + reason), never among the provided/depended interfaces. One condition alone is not enough: a marker with a live page, or a page-less interface without the marker, is not deprecated.

Record every assertion in the ledger as you go — reconstructing it later produces invented evidence.

A capability with no independent business line (pure CRUD, no cross-role or cross-system behavior) does not need its own scenario: cover it as a function point inside the nearest scenario, or as a `G` rule.

Completion condition per module: its CodeMap was used for verification; every scenario has all six sub-items; every rule sits at exactly one level (scenario / cross-scenario / global); every scenario has both acceptance layers; §7 items carry a scenario id; ledger rows for every assertion.

### 5. Aggregate open questions, coverage, and the ledger

Collect every module's §7 and every common doc's `待确认` into `appendix/01-全局待确认问题.md`, ranked by blast radius, **tagged with the scenario it belongs to**, and assigned to whoever can actually answer (product for intent and business rules, dev for implementation, platform admin for messages and permissions). A question with no scenario is a question that never landed on a business line — send it back, except platform-level C04/C06 questions (global messages, shared constraints), which are tagged `全局`.

Account for coverage twice: every user-reachable capability resolves to an `M` id, and every scenario in the overview's inventory resolves to a written section. Whatever does not goes to `appendix/02-未覆盖模块.md` with the reason and a risk level.

Then finish `appendix/03-溯源台账.md`: every assertion has a row, every row has a real position, every `未找到依据` item has been demoted to `低` and given a Q entry. The physical mapping table (§3) and the code-value mapping (§4) are what let implementation-side readers connect business names to storage without polluting C02.

Completion condition: A01 ranks every open question, tags its scenario, and links back to its module; A02 gives a reason and risk level for every uncovered capability or scenario; A03 covers every assertion with no empty position; `00-总览.md` flips finished domains from `占位` to `已完成` and recomputes the scenario counts — a tree left at `占位` is an unfinished run.

### 6. Adversarial review — accuracy, then readability, then completeness

The agent that wrote the docs cannot grade them. Spawn **one fresh-context sub-agent** with the generated tree and repo access — never the generation chat — and hand it [review-brief.md](./references/review-brief.md), which carries the three-pass checklist, the verdict vocabulary, and the report format. The sub-agent must be **read-only and code-capable** — use the environment's code-exploration type (e.g. `code-explorer`); it reads code to verify claims and reports defects, never edits docs or code.

In incremental mode, scope the review to the documents this pass rewrote.

Three passes, in this order:

1. **Accuracy** — every claim checked against the code through the ledger; unverifiable claims demoted, not shipped as facts.
2. **Readability** — a mechanical sweep for implementation leakage (the pattern list is in the brief), then a substance check: are scenarios the backbone, are rules arguable, are interfaces in product terms, are implementation concerns translated. Every `LEAK` comes with the rewrite, not just the complaint.
3. **Completeness** — every real branch in the traced code resolves to a rule, a scenario step, an `AC`, an open question, or an explicit out-of-scope note.

Defects → fix the docs and re-run the same sub-agent. Past **3 rounds**, stop and move the unresolved items into `appendix/01`, then say so in the hand-off.

Completion condition: the reviewer reports PASS on all three passes.

### 7. Self-check before declaring done

Run the Quality Gate below. Fix what you can; what genuinely can't be fixed here (e.g. a repo the user declared unavailable) is surfaced, never silently noted.

After the reviewer returns, re-run the mechanical sweep in [review-brief Pass 2](./references/review-brief.md) to confirm every fix landed — it saves a round.

Completion condition: every Quality Gate item is green; anything still red is reported to the user as a known gap, not silently shipped.

## Split Granularity

| Criterion | Rule |
|---|---|
| One top-level menu / one business domain | One module doc — the default |
| A domain carrying more than ~12 scenarios, or a doc > 1500 lines | Split by sub-capability with fresh sequential ids (`M06-订单-下单.md`, `M07-订单-退款.md`) — never reuse the old id (see conventions) |
| A capability with no independent business line | Do not give it a scenario: make it a function point inside the nearest scenario, or a `G` rule |
| Whole-tree document count | ~11 fixed docs (00-总览 + 6 common + DDL + 3 appendix) plus up to ~20 module docs — a **signal, not a cap**, see the precedence rule below |

The split trigger and the whole-tree count collide on a large system: applying the 12-scenario / 1500-line split honestly can push past 20 module docs. When they do, decide in this order:

1. **Single-doc maintainability wins.** The 12-scenario / 1500-line split is a hard trigger — satisfy it first. Never stuff scenarios back into an oversized doc just to hit a count.
2. **Re-examine the split before grouping.** Over the ~20-module budget, first check whether the split is too fine: merge "pseudo-domains" that were split along code directories or route prefixes instead of business capability, and sink capabilities with no independent business line into `G` rules or function points.
3. **Group in the overview, not in the documents.** Still over budget, add a coarser business-domain grouping layer to `00-总览` (e.g. 「订单域组 = M02 下单 + M03 退款」) for navigation. Grouping is an index-level navigation merge — it never merges document content back into one file.

## Authority — one truth per fact

Each fact has exactly one authoritative location; every other place may only reference it. The full table is in [conventions](./references/conventions.md). The rules that matter most:

- A module doc lists only *the roles, attributes, exceptions, and constraints this module touches*, then links out. It never copies a global table.
- Cross-module behavior is described in the **initiating** module; the affected module writes one line — "driven by the M03 callback, see M03 §3.x" — and stops there.
- Code positions and physical names exist in exactly three artifacts — the per-module `interfaces/` structure files, `common/07-表结构DDL.sql`, and `appendix/03-溯源台账.md`. A code reference anywhere in the business prose is a defect, not a nicety.

## Numbering

Document ids: `PRD-01` (overview), `PRD-C01`–`PRD-C06` (common), `PRD-M01`–`PRD-M0N` (modules), `PRD-A01`–`PRD-A03` (appendix). Module, scenario, rule, acceptance-criterion, and open-question ids are defined in [conventions](./references/conventions.md).

Every document opens with a version block directly under its H1. The bump rules and the no-changelog rule are defined once in [conventions](./references/conventions.md); the templates show the block as a literal example and do not restate the rules.

## Quality Gate

- [ ] Every **product** document (00 / C01–C06 / M* / A01–A02) has its PRD id and version block; implementation-side files (`interfaces/`, `common/07-表结构DDL.sql`, `appendix/03`) carry a version block but no PRD id. The version names a real commit for **every repo in scope** (frontend and backend when both were available).
- [ ] The missing half is accounted for: either the other repo was explored, or the user declared it unavailable and every entry / menu / label / message claim is demoted to `低` with a Q, and the overview's coverage row says so.
- [ ] File names match the content's language (Chinese content → Chinese names, ordering prefix kept, directory names English).
- [ ] **No implementation detail in any business prose** — the ban list is [business-voice §2](./references/business-voice.md); code positions and physical names appear only in the `interfaces/` structure files, `common/07-表结构DDL.sql`, and `appendix/03`.
- [ ] Module bodies are **scenario-driven**: §2 lists every scenario with role, trigger, business goal and priority; §3 gives each scenario all six sub-items (描述 / 流程（含功能点） / 规则 / 异常 / 权限 / 验收).
- [ ] Each scenario's **场景流程** states input (business elements), processing, output, key validation and data change per step, with function points `S…-F` hanging off their steps; its exception table carries **what the user sees** (`无提示` where silent).
- [ ] Acceptance criteria come in **two layers** per scenario — scenario-level end-to-end with post-condition, then function-point / exception level — each one checkable result, numbered under its scenario.
- [ ] Every rule sits at exactly **one** level: scenario (`S…-R`), cross-scenario (`G…-R`), or global (`common/`). No rule is copied between levels.
- [ ] Module structure is complete: §1 positioning with out-of-scope, §4 cross-scenario rules and the state machine with 流转性质, §5 business data with **who consumes it**, §7 open questions tagged with their scenario; no non-functional section (that belongs to C05).
- [ ] Every rule is stated so a business owner can argue it, and says what happens when it is violated.
- [ ] Every implementation-shaped concern (transaction, lock, idempotency, compensation, soft delete) is translated into a business consequence, not carried over in developer vocabulary.
- [ ] §6 covers **both directions** — interfaces provided (business name, caller, purpose, request/response essentials, failure convention) and external systems depended on (failure impact, remedy).
- [ ] C01–C06 are business-language throughout: C02 states delete semantics, retention, source-of-truth, reconciliation and consumers; C04 states what the user sees plus a **silent-failure** section; C05 states external failure impact, timeliness, scheduled actions, compatibility and sensitive-data constraints.
- [ ] Every assertion has a row in `appendix/03` with a real position; every `未找到依据` item is demoted to `低` and has a Q entry.
- [ ] Confidence is marked at module level; every rule that is less certain carries an inline `（推断）` / `（未确认）` marker and a Q entry; no guess is written as a fact.
- [ ] Every open question is aggregated into `appendix/01` with its scenario tag and phrased so a product owner can answer it; every user-reachable capability maps to an `M` id and every inventoried scenario to a written section, or both are listed in `appendix/02` with a reason and risk level.
- [ ] No global table is duplicated inside a module doc, and no CodeMap link or code terrain appears in module bodies — the overview carries a single 工程侧资料 section (codemap / interfaces / DDL / A03).
- [ ] Every branch in the traced call chain (validation, permission, state guard, exception, boundary, side effect) resolves to a rule, a scenario step, an `AC`, an open question, or an explicit out-of-scope note.
- [ ] Relative links resolve, and the fresh-context reviewer reports PASS on accuracy, readability, and completeness.

## After the PRD tree

The PRD is the entry ticket for handover, not the end state:

- `appendix/01` is ready-made input for the QA pipeline — route it to `/qa-clarify` / `/qa-cases`. Items still at `低` confidence are probe material; the rest need people.
- The scenarios in §3 and their `AC` translate almost directly into QA cases — a scenario that cannot produce a testable case was probably written from an endpoint, not from a business line.
- First changes after handover should start low-risk (logging, validation, clear-cut bug fixes) and go through `/to-spec` / `/sdd-flow` — the generated PRD feeds `/to-spec` directly.
- Index the PRD tree in the target repo's AGENTS.md so later conversations start from the documents instead of re-reading the code.

## References

- [business-voice.md](./references/business-voice.md) — the ban list, whitelist, and rewrite table. Load before writing any body text.
- [overview-template.md](./references/overview-template.md) — `00-总览.md` skeleton.
- [module-template.md](./references/module-template.md) — the 7-section module doc, six sub-items per scenario (功能点 merged into 场景流程).
- [interface-template.md](./references/interface-template.md) — the per-module interface structure file (对外接口 + 依赖接口, jsonc request/response).
- [common-templates.md](./references/common-templates.md) — the six global-layer documents.
- [appendix-templates.md](./references/appendix-templates.md) — open questions, uncovered capabilities, and the traceability ledger.
- [review-brief.md](./references/review-brief.md) — the fresh-context reviewer's three-pass brief.
- [conventions.md](./references/conventions.md) — authority map, language contract, numbering, traceability, confidence, and version rules.
