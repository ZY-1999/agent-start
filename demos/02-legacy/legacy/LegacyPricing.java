import java.util.Date;

/**
 * ？？？—— 文件头有一行 2008 年的 SVN 关键字，其他什么都没有
 * $Id: Pricing.java 4172 2008-03-14 $
 */
public class LegacyPricing {

    public double calc(double p, int t, boolean f, Date d) {
        double r = 1.0;
        if (t > 100) {
            r -= 0.1;
        } else if (t > 50) {
            r -= 0.05;
        }
        if (f) {
            r -= 0.07;
        }
        if (d != null && d.getMonth() == 5) {
            r -= 0.2;
        }
        double x = p * t * r;
        if (x > 999) {
            x -= 50;
        }
        return x;
    }
}
