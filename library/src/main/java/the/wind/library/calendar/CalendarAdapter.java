package the.wind.library.calendar;

import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Calendar month view adapter which used for view pager
 */
public class CalendarAdapter extends FragmentStatePagerAdapter {

    // default number of month preloaded
    private static final int DEFAULT_PRE_LOAD = 2;

    // Maximum number of slide
    private static final int MAX_SLIDE = 2000;
    // Calendar type
    // calendar abstract class, ex {@link java.util.GregorianCalendar}, {@link android.icu.util.ChineseCalendar}
    private final Calendar calendar;
    // the number of month will be preloaded in the left side and right side of current month
    private int preLoaded;
    // selected month
    private MonthInfo selectedMonth;
    private int currentPosition;

    /**
     * Constructor
     *
     * @param fm month view fragment
     */
    public CalendarAdapter(FragmentManager fm) {
        this(fm, DEFAULT_PRE_LOAD);
    }

    /**
     * Constructor
     *
     * @param fm        month view fragment
     * @param preLoaded the number of month will be preloaded in the left side and right side of current month
     */
    public CalendarAdapter(FragmentManager fm, int preLoaded) {
        this(fm, new GregorianCalendar(), preLoaded);
    }

    /**
     * Constructor
     *
     * @param fm        month view fragment
     * @param calendar  abstract calendar with any given date
     * @param preLoaded the number of month will be preloaded in the left side and right side of current month
     */
    public CalendarAdapter(FragmentManager fm, Calendar calendar, int preLoaded) {
        super(fm);
        this.calendar = calendar;
        if (preLoaded > 0) {
            this.preLoaded = preLoaded;
        }
        currentPosition = getCenterSlidePosition();
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public Fragment getItem(int position) {
        int diff = position - currentPosition;
        if (diff == 0) return new MonthViewFragment(selectedMonth);

        int sign = diff / Math.abs(diff);
        MonthInfo target = selectedMonth;
        int idx = 0;
        while (sign > 0 ? idx < diff : idx > diff) {
            if (sign > 0) {
                target = target.next();
                idx++;
            } else {
                target = target.previous();
                idx--;
            }
        }
        return new MonthViewFragment(target);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return MAX_SLIDE;
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Set select month
     *
     * @param date any date of month
     */
    protected void setSelectedDate(Date date) {
        selectedMonth = CalendarUtil.createMonthLink(calendar, date, preLoaded);
    }

    /**
     * Get number of preloaded month
     *
     * @return the number of month will be preloaded in the left side and right side of current month
     */
    public int getPreLoaded() {
        return preLoaded;
    }

    /**
     * @return position of the center slide
     */
    public int getCenterSlidePosition() {
        return MAX_SLIDE / 2;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Slide calendar month view up/down to given position
     *
     * @param position given position
     */
    protected void slide(int position) {
        if (selectedMonth == null) return;
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
            nextMonth = new MonthInfo(calendar);
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
            preMonth = new MonthInfo(calendar);
            preMonth.next(monthIt);
            monthIt.previous(preMonth);
        }
    }

    /* ---------------------- INNER CLASS -------------------- */
}
