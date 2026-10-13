package com.demo.order.service.impl;

import com.demo.order.dao.OrderMapper;
import com.demo.order.dao.entity.Order;
import com.demo.order.service.Amounts;
import com.demo.order.service.BadOrderRequestException;
import com.demo.order.service.CreateOrderCommand;
import com.demo.order.service.OrderNotFoundException;
import com.demo.order.service.OrderPage;
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

    /** 日志。 */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    /** 订单号中的时间部分。 */
    private static final DateTimeFormatter ORDER_NO_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** 订单号冲突时的最大尝试次数。 */
    private static final int MAX_ATTEMPTS = 3;

    /** 订单表访问。 */
    private final OrderMapper orderMapper;

    /** 每次插入使用独立事务。 */
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
        return createAt(command, LocalDateTime.now().withNano(0));
    }

    /**
     * 按指定创建时间落库，供示例数据把日期相对到今天。
     *
     * @param command 创建入参
     * @param createTime 创建时间，同时作为更新时间
     * @return 已落库的订单
     */
    @Override
    public Order createAt(CreateOrderCommand command, LocalDateTime createTime) {
        BigDecimal unitPrice = Amounts.scale(command.getUnitPrice());
        BigDecimal totalAmount = Amounts.total(command.getQuantity(), command.getUnitPrice());
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String orderNo = nextOrderNo();
            try {
                return transactionTemplate.execute(status -> insert(
                        command, orderNo, unitPrice, totalAmount, createTime));
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
     * 分页查询未删除订单。关键字按字面量做右前缀，不接受左模糊。
     *
     * @param keyword 订单号或客户名称前缀
     * @param status 精确状态
     * @param page 页码
     * @param size 页大小
     * @return 当前页
     */
    @Override
    public OrderPage page(String keyword, String status, Integer page, Integer size) {
        int pageNo = page == null ? 1 : page.intValue();
        int pageSize = size == null ? 10 : size.intValue();
        if (pageNo < 1 || (pageSize != 10 && pageSize != 20 && pageSize != 50)) {
            throw new BadOrderRequestException("页码或页大小不合法");
        }
        String kw = likePrefix(keyword);
        String exactStatus = blankToNull(status);
        long total = orderMapper.countActive(kw, exactStatus);
        int offset = (pageNo - 1) * pageSize;
        return new OrderPage(total, pageNo, pageSize, orderMapper.selectActivePage(kw, exactStatus, pageSize, offset));
    }

    /**
     * 修改订单内容。主键不存在才是 404，已逻辑删除的行仍可修改。
     *
     * @param id 主键
     * @param command 新内容
     * @return 修改后的订单
     */
    @Override
    public Order update(Long id, CreateOrderCommand command) {
        Order existing = orderMapper.selectByIdExplicit(id);
        if (existing == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        existing.setCustomerName(command.getCustomerName());
        existing.setProductName(command.getProductName());
        existing.setQuantity(command.getQuantity());
        existing.setUnitPrice(Amounts.scale(command.getUnitPrice()));
        existing.setTotalAmount(Amounts.total(command.getQuantity(), command.getUnitPrice()));
        existing.setStatus(command.getStatus());
        existing.setRemark(blankToNull(command.getRemark()));
        existing.setUpdateTime(LocalDateTime.now().withNano(0));
        orderMapper.updateContent(existing);
        return existing;
    }

    /**
     * 逻辑删除一条未删除订单。
     *
     * @param id 主键
     */
    @Override
    public void delete(Long id) {
        int updated = orderMapper.markDeleted(id, LocalDateTime.now().withNano(0));
        if (updated == 0) {
            throw new OrderNotFoundException("订单不存在");
        }
    }

    /**
     * 插入一条订单。
     *
     * @param command 创建入参
     * @param orderNo 订单号
     * @param unitPrice 已舍入的单价
     * @param totalAmount 已舍入的总金额
     * @param createTime 创建时间
     * @return 已落库的订单
     */
    private Order insert(CreateOrderCommand command, String orderNo, BigDecimal unitPrice, BigDecimal totalAmount,
            LocalDateTime createTime) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setCustomerName(command.getCustomerName());
        order.setProductName(command.getProductName());
        order.setQuantity(command.getQuantity());
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(totalAmount);
        order.setStatus(command.getStatus());
        order.setRemark(blankToNull(command.getRemark()));
        order.setDeleted(0);
        order.setCreateTime(createTime);
        order.setUpdateTime(createTime);
        orderMapper.insert(order);
        return order;
    }

    /**
     * 空白字符串视为未填写。
     *
     * @param value 原字符串
     * @return 原值；空白时为空
     */
    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value;
    }

    /**
     * 把关键字收成字面量前缀，转义 LIKE 通配符。
     *
     * @param keyword 原始关键字
     * @return 可放进 LIKE 的前缀；未填写时为空
     */
    private String likePrefix(String keyword) {
        String value = blankToNull(keyword);
        if (value == null) {
            return null;
        }
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    /**
     * 生成 ORD + 时间 + 3 位随机数。
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
