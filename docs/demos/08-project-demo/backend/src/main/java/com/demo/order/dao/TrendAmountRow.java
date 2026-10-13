package com.demo.order.dao;

import java.math.BigDecimal;

/**
 * 按自然日汇总。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public class TrendAmountRow {

    /** 日期。 */
    private String day;

    /** 笔数。 */
    private Long count;

    /** 金额。 */
    private BigDecimal amount;

    /** 返回日期。 */
    public String getDay() {
        return day;
    }

    /** 设置日期。 */
    public void setDay(String day) {
        this.day = day;
    }

    /** 返回笔数。 */
    public Long getCount() {
        return count;
    }

    /** 设置笔数。 */
    public void setCount(Long count) {
        this.count = count;
    }

    /** 返回金额。 */
    public BigDecimal getAmount() {
        return amount;
    }

    /** 设置金额。 */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
