---
name: route
description: Route any question to the skill or flow that fits it. A router over this fork's resolved skills. Picks the delivery flow by SDD applicability + requirement maturity (direct / to-spec fast lane / full SDD).
disable-model-invocation: true
---

# Route

You don't remember every skill. Route the question to the one that fits.

Decide in two steps: **(1)** is this a request to deliver a change (code / feature / fix), or a tooling / session request? A delivery request goes through the **three tiers** below — pick by SDD applicability + requirement maturity, not by gut feel. A tooling request jumps straight to its category at the bottom. One question → one skill (or one short chain).

## The three tiers (delivery requests)

The tiers are the concrete form of the repo's "route everything" entry rule — every requirement goes through this router before any work starts. Every tier clarifies first, and every tier stops for human confirmation before building; only the weight of the plan differs.

| Tier | When | Flow | Human gate |
|------|------|------|-----------|
| 1 — direct | Not SDD-applicable (docs / skill / manifest / config / demo) or a trivial fix | clarify → phased plan → execute | plan + wait for confirmation (AGENTS.md rule) |
| 2 — fast lane | SDD-applicable, requirement already settled but needs splitting / contract alignment | clarify → `/to-spec` → Gate A → `/tdd` → `/review` + trace gate | Gate A (one-screen alignment card) |
| 3 — full SDD | Fuzzy / cross-feature / needs ADR or glossary decisions | clarify → `/idea-to-prd` → Gate 0 → `/sdd-flow` | Gate 0, then Gate A |

**Discriminator — SDD applicability + maturity:**

- **Not SDD-applicable or trivial** → tier 1. The work has no `spec` unit: docs, lecture HTML, skill assets, plugin manifests, config, a one-line fix, or a diagnosis whose deliverable is the diagnosis itself.
- **SDD-applicable and already settled, but multi-unit** → tier 2. Goal, scope, and done-behaviors are stated or directly implied; you need contracts + a scenario trace + a decomposition before coding, but no PRD-level product decisions remain.
- **SDD-applicable but not settled** → tier 3. The goal is still moving, spans features, or needs domain language / an ADR before anyone can plan. Grill it into an approved PRD first.

If a requirement arrives half-settled, don't guess the tier — clarify (`/grill-me`, or `/grill-with-docs` when a codebase is involved) until you can place it.

### Tier 1 — direct: clarify, plan, then execute

The AGENTS.md entry rule in its lightweight form: clarify briefly, write a phased plan (which files, in what order, how to verify), wait for confirmation, then act. No SDD artifacts on disk.

