package the.wind.library.calendar;

import android.icu.util.Calendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final class CalendarInfo {

    private String tagCode;
    private final String todayId;
    public final Set<String> eventDates = new HashSet<>();
    public final Set<String> highlightDates = new HashSet<>();
    public final Map<String, MonthAdapter.ViewHolder> selectedDateViewMap = new HashMap<>();

    @Nullable
    public CalendarType lunarType = null;
    private WeekStartsOn weekStartsOn = WeekStartsOn.SUNDAY;

    /**
     * Constructor
     */
    public CalendarInfo(Calendar today) {
        this.todayId = CalendarUtil.toId(today);
    }

    /**
     * Get tag code which is used to identified month view fragments for each calendar instance.
     * All month view fragments of each calendar instance will have the same tag code configured in {@link WindCalendar}
     *
     * @return tag code
     */
    @Nullable
    public String getTagCode() {
        return tagCode;
    }

    /**
     * Set tag code
     *
     * @param tagCode tag code
     */
    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
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
     * @return lunar calendar type
     */
    @Nullable
    public CalendarType getLunarType() {
        return lunarType;
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

    /**
     * @return day that week starts on
     */
    @NonNull
    public WeekStartsOn getWeekStartsOn() {
        return weekStartsOn;
    }

    /**
     * Set day when week starts on
     *
     * @param day day of week
     */
    public void setWeekStartsOn(@NonNull WeekStartsOn day) {
        weekStartsOn = day;
    }

    /**
     * Get selected dates
     *
     * @return list of selected dates
     */
    public List<DateInfo> getSelectedDate() {
        List<DateInfo> list = new ArrayList<>();
        for (Map.Entry<String, MonthAdapter.ViewHolder> entry : selectedDateViewMap.entrySet()) {
            list.add(entry.getValue().getAdapterData());
        }
        return list;
    }

    /**
     * Clear highlight events without refreshing page
     */
    public void clearSelectedDates() {
        highlightDates.clear();
        Iterator<Map.Entry<String, MonthAdapter.ViewHolder>> itemIt = selectedDateViewMap.entrySet().iterator();
        while (itemIt.hasNext()) {
            itemIt.next().getValue().touchUp();
            itemIt.remove();
        }
        selectedDateViewMap.clear();
    }

    /**
     * Select date item view
     *
     * @param viewHolder view holder
     * @param data       date info
     * @return true if item view is already selected before
     */
    public boolean selectDateItemView(MonthAdapter.ViewHolder viewHolder, DateInfo data) {
        boolean alreadySelected = selectedDateViewMap.containsKey(data.getId());
        selectedDateViewMap.put(data.getId(), viewHolder);
        viewHolder.touchDown();
        return alreadySelected;
    }

    /**
     * Reset info
     */
    public void reset() {
        eventDates.clear();
        highlightDates.clear();
        selectedDateViewMap.clear();
    }
}
