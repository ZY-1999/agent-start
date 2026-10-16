package com.demo.order.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 列表、修改、删除、汇总、重置、导出和前端回退。
 *
 * @author 张颖
 * @date 2026/10/13
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerApiTest {

    /** 生成互不重复的测试订单号。 */
    private final AtomicInteger sequence = new AtomicInteger();

    /** 接口调用。 */
    @Autowired
    private MockMvc mockMvc;

    /** 测试库迁移。 */
    @Autowired
    private Flyway flyway;

    /** 直接查表。 */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** 解析响应。 */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 每轮清空并重建表结构。测试配置不在启动时写示例数据。
     */
    @BeforeEach
    void resetDatabase() {
        flyway.clean();
        flyway.migrate();
    }

    /**
     * 新订单排在前面；页大小不在 10、20、50 时返回 400。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void pagesNewestFirstAndRejectsUnknownSize() throws Exception {
        LocalDateTime older = LocalDateTime.of(2026, 10, 1, 8, 0, 0);
        LocalDateTime newer = LocalDateTime.of(2026, 10, 2, 8, 0, 0);
        insertRow("老客户", "PENDING", "10.00", older);
        String newerNo = insertRow("新客户", "PAID", "20.00", newer);

        MvcResult result = mockMvc.perform(get("/api/orders").param("page", "1").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(2))
                .andReturn();
        JsonNode records = read(result).path("data").path("records");
        assertThat(records.get(0).path("orderNo").asText()).isEqualTo(newerNo);
        assertThat(records.get(0).path("customerName").asText()).isEqualTo("新客户");

        mockMvc.perform(get("/api/orders").param("size", "15"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
        mockMvc.perform(get("/api/orders").param("page", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    /**
     * 关键字只匹配前缀，并与状态做与运算；无法识别的状态返回空页。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void prefixAndStatusFilter() throws Exception {
        String zhangNo = insertRow("张三", "PENDING", "10.00", LocalDateTime.of(2026, 10, 12, 12, 0, 0));
        insertRow("李四", "PAID", "20.00", LocalDateTime.of(2026, 10, 11, 12, 0, 0));

        mockMvc.perform(get("/api/orders").param("kw", zhangNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].customerName").value("张三"));
        mockMvc.perform(get("/api/orders").param("kw", "张"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].customerName").value("张三"));
        mockMvc.perform(get("/api/orders").param("kw", "三"))
                .andExpect(jsonPath("$.data.total").value(0));
        mockMvc.perform(get("/api/orders").param("kw", zhangNo.substring(zhangNo.length() - 4)))
                .andExpect(jsonPath("$.data.total").value(0));
        mockMvc.perform(get("/api/orders").param("kw", "张").param("status", "PAID"))
                .andExpect(jsonPath("$.data.total").value(0));
        mockMvc.perform(get("/api/orders").param("status", "PAID"))
                .andExpect(jsonPath("$.data.records[0].customerName").value("李四"));
        mockMvc.perform(get("/api/orders").param("status", "UNKNOWN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(0));
    }

    /**
     * 百分号和下划线按字面量前缀匹配。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void keywordWildcardIsLiteral() throws Exception {
        insertRow("百分号客户", "PENDING", "10.00", LocalDateTime.of(2026, 10, 12, 12, 0, 0));
        mockMvc.perform(get("/api/orders").param("kw", "%"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0));
        mockMvc.perform(get("/api/orders").param("kw", "百分"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    /**
     * 导出复用筛选条件并按创建时间倒序；备注转义逗号、引号和公式前缀，非法状态只有表头。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void exportCsvFiltersSortsAndEscapes() throws Exception {
        insertRow("老客户", "PENDING", "10.00", LocalDateTime.of(2026, 10, 1, 8, 0, 0),
                "备注,含\"引号\"");
        String newerNo = insertRow("新客户", "PAID", "20.00", LocalDateTime.of(2026, 10, 2, 8, 0, 0),
                "=SUM(A1)");
        insertRow("张三", "COMPLETED", "30.00", LocalDateTime.of(2026, 10, 3, 8, 0, 0));

        MvcResult all = mockMvc.perform(get("/api/orders/export"))
                .andExpect(status().isOk())
                .andReturn();
        String csv = all.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(csv).startsWith(OrderCsv.BOM);
        assertThat(all.getResponse().getContentType()).isEqualTo("text/csv;charset=UTF-8");
        assertThat(all.getResponse().getHeader("Content-Disposition"))
                .startsWith("attachment; filename=\"orders-export-");
        String[] lines = csv.substring(1).split("\r\n");
        assertThat(lines).hasSize(4);
        assertThat(lines[0]).isEqualTo("\"订单号\",\"客户名称\",\"商品名称\",\"数量\",\"单价\",\"总金额\",\"状态\",\"备注\",\"创建时间\"");
        assertThat(lines[1]).contains("张三").contains("已完成").contains("2026-10-03 08:00:00");
        assertThat(lines[2]).contains(newerNo).contains("新客户").contains("已支付")
                .contains("\"'=SUM(A1)\"").contains("2026-10-02 08:00:00");
        assertThat(lines[3]).contains("老客户").contains("待支付").contains("\"备注,含\"\"引号\"\"\"");

        String filtered = mockMvc.perform(get("/api/orders/export").param("status", "PAID"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(filtered.substring(1).split("\r\n")).hasSize(2);
        assertThat(filtered).contains(newerNo).doesNotContain("张三");

        String unknown = mockMvc.perform(get("/api/orders/export").param("status", "UNKNOWN"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(unknown.substring(1).split("\r\n")).hasSize(1);
    }

    /**
     * Excel 导出端点返回标准 MIME 与带时间戳的附件名；工作簿 sheet 名为「订单」，表头与 CSV 完全一致。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void exportXlsxServesStandardAttachmentReadableWithCsvHeader() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders/export.xlsx"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentType())
                .isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        assertThat(result.getResponse().getHeader("Content-Disposition"))
                .matches("attachment; filename=\"orders-export-\\d{14}\\.xlsx\"");

        ReadBack readBack = readWorkbook(result.getResponse().getContentAsByteArray());
        assertThat(readBack.sheetName).isEqualTo("订单");
        assertThat(readBack.rows).hasSize(1);
        assertThat(texts(readBack.rows.get(0))).containsExactly(
                "订单号", "客户名称", "商品名称", "数量", "单价", "总金额", "状态", "备注", "创建时间");
    }

    /**
     * 订单号/状态/备注/创建时间为字符串单元格且时间格式固定，数量与金额为数值单元格且金额带两位小数显示格式。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void exportXlsxWritesTypedCellsForSumAndDisplay() throws Exception {
        String orderNo = insertRow("张三", "COMPLETED", "30.00", LocalDateTime.of(2026, 10, 3, 8, 0, 0), "备注文本");

        MvcResult result = mockMvc.perform(get("/api/orders/export.xlsx"))
                .andExpect(status().isOk())
                .andReturn();
        ReadBack readBack = readWorkbook(result.getResponse().getContentAsByteArray());
        assertThat(readBack.rows).hasSize(2);
        Map<Integer, ReadCellData<?>> dataRow = readBack.rows.get(1);
        assertThat(dataRow.get(0).getType()).isEqualTo(CellDataTypeEnum.STRING);
        assertThat(dataRow.get(0).getStringValue()).isEqualTo(orderNo);
        assertThat(dataRow.get(6).getType()).isEqualTo(CellDataTypeEnum.STRING);
        assertThat(dataRow.get(6).getStringValue()).isEqualTo("已完成");
        assertThat(dataRow.get(7).getType()).isEqualTo(CellDataTypeEnum.STRING);
        assertThat(dataRow.get(7).getStringValue()).isEqualTo("备注文本");
        assertThat(dataRow.get(8).getType()).isEqualTo(CellDataTypeEnum.STRING);
        assertThat(dataRow.get(8).getStringValue()).isEqualTo("2026-10-03 08:00:00");
        assertThat(dataRow.get(3).getType()).isEqualTo(CellDataTypeEnum.NUMBER);
        assertThat(dataRow.get(3).getNumberValue()).isEqualByComparingTo("1");
        assertThat(dataRow.get(4).getType()).isEqualTo(CellDataTypeEnum.NUMBER);
        assertThat(dataRow.get(4).getNumberValue()).isEqualByComparingTo("30.00");
        assertThat(dataRow.get(5).getType()).isEqualTo(CellDataTypeEnum.NUMBER);
        assertThat(dataRow.get(5).getNumberValue()).isEqualByComparingTo("30.00");
        assertThat(dataRow.get(4).getDataFormatData().getFormat()).isEqualTo("0.00");
        assertThat(dataRow.get(4).getDataFormatData().getIndex().intValue()).isEqualTo(2);
        assertThat(dataRow.get(5).getDataFormatData().getFormat()).isEqualTo("0.00");
        assertThat(dataRow.get(5).getDataFormatData().getIndex().intValue()).isEqualTo(2);
    }

    /**
     * Excel 导出沿用列表过滤语义：关键字只做订单号/客户名称前缀、状态精确匹配，导出恒为创建时间倒序。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void exportXlsxFiltersAndSortsLikePage() throws Exception {
        insertRow("老客户", "PENDING", "10.00", LocalDateTime.of(2026, 10, 1, 8, 0, 0));
        String newerNo = insertRow("新客户", "PAID", "20.00", LocalDateTime.of(2026, 10, 2, 8, 0, 0));
        String zhangNo = insertRow("张三", "COMPLETED", "30.00", LocalDateTime.of(2026, 10, 3, 8, 0, 0));

        ReadBack all = readWorkbook(mockMvc.perform(get("/api/orders/export.xlsx")).andReturn()
                .getResponse().getContentAsByteArray());
        assertThat(all.rows).hasSize(4);
        assertThat(texts(all.rows.get(1)).get(0)).isEqualTo(zhangNo);
        assertThat(texts(all.rows.get(2)).get(0)).isEqualTo(newerNo);

        ReadBack byKw = readWorkbook(mockMvc.perform(get("/api/orders/export.xlsx").param("kw", "张"))
                .andReturn().getResponse().getContentAsByteArray());
        assertThat(byKw.rows).hasSize(2);
        assertThat(texts(byKw.rows.get(1)).get(1)).isEqualTo("张三");

        ReadBack byStatus = readWorkbook(mockMvc.perform(get("/api/orders/export.xlsx").param("status", "PAID"))
                .andReturn().getResponse().getContentAsByteArray());
        assertThat(byStatus.rows).hasSize(2);
        assertThat(texts(byStatus.rows.get(1)).get(0)).isEqualTo(newerNo);

        ReadBack both = readWorkbook(mockMvc
                .perform(get("/api/orders/export.xlsx").param("kw", "新").param("status", "PAID"))
                .andReturn().getResponse().getContentAsByteArray());
        assertThat(both.rows).hasSize(2);
        assertThat(texts(both.rows.get(1)).get(0)).isEqualTo(newerNo);

        ReadBack none = readWorkbook(mockMvc
                .perform(get("/api/orders/export.xlsx").param("kw", "新").param("status", "PENDING"))
                .andReturn().getResponse().getContentAsByteArray());
        assertThat(none.rows).hasSize(1);
    }

    /**
     * 过滤无匹配与非法状态都返回 200 且只有表头的合法工作簿。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void exportXlsxEmptyResultAndUnknownStatusAreHeaderOnly() throws Exception {
        insertRow("张三", "PENDING", "10.00", LocalDateTime.of(2026, 10, 1, 8, 0, 0));

        MvcResult noMatch = mockMvc.perform(get("/api/orders/export.xlsx").param("kw", "李四"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(noMatch.getResponse().getContentType())
                .isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        ReadBack emptyResult = readWorkbook(noMatch.getResponse().getContentAsByteArray());
        assertThat(emptyResult.sheetName).isEqualTo("订单");
        assertThat(emptyResult.rows).hasSize(1);
        assertThat(texts(emptyResult.rows.get(0)).get(0)).isEqualTo("订单号");

        ReadBack unknown = readWorkbook(mockMvc
                .perform(get("/api/orders/export.xlsx").param("status", "UNKNOWN"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray());
        assertThat(unknown.rows).hasSize(1);
        assertThat(texts(unknown.rows.get(0)).get(0)).isEqualTo("订单号");
    }

    /**
     * 公式样内容原样写入字符串单元格，不加 CSV 的防公式前缀。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void exportXlsxWritesFormulaLikeRemarkAsPlainText() throws Exception {
        insertRow("新客户", "PAID", "20.00", LocalDateTime.of(2026, 10, 2, 8, 0, 0), "=SUM(A1)");

        ReadBack readBack = readWorkbook(mockMvc.perform(get("/api/orders/export.xlsx"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray());
        assertThat(readBack.rows).hasSize(2);
        assertThat(readBack.rows.get(1).get(7).getType()).isEqualTo(CellDataTypeEnum.STRING);
        assertThat(readBack.rows.get(1).get(7).getStringValue()).isEqualTo("=SUM(A1)");
    }

    /**
     * 修改保留订单号并重算金额；不存在的主键返回 404。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void updateKeepsOrderNoAndRecomputesAmount() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson("演示客户", "演示商品", 1, "1.00", "PENDING")))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode order = read(created).path("data");
        long id = order.path("id").asLong();
        String orderNo = order.path("orderNo").asText();

        mockMvc.perform(put("/api/orders/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson("演示客户", "演示商品", 3, "1.005", "PAID")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNo").value(orderNo))
                .andExpect(jsonPath("$.data.unitPrice").value("1.01"))
                .andExpect(jsonPath("$.data.totalAmount").value("3.02"));

        mockMvc.perform(put("/api/orders/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson("演示客户", "演示商品", 1, "1.00", "PENDING")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    /**
     * 删除后列表看不到，再删返回 404，行仍留在表里。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void logicalDeleteHidesOrderFromReads() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson("待删客户", "商品", 1, "8.00", "PENDING")))
                .andExpect(status().isOk())
                .andReturn();
        long id = read(created).path("data").path("id").asLong();

        mockMvc.perform(delete("/api/orders/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        mockMvc.perform(get("/api/orders"))
                .andExpect(jsonPath("$.data.total").value(0));
        mockMvc.perform(delete("/api/orders/" + id))
                .andExpect(status().isNotFound());
        Integer deleted = jdbcTemplate.queryForObject("SELECT is_deleted FROM t_order WHERE id = ?", Integer.class, id);
        assertThat(deleted).isEqualTo(1);

        mockMvc.perform(put("/api/orders/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson("待删客户", "改后仍在", 1, "8.00", "PAID")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productName").value("改后仍在"));
        Integer stillDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM t_order WHERE id = ?", Integer.class, Long.valueOf(id));
        assertThat(stillDeleted).isEqualTo(1);
        mockMvc.perform(get("/api/orders"))
                .andExpect(jsonPath("$.data.total").value(0));
    }

    /**
     * 指标统计全部未删除订单；趋势只含近 7 天，已删除订单不计入。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void statsWindowAndDeletedExclusion() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDateTime windowStart = today.minusDays(6).atStartOfDay();
        insertRow("窗内早", "PENDING", "25.00", windowStart);
        insertRow("窗内今", "COMPLETED", "25.00", today.atTime(23, 59, 59));
        insertRow("窗外早", "PAID", "25.00", windowStart.minusSeconds(1));
        insertRow("窗外晚", "SHIPPED", "25.00", today.plusDays(1).atStartOfDay());
        String deletedNo = insertRow("已删", "CANCELLED", "25.00", today.atTime(12, 0, 0));
        Long deletedId = jdbcTemplate.queryForObject(
                "SELECT id FROM t_order WHERE order_no = ?", Long.class, deletedNo);
        mockMvc.perform(delete("/api/orders/" + deletedId)).andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/orders/stats")).andExpect(status().isOk()).andReturn();
        JsonNode data = read(result).path("data");
        assertThat(data.path("kpi").path("totalCount").asInt()).isEqualTo(4);
        assertThat(data.path("kpi").path("totalAmount").asText()).isEqualTo("100.00");
        assertThat(data.path("kpi").path("pendingCount").asInt()).isEqualTo(1);
        assertThat(data.path("kpi").path("completedCount").asInt()).isEqualTo(1);
        JsonNode trend = data.path("trend");
        assertThat(trend.size()).isEqualTo(7);
        int trendCount = 0;
        Set<String> dates = new HashSet<String>();
        for (JsonNode point : trend) {
            trendCount += point.path("count").asInt();
            dates.add(point.path("date").asText());
        }
        assertThat(trendCount).isEqualTo(2);
        assertThat(dates).doesNotContain(today.plusDays(1).toString());
        assertThat(trend.get(0).path("date").asText()).isEqualTo(today.minusDays(6).toString());
        assertThat(trend.get(6).path("date").asText()).isEqualTo(today.toString());
    }

    /**
     * 重置逻辑删除旧行后再写入 8 条示例，不追加。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void resetRestoresEightSamplesWithoutAppending() throws Exception {
        insertRow("旧数据", "PENDING", "1.00", LocalDateTime.now().withNano(0));

        mockMvc.perform(post("/api/orders/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.inserted").value(8));
        mockMvc.perform(get("/api/orders").param("size", "50"))
                .andExpect(jsonPath("$.data.total").value(8));

        MvcResult stats = mockMvc.perform(get("/api/orders/stats")).andExpect(status().isOk()).andReturn();
        JsonNode data = read(stats).path("data");
        assertThat(data.path("kpi").path("totalAmount").asText()).isEqualTo("3515.16");
        assertThat(data.path("kpi").path("pendingCount").asInt()).isEqualTo(2);
        assertThat(data.path("kpi").path("completedCount").asInt()).isEqualTo(2);
        for (JsonNode point : data.path("trend")) {
            assertThat(point.path("count").asInt()).isGreaterThan(0);
        }
        Integer deleted = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM t_order WHERE is_deleted = 1", Integer.class);
        Integer active = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM t_order WHERE is_deleted = 0", Integer.class);
        assertThat(deleted).isEqualTo(1);
        assertThat(active).isEqualTo(8);
    }

    /**
     * 未知的非接口路径返回入口页，未知接口仍是 404。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void unknownFrontendPathFallsBackToEntry() throws Exception {
        MvcResult page = mockMvc.perform(get("/orders")).andExpect(status().isOk()).andReturn();
        String body = page.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(body).contains("<div id=\"root\">");
        assertThat(body).doesNotContain("\"code\"");
        mockMvc.perform(get("/api/missing")).andExpect(status().isNotFound());
    }

    /**
     * 插入一行未删除订单，不带备注。
     *
     * @param customer 客户名称
     * @param status 状态
     * @param amount 单价与总金额
     * @param time 创建时间
     * @return 订单号
     */
    private String insertRow(String customer, String status, String amount, LocalDateTime time) {
        return insertRow(customer, status, amount, time, null);
    }

    /**
     * 插入一行未删除订单。
     *
     * @param customer 客户名称
     * @param status 状态
     * @param amount 单价与总金额
     * @param time 创建时间
     * @param remark 备注，可为空
     * @return 订单号
     */
    private String insertRow(String customer, String status, String amount, LocalDateTime time, String remark) {
        String orderNo = String.format("ORD%017d", sequence.incrementAndGet());
        jdbcTemplate.update(
                "INSERT INTO t_order (order_no, customer_name, product_name, quantity, unit_price, total_amount, "
                        + "status, remark, is_deleted, create_time, update_time) VALUES (?,?,?,?,?,?,?,?,0,?,?)",
                orderNo, customer, "商品", Integer.valueOf(1), new BigDecimal(amount), new BigDecimal(amount), status,
                remark, Timestamp.valueOf(time), Timestamp.valueOf(time));
        return orderNo;
    }

    /**
     * 创建或修改请求体。
     *
     * @param customer 客户名称
     * @param product 商品名称
     * @param quantity 数量
     * @param unitPrice 单价
     * @param status 状态
     * @return JSON
     */
    private String orderJson(String customer, String product, int quantity, String unitPrice, String status) {
        return "{\"customerName\":\"" + customer + "\",\"productName\":\"" + product + "\",\"quantity\":" + quantity
                + ",\"unitPrice\":" + unitPrice + ",\"status\":\"" + status + "\"}";
    }

    /**
     * 读取响应 JSON。
     *
     * @param result MVC 结果
     * @return JSON
     * @throws Exception 解析失败时抛出
     */
    private JsonNode read(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    /**
     * 用 EasyExcel 把响应字节读回原始单元格，不经过默认的字符串转换，保留类型与格式信息。
     *
     * @param body 响应字节
     * @return sheet 名与全部行（表头行在前）
     */
    private ReadBack readWorkbook(byte[] body) {
        final List<Map<Integer, ReadCellData<?>>> rows = new ArrayList<Map<Integer, ReadCellData<?>>>();
        final String[] sheetName = new String[1];
        EasyExcel.read(new ByteArrayInputStream(body))
                .registerReadListener(new AnalysisEventListener<Map<Integer, ReadCellData<?>>>() {
                    @Override
                    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                        sheetName[0] = context.readSheetHolder().getSheetName();
                        rows.add(headMap);
                    }

                    @Override
                    public void invoke(Map<Integer, ReadCellData<?>> row, AnalysisContext context) {
                        rows.add(row);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                    }
                })
                .useDefaultListener(false)
                .sheet()
                .doRead();
        return new ReadBack(sheetName[0], rows);
    }

    /**
     * 按列号取一行的字符串值。
     *
     * @param row 读回的一行
     * @return 从第 0 列起的字符串值
     */
    private List<String> texts(Map<Integer, ReadCellData<?>> row) {
        List<String> texts = new ArrayList<String>();
        for (int column = 0; column < 9; column++) {
            texts.add(row.get(column).getStringValue());
        }
        return texts;
    }

    /**
     * EasyExcel 读回的工作簿内容。
     *
     * @author 张颖
     * @date 2026/10/14
     */
    private static class ReadBack {

        /** sheet 名。 */
        private final String sheetName;

        /** 全部行，每行是列号到原始单元格的映射。 */
        private final List<Map<Integer, ReadCellData<?>>> rows;

        /**
         * @param sheetName sheet 名
         * @param rows 全部行
         */
        private ReadBack(String sheetName, List<Map<Integer, ReadCellData<?>>> rows) {
            this.sheetName = sheetName;
            this.rows = rows;
        }
    }
}
