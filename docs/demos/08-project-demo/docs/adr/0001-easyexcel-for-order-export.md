# 订单 Excel 导出采用 EasyExcel 而非原生 Apache POI

订单导出新增 `.xlsx` 格式（2026-10，与既有 CSV 并存，端点 `GET /api/orders/export.xlsx`），生成库选用阿里 EasyExcel（3.3.x，兼容 Java 8）：与仓库遵循的《Java开发规范》同生态，注解式模型导出代码量最小，且写入天然流式——CodeMap 风险区已记录「导出无行数上限、`selectActiveAll` 全量入内存」，流式写把该风险的写出侧影响降到最低。

## Considered Options

- **Apache POI 原生**：工业标准、依赖显式，但需手写 ~60 行 SXSSF 样板并在 pom 自钉版本；demo 场景下维护面更大，弃用。
