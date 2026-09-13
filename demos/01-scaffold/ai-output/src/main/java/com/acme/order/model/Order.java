package com.acme.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 订单实体（内存存储，未接数据库）
 */
public class Order {

    /** 订单编号（UUID） */
    private String id;

    /** 客户名称 */
    private String customerName;

    /** 订单金额（两位小数） */
    private BigDecimal amount;

    /** 订单状态 */
    private OrderStatus status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    public Order() {
    }

    public Order(String id, String customerName, BigDecimal amount,
                 OrderStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{id='" + id + "', customerName='" + customerName
                + "', amount=" + amount + ", status=" + status
                + ", createdAt=" + createdAt + '}';
    }
}
