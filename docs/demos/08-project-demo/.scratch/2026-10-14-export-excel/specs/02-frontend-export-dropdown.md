# 前端导出下拉菜单（CSV / Excel）

Type: spec
Status: ready-for-human
Parent: #1
Blocked by: #1（「导出 Excel」项的人工冒烟依赖 `/export.xlsx` 端点已存在；CSV 项行为不变，只是入口被改造）

## Goal

订单页「导出 CSV」按钮改为 antd 5 Dropdown「导出」菜单（导出 CSV / 导出 Excel 两项），Excel 项沿用同一 `<a>` 下载与查询串拼法请求 `/export.xlsx`。

## Acceptance criteria

验证机制 = `npm run build`（含 tsc 类型检查）+ 人工点击冒烟（关键路径在前）：

- [ ] `cd frontend && npm run build` 通过（tsc 无类型错误）— 证明类型与构建成立。
- [ ] 订单页原「导出 CSV」按钮位置变为「导出」下拉，展开后恰有「导出 CSV」「导出 Excel」两项 — 证明入口形态正确（User Story 1 的 UI 面）。
- [ ] 点击「导出 CSV」：行为与改造前完全一致（同 URL、同查询串、同 `<a>` 下载），下载到 orders-export-<时间戳>.csv — 证明 CSV 入口零回归（User Story 6）。
- [ ] 点击「导出 Excel」：请求 `/api/orders/export.xlsx` 且携带与 CSV 项相同的 kw/status 查询串，下载到 orders-export-<时间戳>.xlsx，可用 Excel 打开 — 证明 Excel 入口贯通（依赖 Spec 1 端点）。
- [ ] 人工冒烟：设置关键字/状态过滤后点「导出 Excel」，文件内容即当前过滤结果，数量/单价/总金额在 Excel 中为可直接求和的数值 — 证明端到端过滤贯通（User Story 2/3）。
- [ ] 人工冒烟：Excel 打开导出文件，确认表头加粗 + 浅灰底、列宽可读 — User Story 5 的验收落点（样式不进机器断言）。

## Scope

- **In**: `frontend/src/pages/OrdersPage.tsx` 导出按钮区域改造为 Dropdown 菜单；kw/status 查询串拼装逻辑提取复用，仅目标 URL 不同。
- **Out**: 不改 `frontend/src/api/order.ts`（导出走 `<a>` 直链下载，不经 axios 封装）；不改其他页面与路由；不改后端任何代码（Spec 1 已交付）；不做导出进度条/批量导出（PRD Out of Scope）。

## Context

- 改造点：`frontend/src/pages/OrdersPage.tsx` — 现有 `exportCsv`（`document.createElement('a')` + `URLSearchParams` 拼 kw/status + click 下载）与工具栏 `<Button onClick={exportCsv}>导出 CSV</Button>`（与「新建订单」按钮及筛选控件同处 `Space`；全局「重置数据」按钮在 `layout/AppShell.tsx`，不在本页面）。
- antd 5（zhCN locale）`Dropdown` + `MenuProps`（items 写法）是本仓库既有依赖内的标准用法，无需新增包。
- Excel 项目标 URL：`/api/orders/export.xlsx`（Spec 1 交付的端点；查询串与 CSV 项同构）。
- 前端验证机制：`npm run build` = `tsc --noEmit && vite build`；本仓库前端无自动化测试（CodeMap Validation 节），人工冒烟需本地起后端（`ORDER_DB_PASSWORD=order_demo mvn spring-boot:run`，注意 `spring-boot:run` 用旧 static 需手动拷 dist 或前端 dev 代理）。
- UI 语言：全中文文案（与现页面一致）。

## Design

- **Interface delta** — 纯 UI 面：订单页工具栏原「导出 CSV」单按钮位置改为一个「导出」按钮触发的 antd Dropdown 菜单，恰含「导出 CSV」「导出 Excel」两项。无 API 面、无类型面变化（`api/order.ts` 不动，下载仍是 `<a>` 直链，不经 axios）。
- **Internal architecture** — 把现有 kw/status 查询串拼装 + `<a>` 点击下载提取为一个本地辅助函数，两个菜单项以不同目标路径（`/api/orders/export` 与 `/api/orders/export.xlsx`）调用同一辅助函数；菜单用 antd `Dropdown` 的 items 写法，click 触发。结构极简，无状态、无新组件文件，其余留给 `/tdd` 涌现。

## Rework on failure

失败隔离：改动集中在订单页一个文件；回退即恢复单「导出 CSV」按钮，重做本 spec 即可。
