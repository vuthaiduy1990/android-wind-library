package the.wind.library.calendar;

import android.icu.util.Calendar;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Wrapper of a month
 */
public class MonthInfo implements Serializable {

    // Month date. It should be start day of month
    private Date date;

    // Unique ID
    private String id;

    // month info
    private int year;
    private int month;
    private List<DateInfo> dateInfoList;

    // neighbour month
    private MonthInfo preMonth;
    private MonthInfo nextMonth;

    /**
     * Private constructor for serialization
     */
    private MonthInfo() {
    }

    /**
     * Constructor
     *
     * @param stdCal       standard calendar class, ex {@link android.icu.util.GregorianCalendar}
     * @param lunarCal     lunar calendar, ex {@link android.icu.util.ChineseCalendar}
     * @param weekStartDay start day of week
     */
    public MonthInfo(Calendar stdCal, @Nullable Calendar lunarCal, @NonNull WeekStartsOn weekStartDay) {
        this.date = stdCal.getTime();
        this.year = stdCal.get(Calendar.YEAR);
        this.month = stdCal.get(Calendar.MONTH);
        this.id = CalendarUtil.toId(year, month);
        this.dateInfoList = CalendarUtil.getMonthDays(stdCal, lunarCal, stdCal.getTime(), weekStartDay);
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return unique ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return target date
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * @return year
     */
    public int getYear() {
        return year;
    }

    /**
     * @return month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @return list of month date info
     */
    public List<DateInfo> getDateInfoList() {
        return dateInfoList;
    }

    /**
     * Set neighbour month
     *
     * @param pre  previous month
     * @param next next month
     */
    public void link(MonthInfo pre, MonthInfo next) {
        this.preMonth = pre;
        this.nextMonth = next;
    }

    /**
     * @return next month
     */
    public MonthInfo next() {
        return nextMonth;
    }

    /**
     * Set next month
     *
     * @param next next month
     */
    public void next(MonthInfo next) {
        nextMonth = next;
    }

    /**
     * @return previous month
     */
    public MonthInfo previous() {
        return preMonth;
    }

    /**
     * Set previous month
     *
     * @param previous previous month
     */
    public void previous(MonthInfo previous) {
        preMonth = previous;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */
}
