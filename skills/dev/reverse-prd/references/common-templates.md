# 全局层模板（common/）

六份文档，编号 `PRD-C01`–`PRD-C06`。**先出这六份，再写模块**——模块文档引用它们，顺序反了必然返工。

每份都是"权威版本"：模块文档只能链接，不能复制表格。每份末尾留 `待确认`，条目格式 `Q-C<编号>-<序号>`。

---

## C01 · 角色与权限

````markdown
# 角色与权限

> 文档编号：PRD-C01 ｜ 版本：v1.0（2026-10-23，基于 dev@41e9ecd 代码逆向整理）

## 1. 角色定义

| 角色 | 代码中的标识 | 来源 | 证据 |
|---|---|---|---|
| 买家 | `role=1` / `BUYER` | 用户表 `role` 字段 | `entities/user.ts#User.role` |
| 管理员 | `role=9` / `ADMIN` | 用户表 `role` 字段 | `entities/user.ts#User.role` |
| 运营 | `role=5` | 后台账号体系 | `modules/admin/auth.ts#login` |

角色若分散在多处（前台 token、后台账号、第三方回调），逐个列来源；不要合并成一个"角色"含糊带过。

## 2. 权限判定链路

说明请求进入后权限怎么判：中间件 → 守卫 → 装饰器 → 业务内二次校验，按实际顺序写。

| 顺序 | 位置 | 作用 | 失败表现 |
|---|---|---|---|
| 1 | `middleware/auth.ts#verifyJwt` | 解析 token，写 `req.user` | 401 |
| 2 | `decorators/role.ts#Role` | 比对角色白名单 | 403 |
| 3 | `services/order.service.ts#findById` | 校验资源归属 | 404（不暴露存在性） |

## 3. 全局权限矩阵

| 操作 | 买家 | 管理员 | 运营 | 判定位置 |
|---|---|---|---|---|
| 创建订单 | ✅ | ❌ | ❌ | `decorators/role.ts#Role` |
| 强制关闭订单 | ❌ | ✅ | ❌ | `controllers/admin/order.ts#close` |

矩阵按"操作"行展开，不按"页面"列——页面会改，操作相对稳定。

## 4. 数据可见范围

| 角色 | 可见数据范围 | 实现方式 | 证据 |
|---|---|---|---|
| 买家 | 仅本人订单 | 查询条件注入 `user_id` | `services/order.service.ts#list` |
| 管理员 | 全部 | 无过滤 | `services/order.service.ts#list` |

跨组织 / 跨租户的数据可见性是内部系统最常见的越权点，必须写清是"代码过滤"还是"数据库行级策略"。

## 5. 待确认

| # | 类型 | 描述 | 证据 | 建议确认人 |
|---|---|---|---|---|
| Q-C01-1 | 需求意图不明 | 运营角色为何可改价 | `controllers/admin/order.ts#updatePrice` | 产品经理 |
````

---

## C02 · 数据字典

````markdown
# 数据字典

> 文档编号：PRD-C02 ｜ 版本：v1.0（2026-10-23，基于 dev@41e9ecd 代码逆向整理）

## 1. 实体总览

```text
User 1 ──< Order 1 ──< OrderItem >── 1 Sku >── 1 Product
```

关系图用手写 ASCII 即可，不必引入绘图工具。

| 实体 | 表名 | 职责 | 证据 |
|---|---|---|---|
| Order | `t_order` | 订单主表 | `migrations/001.sql` |

## 2. 实体明细

每个实体一节，字段表必须含约束与证据。

### Order

| 字段 | 类型 | 可空 | 默认 | 约束 | 说明 | 证据 |
|---|---|---|---|---|---|---|
| id | bigint | 否 | 自增 | PK | 主键 | `migrations/001.sql` |
| user_id | bigint | 否 | — | FK→User | 下单人 | `entities/order.ts#Order.userId` |
| status | tinyint | 否 | 0 | 枚举见 C03 | 订单状态 | `entities/order.ts#OrderStatus` |
| amount | decimal(10,2) | 否 | — | ≥0 | 实付金额 | `entities/order.ts#Order.amount` |

