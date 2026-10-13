package com.demo.order.dao;

import java.math.BigDecimal;

/**
 * 按状态汇总。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public class StatusAmountRow {

    /** 状态。 */
    private String status;

    /** 笔数。 */
    private Long count;

    /** 金额。 */
    private BigDecimal amount;

    /** 返回状态。 */
    public String getStatus() {
        return status;
    }

    /** 设置状态。 */
    public void setStatus(String status) {
        this.status = status;
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
