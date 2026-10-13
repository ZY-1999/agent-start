# 单 jar 启动与前端路由回退

Type: spec
Status: ready-for-human
Parent: #1
Blocked by: #3, #5, #6

## Goal

管理员用一条 `java -jar` 启动完整系统，刷新订单页和汇总页的深链接仍然可用。

## Acceptance criteria

- [x] 只执行 `java -jar` 后，订单管理页和汇总看板页都能完成各自的主路径（分页列表与新建、看板三块统计、重置数据），且 jar 内修改保存与确认删除仍可用，无需再单独启动前端。手工走查。证明交付形态是单 jar 内的完整系统。
- [x] 直接打开并刷新订单管理页、汇总看板页的地址时，页面仍可使用。手工走查。证明 SPA 深链接和刷新可用。
- [x] 请求一个非 `/api` 的未知路径时返回前端入口页面。可用 MockMvc 核对这一响应。证明 jar 内的未知前端路径会回退到单页入口。

## Scope

- **In**: 前端构建产物嵌入后端并随单 jar 提供；非 `/api` 未知路径回退到前端入口；jar 内走查已有主路径。
- **Out**: 新的订单业务行为、CORS、认证、CI/CD。不把前序 spec 的 MockMvc 业务口径整段重验。

## Context

- 父 PRD：`.scratch/2026-10-13-order-admin/01-order-admin-system.md`。开发期前后端分离；交付期同源、单 jar、SPA fallback。
- 依赖 spec #3、#5、#6 都已落地后，修改/删除、汇总页和重置按钮才同时存在。
- 无 `CONTEXT.md`、无 ADR。

## Design

外部接缝仍是 HTTP。本 spec 不新增订单行为，只把已经存在的页面和接口收成一个可单进程启动的交付模块。深度在于：调用方只学「一条 `java -jar`」和「非 `/api` 的未知路径回到前端入口」；资源放在哪、哪些路径回退，都藏在模块里。删掉它，交付会裂成两个进程，深链接也会在刷新时丢失。

- **Interface delta**
  - 不新增、不修改 `/api` 下的订单接口。`/api` 未知路径仍然是接口的 404，不回退到前端入口。
  - 非 `/api` 且没有对应静态资源的路径（含直接打开或刷新 `/orders`、`/dashboard`，以及任意未知前端路径）返回前端入口文档，HTTP 200，内容是该入口而不是 `Result` JSON。
  - 已构建的静态资源按自身路径返回，不套这层回退。
  - 开发期前后端分离与 `/api` 代理保持不变。本 spec 只增加打成一个 jar 之后的形态：一个进程同时提供订单管理页、汇总看板页和已有接口。不配置 CORS。
  - 手工走查只覆盖 jar 内主路径：分页列表与新建、修改保存、确认删除、看板三块统计、重置数据，以及两个页面的直接打开和刷新。不把前序 spec 的 MockMvc 业务口径整段重跑。
  - 自动化只核对回退：MockMvc 请求一个非 `/api` 的未知路径，响应为前端入口文档。
- **Internal architecture**
  - 交付模块在构建时把前端产物带进同一个 jar。回退规则只有一条：路径不以 `/api` 开头，且不是已有静态资源，就返回入口文档。
  - 订单模块的接口保持原样。本 spec 不把业务规则搬进交付模块。

## Rework on failure

失败时去掉单 jar 打包与路径回退即可，分离运行的订单功能仍在。重做本 spec。

## Comments

- 2026-10-13 — 由原 #6 顺延。Gate A 追加 spec #6（演示种子与重置）后，本 spec 改为 #7，Blocked by 增加 #6。
- 2026-10-14 — `mvn -f backend/pom.xml -DskipTests package` 产出 `backend/target/order-admin-1.0.0.jar`。`java -jar` 在 `http://127.0.0.1:8080` 走查：列表 8 条、新建张验收/验收商品数量 2 单价 12.50 总金额 25.00（ORD20261014003453615）、改商品名为验收已改且订单号不变、确认删除后前缀搜索无数据、看板总订单数 8 / 总金额 3,515.16 / 待支付 2 / 已完成 2 且两块图、重置后订单号换为 ORD20261014004108xxx 仍为 8 条样本。直接打开并刷新 `/orders`（8 行）与 `/dashboard`（KPI 与两块图仍在）。`GET /not-a-page` 返回入口 HTML 200；`GET /api/missing` 为 404 JSON。MockMvc：`OrderControllerApiTest.unknownFrontendPathFallsBackToEntry` 通过。Status: ready-for-human。
