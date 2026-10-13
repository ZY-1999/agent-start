package com.demo.order.web;

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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
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
 * 列表、修改、删除、汇总、重置和前端回退。
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
     * 插入一行未删除订单。
     *
     * @param customer 客户名称
     * @param status 状态
     * @param amount 单价与总金额
     * @param time 创建时间
     * @return 订单号
     */
    private String insertRow(String customer, String status, String amount, LocalDateTime time) {
        String orderNo = String.format("ORD%017d", sequence.incrementAndGet());
        jdbcTemplate.update(
                "INSERT INTO t_order (order_no, customer_name, product_name, quantity, unit_price, total_amount, "
                        + "status, is_deleted, create_time, update_time) VALUES (?,?,?,?,?,?,?,0,?,?)",
                orderNo, customer, "商品", Integer.valueOf(1), new BigDecimal(amount), new BigDecimal(amount), status,
                Timestamp.valueOf(time), Timestamp.valueOf(time));
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
}
