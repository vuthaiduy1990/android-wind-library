package the.wind.library.calendar;

import android.icu.util.Calendar;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Wrapper of a month
 */
public class MonthInfo implements Serializable {

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
     * @param stdCal   standard calendar class, ex {@link android.icu.util.GregorianCalendar}
     * @param lunarCal lunar calendar, ex {@link android.icu.util.ChineseCalendar}
     */
    public MonthInfo(Calendar stdCal, @Nullable Calendar lunarCal) {
        this.year = stdCal.get(Calendar.YEAR);
        this.month = stdCal.get(Calendar.MONTH);
        this.dateInfoList = CalendarUtil.getMonthDays(stdCal, lunarCal, stdCal.getTime());
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

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
