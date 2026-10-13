package com.demo.order.service;

/**
 * 订单号重试耗尽后仍然冲突。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public class OrderConflictException extends RuntimeException {

    /**
     * @param message 给调用方的说明，不包含订单号
     */
    public OrderConflictException(String message) {
        super(message);
    }
}
