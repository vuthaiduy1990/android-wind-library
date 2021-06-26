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
        String[] weekDays = new String[]{"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};
        float weekDayTextSize = 0f;
        int weekDayTextColor = 0;
        int weekDayPanelBackground = 0;
        int weekDayHoverBackground = 0;
        int dateCellSize = 0;
        float dateTextSize = 0f;
        float dateLunarTextSize = 0f;
        float dateEventSymbolSize = 0f;
        int dateTextColor = 0;
        int dateLunarTextColor = 0;
        int dateEventTextColor = 0;
        int dateWeekendTextColor = 0;
        int dateTodayTextColor = 0;
        int dateHighlightTextColor = 0;
        int dateCellBackground = 0;
        int dateEventBackground = 0;
        int dateWeekendBackground = 0;
        int dateTodayBackground = 0;
        int dateHighlightBackground = 0;
        int dateHoverBackground = 0;

        Resources res = getResources();
        try {
            // Week day style
            String weekDaysStr = typeArray.getString(R.styleable.WindCalendar_weekDays);
            if (weekDaysStr != null) {
                weekDays = weekDaysStr.split(",");
            }
            weekDayTextSize = typeArray.getDimension(R.styleable.WindCalendar_weekDayTextSize, res.getDimension(R.dimen.wl_text_small));
            weekDayTextColor = typeArray.getColor(R.styleable.WindCalendar_weekDayTextColor, ContextCompat.getColor(context, R.color.wl_calendar_week_day));
            weekDayPanelBackground = typeArray.getResourceId(R.styleable.WindCalendar_weekDayPanelBackground, 0);
            weekDayHoverBackground = typeArray.getResourceId(R.styleable.WindCalendar_weekDayHoverBackground, R.drawable.wl_calendar_hover_background);

            // Date cell and text size
            dateCellSize = (int) typeArray.getDimension(R.styleable.WindCalendar_dateCellSize, res.getDimension(R.dimen.wl_calendar_date_cell_size));
            dateTextSize = typeArray.getDimension(R.styleable.WindCalendar_dateTextSize, res.getDimension(R.dimen.wl_text_small));
            dateLunarTextSize = typeArray.getDimension(R.styleable.WindCalendar_dateLunarTextSize, res.getDimension(R.dimen.wl_calendar_lunar_date_text_size));
            dateEventSymbolSize = typeArray.getDimension(R.styleable.WindCalendar_dateEventSymbolSize, res.getDimension(R.dimen.wl_calendar_event_symbol_size));

            // Date text color
            dateTextColor = typeArray.getColor(R.styleable.WindCalendar_dateTextColor, ContextCompat.getColor(context, R.color.wl_black));
            dateLunarTextColor = typeArray.getColor(R.styleable.WindCalendar_dateLunarTextColor, ContextCompat.getColor(context, R.color.wl_calendar_lunar_date_text));
            dateEventTextColor = typeArray.getColor(R.styleable.WindCalendar_dateEventTextColor, ContextCompat.getColor(context, R.color.wl_black));
            dateWeekendTextColor = typeArray.getColor(R.styleable.WindCalendar_dateWeekendTextColor, ContextCompat.getColor(context, R.color.wl_danger));
            dateTodayTextColor = typeArray.getColor(R.styleable.WindCalendar_dateTodayTextColor, ContextCompat.getColor(context, R.color.wl_white));
            dateHighlightTextColor = typeArray.getColor(R.styleable.WindCalendar_dateHighlightTextColor, ContextCompat.getColor(context, R.color.wl_white));

            // Date cell background
            dateCellBackground = typeArray.getResourceId(R.styleable.WindCalendar_dateCellBackground, 0);
            dateEventBackground = typeArray.getResourceId(R.styleable.WindCalendar_dateEventBackground, 0);
            dateWeekendBackground = typeArray.getResourceId(R.styleable.WindCalendar_dateWeekendBackground, 0);
            dateTodayBackground = typeArray.getResourceId(R.styleable.WindCalendar_dateTodayBackground, R.drawable.wl_calendar_today_background);
            dateHighlightBackground = typeArray.getResourceId(R.styleable.WindCalendar_dateHighlightBackground, R.drawable.wl_calendar_highlight_background);
            dateHoverBackground = typeArray.getResourceId(R.styleable.WindCalendar_dateHoverBackground, R.drawable.wl_calendar_hover_background);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            typeArray.recycle();
        }

        adapter = new CalendarAdapter(fragManager, new GregorianCalendar());
        adapter.setCalendarStyle(
                CalendarStyle.config()
                        .weekDays(weekDays)
                        .weekDayTextSize(weekDayTextSize)
                        .weekDayTextColor(weekDayTextColor)
                        .weekDayPanelBackground(weekDayPanelBackground)
                        .weekDayHoverBackground(weekDayHoverBackground)
                        .dateCellSize(dateCellSize)
                        .dateTextSize(dateTextSize)
                        .dateLunarTextSize(dateLunarTextSize)
                        .dateEventSymbolSize(dateEventSymbolSize)
                        .dateTextColor(dateTextColor)
                        .dateLunarTextColor(dateLunarTextColor)
                        .dateEventTextColor(dateEventTextColor)
                        .dateWeekendTextColor(dateWeekendTextColor)
                        .dateTodayTextColor(dateTodayTextColor)
                        .dateHighlightTextColor(dateHighlightTextColor)
                        .dateCellBackground(dateCellBackground)
                        .dateEventBackground(dateEventBackground)
                        .dateWeekendBackground(dateWeekendBackground)
                        .dateTodayBackground(dateTodayBackground)
                        .dateHighlightBackground(dateHighlightBackground)
                        .dateHoverBackground(dateHoverBackground));
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
