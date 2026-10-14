# 后端 Excel 导出端点（依赖 + 组装 + 端点 + API 测试）

Type: spec
Status: ready-for-agent
Parent: #1
Blocked by: None — can start immediately

## Goal

新增 `GET /api/orders/export.xlsx`，用 EasyExcel 生成带样式的 .xlsx 附件，并在既有 API 测试缝上用「EasyExcel 读回」完成闭环验证。

## Acceptance criteria

全部可经 `mvn test -DforkCount=0` 独立验证（关键路径在前）：

- [ ] `GET /api/orders/export.xlsx` 返回 200，Content-Type 为 .xlsx 标准 MIME（`application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`），Content-Disposition 为 `attachment; filename="orders-export-<yyyyMMddHHmmss>.xlsx"`（时间戳规则与 CSV 一致）— 证明端点契约成立。
- [ ] EasyExcel 读回工作簿：sheet 名「订单」；首个非空行为 9 列表头，列名与 CSV 完全一致（订单号、客户名称、商品名称、数量、单价、总金额、状态、备注、创建时间）— 证明列结构与 CSV 对齐。
- [ ] 单元格类型：订单号/状态（中文 label）/备注/创建时间为字符串单元格，创建时间格式 yyyy-MM-dd HH:mm:ss；数量为整数数值单元格；单价/总金额为数值单元格且两位小数显示格式（`dataFormatData` 断言 index==2 或 format=="0.00"）— 证明数值可直接求和（User Story 3）。
- [ ] 过滤语义与 CSV 一致：kw 只做订单号/客户名称前缀匹配，status 精确匹配，二者与运算；排序恒为创建时间倒序（新订单在首行）— 证明导出的就是用户正在看的这批订单（User Story 2）。
- [ ] 空结果（过滤无匹配）与非法状态（如 status=UNKNOWN）均返回 200 + 仅表头的合法 .xlsx，可被 EasyExcel 读回不报错 — 证明脚本化下载不失败（User Story 7）。
- [ ] 公式注入内容（如备注 `=SUM(A1)`）原样写入字符串单元格，不加 CSV 的防公式前缀 — 证明 .xlsx 类型化单元格免疫公式注入。
- [ ] 回归锚：既有 CSV 导出用例 `exportCsvFiltersSortsAndEscapes` 不做任何改动且保持通过，其余全部既有测试保持通过 — 证明 CSV 行为零变化（User Story 6）。

## Scope

- **In**: 后端 pom 依赖、web 层 Excel 组装、`/export.xlsx` 端点、`OrderControllerApiTest` 新增 Excel 用例。
- **Out**: 前端改造（Spec 2）；CSV 端点与既有测试的任何改动；导出行数上限/游标流式读取（PRD Out of Scope）；表头样式与列宽的机器断言（读回 API 不覆盖视觉属性，走 Spec 2 人工冒烟）。

## Context

- 现有 CSV 导出链路：`backend/src/main/java/com/demo/order/web/OrderController.java` 的 `export` 方法（kw/status 语义、非法状态→空列表、时间戳文件名）、同包 `OrderCsv`（BOM/转义/防公式前缀，Excel 版均不需要）、`service/OrderService#export` 与 `OrderServiceImpl`、`resources/mapper/OrderMapper.xml` 的 `selectActiveAll`（倒序排序语义）。
- 实体字段：`dao/entity/Order`（`Integer quantity`、`BigDecimal unitPrice/totalAmount`、`String status`、`LocalDateTime createTime`）；状态中文 label：`common/OrderStatus#labelOf`。
- 测试缝（沿用，勿新建）：`backend/src/test/java/com/demo/order/web/OrderControllerApiTest.java` — MockMvc + Flyway clean/migrate + JdbcTemplate 直插数据；CSV 用例 `exportCsvFiltersSortsAndEscapes` 是数据准备与断言风格的 prior art。
- 库选型与版本：ADR-0001（`docs/adr/0001-easyexcel-for-order-export.md`）— EasyExcel 3.3.x，兼容 Java 8；版本以 `<easyexcel.version>` 属性管理，对齐 pom 现有 `mybatis-plus.version` 风格。
- 可行性实证（PRD Comments 记录）：依赖树无冲突；读回需 `.useDefaultListener(false)` 才能拿原始 `ReadCellData`（类型/格式），默认 listener 收到的是转换后的字符串 Map；`@NumberFormat`（`0.00`）写入后 `dataFormatData` index==2/format=="0.00" 可断言；空列表写出为仅表头合法文件。
- 构建环境：测试命令 `mvn test -DforkCount=0`（surefire GBK fork 问题，必带 forkCount=0）+ `JAVA_HOME` 指向 JDK 21（`--release 8` 需要）；本地 MySQL 经 docker-compose（宿主 3307）。细节见 `docs/codemap/project.md` Validation 节与 `PROJECT_KNOWLEDGE.md`。
- 仓库规范：`docs/agents/JAVA开发规范.md`（分层：Excel 组装放 web 层与 OrderCsv 对等；Javadoc 注释）。

## Design

- **Interface delta** — 唯一新公共面是 HTTP 端点 `GET /api/orders/export.xlsx?kw=&status=`，返回 .xlsx 二进制附件（标准 MIME + `orders-export-<yyyyMMddHHmmss>.xlsx` 文件名）；服务层与 DAO 零变化。web 层新增与 `OrderCsv` 对等的包级组装器：对外表面仅一个「订单列表 → 输出流」写出操作。**Deep-module note**：该组装器把 EasyExcel 的行模型注解、表头样式、列宽策略、字段格式化（金额两位小数、状态中文、时间文本化）全部藏在单一写出操作之后——删除它会让这堆复杂度涌入 Controller，复杂度不会凭空消失，深度成立。Controller 侧新增与 CSV `export` 镜像的 `exportXlsx` 方法：同样的 kw/status 判空与非法状态→空列表逻辑，设置响应头后把响应输出流交给组装器。
- **Internal architecture** — 行模型为 EasyExcel 注解式私有类（9 列与 CSV 表头逐字一致）；映射规则：状态经 `OrderStatus#labelOf` 转中文、创建时间格式化为 `yyyy-MM-dd HH:mm:ss` 文本、数量为整数数值（空→空白）、单价/总金额为 BigDecimal 数值 + 两位小数显示格式。sheet 名「订单」；表头加粗 + 浅灰底、列宽自适应用 EasyExcel 内置样式策略。写出直连响应输出流（流式，不落中间文件）；空列表天然产出仅表头的合法工作簿。测试新增用例全部落在既有 `OrderControllerApiTest` 缝上，读断言以 EasyExcel 从响应字节读回（`useDefaultListener(false)` 取原始 `ReadCellData` 以断言类型与格式），数据准备沿用 CSV 用例的 JdbcTemplate 直插风格。实现细节（注解组合、converter 选型）留给 `/tdd` 涌现。

## Rework on failure

失败隔离：仅回退 `/export.xlsx` 端点、web 层组装器与新增测试用例；CSV 链路与既有测试不受影响，重做本 spec 即可。
