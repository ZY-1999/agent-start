# order-admin CodeMap (project)

> 范围：`docs/demos/08-project-demo/`（订单管理演示：Spring Boot 后端 + React 前端，单 jar 部署）。
> 路径均相对本 demo 目录。

## 1. Orientation

- Last updated: `2026-10-14`
- Project: order-admin（08-project-demo）
- Role / responsibility: 订单管理演示应用——订单 CRUD、CSV 导出、汇总看板、示例数据重置；后端同时托管前端静态资源
- Main languages / frameworks: Java 8（Spring Boot 2.7.18 + MyBatis-Plus 3.5.7 + Flyway）/ TypeScript（React 18 + antd 5 + ECharts + Vite）
- Runtime / deployment shape: 单 jar（`mvn package` 时自动 npm build 并把 `frontend/dist` 拷进 `classpath:/static`）；MySQL 8（docker-compose，宿主 3307）
- Primary entry types: REST API（`/api/orders` 系列）、SPA 路由（`/orders`、`/dashboard`）、启动 Runner（示例数据）
- Confidence:
  - confirmed: 全部条目均直接读源码核实（backend 29 个 Java 文件、frontend src 全部、pom、docker-compose、迁移 SQL）
  - inferred: 无
  - unknown: CI 流水线是否存在（仓库内无 CI 配置）

## 2. Context Tree

```text
Node: order-admin
  -> Node: Capability Index
  -> Node: Module Index
  -> Node: Entry Index
  -> Node: Domain And Data
  -> Node: External Dependencies
  -> Node: Cross-Module Flows
  -> Node: Validation
  -> Node: Risk Areas
  -> Node: Feature CodeMap Backlog
```

### Node: order-admin

- Type: `project`
- Status: `confirmed`
- Purpose: 订单管理演示，前后端同仓、单进程部署
- Read First:
  - `backend/pom.xml`: 依赖与打包链（npm build + dist 拷贝）
  - `backend/src/main/resources/application.yml`: 端口 8080、数据源、`app.demo.seed-on-startup`
  - `docker-compose.yml`: 本地 MySQL（3307→3306，root/order_demo，建 `order_db` / `order_db_test`）
- Edges / Children: 见 Context Tree
- Evidence: 上述文件直接读取
- Unknowns: 无 CI 配置（未见 `.github/`、`Jenkinsfile` 等）
- Next Drill-Down: 首次接手先读 `OrderController` 与 `OrderMapper.xml`，再看 `frontend/src/pages/OrdersPage.tsx`

### Node: Capability Index

- Type: `capability`
- Status: `confirmed`
- Purpose: 五大能力路由
- Children:
  - 订单 CRUD（建/查/改/逻辑删）:
    - Main modules: `web/OrderController`、`service/impl/OrderServiceImpl`、`dao/OrderMapper`
    - Entry: `POST|GET|PUT|DELETE /api/orders`
    - Feature CodeMap: pending
    - Status: `confirmed`
  - 订单导出 CSV:
    - Main modules: `web/OrderController#export`、`web/OrderCsv`、`service OrderService#export`、`dao OrderMapper#selectActiveAll`
    - Entry: `GET /api/orders/export?kw=&status=`
    - Feature CodeMap: pending
    - Status: `confirmed`（2026-10-14 新增，提交 72eedeb 系列）
  - 订单导出 Excel:
    - Main modules: `web/OrderController#exportXlsx`、`web/OrderExcel`、`service OrderService#export`、`dao OrderMapper#selectActiveAll`
    - Entry: `GET /api/orders/export.xlsx?kw=&status=`
    - Feature CodeMap: pending
    - Status: `confirmed`（2026-10-14 新增，EasyExcel 3.3.4）
  - 汇总看板:
    - Main modules: `service/impl/OrderStatsServiceImpl`、`frontend/src/pages/DashboardPage.tsx`
    - Entry: `GET /api/orders/stats`、UI `/dashboard`
    - Feature CodeMap: pending
    - Status: `confirmed`
  - 示例数据 / 重置:
    - Main modules: `service/DemoDataService`、`service/DemoSeedRunner`
    - Entry: `POST /api/orders/reset`、启动 Runner、UI 头部「重置数据」按钮
    - Feature CodeMap: pending
    - Status: `confirmed`
  - SPA 托管 / 前端回退:
    - Main modules: `web/SpaResourceConfig`
    - Entry: 非 `/api` 未知路径回退 `static/index.html`
    - Feature CodeMap: pending
    - Status: `confirmed`
