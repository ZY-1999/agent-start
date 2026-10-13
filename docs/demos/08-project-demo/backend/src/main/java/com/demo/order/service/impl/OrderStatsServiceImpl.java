package com.demo.order.service.impl;

import com.demo.order.common.OrderStatus;
import com.demo.order.dao.OrderMapper;
import com.demo.order.dao.StatusAmountRow;
import com.demo.order.dao.TrendAmountRow;
import com.demo.order.service.Amounts;
import com.demo.order.service.OrderStats;
import com.demo.order.service.OrderStatsService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 汇总未删除订单。趋势按本地自然日补零。 @author 张颖 @date 2026/10/13 */
@Service
public class OrderStatsServiceImpl implements OrderStatsService {
    /** 订单表访问。 */
    private final OrderMapper orderMapper;

    /**
     * @param orderMapper 订单表访问
     */
    public OrderStatsServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }
    /**
     * 汇总未删除订单，并按本地自然日把近 7 天补零。
     *
     * @return 看板统计
     */
    @Override
    public OrderStats current() {
        Map<String, StatusAmountRow> byStatus = new HashMap<String, StatusAmountRow>();
        for (StatusAmountRow row : orderMapper.sumByStatus()) {
            byStatus.put(row.getStatus(), row);
        }
        long totalCount = 0L;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderStats.StatusCount> statusCounts = new ArrayList<OrderStats.StatusCount>();
        for (OrderStatus status : OrderStatus.values()) {
            StatusAmountRow row = byStatus.get(status.name());
            long count = row == null || row.getCount() == null ? 0L : row.getCount().longValue();
            BigDecimal amount = row == null || row.getAmount() == null ? BigDecimal.ZERO : row.getAmount();
            totalCount += count;
            totalAmount = totalAmount.add(amount);
            statusCounts.add(new OrderStats.StatusCount(status.name(), count));
        }
        OrderStats.Kpi kpi = new OrderStats.Kpi(totalCount, Amounts.text(totalAmount),
                countOf(byStatus, "PENDING"), countOf(byStatus, "COMPLETED"));
        LocalDate today = LocalDate.now();
        Map<String, TrendAmountRow> byDay = new HashMap<String, TrendAmountRow>();
        for (TrendAmountRow row : orderMapper.sumByDay(today.minusDays(6).atStartOfDay(), today.atTime(23, 59, 59))) {
            byDay.put(row.getDay(), row);
        }
        List<OrderStats.TrendPoint> trend = new ArrayList<OrderStats.TrendPoint>();
        for (int offset = 6; offset >= 0; offset--) {
            LocalDate day = today.minusDays(offset);
            TrendAmountRow row = byDay.get(day.toString());
            long count = row == null || row.getCount() == null ? 0L : row.getCount().longValue();
            String amount = row == null ? "0.00" : Amounts.text(row.getAmount());
            trend.add(new OrderStats.TrendPoint(day.toString(), count, amount));
        }
        return new OrderStats(kpi, statusCounts, trend);
    }
    /**
     * 取出某个状态的笔数，没有则是 0。
     *
     * @param byStatus 按状态索引的汇总
     * @param status 状态名
     * @return 笔数
     */
    private long countOf(Map<String, StatusAmountRow> byStatus, String status) {
        StatusAmountRow row = byStatus.get(status);
        return row == null || row.getCount() == null ? 0L : row.getCount().longValue();
    }
}
