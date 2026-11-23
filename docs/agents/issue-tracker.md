# Issue tracker：本地 Markdown

本仓库的 issue 以 markdown 文件形式存放在仓库根的 `.scratch/` 下。**issue 是载体**，承载三类工作之一——`spec`、`prd` 或 `bug`。

## 约定

- 一个特性一个目录：`.scratch/<YYYY-MM-DD>-<feature-slug>/`
- 只有 `spec` 进 `specs/`；`prd` 与 `bug`（父项）放在特性根目录，与 `specs/` 平级：

  ```
  .scratch/2026-06-18-login-redirect/
  ├── 01-fix-login-redirect-loop.md   # prd 或 bug —— 父项
  ├── contracts.md                    # 技术契约真源 —— C-NN 契约点（由 /to-spec 生成）
  ├── trace.md                        # 追溯矩阵 —— AC→场景→契约→spec→test→commit（由 /to-spec 生成）
  └── specs/
      ├── 01-validate-token.md         # spec
      └── 02-refresh-flow.md           # spec
  ```

- `contracts.md` 与 `trace.md` 是**特性级产物**，不是 issue：`/to-spec` 拆解时生成（把 PRD 的 AC 双锚翻译为契约点）、`/tdd` 收尾回填 trace 行、`/spec-sync` 在变更时联动、`/spec-check` 审计它们。特性目录里没有这两个文件，只说明尚未拆解。
- 文件在其所在目录内从 `01` 起按 `<NN>` 编号。`specs/` 按依赖顺序编号，使 `Blocked by: #<n>` 指向同级的 spec；`Parent: #<n>` 指向特性根目录的父项。
- 每个 issue 开头是 `Type:` 行（`spec` / `prd` / `bug`）与 `Status:` 行（取值见 `triage-labels.md`），末尾追加 `## Comments` 段。

最小 issue 示例：

```markdown
# 修复登录重定向死循环

Type: bug
Status: needs-triage

<bug 报告 / spec / PRD 正文>

## Comments

- 2026-06-18 — 已在预发环境复现，…
```

## 当某个 skill 说"发布到 issue tracker"

`spec` 写到 `.scratch/<YYYY-MM-DD>-<feature-slug>/specs/<NN>-<slug>.md`；`prd` / `bug` 写到特性根目录的 `.scratch/<YYYY-MM-DD>-<feature-slug>/<NN>-<slug>.md`。目录不存在就创建。

## 当某个 skill 说"取相关工单"

读取引用路径上的文件。用户通常会直接给出路径或 issue 编号。
