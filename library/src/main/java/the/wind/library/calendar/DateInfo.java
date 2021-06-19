package the.wind.library.calendar;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import the.wind.library.CWBundle;

/**
 * Wrapper of a date
 */
public class DateInfo implements Serializable {

    // bundle data
    private final CWBundle bundle = new CWBundle();
    // date info
    private Date date;
    private int year;
    private int month;
    private int dayOfMonth;
    private int dayOfWeek;
    // decorator info
    private int background;
    private boolean hasEvent;

    /**
     * Private constructor for serialization
     */
    private DateInfo() {
    }

    /**
     * Constructor
     *
     * @param unmodifiedCal calendar abstract class, ex {@link java.util.GregorianCalendar}, {@link android.icu.util.ChineseCalendar}
     */
    public DateInfo(Calendar unmodifiedCal) {
        this();
        this.date = unmodifiedCal.getTime();
        this.year = unmodifiedCal.get(Calendar.YEAR);
        this.month = unmodifiedCal.get(Calendar.MONTH);
        this.dayOfMonth = unmodifiedCal.get(Calendar.DAY_OF_MONTH);
        this.dayOfWeek = unmodifiedCal.get(Calendar.DAY_OF_WEEK);
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public int hashCode() {
        return date.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof DateInfo) {
            return this.date.equals(((DateInfo) o).getDate());
        }
        return false;
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return bundle data
     */
    public CWBundle bundle() {
        return bundle;
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
     * @return day of month
     */
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * @return day of week
     */
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */
}
