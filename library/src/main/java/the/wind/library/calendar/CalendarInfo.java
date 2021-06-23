package the.wind.library.calendar;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;

class CalendarInfo {

    private final String todayId;
    private final Set<String> eventDates = new HashSet<>();

    /**
     * Constructor
     */
    public CalendarInfo(Calendar today) {
        this.todayId = CalendarUtil.toId(today);
    }

    /**
     * @return today date ID
     */
    public String dateId() {
        return todayId;
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
     */
    public boolean hasEvent(@NonNull String dateId) {
        return eventDates.contains(dateId);
    }

    /**
     * Add event date
     *
     * @param dateId date ID
     * @see CalendarUtil#toId(int, int, int)
     */
    public void addEventDate(@NonNull String dateId) {
        eventDates.add(dateId);
    }

    /**
     * Remove event date
     *
     * @param dateId date ID
     * @see CalendarUtil#toId(int, int, int)
     */
    public void removeEventDate(String dateId) {
        eventDates.remove(dateId);
    }

    /**
     * Clear event dates
     */
    public void clearEvents() {
        eventDates.clear();
    }

}
