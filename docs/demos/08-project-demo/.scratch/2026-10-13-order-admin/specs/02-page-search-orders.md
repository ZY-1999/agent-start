# 分页搜索与筛选订单

Type: spec
Status: ready-for-agent
Parent: #1
Blocked by: #1

## Goal

管理员能按创建时间倒序分页浏览订单，并用订单号或客户名称前缀加上状态精确条件定位目标。

## Acceptance criteria

- [ ] 默认每页 10 条，按 `create_time` 降序；超过 10 条时第一页只有最新的 10 条，后续页能看到更早的订单；页大小可改为 20 或 50。测试接缝为 MockMvc + 真实 MySQL `order_db_test`（Flyway clean+migrate）。证明默认分页与排序。
- [ ] 关键字按前缀 `kw%` 命中订单号或客户名称；只用订单号或客户名称的中间片段、或只用结尾片段时不命中。证明搜索是前缀匹配，左模糊和全模糊都不成立。
- [ ] 状态条件只返回该精确状态；前缀与状态同时给出时，结果必须同时满足两者；没有命中时结果为空。证明筛选是精确且可组合。
- [ ] 手工：订单管理页列表能翻页，前缀搜索和状态筛选的结果与上述口径一致。

## Scope

- **In**: 订单分页列表、订单号/客户名称前缀搜索、状态精确筛选、页大小 10/20/50，以及对应的列表页。
- **Out**: 创建订单的生成规则与金额计算（由 #1 交付）、修改、逻辑删除、汇总看板、单 jar 打包。

## Context

- 父 PRD：`.scratch/2026-10-13-order-admin/01-order-admin-system.md`。搜索口径为前缀 `kw%`，遵循 `docs/agents/数据库规范.md` 严禁左模糊/全模糊。
- 依赖 spec #1 已能创建并落库订单；本 spec 只增加读路径。
- 分页默认页大小 10，列表默认按 `create_time` 降序。
- 无 `CONTEXT.md`、无 ADR。

## Design

外部接缝仍只有 HTTP：测试与页面都走这一处，不另设仓储接缝。分页查询是一个深模块，深度在于一次查询藏起前缀口径、组合条件和排序；删掉它，这些规则会散回每个调用方。

- **Interface delta**
  - 沿用 spec #1 的 `Result<T>`：成功为 HTTP 200 且 `code = 0`。本 spec 只新增读接口，创建接口不变。
  - `GET /api/orders`。参数：`kw`（可空）、`status`（可空）、`page`（缺省 1，从 1 起计）、`size`（缺省 10）。`size` 只允许 10、20、50；其他值为 HTTP 400，`data` 为空。类型不合法沿用 spec #1 的 HTTP 400。
  - `kw` 缺省或空串时不施加前缀条件（空串不得变成匹配全部的 `%`）。非空时按前缀 `kw%` 匹配订单号或客户名称，命中其一即可。左模糊与全模糊不在此接口内。
  - `status` 缺省时不按状态收窄；给出时与库中 `status` 精确匹配。与非空 `kw` 同时给出时为 AND。给出的值不在五个状态内时视为无命中，创建接口的枚举拒绝不用于这条读路径。
  - 无命中：HTTP 200，`records` 为空，`total` 为 0。请求页超出已有页时仍是 HTTP 200，`records` 为空，`total` 保持命中总数。
  - 排序固定为 `create_time` 降序，同一秒用 `id` 降序打破平局。调用方不传排序参数。
  - 成功时 `data` 为 `{ total, page, size, records }`。`page` 与 `size` 回显生效值。`records` 元素与 spec #1 的订单形态相同：`id`、`orderNo`、`customerName`、`productName`、`quantity`、`unitPrice`、`totalAmount`、`status`、`remark`、`createTime`、`updateTime`。金额为保留两位的十进制字符串，时间为服务器本地时区的 `yyyy-MM-dd HH:mm:ss`，不带偏移。响应不暴露逻辑删除标记。
  - 列表查询列写明：`id`、`order_no`、`customer_name`、`product_name`、`quantity`、`unit_price`、`total_amount`、`status`、`remark`、`create_time`、`update_time`。计数只取命中行数。不用 `*`。本 spec 不改 `t_order`。读路径不按 `is_deleted` 排除；排除由 spec #4 加到这条读路径上。现在库里只有未删除行。
  - 页面：在 `/orders` 上增加列表、分页、前缀搜索框和状态筛选，页大小仅 10/20/50。本 spec 不增加汇总导航。开发期代理沿用 spec #1。
- **Internal architecture**
  - 模块内部分层为 Web → Service → DAO，无 Manager。分页与前缀条件放在模块内部，不泄漏到 Web 或页面。
  - 测试只穿过 MockMvc，并核对 `order_db_test` 上这条读路径的返回口径。不新增仓储接缝。

## Rework on failure

失败时回退本 spec 的列表读路径，spec #1 的创建不受影响。重做本 spec。

## Comments

- 2026-10-13 — `/to-spec` step 4 落骨架。step 5 完成设计；step 6 双轴审查 PASS。Status → ready-for-human，待 Gate A。
- 2026-10-13 — Gate A 通过。Status → ready-for-agent。
