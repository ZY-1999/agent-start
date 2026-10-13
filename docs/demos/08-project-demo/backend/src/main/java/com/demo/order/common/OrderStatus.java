package com.demo.order.common;

/**
 * 订单状态。创建时任一状态都可直接保存。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public enum OrderStatus {

    /** 待支付。 */
    PENDING,

    /** 已支付。 */
    PAID,

    /** 已发货。 */
    SHIPPED,

    /** 已完成。 */
    COMPLETED,

    /** 已取消。 */
    CANCELLED
}
