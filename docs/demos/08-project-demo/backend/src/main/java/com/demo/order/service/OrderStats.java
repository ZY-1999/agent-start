package com.demo.order.service;

import java.util.List;

/**
 * 看板统计。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public class OrderStats {

    /** 四项指标。 */
    private final Kpi kpi;

    /** 五个状态的笔数。 */
    private final List<StatusCount> statusCounts;

    /** 近 7 日趋势。 */
    private final List<TrendPoint> trend;

    /**
     * @param kpi 四项指标
     * @param statusCounts 五个状态的笔数
     * @param trend 近 7 日趋势
     */
    public OrderStats(Kpi kpi, List<StatusCount> statusCounts, List<TrendPoint> trend) {
        this.kpi = kpi;
        this.statusCounts = statusCounts;
        this.trend = trend;
    }

    /** 返回四项指标。 */
    public Kpi getKpi() {
        return kpi;
    }

    /** 返回五个状态的笔数。 */
    public List<StatusCount> getStatusCounts() {
        return statusCounts;
    }

    /** 返回近 7 日趋势。 */
    public List<TrendPoint> getTrend() {
        return trend;
    }

    /**
     * 看板四项指标。
     *
     * @author 张颖
     * @date 2026/10/13
     */
    public static class Kpi {

        /** 未删除订单总数。 */
        private final Long totalCount;

        /** 待支付笔数。 */
        private final Long pendingCount;

        /** 已完成笔数。 */
        private final Long completedCount;

        /** 未删除订单总金额。 */
        private final String totalAmount;

        /**
         * @param totalCount 未删除订单总数
         * @param totalAmount 未删除订单总金额
         * @param pendingCount 待支付笔数
         * @param completedCount 已完成笔数
         */
        public Kpi(long totalCount, String totalAmount, long pendingCount, long completedCount) {
            this.totalCount = Long.valueOf(totalCount);
            this.totalAmount = totalAmount;
            this.pendingCount = Long.valueOf(pendingCount);
            this.completedCount = Long.valueOf(completedCount);
        }

        /** 返回未删除订单总数。 */
        public Long getTotalCount() {
            return totalCount;
        }

        /** 返回未删除订单总金额。 */
        public String getTotalAmount() {
            return totalAmount;
        }

        /** 返回待支付笔数。 */
        public Long getPendingCount() {
            return pendingCount;
        }

        /** 返回已完成笔数。 */
        public Long getCompletedCount() {
            return completedCount;
        }
    }

    /**
     * 某一状态的笔数。
     *
     * @author 张颖
     * @date 2026/10/13
     */
    public static class StatusCount {

        /** 状态。 */
        private final String status;

        /** 笔数。 */
        private final Long count;

        /**
         * @param status 状态
         * @param count 笔数
         */
        public StatusCount(String status, long count) {
            this.status = status;
            this.count = Long.valueOf(count);
        }

        /** 返回状态。 */
        public String getStatus() {
            return status;
        }

        /** 返回笔数。 */
        public Long getCount() {
            return count;
        }
    }

    /**
    /**
     * 某一个自然日的笔数和金额。
     *
     * @author 张颖
     * @date 2026/10/13
     */
    public static class TrendPoint {

        /** 日期。 */
        private final String date;

        /** 金额。 */
        private final String amount;

        /** 笔数。 */
        private final Long count;

        /**
         * @param date 日期
         * @param count 笔数
         * @param amount 金额
         */
        public TrendPoint(String date, long count, String amount) {
            this.date = date;
            this.count = Long.valueOf(count);
            this.amount = amount;
        }

        /** 返回日期。 */
        public String getDate() {
            return date;
        }

        /** 返回笔数。 */
        public Long getCount() {
            return count;
        }

        /** 返回金额。 */
        public String getAmount() {
            return amount;
        }
    }
}