## 3. 索引与唯一约束

| 索引名 | 表 | 字段 | 类型 | 用途 | 证据 |
|---|---|---|---|---|---|
| `uk_user_sku` | `t_order_item` | (user_id, sku_id) | 唯一 | 防重复下单 | `migrations/003.sql` |

## 4. 软删与审计

- 软删字段：`deleted_at`（`entities/base.ts#BaseEntity`）
- 创建/更新字段：`created_at` / `updated_at`，由 `middleware/audit.ts` 填充
- 是否有操作日志表：`t_operation_log`（写入位置 `middleware/audit.ts#log`）

## 5. 枚举字段

不在此展开，见 [C03 枚举与状态机](./03-enums-states.md)。

## 6. 待确认

| # | 类型 | 描述 | 证据 | 建议确认人 |
|---|---|---|---|---|
| Q-C02-1 | 文档缺口 | `ext_json` 字段无注释，用途不明 | `migrations/005.sql` | 后端负责人 |
````

---

## C03 · 枚举与状态机

````markdown
# 枚举与状态机

> 文档编号：PRD-C03 ｜ 版本：v1.0（2026-10-23，基于 dev@41e9ecd 代码逆向整理）

## 1. 枚举总表

| 枚举 | 取值 | 含义 | 定义位置 |
|---|---|---|---|
| OrderStatus | 0 待支付 / 1 已支付 / 2 已取消 / 3 已关闭 | 订单状态 | `entities/order.ts#OrderStatus` |
| PayChannel | 1 微信 / 2 支付宝 | 支付渠道 | `entities/pay.ts#PayChannel` |

取值、含义、定义位置三者必须齐——只有中文含义而不知代码值，模块文档无法与代码对齐。

## 2. 状态机总表

| 实体 | 状态集合 | 合法转移 | 驱动方 |
|---|---|---|---|
| Order | 0/1/2/3 | 0→1, 0→2, 0→3, 1→2 | 用户操作 / 支付回调 / 超时任务 |

本节只列"有哪些状态、谁驱动"。**转移细节（副作用、证据）在各模块 §5 展开**，不在此重复。

## 3. 状态与业务动作映射

| 状态 | 允许的动作 | 禁止的动作 | 判定位置 |
|---|---|---|---|
| 0 待支付 | 支付、取消 | 发货、退款 | `services/order.service.ts#assertStatus` |

## 4. 待确认

| # | 类型 | 描述 | 证据 | 建议确认人 |
|---|---|---|---|---|
| Q-C03-1 | 代码缺陷 | 状态 1 直接转 3，跳过 2，是否有意为之 | `jobs/orderTimeout.ts#handle` | 后端负责人 |
````

---

## C04 · 错误码

````markdown
# 错误码

> 文档编号：PRD-C04 ｜ 版本：v1.0（2026-10-23，基于 dev@41e9ecd 代码逆向整理）

## 1. 错误码总表

| 错误码 | 异常类 / 常量 | HTTP | 触发条件 | 用户提示 | 抛出位置 |
|---|---|---|---|---|---|
| E2001 | `StockNotEnoughException` | 200 | 库存不足 | "库存不足" | `services/order.service.ts#createOrder` |
| E2002 | `BizException(E2002)` | 200 | 未支付订单超限 | "待支付订单过多" | `services/order.service.ts#createOrder` |

内部系统常见"HTTP 200 + 业务码"风格，把 HTTP 列如实写出来，避免读者按状态码排查。

## 2. 异常处理链路

| 顺序 | 位置 | 作用 |
|---|---|---|
| 1 | `exceptions/BizException.ts` | 业务异常基类，携带 code + message |
| 2 | `middleware/errorHandler.ts#handle` | 统一转 `{code, message, data}` |
| 3 | 前端 `utils/request.ts#interceptor` | 按 code 分支提示 / 跳转 |

## 3. 提示文案来源

