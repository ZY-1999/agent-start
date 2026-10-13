package com.demo.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.order.dao.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单表访问。
 *
 * @author 张颖
 * @date 2026/10/13
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 按前缀和状态分页查询未删除订单。
     *
     * @param keyword 订单号或客户名称前缀，空则不按关键字过滤
     * @param status 精确状态，空则不按状态过滤
     * @param limit 本页条数
     * @param offset 跳过的条数
     * @return 当前页订单
     */
    List<Order> selectActivePage(@Param("keyword") String keyword, @Param("status") String status,
            @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 统计符合前缀和状态的未删除订单数。
     *
     * @param keyword 订单号或客户名称前缀，空则不按关键字过滤
     * @param status 精确状态，空则不按状态过滤
     * @return 订单数
     */
    long countActive(@Param("keyword") String keyword, @Param("status") String status);

    /**
     * 统计全部未删除订单，供启动时判断要不要灌入示例。
     *
     * @return 未删除订单数
     */
    long countNotDeleted();

    /**
     * 按主键读取订单，包含已逻辑删除的行。
     *
     * @param id 主键
     * @return 订单；主键不存在时为空
     */
    Order selectByIdExplicit(@Param("id") Long id);

    /**
     * 更新订单内容，不改订单号，也不改删除标记。
     *
     * @param order 已填好新内容的订单
     * @return 更新行数
     */
    int updateContent(Order order);

    /**
     * 把一条未删除订单标成已删除。
     *
     * @param id 主键
     * @param updateTime 更新时间
     * @return 更新行数；已经删除或不存在时为 0
     */
    int markDeleted(@Param("id") Long id, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 把全部未删除订单标成已删除。
     *
     * @param updateTime 更新时间
     * @return 更新行数
     */
    int markAllDeleted(@Param("updateTime") LocalDateTime updateTime);

    /**
     * 按状态汇总未删除订单的笔数和金额。
     *
     * @return 每个已出现状态一行
     */
    List<StatusAmountRow> sumByStatus();

    /**
     * 按自然日汇总未删除订单的笔数和金额。
     *
     * @param start 窗口开始，含
     * @param end 窗口结束，含
     * @return 每个有订单的日期一行
     */
    List<TrendAmountRow> sumByDay(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