- Evidence: 各 Controller/Service 源码
- Unknowns: 无
- Validation: 见 Node: Validation
- Next Drill-Down: 深链路（撞号重试、CSV 转义）建议按 Backlog 建 feature 地图

### Node: Module Index

- Type: `module`
- Status: `confirmed`
- Purpose: 后端分包 + 前端结构
- Children:
  - `backend …/common/`: `Result`（code/message/data 统一响应）、`OrderStatus`（五状态枚举 + 中文 label/labelOf）
    - Risk notes: 无
  - `backend …/dao/`: `OrderMapper`（XML 手写 SQL 为主）+ `entity/Order`（`t_order` 行）+ `StatusAmountRow`/`TrendAmountRow`（聚合行）
    - Key dependencies: MyBatis-Plus `BaseMapper`（仅 `insert` 用到）
    - Risk notes: 查询 SQL 全在 `resources/mapper/OrderMapper.xml`，改字段需同步 XML 列清单
  - `backend …/service/`: 接口层 `OrderService`/`OrderStatsService` + `impl/`；命令与异常（`CreateOrderCommand`、`BadOrderRequestException`、`OrderConflictException`、`OrderNotFoundException`）；`Amounts`（金额舍入）；`DemoDataService`/`DemoSeedRunner`
    - Risk notes: `OrderServiceImpl` 创建用 `REQUIRES_NEW` 独立事务支持撞号换号重试
  - `backend …/web/`: `OrderController`（8 个端点）、DTO（`CreateOrderRequest`/`OrderResponse`/`OrderPageResponse`/`ResetResponse`）、`GlobalExceptionHandler`（400/404/409 统一 Result）、`SpaResourceConfig`、`OrderCsv`（包级 CSV 组装）、`OrderExcel`（包级 Excel 组装，EasyExcel 注解行模型 + 数值单元格）
    - Risk notes: 非法状态在 Controller 层拦截（分页返回空页、导出返回仅表头）
  - `frontend/src/`: `api/order.ts`（axios 封装 + 类型）、`pages/OrdersPage.tsx`、`pages/DashboardPage.tsx`、`layout/AppShell.tsx`（菜单 + 重置）、`status.ts`（状态中文映射）、`App.tsx`（路由）
    - Key dependencies: antd 5（zhCN locale）、echarts、axios、react-router-dom
    - Risk notes: vite dev 代理 `/api` → 8080
- Evidence: 源码目录遍历 + 全文件阅读
- Unknowns: 无
- Validation: `mvn test`（见 Validation 节）
- Next Drill-Down: 改接口先看 `OrderController` 与前端 `api/order.ts` 的对应关系

### Node: Entry Index

- Type: `entry`
- Status: `confirmed`
- Purpose: 全部对外入口
- Entries:
  - APIs / controllers（`backend …/web/OrderController.java`，基路径 `/api/orders`）:
    - `POST /api/orders` → `create`（`@Valid CreateOrderRequest`）
    - `GET /api/orders?kw=&status=&page=&size=` → `page`（size 仅 10/20/50）
    - `GET /api/orders/export?kw=&status=` → `export`（CSV 附件，UTF-8 BOM）
    - `GET /api/orders/export.xlsx?kw=&status=` → `exportXlsx`（.xlsx 附件，数值单元格）
    - `PUT /api/orders/{id}` → `update`（订单号不变）
    - `DELETE /api/orders/{id}` → `delete`（逻辑删）
    - `GET /api/orders/stats` → `stats`（KPI + 状态分布 + 近 7 日趋势）
    - `POST /api/orders/reset` → `reset`（重写 8 条示例）
  - UI / routes（`frontend/src/App.tsx`）: `/` → redirect `/orders`；`/orders` → `OrdersPage`；`/dashboard` → `DashboardPage`
  - Jobs / schedulers: `DemoSeedRunner`（ApplicationRunner，`app.demo.seed-on-startup`，默认 true；test profile 关闭）
  - CLI / commands / consumers / library exports: 无
