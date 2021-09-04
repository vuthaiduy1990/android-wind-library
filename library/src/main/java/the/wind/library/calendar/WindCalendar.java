package the.wind.library.calendar;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import the.wind.library.CWBundle;
import the.wind.library.R;
import the.wind.library.anim.PageTransformerType;

public class WindCalendar extends LinearLayout {

    // List if week days
    private static final Map<Integer, Integer> WEEK_DAY_MAP = new HashMap<>();

    static {
        WEEK_DAY_MAP.put(Calendar.MONDAY, 0);
        WEEK_DAY_MAP.put(Calendar.TUESDAY, 1);
        WEEK_DAY_MAP.put(Calendar.WEDNESDAY, 2);
        WEEK_DAY_MAP.put(Calendar.THURSDAY, 3);
        WEEK_DAY_MAP.put(Calendar.FRIDAY, 4);
        WEEK_DAY_MAP.put(Calendar.SATURDAY, 5);
        WEEK_DAY_MAP.put(Calendar.SUNDAY, 6);
    }

    // Views
    private final LayoutInflater inflater;
    private final FragmentManager fragManager;
    private final ViewGroup _rootView;
    private final CalendarViewPager _calendarViewPager;
    private final ViewGroup _weekDayPanelView;
    private View _weekDayTouchedView;

    //Adapter
    private CalendarAdapter adapter;
    private int offscreen;

    // bundle data
    private final CWBundle bundle = new CWBundle();

    // model
    private final Style style = new Style();
    private final CalendarInfo info = new CalendarInfo(new GregorianCalendar());
    private final CalendarEvent eventListener = new CalendarEvent();

