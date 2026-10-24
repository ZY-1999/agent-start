# 模块文档模板

每个模块一份，复制后替换 `M02` / `订单` / 路径。文档编号按 `PRD-M<两位模块号>`。

## 写之前

本模块必须已有自己的 feature CodeMap：`docs/codemap/M02-订单.md`（`/codemap` feature 模式生成）。建完先抽查入口路由、controller 方法、表名与代码是否一致，**图不准就先修图，不要绕过它写文档**。模块文档里的 CodeMap 链接指向这份图，不指向项目图，也不指向任何共享的代码索引。

## 证据写法

统一写 **`仓库根相对路径` + `#` + `函数/方法名`**，行号只作提示且可省略：

```text
src/services/order.service.ts#createOrder        ✅ 函数改名可定位，行号漂移不影响
src/services/order.service.ts:45                 ⚠️ 行号会漂移，仅作提示
../src/services/order.service.ts                 ❌ 不写相对文档的路径
```

深层调用链额外标注本模块 CodeMap 的节点，读者要顺藤摸瓜时去那里：

```text
CodeMap: docs/codemap/M02-订单.md（节点见该图的 Main Flow 节点）
```

## 模板

````markdown
# M02 · 订单模块

> 文档编号：PRD-M02 ｜ 版本：v1.0（2026-10-23，基于 dev@41e9ecd 代码逆向整理）
> 上级总览：[00-overview](../00-overview.md) ｜ 置信度：中 ｜ CodeMap：[M02](../../codemap/M02-订单.md)

本文只写本模块独有内容。角色、字段、错误码、枚举请引用 `common/` 下的权威文档，不要复制表格。

## 1. 模块概述

- **职责**：一句话说明本模块解决什么用户问题（从入口页面与主流程推断，不写实现细节）
- **入口**：

  | 类型 | 路径 | 证据 |
  |---|---|---|
  | 页面路由 | `/orders/list` | `router/order.ts#routes` |
  | API | `POST /api/orders` | `controllers/order.ts#create` |
  | 入口按钮 | 商品详情页"立即购买" | `pages/product/detail.tsx#BuyButton` |

- **上游/下游依赖**：依赖 M04 商品、M03 支付；被 M05 售后依赖

## 2. 角色与权限（本模块视角）

