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

    // Unique ID
    private String id;

    // Standard date info
    private Date date;
    private int year;
    private int month;
    private int dayOfMonth;
    private int dayOfWeek;

    // Lunar date info
    private Date lunarDate;
    private int lunarYear;
    private int lunarMonth;
    private int lunarDayOfMonth;

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
        this.id = CalendarUtil.toId(year, month, dayOfMonth);
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

    /**
     * @return lundar date
     */
    public Date getLunarDate() {
        return lunarDate;
    }

    /**
     * @return lunar year
     */
    public int getLunarYear() {
        return lunarYear;
    }

    /**
     * @return lunar month
     */
    public int getLunarMonth() {
        return lunarMonth;
    }

    /**
     * @return lunar day of month
     */
    public int getLunarDayOfMonth() {
        return lunarDayOfMonth;
    }

    /**
     * @return true if the date is weekend (Saturday and Sunday)
     */
    public boolean isWeekend() {
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */
}
