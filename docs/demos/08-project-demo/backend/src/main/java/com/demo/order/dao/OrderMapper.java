package com.demo.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.order.dao.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表访问。
 *
 * @author 张颖
 * @date 2026/10/13
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
