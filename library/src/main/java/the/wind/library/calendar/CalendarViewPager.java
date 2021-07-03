package the.wind.library.calendar;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Custom view pager for calendar view
 */
public class CalendarViewPager extends ViewPager {

    private CalendarEvent calendarEvent;

    /**
     * Constructor
     *
     * @param context application context
     */
    public CalendarViewPager(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context application context
     * @param attrs   attributes
     */
    public CalendarViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (getAdapter() != null) {
                    CalendarAdapter adapter = (CalendarAdapter) getAdapter();
                    MonthInfo prePage = adapter.getSelectedMonth();
                    adapter.slide(position);
                    if (calendarEvent.monthPageChangeListener != null) {
                        calendarEvent.monthPageChangeListener.onChange(prePage, adapter.getSelectedMonth());
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        if (!(adapter instanceof CalendarAdapter)) {
            throw new IllegalArgumentException("adapter is not calendar");
        }
        super.setAdapter(adapter);
        CalendarAdapter _adapter = (CalendarAdapter) adapter;
        setOffscreenPageLimit(_adapter.getPreLoaded());
    }

    @Override
    public void setCurrentItem(int item) {
        if (item < 0 && getAdapter() != null) {
            super.setCurrentItem(((CalendarAdapter) getAdapter()).getCurrentPagePosition());
            return;
        }
        super.setCurrentItem(item);
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Set selected date.
     * Must set after setting adapter
     *
     * @param date selected date
     */
    void setSelectedDate(Date date) {
        CalendarAdapter adapter = (CalendarAdapter) getAdapter();
        if (adapter != null) {
            adapter.setSelectedDate(date);
        } else {
            throw new IllegalArgumentException("adapter does not exist");
        }
        setCurrentItem(-1); // reset current item to the middle of calendar slideshow
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
     * Refresh adapter
     *
     * @param adapter calendar adapter
     */
    void refreshAdapter(CalendarAdapter adapter) {
        adapter.resetPositionToZero();
        setAdapter(adapter);
        setSelectedDate(new Date());
    }

    /* ---------------------- INNER CLASS -------------------- */
}
