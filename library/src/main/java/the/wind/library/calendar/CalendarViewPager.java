package the.wind.library.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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
    private boolean swipeEnabled = true;

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
        setOffscreenPageLimit(_adapter.getOffscreen());
    }

    @Override
    public void setCurrentItem(int item) {
        CalendarAdapter adapter = (CalendarAdapter) getAdapter();
        if (item < 0 && adapter != null) {
            super.setCurrentItem(adapter.getCurrentPagePosition(), true);
            return;
        }
        super.setCurrentItem(item, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            if (this.swipeEnabled) {
                return super.onTouchEvent(ev);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            if (this.swipeEnabled) {
                return super.onInterceptTouchEvent(ev);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // https://mobikul.com/viewpager/
        try {
            int mode = MeasureSpec.getMode(heightMeasureSpec);
            // Unspecified means that the ViewPager is in a ScrollView WRAP_CONTENT.
            // At Most means that the ViewPager is not in a ScrollView WRAP_CONTENT.
            if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
                // super has to be called in the beginning so the child views can be initialized.
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                int height = 0;
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    int childMeasuredHeight = child.getMeasuredHeight();
                    if (childMeasuredHeight > height) {
                        height = childMeasuredHeight;
                    }
                }
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // super has to be called again so the new specs are treated as exact measurements
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

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
        this.swipeEnabled = enabled;
    }

    /* ---------------------- METHOD ------------------------- */

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
        setOffscreenPageLimit(1); // set 1 to avoid creating too much cache fragment for empty adapter
        setAdapter(adapter); // that will reset offscreen limit to configured value

        // set selected date
        scrollToDate(date);
    }

    /* ---------------------- INNER CLASS -------------------- */
}
