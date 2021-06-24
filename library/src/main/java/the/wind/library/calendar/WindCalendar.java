package the.wind.library.calendar;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import the.wind.library.CWBundle;
import the.wind.library.R;
import the.wind.library.anim.PageTransformerType;

public class WindCalendar extends LinearLayout {

    // Views
    private final ViewGroup _rootView;
    private final CalendarViewPager _calendarViewPager;

    // Adapter & data
    private final CalendarAdapter adapter;
    private final CalendarInfo info = new CalendarInfo(new GregorianCalendar());
    private final CalendarEvent eventListener = new CalendarEvent();

    // bundle data
    private final CWBundle bundle = new CWBundle();

    /**
     * Constructor
     *
     * @param context application context
     */
    public WindCalendar(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context application context
     * @param attrs   collection of attributes
     */
    public WindCalendar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context      application context
     * @param attrs        collection of attributes
     * @param defStyleAttr style attribute
     */
    public WindCalendar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Constructor
     *
     * @param context      application context
     * @param attrs        collection of attributes
     * @param defStyleAttr style attribute
     * @param defStyleRes  style resource
     */
    public WindCalendar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.wl_calendar, this);

        FragmentManager fragManager = null;
        if (context instanceof FragmentActivity) {
            fragManager = ((FragmentActivity) context).getSupportFragmentManager();
        } else {
            throw new ActivityNotFoundException("Context is not an fragment activity");
        }

        // Bind views
        _rootView = findViewById(R.id._rootView);
        _calendarViewPager = _rootView.findViewById(R.id._calendarViewPager);