    // Listener
    private final OnTouchListener weekDayItemTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.performClick();
            _weekDayTouchedView = v;
            int dayValue = (Integer) v.getTag();

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    if (eventListener.weekDayItemTouchDownListener != null) {
                        if (eventListener.weekDayItemTouchDownListener.onTouchDown(v, dayValue)) {
                            break;
                        }
                    }
                    v.setBackgroundResource(style.weekDayHoverBackground());
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (eventListener.weekDayItemTouchUpListener != null) {
                        if (eventListener.weekDayItemTouchUpListener.onTouchUp(v, dayValue)) {
                            break;
                        }
                    }
                    v.setBackground(null);
                    break;
            }
            weekDateItemGestureDetector.onTouchEvent(event);
            return true;
        }
    };

    // Gesture detector
    private final GestureDetector weekDateItemGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
            if (eventListener.weekDayItemLongClickListener != null) {
                eventListener.weekDayItemLongClickListener.onLongClick(_weekDayTouchedView, (Integer) _weekDayTouchedView.getTag());
            }
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (eventListener.weekDayItemDoubleClickListener != null) {
                eventListener.weekDayItemDoubleClickListener.onDoubleClick(_weekDayTouchedView, (Integer) _weekDayTouchedView.getTag());
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (eventListener.weekDayItemClickListener != null) {
                eventListener.weekDayItemClickListener.onClick(_weekDayTouchedView, (Integer) _weekDayTouchedView.getTag());
            }
            return true;
        }
    });

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

        inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.wl_calendar, this);

        // Get fragment manager
        if (context instanceof FragmentActivity) {
            fragManager = ((FragmentActivity) context).getSupportFragmentManager();
        } else if (context instanceof ContextThemeWrapper && ((ContextThemeWrapper) context).getBaseContext() instanceof FragmentActivity) {
            fragManager = ((FragmentActivity) ((ContextThemeWrapper) context).getBaseContext()).getSupportFragmentManager();
        } else {
            throw new ActivityNotFoundException("Context is not an fragment activity");
        }

        // Bind views
        _rootView = findViewById(R.id._rootView);
        _calendarViewPager = _rootView.findViewById(R.id._calendarViewPager);
        _weekDayPanelView = _rootView.findViewById(R.id._weekDayPanelView);

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
        int dateHighlightLunarTextColor = 0;
        int monthPanelViewBackground = 0;
        int dateCellBackground = 0;
        int dateEventBackground = 0;
        int dateWeekendBackground = 0;
        int dateTodayBackground = 0;
        int dateHighlightBackground = 0;

        boolean autoBuild = true;

        Resources res = getResources();
        try {
            // Week day style
            String weekDaysStr = typeArray.getString(R.styleable.WindCalendar_weekDays);
            if (weekDaysStr != null) {
                weekDays = weekDaysStr.split(",");
            }
            weekDayTextSize = typeArray.getDimension(R.styleable.WindCalendar_weekDayTextSize, res.getDimension(R.dimen.wl_calendar_date_text_size));
            weekDayTextColor = typeArray.getColor(R.styleable.WindCalendar_weekDayTextColor, ContextCompat.getColor(context, R.color.wl_calendar_week_day));
            weekDayPanelBackground = typeArray.getResourceId(R.styleable.WindCalendar_weekDayPanelBackground, 0);
            weekDayHoverBackground = typeArray.getResourceId(R.styleable.WindCalendar_weekDayHoverBackground, R.drawable.wl_calendar_highlight_background);

            // Date cell and text size
            dateCellSize = (int) typeArray.getDimension(R.styleable.WindCalendar_dateCellSize, res.getDimension(R.dimen.wl_calendar_date_cell_size));
            dateTextSize = typeArray.getDimension(R.styleable.WindCalendar_dateTextSize, res.getDimension(R.dimen.wl_calendar_date_text_size));
            dateLunarTextSize = typeArray.getDimension(R.styleable.WindCalendar_dateLunarTextSize, res.getDimension(R.dimen.wl_calendar_lunar_date_text_size));
            dateEventSymbolSize = typeArray.getDimension(R.styleable.WindCalendar_dateEventSymbolSize, res.getDimension(R.dimen.wl_calendar_event_symbol_size));

            // Date text color
            dateTextColor = typeArray.getColor(R.styleable.WindCalendar_dateTextColor, ContextCompat.getColor(context, R.color.wl_black));
            dateLunarTextColor = typeArray.getColor(R.styleable.WindCalendar_dateLunarTextColor, ContextCompat.getColor(context, R.color.wl_calendar_lunar_date_text));
            dateEventTextColor = typeArray.getColor(R.styleable.WindCalendar_dateEventTextColor, ContextCompat.getColor(context, R.color.wl_black));
            dateWeekendTextColor = typeArray.getColor(R.styleable.WindCalendar_dateWeekendTextColor, ContextCompat.getColor(context, R.color.wl_danger));
            dateTodayTextColor = typeArray.getColor(R.styleable.WindCalendar_dateTodayTextColor, ContextCompat.getColor(context, R.color.wl_white));
            dateHighlightTextColor = typeArray.getColor(R.styleable.WindCalendar_dateHighlightTextColor, ContextCompat.getColor(context, R.color.wl_black));
            dateHighlightLunarTextColor = typeArray.getColor(R.styleable.WindCalendar_dateHighlightLunarTextColor, ContextCompat.getColor(context, R.color.wl_calendar_lunar_date_text));

            // Date cell background
            monthPanelViewBackground = typeArray.getResourceId(R.styleable.WindCalendar_monthPanelViewBackground, 0);
            dateCellBackground = typeArray.getResourceId(R.styleable.WindCalendar_dateCellBackground, 0);
            dateEventBackground = typeArray.getResourceId(R.styleable.WindCalendar_dateEventBackground, 0);
            dateWeekendBackground = typeArray.getResourceId(R.styleable.WindCalendar_dateWeekendBackground, 0);
            dateTodayBackground = typeArray.getResourceId(R.styleable.WindCalendar_dateTodayBackground, R.drawable.wl_calendar_today_background);
            dateHighlightBackground = typeArray.getResourceId(R.styleable.WindCalendar_dateHighlightBackground, R.drawable.wl_calendar_highlight_background);

            // tag code
            info.setTagCode(typeArray.getString(R.styleable.WindCalendar_tagCode));

            // preload
            offscreen = typeArray.getInteger(R.styleable.WindCalendar_offscreen, CalendarAdapter.DEFAULT_OFF_SCREEN);

            // calendar type
            int calendarTypeIdx = typeArray.getInt(R.styleable.WindCalendar_lunar, -1);
            if (calendarTypeIdx >= 0) {
                info.setLunarType(CalendarType.typeOf(calendarTypeIdx));
            }
            // Week start on
            int weekStartsOnDay = typeArray.getInt(R.styleable.WindCalendar_weekStartsOn, Calendar.SUNDAY);
            if (weekStartsOnDay > 0) {
                info.setWeekStartsOn(WeekStartsOn.typeOf(weekStartsOnDay));
            }

            // get auto build option
            autoBuild = typeArray.getBoolean(R.styleable.WindCalendar_autoBuild, true);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            typeArray.recycle();
        }

        style.weekDays(weekDays)
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
                .dateHighlightLunarTextColor(dateHighlightLunarTextColor)
                .monthPanelViewBackground(monthPanelViewBackground)
                .dateCellBackground(dateCellBackground)
                .dateEventBackground(dateEventBackground)
                .dateWeekendBackground(dateWeekendBackground)
                .dateTodayBackground(dateTodayBackground)
                .dateHighlightBackground(dateHighlightBackground);

        // Remove old fragment when activity is reloaded by system setting changed
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        for (Fragment frag : fragManager.getFragments()) {
            if (frag instanceof MonthViewFragment) {
                String tagCode = ((MonthViewFragment) frag).getTagCode();
                if (tagCode == null || tagCode.equals(info.getTagCode())) {
                    fragTrans.remove(frag);
                }
            }
        }
        fragTrans.commit();

        // setup calendar view pager
        _calendarViewPager.setCalendarEvent(eventListener);
        _calendarViewPager.setSaveEnabled(false); // do not keep fragment state when view is restart
        _calendarViewPager.setBackgroundResource(style.monthPanelViewBackground());

        // If auto build is false, user need to call rebuild function after setting up the calendar
        if (autoBuild) {
            createWeekDayPanel();
            adapter = createCalendarAdapter();
            _calendarViewPager.setAdapter(adapter);
            _calendarViewPager.setSelectedDate(new Date()); // set current date as default
        }
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
     * Get calendar style
     *
     * @return style
     */
    public Style getStyle() {
        return style;
    }

    /**
     * @return calendar view pager
     */
    public CalendarViewPager getCalendarViewPager() {
        return _calendarViewPager;
    }

    /**
     * @return week day panel
     */
    public ViewGroup getWeekDayPanelView() {
        return _weekDayPanelView;
    }

    /**
     * Get lunar calendar typeS
     *
     * @return lunar calendar type
     */
    @Nullable
    public CalendarType getLunarType() {
        return info.getLunarType();
    }

    /**
     * @return day that week starts on
     */
    @NonNull
    public WeekStartsOn getWeekStartsOn() {
        return info.getWeekStartsOn();
    }

    /**
     * Get calendar adapter
     *
     * @return adapter
     */
    public CalendarAdapter getAdapter() {
        return adapter;
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
     * Get current selected view page
     *
     * @return current view page
     */
    public MonthViewFragment getSelectedPage() {
        return adapter.getCurrentPage();
    }

    /**
     * Get current month
     *
     * @return current month
     */
    public MonthInfo getSelectedMonth() {
        return adapter.getSelectedMonth();
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
     * Get selected dates
     *
     * @return list of selected date IDs
     */
    public Collection<String> getSelectedDate() {
        return info.getSelectedDate();
    }

    /**
     * Clear highlight events without refreshing page
     */
    public void clearSelectedDates() {
        info.clearSelectedDates();
    }

    /**
     * Select date item view
     *
     * @param viewHolder view holder
     * @return true if item view is already selected before
     */
    public boolean selectDate(MonthAdapter.ViewHolder viewHolder) {
        return info.selectDate(viewHolder);
    }

    /**
     * Select dates
     *
     * @param dateIds list of date ids
     * @see CalendarUtil#toId(int, int, int)
     */
    public void selectDates(Collection<String> dateIds) {
        info.selectDates(dateIds);
        adapter.refreshCurrentPage();
    }

    /**
     * Set highlight dates and refresh current page also
     * Set empty to clear all events and refresh page also
     *
     * @param dateIds list of date ids
     * @see CalendarUtil#toId(int, int, int)
     */
    public void selectDates(String... dateIds) {
        selectDates(Arrays.asList(dateIds));
    }

    /**
     * Set selected date
     *
     * @param date selected date
     */
    public void setSelectedDate(Date date) {
        _calendarViewPager.setSelectedDate(date);
    }

    /**
     * Set week days string. The day should start from Monday.
     * For example, new String[] {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"}
     *
     * @param weekDays week days
     */
    public void setWeekDayString(String[] weekDays) {
        style.weekDays(weekDays);
    }

    /**
     * Set lunar calendar type
     *
     * @param lunarType lunar calendar type
     */
    public void setLunarType(CalendarType lunarType) {
        info.setLunarType(lunarType);
    }

    /**
     * Set week start on option
     *
     * @param day start day of week
     */
    public void setWeekStartsOn(WeekStartsOn day) {
        info.setWeekStartsOn(day);
    }

    /* -------------------- Event Listener ------------------- */

    /**
     * Set date item click listener
     *
     * @param listener date item click listener
     * @return wind calendar
     */
    public WindCalendar setOnDateItemClickListener(OnDateItemClickListener listener) {
        eventListener.dateItemClickListener = listener;
        return this;
    }

    /**
     * Set date item long click listener
     *
     * @param listener date item long click listener
     * @return wind calendar
     */
    public WindCalendar setOnDateItemLongClickListener(OnDateItemLongClickListener listener) {
        eventListener.dateItemLongClickListener = listener;
        return this;
    }

    /**
     * Set date item double click listener
     *
     * @param listener date item double click listener
     * @return wind calendar
     */
    public WindCalendar setOnDateItemDoubleClickListener(OnDateItemDoubleClickListener listener) {
        eventListener.dateItemDoubleClickListener = listener;
        return this;
    }

    /**
     * Set date item touch up listener
     *
     * @param listener date item touch up listener
     * @return wind calendar
     */
    public WindCalendar setOnDateItemTouchUpListener(OnDateItemTouchUpListener listener) {
        eventListener.dateItemTouchUpListener = listener;
        return this;
    }

    /**
     * Set date item touch down listener
     *
     * @param listener date item touch down listener
     * @return wind calendar
     */
    public WindCalendar setOnDateItemTouchDownListener(OnDateItemTouchDownListener listener) {
        eventListener.dateItemTouchDownListener = listener;
        return this;
    }

    /**
     * Set week day item click listener
     *
     * @param listener listener
     * @return calendar
     */
    public WindCalendar setOnWeekDayItemClickListener(OnWeekDayItemClickListener listener) {
        eventListener.weekDayItemClickListener = listener;
        return this;
    }

    /**
     * Set week day item long click listener
     *
     * @param listener listener
     * @return calendar
     */
    public WindCalendar setOnWeekDayItemLongClickListener(OnWeekDayItemLongClickListener listener) {
        eventListener.weekDayItemLongClickListener = listener;
        return this;
    }

    /**
     * Set week day item double click listener
     *
     * @param listener listener
     * @return calendar
     */
    public WindCalendar setOnWeekDayItemDoubleClickListener(OnWeekDayItemDoubleClickListener listener) {
        eventListener.weekDayItemDoubleClickListener = listener;
        return this;
    }

    /**
     * Set week day item touch down listener
     *
     * @param listener listener
     * @return calendar
     */
    public WindCalendar setOnWeekDayItemTouchDownListener(OnWeekDayItemTouchDownListener listener) {
        eventListener.weekDayItemTouchDownListener = listener;
        return this;
    }

    /**
     * Set week day item touch up listener
     *
     * @param listener listener
     * @return calendar
     */
    public WindCalendar setOnWeekDayItemTouchUpListener(OnWeekDayItemTouchUpListener listener) {
        eventListener.weekDayItemTouchUpListener = listener;
        return this;
    }

    /**
     * Set week day item touch up listener
     *
     * @param listener listener
     * @return calendar
     */
    public WindCalendar setOnMonthPageChangeListener(OnMonthPageChangeListener listener) {
        eventListener.monthPageChangeListener = listener;
        return this;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Create calendar adapter
     *
     * @return adapter
     */
    private CalendarAdapter createCalendarAdapter() {
        // Create new adapter
        CalendarAdapter adapter = new CalendarAdapter(fragManager, new GregorianCalendar(), offscreen);
        adapter.setCalendarStyle(style);
        adapter.setCalendarInfo(info);
        adapter.setCalendarEvent(eventListener);
        return adapter;
    }

    /**
     * Create week date panel view
     */
    private void createWeekDayPanel() {
        _weekDayPanelView.removeAllViews();
        _weekDayPanelView.setBackgroundResource(style.weekDayPanelBackground());
        Integer[] weekDays;
        switch (info.getWeekStartsOn()) {
            case MONDAY:
                weekDays = new Integer[]{Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
                break;
            case SATURDAY:
                weekDays = new Integer[]{Calendar.SATURDAY, Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY};
                break;
            case SUNDAY:
                weekDays = new Integer[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};
                break;
            default:
                weekDays = new Integer[]{};
        }

        String[] weekDayTexts = style.weekDays();
        for (Integer day : weekDays) {
            View itemView = inflater.inflate(R.layout.wl_calendar_week_day_view, _weekDayPanelView, false);
            itemView.setTag(day);
            itemView.setOnTouchListener(weekDayItemTouchListener);
            TextView _dayTextView = itemView.findViewById(R.id._dayTextView);
            Integer dayIdx = WEEK_DAY_MAP.get(day);
            _dayTextView.setText(weekDayTexts[dayIdx != null ? dayIdx : 0]);
            _dayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.weekDayTextSize());
            _dayTextView.setTextColor(style.weekDayTextColor());

            // add to layout
            ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            lp.width = style.dateCellSize();
            _weekDayPanelView.addView(itemView, lp);
        }
    }

    /**
     * Rebuild the calendar
     *
     * @return calendar
     */
    public WindCalendar rebuild() {
        // re-create week day panel
        createWeekDayPanel();

        // create calendar adapter if it not exist
        if (adapter == null) {
            adapter = createCalendarAdapter();
            adapter.setSelectedDate(new Date());
            _calendarViewPager.refreshAdapter(adapter);
            _calendarViewPager.setCurrentItem(-1);
        } else {
            // Refresh month view panel
            _calendarViewPager.refreshAdapter(adapter);
            // set selected date -> set viewpager current page to center of calendar slideshow
            setSelectedDate(new Date());
        }
        return this;
    }

    /**
     * Refresh data
     */
    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Custom calendar style
     */
    public static class Style extends CalendarStyle {

    }

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

    /**
     * On week day item click listener
     */
    public interface OnWeekDayItemClickListener {
        /**
         * Trigger when user click on week day item
         *
         * @param view      day view
         * @param dayOfWeek day of week
         * @return true if consume the event
         */
        boolean onClick(View view, int dayOfWeek);
    }

    /**
     * On week day item long click listener
     */
    public interface OnWeekDayItemLongClickListener {
        /**
         * Trigger when user long click on week day item
         *
         * @param view      day view
         * @param dayOfWeek day of week
         * @return true if consume the event
         */
        boolean onLongClick(View view, int dayOfWeek);
    }

    /**
     * On week day item double click listener
     */
    public interface OnWeekDayItemDoubleClickListener {
        /**
         * Trigger when user double click on week day item
         *
         * @param view      day view
         * @param dayOfWeek day of week
         * @return true if consume the event
         */
        boolean onDoubleClick(View view, int dayOfWeek);
    }

    /**
     * On week day item touch down listener
     */
    public interface OnWeekDayItemTouchDownListener {
        /**
         * Trigger when user touch down on week day item
         *
         * @param view      day view
         * @param dayOfWeek day of week
         * @return true if consume the event
         */
        boolean onTouchDown(View view, int dayOfWeek);
    }

    /**
     * On week day item touch up listener
     */
    public interface OnWeekDayItemTouchUpListener {
        /**
         * Trigger when user touch up on week day item
         *
         * @param view      day view
         * @param dayOfWeek day of week
         * @return true if consume the event
         */
        boolean onTouchUp(View view, int dayOfWeek);
    }

    /**
     * On month page change listener
     */
    public interface OnMonthPageChangeListener {
        /**
         * Trigger when user swipe to go to previous or next month
         *
         * @param preMonth previous month. Null for the first loading
         * @param curMonth current selected month
         */
        void onChange(@Nullable MonthInfo preMonth, @NonNull MonthInfo curMonth);
    }

}
