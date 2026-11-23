---
name: knowledge
description: Capture a durable project lesson into the knowledge base under docs/knowledge/ — a pitfall, a user correction, a stabilized working convention, or an environment quirk that future sessions must honor and can't derive from the code or docs. Use at task wrap-up, before /handoff, or whenever the session learned something the hard way.
---

# Knowledge — capture what this repo taught you, durably

Sessions keep re-learning the same things: the Windows path quirk, the hook that bites on `amend`, the convention the user corrected twice. The knowledge base is where those lessons persist — `docs/knowledge/`, one index line + one file per entry, governed by the deployed contract `docs/agents/knowledge.md`.

This skill is the **write path**. The read path is cheaper and needs no skill: any session entering an unfamiliar area reads `docs/knowledge/KNOWLEDGE.md` (the index) per the contract.

> Precondition: `docs/agents/knowledge.md` exists (deployed by `/setup-skills`). If it doesn't, this repo has no knowledge base — ask the user to run `/setup-skills` first, or whether to create one.

## The gate — run it on every candidate

A candidate passes only if **all four** hold:

1. **Non-derivable** — not recoverable from the code, git history, `CONTEXT.md`, ADRs, CodeMap, or the deployed `docs/agents/*.md` contracts. Derivable facts need a pointer at most, never an entry.
2. **Cross-session** — it would change behavior in a future session (a pitfall to avoid, a rule to follow, a fact to know before acting). Single-task state fails this — that lives in the feature folder or a handoff.
3. **Sourced** — you can name where it came from: this session's events, a commit, an issue path. No source, no entry.
4. **Bounded** — you can state its scope (which repo / OS / tool / flow it holds for). Unbounded claims are usually unverified guesses — reject those.

Never capture: secrets, tokens, `.env` content, PII, or unverified guesses.

## Process

### 1. Collect candidates

From the current session (or the argument), list everything that smells durable: pitfalls hit, user corrections, conventions that stabilized, environment quirks, third-party behavior that surprised you.

### 2. Gate each one

Run the four checks above. For each rejected candidate, say why it failed — one line. The rejection list is part of the report; it keeps the base honest and shows the user what the gate means.

### 3. Match against existing entries

Read `KNOWLEDGE.md`. For each passing candidate:

- An entry on the same topic exists → **merge**: update that entry (fact, boundary, evidence) and its index line. Don't open a near-duplicate.
- An existing entry is now wrong (the lesson no longer holds) → rewrite it, noting why it changed, or delete it. Deleting is maintenance, not history loss — ADRs carry decision history; this base carries operational truth.

### 4. Write

- New entry: `docs/knowledge/<NNNN>-<slug>.md` — next free number, kebab-case slug.
- Frontmatter: `date`, `source`, `scope` — one line each.
- Body: the fact, why it holds, its boundary, and the evidence (what happened, where).

### 5. Update the index

`KNOWLEDGE.md` — **one line per entry**: number + one-line hook, nothing more. The index is the only part every session reads; keep it scannable — the contract consolidates entries once the index passes ~40 lines.

### 6. Report

One paragraph: captured (N new, M merged), pruned (K), rejected (with reasons) — or state plainly that nothing durable passed the gate. Committing follows the repo's Git Contract like any other file.

## When NOT to use this skill

- Task-transient state (progress, next steps) → the feature folder in `.scratch/`, or `/handoff`.
- A domain decision or term → `CONTEXT.md` / `docs/adr/` via `/domain-modeling`.
- Code navigation facts → `/codemap` (drift-check refreshes them).
- The repo has no knowledge base and the user doesn't want one → don't create files.
