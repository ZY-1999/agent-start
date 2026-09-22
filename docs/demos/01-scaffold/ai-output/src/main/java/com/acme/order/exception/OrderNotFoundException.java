package com.acme.order.exception;

/**
 * 订单不存在异常
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String orderId) {
        super("订单不存在: " + orderId);
    }
}
