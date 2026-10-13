package com.demo.order.service;

/**
 * 分页等查询参数不合法。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public class BadOrderRequestException extends RuntimeException {

    /**
     * @param message 说明
     */
    public BadOrderRequestException(String message) {
        super(message);
    }
}