- Evidence: Controller 源码 + App.tsx
- Unknowns: 无
- Validation: `OrderControllerApiTest` 覆盖全部端点
- Next Drill-Down: 错误码语义见 `GlobalExceptionHandler`（400 校验/参数、404 不存在、409 撞号耗尽）

### Node: Domain And Data

- Type: `object`
- Status: `confirmed`
- Purpose: 核心领域对象与存储
- Children:
  - Core domain objects: `Order`（entity）、`CreateOrderCommand`（建/改共用命令）、`OrderPage`、`OrderStats`
  - Database tables / models: `t_order`（`backend/src/main/resources/db/migration/V1__create_t_order.sql`）；字段 id/order_no/customer_name/product_name/quantity/unit_price/total_amount/status/remark/is_deleted/create_time/update_time；`UNIQUE KEY uk_order_no`
  - State machines: 无显式状态机——`OrderStatus` 五态（PENDING/PAID/SHIPPED/COMPLETED/CANCELLED）创建时任选、更新时直接覆盖，无迁移约束
  - Important enums / statuses: `OrderStatus`（含中文 label；`labelOf` 未知码原样返回）
  - Config namespaces: `app.demo.seed-on-startup`；`spring.datasource`（`${ORDER_DB_PASSWORD:}` 环境变量）；`mybatis-plus.mapper-locations`
- Evidence: 迁移 SQL + 实体/枚举源码
- Unknowns: 无
- Validation: 金额两位小数 HALF_UP（`Amounts`）；查询排序恒 `create_time DESC, id DESC`（分页与导出一致）
- Next Drill-Down: SQL 语义（前缀 LIKE 转义、逻辑删除过滤）集中在 `OrderMapper.xml` 的 `activeFilter`

### Node: External Dependencies

- Type: `dependency`
- Status: `confirmed`
- Purpose: 进程外依赖
- Children:
  - Storage: MySQL 8（`docker-compose.yml`，宿主 3307；主库 `order_db`、测试库 `order_db_test`；root 密码 `order_demo`）
  - Build-time: `npm.cmd run build`（exec-maven-plugin，prepare-package 阶段）+ dist 拷贝（maven-resources-plugin）
  - RPC / MQ / 第三方 SDK / Auth / Observability: 无
  - Edges:
    - used by: 全部后端能力（MyBatis 连 MySQL）；前端构建链
    - failure surfaces: MySQL 未起 → 启动失败 / 测试连不上；npm 不可用 → package 失败
- Evidence: pom.xml + docker-compose.yml + application*.yml
- Unknowns: 无
- Validation: `docker compose up -d` 后 healthcheck 通过再跑测试
- Next Drill-Down: 本机构建环境坑见 `PROJECT_KNOWLEDGE.md`

### Node: Cross-Module Flows

- Type: `flow`
- Status: `confirmed`
- Purpose: 主要跨模块链路
- Major Flows:
  - 创建订单: `OrdersPage 表单` → `api/order.ts#createOrder` → `OrderController#create` → `OrderServiceImpl#create`（`nextOrderNo`=ORD+yyyyMMddHHmmss+3位随机；`DuplicateKeyException` 最多重试 3 次，`REQUIRES_NEW` 独立事务）→ `OrderMapper.insert`
  - 导出 CSV: `OrdersPage 导出下拉·CSV 项`（拼 kw/status 到 `<a>` 下载）→ `OrderController#export`（非法状态→空列表）→ `OrderServiceImpl#export` → `OrderMapper.selectActiveAll`（`create_time DESC, id DESC`）→ `OrderCsv.csv`（BOM + 全字段引号转义 + 公式前缀加 `'`）→ 流式响应
  - 导出 Excel: `OrdersPage 导出下拉·Excel 项`（同一 `<a>` 下载辅助函数，仅路径为 `/export.xlsx`）→ `OrderController#exportXlsx`（与 CSV 同一套过滤/非法状态语义）→ `OrderServiceImpl#export` → `OrderMapper.selectActiveAll` → `OrderExcel.xlsx`（EasyExcel 注解行模型，金额/数量为数值单元格，写出直连响应流）
  - 打包: `mvn package` → prepare-package 先 npm build 再拷 `frontend/dist` → `classpath:/static` → `SpaResourceConfig` 兜底回退 index.html
  - 启动播种: `DemoSeedRunner` → `DemoDataService#seedIfEmpty`（无未删除订单时写 8 条，日期=今天-daysAgo 12:00）
