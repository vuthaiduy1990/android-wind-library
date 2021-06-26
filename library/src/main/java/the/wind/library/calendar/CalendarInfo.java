package the.wind.library.calendar;

import android.icu.util.Calendar;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final class CalendarInfo {

    private final String todayId;
    public final Set<String> eventDates = new HashSet<>();
    public final Set<String> highlightDates = new HashSet<>();
    @Nullable
    public CalendarType lunarType = null;

    /**
     * Constructor
     */
    public CalendarInfo(Calendar today) {
        this.todayId = CalendarUtil.toId(today);
    }

    /**
     * Check if given date is today or not
     *
     * @param dateId date ID
     * @return true if given date is today, else return false
     */
    public boolean isToday(@NonNull String dateId) {
        return this.todayId.equals(dateId);
    }

    /**
     * Check if given date has event or not
     *
     * @param dateId date ID
     * @return true if given date has event, else return false
     *
     * @see CalendarUtil#toId(int, int, int)
     */
    public boolean hasEvent(@NonNull String dateId) {
        return eventDates.contains(dateId);
    }

    /**
     * Check if date is highlight or not
     *
     * @param dateId date id
     * @return true if highlight
     *
     * @see CalendarUtil#toId(int, int, int)
     */
    public boolean isHighlight(@NonNull String dateId) {
        return highlightDates.contains(dateId);
    }

    /**
     * Set lunar calendar type
     *
     * @param lunarType lunar calendar type
     */
    public void setLunarType(@Nullable CalendarType lunarType) {
        this.lunarType = lunarType;
    }

    /**
     * Get lunar calendar typeS
     *
     * @return lunar calendar type
     */
    @Nullable
    public Calendar getLunarCalendar() {
        return lunarType != null ? lunarType.getValue() : null;
    }
}
