package the.wind.library.calendar;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import the.wind.library.R;
import the.wind.library.view.WindRecycleView;

public class MonthViewFragment extends Fragment {

    // date list
    private final MonthInfo monthInfo;
    private WindRecycleView _rvDateGridView;
    // styling
    private final CalendarStyle calendarStyle;
    private MonthAdapter monthAdapter;
    private final CalendarInfo calendarInfo;
    private final CalendarEvent calendarEvent;
    // views
    private LayoutInflater inflater;
    private ViewGroup _weekDayPanelView;

    /**
     * Constructor
     *
     * @param monthInfo     month info
     * @param calendarInfo  calendar info
     * @param calendarStyle date cell style
     * @param calendarEvent calendar event listener
     */
    public MonthViewFragment(MonthInfo monthInfo, CalendarInfo calendarInfo, CalendarStyle calendarStyle, CalendarEvent calendarEvent) {
        this.monthInfo = monthInfo;
        this.calendarInfo = calendarInfo;
        this.calendarStyle = calendarStyle;
        this.calendarEvent = calendarEvent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflater.inflate(R.layout.wl_calendar_month_view_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createWeekDatePanel(view);
        createMonthDateGridView(view);

    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /* ---------------------- METHOD ------------------------- */

    /**
     * Create week date panel view
     *
     * @param rootView root view
     */
    private void createWeekDatePanel(View rootView) {
        _weekDayPanelView = rootView.findViewById(R.id._weekDayPanelView);
        _weekDayPanelView.setBackgroundResource(calendarStyle.weekDayPanelBackground());

        String[] weekDays = calendarStyle.weekDays();
        for (String weekDay : weekDays) {
            final View itemView = inflater.inflate(R.layout.wl_calendar_week_day_view, _weekDayPanelView, false);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.performClick();
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setBackgroundResource(calendarStyle.weekDayHoverBackground());
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            v.setBackground(null);
                            break;
                    }
                    return true;
                }
            });
            TextView _dayTextView = itemView.findViewById(R.id._dayTextView);
            _dayTextView.setText(weekDay);
            _dayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, calendarStyle.weekDayTextSize());
            _dayTextView.setTextColor(calendarStyle.weekDayTextColor());

            // add to layout
            ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            lp.width = calendarStyle.dateCellSize();
            _weekDayPanelView.addView(itemView, lp);
        }
    }

    /**
     * Create month date view
     *
     * @param rootView root view
     */
    private void createMonthDateGridView(View rootView) {
        // create month view
        _rvDateGridView = rootView.findViewById(R.id._rvDateGridView);
        _rvDateGridView.setLayoutAnimation(null);
        _rvDateGridView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        monthAdapter = new MonthAdapter(monthInfo, calendarInfo, calendarStyle, calendarEvent);
        _rvDateGridView.setAdapter(monthAdapter);
    }

    /**
     * Notify dataset changes
     */
    public void notifyDataSetChanged() {
        monthAdapter.notifyDataSetChanged();
    }

    /* ---------------------- INNER CLASS -------------------- */
}
