package com.acme.order.model;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    /** 待支付 */
    CREATED,
    /** 已支付 */
    PAID,
    /** 已取消 */
    CANCELLED
}
