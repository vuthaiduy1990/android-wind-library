package the.wind.library.sample.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import the.wind.library.anim.PageTransformerType;
import the.wind.library.calendar.CalendarUtil;
import the.wind.library.calendar.WindCalendar;
import the.wind.library.sample.R;

public class CalendarPage extends Fragment {

    private WindCalendar _calendarView;

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
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -2);
        for (int i = 0; i <= 4; i++) {
            _calendarView.addEventDate(CalendarUtil.toId(cal));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    public void setPageTransformer(PageTransformerType type) {
        _calendarView.setPageTransformer(type);
    }
}