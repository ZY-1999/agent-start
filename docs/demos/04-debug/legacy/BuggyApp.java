package com.acme.order.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单服务（演示排障用的简化版）
 *
 * 注意：演示意图就是让这段代码抛 ConcurrentModificationException，
 * 对应 error.log 中 OrderService.java:42 的堆栈。
 */
public class OrderService {

    /** 订单缓存：orderId -> 金额 */
    private static final Map<String, Double> ORDER_CACHE = new HashMap<>();

    /**
     * 批量取消大额订单（金额 > 阈值）
     */
    public void cancelLargeOrders(double threshold) {
        for (String orderId : ORDER_CACHE.keySet()) {            // 遍历 keySet 视图
            if (ORDER_CACHE.get(orderId) > threshold) {
                ORDER_CACHE.remove(orderId);                     // <-- 遍历中结构性修改：CME 触发点
            }
        }
    }

    public void putOrder(String orderId, double amount) {
        ORDER_CACHE.put(orderId, amount);
    }

    public int orderCount() {
        return ORDER_CACHE.size();
    }

    public static void main(String[] args) {
        OrderService service = new OrderService();
        service.putOrder("O-1001", 500);
        service.putOrder("O-1002", 20000);
        service.putOrder("O-1003", 99999);
        service.cancelLargeOrders(10000);
        System.out.println("remaining = " + service.orderCount());
    }
}
