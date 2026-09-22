import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 日期工具类（Java 17 迁移版）
 *
 * 变更点清单：
 * | 原写法                              | 新写法                        | 原因 |
 * |-------------------------------------|-------------------------------|------|
 * | SimpleDateFormat / Date             | LocalDate.parse / toString    | java.time 不可变、线程安全 |
 * | Calendar.add(DAY_OF_MONTH, n)       | date.plusDays(n)              | 可读、无副作用 |
 * | getTime() 毫秒差换算天数            | ChronoUnit.DAYS.between       | 语义明确，无魔法数字 |
 * | getActualMaximum(DAY_OF_MONTH)      | date.getDayOfMonth() == lengthOfMonth() | 无 Calendar 样板代码 |
 * | 匿名内部类 Comparator               | Comparator.comparing + 方法引用 | lambda 替代 |
 * | Collections.sort(copy, cmp)         | copy.sort(cmp)                | List 自带 sort |
 */
public class DateUtils {

    private static final String PATTERN = "yyyy-MM-dd";

    /** 字符串转日期，格式不合法返回 null */
    public static LocalDate parse(String text) {
        try {
            return LocalDate.parse(text);
        } catch (java.time.format.DateTimeParseException e) {
            return null;
        }
    }

    /** 日期转字符串 */
    public static String format(LocalDate date) {
        return date.toString(); // LocalDate 默认即为 yyyy-MM-dd
    }

    /** 给日期加 N 天 */
    public static LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }

    /** 计算两个日期之间相差的天数（按自然日） */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /** 判断是否月末最后一天 */
    public static boolean isLastDayOfMonth(LocalDate date) {
        return date.getDayOfMonth() == date.lengthOfMonth();
    }

    /** 按日期倒序排序 */
    public static List<LocalDate> sortDesc(List<LocalDate> dates) {
        List<LocalDate> copy = new ArrayList<>(dates);
        copy.sort(Comparator.comparing(LocalDate::toEpochDay).reversed());
        return copy;
    }
}