| 来源 | 位置 | 说明 |
|---|---|---|
| 后端硬编码 | `exceptions/*.ts` | 中文直接写在异常里 |
| 前端 i18n | `locales/zh-CN.ts` | 按 code 映射 |

## 4. 待确认

| # | 类型 | 描述 | 证据 | 建议确认人 |
|---|---|---|---|---|
| Q-C04-1 | 文档缺口 | E2003–E2010 已定义但无抛出点，是否废弃 | `constants/error.ts` | 后端负责人 |
````

---

## C05 · 非功能需求与外部依赖

````markdown
# 非功能需求与外部依赖

> 文档编号：PRD-C05 ｜ 版本：v1.0（2026-10-23，基于 dev@41e9ecd 代码逆向整理）

## 1. 第三方集成

| 服务 | 用途 | 配置项 | 失败降级 | 证据 |
|---|---|---|---|---|
| 微信支付 | 收款、退款 | `WX_MCH_ID` / `WX_KEY` | 下单失败即回滚订单 | `services/pay/wechat.ts#createPay` |
| 短信 | 验证码 | `SMS_KEY` | 无降级，直接报错 | `services/sms.ts#send` |

配置项只写 key 名，**不写值**。

## 2. 限流与配额

| 对象 | 阈值 | 实现 | 超限表现 |
|---|---|---|---|
| 全局 API | 100 req/min/IP | `middleware/rateLimit.ts` | 429 |

## 3. 缓存与异步

| 类型 | 键 / 队列 | TTL / 并发 | 用途 | 证据 |
|---|---|---|---|---|
| 缓存 | `order:{id}` | 300s | 订单详情 | `services/cache.ts#get` |
| 延迟队列 | `order:timeout` | — | 30 分钟未支付自动关闭 | `jobs/orderTimeout.ts#handle` |

## 4. 定时任务

| 任务 | 频率 | 作用 | 幂等 | 证据 |
|---|---|---|---|---|
| 关闭超时订单 | 每分钟 | 扫描并关闭 | 是（按状态条件更新） | `jobs/orderTimeout.ts#handle` |

## 5. 配置项清单

| key | 默认值 | 环境 | 影响 |
|---|---|---|---|
| `ORDER_TIMEOUT_MINUTES` | 30 | 全部 | 超时关单时间 |

## 6. 待确认

| # | 类型 | 描述 | 证据 | 建议确认人 |
|---|---|---|---|---|
| Q-C05-1 | 需求意图不明 | 限流阈值 100 是否按用户还是按 IP | `middleware/rateLimit.ts#limit` | 运维 |
````

---

## C06 · 术语表

````markdown
# 术语表

> 文档编号：PRD-C06 ｜ 版本：v1.0（2026-10-23，基于 dev@41e9ecd 代码逆向整理）

从常量、类名、字段名反推业务词汇。代码里的写法必须保留原样——这是与源码对照的唯一锚点。

## 1. 术语对照

| 术语 | 代码中的写法 | 含义 | 出处 |
|---|---|---|---|
| 锁库存 | `lockStock` | 下单时预占库存，支付后转实扣 | `services/stock.service.ts#lockStock` |
| 超时关单 | `closeTimeoutOrder` | 待支付超 30 分钟自动关闭 | `jobs/orderTimeout.ts#handle` |

## 2. 同义词冲突

代码里两个词指同一件事、或一个词指两件事，单独列出——这是模块拆分时的常见坑：

| 现象 | 位置 A | 位置 B | 建议 |
|---|---|---|---|
| `cancel` 既指用户取消也指超时关闭 | `order.service.ts#cancelOrder` | `jobs/orderTimeout.ts` | 文档统一为"取消"与"关闭"，见 C03 |

## 3. 待确认

| # | 类型 | 描述 | 证据 | 建议确认人 |
|---|---|---|---|---|
| Q-C06-1 | 需求意图不明 | `refund` 既指售后退款也指打款重试，对外口径未定 | `services/refund.service.ts#retry` | 产品经理 |
````
