package com.demo.order.service;

import com.demo.order.dao.entity.Order;

/**
 * 订单业务。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public interface OrderService {

    /**
     * 创建订单：生成订单号、按单价计算总金额并落库。
     * 撞上唯一约束时换号，最多 3 次。
     *
     * @param command 创建入参
     * @return 已落库的订单
     * @throws OrderConflictException 重试耗尽仍然撞号
     */
    Order create(CreateOrderCommand command);

    /**
     * 按指定创建时间落库，供示例数据把日期相对到今天。
     *
     * @param command 创建入参
     * @param createTime 创建时间，同时作为更新时间
     * @return 已落库的订单
     * @throws OrderConflictException 重试耗尽仍然撞号
     */
    Order createAt(CreateOrderCommand command, java.time.LocalDateTime createTime);

    /**
     * 分页查询未删除订单。关键字是前缀，状态是精确匹配。
     *
     * @param keyword 订单号或客户名称前缀
     * @param status 状态；非法状态由调用方先处理
     * @param page 页码，从 1 开始
     * @param size 页大小，只允许 10、20、50
     * @return 当前页
     */
    OrderPage page(String keyword, String status, Integer page, Integer size);

    /**
     * 修改订单内容。保留原订单号，总金额按服务端重算。已逻辑删除的行仍可修改，删除标记不变。
     *
     * @param id 主键
     * @param command 新内容
     * @return 修改后的订单
     * @throws OrderNotFoundException 主键不存在
     */
    Order update(Long id, CreateOrderCommand command);

    /**
     * 逻辑删除一条未删除订单。
     *
     * @param id 主键
     * @throws OrderNotFoundException 主键不存在或已经删除
     */
    void delete(Long id);
}

