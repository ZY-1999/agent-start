-- ============================================================
-- 演示输入：订单表（3000 万行）上的慢查询，实测 8.2 秒
-- ============================================================

CREATE TABLE `t_order` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `order_no`      VARCHAR(32)  NOT NULL COMMENT '订单号',
  `customer_id`   BIGINT       NOT NULL COMMENT '客户ID',
  `amount`        DECIMAL(12,2) NOT NULL COMMENT '订单金额',
  `status`        TINYINT      NOT NULL COMMENT '0待支付 1已支付 2已发货 3已完成 4已取消',
  `channel`       VARCHAR(16)  NOT NULL COMMENT '渠道：APP/WEB/MiniProgram',
  `created_at`    DATETIME     NOT NULL COMMENT '下单时间',
  `paid_at`       DATETIME     NULL COMMENT '支付时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ------------------------------------------------------------
-- 慢查询：找出 2026 年 9 月 APP 渠道、支付金额 TOP 100 的客户及其订单数
-- ------------------------------------------------------------
SELECT *
FROM t_order
WHERE customer_id IN (
    SELECT customer_id
    FROM t_order
    WHERE DATE(created_at) >= '2026-09-01'
      AND channel = 'APP'
    GROUP BY customer_id
    HAVING SUM(amount) > 10000
)
AND DATE(created_at) >= '2026-09-01'
AND status IN (1, 2, 3)
ORDER BY amount DESC
LIMIT 100;

-- EXPLAIN 关键信息：
-- type=ALL（全表扫描），rows≈29,800,000
-- Using temporary; Using filesort
