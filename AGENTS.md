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

### Third-party skills (zentao / confluence)

The plugin ships vendored third-party skills in two buckets. `skills/third-party-zentao/` holds `zentao-cli` and `zentao-tour` (vendored from easysoft/zentao-cli) for operating Zentao (禅道) data: products, projects, stories, tasks, bugs, personal todos — route Zentao work to them; prerequisites: the `zentao` CLI (`npm install -g zentao-cli`) plus credentials — **environment variables recommended** (`ZENTAO_URL` / `ZENTAO_ACCOUNT` / `ZENTAO_TOKEN`|`ZENTAO_PASSWORD` exported in your own `~/.bashrc`; they outrank local profiles and password mode avoids token expiry), local-profile login (`zentao login --web`) as fallback. `skills/third-party-confluence/` holds `confluence` (vendored from pchuri/confluence-cli) for reading, searching, creating, updating, moving, deleting and converting Confluence pages and attachments — route Confluence work to it; prerequisites: the `confluence` CLI (`npm install -g confluence-cli`) with a profile in `~/.config/confluence-cli/config.json` (or `confluence init`).
