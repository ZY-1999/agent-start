package com.demo.order.service;

/**
 * 订单汇总。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public interface OrderStatsService {

    /**
     * 计算当前看板：全部未删除订单的指标，以及近 7 个自然日的趋势。
     *
     * @return 看板统计
     */
    OrderStats current();
}
