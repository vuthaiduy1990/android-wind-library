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
     * @param solarCal     solar calendar class, ex {@link android.icu.util.GregorianCalendar}
     * @param lunarCal     lunar calendar, ex {@link android.icu.util.ChineseCalendar}
     * @param date         given date
     * @param weekStartDay start day of week
     * @return list of date info
     */
    public static List<DateInfo> getMonthDays(Calendar solarCal, @Nullable Calendar lunarCal, Date date, WeekStartsOn weekStartDay) {
        List<DateInfo> days = new LinkedList<>();
        solarCal.setTime(date);
        solarCal.set(Calendar.DATE, 1); // Go to first day of month

        // add missing week-days (null) until the first day of month
        int firstDayOfMonth = solarCal.get(Calendar.DAY_OF_WEEK);
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
        int month = solarCal.get(Calendar.MONTH);
        while (solarCal.get(Calendar.MONTH) == month) {
            days.add(new DateInfo(solarCal, lunarCal));
            solarCal.add(Calendar.DATE, 1);
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
     * @param solarCal     solar calendar class, ex {@link android.icu.util.GregorianCalendar}
     * @param lunarCal     lunar calendar, ex {@link android.icu.util.ChineseCalendar}
     * @param date         select month
     * @param preLoaded    the number of month will be preloaded in the left side and right side of current month
     * @param weekStartDay start day of week
     * @return selected month
     */
    public static MonthInfo createMonthLink(Calendar solarCal, @Nullable Calendar lunarCal, Date date, WeekStartsOn weekStartDay, int preLoaded) {
        // set selected month
        Calendar cal = (Calendar) solarCal.clone();
        cal.setTime(date);

        // Start from first day of oldest month
        cal.add(Calendar.MONTH, -preLoaded);
        cal.set(Calendar.DATE, 1);

        // Create link between months
        MonthInfo cur = new MonthInfo((Calendar) cal.clone(), lunarCal, weekStartDay);
        MonthInfo pre = null;
        MonthInfo selectedMonth = null;
        int idx = -preLoaded;
        while (idx <= preLoaded) {
            cal.add(Calendar.MONTH, 1);
            MonthInfo next = idx < preLoaded ? new MonthInfo((Calendar) cal.clone(), lunarCal, weekStartDay) : null;
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

    /**
     * Get timezone of given calendar date
     *
     * @param cal  calendar
     * @param date date date
     * @return timezone number in range [-12,  +14]
     */
    public static float getTimeZone(Calendar cal, Date date) {
        return cal.getTimeZone().getOffset(date.getTime()) / 1000f / 60f / 60f;
    }

    /**
     * Get lunar date info
     *
     * @param lunarCal lunar calendar
     * @param date     date
     * @param year     solar year
     * @param month    solar month
     * @param day      solar day
     * @return [year, month, day, leap]
     */
    public static int[] getLunarDateInfo(Calendar lunarCal, Date date, int year, int month, int day) {
        int[] result = new int[4];
        if (VietnameseCalendar.class.equals(lunarCal.getClass())) {
            float timezone = getTimeZone(lunarCal, date);
            int[] lunarDate = ((VietnameseCalendar) lunarCal).convertSolar2Lunar(year, month + 1, day, timezone);
            result[0] = lunarDate[0]; // year
            result[1] = lunarDate[1] - 1; // month
            result[2] = lunarDate[2]; // day
            result[3] = lunarDate[3]; // leap
        } else {
            lunarCal.setTime(date);
            result[0] = lunarCal.get(Calendar.YEAR);
            result[1] = lunarCal.get(Calendar.MONTH);
            result[2] = lunarCal.get(Calendar.DAY_OF_MONTH);
            result[3] = lunarCal.get(Calendar.IS_LEAP_MONTH);
        }
        return result;
    }

}
