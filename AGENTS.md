# agent-start

本仓库的 agent 入口规则；人类读者的大门是 [README.md](README.md)。

## Agent Notice

### Grill first

Every time the agent receives a requirement, interview the user first with `/grill-me` — or `/grill-with-docs` when the decisions should be captured as ADRs/glossary — and keep asking until the full context is clear. Only then start the work.

### Issue tracker

Local markdown under `.scratch/` at the repo root — one directory per feature, specs in `specs/`. See `docs/agents/issue-tracker.md`.

### Triage labels

`needs-triage` / `needs-info` / `ready-for-agent` / `ready-for-human` / `wontfix`. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: `CONTEXT.md` + `docs/adr/` at the repo root. See `docs/agents/domain.md`.

### CodeMap

Start here when exploring the codebase: a project-level terrain index under `docs/codemap/`; refresh or add feature maps via `/codemap`; when unsure whether a map is still trustworthy, run `/codemap` drift-check — if it reports drift, update the affected map.

### Git contract

Solo on the current branch; commit messages use conventional-commits prefixes (`docs:` / `feat:` / `fix:` …) with Chinese subjects allowed. See `docs/agents/git-contract.md`.

### QA skills

`skills/qa/` holds the test-side pipeline: `qa-flow` (stage router), `qa-clarify` (requirement → verifiable assertions + question list), `qa-cases` (four layers — smoke gate, end-to-end scenarios, single-feature checks, and specialty checks which are **off by default**; test data & verification SQL included), `qa-regress` (fix verification + blast radius), `qa-review` (release verdict + demo readiness + retro). Route any test work there. QA assets live in the target project's `docs/qa/` (layout in `skills/qa/qa-flow/references/project-layout.md`); if absent, ask once and record the path in that project's `AGENTS.md`. Zentao work routes to `zentao-cli`, Confluence work to `confluence`.

**Testing is manual-only here** — no automation, no CI test runs. Every case must be executable by hand: preconditions reproducible (steps or seed SQL), verification SQL non-optional, ≤ 12 steps per case, per-case duration estimate on L2/L3, and an execution checklist with batches, owners and totals. `/tdd` is not part of this pipeline today; `[规则型]` tags only mark cases that would be easiest to automate later.

**All systems under test are internal business systems** — employees only, no external users, and there is currently no specialty-testing requirement. L4 (performance / security / compatibility / integration) is therefore **not produced by default**; say "no specialty testing this round" in the hand-off instead of building an empty section. The seven risks that still bite internal systems (cross-org data visibility, button-level permission bypass, sensitive fields in exports, list performance at ~10k rows, double submit, concurrent approval of the same document, exported files opening in WPS/Office) are functional correctness — they belong to L2/L3 permission and list scenarios, not to L4. Only build L4 when the user explicitly asks (customer test report, pre-release security review, going external) or the requirement clearly signals it.

The QA pipeline is **not** isolated from the rest of this repo. It borrows four general skills: `/grill-me` (or `/grill-with-docs`) when the requirement owner is present, `/to-spec` (assertions become spec acceptance criteria), `/review` (diff informs the regression blast radius), `/diagnose-bug` (root cause unknown). `/tdd` stays out of this pipeline until the team adopts automation. Full cross-bucket map, the three hand-off interfaces, and the five confusable pairs are in `skills/qa/qa-flow/references/pipeline-map.md`. In particular `/review` (code-diff gate) and `/qa-review` (release business gate) are different gates — both must pass before shipping.

### Third-party skills (zentao / confluence)

The plugin ships vendored third-party skills in two buckets. `skills/third-party-zentao/` holds `zentao-cli` and `zentao-tour` (vendored from easysoft/zentao-cli) for operating Zentao (禅道) data: products, projects, stories, tasks, bugs, personal todos — route Zentao work to them; prerequisites: the `zentao` CLI (`npm install -g zentao-cli`) plus credentials — **environment variables recommended** (`ZENTAO_URL` / `ZENTAO_ACCOUNT` / `ZENTAO_TOKEN`|`ZENTAO_PASSWORD` exported in your own `~/.bashrc`; they outrank local profiles and password mode avoids token expiry), local-profile login (`zentao login --web`) as fallback. `skills/third-party-confluence/` holds `confluence` (vendored from pchuri/confluence-cli) for reading, searching, creating, updating, moving, deleting and converting Confluence pages and attachments — route Confluence work to it; prerequisites: the `confluence` CLI (`npm install -g confluence-cli`) with a profile in `~/.config/confluence-cli/config.json` (or `confluence init`).
