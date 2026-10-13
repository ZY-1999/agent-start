# 汇总看板聚合统计

Type: spec
Status: ready-for-agent
Parent: #1
Blocked by: #4

## Goal

管理员能在汇总看板看到未删除订单的总量、金额、状态构成和近 7 天下单趋势。

## Acceptance criteria

- [ ] KPI 的总订单数、总金额只统计未删除订单；待支付数只计 `PENDING`，已完成数只计 `COMPLETED`，`PAID` / `SHIPPED` / `CANCELLED` 不计入这两张卡片。测试接缝为 MockMvc + 真实 MySQL `order_db_test`（Flyway clean+migrate）。证明四张卡片的统计口径。
- [ ] 状态分布给出五个状态各自的订单数，各类相加等于 KPI 总订单数。证明饼图与总订单数同源。
- [ ] 近 7 天按服务器本地时区、窗口 [今日−6 天 00:00, 今日 23:59:59]，以 `create_time` 的日历日汇总每日订单数和金额。窗口起点整秒与今日 23:59:59 的订单计入；起点之前、以及次日 00:00 的订单不计入。证明窗口边界。
- [ ] 窗口内没有订单的自然日仍返回订单数 0、金额 0。证明无单日补零。
- [ ] 已逻辑删除的订单不进入 KPI、状态分布和近 7 天趋势。证明三块统计都排除已删订单。
- [ ] 手工：汇总看板能看到四张卡片、状态饼图和近 7 天柱状图，展示与上述口径一致。

## Scope

- **In**: 未删除订单的 KPI、五状态分布、近 7 天按日订单数与金额，以及汇总看板页的三块展示。
- **Out**: 订单写入与修改、列表搜索、报表导出、严格时区配置项、单 jar 打包。

## Context

- 父 PRD：`.scratch/2026-10-13-order-admin/01-order-admin-system.md`。近 7 天窗口与补零口径以 PRD Implementation Decisions 为准。
- 依赖 spec #4：逻辑删除已经存在，本 spec 验收三块统计都排除已删订单。不依赖 spec #3。
- 无 `CONTEXT.md`、无 ADR。前端图表库为 ECharts；本 spec 不新增前端单元测试。

## Design

外部接缝只有 HTTP：测试与页面都走这一处，不另设可替换的仓储接缝。汇总统计是一个深模块——接口是一次 `GET /api/orders/stats`，深度在于一次调用藏起排除已删、五状态补零、7 日补零和窗口边界。删掉这个模块，这些口径会散成三个浅接口，由页面自己拼。

- **Interface delta**
  - 沿用 `Result<T>`：`code`（int，成功为 0）、`message`（string）、`data`（T 或空）。成功为 HTTP 200 且 `code = 0`。
  - `GET /api/orders/stats`。调用方不传窗口，也不传状态；模块按服务器本地时区取近 7 天。
  - 成功时 `data` 含三块，页面一次取回：
    - `kpi`：`totalCount`（int）、`totalAmount`（保留两位的十进制字符串，如 `"12.30"`）、`pendingCount`（int，只计 `PENDING`）、`completedCount`（int，只计 `COMPLETED`）。`PAID`、`SHIPPED`、`CANCELLED` 只出现在 `statusCounts`，不进入后两张卡。
    - `statusCounts`：五个状态各一条 `{ status, count }`，顺序固定为 `PENDING`、`PAID`、`SHIPPED`、`COMPLETED`、`CANCELLED`。没有订单的状态 `count` 为 0。五者之和等于 `kpi.totalCount`。
    - `trend`：恰好 7 个元素 `{ date, count, amount }`，从今日−6 天到今日，含两端，按日期从早到晚。`date` 为服务器本地时区的 `yyyy-MM-dd`，不带偏移。无订单的日子 `count` 为 0、`amount` 为 `"0.00"`。
  - 只统计 `is_deleted = 0`。已删行仍在且标记为删除，同一次返回的 KPI、五状态和近 7 天都不计入它。
  - 近 7 天窗口为 [今日−6 天 00:00:00, 今日 23:59:59]，按 `create_time` 的日历日分组。窗口起点整秒与今日 23:59:59 计入；起点之前与次日 00:00:00 不计入。
  - 聚合用 `count(*)`。金额求和没有命中行时收成 `"0.00"`，调用方拿到的是这个字符串。`kpi.totalAmount` 与 `trend.amount` 都是保留两位的十进制字符串。
  - 没有任何未删除订单时仍是成功：四项计数为 0，总金额为 `"0.00"`，五个状态计数为 0，趋势七日均为 0 与 `"0.00"`。
  - 页面：路由 `/dashboard`。四张 KPI 卡、状态饼图、近 7 天柱状图（订单数与金额），都只渲染这一次返回。本 spec 第一次出现第二个页面，顶部导航放在这里，可在 `/orders` 与 `/dashboard` 之间切换。
  - 测试穿过这一条 HTTP 接口。
- **Internal architecture**
  - 模块内部分层为 Web → Service → DAO，无 Manager。窗口与补零在 Service，不在页面。DAO 按未删除行做 `count(*)` 与金额求和；五状态补零、7 日补零、窗口边界和空金额收成 `"0.00"` 都留在 Service。

## Rework on failure

失败只撤回看板与该读接口；订单 CRUD 仍在。重做本 spec。

## Comments

- 2026-10-13 — `/to-spec` step 4 落骨架。step 5 完成设计；step 6 双轴审查 PASS。Status → ready-for-human，待 Gate A。
- 2026-10-13 — Gate A 通过。Status → ready-for-agent。
