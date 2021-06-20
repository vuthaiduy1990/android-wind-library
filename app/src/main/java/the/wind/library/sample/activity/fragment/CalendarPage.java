package the.wind.library.sample.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import the.wind.library.anim.PageTransformerType;
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

    }

    public void setPageTransformer(PageTransformerType type) {
        _calendarView.setPageTransformer(type);
    }
}