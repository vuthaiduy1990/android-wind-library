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
    private MonthInfo preMonth;

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
                    adapter.slide(position);
                    if (calendarEvent.monthPageChangeListener != null) {
                        calendarEvent.monthPageChangeListener.onChange(preMonth, adapter.getSelectedMonth());
                    }
                    preMonth = adapter.getSelectedMonth();
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
        if (adapter == null) {
            super.setAdapter(null);
            return;
        }
        if (!(adapter instanceof CalendarAdapter)) {
            throw new IllegalArgumentException("adapter is not calendar");
        }
        super.setAdapter(adapter);
        CalendarAdapter _adapter = (CalendarAdapter) adapter;
        setOffscreenPageLimit(_adapter.getPreLoaded());
    }

    @Override
    public void setCurrentItem(int item) {
        CalendarAdapter adapter = (CalendarAdapter) getAdapter();
        if (item < 0 && adapter != null) {
            super.setCurrentItem(adapter.getCurrentPagePosition());
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
        // reset old adapter -> will call destroyItem in calendar adapter
        setAdapter(null);

        // set new adapter that lead viewpager back to start page
        // Therefore, we should reset adapter position to zero too
        adapter.resetPositionToZero();
        setOffscreenPageLimit(1); // set 1 to avoid creating too much cache fragment
        setAdapter(adapter);
    }

    /* ---------------------- INNER CLASS -------------------- */
}
