---
name: route
description: Route any question to the skill or flow that fits it. A router over this fork's resolved skills.
disable-model-invocation: true
---

# Route

You don't remember every skill. Route the question to the one that fits.

Scan the **intent** below; run the skill on the right. One question → one skill (or one short chain).

## Build something

- **Start from an architecture scan** → `/improve-architecture`
- **Idea → approved PRD (grill + Gate 0)** → `/idea-to-prd`
- **I have an incomplete PRD draft(status = 'needs-info')** → `/idea-to-prd`
- **Existing codebase with no docs — reverse-engineer a module-split, product-readable PRD** → `/reverse-prd`
- **Drive it myself, I have a codebase** → `/grill-with-docs` → `/to-prd` → `/sdd-flow`
- **Drive it myself, no codebase** → `/grill-me` → `/to-prd` → `/sdd-flow`
- **Requirement is simple and already complete (goal, scope, done-behaviors stated) — skip grilling and the PRD** → `/to-spec` → `/tdd`
- **Approved PRD → continue into SDD (spec → build → review)** → `/sdd-flow`
- **Just decompose a PRD into specs** → `/to-spec`
- **I already have a spec** → `/tdd`

## A bug

- **Find the root cause of one bug** → `/diagnose-bug`
- **Diagnosis in hand — turn it into a tracked fix and ship it** → `/to-prd` → `/sdd-flow`
- **The fix is clear, just implement it** → `/tdd`

## Test & QA

- **Not sure which test step comes first** → `/qa-flow`
- **Fuzzy requirement — what do we actually verify?** → `/qa-clarify`
- **Fuzzy requirement and the owner is right here to answer** → `/grill-me` → `/qa-clarify`
- **Assertions are settled — turn them into implementable specs** → `/to-spec`
- **A rule-level case that belongs in CI as a unit test** → `/tdd`
- **Need a transfer gate (smoke) or full business-flow cases with data & SQL** → `/qa-cases`
- **Bug fixed — verify it and size the regression blast radius** → `/qa-regress`
- **Bug won't reproduce / root cause unknown** → `/diagnose-bug` → `/qa-regress`
- **Release endgame: ship or not / demo readiness / retro** → `/qa-review`

Which test step borrows which `dev/` `sdd/` skill, and the five confusable pairs (`grill-me` vs `qa-clarify`, `to-spec` vs `qa-cases`, `tdd` vs `qa-cases`, `review` vs `qa-review`, `diagnose-bug` vs `qa-regress`): `skills/qa/qa-flow/references/pipeline-map.md`.

## Design & modeling

- **Refine domain language / build the glossary (`CONTEXT.md`)** → `/domain-modeling`
- **Module boundaries, testability, interface shape** → `/codebase-design`

## Code & terrain

- **Need an agent-facing code map, or the map may be stale** → `/codemap`

## Session & context

- **Thread is full — fresh session, keep this conversation** → `/handoff`
- **Phase break — summarize in place** → `/compact` (built-in)
- **Throwaway prototype to answer a runnable question** → `/prototype`
- **Durable lesson/pitfall worth keeping — capture into the project knowledge base** → `/knowledge`

## Learn

- **Learn a concept over multiple sessions** → `/teach`

## Review, setup & meta

- **Review current changes (Standards + Spec)** → `/review` (or `/code-review`)
- **Ship-or-not for a version / demo readiness / retro** → `/qa-review` — business gate, not the code diff
- **First-time repo setup (tracker / labels / domain docs / codemap)** → `/setup-skills`
- **Write or edit a skill (in this repo)** → `/writing-great-skills`
- **I don't know which skill fits** → you're in the right place — that's what `/route` is for.
