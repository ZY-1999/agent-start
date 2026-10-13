package com.demo.order.web;

import com.demo.order.dao.entity.Order;
import com.demo.order.service.OrderPage;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单分页响应。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public class OrderPageResponse {

    /** 总条数。 */
    private Long total;

    /** 页码。 */
    private Integer page;

    /** 页大小。 */
    private Integer size;

    /** 当前页记录。 */
    private List<OrderResponse> records;

    /**
     * 把分页结果转成接口响应。
     *
     * @param page 分页结果
     * @return 响应
     */
    public static OrderPageResponse from(OrderPage page) {
        OrderPageResponse response = new OrderPageResponse();
        response.total = page.getTotal();
        response.page = page.getPage();
        response.size = page.getSize();
        response.records = new ArrayList<OrderResponse>();
        for (Order order : page.getRecords()) {
            response.records.add(OrderResponse.from(order));
        }
        return response;
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
    public List<OrderResponse> getRecords() {
        return records;
    }
}
