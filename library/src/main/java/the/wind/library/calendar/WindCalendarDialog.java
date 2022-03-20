package the.wind.library.calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import the.wind.library.CWBundle;
import the.wind.library.R;
import the.wind.library.dialog.WindDialog;
import the.wind.library.dialog.YearMonthSelectionDialog;
import the.wind.library.view.Button;

/**
 * Calendar dialog
 */
public class WindCalendarDialog extends DialogFragment {

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

    // views
    private final LayoutInflater inflater;
    private final WindDialog _coreDialog;
    private CalendarViewPager _calendarViewPager;
    private ViewGroup _weekDayPanelView;
    private YearMonthSelectionDialog _yearSelectionDialog;

    // adapter
    private CalendarAdapter adapter;

    // model
    private final Calendar calendar = new GregorianCalendar();
    private final Calendar eventCal = new GregorianCalendar();
    private final CWBundle bundle = new CWBundle();
    private final Style style = new Style();
    private final CalendarInfo info = new CalendarInfo(calendar);
    private final CalendarEvent eventListener = new CalendarEvent();
    private final Map<String, Date> selectedDateMap = new HashMap<>();
    private Date selectedDate;

    // label resource
    private Integer yearLabelResId;
    private Integer monthLabelResId;
    private Integer cancelBtnResId;
    private Integer selectBtnResId;

    // animation
    private final RotateAnimation rotationIconAnim;

