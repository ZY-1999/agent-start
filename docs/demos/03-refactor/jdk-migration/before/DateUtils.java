import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类（JDK 8 时代写法，准备迁移到 Java 17）
 */
public class DateUtils {

    private static final String PATTERN = "yyyy-MM-dd";

    /** 字符串转日期，格式不合法返回 null */
    public static Date parse(String text) {
        try {
            return new SimpleDateFormat(PATTERN).parse(text);
        } catch (ParseException e) {
            return null;
        }
    }

    /** 日期转字符串 */
    public static String format(Date date) {
        return new SimpleDateFormat(PATTERN).format(date);
    }

    /** 给日期加 N 天 */
    public static Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    /** 计算两个日期之间相差的天数（按自然日） */
    public static long daysBetween(Date start, Date end) {
        long ms = end.getTime() - start.getTime();
        return ms / (1000 * 60 * 60 * 24);
    }

    /** 判断是否月末最后一天 */
    public static boolean isLastDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int today = c.get(Calendar.DAY_OF_MONTH);
        int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        return today == maxDay;
    }

    /** 按日期倒序排序（匿名内部类写法） */
    public static List<Date> sortDesc(List<Date> dates) {
        List<Date> copy = new ArrayList<Date>(dates);
        Collections.sort(copy, new Comparator<Date>() {
            @Override
            public int compare(Date a, Date b) {
                return b.compareTo(a);
            }
        });
        return copy;
    }
}
