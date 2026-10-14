# 订单导出新增 Excel(.xlsx) 格式

Type: prd
Status: ready-for-agent

## Problem Statement

订单导出目前只有 CSV 一种格式。CSV 虽可被 Excel 双击打开（带 BOM、防公式前缀），但金额/数量都是文本单元格，用户在 Excel 里做二次加工（求和、透视、分列）前需手工转换；且无表头样式与列宽，打印阅读体验差。团队需要一个原生 .xlsx 导出。

## Solution

与 CSV 并存，新增 `GET /api/orders/export.xlsx` 导出端点，用阿里 EasyExcel 生成带样式的 .xlsx 附件；前端「导出 CSV」按钮改为「导出」下拉菜单（CSV / Excel 两项）。CSV 端点与行为保持原样。

## User Stories

1. 作为订单管理员，我希望一键导出 .xlsx 文件，以便在 Excel 中直接得到带样式的数据表而无需文本导入。
2. 作为订单管理员，我希望导出沿用当前的关键字/状态过滤条件，以便导出的就是我正在看的这批订单。
3. 作为订单管理员，我希望数量、单价、总金额在 Excel 里是真正的数值单元格，以便直接求和与透视。
4. 作为订单管理员，我希望导出文件名带时间戳，以便归档多个批次的导出。
5. 作为订单管理员，我希望表头加粗、列宽合适、sheet 名为「订单」，以便阅读与打印。
6. 作为仍在消费 CSV 的下游流程使用者，我希望 CSV 导出端点与文件内容完全不变，以便现有流程不受影响。
7. 作为订单管理员，我希望过滤无匹配或状态非法时仍得到只有表头的合法 .xlsx 文件，以便脚本化下载不失败。

## Implementation Decisions

- 与 CSV 并存：新增端点 `GET /api/orders/export.xlsx`；kw/status 参数语义与现有 CSV 导出完全一致（关键字为订单号/客户名称前缀、状态精确匹配、非法状态返回仅表头的空文件）；排序恒为创建时间倒序。
- 生成库：阿里 EasyExcel 3.3.x（兼容 Java 8），选型理由与弃用方案见 ADR-0001；注解式模型 + 流式写出。
- 列结构：与 CSV 完全一致的 9 列（订单号、客户名称、商品名称、数量、单价、总金额、状态、备注、创建时间）；订单号/状态（中文 label）/备注/创建时间为文本单元格，时间格式 yyyy-MM-dd HH:mm:ss；数量为整数数值单元格；单价/总金额为数值单元格并带两位小数显示格式。
- 表头样式：加粗 + 浅灰底；列宽自适应；sheet 名「订单」。
- 响应：附件下载，文件名 orders-export-<yyyyMMddHHmmss>.xlsx（时间戳规则与现有 CSV 导出一致），Content-Type 为 .xlsx 标准 MIME。
- 公式注入：.xlsx 单元格有类型，字符串单元格不会被 Excel 当公式执行，因此不加 CSV 版本的防公式前缀。
- 数据来源：复用现有订单导出查询（服务层导出方法与 DAO 查询不新增、不修改）。
- 前端：订单页「导出 CSV」按钮改为 antd 5 Dropdown 下拉菜单，含「导出 CSV」「导出 Excel」两项，沿用 <a> 触发下载的实现方式，kw/status 拼查询串的方式不变。
- 后端 Excel 组装逻辑放在 web 层、与现有 CSV 组装对等的位置。
- 依赖版本管理：pom 中以 `<easyexcel.version>` 属性定义版本，对齐现有 `mybatis-plus.version` 风格（JAVA 开发规范二方库条款）。

## Testing Decisions

- 只测外部行为（HTTP 响应头 + 用 EasyExcel 读回的工作簿内容），不测组装实现的内部细节。
- 在既有 MockMvc + Flyway clean/migrate + JdbcTemplate 直插数据的 API 测试缝上新增 Excel 导出用例（与现有 CSV 导出测试同缝）：验证过滤生效、创建时间倒序、9 列表头、数量/金额为数值单元格且金额两位小数格式、MIME 与 Content-Disposition 文件名后缀、空结果与非法状态仅表头。
- 读断言用 EasyExcel 读回响应字节实现（读写同库）；读回时需 `.useDefaultListener(false)` 才能取得原始 `ReadCellData`（类型/格式信息），默认 listener 收到的是转换后的字符串 Map；金额两位小数用 `dataFormatData`（index==2 或 format=="0.00"）断言。
- Prior art：现有 CSV 导出测试已在该缝锚定 BOM/转义/倒序/过滤行为，新用例沿用其数据准备与断言风格。
- 前端无自动化测试：验证方式为 npm run build（含 tsc 类型检查）+ 人工点击冒烟。

## Out of Scope

- 不改动 CSV 端点行为与其既有测试。
- 不做导出行数上限 / 游标流式读取（CodeMap 风险区已记录该风险，另案处理）。
- 不做多 sheet、汇总行、复杂模板导出。
- 不做前端导出进度条 / 批量导出。
- 不修改仓库 git-contract 文档（solo 约定，本次新建 feat/export-excel 分支是一次性覆盖）。

## Further Notes

- 分支 feat/export-excel 已创建。
- ADR-0001 已记录 EasyExcel 选型。

## Comments

- 2026-10-14 — PRD 由 /grill-with-docs 面试固化（7 项决策均由用户当面敲定：并存、/export.xlsx 新路径、EasyExcel、默认呈现方案、前端下拉菜单、feat/export-excel 分支、完整 SDD 流水线）。
- 2026-10-14 — 对抗评审 PASS（新鲜上下文子代理两阶段核实：真实性逐条对照源码通过；可行性经镜像工程实测——EasyExcel 3.3.4 + Java 8 + Boot 2.7.18 依赖树无冲突、读写实验成功、`/export.xlsx` 路由无重叠、`useDefaultListener(false)` 读回原始单元格可行）。修正建议 3 条已并入正文。
- 2026-10-14 — Gate 0：用户在面试中逐题确认全部决策并明确选择完整 SDD 流水线，视为通过。
