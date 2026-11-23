---
name: setup-skills
description: Configure a repo for the SDD skills — write the AGENTS.md entry (project overview, language, build & verify, agent notice, boundaries, minimal memory) plus docs/agents/{issue-tracker,triage-labels,domain,git-contract,developer,checklist,knowledge}.md, and build an initial project-level CodeMap. Run once before first use of the other skills.
disable-model-invocation: true
---

# Setup Skills

Scaffold the per-repo configuration that the skills assume:

- **AGENTS.md entry** — the project-level on-ramp for AI coding tools: overview, language, build & verify, `## Agent Notice`, default boundaries, minimal memory. Written in Chinese. Skeleton: [agents-md.md](./references/agents-md.md).
- **Issue tracker** — where issues live. **Local markdown only** in this fork (no GitHub/GitLab).
- **Triage labels** — the strings used for the canonical triage roles.
- **Domain docs** — where `CONTEXT.md` and ADRs live, and the consumer rules for reading them.
- **Developer rules** — coding and doc conventions for humans and agents alike.
- **Checklist** — the review checklist used by `/review` and by self-checks before finishing.
- **CodeMap** — an agent-facing code-terrain index under `docs/codemap/`. A project-level map is built once during setup; `/codemap` adds feature maps or refreshes it later.
- **Knowledge base** — where durable lessons live: an index + one file per entry under `docs/knowledge/`, behind a strict capture gate. Read the index before entering unfamiliar areas; capture at wrap-up or on pitfalls.
- **Git contract** — the branch model (a ZenTao-driven table, trimmed to the repo's actual branches on deploy) and the commit-message association-ID convention, deployed to `docs/agents/git-contract.md`.

All deployed artifacts are written in **Chinese** (this fork's docs convention). Keep machine-readable tokens untranslated — `Type:` / `Status:` line names, the triage label strings, and every path.

This is a prompt-driven skill, not a deterministic script. Explore, present what you found, confirm with the user, then write.

One step is environmental rather than per-repo: the plugin also ships vendored third-party skills (`zentao-cli`, `zentao-tour`, `confluence`). Step 5 verifies their CLIs and recommends how the user configures credentials themselves — it never handles secret values.

## Existing spec / issue systems are reference only

The repo may already have its own spec / PRD / issue-tracking system — bespoke markdown under `docs/specs/`, an exported knowledge base, a legacy `SPECS.md`, anything of the kind. Treat it as **input, not authority**:

- In **Explore**, read it to understand the project's current state, terms, and prior decisions. That's reference material.
- It does **not** change the structure this skill sets up. The layouts of the issue tracker (`.scratch/`), triage labels, domain docs (`CONTEXT.md` + `docs/adr/`), and CodeMap (`docs/codemap/`) are defined by this skill's defaults. Don't relocate them to match the existing system, don't fork a second layout alongside it, and don't reshape this skill's files to fit it.
- If the existing system holds something worth keeping (a glossary, decision log, settled scope), fold that **content** into this skill's files — write it into `CONTEXT.md`, `docs/adr/`, `docs/agents/domain.md`. Adapt the content to this structure, never the structure to the content.

This is distinct from step 2's "raise as a question if the repo contradicts the default": that rule covers _this skill's own outputs_ already pointing somewhere else. An unrelated pre-existing system is not a contradiction — it's source material. Only an explicit user request to point one of this skill's outputs at the existing system counts as a step-2 exception.

## Process

### 1. Explore

Look at the current repo to understand its starting state. Read whatever exists; don't assume:

- `AGENTS.md` and `CLAUDE.md` at the repo root — does either exist? Which of the skeleton sections does it already carry (project overview / language / build & verify / `## Agent Notice` / default boundaries / minimal memory)?
- `CONTEXT.md` and `CONTEXT-MAP.md` at the repo root
- `docs/adr/` and any `src/*/docs/adr/` directories
- `docs/agents/` — does this skill's prior output already exist? Which of the seven files are present?
- The build and verification commands the repo actually uses — build tool, test command, how to start it, and whether testing is manual-only. Needed for the overview and build sections; ask rather than guess.
- `docs/codemap/` — does a project-level CodeMap already exist?
- `docs/knowledge/` — does a knowledge base already exist (index + entries)?
- `.scratch/` — sign that the local-markdown issue tracker convention is already in use
- Any pre-existing spec / PRD / issue system the repo already has (its own `docs/specs/`, a tracking doc, an exported knowledge base, …) — read it for context. **Reference only**; see the rule above.

### 2. Present the plan (defaults applied)

Summarise what's present and what's missing, then present the **defaults for all nine items in one list** — don't walk through them one at a time. State that you'll apply these unless the user objects, and ask only for exceptions.

The defaults:

- **AGENTS.md entry** — add the six-section skeleton (overview / language / build & verify / `## Agent Notice` / boundaries / minimal memory) to whichever entry file exists, filled from what Explore found.
- **Issue tracker** — local markdown under `.scratch/` at the repo root.
- **Triage labels** — each canonical role's string equals its name (`needs-triage`, `needs-info`, `ready-for-human`, `ready-for-agent`, `implemented`, `closed`, `wontfix`; a spec also carries `needs-design` before its design is filled). See [triage-labels.md](./references/triage-labels.md).
- **Domain docs** — single-context: one `CONTEXT.md` + `docs/adr/` at the repo root.
- **Developer rules** — deploy [developer.md](./references/developer.md) to `docs/agents/developer.md`. Drop section two (skill / rule-asset conventions) when the repo carries no such assets.
- **Checklist** — deploy [checklist.md](./references/checklist.md) to `docs/agents/checklist.md`. Drop the rule-asset line when it doesn't apply.
- **CodeMap** — build the project-level map across the whole repo via `/codemap`.
- **Knowledge base** — `docs/knowledge/` (`KNOWLEDGE.md` index + one file per entry), rules in [knowledge.md](./references/knowledge.md). `/knowledge` is the guided write path when installed; the contract alone also suffices.
- **Git contract** — deploy [git-contract.md](./references/git-contract.md) to `docs/agents/git-contract.md`. The template leads with a **ZenTao-driven branch model** (`main`/`develop`/`feature`/`bugfix`/`release`/`hotfix`, each mapped to a ZenTao object); trim it to the branch types the repo actually uses, and for a repo that develops on the current branch keep only `main` and delete the rest of the table. Commit messages carry one association ID each — a ZenTao task/bug/story, or an issue slug;

Raise a section as a question **only if** the repo's actual state contradicts the default — e.g. a monorepo needs multi-context (`CONTEXT-MAP.md` + per-context `CONTEXT.md` files), issues already live somewhere other than `.scratch/`, or the repo is too large/trivial for a CodeMap (or `/codemap` isn't installed). Otherwise proceed with the defaults.

### 3. Confirm and edit

Show the user a draft of:

- The full entry-file skeleton ([agents-md.md](./references/agents-md.md)) to add to whichever of `CLAUDE.md` / `AGENTS.md` is being edited (see step 4 for selection rules) — show the sections around `## Agent Notice` too, not just the block itself
- The contents of `docs/agents/issue-tracker.md`, `docs/agents/triage-labels.md`, `docs/agents/domain.md`, `docs/agents/git-contract.md`, `docs/agents/developer.md`, `docs/agents/checklist.md`, `docs/agents/knowledge.md`
- **Three decisions** — (1) branch model: the ZenTao-driven table trimmed to the branch types the repo uses, or a single current branch (table reduced to `main`); (2) the association-ID source for commits — ZenTao task/bug/story, an issue slug, or "none" — plus any conventional-commits prefix; (3) whether the repo carries skill / prompt / rule assets, which decides whether `developer.md` keeps its second section and whether the checklist keeps its rule-asset line. The `git-contract.md` draft reflects the first two — trim the branch table, then rewrite the association-ID column and the commit template to the repo's real carrier.

Let them edit before writing.

### 4. Write

**Pick the file to edit:**

- If `CLAUDE.md` exists, edit it.
- Else if `AGENTS.md` exists, edit it.
- If neither exists, ask the user which one to create — don't pick for them.

Never create `AGENTS.md` when `CLAUDE.md` already exists (or vice versa) — always edit the one that's already there.

The entry-file skeleton lives in [agents-md.md](./references/agents-md.md) — six top-level sections, all Chinese, with `<…>` placeholders. Read it, fill the placeholders from what Explore found, and merge it into the entry file. If the file already carries part of the skeleton, update `## Agent Notice` in place and add only the missing top-level sections — never overwrite user edits, never append a duplicate `## Agent Notice`. When updating an existing `## Agent Notice`, replace each of its sections with the **current version from the skeleton template** even if that section already exists in the target file — do not preserve stale wording; sections the user added outside the template are left untouched.

Then write the seven docs files using the seed templates in this skill folder as a starting point:

- [issue-tracker-local.md](./references/issue-tracker-local.md) — 本地 markdown issue tracker
- [triage-labels.md](./references/triage-labels.md) — 标签映射
- [domain.md](./references/domain.md) — 领域文档消费规则与布局
- [knowledge.md](./references/knowledge.md) — 知识库布局、读写规则与沉淀门禁
- [developer.md](./references/developer.md) — 开发规范；仓库没有 skill / prompt / 规则资产时删掉第二节
- [checklist.md](./references/checklist.md) — 评审清单；不适用时删掉规则资产那一条
- [git-contract.md](./references/git-contract.md) — git 工作流契约。**部署时定稿**：按仓库实际裁剪分支类型表（在当前分支开发的仓库只留 `main`），把「主要关联对象」列与提交模板换成该仓库真实载体（禅道 ID / issue slug），并删掉两处 `_<部署时定稿…>_` 说明。`/sdd-flow` 与 `/tdd` 读部署后的 `docs/agents/git-contract.md`。

All seven files are written in Chinese. Keep the machine-readable tokens as-is: `Type:` / `Status:` line names, the triage label strings, and every path.

Then build the project-level CodeMap: run `/codemap` in **project** mode to generate `docs/codemap/project.md`. Feature maps are added later as work needs them — not during setup. (Skip if `/codemap` isn't installed.)

### 5. Check the third-party CLIs (zentao / confluence)

The zentao skills operate Zentao (禅道) through the `zentao` command, from the [`zentao-cli`](https://www.npmjs.com/package/zentao-cli) npm package. These are per-machine dependencies — setup only verifies them and recommends the credential approach; it never installs, logs in, or handles secret values itself.

- Run `zentao --version`. If the command is missing or errors, say the two zentao skills won't work yet and give the install command: `npm install -g zentao-cli` (or `bun` / `pnpm`; one-off use can go through `npx zentao-cli`). Don't treat it as a setup failure — everything else this skill deploys works without it.
- Credentials: recommend the user configure environment variables themselves in bash — `ZENTAO_URL`, `ZENTAO_ACCOUNT`, `ZENTAO_TOKEN` (or `ZENTAO_PASSWORD`) exported in `~/.bashrc`, then `source ~/.bashrc`. Env credentials take priority over local profiles and password mode avoids token expiry. Setup never writes or fills these values.
- Alternative for personal machines: `zentao login --web` where the installed version supports it (older versions: the user runs that version's own login in their terminal).
- Confluence: the vendored `confluence` skill drives the `confluence` command from the [`confluence-cli`](https://www.npmjs.com/package/confluence-cli) npm package. Run `confluence --version`; if missing, give `npm install -g confluence-cli` and point the user to `confluence init` or the `~/.config/confluence-cli/config.json` profile for credentials (setup never writes or fills these). Same non-failure rule as above — everything else this skill deploys works without it.

### 6. Done

Tell the user the setup is complete and which files now drive what: `AGENTS.md` is the entry every agent reads first; `/idea-to-prd` and `/review` read the issue tracker; `/sdd-flow` and `/tdd` follow the git contract; `/review`'s Standards axis reads `docs/agents/developer.md` and uses `checklist.md` as its coverage map; `/knowledge` writes `docs/knowledge/` — read `KNOWLEDGE.md` before entering unfamiliar areas. Mention they can edit `docs/agents/*.md` directly later, and refresh or extend the CodeMap via `/codemap` — re-running this skill is only necessary to restart from scratch. If step 5 found a third-party CLI missing, repeat the install reminder once in this summary.
