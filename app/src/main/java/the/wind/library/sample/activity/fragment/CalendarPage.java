package the.wind.library.sample.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import the.wind.library.anim.PageTransformerType;
import the.wind.library.calendar.CalendarUtil;
import the.wind.library.calendar.DateInfo;
import the.wind.library.calendar.MonthAdapter;
import the.wind.library.calendar.WindCalendar;
import the.wind.library.sample.R;

public class CalendarPage extends Fragment {

    private WindCalendar _calendarView;
    private boolean highlighted;
    private Calendar cal = Calendar.getInstance();
    private List<String> neighborDates = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _calendarView = view.findViewById(R.id._calendarView);

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
                Toast.makeText(getContext(), String.format("%s-%s-%s", data.getYear(), data.getMonth(), data.getDayOfMonth()), Toast.LENGTH_SHORT).show();
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

    }

    public void setPageTransformer(PageTransformerType type) {
        _calendarView.setPageTransformer(type);
    }
}