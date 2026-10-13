package com.demo.order.web;

import com.demo.order.dao.entity.Order;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 订单响应。金额是两位小数字符串，不返回删除标记。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public class OrderResponse {

    /** 主键。 */
    private Long id;

    /** 订单号。 */
    private String orderNo;

    /** 客户名称。 */
    private String customerName;

    /** 商品名称。 */
    private String productName;

    /** 数量。 */
    private Integer quantity;

    /** 单价。 */
    private String unitPrice;

    /** 总金额。 */
    private String totalAmount;

    /** 状态。 */
    private String status;

    /** 备注。 */
    private String remark;

    /** 创建时间。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新时间。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 由已落库订单组装响应。
     *
     * @param order 已落库订单
     * @return 响应
     */
    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setCustomerName(order.getCustomerName());
        response.setProductName(order.getProductName());
        response.setQuantity(order.getQuantity());
        response.setUnitPrice(money(order.getUnitPrice()));
        response.setTotalAmount(money(order.getTotalAmount()));
        response.setStatus(order.getStatus());
        response.setRemark(order.getRemark());
        response.setCreateTime(order.getCreateTime());
        response.setUpdateTime(order.getUpdateTime());
        return response;
    }

    /**
     * 金额转成两位小数字符串。
     *
     * @param amount 金额
     * @return 两位小数
     */
    private static String money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    /**
     * @return 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return 订单号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * @param orderNo 订单号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

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
    public String getUnitPrice() {
        return unitPrice;
    }

    /**
     * @param unitPrice 单价
     */
    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * @return 总金额
     */
    public String getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount 总金额
     */
    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @return 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status 状态
     */
    public void setStatus(String status) {
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

    /**
     * @return 创建时间
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * @return 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime 更新时间
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