        // Styling
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.WindCalendar, defStyleAttr, defStyleRes);
        int dateCellSize = 0;
        float dateTextSize = 0f;
        float lunarDateTextSize = 0f;
        float eventSymbolSize = 0f;
        int dateTextColor = 0;
        int lunarDateTextColor = 0;
        int eventTextColor = 0;
        int weekendTextColor = 0;
        int todayTextColor = 0;
        int highlightTextColor = 0;
        int cellBackground = 0;
        int eventBackground = 0;
        int weekendBackground = 0;
        int todayBackground = 0;
        int highlightBackground = 0;
        int touchBackground = 0;

        Resources res = getResources();
        try {
            // Date cell and text size
            dateCellSize = (int) typeArray.getDimension(R.styleable.WindCalendar_dateCellSize, res.getDimension(R.dimen.wl_calendar_date_cell_size));
            dateTextSize = typeArray.getDimension(R.styleable.WindCalendar_dateTextSize, res.getDimension(R.dimen.wl_text_small));
            lunarDateTextSize = typeArray.getDimension(R.styleable.WindCalendar_lunarDateTextSize, res.getDimension(R.dimen.wl_calendar_lunar_date_text_size));
            eventSymbolSize = typeArray.getDimension(R.styleable.WindCalendar_eventSymbolSize, res.getDimension(R.dimen.wl_calendar_event_symbol_size));

            // Date text color
            dateTextColor = typeArray.getColor(R.styleable.WindCalendar_dateTextColor, ContextCompat.getColor(context, R.color.wl_black));
            lunarDateTextColor = typeArray.getColor(R.styleable.WindCalendar_lunarDateTextColor, ContextCompat.getColor(context, R.color.wl_calendar_lunar_date_text));
            eventTextColor = typeArray.getColor(R.styleable.WindCalendar_eventTextColor, ContextCompat.getColor(context, R.color.wl_black));
            weekendTextColor = typeArray.getColor(R.styleable.WindCalendar_weekendTextColor, ContextCompat.getColor(context, R.color.wl_danger));
            todayTextColor = typeArray.getColor(R.styleable.WindCalendar_todayTextColor, ContextCompat.getColor(context, R.color.wl_white));
            highlightTextColor = typeArray.getColor(R.styleable.WindCalendar_highlightTextColor, ContextCompat.getColor(context, R.color.wl_white));

            // Date cell background
            cellBackground = typeArray.getResourceId(R.styleable.WindCalendar_cellBackground, 0);
            eventBackground = typeArray.getResourceId(R.styleable.WindCalendar_eventBackground, 0);
            weekendBackground = typeArray.getResourceId(R.styleable.WindCalendar_weekendBackground, 0);
            todayBackground = typeArray.getResourceId(R.styleable.WindCalendar_todayBackground, R.drawable.wl_calendar_today_background);
            highlightBackground = typeArray.getResourceId(R.styleable.WindCalendar_highlightBackground, R.drawable.wl_calendar_highlight_background);
            touchBackground = typeArray.getResourceId(R.styleable.WindCalendar_touchBackground, R.drawable.wl_calendar_date_touch_background);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            typeArray.recycle();
        }

        adapter = new CalendarAdapter(fragManager, new GregorianCalendar());
        adapter.setCalendarStyle(
                CalendarStyle.config()
                        .dateCellSize(dateCellSize)
                        .dateTextSize(dateTextSize)
                        .lunarDateTextSize(lunarDateTextSize)
                        .eventSymbolSize(eventSymbolSize)
                        .dateTextColor(dateTextColor)
                        .lunarDateTextColor(lunarDateTextColor)
                        .eventTextColor(eventTextColor)
                        .weekendTextColor(weekendTextColor)
                        .todayTextColor(todayTextColor)
                        .highlightTextColor(highlightTextColor)
                        .cellBackground(cellBackground)
                        .eventBackground(eventBackground)
                        .weekendBackground(weekendBackground)
                        .todayBackground(todayBackground)
                        .highlightBackground(highlightBackground)
                        .touchBackground(touchBackground));
        adapter.setCalendarInfo(info);
        adapter.setCalendarEvent(eventListener);
        _calendarViewPager.setAdapter(adapter);
        _calendarViewPager.setSelectedDate(new Date()); // must be set after setting adapter
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return bundle data
     */
    public CWBundle bundle() {
        return bundle;
    }

    /**
     * @return calendar view pager
     */
    public CalendarViewPager getCalendarViewPager() {
        return _calendarViewPager;
    }

    /**
     * Set page transformer
     *
     * @param type page transformer
     */
    public void setPageTransformer(PageTransformerType type) {
        _calendarViewPager.setPageTransformer(true, type.getTransformer());
    }

    /**
     * Add event date
     *
     * @param dateIds list of date id
     * @see CalendarUtil#toId(int, int, int)
     */
    public void addEventDate(String... dateIds) {
        addEventDate(Arrays.asList(dateIds));
    }

    /**
     * Add event date
     *
     * @param dateIds list of date id
     * @see CalendarUtil#toId(int, int, int)
     */
    public void addEventDate(Iterable<String> dateIds) {
        for (String id : dateIds) {
            info.eventDates.add(id);
        }
    }

    /**
     * Remove event date
     *
     * @param dateId date ID
     * @see CalendarUtil#toId(int, int, int)
     */
    public void removeEventDate(String dateId) {
        info.eventDates.remove(dateId);
    }

    /**
     * Clear event dates
     */
    public void clearEvents() {
        info.eventDates.clear();
    }


    /**
     * Set highlight dates
     *
     * @param dateIds list of date ids
     * @see CalendarUtil#toId(int, int, int)
     */
    public void highlightDates(Iterable<String> dateIds) {
        for (String id : dateIds) {
            info.highlightDates.add(id);
        }
        adapter.refreshCurrentPage();
    }

    /**
     * Set highlight dates
     *
     * @param dateIds list of date ids
     * @see CalendarUtil#toId(int, int, int)
     */
    public void highlightDates(String... dateIds) {
        if (dateIds.length == 0) {
            info.highlightDates.clear();
            adapter.refreshCurrentPage();
        } else {
            highlightDates(Arrays.asList(dateIds));
        }
    }

    /**
     * Set date item click listener
     *
     * @param dateItemClickListener date item click listener
     */
    public void setOnDateItemClickListener(OnDateItemClickListener dateItemClickListener) {
        eventListener.dateItemClickListener = dateItemClickListener;
    }

    /**
     * Set date item long click listener
     *
     * @param dateItemLongClickListener date item long click listener
     */
    public void setOnDateItemLongClickListener(OnDateItemLongClickListener dateItemLongClickListener) {
        eventListener.dateItemLongClickListener = dateItemLongClickListener;
    }

    /**
     * Set date item double click listener
     *
     * @param dateItemDoubleClickListener date item double click listener
     */
    public void setOnDateItemDoubleClickListener(OnDateItemDoubleClickListener dateItemDoubleClickListener) {
        eventListener.dateItemDoubleClickListener = dateItemDoubleClickListener;
    }

    /**
     * Set date item touch up listener
     *
     * @param dateItemTouchUpListener date item touch up listener
     */
    public void setOnDateItemTouchUpListener(OnDateItemTouchUpListener dateItemTouchUpListener) {
        eventListener.dateItemTouchUpListener = dateItemTouchUpListener;
    }

    /**
     * Set date item touch down listener
     *
     * @param dateItemTouchDownListener date item touch down listener
     */
    public void setOnDateItemTouchDownListener(OnDateItemTouchDownListener dateItemTouchDownListener) {
        eventListener.dateItemTouchDownListener = dateItemTouchDownListener;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * On date item click listener
     */
    public interface OnDateItemClickListener {
        /**
         * Trigger when user click on date cell item
         *
         * @param viewHolder view holder
         * @param view       date cell view
         * @param data       date info
         * @return true if consume the event.
         */
        boolean onClick(MonthAdapter.ViewHolder viewHolder, View view, DateInfo data);
    }

    /**
     * On date item long click listener
     */
    public interface OnDateItemLongClickListener {
        /**
         * Trigger when user long click on date cell item
         *
         * @param viewHolder view holder
         * @param view       date cell view
         * @param data       date info
         * @return true if consume the event.
         */
        boolean onLongClick(MonthAdapter.ViewHolder viewHolder, View view, DateInfo data);
    }

    /**
     * On date item double click listener
     */
    public interface OnDateItemDoubleClickListener {
        /**
         * Trigger when user double click on date cell item
         *
         * @param viewHolder view holder
         * @param view       date cell view
         * @param data       date info
         * @return true if consume the event.
         */
        boolean onDoubleClick(MonthAdapter.ViewHolder viewHolder, View view, DateInfo data);
    }

    /**
     * On date item touch up listener
     */
    public interface OnDateItemTouchUpListener {
        /**
         * Trigger when user touch up on date cell item
         *
         * @param viewHolder view holder
         * @param view       date cell view
         * @param data       date info
         * @return true if consume the event.
         */
        boolean onTouchUp(MonthAdapter.ViewHolder viewHolder, View view, DateInfo data);
    }

    /**
     * On date item touch down listener
     */
    public interface OnDateItemTouchDownListener {
        /**
         * Trigger when user touch up on date cell item
         *
         * @param viewHolder view holder
         * @param view       date cell view
         * @param data       date info
         * @return true if consume the event.
         */
        boolean onTouchDown(MonthAdapter.ViewHolder viewHolder, View view, DateInfo data);
    }

}
