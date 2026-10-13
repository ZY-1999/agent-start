# 逻辑删除订单

Type: spec
Status: ready-for-human
Parent: #1
Blocked by: #2

## Goal

管理员确认后逻辑删除订单，该订单从列表、前缀搜索和状态筛选中消失，库中原行仍在。

## Acceptance criteria

- [ ] 确认删除后，该订单不再出现在默认列表、前缀搜索和状态筛选中，库中原行仍在且已标记逻辑删除。测试接缝为 MockMvc + 真实 MySQL `order_db_test`（Flyway clean+migrate）。证明删除是逻辑删除，并且读路径排除已删行。
- [ ] 手工：删除前出现确认，取消确认则订单仍在列表中，确认后从列表消失。

## Scope

- **In**: 按 id 逻辑删除，以及默认列表、前缀搜索、状态筛选对已删行的排除；删除确认交互。
- **Out**: 物理删除、修改订单资料、汇总统计中的已删排除（由 #5 验收）、单 jar 打包。

## Context

- 父 PRD：`.scratch/2026-10-13-order-admin/01-order-admin-system.md`。逻辑删除字段遵循 `docs/agents/数据库规范.md` 的 `is_deleted`。
- 依赖 spec #2 的列表、前缀搜索与状态筛选；不依赖 spec #3 的修改。
- 无 `CONTEXT.md`、无 ADR。

## Design

外部接缝只有 HTTP：测试与页面都走这一处。沿用 spec #1 的 `Result<T>`（`code`、`message`、`data`），成功仍是 HTTP 200 且 `code = 0`。逻辑删除是一个深模块——删除接口很小，深度在于把「标记」和「所有已有读路径排除」收在同一模块里。删掉它，这两件事会散回每个调用方。

- **Interface delta**
  - 新增 `DELETE /api/orders/{id}`，无请求体。成功：HTTP 200，`data` 为空。行为是把该行 `is_deleted` 置为 1，并刷新 `update_time`。行仍在库中。不做物理删除。
  - `id` 不存在：HTTP 404，`data` 为空。已标记删除的 id 视为不可见，再删返回 404。二次删除不单列验收。
  - 不把已删除标记放进订单 JSON。
  - 本 spec 修改 spec #2 的读路径不变量，不是新接口：默认列表、前缀 `kw%`、状态精确筛选都只返回 `is_deleted = 0` 的行。这是同一读模块上的一个不变量。
  - 不新增列。表已有 `is_deleted` unsigned tinyint，属性名 `deleted`；新建行仍为 0。查询排除由本 spec 在读路径打开。
  - 不改汇总统计（spec #5）。不改修改接口（spec #3）。
  - 页面：删除前确认。取消确认不发删除请求，订单仍在列表。确认后列表不再显示该订单。
- **Internal architecture**
  - 模块内部分层为 Web → Service → DAO。标记放在 Service，经 DAO 更新原行。
  - 排除条件放在读路径的一处，默认列表、前缀搜索和状态筛选共用，而不是每个调用方各写一遍。

## Rework on failure

失败时撤销删除与读路径排除，创建与未删除列表仍可用。重做本 spec。

## Comments

- 2026-10-13 — `/to-spec` step 4 落骨架。step 5 完成设计；step 6 双轴审查 PASS。Status → ready-for-human，待 Gate A。
- 2026-10-13 — Gate A 通过。Status → ready-for-agent。
