package com.demo.order.web;

import com.demo.order.common.OrderStatus;
import com.demo.order.dao.entity.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 订单 CSV 组装。输出 UTF-8 文本，带 BOM 头，Excel 双击可直接打开。
 * 所有字段都加引号并转义内部引号；以 =、+、-、@ 开头的值前置单引号，防止被 Excel 当公式执行。
 *
 * @author 张颖
 * @date 2026/10/14
 */
final class OrderCsv {

    /** UTF-8 BOM，让 Excel 识别编码。 */
    static final String BOM = "\uFEFF";

    /** 表头列名。 */
    private static final String[] HEADER_CELLS =
            {"订单号", "客户名称", "商品名称", "数量", "单价", "总金额", "状态", "备注", "创建时间"};

    /** 时间输出格式，与页面一致。 */
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private OrderCsv() {
    }

    /**
     * 组装整个导出文件内容。空列表时只有表头。
     *
     * @param orders 已按创建时间倒序的订单
     * @return 以 BOM 开头的 CSV 文本
     */
    static String csv(List<Order> orders) {
        StringBuilder builder = new StringBuilder(BOM.length() + 128 * (orders.size() + 1));
        builder.append(BOM);
        for (String head : HEADER_CELLS) {
            builder.append(cell(head)).append(',');
        }
        builder.setLength(builder.length() - 1);
        builder.append("\r\n");
        for (Order order : orders) {
            builder.append(cell(order.getOrderNo())).append(',')
                    .append(cell(order.getCustomerName())).append(',')
                    .append(cell(order.getProductName())).append(',')
                    .append(cell(order.getQuantity() == null ? "" : String.valueOf(order.getQuantity()))).append(',')
                    .append(cell(money(order.getUnitPrice()))).append(',')
                    .append(cell(money(order.getTotalAmount()))).append(',')
                    .append(cell(OrderStatus.labelOf(order.getStatus()))).append(',')
                    .append(cell(order.getRemark())).append(',')
                    .append(cell(order.getCreateTime() == null ? "" : TIME.format(order.getCreateTime())))
                    .append("\r\n");
        }
        return builder.toString();
    }

    /**
     * 金额转成两位小数字符串。
     *
     * @param amount 金额
     * @return 两位小数；空值时为空串
     */
    private static String money(BigDecimal amount) {
        return amount == null ? "" : amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    /**
     * 单元格转义：整体加引号，内部引号翻倍，公式前缀加单引号。
     *
     * @param value 原值
     * @return 转义后的单元格；空值时为空单元格
     */
    private static String cell(String value) {
        if (value == null || value.isEmpty()) {
            return "\"\"";
        }
        String safe = "=+-@".indexOf(value.charAt(0)) >= 0 ? "'" + value : value;
        return '"' + safe.replace("\"", "\"\"") + '"';
    }
}
