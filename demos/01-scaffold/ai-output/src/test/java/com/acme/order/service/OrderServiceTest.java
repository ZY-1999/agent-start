package com.acme.order.service;

import com.acme.order.dto.OrderCreateDto;
import com.acme.order.dto.OrderUpdateDto;
import com.acme.order.exception.OrderNotFoundException;
import com.acme.order.model.Order;
import com.acme.order.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderService 单元测试
 */
class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
    }

    private OrderCreateDto buildCreateDto(String name, String amount) {
        return new OrderCreateDto(name, new BigDecimal(amount));
    }

    // ---------- 正常流 ----------

    @Test
    @DisplayName("创建订单：编号自动生成、初始状态 CREATED、金额两位小数")
    void create_shouldGenerateIdAndSetCreatedStatus() {
        Order order = orderService.create(buildCreateDto("张三", "99.999"));

        assertNotNull(order.getId());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertNotNull(order.getCreatedAt());
        // 金额四舍五入到两位小数：99.999 -> 100.00
        assertEquals(new BigDecimal("100.00"), order.getAmount());
    }

    @Test
    @DisplayName("修改订单：仅客户名称与金额变化")
    void update_shouldChangeNameAndAmount() {
        Order created = orderService.create(buildCreateDto("张三", "10.00"));

        Order updated = orderService.update(created.getId(),
                new OrderUpdateDto("李四", new BigDecimal("20.00")));

        assertEquals("李四", updated.getCustomerName());
        assertEquals(new BigDecimal("20.00"), updated.getAmount());
        assertEquals(OrderStatus.CREATED, updated.getStatus());
    }

    @Test
    @DisplayName("删除订单后再查询应抛出 OrderNotFoundException")
    void delete_thenGetShouldThrow() {
        Order created = orderService.create(buildCreateDto("张三", "10.00"));

        assertTrue(orderService.delete(created.getId()));
        assertThrows(OrderNotFoundException.class, () -> orderService.getById(created.getId()));
    }

    // ---------- 异常流 ----------

    @Test
    @DisplayName("查询不存在的订单应抛出 OrderNotFoundException")
    void getById_notExists_shouldThrow() {
        assertThrows(OrderNotFoundException.class,
                () -> orderService.getById("not-exist-id"));
    }

    @Test
    @DisplayName("删除不存在的订单应抛出 OrderNotFoundException")
    void delete_notExists_shouldThrow() {
        assertThrows(OrderNotFoundException.class,
                () -> orderService.delete("not-exist-id"));
    }

    // ---------- 状态过滤 ----------

    @Test
    @DisplayName("列表查询：不传状态返回全部，传状态只返回匹配的")
    void list_filterByStatus() {
        orderService.create(buildCreateDto("张三", "10.00"));
        Order second = orderService.create(buildCreateDto("李四", "20.00"));

        List<Order> all = orderService.list(null);
        List<Order> createdOnly = orderService.list(OrderStatus.CREATED);

        assertEquals(2, all.size());
        assertEquals(2, createdOnly.size());

        // 将第二个订单状态改为 PAID 后，过滤结果应只剩 1 个
        second.setStatus(OrderStatus.PAID);
        assertEquals(1, orderService.list(OrderStatus.CREATED).size());
        assertEquals(1, orderService.list(OrderStatus.PAID).size());
    }
}