    // listener
    private final List<OnDateSetListener> dateSetListeners = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context application context
     */
    public WindCalendarDialog(Context context) {
        inflater = LayoutInflater.from(context);
        Resources res = context.getResources();

        // Week day style
        style.weekDays(new String[]{"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"});
        style.weekDayTextSize(res.getDimension(R.dimen.wl_calendar_dialog_date_text_size));
        style.weekDayTextColor(ContextCompat.getColor(context, R.color.wl_calendar_week_day));
        style.weekDayPanelBackground(0);
        style.weekDayHoverBackground(R.drawable.wl_calendar_highlight_background);

        // Date cell and text size
        style.dateCellSize((int) res.getDimension(R.dimen.wl_calendar_dialog_date_cell_size));
        style.dateTextSize(res.getDimension(R.dimen.wl_calendar_dialog_date_text_size));
        style.dateLunarTextSize(res.getDimension(R.dimen.wl_calendar_dialog_lunar_date_text_size));
        style.dateEventSymbolSize(res.getDimension(R.dimen.wl_calendar_event_symbol_size));

        // Date text color
        style.dateTextColor(ContextCompat.getColor(context, R.color.wl_black));
        style.dateLunarTextColor(ContextCompat.getColor(context, R.color.wl_calendar_lunar_date_text));
        style.dateEventTextColor(ContextCompat.getColor(context, R.color.wl_black));
        style.dateWeekendTextColor(ContextCompat.getColor(context, R.color.wl_danger));
        style.dateTodayTextColor(ContextCompat.getColor(context, R.color.wl_white));
        style.dateHighlightTextColor(ContextCompat.getColor(context, R.color.wl_black));
        style.dateHighlightLunarTextColor(ContextCompat.getColor(context, R.color.wl_calendar_lunar_date_text));

        // Date cell background
        style.monthPanelViewBackground(0);
        style.dateCellBackground(0);
        style.dateEventBackground(0);
        style.dateWeekendBackground(0);
        style.dateTodayBackground(R.drawable.wl_calendar_today_background);
        style.dateHighlightBackground(R.drawable.wl_calendar_highlight_background);

        // set calendar info
        info.setTagCode(WindCalendarDialog.class.getName());
        setLunarType(null);
        setWeekStartsOn(WeekStartsOn.SUNDAY);

        // set event listener
        eventListener.dateItemTouchDownListener = (viewHolder, view, data) -> true;
        eventListener.dateItemTouchUpListener = (viewHolder, view, data) -> true;
        eventListener.dateItemClickListener = this::onDateItemClick;
        eventListener.dateItemDoubleClickListener = this::onDateItemDoubleClick;
        eventListener.dateItemLongClickListener = this::onDateItemLongClick;
        eventListener.monthPageChangeListener = this::onMonthPageChange;

        // create animation
        rotationIconAnim = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotationIconAnim.setRepeatMode(Animation.RESTART);
        rotationIconAnim.setRepeatCount(0);
        rotationIconAnim.setDuration(500);

        // create core dialog
        _coreDialog = createCoreDialog(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Rebuild month view panel
        FragmentManager fragManager = getChildFragmentManager();
        adapter = createMonthViewAdapter(fragManager);

        if (_calendarViewPager.getAdapter() == null) {
            _calendarViewPager.setAdapter(adapter);
            _calendarViewPager.scrollToDate(selectedDate);

        } else {
            _calendarViewPager.refreshAdapter(adapter, selectedDate);
        }

        return _coreDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        reset();
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public void show(FragmentManager manager, String tag) {
        if (isAdded()) return;
        if (selectedDate == null) {
            selectedDate = new Date();
            mapDate(selectedDate, null);
        }
        info.setTagCode(tag);
        super.show(manager, tag);
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /**
     * On calendar date item click handler
     *
     * @param viewHolder item view holder
     * @param view       item view
     * @param data       date info
     * @return true if consume the event, else return false
     */
    protected boolean onDateItemClick(MonthAdapter.ViewHolder viewHolder, View view, DateInfo data) {
        // clear old selected items
        info.clearSelectedDates();
        selectedDateMap.clear();
        // highlight selected items
        info.selectDate(viewHolder);
        selectedDateMap.put(data.getId(), data.getDate());
        return true;
    }

    /**
     * On calendar date item double click handler
     * Auto select date and close dialog
     *
     * @param viewHolder item view holder
     * @param view       item view
     * @param data       date info
     * @return true if consume the event, else return false
     */
    protected boolean onDateItemDoubleClick(MonthAdapter.ViewHolder viewHolder, View view, DateInfo data) {
        onSelectedDate(Collections.singletonList(data.getDate()));
        dismiss();
        return true;
    }

    /**
     * On calendar date item long click handler
     *
     * @param viewHolder item view holder
     * @param view       item view
     * @param data       date info
     * @return true if consume the event, else return false
     */
    protected boolean onDateItemLongClick(MonthAdapter.ViewHolder viewHolder, View view, DateInfo data) {
        // highlight selected items
        info.selectDate(viewHolder);
        selectedDateMap.put(data.getId(), data.getDate());
        return true;
    }

    /**
     * On month page change handler
     *
     * @param preMonth previous month
     * @param curMonth current month
     */
    protected void onMonthPageChange(@Nullable MonthInfo preMonth, @NonNull MonthInfo curMonth) {
        _coreDialog.setSubTitle(String.format("%s %s", formatTitleDate(curMonth.getDate()), "▼"));
    }

    /**
     * On title view click
     *
     * @param v view
     */
    private void onTitleViewClick(View v) {
        CalendarAdapter adapter = (CalendarAdapter) _calendarViewPager.getAdapter();
        assert adapter != null;
        MonthInfo monthInfo = adapter.getSelectedMonth();
        createYearSelectionDialog(v.getContext()).show(monthInfo.getYear(), monthInfo.getMonth());
    }

    /**
     * On reload calendar to given date
     *
     * @param date date
     */
    protected void onReloadCalendar(Date date) {
        _calendarViewPager.scrollToDate(date);
    }

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return bundle data
     */
    public CWBundle bundle() {
        return bundle;
    }

    /**
     * Get wind dialog
     *
     * @return wind dialog
     */
    public WindDialog getWindDialog() {
        return _coreDialog;
    }

    /**
     * Get calendar dialog style
     *
     * @return style
     */
    public Style getStyle() {
        return style;
    }

    /**
     * Set year label resource id
     *
     * @param resId string resource id
     */
    public void setYearLabelResId(@StringRes int resId) {
        this.yearLabelResId = resId;
    }

    /**
     * Set month label resource id
     *
     * @param resId string resource id
     */
    public void setMonthLabelResId(@StringRes int resId) {
        this.monthLabelResId = resId;
    }

    /**
     * Set cancel button label resource id
     *
     * @param resId string resource id
     */
    public void setCancelBtnResId(@StringRes int resId) {
        this.cancelBtnResId = resId;
    }

    /**
     * Set select button label resource id
     *
     * @param resId string resource id
     */
    public void setSelectBtnResId(@StringRes int resId) {
        this.selectBtnResId = resId;
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

    /**
     * Get on selected date listener
     *
     * @return listeners
     */
    public List<OnDateSetListener> getDateSetListeners() {
        return dateSetListeners;
    }

    /**
     * Set on date set listener
     *
     * @param listener listener
     */
    public void addOnDateSetListener(OnDateSetListener listener) {
        dateSetListeners.add(listener);
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Create wind dialog
     *
     * @param context application dialog
     * @return wind dialog
     */
    private WindDialog createCoreDialog(Context context) {
        WindDialog dialog = new WindDialog(context, WindDialog.LayoutType.FUBUKI) {
            @Override
            public void dismiss() {
                dismissImmediately();
            }
        };
        dialog.setInOutAnimType(WindDialog.InOutAnimType.SWEET_ALERT);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        // set dialog content view
        dialog.setContentView(R.layout.wl_calendar_dialog);
        View contentView = dialog.contentView();
        _weekDayPanelView = contentView.findViewById(R.id._weekDayPanelView);
        _calendarViewPager = contentView.findViewById(R.id._calendarViewPager);

        // Add extra header
        View extraHeader = inflater.inflate(R.layout.wl_calendar_dialog_extra_header, dialog.headerLayout(), false);
        dialog.addViewToHeader(extraHeader);
        extraHeader.findViewById(R.id._reloadIconView).setOnClickListener(v -> {
            v.startAnimation(rotationIconAnim);
            onReloadCalendar(selectedDate);
        });

        // show year selection dialog when user click on title view of dialog
        dialog.titleView().setOnClickListener(this::onTitleViewClick);
        dialog.subTitleView().setOnClickListener(this::onTitleViewClick);
        dialog.icon().setOnClickListener(this::onTitleViewClick);

        // add action button
        dialog.addButton(Button.Type.GRAY, context.getString(R.string.wl_cancel), null)
                .setOnClickListener(v -> {
                    dismiss();
                });
        dialog.addButton(Button.Type.SUCCESS, context.getString(R.string.wl_ok), null)
                .setOnClickListener(v -> {
                    onSelectedDate(new ArrayList<>(selectedDateMap.values()));
                    dismiss();
                });
        return dialog;
    }

    /**
     * Rebuild the calendar
     *
     * @return calendar dialog
     */
    public WindCalendarDialog build() {
        // re-create week day panel
        createWeekDayPanel();

        // setup calendar view pager
        _calendarViewPager.setCalendarEvent(eventListener);
        _calendarViewPager.setSaveEnabled(false); // do not keep fragment state when view is restart
        _calendarViewPager.setBackgroundResource(style.monthPanelViewBackground());
        return this;
    }

    /**
     * Create month view panel
     *
     * @param fragManager fragment manager
     * @return calendar adapter
     */
    private CalendarAdapter createMonthViewAdapter(FragmentManager fragManager) {
        // Create new adapter
        CalendarAdapter adapter = new CalendarAdapter(fragManager, new GregorianCalendar());
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
     * Create year selection dialog
     *
     * @param context application context
     * @return dialog
     */
    private YearMonthSelectionDialog createYearSelectionDialog(Context context) {
        if (_yearSelectionDialog != null) return _yearSelectionDialog;
        _yearSelectionDialog = new YearMonthSelectionDialog(context) {

            @Override
            protected boolean onSelection(@NonNull YearMonthSelectionDialog dialog, int year, int month) {
                calendar.set(year, month, 1);
                onReloadCalendar(calendar.getTime());
                return false;
            }
        };
        if (cancelBtnResId != null) {
            _yearSelectionDialog.setButtonText(0, cancelBtnResId);
        }
        if (selectBtnResId != null) {
            _yearSelectionDialog.setButtonText(1, selectBtnResId);
        }
        if (yearLabelResId != null) {
            _yearSelectionDialog.setYearLabel(yearLabelResId);
        }
        if (monthLabelResId != null) {
            _yearSelectionDialog.setMonthLabel(monthLabelResId);
        }
        return _yearSelectionDialog;
    }

    /**
     * Show calendar dialog
     *
     * @param manager fragment manager
     * @param dates   selected dates
     */
    public void show(FragmentManager manager, Date... dates) {
        if (isAdded()) return;
        show(manager, Arrays.asList(dates));
    }

    /**
     * Show calendar dialog
     *
     * @param manager  fragment manager
     * @param dates    selected dates
     * @param timeZone event timezone
     */
    public void show(FragmentManager manager, @Nullable TimeZone timeZone, Date... dates) {
        if (isAdded()) return;
        show(manager, Arrays.asList(dates), timeZone);
    }

    /**
     * Show calendar dialog
     *
     * @param manager fragment manager
     * @param dates   selected dates
     */
    public void show(FragmentManager manager, Iterable<Date> dates) {
        show(manager, dates, null);
    }

    /**
     * Show calendar dialog
     *
     * @param manager  fragment manager
     * @param dates    selected dates
     * @param timeZone event timezone
     */
    public void show(FragmentManager manager, Iterable<Date> dates, @Nullable TimeZone timeZone) {
        if (isAdded()) return;
        if (dates != null) {
            for (Date date : dates) {
                if (selectedDate == null) {
                    selectedDate = date;
                }
                mapDate(date, timeZone);
            }
        }
        show(manager, WindCalendarDialog.class.getName());
    }

    /**
     * Map date
     *
     * @param date     date
     * @param timeZone timezone
     */
    private void mapDate(Date date, @Nullable TimeZone timeZone) {
        if (timeZone != null) {
            eventCal.setTimeZone(timeZone);
        }
        eventCal.setTime(date);
        eventCal.set(Calendar.HOUR_OF_DAY, 0);
        eventCal.set(Calendar.MINUTE, 0);
        eventCal.set(Calendar.SECOND, 0);
        eventCal.set(Calendar.MILLISECOND, 0);
        String dateId = CalendarUtil.toId(eventCal);
        selectedDateMap.put(dateId, date);
        info.selectDates(dateId);
    }

    /**
     * Format title date
     *
     * @param date date
     * @return date string
     */
    private String formatTitleDate(Date date) {
        int flags = DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_MONTH;
        return DateUtils.formatDateTime(requireContext(), date.getTime(), flags);
    }

    /**
     * Reset data
     */
    private void reset() {
        info.reset();
        selectedDateMap.clear();
        selectedDate = null;
    }

    /**
     * On selected date
     *
     * @param dates selected date
     */
    private void onSelectedDate(List<Date> dates) {
        for (OnDateSetListener listener : dateSetListeners) {
            listener.onDateSet(WindCalendarDialog.this, new ArrayList<>(selectedDateMap.values()));
        }
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * On date select select listener
     */
    public interface OnDateSetListener {

        /**
         * On date set listener
         *
         * @param dialog wind calendar dialog
         * @param dates  list of selected dates
         */
        void onDateSet(WindCalendarDialog dialog, List<Date> dates);
    }

    /**
     * Dialog custom style
     */
    public static class Style extends CalendarStyle {

    }
}
