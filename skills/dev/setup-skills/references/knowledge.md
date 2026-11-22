# Knowledge Base

How this repo's durable lessons are stored, read, and written. The knowledge base holds **operational truth a future session must honor but cannot derive**: pitfalls, corrected mistakes, stabilized working conventions, environment quirks. It is distinct from the domain docs (`CONTEXT.md` + `docs/adr/` carry decisions and terms) and from the CodeMap (terrain).

## Before entering an unfamiliar area, read the index

- **`docs/knowledge/KNOWLEDGE.md`** — one line per entry. Read it at session start or when picking up an unfamiliar area; open the entries relevant to the task at hand.

If the file doesn't exist, **proceed silently** — don't flag the absence or push to create entries upfront. `/knowledge` (when installed) creates entries lazily, when something durable actually happens.

## File structure

```
docs/knowledge/
├── KNOWLEDGE.md                        ← index: one line per entry
├── 0001-windows-adb-path-quirk.md
└── 0002-review-gate-amend-pitfall.md
```

Entry format — frontmatter (`date`, `source`, `scope`) plus a short body (fact / why / boundary / evidence, with an optional action):

```markdown
---
date: 2026-11-22
source: session 2026-11-22 / commit abc1234 / .scratch/<feature>/
scope: this repo on Windows; zy-skills plugin ≥ 1.2
---

# <the lesson, as a title>

<the fact — one or two sentences.>

**Why:** <what makes it hold / what it cost to learn.>

**Boundary:** <where it stops being true.>

**Evidence:** <what happened, where — commit, session event, issue.>

**Action:** <optional — what a future session should do about it.>
```

## What belongs here — the gate

An entry must pass all four:

1. **Non-derivable** — not recoverable from code, git, `CONTEXT.md`, ADRs, CodeMap, or these `docs/agents/` contracts.
2. **Cross-session** — it would change a future session's behavior.
3. **Sourced** — names where it came from.
4. **Bounded** — states its scope.

Never: secrets, tokens, PII, unverified guesses. Task-transient state belongs in the feature folder or a handoff, not here.

## Writing entries

Any session may write an entry by following this contract directly; `/knowledge` (when installed) is the guided path — it also merges near-duplicates and prunes stale entries. Keep `KNOWLEDGE.md` at one line per entry; detail lives in the entry files. Entries are versioned with the repo like any other doc — commits follow the Git Contract.

## Maintenance

When an entry proves stale, rewrite or delete it — this base carries operational truth, not history (ADRs carry history). When the index grows past ~40 lines, consolidate entries that share a topic.
