package com.demo.order.web;

import com.demo.order.common.OrderStatus;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 创建订单请求。订单号与总金额即使传入也会被忽略。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public class CreateOrderRequest {

    /** 客户名称。 */
    @NotBlank(message = "不能为空")
    @Size(max = 64, message = "最长 64")
    private String customerName;

    /** 商品名称。 */
    @NotBlank(message = "不能为空")
    @Size(max = 128, message = "最长 128")
    private String productName;

    /** 数量。 */
    @NotNull(message = "不能为空")
    @Min(value = 1, message = "必须大于 0")
    @Max(value = 1000000, message = "过大")
    private Integer quantity;

    /** 单价。 */
    @NotNull(message = "不能为空")
    @DecimalMin(value = "0.00", message = "不能为负")
    @DecimalMax(value = "99999999.99", message = "超出金额上限")
    @Digits(integer = 8, fraction = 4, message = "金额刻度不合法")
    private BigDecimal unitPrice;

    /** 状态。 */
    @NotNull(message = "不能为空")
    private OrderStatus status;

    /** 备注。 */
    @Size(max = 255, message = "最长 255")
    private String remark;

    /**
     * @return 客户名称
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName 客户名称
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return 商品名称
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName 商品名称
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return 数量
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @param quantity 数量
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return 单价
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * @param unitPrice 单价
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * @return 状态
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * @param status 状态
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
