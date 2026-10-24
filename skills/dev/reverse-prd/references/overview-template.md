# 总览层模板

`00-overview.md`（PRD-01）。这一层只索引**文档**——哪个模块写到哪、覆盖了多少、按什么顺序读，不放业务内容。

它不索引代码。代码地形、文件清单、调用链一律在 `docs/codemap/`，两边对象不同，不重叠也不互抄。

## 模板

````markdown
# [产品名] 逆向 PRD · 总览

> 文档编号：PRD-01 ｜ 版本：v1.0（2026-10-23，基于 dev@41e9ecd 代码逆向整理）

## 文档信息

只写版本块里没有的信息——编号、版本、代码版本都在版本块里，不要在这里重复。

| 项 | 内容 |
|---|---|
| 代码地形 | `docs/codemap/`：项目图 1 份 + 每模块 1 份，见下表 CodeMap 列 |
| 覆盖度 | 已生成 4 / 共 5 个模块；未覆盖见 [A02](appendix/02-uncovered.md) |
| 待确认 | 4 条，按影响面排序见 [A01](appendix/01-open-questions.md) |

## 功能地图（路由 / 菜单树 → 模块映射）

| 模块编号 | 模块名 | 对应路由 / 菜单 | 文档 | CodeMap | 置信度 | 状态 |
|---|---|---|---|---|---|---|
| M01 | 用户认证 | `/login`, `/register`, `/auth/*` | [M01](modules/M01-用户认证.md) | [M01](../codemap/M01-用户认证.md) | 高 | 已完成 |
| M02 | 订单 | `/orders/*` | [M02](modules/M02-订单.md) | [M02](../codemap/M02-订单.md) | 中 | 进行中 |
| M03 | 支付 | `/pay/*`, `/callback/*` | [M03](modules/M03-支付.md) | [M03](../codemap/M03-支付.md) | 中 | 已完成 |
| M04 | 商品管理 | `/products/*` | [M04](modules/M04-商品管理.md) | [M04](../codemap/M04-商品管理.md) | 高 | 已完成 |
| M05 | 售后 | `/aftersale/*` | — | — | — | 未覆盖，见 [A02](appendix/02-uncovered.md) |

状态取值：`占位`（只有编号和路由）/ `进行中` / `已完成` / `未覆盖`。CodeMap 列为空而状态是"已完成"，说明该模块文档还没接上代码地形，属未完工。

## 全局文档

| 编号 | 文档 | 内容 |
|---|---|---|
| PRD-C01 | [角色与权限](common/01-roles-permission.md) | 角色定义、全局权限矩阵、数据可见范围 |
| PRD-C02 | [数据字典](common/02-data-dictionary.md) | 实体、字段、约束、ER |
| PRD-C03 | [枚举与状态机](common/03-enums-states.md) | 全局枚举、状态机总表 |
| PRD-C04 | [错误码](common/04-errors.md) | 错误码、异常类、用户提示 |
| PRD-C05 | [非功能需求](common/05-nfr.md) | 第三方集成、限流、配置项、定时任务 |
| PRD-C06 | [术语表](common/06-glossary.md) | 从常量与命名反推的业务词汇 |

## 建议阅读顺序

`00-overview` → `common/01` → `common/02` → `common/03` → 按 M01→M0N 顺序读模块 → `appendix/01`。

先读权限与数据字典，再看模块，能避免把"谁能做什么""字段什么意思"当成模块细节反复查。

待确认问题不在这里复述，一律看 [A01](appendix/01-open-questions.md)——它按影响面排好序，本文件再抄一份就是第二个真相。
````
