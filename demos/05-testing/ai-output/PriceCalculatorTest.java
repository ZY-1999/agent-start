import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PriceCalculator 单元测试（AI 参考生成）
 */
class PriceCalculatorTest {

    private final PriceCalculator calculator = new PriceCalculator();

    // ---------- 等价类：常规购买 ----------

    @ParameterizedTest(name = "单价{0} × 数量{1}，VIP={2} → {3}")
    @CsvSource({
            // unitPrice, quantity, vip, expected —— 期望值全部手算核对过
            "10.00,    1,  false,  10.00",     // 最小正常购买
            "10.50,    3,  false,  31.50",     // 小数单价
            "10.00,    1,  true,    9.00",     // VIP 无折扣叠加
            "0.01,     1,  false,   0.01",     // 最小单价
    })
    @DisplayName("常规购买：单价×数量，VIP 单独 9 折")
    void total_normalCases(String unit, int qty, boolean vip, String expected) {
        BigDecimal result = calculator.total(new BigDecimal(unit), qty, vip);
        // 用 compareTo 断言：equals 会比较 scale（100.00 != 100.0）
        assertTrue(result.compareTo(new BigDecimal(expected)) == 0,
                "期望 " + expected + "，实际 " + result);
    }

    // ---------- 边界值：折扣临界数量 99 / 100 / 101 ----------

    @ParameterizedTest(name = "数量 {0} → 期望 {1}")
    @CsvSource({
            "99,  990.00",    // 差一件不打折
            "100, 950.00",    // 临界：恰好满 100，打 95 折
            "101, 959.50",    // 刚过临界
    })
    @DisplayName("折扣临界：99 不打折，100/101 打 95 折")
    void total_discountBoundary(int qty, String expected) {
        BigDecimal result = calculator.total(new BigDecimal("10.00"), qty, false);
        assertTrue(result.compareTo(new BigDecimal(expected)) == 0,
                "期望 " + expected + "，实际 " + result);
    }

    @Test
    @DisplayName("VIP 与数量折扣叠加：100 件 VIP = 1000 × 0.95 × 0.90 = 855.00")
    void total_vipStacked() {
        BigDecimal result = calculator.total(new BigDecimal("10.00"), 100, true);
        assertTrue(result.compareTo(new BigDecimal("855.00")) == 0);
    }

    // ---------- 边界值：数量 0 与空购物 ----------

    @Test
    @DisplayName("数量 0：总价为 0（业务上应允许？建议产品确认）")
    void total_zeroQuantity() {
        BigDecimal result = calculator.total(new BigDecimal("10.00"), 0, false);
        assertTrue(result.compareTo(BigDecimal.ZERO) == 0);
    }

    // ---------- 非法输入（暴露防御缺失） ----------

    @ParameterizedTest(name = "数量 {0} 应该被拒绝，当前实现返回负数总额 —— 防御缺失")
    @ValueSource(ints = {-1, -100})
    @DisplayName("负数数量：当前实现返回负数总额（bug，见类末尾说明）")
    void total_negativeQuantity_currentBehaviorIsBug(int qty) {
        BigDecimal result = calculator.total(new BigDecimal("10.00"), qty, false);
        // 记录现状：当前返回负数。此测试用例展示"现状即 bug"，
        // 修复后应改为 assertThrows(IllegalArgumentException.class, ...)
        assertTrue(result.compareTo(BigDecimal.ZERO) < 0);
    }

    @ParameterizedTest(name = "单价 null（第 {index} 组）")
    @NullSource
    @DisplayName("单价 null：当前抛 NullPointerException，建议改为带业务信息的 IllegalArgumentException")
    void total_nullUnitPrice(BigDecimal unit) {
        assertThrows(NullPointerException.class,
                () -> calculator.total(unit, 1, false));
    }

    // ---------- 精度 ----------

    @Test
    @DisplayName("精度：0.1 元级别的单价 × 3 件不会产生浮点误差（BigDecimal 乘法无误差）")
    void total_precision() {
        BigDecimal result = calculator.total(new BigDecimal("0.10"), 3, false);
        assertTrue(result.compareTo(new BigDecimal("0.30")) == 0);
    }

    @Test
    @DisplayName("四舍五入到分：单价 9.99 × 1 件 VIP = 8.991 → 8.99")
    void total_rounding() {
        BigDecimal result = calculator.total(new BigDecimal("9.99"), 1, true);
        assertEquals(new BigDecimal("8.99"), result); // scale 确定为 2，此处 equals 安全
    }
}

/*
 * ============================================================
 * AI 对被测代码本身的 Review 结论（Prompt 第 5 条要求的回答）
 * ============================================================
 * 1. 【bug】负数数量未校验：total(10.00, -5, false) 返回 -50.00，
 *    等于"下单越多退钱越多"。建议入口处：
 *        if (quantity < 0) throw new IllegalArgumentException("数量不能为负");
 * 2. 【风险】超大数量：quantity 接近 Integer.MAX_VALUE 时
 *    BigDecimal.valueOf(quantity) 不会溢出，但业务上限（如单笔 9999 件）
 *    更应该在入口校验——这属于业务规则，需要人确认上限值
 * 3. 【建议】折扣率 0.95 / 0.90 硬编码，建议抽为常量或配置，
 *    便于促销活动调整与单测覆盖
 * 4. 【确认】数量 0 的语义（允许？还是应该拒绝？）需要产品确认
 */
