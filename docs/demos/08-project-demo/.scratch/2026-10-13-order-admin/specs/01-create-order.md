# 创建订单并证明落库

Type: spec
Status: ready-for-agent
Parent: #1
Blocked by: None — can start immediately

## Goal

管理员能录入一条订单，系统生成唯一订单号、由后端算好总金额，并在真实 MySQL 中留下可核对的行。

## Acceptance criteria

- [ ] 提交客户名称、商品名称、数量、单价、五个状态之一和备注后，MockMvc 响应与 `order_db_test` 中的新行一致（含订单号、状态、总金额、`create_time`）。测试接缝为 MockMvc + 真实 MySQL `order_db_test`，每轮先 Flyway clean+migrate。证明创建已经落库。
- [ ] 订单号由后端在创建时生成，形态为 `ORD` + `yyyyMMddHHmmss` + 3 位随机数字，并由 `uk_order_no` 保证唯一；再次写入相同订单号被拒绝。证明编号不靠人工、唯一性有库约束兜底。
- [ ] 总金额等于数量 × 单价、按 HALF_UP 保留两位，单价与总金额以 `DECIMAL(10,2)` 落库；调用方另行传入的总金额不影响库中结果。证明金额只信后端计算。
- [ ] 创建时 `PENDING` / `PAID` / `SHIPPED` / `COMPLETED` / `CANCELLED` 均可直接保存。证明创建不做状态流转校验。
- [ ] 空的 `order_db_test` 只跑迁移就能完成上述创建，且下一轮 clean+migrate 后行为相同。证明表结构由可重复迁移建立，而不是手工建库。
- [ ] 手工：订单管理页打开新建弹窗，提交后能看到系统生成的订单号和后端算出的总金额。

## Scope

- **In**: 单表订单的可重复迁移、创建订单（订单号、后端总金额、五状态直存）、最小新建弹窗，以及上述行为的 MockMvc + `order_db_test` 测试。
- **Out**: 分页、搜索、筛选、修改、逻辑删除、汇总看板、单 jar 打包与 SPA 深链接回退。

## Context

- 父 PRD：`.scratch/2026-10-13-order-admin/01-order-admin-system.md`（Implementation Decisions、Testing Decisions、Out of Scope）。
- 仓库规范：`docs/agents/数据库规范.md`（必备三字段、`is_deleted`、索引命名）、`docs/agents/JAVA开发规范.md`（分层取 Web / Service / DAO，省 Manager）。
- 仓库为空：无既有代码、无 `CONTEXT.md`、无 ADR。本 spec 建立首条垂直切片。
- 测试只验证 HTTP 输入输出与数据库最终状态；前端不加单元测试。

## Design

外部接缝只有 HTTP：测试与页面都走这一处，不另设可替换的仓储接缝。订单创建是一个深模块——一次创建把订单号生成、撞号重试和金额舍入挡在调用方外面；删掉它，这些规则会散回每个调用方。

- **Interface delta**
  - 统一包装 `Result<T>`：`code`（int，成功为 0）、`message`（string）、`data`（T 或空）。后续 spec 沿用，不再另起包装。成功为 HTTP 200 且 `code = 0`。缺必填字段或类型不合法为 HTTP 400；创建重试耗尽仍撞上唯一约束为 HTTP 409。这两种失败的 `data` 为空。
  - `POST /api/orders`。请求体：`customerName`、`productName`、`quantity`（整数）、`unitPrice`、`status`、可选 `remark`。`orderNo` 与 `totalAmount` 若出现则忽略。
  - 成功时 `data` 为订单：`id`、`orderNo`、`customerName`、`productName`、`quantity`、`unitPrice`、`totalAmount`、`status`、`remark`、`createTime`、`updateTime`。金额以保留两位的十进制字符串返回（如 `"12.30"`），避免 JSON 数字丢掉刻度。时间是服务器本地时区的 `yyyy-MM-dd HH:mm:ss`，不带偏移。响应不暴露逻辑删除标记。
  - `status` 只接受 `PENDING`、`PAID`、`SHIPPED`、`COMPLETED`、`CANCELLED`，创建时任一可直接保存。
  - `orderNo` 由模块在创建时生成：`ORD` + `yyyyMMddHHmmss` + 3 位随机数字，共 20 位。撞上唯一约束则换号重试，最多 3 次，仍失败则 409，不把重复号返回给调用方。
  - `totalAmount = quantity × unitPrice`，`HALF_UP` 保留 2 位。不采用请求里的总金额。
  - 表 `t_order`（本 spec 用可重复迁移建出；新建行 `is_deleted = 0`）：
    - `id` bigint unsigned 自增，主键索引 `pk_id`
    - `order_no` char(20) 非空，唯一索引 `uk_order_no`（定长，不做前缀索引）
    - `customer_name` varchar(64) 非空
    - `product_name` varchar(128) 非空
    - `quantity` int 非空
    - `unit_price` decimal(10,2) 非空
    - `total_amount` decimal(10,2) 非空
    - `status` varchar(16) 非空
    - `remark` varchar(255) 可空
    - `is_deleted` unsigned tinyint 非空，默认 0（1 是 / 0 否）；持久化对象的属性名是 `deleted`，映射到 `is_deleted`
    - `create_time`、`update_time` datetime 非空
  - 无外键。查询列必须写明，不用 `*`。
  - 页面：`/orders` 上的新建弹窗。提交成功后展示返回的 `orderNo` 与 `totalAmount`。开发期前端把 `/api` 代理到后端 8080。本 spec 不提供列表。
  - 运行：compose 的 MySQL 8 监听宿主 3307，预建 `order_db` 与 `order_db_test`。应用默认连 `order_db`。测试每轮对 `order_db_test` 做 Flyway clean + migrate。口令来自环境变量或 git 忽略的本地配置，不写入 spec、不入库。
- **Internal architecture**
  - 模块内部分层为 Web → Service → DAO，无 Manager。订单号、舍入和撞号重试放在 Service，不泄漏到 Web 或页面。
  - 逻辑删除的「查询自动排除」不在本 spec 打开。本 spec 只保证新建行标记为未删除；排除行为由 spec #4 加到读路径上。
  - 测试只穿过 MockMvc，并核对 `order_db_test` 中的行。唯一性用「同一 `order_no` 写第二次被拒绝」证明，不把订单号变成请求入参。

## Rework on failure

失败隔离在本 spec；此时还没有后续 spec 的实现。重做本 spec 即可。

## Comments

- 2026-10-13 — `/to-spec` step 4 落骨架（Status: needs-design）。分解 winner 为 candidate-1（按风险的 6 条垂直切片，裁判第 2 轮 PASS）。
- 2026-10-13 — step 5 完成设计；step 6 双轴审查 PASS。Status → ready-for-human，待 Gate A。
- 2026-10-13 — Gate A 通过。Status → ready-for-agent。
