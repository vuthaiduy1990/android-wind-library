package the.wind.library.calendar;

import android.icu.util.Calendar;
import android.view.ViewGroup;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Calendar month view adapter which used for view pager
 */
public class CalendarAdapter extends FragmentStatePagerAdapter {

    // default number of month preloaded on both side of selected month page
    public static final int DEFAULT_OFF_SCREEN = 2;

    // Maximum number of slide
    private static final int MAX_SLIDE = 1000;

    // Calendar type
    // solar calendar class, ex {@link android.icu.util.GregorianCalendar}
    private final Calendar calendar;

    // the number of month will be preloaded in the left side and right side of current month
    private final int offscreen;
    private final Map<Integer, MonthViewFragment> fragmentCache = new HashMap<>();

    // selected month
    private MonthInfo selectedMonth;
    private int currentPosition;

    // styling
    private CalendarStyle calendarStyle;
    private CalendarInfo calendarInfo;
    private CalendarEvent calendarEvent;

    /**
     * Constructor
     *
     * @param fm       month view fragment
     * @param calendar abstract calendar with any given date
     */
    public CalendarAdapter(FragmentManager fm, Calendar calendar) {
        this(fm, calendar, DEFAULT_OFF_SCREEN);
    }

    /**
     * Constructor
     *
     * @param fm        month view fragment
     * @param calendar  abstract calendar with any given date
     * @param offscreen the number of month will be preloaded in the left side and right side of current month
     */
    public CalendarAdapter(FragmentManager fm, Calendar calendar, int offscreen) {
        super(fm);
        this.calendar = calendar;
        if (offscreen > 0) {
            this.offscreen = offscreen;
        } else {
            this.offscreen = DEFAULT_OFF_SCREEN;
        }
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public Fragment getItem(int position) {
        if (selectedMonth == null) return new MonthViewFragment(); // empty fragment
        int diff = position - currentPosition;
        if (diff == 0) return newMonthFragment(selectedMonth);

        int sign = diff / Math.abs(diff);
        MonthInfo target = selectedMonth;
        int idx = 0;
        while (sign > 0 ? idx < diff : idx > diff) {
            if (target == null) return new MonthViewFragment(); // empty fragment
            if (sign > 0) {
                target = target.next();
                idx++;
            } else {
                target = target.previous();
                idx--;
            }
        }
        return newMonthFragment(target);
    }

    @Override
    public int getCount() {
        return MAX_SLIDE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        MonthViewFragment frag = (MonthViewFragment) super.instantiateItem(container, position);
        fragmentCache.put(position, frag);
        return frag;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
        fragmentCache.remove(position);
    }

    @Override
    public void notifyDataSetChanged() {
        for (Map.Entry<Integer, MonthViewFragment> entry : fragmentCache.entrySet()) {
            MonthViewFragment frag = entry.getValue();
            frag.notifyDataSetChanged();
        }
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Create new month view fragment
     *
     * @param monthInfo month info
     * @return fragment
     */
    private MonthViewFragment newMonthFragment(@Nullable MonthInfo monthInfo) {
        return new MonthViewFragment(monthInfo, calendarInfo, calendarStyle, calendarEvent);
    }

    /**
     * Get offscreen fragment
     *
     * @return map between position and fragment
     */
    public Map<Integer, MonthViewFragment> getFragmentCache() {
        return fragmentCache;
    }

    /**
     * Set select month
     *
     * @param date any date of month
     */
    protected void setSelectedDate(Date date) {
        int centerSlideShow = MAX_SLIDE / 2;
        currentPosition = currentPosition == centerSlideShow ? MAX_SLIDE / 4 : centerSlideShow;
        selectedMonth = CalendarUtil.createMonthLink(calendar, calendarInfo.getLunarCalendar(), date, calendarInfo.getWeekStartsOn(), offscreen);
    }

    /**
     * Get number of preloaded month
     *
     * @return the number of month will be preloaded in the left side and right side of current month
     */
    public int getOffscreen() {
        return offscreen;
    }

    /**
     * Get current view page
     *
     * @return current view page
     */
    public MonthViewFragment getCurrentPage() {
        return fragmentCache.get(currentPosition);
    }

    /**
     * Get current page position
     *
     * @return position
     */
    public int getCurrentPagePosition() {
        return currentPosition;
    }

    /**
     * Get current month
     *
     * @return current month
     */
    public MonthInfo getSelectedMonth() {
        return selectedMonth;
    }

    /**
     * Set date cell style
     *
     * @param style date cell style
     */
    void setCalendarStyle(CalendarStyle style) {
        this.calendarStyle = style;
    }

    /**
     * Calendar info
     *
     * @param info calendar info
     */
    void setCalendarInfo(CalendarInfo info) {
        this.calendarInfo = info;
    }

    /**
     * Set calendar event listener
     *
     * @param calendarEvent calendar event listener
     */
    void setCalendarEvent(CalendarEvent calendarEvent) {
        this.calendarEvent = calendarEvent;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Slide calendar month view up/down to given position
     *
     * @param position given position
     * @return position after slide to
     */
    protected int slide(int position) {
        if (selectedMonth == null || currentPosition == position) return currentPosition;
        boolean up = position > currentPosition;
        if (up) {
            currentPosition++;
            selectedMonth = selectedMonth.next();
        } else {
            currentPosition--;
            selectedMonth = selectedMonth.previous();
        }

        /*
         * Iterate over next month
         */
        MonthInfo monthIt = selectedMonth;
        MonthInfo nextMonth = null;
        while ((nextMonth = monthIt.next()) != null) {
            monthIt = nextMonth;
        }
        if (up) {
            // add one more month to the last
            calendar.set(monthIt.getYear(), monthIt.getMonth(), 1);
            calendar.add(Calendar.MONTH, 1);
            nextMonth = new MonthInfo(calendar, calendarInfo.getLunarCalendar(), calendarInfo.getWeekStartsOn());
            nextMonth.previous(monthIt);
            monthIt.next(nextMonth);
        } else {
            // remove the last one
            monthIt.next(null);
        }

        /*
         * Iterate over previous month
         */
        monthIt = selectedMonth;
        MonthInfo preMonth = null;
        while ((preMonth = monthIt.previous()) != null) {
            monthIt = preMonth;
        }
        if (up) {
            // remove the beginning one
            monthIt.previous(null);
        } else {
            // add one more month to the beginning
            calendar.set(monthIt.getYear(), monthIt.getMonth(), 1);
            calendar.add(Calendar.MONTH, -1);
            preMonth = new MonthInfo(calendar, calendarInfo.getLunarCalendar(), calendarInfo.getWeekStartsOn());
            preMonth.next(monthIt);
            monthIt.previous(preMonth);
        }
        return currentPosition;
    }

    /**
     * Notify dataset changes
     */
    void refreshCurrentPage() {
        MonthViewFragment frag = fragmentCache.get(currentPosition);
        if (frag != null) {
            frag.notifyDataSetChanged();
        }
    }

    /**
     * Reset adapter
     */
    void reset() {
        selectedMonth = null;
        currentPosition = 0;
    }

    /* ---------------------- INNER CLASS -------------------- */
}
