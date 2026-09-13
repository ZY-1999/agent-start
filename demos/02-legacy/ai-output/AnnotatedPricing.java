import java.util.Date;

/**
 * 祖传定价计算器（B2C 商城，2008 年起服役）
 *
 * 业务规则（由 AI 推断并经业务方确认）：
 * 1. 按购买数量给阶梯折扣：>100 件 9 折；>50 件 95 折
 * 2. 会员（或首单，待确认）额外 93 折
 * 3. 促销月（6 月，见下方 bug 说明）额外 8 折
 * 4. 应付总额满 999 减 50
 * 折扣可叠加，无上限审核。
 */
public class LegacyPricing {

    /**
     * 计算应付总额。
     *
     * @param p 单价
     * @param t 购买数量
     * @param f 会员标记（true = 享受会员折扣）
     * @param d 促销判定日期，传 null 表示不参与促销
     * @return 应付总额（元）
     */
    public double calc(double p, int t, boolean f, Date d) {
        // 折扣系数：从 1.0 开始，逐项叠加往下减
        double r = 1.0;

        // 数量阶梯折扣：>100 件减 10%；否则 >50 件减 5%
        if (t > 100) {
            r -= 0.1;
        } else if (t > 50) {
            r -= 0.05;
        }

        // 会员额外折扣：减 7%
        if (f) {
            r -= 0.07;
        }

        // ⚠️ BUG：Date.getMonth() 从 0 开始，5 实际是 6 月。
        // 若业务规则是"五月促销"，则每年 6 月才会错误触发。
        // 且该方法自 JDK 1.1 起已废弃。
        if (d != null && d.getMonth() == 5) {
            r -= 0.2;
        }

        // 应付 = 单价 × 数量 × 总折扣
        double x = p * t * r;

        // 满 999 减 50（叠加在折扣之后）
        if (x > 999) {
            x -= 50;
        }

        // ⚠️ 风险：double 计算金额存在精度丢失，应迁移到 BigDecimal
        return x;
    }
}
