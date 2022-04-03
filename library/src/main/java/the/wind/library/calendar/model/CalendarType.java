package the.wind.library.calendar.model;

import android.icu.util.BuddhistCalendar;
import android.icu.util.Calendar;
import android.icu.util.ChineseCalendar;
import android.icu.util.CopticCalendar;
import android.icu.util.HebrewCalendar;
import android.icu.util.IndianCalendar;
import android.icu.util.IslamicCalendar;
import android.icu.util.JapaneseCalendar;
import android.icu.util.TaiwanCalendar;

import the.wind.library.calendar.VietnameseCalendar;

/**
 * Calendar type
 */
public enum CalendarType {

    Buddhist(0, new BuddhistCalendar()),
    Chinese(1, new ChineseCalendar()),
    Coptic(2, new CopticCalendar()),
    Hebrew(3, new HebrewCalendar()),
    Indian(4, new IndianCalendar()),
    Islamic(5, new IslamicCalendar()),
    Japanese(6, new JapaneseCalendar()),
    Taiwan(7, new TaiwanCalendar()),
    Vietnamese(8, new VietnameseCalendar());

    private final int idx;
    private final Calendar value;

    /**
     * Constructor
     *
     * @param idx   index
     * @param value calendar type value
     */
    CalendarType(int idx, Calendar value) {
        this.idx = idx;
        this.value = value;
    }

    /**
     * Get calendar type by index
     *
     * @param idx index
     * @return calendar type
     */
    public static CalendarType typeOf(int idx) {
        for (CalendarType type : CalendarType.values()) {
            if (type.idx == idx) {
                return type;
            }
        }
        throw new NullPointerException("calendar type does not exist");
    }

    /**
     * Get calendar value
     *
     * @return calendar instance
     */
    public Calendar getValue() {
        return (Calendar) this.value.clone();
    }
}
