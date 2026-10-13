package com.demo.order.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 创建订单的 HTTP 行为。
 *
 * @author 张颖
 * @date 2026/10/13
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerTest {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter ORDER_NO_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final String ROW_SQL = "SELECT id, order_no, customer_name, product_name, quantity, "
            + "unit_price, total_amount, status, remark, is_deleted, create_time, update_time "
            + "FROM t_order WHERE order_no = ?";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 每轮清空并重建表结构。
     */
    @BeforeEach
    void resetDatabase() {
        flyway.clean();
        flyway.migrate();
    }

    /**
     * 提交订单后，响应与库中新行一致。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void createOrderResponseMatchesPersistedRow() throws Exception {
        String body = "{"
                + "\"customerName\":\"张三\","
                + "\"productName\":\"键盘\","
                + "\"quantity\":2,"
                + "\"unitPrice\":\"12.30\","
                + "\"status\":\"PENDING\","
                + "\"remark\":\"加急\""
                + "}";

        MvcResult result = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();

        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode data = objectMapper.readTree(json).get("data");
        assertThat(data.get("deleted")).isNull();
        assertThat(data.get("isDeleted")).isNull();
        assertThat(data.get("customerName").asText()).isEqualTo("张三");
        assertThat(data.get("productName").asText()).isEqualTo("键盘");
        assertThat(data.get("quantity").asInt()).isEqualTo(2);
        assertThat(data.get("unitPrice").asText()).isEqualTo("12.30");
        assertThat(data.get("totalAmount").asText()).isEqualTo("24.60");
        assertThat(data.get("status").asText()).isEqualTo("PENDING");
        assertThat(data.get("remark").asText()).isEqualTo("加急");
        assertThat(data.get("id").asLong()).isPositive();

        String orderNo = data.get("orderNo").asText();
        Map<String, Object> row = jdbcTemplate.queryForMap(ROW_SQL, orderNo);
        assertThat(row.get("order_no")).isEqualTo(orderNo);
        assertThat(row.get("customer_name")).isEqualTo("张三");
        assertThat(row.get("product_name")).isEqualTo("键盘");
        assertThat(((Number) row.get("quantity")).intValue()).isEqualTo(2);
        assertThat((BigDecimal) row.get("unit_price")).isEqualByComparingTo("12.30");
        assertThat((BigDecimal) row.get("total_amount")).isEqualByComparingTo("24.60");
        assertThat(row.get("status")).isEqualTo("PENDING");
        assertThat(row.get("remark")).isEqualTo("加急");
        assertThat(((Number) row.get("is_deleted")).intValue()).isZero();
        assertThat(((Number) row.get("id")).longValue()).isEqualTo(data.get("id").asLong());

        String createTime = formatTime(row.get("create_time"));
        String updateTime = formatTime(row.get("update_time"));
        assertThat(data.get("createTime").asText()).isEqualTo(createTime);
        assertThat(data.get("updateTime").asText()).isEqualTo(updateTime);
    }

    /**
     * 订单号由后端生成，形态固定，相同订单号再次写入被拒绝。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void generatedOrderNoMatchesPatternAndDuplicateWriteIsRejected() throws Exception {
        String body = "{"
                + "\"customerName\":\"张三\","
                + "\"productName\":\"键盘\","
                + "\"quantity\":1,"
                + "\"unitPrice\":\"1.00\","
                + "\"status\":\"PENDING\","
                + "\"orderNo\":\"ORD0000000000000000\""
                + "}";
        JsonNode data = postOrder(body);
        String orderNo = data.get("orderNo").asText();
        assertThat(orderNo).isNotEqualTo("ORD0000000000000000");
        assertThat(orderNo).hasSize(20);
        assertThat(orderNo).matches("ORD\\d{17}");
        LocalDateTime stamped = LocalDateTime.parse(orderNo.substring(3, 17), ORDER_NO_TIME);
        assertThat(stamped).isAfter(LocalDateTime.now().minusMinutes(2));
        assertThat(stamped).isBefore(LocalDateTime.now().plusMinutes(2));

        String insertSql = "INSERT INTO t_order (order_no, customer_name, product_name, quantity, "
                + "unit_price, total_amount, status, is_deleted, create_time, update_time) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        LocalDateTime now = LocalDateTime.now().withNano(0);
        assertThatThrownBy(() -> jdbcTemplate.update(insertSql, orderNo, "李四", "鼠标", 1,
                new BigDecimal("1.00"), new BigDecimal("1.00"), "PAID", 0, now, now))
                .isInstanceOf(DuplicateKeyException.class);
    }

    /**
     * 总金额只按数量乘单价、HALF_UP 保留两位，忽略调用方传入的总金额。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void totalAmountIsComputedByServerAndIgnoresClientValue() throws Exception {
        String body = "{"
                + "\"customerName\":\"张三\","
                + "\"productName\":\"键盘\","
                + "\"quantity\":3,"
                + "\"unitPrice\":\"1.005\","
                + "\"totalAmount\":\"999.99\","
                + "\"status\":\"PENDING\""
                + "}";
        JsonNode data = postOrder(body);
        assertThat(data.get("unitPrice").asText()).isEqualTo("1.01");
        assertThat(data.get("totalAmount").asText()).isEqualTo("3.02");

        Map<String, Object> row = jdbcTemplate.queryForMap(ROW_SQL, data.get("orderNo").asText());
        BigDecimal unitPrice = (BigDecimal) row.get("unit_price");
        BigDecimal totalAmount = (BigDecimal) row.get("total_amount");
        assertThat(unitPrice).isEqualByComparingTo("1.01");
        assertThat(unitPrice.scale()).isEqualTo(2);
        assertThat(totalAmount).isEqualByComparingTo("3.02");
        assertThat(totalAmount.scale()).isEqualTo(2);
        assertThat(totalAmount).isNotEqualByComparingTo("999.99");
    }

    /**
     * 五个状态在创建时都可以直接保存。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void everyStatusCanBeStoredDirectly() throws Exception {
        String[] statuses = new String[] {"PENDING", "PAID", "SHIPPED", "COMPLETED", "CANCELLED"};
        for (String status : statuses) {
            String body = "{"
                    + "\"customerName\":\"张三\","
                    + "\"productName\":\"键盘\","
                    + "\"quantity\":1,"
                    + "\"unitPrice\":\"2.00\","
                    + "\"status\":\"" + status + "\""
                    + "}";
            JsonNode data = postOrder(body);
            assertThat(data.get("status").asText()).isEqualTo(status);
            Map<String, Object> row = jdbcTemplate.queryForMap(ROW_SQL, data.get("orderNo").asText());
            assertThat(row.get("status")).isEqualTo(status);
        }
    }

    /**
     * 空库只靠迁移就能创建，再清一次迁移后行为相同。
     *
     * @throws Exception 请求失败时抛出
     */
    @Test
    void cleanMigrateAllowsCreateAndRepeats() throws Exception {
        String body = "{"
                + "\"customerName\":\"张三\","
                + "\"productName\":\"键盘\","
                + "\"quantity\":1,"
                + "\"unitPrice\":\"2.00\","
                + "\"status\":\"PAID\""
                + "}";
        for (int round = 0; round < 2; round++) {
            flyway.clean();
            flyway.migrate();
            Integer before = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_order", Integer.class);
            assertThat(before).isZero();
            JsonNode data = postOrder(body);
            assertThat(data.get("orderNo").asText()).matches("ORD\\d{17}");
            assertThat(data.get("totalAmount").asText()).isEqualTo("2.00");
            Integer after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_order", Integer.class);
            assertThat(after).isEqualTo(1);
        }
    }

    /**
     * 把库中的时间格式化成接口约定的字符串。
     *
     * @param value 查询结果中的时间
     * @return yyyy-MM-dd HH:mm:ss
     */
    private String formatTime(Object value) {
        LocalDateTime dateTime;
        if (value instanceof LocalDateTime) {
            dateTime = (LocalDateTime) value;
        } else if (value instanceof Timestamp) {
            dateTime = ((Timestamp) value).toLocalDateTime();
        } else {
            throw new IllegalStateException("无法识别的时间类型");
        }
        return dateTime.format(DATE_TIME);
    }

    /**
     * 提交创建请求并返回 data。
     *
     * @param body 请求体
     * @return data 节点
     * @throws Exception 请求失败时抛出
     */
    private JsonNode postOrder(String body) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readTree(json).get("data");
    }
}
