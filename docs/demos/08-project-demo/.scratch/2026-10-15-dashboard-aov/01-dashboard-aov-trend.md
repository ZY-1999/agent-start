# 汇总页新增近 7 日客单价折线图

Type: prd
Status: ready-for-agent

## Problem Statement

汇总看板（/dashboard）现有 4 个 KPI 卡、状态分布饼图、近 7 日订单数/金额双轴柱状图，但没有客单价视角，看不出每日单均消费水平的变化。

## Solution

在现有两行图表下方新增第三行整行折线卡「近 7 日客单价」：每日客单价 = 当日总金额 ÷ 当日订单数。数据完全由前端从既有 `GET /api/orders/stats` 响应的 `trend`（每日 count/amount）计算，后端零改动。

## User Stories

1. 作为订单管理员，我希望在汇总页看到近 7 日每日客单价折线，以便快速发现单均消费水平的变化趋势。

## Implementation Decisions

- 口径：全部未删除订单（含已取消），与现有 KPI 卡「总金额 ÷ 总订单数」口径一致（用户敲定）。
- 形态：独立折线卡，第三行整行 span 24，高 320px，与其他图一致（用户敲定）。
- 无订单日（count=0）：该点为 null，折线断开不画。
- 精度：两位小数展示（与后端金额规范一致）。
- 数据来源：`stats.trend` 已含每日金额与订单数，前端计算，不改后端契约、不加接口。

## Testing Decisions

- 前端无自动化测试（与既有约定一致）：验证方式为 `npm run build`（含 tsc 类型检查）+ 浏览器人工冒烟（对数：任选一天，金额 ÷ 订单数 = 折线值）。

## Out of Scope

- 不加整体客单价 KPI 数字卡（KPI 行维持 4 卡布局）。
- 不改后端 stats 接口与测试。
- 不做客单价按状态/客户的下钻。

## Comments

- 2026-10-15 — 需求由 /grill-me 面试固化（口径、形态、布局 3 项决策均由用户当面敲定）。
- 2026-10-15 — 实现完成并验证：`frontend/src/pages/DashboardPage.tsx` 新增 `aovRef` 与第三行整行折线卡；`npm run build`（tsc 通过）；dist 已拷贝 `backend/target/classes/static/`；浏览器像素级对数通过——7 个数据点换算值 189.00 / 300.50 / 259.00 / 399.00 / 269.70 / 849.00 / 10.00 与 `GET /api/orders/stats` 的 trend 手算值（amount÷count）逐一吻合（10-14=1698÷2、10-15=当日新单 10 元）。改动待提交。
