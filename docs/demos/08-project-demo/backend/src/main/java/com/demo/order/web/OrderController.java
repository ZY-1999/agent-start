package com.demo.order.web;

import com.demo.order.common.Result;
import com.demo.order.dao.entity.Order;
import com.demo.order.service.BadOrderRequestException;
import com.demo.order.service.CreateOrderCommand;
import com.demo.order.service.DemoDataService;
import com.demo.order.service.OrderPage;
import com.demo.order.service.OrderService;
import com.demo.order.service.OrderStats;
import com.demo.order.service.OrderStatsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

/**
 * 订单 HTTP 接口。
 *
 * @author 张颖
 * @date 2026/10/13
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    /** 订单业务。 */
    private final OrderService orderService;

    /** 汇总业务。 */
    private final OrderStatsService orderStatsService;

    /** 示例数据。 */
    private final DemoDataService demoDataService;

    /**
     * @param orderService 订单业务
     * @param orderStatsService 汇总业务
     * @param demoDataService 示例数据
     */
    public OrderController(OrderService orderService, OrderStatsService orderStatsService,
            DemoDataService demoDataService) {
        this.orderService = orderService;
        this.orderStatsService = orderStatsService;
        this.demoDataService = demoDataService;
    }

    /**
     * 创建订单。
     *
     * @param request 创建入参
     * @return 已落库的订单
     */
    @PostMapping
    public Result<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        return Result.ok(OrderResponse.from(orderService.create(toCommand(request))));
    }

    /**
     * 分页查询未删除订单。非法状态返回空页。
     *
     * @param keyword 订单号或客户名称前缀
     * @param status 精确状态
     * @param page 页码
     * @param size 页大小
     * @return 当前页
     */
    @GetMapping
    public Result<OrderPageResponse> page(@RequestParam(value = "kw", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        if (status != null && !status.trim().isEmpty() && !known(status)) {
            return Result.ok(empty(page, size));
        }
        String exact = status == null || status.trim().isEmpty() ? null : status;
        return Result.ok(OrderPageResponse.from(orderService.page(keyword, exact, page, size)));
    }

    /**
     * 修改订单。订单号保持不变。
     *
     * @param id 主键
     * @param request 新内容
     * @return 修改后的订单
     */
    @PutMapping("/{id}")
    public Result<OrderResponse> update(@PathVariable("id") Long id, @Valid @RequestBody CreateOrderRequest request) {
        return Result.ok(OrderResponse.from(orderService.update(id, toCommand(request))));
    }

    /**
     * 逻辑删除订单。
     *
     * @param id 主键
     * @return 空 data
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        orderService.delete(id);
        return Result.ok(null);
    }

    /**
     * 看板统计。
     *
     * @return 指标、状态分布和近 7 日趋势
     */
    @GetMapping("/stats")
    public Result<OrderStats> stats() {
        return Result.ok(orderStatsService.current());
    }

    /**
     * 逻辑删除现有订单后重新写入 8 条示例。
     *
     * @return 写入条数
     */
    @PostMapping("/reset")
    public Result<ResetResponse> reset() {
        return Result.ok(new ResetResponse(demoDataService.reset()));
    }

    /**
     * 把请求体转成创建或修改命令。
     *
     * @param request 请求体
     * @return 命令
     */
    private CreateOrderCommand toCommand(CreateOrderRequest request) {
        return new CreateOrderCommand(request.getCustomerName(), request.getProductName(), request.getQuantity(),
                request.getUnitPrice(), request.getStatus().name(), request.getRemark());
    }

    /**
     * 判断是不是五种合法状态之一。
     *
     * @param status 状态
     * @return 合法时为 true
     */
    private boolean known(String status) {
        return "PENDING".equals(status) || "PAID".equals(status) || "SHIPPED".equals(status)
                || "COMPLETED".equals(status) || "CANCELLED".equals(status);
    }

    /**
     * 非法状态时返回的空页。页码和页大小仍要合法。
     *
     * @param page 页码
     * @param size 页大小
     * @return 空页
     */
    private OrderPageResponse empty(Integer page, Integer size) {
        int pageNo = page == null ? 1 : page.intValue();
        int pageSize = size == null ? 10 : size.intValue();
        if (pageNo < 1 || (pageSize != 10 && pageSize != 20 && pageSize != 50)) {
            throw new BadOrderRequestException("页码或页大小不合法");
        }
        OrderPage blank = new OrderPage(0L, pageNo, pageSize, Collections.<Order>emptyList());
        return OrderPageResponse.from(blank);
    }
}
