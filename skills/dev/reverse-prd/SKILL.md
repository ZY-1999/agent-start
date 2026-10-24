---
name: reverse-prd
description: "Reverse-engineer a multi-document PRD system from an existing codebase, split by functional module. Use it when a legacy or undocumented system needs a PRD before it can be maintained, tested, or replaced, when taking over someone else's code, when preparing handoff docs for QA, new joiners, or an outsourcing team, or when the user says 逆向PRD / 从代码反推PRD / 给这个系统补文档 / 按模块拆分PRD. For a code-terrain index alone, use /codemap instead."
---

# Reverse PRD

## Core Position

Turn an existing codebase into a **multi-document PRD system** split by functional module. The goal is not one large document; it is a set of documents that stays correct after the code moves.

Splitting by module creates one dominant failure mode: **duplication and drift**. A permission matrix, a data dictionary, or an error-code table copied into ten module docs means ten places to edit. Defend with a three-layer split:

- **Global layer** (`common/`) — written once, referenced from everywhere.
- **Module layer** (`modules/`) — only what is unique to that module.
- **Overview layer** (`00-overview.md`) — navigation, coverage accounting, reading order. It indexes the documents, not the code. No business content.

Code terrain is **not** this skill's job. `/codemap` owns it: this skill consumes CodeMaps and links to them, and never grows a second, competing code index. Every module gets its own feature CodeMap — one map per module, no shared "code index" document inside the PRD tree.

## When To Use

- A legacy or undocumented system needs a PRD before it can be maintained, tested, or replaced.
- Taking over someone else's code and needing to know what it actually does.
- Handing a system to QA, new joiners, or an outsourcing team and needing executable documentation.
- The user says: 逆向 PRD、从代码反推 PRD、给这个系统补文档、按模块拆分 PRD.

## When Not To Use

- The requirement is not built yet → `/idea-to-prd`.
- Only the design needs sharpening, no document artifact → `/improve-architecture` or `/codebase-design`.
- One module, one question → read the code, answer it, skip the doc system.
- `docs/reverse-prd/` already exists → incremental update (Workflow, Incremental mode), not a fresh build.

## Output Shape

```text
docs/reverse-prd/
├── 00-overview.md              # PRD-01: module list, coverage, reading order
├── common/                     # global layer, written once
│   ├── 01-roles-permission.md  # PRD-C01
│   ├── 02-data-dictionary.md   # PRD-C02
│   ├── 03-enums-states.md      # PRD-C03
│   ├── 04-errors.md            # PRD-C04
│   ├── 05-nfr.md               # PRD-C05
│   └── 06-glossary.md          # PRD-C06
├── modules/
│   ├── M01-用户认证.md          # PRD-M01
│   └── M02-订单.md              # PRD-M02
└── appendix/
    ├── 01-open-questions.md    # PRD-A01: every module's §9 + every common doc's 待确认, ranked by blast radius
    └── 02-uncovered.md         # PRD-A02: routes/controllers with no module
```

The PRD tree carries no code index. Code terrain lives in `docs/codemap/` — one project map plus one feature map per module (`docs/codemap/M02-订单.md`). Two indexes exist and they index different things: `/codemap` indexes the **code**, `00-overview` indexes the **documents**. Never let one grow into the other.

## Workflow

### 0. Pin the code version — stop and ask if unknown

Record the code version (`git rev-parse --short HEAD`, or branch + tag) before writing anything. It lands in every document's version block, so it must be a real commit — never a placeholder.

Stop and ask the user when any of these is unresolved:

- the code version is ambiguous (dirty tree, no git, several deployable services);
- the scan scope is unclear (monorepo with unrelated services, or a module that is partly in another repo);
- the module boundary cannot be aligned with the menu/route tree.

Completion condition: a real commit hash is recorded, and each of the three unknowns above is either answered or explicitly parked as an open question.

### Incremental mode

