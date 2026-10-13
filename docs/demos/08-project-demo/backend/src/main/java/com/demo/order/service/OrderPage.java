package com.demo.order.service;

import com.demo.order.dao.entity.Order;

import java.util.List;

/**
 * 订单分页结果。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public class OrderPage {

    /** 总条数。 */
    private final Long total;

    /** 页码。 */
    private final Integer page;

    /** 页大小。 */
    private final Integer size;

    /** 当前页记录。 */
    private final List<Order> records;

    /**
     * @param total 符合条件的总条数
     * @param page 页码，从 1 开始
     * @param size 页大小
     * @param records 当前页
     */
    public OrderPage(long total, int page, int size, List<Order> records) {
        this.total = Long.valueOf(total);
        this.page = Integer.valueOf(page);
        this.size = Integer.valueOf(size);
        this.records = records;
    }

    /** 返回总条数。 */
    public Long getTotal() {
        return total;
    }

    /** 返回页码。 */
    public Integer getPage() {
        return page;
    }

    /** 返回页大小。 */
    public Integer getSize() {
        return size;
    }

    /** 返回当前页记录。 */
    public List<Order> getRecords() {
        return records;
    }
}
