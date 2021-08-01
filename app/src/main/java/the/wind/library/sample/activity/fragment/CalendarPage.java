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
import the.wind.library.anim.PageTransformerType;
import the.wind.library.calendar.CalendarType;
import the.wind.library.calendar.CalendarUtil;
import the.wind.library.calendar.DateInfo;
import the.wind.library.calendar.MonthAdapter;
import the.wind.library.calendar.MonthInfo;
import the.wind.library.calendar.WeekStartsOn;
import the.wind.library.calendar.WindCalendar;
import the.wind.library.sample.R;

public class CalendarPage extends Fragment {

    private WindCalendar _calendarView;
    private TextView _monthInfoText;
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
            public void onChange(@Nullable MonthInfo previousMonth, MonthInfo currentMonth) {
                String month = currentMonth.getYear() + "/" + (currentMonth.getMonth() + 1);
                _monthInfoText.setText(month);
            }
        }).setOnWeekDayItemClickListener(new WindCalendar.OnWeekDayItemClickListener() {
            @Override
            public boolean onClick(View view, int dayOfWeek) {
                Toast.makeText(getContext(), String.format("%s", dayOfWeek), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // set highlight event
        view.findViewById(R.id._highlightBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (highlighted) {
                    _calendarView.highlightDates();
                } else {
                    _calendarView.highlightDates(neighborDates);
                }
                highlighted = !highlighted;
            }
        });

        // Set today
        view.findViewById(R.id._todayBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _calendarView.setSelectedDate(new Date());
            }
        });

        // Change Lunar type
        view.findViewById(R.id._IslamicLunarCalendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _calendarView.setLunarType(CalendarType.Islamic);
                _calendarView.rebuild();
            }
        });
        view.findViewById(R.id._ChineseLunarCalendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _calendarView.setLunarType(CalendarType.Chinese);
                _calendarView.rebuild();
            }
        });

        // Set weeks start on
        view.findViewById(R.id._startOnMonday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _calendarView.setWeekStartsOn(WeekStartsOn.MONDAY);
                _calendarView.rebuild();
            }
        });
        view.findViewById(R.id._startOnSunday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _calendarView.setWeekStartsOn(WeekStartsOn.SUNDAY);
                _calendarView.rebuild();
            }
        });

    }

    public void setPageTransformer(PageTransformerType type) {
        _calendarView.setPageTransformer(type);
    }
}