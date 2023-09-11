package the.wind.library.calendar;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Custom view pager for calendar view
 */
public class CalendarViewPager {

    private final ViewPager2 _viewPager;
    private CalendarEvent calendarEvent;
    private MonthInfo preMonth;

    /**
     * Constructor
     *
     * @param viewPager view pager
     */
    public CalendarViewPager(ViewPager2 viewPager) {
        this._viewPager = viewPager;
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                CalendarAdapter adapter = getAdapter();
                if (adapter != null) {
                    // adapter.slide(position);
                    if (calendarEvent.monthPageChangeListener != null) {
                        calendarEvent.monthPageChangeListener.onChange(preMonth, adapter.getSelectedMonth());
                    }
                    preMonth = adapter.getSelectedMonth();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /**
     * Set adapter
     *
     * @param adapter adapter
     */
    public void setAdapter(@Nullable CalendarAdapter adapter) {
        if (adapter == null) {
            _viewPager.setAdapter(null);
            return;
        }
        _viewPager.setAdapter(adapter);
        _viewPager.setOffscreenPageLimit(adapter.getOffscreen());
    }

    /**
     * Set current page
     *
     * @param item item position
     */
    public void setCurrentItem(int item) {
        CalendarAdapter adapter = getAdapter();
        if (item < 0 && adapter != null) {
            _viewPager.setCurrentItem(adapter.getCurrentPagePosition(), true);
            return;
        }
        _viewPager.setCurrentItem(item, true);
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Get view pager
     *
     * @return view pager
     */
    public ViewPager2 getViewPager() {
        return _viewPager;
    }

    /**
     * Get adapter
     *
     * @return adapter
     */
    public CalendarAdapter getAdapter() {
        return (CalendarAdapter) _viewPager.getAdapter();
    }

    /**
     * Set calendar event listener
     *
     * @param calendarEvent calendar event listener
     */
    void setCalendarEvent(CalendarEvent calendarEvent) {
        this.calendarEvent = calendarEvent;
    }

    /**
     * Enable/disable swiping
     *
     * @param enabled true/false
     */
    public void setSwipingEnabled(boolean enabled) {
        _viewPager.setUserInputEnabled(enabled);
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Scroll calendar view to selected date
     * Must set after setting adapter
     *
     * @param date selected date
     */
    void scrollToDate(Date date) {
        CalendarAdapter adapter = (CalendarAdapter) getAdapter();
        if (adapter != null) {
            adapter.setSelectedDate(date);
        } else {
            throw new IllegalArgumentException("adapter does not exist");
        }
        setCurrentItem(-1); // reset current item to the middle of calendar slideshow
    }

    /**
     * Refresh adapter to selected date
     *
     * @param adapter calendar adapter
     * @param date    selected date
     */
    void refreshAdapter(CalendarAdapter adapter, Date date) {
        // reset old adapter -> will call destroyItem in calendar adapter
        setAdapter(null);

        // set new adapter that lead viewpager back to start page
        // Therefore, we should reset adapter position to zero too
        adapter.reset();
        _viewPager.setOffscreenPageLimit(1); // set 1 to avoid creating too much cache fragment for empty adapter
        setAdapter(adapter); // that will reset offscreen limit to configured value

        // set selected date
        scrollToDate(date);
    }

    /* ---------------------- INNER CLASS -------------------- */
}