- **Docs / lecture / skill asset / plugin manifest / config change** → clarify, plan, confirm, edit; verify per the repo's build-and-verify rules.
- **Fix is clear, just implement it** → `/tdd` (still give the plan + wait first).
- **Bug diagnosis with no fix (root cause unknown / won't reproduce)** → `/diagnose-bug` — see *A bug* below; diagnosis is the deliverable.
- **Throwaway prototype to answer a runnable question** → `/prototype`.

### Tier 2 — fast lane: clarified → to-spec → Gate A → build

Use when the requirement is settled but must be cut into specs. This is the **middle of `/sdd-flow`** (Stage 1 + Gate A + Stages 2–3) with the PRD / Gate 0 skipped — the spec itself carries the plan (contracts + scenarios + design).

Full chain: **clarify (`/grill-me` if needed) → `/to-spec` (`contracts.md` + `trace.md` + designed specs, `ready-for-human`) → Gate A one-screen alignment card, wait for approval → `/tdd` per spec in dependency order → `/review` + trace gate.**

- **Simple and complete requirement, needs splitting** → `/to-spec` → Gate A → `/tdd` (no PRD, no Gate 0; the completeness bar at `/to-spec` entry still applies).
- **Already grilled (design settled in conversation), no PRD** → `/to-spec` — do not re-interview.
- **Just decompose a PRD / bug into specs** → `/to-spec` (start at Stage 1).
- **Approved specs on disk, `ready-for-agent`** → `/tdd` (start at Stage 2).
- **Requirement or contract changed after decomposition** → `/spec-sync` (ripple AC / contracts / scenarios / specs / tests / trace, then re-run the double-anchor check).
- **Audit a feature's spec artifacts, or see what's in flight** → `/spec-check`.

Gate A is owned by `/sdd-flow`; if you drive tier 2 by hand, present the same one-screen card (the cut + P0 alignment + machine verdicts + one spot-check spec) and wait — don't skip it just because you skipped the PRD.

### Tier 3 — full SDD: clarified → idea-to-prd → sdd-flow

Use when the requirement is not yet a settled design. Grill → PRD → Gate 0, then the whole back half.

Full chain: **`/idea-to-prd` (grill → PRD → Gate 0; it uses `/grilling` + `/domain-modeling` + `/to-prd`) → `/sdd-flow` (which runs tier 2's `/to-spec` → Gate A → `/tdd` → `/review` + trace gate → doc maintenance).**

- **Raw idea / requirement** → `/idea-to-prd`.
- **Incomplete PRD draft (`needs-info`)** → `/idea-to-prd` (grills only the gaps, updates in place).
- **Diagnosis in hand — turn it into a tracked fix and ship it** → `/to-prd` → `/sdd-flow`.
- **Existing codebase with no docs, need a product-readable PRD** → `/reverse-prd` → `/idea-to-prd` (mode 2) → Gate 0.
- **Start from an architecture scan** → `/improve-architecture` → `/idea-to-prd` (mode 2, on the chosen draft) → Gate 0.
- **Approved PRD already on disk** → `/sdd-flow` directly (skip `/idea-to-prd`; Gate 0 is done).
- **Manual, step-by-step driving instead of the wrappers** → `/grill-with-docs` (codebase) or `/grill-me` (none) → `/to-prd` → `/sdd-flow`.

## Tooling & session requests (go straight to the category)

These aren't "deliver a change" requests — no tier, no gate.

### A bug

- **Find the root cause of one bug** → `/diagnose-bug`
- **Bug fixed — verify it and size the regression blast radius** → `/qa-regress`
- **Bug won't reproduce / root cause unknown** → `/diagnose-bug` → `/qa-regress`

### Test & QA

- **Not sure which test step comes first** → `/qa-flow`
- **Fuzzy requirement — what do we actually verify?** → `/qa-clarify`
- **Fuzzy requirement and the owner is right here to answer** → `/grill-me` → `/qa-clarify`
- **Assertions are settled — turn them into implementable specs** → `/to-spec`
- **A rule-level case worth automating later** → keep it in `/qa-cases`, tagged `[规则型]` (no automation or CI this round)
- **Need a transfer gate (smoke) or full business-flow cases with data & SQL** → `/qa-cases`
- **Release endgame: ship-or-not / demo readiness / retro** → `/qa-review`

Which test step borrows which `dev/` `sdd/` skill, and the five confusable pairs (`grill-me` vs `qa-clarify`, `to-spec` vs `qa-cases`, `tdd` vs `qa-cases`, `review` vs `qa-review`, `diagnose-bug` vs `qa-regress`): `skills/qa/qa-flow/references/pipeline-map.md`.

### Design & modeling

- **Refine domain language / build the glossary (`CONTEXT.md`)** → `/domain-modeling`
- **Module boundaries, testability, interface shape** → `/codebase-design`

### Code & terrain

- **Need an agent-facing code map, or the map may be stale** → `/codemap`

### Session & context

- **Thread is full — fresh session, keep this conversation** → `/handoff`
- **Phase break — summarize in place** → `/compact` (built-in)
- **Durable lesson/pitfall worth keeping — capture into the project knowledge base** → `/knowledge`

### Learn

- **Learn a concept over multiple sessions** → `/teach`

### Review, setup & meta

- **Review current changes (Standards + Spec)** → `/review` (never inline `/code-review` in the main context — the two axes need fresh-context sub-agents)
- **Ship-or-not for a version / demo readiness / retro** → `/qa-review` — business gate, not the code diff
- **First-time repo setup (tracker / labels / domain docs / codemap)** → `/setup-skills`
- **Write or edit a skill (in this repo)** → `/writing-great-skills`
- **I don't know which skill fits** → you're in the right place — that's what `/route` is for.