- Evidence: 各链路源码通读
- Unknowns: 无
- Validation: ApiTest 中 `pagesNewestFirst…`、`exportCsvFiltersSortsAndEscapes`、`resetRestoresEightSamples…` 分别锚定三条链路
- Next Drill-Down: 撞号重试与 CSV 转义细节值得单独 feature 地图（见 Backlog）

### Node: Validation

- Type: `validation`
- Status: `confirmed`
- Purpose: 怎么证明没坏
- Validation Entry:
  - Test commands: `mvn test -DforkCount=0`（**必须 forkCount=0**，中文 Windows 下 surefire 2.22.2 fork 管道 GBK bug）+ `JAVA_HOME=~/.sdkman/candidates/java/21.0.12-tem`（`--release 8` 需 JDK9+ 编译器）
  - Test directories: `backend/src/test/java/com/demo/order/web/`（`OrderControllerApiTest`=MockMvc+Flyway clean/migrate+JdbcTemplate 直插；`OrderControllerTest`=轻量单测）
  - Smoke paths: `docker compose up -d` 后 `ORDER_DB_PASSWORD=order_demo mvn spring-boot:run` → 浏览器 `http://localhost:8080/orders`
  - Frontend: `cd frontend && npm run build`（含 tsc 类型检查）
  - Logs / metrics: 无（logback 控制台）
  - Known CI checks: 无
- Edges / Children:
  - proves: 全部 REST 端点行为（含导出 BOM/倒序/转义、Excel 导出类型化单元格/过滤/仅表头、逻辑删除、重置幂等、SPA 回退）
  - does not prove: 真实并发撞号概率、Excel 打开体验（人工验收）、前端交互回归（无前端自动化测试）
- Evidence: 测试源码 + 本机 2026-10-14 全绿（19/19，ApiTest 14 + ControllerTest 5）
- Unknowns: 无
- Next Drill-Down: 改 SQL/接口时优先跑 `OrderControllerApiTest`

### Node: Risk Areas

- Type: `risk`
- Status: `confirmed`
- Purpose: 容易踩的地形
- Risks:
  - surefire fork 崩溃（GBK Windows）: Source: 本机环境；Affected: 全部测试；Suggested: 记住 `-DforkCount=0`（已记 `PROJECT_KNOWLEDGE.md`）
  - `spring-boot:run` 用旧 static: Source: dist 拷贝只在 package 阶段；Affected: 前端联调；Suggested: 手动拷 dist 到 `backend/target/classes/static/`
  - 导出无行数上限: Source: `selectActiveAll` 全量入内存；Affected: 导出能力；Suggested: 数据量增长后改流式/游标
  - 订单号碰撞窗口: Source: 秒级时间 + 3 位随机 + 重试 3 次；Affected: 高并发创建（demo 场景足够）
  - 主库密码默认空: Source: `application.yml` `${ORDER_DB_PASSWORD:}`；Affected: 本地起服务连不上 compose MySQL；Suggested: 传 `ORDER_DB_PASSWORD=order_demo`
  - XML 与实体列清单需同步: Source: `OrderMapper.xml#columns` 手写全列；Affected: 加字段时漏改
- Unknowns: 无
- Validation: 每条风险均有源码定位
- Next Drill-Down: 无

### Node: Feature CodeMap Backlog

- Type: `capability`
- Status: `confirmed`
- Purpose: 值得深挖的 feature 地图候选
- Backlog:
  - 订单创建链路（订单号生成/撞号重试/金额舍入）:
    - Why: 事务传播与重试是最易误改的逻辑
    - Likely entry: `OrderServiceImpl#createAt`
    - Likely files: `OrderServiceImpl`、`OrderMapper`、`OrderConflictException`
    - Priority: 中（下次改动创建逻辑前）
  - CSV 导出链路:
    - Why: 转义/BOM/公式注入规则多，回归面集中
    - Likely entry: `OrderController#export` + `OrderCsv`
    - Likely files: `OrderCsv`、`OrderMapper.xml#selectActiveAll`
    - Priority: 低（刚实现且测试锚定）
