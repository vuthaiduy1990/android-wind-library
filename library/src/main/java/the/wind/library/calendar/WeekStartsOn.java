package the.wind.library.calendar;

import android.icu.util.Calendar;

public enum WeekStartsOn {
    MONDAY(Calendar.MONDAY),
    SATURDAY(Calendar.SATURDAY),
    SUNDAY(Calendar.SUNDAY);

    // day of week
    private final int day;

    /**
     * Constructor
     *
     * @param day day
     */
    WeekStartsOn(int day) {
        this.day = day;
    }


    /**
     * Get week start option by day
     *
     * @param day day of week
     * @return type
     */
    public static WeekStartsOn typeOf(int day) {
        for (WeekStartsOn type : WeekStartsOn.values()) {
            if (type.getDay() == day) {
                return type;
            }
        }
        throw new NullPointerException("WeekStartsOn type does not exist");
    }

    /**
     * @return day of week
     */
    public int getDay() {
        return day;
    }
}
