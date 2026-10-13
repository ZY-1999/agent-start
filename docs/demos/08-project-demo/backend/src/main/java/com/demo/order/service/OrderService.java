package com.demo.order.service;

import com.demo.order.dao.entity.Order;

/**
 * 订单业务。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public interface OrderService {

    /**
     * 创建订单：生成订单号、按单价计算总金额并落库。
     * 撞上唯一约束时换号，最多 3 次。
     *
     * @param command 创建入参
     * @return 已落库的订单
     * @throws OrderConflictException 重试耗尽仍然撞号
     */
    Order create(CreateOrderCommand command);
}
