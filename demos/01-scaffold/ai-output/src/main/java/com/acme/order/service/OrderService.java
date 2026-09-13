package com.acme.order.service;

import com.acme.order.dto.OrderCreateDto;
import com.acme.order.dto.OrderUpdateDto;
import com.acme.order.exception.OrderNotFoundException;
import com.acme.order.model.Order;
import com.acme.order.model.OrderStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 订单服务（内存实现，未接数据库）
 */
public class OrderService {

    /** 内存存储：key = 订单编号 */
    private final Map<String, Order> store = new ConcurrentHashMap<>();

    /**
     * 创建订单，状态固定为 CREATED，编号自动生成
     */
    public Order create(OrderCreateDto dto) {
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order(
                UUID.randomUUID().toString(),
                dto.getCustomerName(),
                dto.getAmount().setScale(2, RoundingMode.HALF_UP),
                OrderStatus.CREATED,
                now);
        store.put(order.getId(), order);
        return order;
    }

    /**
     * 修改订单（仅客户名称与金额可修改）
     */
    public Order update(String orderId, OrderUpdateDto dto) {
        Order order = getById(orderId);
        synchronized (order) {
            order.setCustomerName(dto.getCustomerName());
            order.setAmount(dto.getAmount().setScale(2, RoundingMode.HALF_UP));
        }
        return order;
    }

    /**
     * 删除订单
     *
     * @return true 表示删除成功；订单不存在时抛出异常
     */
    public boolean delete(String orderId) {
        Order removed = store.remove(orderId);
        if (removed == null) {
            throw new OrderNotFoundException(orderId);
        }
        return true;
    }

    /**
     * 按编号查询订单，不存在时抛出 OrderNotFoundException
     */
    public Order getById(String orderId) {
        Order order = store.get(orderId);
        if (order == null) {
            throw new OrderNotFoundException(orderId);
        }
        return order;
    }

    /**
     * 列表查询，可按状态过滤；status 为 null 时返回全部
     */
    public List<Order> list(OrderStatus status) {
        if (status == null) {
            return new ArrayList<>(store.values());
        }
        return store.values().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }
}
