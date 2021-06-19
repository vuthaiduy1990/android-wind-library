package the.wind.library.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public final class CalendarUtil {

    /**
     * Get all days of month by given date
     *
     * @param unmodifiedCal calendar abstract class, ex {@link java.util.GregorianCalendar}, {@link android.icu.util.ChineseCalendar}
     * @param date          given date
     * @return list of date info
     */
    public static List<DateInfo> getMonthDays(Calendar unmodifiedCal, Date date) {
        Calendar cal = (Calendar) unmodifiedCal.clone();
        List<DateInfo> days = new LinkedList<>();
        cal.setTime(date);
        cal.set(Calendar.DATE, 1); // Go to first day of month

        // add missing week-days (null) until the first day of month
        int firstDay = cal.get(Calendar.DAY_OF_WEEK);
        if (firstDay == Calendar.SUNDAY) {
            firstDay = Calendar.SATURDAY + 1;
        }
        for (int i = Calendar.MONDAY; i < firstDay; i++) {
            days.add(null);
        }

        // add day of target months
        int month = cal.get(Calendar.MONTH);
        while (cal.get(Calendar.MONTH) == month) {
            days.add(new DateInfo(cal));
            cal.add(Calendar.DATE, 1);
        }

        // add missing week-days (null) after the last day of month
        int remainingDayNum = 42 - days.size();
        for (int i = 0; i < remainingDayNum; i++) {
            days.add(null);
        }

        return days;
    }

    /**
     * Create link between month
     *
     * @param unmodifiedCal calendar abstract class, ex {@link java.util.GregorianCalendar}, {@link android.icu.util.ChineseCalendar}
     * @param date          select month
     * @param preLoaded     the number of month will be preloaded in the left side and right side of current month
     * @return selected month
     */
    public static MonthInfo createMonthLink(Calendar unmodifiedCal, Date date, int preLoaded) {
        // set selected month
        Calendar cal = (Calendar) unmodifiedCal.clone();
        cal.setTime(date);

        // Start from first day of oldest month
        cal.add(Calendar.MONTH, -preLoaded);
        cal.set(Calendar.DATE, 1);

        // Create link between months
        MonthInfo cur = new MonthInfo(cal);
        MonthInfo pre = null;
        MonthInfo selectedMonth = null;
        int idx = -preLoaded;
        while (idx <= preLoaded) {
            cal.add(Calendar.MONTH, 1);
            MonthInfo next = idx < preLoaded ? new MonthInfo(cal) : null;
            if (idx == 0) {
                selectedMonth = cur;
            }
            cur.link(pre, next);
            pre = cur;
            cur = next;
            idx++;
        }
        return selectedMonth;
    }

}