`docs/reverse-prd/` already existing means an incremental update, not a fresh build. After pinning the new code version (step 0): drift-check the existing CodeMaps instead of rebuilding them; rewrite only the module and common documents whose facts moved; refresh the overview's coverage accounting and `appendix/` last. Re-verify untouched documents against the maps and move their version block to the new code version — the block moves even when the content did not (see the version rules). Step 2's order is a fresh-run rule: never regenerate C01–C06 wholesale in incremental mode.

Stop and ask the user when the drift check cannot tell whether a fact moved (map node missing, or the code moved in a way the map does not cover) — do not guess between "unchanged" and "rewrite".

Completion condition: every touched document's version block names the new commit; untouched documents re-verified against their maps; overview coverage and `appendix/` refreshed.

### 1. Build and verify the project CodeMap

Terrain first, documents second — the global layer is built from the same code the map describes, so an unverified map poisons both.

- No map yet → `/codemap` in `project` mode → `docs/codemap/<project>.md`.
- Map exists → `/codemap` `drift-check` first.

Then **verify before you write**. Spot-check the nodes you are about to rely on against the actual code — entry routes, controller methods, table names, config keys. A stale node copied into a PRD becomes a false fact, and a false fact is worse than a gap. Fix the map (or read the source directly) before step 2; never document around a node you could not confirm.

Completion condition: the project map exists, its entry / module / dependency nodes match the code, and every node you plan to cite is confirmed.

### 2. Global layer first — the order is not negotiable (fresh runs)

Scan schema, permission middleware, enums, and error classes to produce `common/` C01–C06 **before** any module doc. Modules are written against these as references; reversing the order guarantees rework.

Completion condition: C01–C06 exist, each has its version block, and every row in a table that has an evidence column carries evidence.

### 3. Split by route tree, occupy the overview

Grab routes and menu entries, map them to `M01`–`M0N`, and write the module list into `00-overview.md` **with placeholders** — module name and route, no content yet. Placeholders make the coverage gap visible while you work.

Completion condition: every route and controller method carries an `M` id (or is parked for `appendix/02`), and `00-overview.md` lists all of them with status `占位`.

### 4. Close each module — CodeMap first, doc second

Each module gets its own feature CodeMap: `/codemap` in `feature` mode, written to `docs/codemap/M<nn>-<模块名>.md`, covering entry → controller → service → data → tests. Verify it the same way as step 1 before filling the template — a module doc citing an unverified map is a guess with a link. No shared or cross-module "code index" document: N modules means N maps.

Then walk the chain entry → controller → service → data fields → test cases and fill the module template. Every function gets a `F<module>-<n>` id, every rule an `R<module>-<n>` id. Evidence cites file + function, plus the CodeMap node for the deeper chain.

Completion condition per module: its own CodeMap exists and is linked from the header; entry table, role view, function list, per-function detail with rule table, state machine, data usage, API list, NFR notes, open questions.

### 5. Aggregate open questions and coverage

Collect every module's §9 and every common doc's `待确认` into `appendix/01-open-questions.md`, ranked by blast radius. Then account for coverage: **every route and every controller method must resolve to an `M` id**. Whatever does not goes to `appendix/02-uncovered.md` with the reason.

Completion condition: `appendix/01` ranks every open question by blast radius and links each back to its module, and `appendix/02` gives a reason and a risk level for every uncovered route. Then update `00-overview.md`: flip each finished module's status from `占位` to `已完成` and recompute the coverage count — a tree left at `占位` is an unfinished run.

### 6. Adversarial review — accuracy, then completeness

The agent that wrote the docs cannot grade them. Spawn **one fresh-context sub-agent** (`general-purpose`) with the generated tree and repo access — never the generation chat — and hand it [review-brief.md](./references/review-brief.md), which carries the per-document checklist, the verdict vocabulary, and the report format.

