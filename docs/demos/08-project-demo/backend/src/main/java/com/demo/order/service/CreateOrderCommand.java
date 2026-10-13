package com.demo.order.service;

import java.math.BigDecimal;

/**
 * 创建订单的入参。订单号与总金额不在其中。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public class CreateOrderCommand {

    /** 客户名称。 */
    private final String customerName;

    /** 商品名称。 */
    private final String productName;

    /** 数量。 */
    private final Integer quantity;

    /** 单价。 */
    private final BigDecimal unitPrice;

    /** 状态。 */
    private final String status;

    /** 备注，可空。 */
    private final String remark;

    /**
     * @param customerName 客户名称
     * @param productName 商品名称
     * @param quantity 数量
     * @param unitPrice 单价
     * @param status 状态
     * @param remark 备注
     */
    public CreateOrderCommand(String customerName, String productName, Integer quantity,
            BigDecimal unitPrice, String status, String remark) {
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.status = status;
        this.remark = remark;
    }

    /**
     * @return 客户名称
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @return 商品名称
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @return 数量
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @return 单价
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * @return 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }
}
