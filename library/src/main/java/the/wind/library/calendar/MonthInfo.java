package the.wind.library.calendar;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

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
     * @param unmodifiedCal calendar abstract class, ex {@link java.util.GregorianCalendar}, {@link android.icu.util.ChineseCalendar}
     */
    public MonthInfo(Calendar unmodifiedCal) {
        this.year = unmodifiedCal.get(Calendar.YEAR);
        this.month = unmodifiedCal.get(Calendar.MONTH);
        this.dateInfoList = CalendarUtil.getMonthDays(unmodifiedCal, unmodifiedCal.getTime());
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
