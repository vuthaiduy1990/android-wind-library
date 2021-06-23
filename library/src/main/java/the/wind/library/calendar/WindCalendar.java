package the.wind.library.calendar;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

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
    private final FragmentManager fragManager;
    private final ViewGroup _rootView;
    private final CalendarViewPager _calendarViewPager;

    // Adapter & data
    private final CalendarAdapter adapter;
    private final CalendarInfo info = new CalendarInfo(new GregorianCalendar());

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
        int cellBackground = 0;
        int eventBackground = 0;
        int weekendBackground = 0;
        int todayBackground = 0;
        int dateTextColor = 0;
        int eventTextColor = 0;
        int weekendTextColor = 0;
        int todayTextColor = 0;

        Resources res = getResources();
        try {
            // Date cell and text size
            dateCellSize = (int) typeArray.getDimension(R.styleable.WindCalendar_dateCellSize, res.getDimension(R.dimen.wl_calendar_date_cell_size));
            dateTextSize = typeArray.getDimension(R.styleable.WindCalendar_dateTextSize, res.getDimension(R.dimen.wl_text_small));
            lunarDateTextSize = typeArray.getDimension(R.styleable.WindCalendar_lunarDateTextSize, res.getDimension(R.dimen.wl_calendar_lunar_date_text_size));
            eventSymbolSize = typeArray.getDimension(R.styleable.WindCalendar_eventSymbolSize, res.getDimension(R.dimen.wl_calendar_event_symbol_size));

            // Date cell background
            cellBackground = typeArray.getResourceId(R.styleable.WindCalendar_cellBackground, R.color.wl_transparent);
            eventBackground = typeArray.getResourceId(R.styleable.WindCalendar_eventBackground, R.color.wl_transparent);
            weekendBackground = typeArray.getResourceId(R.styleable.WindCalendar_weekendBackground, R.color.wl_transparent);
            todayBackground = typeArray.getResourceId(R.styleable.WindCalendar_todayBackground, R.drawable.wl_calendar_today_background);

            // Date text color
            dateTextColor = typeArray.getColor(R.styleable.WindCalendar_dateTextColor, ContextCompat.getColor(context, R.color.wl_black));
            eventTextColor = typeArray.getColor(R.styleable.WindCalendar_eventTextColor, ContextCompat.getColor(context, R.color.wl_black));
            weekendTextColor = typeArray.getColor(R.styleable.WindCalendar_weekendTextColor, ContextCompat.getColor(context, R.color.wl_danger));
            todayTextColor = typeArray.getColor(R.styleable.WindCalendar_todayTextColor, ContextCompat.getColor(context, R.color.wl_danger));

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
                        .cellBackground(cellBackground)
                        .eventBackground(eventBackground)
                        .weekendBackground(weekendBackground)
                        .todayBackground(todayBackground)
                        .dateTextColor(dateTextColor)
                        .eventTextColor(eventTextColor)
                        .weekendTextColor(weekendTextColor)
                        .todayTextColor(todayTextColor));
        adapter.setCalendarInfo(info);
        _calendarViewPager.setAdapter(adapter);
        _calendarViewPager.setSelectedDate(new Date());
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
     * Set event dates
     *
     * @param dateIds date which has event
     */
    public void setEventDates(Set<String> dateIds) {
        info.clearEvents();
        if (dateIds != null) {
            for (String id : dateIds) {
                info.addEventDate(id);
            }
        }
    }

    /**
     * Add event date
     *
     * @param dateId date ID
     * @see CalendarUtil#toId(int, int, int)
     */
    public void addEventDate(@NonNull String dateId) {
        info.addEventDate(dateId);
    }

    /**
     * Remove event date
     *
     * @param dateId date ID
     * @see CalendarUtil#toId(int, int, int)
     */
    public void removeEventDate(String dateId) {
        info.removeEventDate(dateId);
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

}
