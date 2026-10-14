package com.demo.order.common;

/**
 * 订单状态。创建时任一状态都可直接保存。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public enum OrderStatus {

    /** 待支付。 */
    PENDING("待支付"),

    /** 已支付。 */
    PAID("已支付"),

    /** 已发货。 */
    SHIPPED("已发货"),

    /** 已完成。 */
    COMPLETED("已完成"),

    /** 已取消。 */
    CANCELLED("已取消");

    /** 中文标签，供导出等展示场景使用。 */
    private final String label;

    /**
     * @param label 中文标签
     */
    OrderStatus(String label) {
        this.label = label;
    }

    /**
     * @return 中文标签
     */
    public String label() {
        return label;
    }

    /**
     * 按状态码取中文标签；未知状态码原样返回。
     *
     * @param code 状态码
     * @return 中文标签；未知时为原状态码
     */
    public static String labelOf(String code) {
        for (OrderStatus value : values()) {
            if (value.name().equals(code)) {
                return value.label;
            }
        }
        return code;
    }
}
