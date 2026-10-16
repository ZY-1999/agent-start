package com.demo.order.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.demo.order.common.OrderStatus;
import com.demo.order.dao.entity.Order;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单 Excel 组装。生成带样式的 .xlsx：数量与金额为数值单元格（金额两位小数显示格式），
 * 其余为文本单元格；文本单元格天然不会被 Excel 当公式执行，因此不加 CSV 版的防公式前缀。
 * 表头加粗加浅灰底，列宽自适应。
 *
 * @author 张颖
 * @date 2026/10/14
 */
final class OrderExcel {

    /** 时间输出格式，与页面和 CSV 一致。 */
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private OrderExcel() {
    }

    /**
     * 把订单列表写成 .xlsx。空列表时只有表头。
     *
     * @param orders 已按创建时间倒序的订单
     * @param out 输出流
     */
    static void xlsx(List<Order> orders, OutputStream out) {
        EasyExcel.write(out, Row.class)
                .registerWriteHandler(headStyle())
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("订单")
                .doWrite(rows(orders));
    }

    /**
     * 表头样式：加粗加浅灰底。
     *
     * @return EasyExcel 内置水平样式策略
     */
    private static HorizontalCellStyleStrategy headStyle() {
        WriteCellStyle head = new WriteCellStyle();
        head.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        head.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        WriteFont font = new WriteFont();
        font.setBold(true);
        head.setWriteFont(font);
        return new HorizontalCellStyleStrategy(head, (WriteCellStyle) null);
    }

    /**
     * 订单转成导出行。状态转中文，创建时间转文本。
     *
     * @param orders 已按创建时间倒序的订单
     * @return 导出行
     */
    private static List<Row> rows(List<Order> orders) {
        List<Row> rows = new ArrayList<Row>(orders.size());
        for (Order order : orders) {
            Row row = new Row();
            row.setOrderNo(order.getOrderNo());
            row.setCustomerName(order.getCustomerName());
            row.setProductName(order.getProductName());
            row.setQuantity(order.getQuantity());
            row.setUnitPrice(order.getUnitPrice());
            row.setTotalAmount(order.getTotalAmount());
            row.setStatus(OrderStatus.labelOf(order.getStatus()));
            row.setRemark(order.getRemark());
            row.setCreateTime(order.getCreateTime() == null ? null : TIME.format(order.getCreateTime()));
            rows.add(row);
        }
        return rows;
    }

    /**
     * 导出行模型。列顺序与 CSV 表头一致；数量为整数数值，金额带两位小数显示格式。
     *
     * @author 张颖
     * @date 2026/10/14
     */
    private static class Row {

        /** 订单号。 */
        @ExcelProperty(value = "订单号", index = 0)
        private String orderNo;

        /** 客户名称。 */
        @ExcelProperty(value = "客户名称", index = 1)
        private String customerName;

        /** 商品名称。 */
        @ExcelProperty(value = "商品名称", index = 2)
        private String productName;

        /** 数量。 */
        @ExcelProperty(value = "数量", index = 3)
        private Integer quantity;

        /** 单价。 */
        @ExcelProperty(value = "单价", index = 4)
        @NumberFormat("0.00")
        private BigDecimal unitPrice;

        /** 总金额。 */
        @ExcelProperty(value = "总金额", index = 5)
        @NumberFormat("0.00")
        private BigDecimal totalAmount;

        /** 状态，中文标签。 */
        @ExcelProperty(value = "状态", index = 6)
        private String status;

        /** 备注。 */
        @ExcelProperty(value = "备注", index = 7)
        private String remark;

        /** 创建时间，文本。 */
        @ExcelProperty(value = "创建时间", index = 8)
        private String createTime;

        /**
         * @return 订单号
         */
        public String getOrderNo() {
            return orderNo;
        }

        /**
         * @param orderNo 订单号
         */
        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        /**
         * @return 客户名称
         */
        public String getCustomerName() {
            return customerName;
        }

        /**
         * @param customerName 客户名称
         */
        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        /**
         * @return 商品名称
         */
        public String getProductName() {
            return productName;
        }

        /**
         * @param productName 商品名称
         */
        public void setProductName(String productName) {
            this.productName = productName;
        }

        /**
         * @return 数量
         */
        public Integer getQuantity() {
            return quantity;
        }

        /**
         * @param quantity 数量
         */
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        /**
         * @return 单价
         */
        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        /**
         * @param unitPrice 单价
         */
        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        /**
         * @return 总金额
         */
        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        /**
         * @param totalAmount 总金额
         */
        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        /**
         * @return 状态
         */
        public String getStatus() {
            return status;
        }

        /**
         * @param status 状态
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * @return 备注
         */
        public String getRemark() {
            return remark;
        }

        /**
         * @param remark 备注
         */
        public void setRemark(String remark) {
            this.remark = remark;
        }

        /**
         * @return 创建时间
         */
        public String getCreateTime() {
            return createTime;
        }

        /**
         * @param createTime 创建时间
         */
        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