只列本模块涉及的角色与操作，判定逻辑见 [C01](../common/01-roles-permission.md#3-全局权限矩阵)。

| 角色 | 可执行操作 | 权限判定位置 | 详见 |
|---|---|---|---|
| 买家 | 下单、取消、查询自己的订单 | `middleware/auth.ts#requireBuyer` | [C01](../common/01-roles-permission.md#3-全局权限矩阵) |
| 管理员 | 改价、强制关闭 | `decorators/role.ts#Role` | [C01](../common/01-roles-permission.md#3-全局权限矩阵) |

## 3. 功能清单

| 编号 | 功能 | 触发方式 | 置信度 | 证据 |
|---|---|---|---|---|
| F02-01 | 创建订单 | `POST /api/orders` | 高 | `services/order.service.ts#createOrder` |
| F02-02 | 取消订单 | `POST /api/orders/:id/cancel` | 中 | `services/order.service.ts#cancelOrder` |

置信度：`高` = 有测试或明确代码路径支撑；`中` = 代码路径完整但意图靠命名推断；`低` = 只有片段证据，需人工确认。

## 4. 功能详述

每个功能一节。逻辑简单可写成一段话，但**业务规则表不可省**。

### F02-01 创建订单

- **触发**：买家在商品详情页点击"立即购买"
- **主流程**：校验库存 → 计算价格（含优惠券）→ 落单 → 锁库存 → 发超时取消延迟消息
  - 依据：`services/order.service.ts#createOrder` 调用链，见 CodeMap [M02](../../codemap/M02-订单.md)
- **业务规则**：

  | 编号 | 规则 | 证据 | 置信度 |
  |---|---|---|---|
  | R02-01 | 单用户同时未支付订单上限 5 单 | `constants.ts#MAX_PENDING_ORDERS` | 中 |
  | R02-02 | 库存不足直接拒绝，不做预售 | `services/order.service.ts#createOrder` | 高 |
  | R02-03 | 同一 SKU 不可重复下单 | `migrations/003.sql` | 高 |

- **字段级校验**（只写校验规则本身，提示文案的权威在 [C04](../common/04-errors.md)，不在此重复）：

  | 字段 | 规则 | 证据 |
  |---|---|---|
  | quantity | 1~99 整数 | `validators/order.ts#quantity` |

- **异常与边界**（只列本模块抛出的错误码与触发场景，文案与异常类见 [C04](../common/04-errors.md)）：

  | 错误码 | 触发场景 |
  |---|---|
  | E2001 | 库存不足 |
  | E2002 | 超出未支付订单上限 |

- **验收标准**（优先从测试用例转写；无测试时写可手工执行的步骤）：

  - Given 库存为 0，When 提交订单，Then 返回 E2001 且订单不落库（`__tests__/order.spec.ts#库存不足`）
  - Given 已有 5 笔未支付订单，When 再下单，Then 返回 E2002

### F02-02 取消订单

（同上结构）

## 5. 模块状态机

总枚举与全局状态定义见 [C03](../common/03-enums-states.md)。本节展开本模块的转移细节。

| 当前状态 | 触发动作 | 目标状态 | 副作用 | 证据 |
|---|---|---|---|---|
| 待支付 | 用户取消 | 已取消 | 释放库存 | `services/order.service.ts#cancelOrder` |
| 待支付 | 30 分钟超时 | 已关闭 | 释放库存 | `jobs/orderTimeout.ts#handle` |

## 6. 模块数据

只列本模块读写的实体与字段，字段定义见 [C02](../common/02-data-dictionary.md)。

| 实体 | 本模块使用的字段 | 读写 | 说明 |
|---|---|---|---|
| Order | id, user_id, status, amount | 读写 | 主实体 |
| Stock | sku_id, locked | 写 | 下单锁库存 |

- 本模块新增的约束/索引：见 [C02 §3](../common/02-data-dictionary.md#3-索引与唯一约束)（定义与证据在那里，本节不重复列）
- 本模块的审计/软删约定：`deleted_at` 软删，见 [C02](../common/02-data-dictionary.md#4-软删与审计)

## 7. 接口清单

| 方法 | 路径 | 对应功能 | 鉴权 | 关键参数 | 证据 |
|---|---|---|---|---|---|
| POST | `/api/orders` | F02-01 | JWT + 买家 | sku_id, quantity | `controllers/order.ts#create` |
| POST | `/api/orders/:id/cancel` | F02-02 | JWT + 本人 | id | `controllers/order.ts#cancel` |

## 8. 非功能关注点

只写"本模块受哪些约束影响"，约束本身在 [C05](../common/05-nfr.md)。

- 下单接口限流：受 C05 的全局 100 req/min 约束
- 超时任务：延迟队列，精度 ±1min（`jobs/orderTimeout.ts`）

## 9. 待确认

本节条目会汇总进 [A01](../appendix/01-open-questions.md)，编号格式 `Q-M<模块号>-<序号>`。

| # | 类型 | 描述 | 证据 | 建议确认人 |
|---|---|---|---|---|
| Q-M02-1 | 代码缺陷 | 取消订单未回滚库存 | `services/order.service.ts#cancelOrder` | 后端负责人 |
| Q-M02-2 | 需求意图不明 | 为何限 5 单 | `constants.ts#MAX_PENDING_ORDERS` | 产品经理 |

类型取值：`代码缺陷` / `需求意图不明` / `文档缺口`（代码里找不到证据，但业务上应该有）。
````
