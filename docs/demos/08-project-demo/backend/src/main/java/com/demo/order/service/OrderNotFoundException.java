package com.demo.order.service;

/**
 * 订单不存在或已删除。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public class OrderNotFoundException extends RuntimeException {

    /**
     * @param message 说明
     */
    public OrderNotFoundException(String message) {
        super(message);
    }
}
