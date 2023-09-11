package the.wind.library.calendar;

import android.icu.util.Calendar;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Calendar month view adapter which used for view pager
 */
public class CalendarAdapter extends FragmentStateAdapter {

    // default number of month preloaded on both side of selected month page
    public static final int DEFAULT_OFF_SCREEN = 2;

    // Maximum number of slide
    private static final int MAX_SLIDE = 1000;

    // Calendar type
    // solar calendar class, ex {@link android.icu.util.GregorianCalendar}
    private final Calendar calendar;

    // the number of month will be preloaded in the left side and right side of current month
    private final int offscreen;

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
    public CalendarAdapter(FragmentManager fm, Lifecycle lifecycle, Calendar calendar) {
        this(fm, lifecycle, calendar, DEFAULT_OFF_SCREEN);
    }

    /**
     * Constructor
     *
     * @param fm        month view fragment
     * @param calendar  abstract calendar with any given date
     * @param offscreen the number of month will be preloaded in the left side and right side of current month
     */
    public CalendarAdapter(FragmentManager fm, Lifecycle lifecycle, Calendar calendar, int offscreen) {
        super(fm, lifecycle);
        this.calendar = calendar;
        if (offscreen > 0) {
            this.offscreen = offscreen;
        } else {
            this.offscreen = DEFAULT_OFF_SCREEN;
        }
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public int getItemCount() {
        return MAX_SLIDE;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
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
     * Set select month
     *
     * @param date any date of month
     */
    protected void setSelectedDate(Date date) {
        int centerSlideShow = MAX_SLIDE / 2;
        /*
         * Testcase:
         * 1. slide month up/down -> to change current position
         * 2. Select another month from YearMonthSelectionDialog
         * Expected: the current position should be changed large number so that the viewpage can re-render month page view
         */
        currentPosition = (currentPosition >= centerSlideShow - offscreen && currentPosition <= centerSlideShow + offscreen) ? MAX_SLIDE / 4 : centerSlideShow;
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
        MonthInfo nextMonth;
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
        MonthInfo preMonth;
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
        notifyItemChanged(currentPosition);
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
