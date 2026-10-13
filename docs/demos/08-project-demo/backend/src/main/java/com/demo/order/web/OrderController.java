package com.demo.order.web;

import com.demo.order.common.Result;
import com.demo.order.dao.entity.Order;
import com.demo.order.service.CreateOrderCommand;
import com.demo.order.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 订单 HTTP 接口。
 *
 * @author 张颖
 * @date 2026/10/13
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * @param orderService 订单业务
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单。
     *
     * @param request 请求体
     * @return 已创建的订单
     */
    @PostMapping
    public Result<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.create(new CreateOrderCommand(
                request.getCustomerName(),
                request.getProductName(),
                request.getQuantity(),
                request.getUnitPrice(),
                request.getStatus().name(),
                request.getRemark()));
        return Result.ok(OrderResponse.from(order));
    }
}
