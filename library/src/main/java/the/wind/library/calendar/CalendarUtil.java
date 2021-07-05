package the.wind.library.calendar;

import android.icu.util.Calendar;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.Nullable;

public final class CalendarUtil {

    /**
     * Get unique id of date
     *
     * @param year       year
     * @param month      month
     * @param dayOfMonth day of month
     * @return unique date id
     */
    public static String toId(int year, int month, int dayOfMonth) {
        return String.format("%s-%s-%s", year, month, dayOfMonth);
    }

    /**
     * Get unique id of month
     *
     * @param year  year
     * @param month month
     * @return unique month id
     */
    public static String toId(int year, int month) {
        return String.format("%s-%s", year, month);
    }

    /**
     * Get unique id of date
     *
     * @param cal calendar date
     * @return unique date id
     */
    public static String toId(Calendar cal) {
        return toId(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Get all days of month by given date
     *
     * @param stdCal       standard calendar class, ex {@link android.icu.util.GregorianCalendar}
     * @param lunarCal     lunar calendar, ex {@link android.icu.util.ChineseCalendar}
     * @param date         given date
     * @param weekStartDay start day of week
     * @return list of date info
     */
    public static List<DateInfo> getMonthDays(Calendar stdCal, @Nullable Calendar lunarCal, Date date, WeekStartsOn weekStartDay) {
        Calendar _stdCal = (Calendar) stdCal.clone();
        List<DateInfo> days = new LinkedList<>();
        _stdCal.setTime(date);
        _stdCal.set(Calendar.DATE, 1); // Go to first day of month

        Calendar _lunarCal = null;
        if (lunarCal != null) {
            _lunarCal = (Calendar) lunarCal.clone();
            _lunarCal.setTime(_stdCal.getTime());
        }

        // add missing week-days (null) until the first day of month
        int firstDayOfMonth = _stdCal.get(Calendar.DAY_OF_WEEK);
        int startDayOfWeek = weekStartDay.getDay();
        switch (weekStartDay) {
            case SATURDAY:
                startDayOfWeek = 0;
                if (firstDayOfMonth == Calendar.SATURDAY) {
                    firstDayOfMonth = 0;
                }
                break;
            case MONDAY:
                if (firstDayOfMonth == Calendar.SUNDAY) {
                    firstDayOfMonth = Calendar.SATURDAY + 1;
                }
                break;
            default:
                break;
        }
        for (int i = startDayOfWeek; i < firstDayOfMonth; i++) {
            days.add(null);
        }

        // add day of target months
        int month = _stdCal.get(Calendar.MONTH);
        while (_stdCal.get(Calendar.MONTH) == month) {
            days.add(new DateInfo(_stdCal, _lunarCal));
            _stdCal.add(Calendar.DATE, 1);
            if (_lunarCal != null) {
                _lunarCal.add(Calendar.DATE, 1);
            }
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
     * @param stdCal       standard calendar class, ex {@link android.icu.util.GregorianCalendar}
     * @param lunarCal     lunar calendar, ex {@link android.icu.util.ChineseCalendar}
     * @param date         select month
     * @param preLoaded    the number of month will be preloaded in the left side and right side of current month
     * @param weekStartDay start day of week
     * @return selected month
     */
    public static MonthInfo createMonthLink(Calendar stdCal, @Nullable Calendar lunarCal, Date date, WeekStartsOn weekStartDay, int preLoaded) {
        // set selected month
        Calendar _stdCal = (Calendar) stdCal.clone();
        _stdCal.setTime(date);

        // Start from first day of oldest month
        _stdCal.add(Calendar.MONTH, -preLoaded);
        _stdCal.set(Calendar.DATE, 1);

        // Create link between months
        MonthInfo cur = new MonthInfo(_stdCal, lunarCal, weekStartDay);
        MonthInfo pre = null;
        MonthInfo selectedMonth = null;
        int idx = -preLoaded;
        while (idx <= preLoaded) {
            _stdCal.add(Calendar.MONTH, 1);
            MonthInfo next = idx < preLoaded ? new MonthInfo(_stdCal, lunarCal, weekStartDay) : null;
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
