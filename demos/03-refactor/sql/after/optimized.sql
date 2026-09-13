-- ============================================================
-- AI 优化方案（参考产出）：改写 SQL + 索引建议 + EXPLAIN 解读
-- ============================================================

-- ------------------------------------------------------------
-- 一、原 SQL 的问题点（AI 指出）
--
-- 1. DATE(created_at) >= '...'：函数包裹列，idx_created_at 索引直接失效 → 全表扫描
--    这是"对列使用函数"的经典错误，应改写为范围条件让索引可用
-- 2. IN (子查询 GROUP BY + HAVING)：MySQL 8 虽支持半连接优化，
--    但与外层再扫一次大表叠加，代价依然高；可改为 JOIN
-- 3. SELECT *：取出全部列，无法利用覆盖索引，回表代价大
-- 4. ORDER BY amount DESC LIMIT 100：无 amount 索引，全量排序（filesort）
-- ------------------------------------------------------------

-- ------------------------------------------------------------
-- 二、改写后的 SQL
-- ------------------------------------------------------------
SELECT o.id, o.order_no, o.customer_id, o.amount, o.status, o.created_at
FROM (
    -- 先用小结果集定位"9月 APP 渠道累计消费 > 1万"的客户
    SELECT customer_id
    FROM t_order
    WHERE channel = 'APP'
      AND created_at >= '2026-09-01'
      AND created_at <  '2026-10-01'
    GROUP BY customer_id
    HAVING SUM(amount) > 10000
) c
JOIN t_order o
  ON o.customer_id = c.customer_id
 AND o.created_at >= '2026-09-01'
 AND o.created_at <  '2026-10-01'
 AND o.status IN (1, 2, 3)
ORDER BY o.amount DESC
LIMIT 100;

-- ------------------------------------------------------------
-- 三、建议索引（列顺序有讲究）
-- ------------------------------------------------------------
-- 等值列在前、范围列在后，让 channel 的过滤先收敛：
ALTER TABLE t_order ADD INDEX idx_channel_created (channel, created_at, customer_id, amount);
-- 该索引同时服务：
--   * 内层子查询：channel= 等值 + created_at 范围 → 走 index range scan
--   * customer_id / amount 都在索引里 → 覆盖索引，免回表

-- ------------------------------------------------------------
-- 四、EXPLAIN 改善预期
--
-- 改写前：type=ALL, rows≈29,800,000, Using temporary; Using filesort
-- 改写后：内层 type=range，命中 idx_channel_created；外层 type=ref
--         rows 下降 2~3 个数量级（具体以真实数据分布为准）
--
-- ⚠️ 语义差异提醒（AI 主动交代）：
--   1. IN 子查询与 JOIN 在"外层一对多"场景下结果可能重复：
--      本例原 SQL 的 IN 本就可能返回同客户多单，JOIN 行为一致，无差异
--   2. SELECT * 改为显式列清单：调用方若依赖了未列出的列（如 paid_at）需补上
--   3. 范围条件 created_at < '2026-10-01' 与原 DATE() 写法在边界上等价
-- ------------------------------------------------------------

-- ⚠️ 以上为 AI 给出的"最合理假设"方案，索引效果必须在生产级数据量上
--    用 EXPLAIN ANALYZE 实测验证（DBA 流程照走，一步不能省）。
