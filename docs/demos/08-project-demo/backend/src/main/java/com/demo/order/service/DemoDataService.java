package com.demo.order.service;

import com.demo.order.dao.OrderMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 演示用 8 条示例订单。日期相对今天，不写进 Flyway。
 *
 * @author 张颖
 * @date 2026/10/13
 */
@Service
public class DemoDataService {

    /** 固定的 8 条示例。天数是距今天的偏移。 */
    private static final Sample[] SAMPLES = new Sample[] {
        new Sample("张三", "机械键盘", 2, "199.50", "PENDING", 0),
        new Sample("李四", "显示器", 1, "1299.00", "PAID", 0),
        new Sample("王五", "鼠标", 3, "89.90", "SHIPPED", 1),
        new Sample("赵六", "耳机", 1, "399.00", "COMPLETED", 2),
        new Sample("张三", "扩展坞", 1, "259.00", "CANCELLED", 3),
        new Sample("陈七", "键盘", 2, "150.25", "PENDING", 4),
        new Sample("李四", "摄像头", 1, "189.00", "COMPLETED", 5),
        new Sample("周八", "音箱", 4, "99.99", "PAID", 6)
    };

    /** 订单表访问。 */
    private final OrderMapper orderMapper;

    /** 订单业务，示例也走创建逻辑。 */
    private final OrderService orderService;

    /**
     * @param orderMapper 订单表访问
     * @param orderService 订单业务
     */
    public DemoDataService(OrderMapper orderMapper, OrderService orderService) {
        this.orderMapper = orderMapper;
        this.orderService = orderService;
    }

    /**
     * 没有任何未删除订单时写入示例。
     */
    public void seedIfEmpty() {
        if (orderMapper.countNotDeleted() == 0L) {
            insertSamples();
        }
    }

    /**
     * 逻辑删除现有订单后重新写入同样的 8 条示例。
     *
     * @return 写入条数
     */
    public int reset() {
        orderMapper.markAllDeleted(LocalDateTime.now().withNano(0));
        return insertSamples();
    }

    /**
     * 按今天往前推，写入全部示例。
     *
     * @return 写入条数
     */
    private int insertSamples() {
        LocalDate today = LocalDate.now();
        for (Sample sample : SAMPLES) {
            CreateOrderCommand command = new CreateOrderCommand(sample.customerName, sample.productName,
                    sample.quantity, new BigDecimal(sample.unitPrice), sample.status, null);
            orderService.createAt(command, today.minusDays(sample.daysAgo.intValue()).atTime(12, 0, 0));
        }
        return SAMPLES.length;
    }

    /**
     * 一条示例的固定字段。
     *
     * @author 张颖
     * @date 2026/10/13
     */
    private static final class Sample {

        /** 客户名称。 */
        private final String customerName;

        /** 商品名称。 */
        private final String productName;

        /** 单价文本。 */
        private final String unitPrice;

        /** 状态。 */
        private final String status;

        /** 数量。 */
        private final Integer quantity;

        /** 距今天的天数。 */
        private final Integer daysAgo;

        /**
         * @param customerName 客户名称
         * @param productName 商品名称
         * @param quantity 数量
         * @param unitPrice 单价文本
         * @param status 状态
         * @param daysAgo 距今天的天数
         */
        private Sample(String customerName, String productName, int quantity, String unitPrice,
                String status, int daysAgo) {
            this.customerName = customerName;
            this.productName = productName;
            this.quantity = Integer.valueOf(quantity);
            this.unitPrice = unitPrice;
            this.status = status;
            this.daysAgo = Integer.valueOf(daysAgo);
        }
    }
}
