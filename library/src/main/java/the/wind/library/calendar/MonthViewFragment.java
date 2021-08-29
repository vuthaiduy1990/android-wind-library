package the.wind.library.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import the.wind.library.R;
import the.wind.library.view.WindRecycleView;

public class MonthViewFragment extends Fragment {

    // views
    private WindRecycleView _rvDateGridView;
    private MonthAdapter monthAdapter;

    // model
    private MonthInfo monthInfo;
    private String tagCode; // unique tag for fragment management by each calendar instance

    // styling
    private CalendarStyle calendarStyle;
    private CalendarInfo calendarInfo;
    private CalendarEvent calendarEvent;

    /**
     * Default constructor. Not used
     */
    public MonthViewFragment() {
    }

    /**
     * Constructor
     *
     * @param monthInfo     month info
     * @param calendarInfo  calendar info
     * @param calendarStyle date cell style
     * @param calendarEvent calendar event listener
     */
    public MonthViewFragment(MonthInfo monthInfo, CalendarInfo calendarInfo, CalendarStyle calendarStyle, CalendarEvent calendarEvent) {
        this();
        this.monthInfo = monthInfo;
        this.calendarInfo = calendarInfo;
        this.calendarStyle = calendarStyle;
        this.calendarEvent = calendarEvent;
        setTagCode(calendarInfo.getTagCode());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wl_calendar_month_view_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (monthInfo == null) return;
        createMonthDateGridView(view);
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Get tag code which is used to identified month view fragments for each calendar instance.
     * All month view fragments of each calendar instance will have the same tag code configured in {@link WindCalendar}
     *
     * @return tag code
     */
    @Nullable
    public String getTagCode() {
        return tagCode;
    }

    /**
     * Set tag code
     *
     * @param tagCode tag code
     */
    private void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    /**
     * @return month adapter
     */
    public MonthAdapter getMonthAdapter() {
        return monthAdapter;
    }

    /**
     * Get month info
     *
     * @return month info
     */
    public MonthInfo getMonthInfo() {
        return monthInfo;
    }

    /**
     * @return month grid view
     */
    public WindRecycleView getDateGridView() {
        return _rvDateGridView;
    }

    /* ---------------------- METHOD ------------------------- */

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
        _rvDateGridView.setThreshold(42);
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
