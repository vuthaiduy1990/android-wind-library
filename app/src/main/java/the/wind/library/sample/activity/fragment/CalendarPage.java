package the.wind.library.sample.activity.fragment;

import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import the.wind.library.anim.PageTransformerType;
import the.wind.library.calendar.CalendarUtil;
import the.wind.library.calendar.DateInfo;
import the.wind.library.calendar.MonthAdapter;
import the.wind.library.calendar.MonthInfo;
import the.wind.library.calendar.WindCalendar;
import the.wind.library.calendar.WindCalendarDialog;
import the.wind.library.calendar.model.CalendarType;
import the.wind.library.calendar.model.WeekStartsOn;
import the.wind.library.dialog.WindDialog;
import the.wind.library.sample.R;

public class CalendarPage extends Fragment {

    // Fragment manager
    private FragmentManager fragManager;

    // Views
    private WindCalendar _calendarView;
    private TextView _monthInfoText;
    private WindCalendarDialog _calendarDialog;


    private boolean highlighted;
    private final Calendar cal = new GregorianCalendar();
    private final List<String> neighborDates = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragManager = getChildFragmentManager();
        _calendarView = view.findViewById(R.id._calendarView);
        _monthInfoText = view.findViewById(R.id._monthInfoText);

        // Set event dates
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -2);
        for (int i = 0; i <= 4; i++) {
            neighborDates.add(CalendarUtil.toId(cal));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        _calendarView.addEventDate(neighborDates);

        // set date click event
        _calendarView.setOnDateItemClickListener(new WindCalendar.OnDateItemClickListener() {
            @Override
            public boolean onClick(MonthAdapter.ViewHolder viewHolder, View view, DateInfo data) {
                Toast.makeText(getContext(), String.format("%s-%s-%s", data.getYear(), data.getMonth() + 1, data.getDayOfMonth()), Toast.LENGTH_SHORT).show();
                return false;
            }
        }).setOnMonthPageChangeListener(new WindCalendar.OnMonthPageChangeListener() {
            @Override
            public void onChange(@Nullable MonthInfo preMonth, @NonNull MonthInfo curMonth) {
                String preMonthText = preMonth != null ? preMonth.getYear() + "/" + (preMonth.getMonth() + 1) : "null";
                String curMonthText = curMonth.getYear() + "/" + (curMonth.getMonth() + 1);
                _monthInfoText.setText(String.format("pre month: %s, current month: %s", preMonthText, curMonthText));
            }
        }).setOnWeekDayItemClickListener(new WindCalendar.OnWeekDayItemClickListener() {
            @Override
            public boolean onClick(View view, int dayOfWeek) {
                Toast.makeText(getContext(), String.format("%s", dayOfWeek), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // set highlight event
        view.findViewById(R.id._highlightBtn).setOnClickListener(v -> {
            if (highlighted) {
                _calendarView.clearSelectedDates();
            } else {
                _calendarView.selectDateViews(neighborDates);
            }
            highlighted = !highlighted;
        });

        // Set today
        view.findViewById(R.id._todayBtn).setOnClickListener(v -> _calendarView.scrollToDate(new Date()));

        // Change Lunar type
        view.findViewById(R.id._IslamicLunarCalendar).setOnClickListener(v -> {
            _calendarView.setLunarType(CalendarType.Islamic);
            _calendarView.rebuild(new Date());
        });
        view.findViewById(R.id._VietnameseLunarCalendar).setOnClickListener(v -> {
            _calendarView.setLunarType(CalendarType.Vietnamese);
            _calendarView.rebuild(new Date());
        });

        // Set weeks start on
        view.findViewById(R.id._startOnMonday).setOnClickListener(v -> {
            _calendarView.setWeekStartsOn(WeekStartsOn.MONDAY);
            _calendarView.rebuild(new Date());
        });
        view.findViewById(R.id._startOnSunday).setOnClickListener(v -> {
            _calendarView.setWeekStartsOn(WeekStartsOn.SUNDAY);
            _calendarView.rebuild(new Date());
        });

        // Show calendar dialog
        view.findViewById(R.id._calendarDialog).setOnClickListener(v -> {
            cal.setTime(new Date());
            cal.add(Calendar.DATE, 3);
            Date d1 = cal.getTime();
            cal.add(Calendar.MONTH, 10);
            Date d2 = cal.getTime();
            getCalendarDialog().show(fragManager, d1, d2);
            // Support show date with timezone also
            // getCalendarDialog().show(fragManager, TimeZone.getTimeZone("Pacific/Pago_Pago"), d1, d2);
        });
    }

    public void setPageTransformer(PageTransformerType type) {
        _calendarView.setPageTransformer(type);
    }

    public WindCalendarDialog getCalendarDialog() {
        if (_calendarDialog == null) {
            _calendarDialog = new WindCalendarDialog(requireContext());
            WindDialog dialog = _calendarDialog.getWindDialog();
            dialog.setTitle("Date Picker");
            dialog.setIcon(R.drawable.ic_calendar);
            _calendarDialog.setLunarType(CalendarType.Vietnamese);
            _calendarDialog.addOnDateSetListener(new WindCalendarDialog.OnDateSetListener() {
                @Override
                public void onDateSet(WindCalendarDialog dialog, List<Date> dates) {
                    Toast.makeText(requireContext(), dates.size() + " selected", Toast.LENGTH_SHORT).show();
                    _calendarView.scrollToDate(dates.get(0));
                }
            });

            _calendarDialog.build();
        }
        return _calendarDialog;
    }
}