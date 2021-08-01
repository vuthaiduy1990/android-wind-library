package the.wind.library.calendar;

import android.icu.util.Calendar;

import java.io.Serializable;
import java.util.Date;

import androidx.annotation.Nullable;
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
    private boolean hasLunarDate = false;
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
     * @param solarCal solar calendar class, ex {@link android.icu.util.GregorianCalendar}
     * @param lunarCal lunar calendar, ex {@link android.icu.util.ChineseCalendar}
     */
    public DateInfo(Calendar solarCal, @Nullable Calendar lunarCal) {
        this();

        // solar calendar
        this.date = solarCal.getTime();
        this.year = solarCal.get(Calendar.YEAR);
        this.month = solarCal.get(Calendar.MONTH);
        this.dayOfMonth = solarCal.get(Calendar.DAY_OF_MONTH);
        this.dayOfWeek = solarCal.get(Calendar.DAY_OF_WEEK);
        this.id = CalendarUtil.toId(year, month, dayOfMonth);

        // lunar calendar
        if (lunarCal != null) {
            this.hasLunarDate = true;
            lunarCal.setTime(date);
            if (VietnameseCalendar.class.equals(lunarCal.getClass())) {
                int[] lunarDate = ((VietnameseCalendar) lunarCal).convertSolar2Lunar(
                        this.dayOfMonth, this.month + 1, this.year,
                        7);
                this.lunarDayOfMonth = lunarDate[0];
                this.lunarMonth = lunarDate[1] - 1;
                this.lunarYear = lunarDate[2];

            } else {
                this.lunarYear = lunarCal.get(Calendar.YEAR);
                this.lunarMonth = lunarCal.get(Calendar.MONTH);
                this.lunarDayOfMonth = lunarCal.get(Calendar.DAY_OF_MONTH);
            }
        }
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
     * @return true if having lunar date
     */
    public boolean hasLunarDate() {
        return hasLunarDate;
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
