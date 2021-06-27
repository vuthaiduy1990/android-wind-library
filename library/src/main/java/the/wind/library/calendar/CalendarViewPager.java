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
                    MonthViewFragment prePage = adapter.getCurrentPage();
                    adapter.slide(position);
                    if (calendarEvent.monthPageChangeListener != null) {
                        calendarEvent.monthPageChangeListener.onChange(prePage, adapter.getCurrentPage());
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
            super.setCurrentItem(((CalendarAdapter) getAdapter()).getCenterSlidePosition());
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
    public void setSelectedDate(Date date) {
        setCurrentItem(-1); // reset current item to the middle of calendar slideshow
        CalendarAdapter adapter = (CalendarAdapter) getAdapter();
        if (adapter != null) {
            adapter.setSelectedDate(date);
        } else {
            throw new IllegalArgumentException("adapter does not exist");
        }
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

    /* ---------------------- INNER CLASS -------------------- */
}
