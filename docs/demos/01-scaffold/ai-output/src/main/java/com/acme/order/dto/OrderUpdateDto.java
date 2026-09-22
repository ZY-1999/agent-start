package com.acme.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 修改订单请求 DTO（仅允许修改客户名称与金额）
 */
public class OrderUpdateDto {

    /** 客户名称（必填，1~64 字符） */
    @NotBlank(message = "客户名称不能为空")
    @Size(max = 64, message = "客户名称不能超过 64 个字符")
    private String customerName;

    /** 订单金额（必填，非负数，两位小数） */
    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.00", message = "订单金额不能为负数")
    private BigDecimal amount;

    public OrderUpdateDto() {
    }

    public OrderUpdateDto(String customerName, BigDecimal amount) {
        this.customerName = customerName;
        this.amount = amount;
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
}