In incremental mode, scope the review to the documents this pass rewrote; the untouched ones were already re-verified against their CodeMaps.

Two passes, in this order — **accuracy gates completeness**: never judge coverage against claims that might be false.

1. **Accuracy** — every claim is checked against the code; unverifiable claims are demoted, not shipped as facts.
2. **Completeness** — every real branch in the traced code resolves to a rule, an open question, or an explicit out-of-scope note.

Defects → fix the docs and re-run the same sub-agent. Past **3 rounds**, stop and move the unresolved items into `appendix/01-open-questions.md`, then say so in the hand-off.

Completion condition: the reviewer reports PASS on both passes.

### 7. Self-check before declaring done

Run the Quality Gate below. If a check fails, fix it rather than noting it.

Completion condition: every Quality Gate item is green; anything still red is reported to the user as a known gap, not silently shipped.

## Split Granularity

| Criterion | Rule |
|---|---|
| One top-level menu / one business capability | One module doc — the default |
| Expected module doc > 1500 lines | Split into sub-modules with fresh sequential ids (`M06-订单-下单.md`, `M07-订单-退款.md`) — never reuse the old id (see conventions) |
| Only 1–2 endpoints and no independent data | Merge into the parent module |
| Total document count | Keep the whole tree at 10–30 (incl. the 9 fixed docs); beyond that the granularity is too fine — group in the index instead |

## Authority — one truth per fact

Each fact has exactly one authoritative location; every other place may only reference it. The full table is in [conventions](./references/conventions.md). The rule that matters most:

- A module doc lists only *the roles, fields, error codes, and constraints this module touches*, then links out. It never copies a global table.
- Cross-module behavior is described in the **initiating** module; the affected module writes one line — "driven by the M03 callback, see M03 §5" — and stops there.

## Numbering

Document ids: `PRD-01` (overview), `PRD-C01`–`PRD-C06` (common), `PRD-M01`–`PRD-M0N` (modules), `PRD-A01`–`PRD-A02` (appendix). Module, function, rule, and open-question ids are defined in [conventions](./references/conventions.md).

Every document opens with a version block directly under its H1. The bump rules and the no-changelog rule are defined once in [conventions](./references/conventions.md); the templates show the block as a literal example and do not restate the rules.

## Quality Gate

- [ ] Every document has its id and version block, and the version names a real commit.
- [ ] Every module has its own feature CodeMap under `docs/codemap/`, verified against the code, and linked from the module header.
- [ ] No code index, file inventory, or scan-command list inside `docs/reverse-prd/` — that belongs to `/codemap`.
- [ ] Every route and controller method maps to an `M` id, or is listed in `appendix/02`.
- [ ] No global table (roles, fields, enums, error codes) is duplicated inside a module doc.
- [ ] Every rule and error code carries evidence: file + function (non-code artifacts such as DDL or config: file alone), with a line number only as a hint.
- [ ] Confidence is marked per module and per rule (`高` / `中` / `低`); guesses are not written as facts.
- [ ] Every open question is aggregated into `appendix/01` and linked back to its module section.
- [ ] Every assertion was verified against code by the fresh-context reviewer; nothing is left as an unmarked guess.
- [ ] Every branch in the traced call chain (validation, permission, state guard, exception, boundary, side effect) maps to a rule, an open question, or an explicit out-of-scope note.
- [ ] Relative links in `00-overview.md` and every module header resolve.

## References

- [overview-template.md](./references/overview-template.md) — `00-overview.md` skeleton.
- [module-template.md](./references/module-template.md) — the 9-section module doc.
- [common-templates.md](./references/common-templates.md) — the six global-layer documents.
- [appendix-templates.md](./references/appendix-templates.md) — open questions and uncovered modules.
- [review-brief.md](./references/review-brief.md) — the fresh-context reviewer's two-pass brief.
- [conventions.md](./references/conventions.md) — authority map, numbering, evidence, and version rules.
