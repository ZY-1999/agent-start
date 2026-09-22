import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 订单计价器
 *
 * 业务规则：
 * 1. 总价 = 单价 × 数量
 * 2. 数量满 100 件打 95 折
 * 3. VIP 再打 9 折
 * 4. 金额四舍五入到分
 */
public class PriceCalculator {

    public BigDecimal total(BigDecimal unitPrice, int quantity, boolean vip) {
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));

        if (quantity >= 100) {
            total = total.multiply(new BigDecimal("0.95"));
        }
        if (vip) {
            total = total.multiply(new BigDecimal("0.90"));
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
