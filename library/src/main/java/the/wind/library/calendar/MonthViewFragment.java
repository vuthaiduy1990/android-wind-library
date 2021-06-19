package the.wind.library.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
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

    // date recycle list view
    private WindRecycleView _rvDateGridView;

    // date list
    private MonthInfo monthInfo;

    /**
     * Constructor
     *
     * @param monthInfo month info
     */
    public MonthViewFragment(MonthInfo monthInfo) {
        this.monthInfo = monthInfo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wl_calendar_month_view_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _rvDateGridView = view.findViewById(R.id._rvDateGridView);
        _rvDateGridView.setLayoutAnimation(null);
        _rvDateGridView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        _rvDateGridView.setAdapter(new MonthAdapter(monthInfo));

        TextView _monthTextView = view.findViewById(R.id._monthTextView);
        _monthTextView.setText(monthInfo.getYear() + "-" + (monthInfo.getMonth() + 1));
    }
}
