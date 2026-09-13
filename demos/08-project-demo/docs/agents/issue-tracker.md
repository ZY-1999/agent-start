# Issue tracker：本地 Markdown

本仓库的 issue 以 markdown 文件形式存放在 `.scratch/`。**Issue 是三类工作的载体**：`spec`、`prd`、`bug`。

## 约定

- 一个 feature 一个目录：`.scratch/<YYYY-MM-DD>-<feature-slug>/`
- 只有 `spec` 放进 `specs/`；`prd` 与 `bug`（父级）位于 feature 根目录、与 `specs/` 平级：

  ```
  .scratch/2026-06-18-login-redirect/
  ├── 01-fix-login-redirect-loop.md   # prd 或 bug —— 父级
  └── specs/
      ├── 01-validate-token.md         # spec
      └── 02-refresh-flow.md           # spec
  ```

- 文件在各自目录内从 `01` 起以 `<NN>` 编号。`specs/` 按依赖顺序编号，使 `Blocked by: #<n>` 解析到同级 spec；`Parent: #<n>` 指向 feature 根目录的父级。
- 每个 issue 以 `Type:` 行（`spec` / `prd` / `bug`）、`Status:` 行（见 `triage-labels.md`）开头，末尾附 `## Comments` 小节。

最小 issue 文件示例：

```markdown
# Fix login redirect loop

Type: bug
Status: needs-triage

<bug 报告 / spec / PRD 正文>

## Comments

- 2026-06-18 — reproduced on staging with…
```

## 当 skill 说 "publish to the issue tracker"

`spec` 写入 `.scratch/<YYYY-MM-DD>-<feature-slug>/specs/<NN>-<slug>.md`；`prd` / `bug` 写入 `.scratch/<YYYY-MM-DD>-<feature-slug>/<NN>-<slug>.md`（feature 根目录）。目录不存在则创建。

## 当 skill 说 "fetch the relevant ticket"

读取所引用路径上的文件。用户通常会直接给出路径或 issue 编号。
