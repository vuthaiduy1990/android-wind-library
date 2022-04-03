package the.wind.library.calendar;

import android.icu.util.Calendar;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import the.wind.library.calendar.model.CalendarType;
import the.wind.library.calendar.model.WeekStartsOn;

final class CalendarInfo {

    private String tagCode;
    private final String todayId;
    public final Set<String> eventDates = new HashSet<>();
    public final Set<String> selectedDates = new HashSet<>();
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
     * Check if date is selected or not
     *
     * @param dateId date id
     * @return true if selected
     *
     * @see CalendarUtil#toId(int, int, int)
     */
    public boolean isSelected(@NonNull String dateId) {
        return selectedDates.contains(dateId);
    }

    /**
     * Get selected dates
     *
     * @return list of selected date IDs
     */
    public Collection<String> getSelectedDate() {
        return selectedDates;
    }

    /**
     * Clear highlight events without refreshing page
     */
    public void clearSelectedDates() {
        selectedDates.clear();
        Iterator<Map.Entry<String, MonthAdapter.ViewHolder>> itemIt = selectedDateViewMap.entrySet().iterator();
        while (itemIt.hasNext()) {
            MonthAdapter.ViewHolder vh = itemIt.next().getValue();
            vh.touchUp();
            itemIt.remove();
        }
    }

    /**
     * Select date item view
     *
     * @param viewHolder view holder
     */
    public void selectDate(MonthAdapter.ViewHolder viewHolder) {
        String dateId = viewHolder.getAdapterData().getId();
        boolean alreadySelected = selectedDateViewMap.containsKey(dateId);
        if (!alreadySelected) {
            selectedDates.add(dateId);
            selectedDateViewMap.put(dateId, viewHolder);
        }
        viewHolder.touchDown();
    }

    /**
     * Un-select date item view
     *
     * @param viewHolder view holder
     */
    public void unselectDate(MonthAdapter.ViewHolder viewHolder) {
        String dateId = viewHolder.getAdapterData().getId();
        selectedDates.remove(dateId);
        selectedDateViewMap.remove(dateId);
        viewHolder.touchUp();
    }

    /**
     * Select dates
     *
     * @param dateIds list of date ids
     * @see CalendarUtil#toId(int, int, int)
     */
    public void selectDates(Collection<String> dateIds) {
        selectedDates.addAll(dateIds);
    }

    /**
     * Select dates and refresh current page also
     *
     * @param dateIds list of date ids
     * @see CalendarUtil#toId(int, int, int)
     */
    public void selectDates(String... dateIds) {
        selectDates(Arrays.asList(dateIds));
    }

    /**
     * Reset info
     */
    public void reset() {
        eventDates.clear();
        selectedDates.clear();
        selectedDateViewMap.clear();
    }
}
