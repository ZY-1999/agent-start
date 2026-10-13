package com.demo.order.service.impl;

import com.demo.order.dao.OrderMapper;
import com.demo.order.dao.entity.Order;
import com.demo.order.service.CreateOrderCommand;
import com.demo.order.service.OrderConflictException;
import com.demo.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单创建。订单号、金额舍入和撞号重试都留在这里。
 *
 * @author 张颖
 * @date 2026/10/13
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static final DateTimeFormatter ORDER_NO_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final int MAX_ATTEMPTS = 3;

    private final OrderMapper orderMapper;

    private final TransactionTemplate transactionTemplate;

    /**
     * @param orderMapper 订单表访问
     * @param transactionManager 事务管理器
     */
    public OrderServiceImpl(OrderMapper orderMapper, PlatformTransactionManager transactionManager) {
        this.orderMapper = orderMapper;
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.transactionTemplate = template;
    }

    /**
     * 创建订单。撞上唯一约束时换号，最多尝试 3 次。
     *
     * @param command 创建入参
     * @return 已落库的订单
     * @throws OrderConflictException 重试耗尽仍然撞号
     */
    @Override
    public Order create(CreateOrderCommand command) {
        BigDecimal totalAmount = command.getUnitPrice()
                .multiply(BigDecimal.valueOf(command.getQuantity().longValue()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal unitPrice = command.getUnitPrice().setScale(2, RoundingMode.HALF_UP);
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String orderNo = nextOrderNo();
            try {
                return transactionTemplate.execute(status ->
                        insert(command, orderNo, unitPrice, totalAmount));
            } catch (RuntimeException ex) {
                if (!isDuplicateOrderNo(ex)) {
                    LOGGER.error("创建订单失败", ex);
                    throw ex;
                }
                LOGGER.warn("订单号冲突，准备换号重试");
            }
        }
        LOGGER.error("订单号连续冲突，创建失败");
        throw new OrderConflictException("订单号冲突，请稍后重试");
    }

    /**
     * 写入一行未删除的订单。
     *
     * @param command 创建入参
     * @param orderNo 订单号
     * @param unitPrice 已舍入的单价
     * @param totalAmount 已舍入的总金额
     * @return 已落库的订单
     */
    private Order insert(CreateOrderCommand command, String orderNo, BigDecimal unitPrice, BigDecimal totalAmount) {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setCustomerName(command.getCustomerName());
        order.setProductName(command.getProductName());
        order.setQuantity(command.getQuantity());
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(totalAmount);
        order.setStatus(command.getStatus());
        order.setRemark(command.getRemark());
        order.setDeleted(0);
        order.setCreateTime(now);
        order.setUpdateTime(now);
        orderMapper.insert(order);
        return order;
    }

    /**
     * 生成 20 位订单号：ORD + yyyyMMddHHmmss + 3 位随机数字。
     *
     * @return 订单号
     */
    private String nextOrderNo() {
        String time = LocalDateTime.now().format(ORDER_NO_TIME);
        int suffix = ThreadLocalRandom.current().nextInt(1000);
        return "ORD" + time + String.format("%03d", suffix);
    }

    /**
     * 判断异常链是否为订单号唯一约束冲突。
     *
     * @param ex 插入抛出的异常
     * @return 是冲突时返回 true
     */
    private boolean isDuplicateOrderNo(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            if (current instanceof DuplicateKeyException) {
                return true;
            }
            String message = current.getMessage();
            if (message != null && message.contains("Duplicate entry")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
