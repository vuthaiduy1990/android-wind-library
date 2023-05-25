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

    private Date date;
    // Standard date info
    private int[] solarInfo;

    // Lunar date info
    private boolean hasLunarDate = false;
    private int[] lunarInfo;

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
        solarInfo = CalendarUtil.getDateInfo(solarCal);
        this.id = CalendarUtil.toId(solarInfo[0], solarInfo[1], solarInfo[2]);

        // lunar calendar
        if (lunarCal != null) {
            this.hasLunarDate = true;
            lunarInfo = CalendarUtil.getDateInfo(lunarCal, solarCal);
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
     * Get solar date info
     *
     * @return [year, month, month day, leap, week day, hour, minute, second]
     */
    public int[] getSolarInfo() {
        return solarInfo;
    }

    /**
     * Get lunar date info
     *
     * @return [year, month, month day, leap, week day, hour, minute, second]
     */
    public int[] getLunarInfo() {
        return lunarInfo;
    }

    /**
     * @return year
     */
    public int getYear() {
        return solarInfo[0];
    }

    /**
     * @return month
     */
    public int getMonth() {
        return solarInfo[1];
    }

    /**
     * @return day of month
     */
    public int getDayOfMonth() {
        return solarInfo[2];
    }

    /**
     * @return day of week
     */
    public int getDayOfWeek() {
        return solarInfo[4];
    }

    /**
     * @return true if date is in solar leap month
     */
    public boolean isSolarLeapMonth() {
        return solarInfo[3] == 1;
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
        return lunarInfo[0];
    }

    /**
     * @return lunar month
     */
    public int getLunarMonth() {
        return lunarInfo[1];
    }

    /**
     * @return lunar day of month
     */
    public int getLunarDayOfMonth() {
        return lunarInfo[2];
    }

    /**
     * @return true if date is in lunar leap month
     */
    public boolean isLunarLeapMonth() {
        return lunarInfo[3] == 1;
    }

    /**
     * @return true if the date is weekend (Saturday and Sunday)
     */
    public boolean isWeekend() {
        return solarInfo[4] == Calendar.SATURDAY || solarInfo[4] == Calendar.SUNDAY;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */
}
