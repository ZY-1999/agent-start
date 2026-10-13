# 修改订单

Type: spec
Status: ready-for-agent
Parent: #1
Blocked by: #2

## Goal

管理员能改已有订单的资料和任意状态，订单号保持不变，总金额仍由后端重算。

## Acceptance criteria

- [ ] 修改客户名称、商品名称、数量、单价、备注后，列表结果与 `order_db_test` 中的行都变为新值，订单号仍是创建时的值；数量或单价变化时总金额按同一 HALF_UP、两位小数规则重算，仍不采用调用方传入的总金额。测试接缝为 MockMvc + 真实 MySQL `order_db_test`（Flyway clean+migrate）。证明更新落库且金额规则与创建一致。
- [ ] 五个状态可任意互改，包括从 `CANCELLED` 改回 `PENDING`、从 `COMPLETED` 改到 `SHIPPED`。证明修改不做状态机校验。
- [ ] 手工：编辑弹窗可修改资料和状态并看到更新后的列表。

## Scope

- **In**: 按 id 修改订单资料与状态、后端重算总金额、编辑弹窗。
- **Out**: 订单号重新生成、状态机校验、逻辑删除、汇总统计、单 jar 打包。

## Context

- 父 PRD：`.scratch/2026-10-13-order-admin/01-order-admin-system.md`。五状态自由切换，总金额不信任前端。
- 依赖 spec #1 的创建与金额规则、spec #2 的列表读路径（修改后的列表结果要能被读到）。
- 无 `CONTEXT.md`、无 ADR。

## Design

外部接缝只有 HTTP：测试与页面都走这一处，不另设可替换的仓储接缝。订单修改是一个深模块——接口是一次按 id 提交可改字段，深度在于一次修改藏起订单号不可变、金额重算和无状态机。删掉这个模块，这三条规则会散回每个调用方。

- **Interface delta**
  - 沿用 `Result<T>`：`code`（int，成功为 0）、`message`（string）、`data`（T 或空）。成功为 HTTP 200 且 `code = 0`。失败时 `data` 为空。
  - `PUT /api/orders/{id}`。请求体与创建相同的可改字段：`customerName`、`productName`、`quantity`（整数）、`unitPrice`、`status`、可选 `remark`。这一组字段整份替换。请求中的 `orderNo` 与 `totalAmount` 忽略。
  - 成功时 `data` 为更新后的订单，形态与创建相同：`id`、`orderNo`、`customerName`、`productName`、`quantity`、`unitPrice`、`totalAmount`、`status`、`remark`、`createTime`、`updateTime`。`id` 与 `orderNo` 不变。`createTime` 保持创建时的值，`updateTime` 刷新。金额以保留两位的十进制字符串返回（如 `"12.30"`）。时间是服务器本地时区的 `yyyy-MM-dd HH:mm:ss`，不带偏移。响应不暴露逻辑删除标记。
  - `totalAmount = quantity × unitPrice`，`HALF_UP` 保留 2 位，与创建共用同一规则。不采用请求里的总金额。
  - `status` 只接受 `PENDING`、`PAID`、`SHIPPED`、`COMPLETED`、`CANCELLED`。五个状态可任意互改，无状态机。
  - `remark` 可省略或为空，省略与空都记为空备注。
  - id 不存在：HTTP 404。缺必填、类型不合法或 `status` 非法：HTTP 400。本 spec 不处理逻辑删除。
  - 页面：列表行打开编辑弹窗，保存后列表显示新值。列表读路径已由 spec #2 承担；本 spec 只按创建时的订单形态返回更新结果。页面展示返回值，不重算金额。
  - 测试穿过 MockMvc，并核对库中更新后的行。
- **Internal architecture**
  - 模块内部分层为 Web → Service → DAO，无 Manager。金额重算与创建共用同一规则，放在 Service，不泄漏到 Web 或页面。
  - 修改不生成、不覆盖订单号。

## Rework on failure

失败只回退修改路径；创建与列表查询仍在。重做本 spec。

## Comments

- 2026-10-13 — `/to-spec` step 4 落骨架。step 5 完成设计；step 6 双轴审查 PASS。Status → ready-for-human，待 Gate A。
- 2026-10-13 — Gate A 通过。Status → ready-for-agent。
