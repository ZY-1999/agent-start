package com.demo.order.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额按两位小数、四舍五入计算。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public final class Amounts {

    /**
     * 工具类，不允许实例化。
     */
    private Amounts() {}

    /**
     * @param value 原始金额
     * @return 两位小数
     */
    public static BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @param quantity 数量
     * @param unitPrice 单价
     * @return 两位小数总金额
     */
    public static BigDecimal total(Integer quantity, BigDecimal unitPrice) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity.longValue())).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @param value 金额，可空
     * @return 两位小数文本，空值为 0.00
     */
    public static String text(BigDecimal value) {
        BigDecimal scaled = value == null ? BigDecimal.ZERO : value;
        return scaled.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
