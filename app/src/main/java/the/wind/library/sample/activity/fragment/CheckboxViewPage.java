package the.wind.library.sample.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import the.wind.library.sample.R;
import the.wind.library.view.CheckboxView;

public class CheckboxViewPage extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkbox_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CheckboxView checkboxView = view.findViewById(R.id._defaultCheckbox);
        checkboxView.setOnCheckedListener(new CheckboxView.OnCheckedListener() {
            @Override
            public void onChecked(View view, boolean checked) {
                Toast.makeText(view.getContext(), Boolean.toString(checked), Toast.LENGTH_SHORT).show();
            }
        });
    }
}