- Evidence: 本次通读
- Unknowns: 无
- Next Drill-Down: 用 `/codemap` feature 模式添加

## 3. Compact Indexes

### Capability Index Table

| Capability | Main Modules | Entry | Feature CodeMap | Status |
| --- | --- | --- | --- | --- |
| 订单 CRUD | OrderController / OrderServiceImpl / OrderMapper | `POST·GET·PUT·DELETE /api/orders` | pending | confirmed |
| 导出 CSV | OrderController#export / OrderCsv / selectActiveAll | `GET /api/orders/export` | pending | confirmed |
| 导出 Excel | OrderController#exportXlsx / OrderExcel / selectActiveAll | `GET /api/orders/export.xlsx` | pending | confirmed |
| 汇总看板 | OrderStatsServiceImpl / DashboardPage | `GET /api/orders/stats`、`/dashboard` | pending | confirmed |
| 示例数据/重置 | DemoDataService / DemoSeedRunner | `POST /api/orders/reset`、Runner | pending | confirmed |
| SPA 托管 | SpaResourceConfig | 非 `/api` 未知路径 | pending | confirmed |

### Module Index Table

| Module / Package | Path | Responsibility | Key Dependencies | Risk Notes |
| --- | --- | --- | --- | --- |
| common | `backend …/common/` | 统一响应、状态枚举 | 无 | — |
| dao | `backend …/dao/` | 表访问 + 实体 | MyBatis-Plus | SQL 在 XML，列清单手写 |
| service | `backend …/service/` | 业务、金额、示例数据 | dao | 创建走 REQUIRES_NEW |
| web | `backend …/web/` | 端点、DTO、CSV/Excel 组装、异常、SPA | service、EasyExcel | 非法状态在 Controller 拦 |
| frontend | `frontend/src/` | 页面、API 封装、路由 | antd/echarts/axios | dev 代理 /api→8080 |

### Cross-Module Flow Table

| Flow | Modules | Entry | Effect | Drill-Down |
| --- | --- | --- | --- | --- |
| 创建订单 | web→service→dao | `POST /api/orders` | 落库（撞号换号重试） | pending |
| 导出 CSV | web→service→dao→OrderCsv | `GET /api/orders/export` | CSV 附件（倒序） | pending |
| 导出 Excel | web→service→dao→OrderExcel | `GET /api/orders/export.xlsx` | .xlsx 附件（倒序、数值单元格） | pending |
| 打包 | frontend→static | `mvn package` | 单 jar 含前端 | pending |
| 启动播种 | Runner→DemoDataService | 启动 | 空表时 8 条示例 | pending |

### Quick File Index

- `backend/pom.xml`: 构建链与依赖
- `backend/src/main/resources/mapper/OrderMapper.xml`: 全部查询 SQL（排序/过滤语义）
- `backend/…/web/OrderController.java`: 7 个端点
- `backend/…/web/OrderCsv.java`: CSV 组装规则
- `backend/…/web/OrderExcel.java`: Excel 组装规则（EasyExcel 行模型）
- `backend/src/test/…/OrderControllerApiTest.java`: 端点行为锚点
- `frontend/src/pages/OrdersPage.tsx`: 订单页（含「导出」下拉：CSV / Excel 两项）
- `frontend/src/api/order.ts`: 前后端契约
- `docker-compose.yml`: 本地 MySQL
- `PROJECT_KNOWLEDGE.md`: 本机构建/测试环境坑（未入库）

## 4. Maintenance Notes

- 模块边界、端点、外部依赖、验证命令变化时刷新本地图（`Last updated` 同步改）。
- 窄功能修改不要整图刷新——为该功能建/更新 feature 地图（`docs/codemap/<feature>.md`）。
- 与代码冲突时以源码为准，把地图当待复核索引。
