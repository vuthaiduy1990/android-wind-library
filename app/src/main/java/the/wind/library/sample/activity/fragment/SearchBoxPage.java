package the.wind.library.sample.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import the.wind.library.sample.R;
import the.wind.library.view.SearchBox;

public class SearchBoxPage extends Fragment {

    private SearchBox _searchBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_box_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _searchBox = view.findViewById(R.id._searchBox);
        _searchBox.setOnSearchListener(new SearchBox.OnSearchListener() {
            @Override
            public int onSearch(EditText view, String oldInput, String newInput) {
                Toast.makeText(getContext(), oldInput + " - " + newInput, Toast.LENGTH_SHORT).show();
                return 1000;
            }
        });
        _searchBox.setOnEnterListener(new SearchBox.OnEnterListener() {
            @Override
            public void onEnter(EditText view, String oldInput, String newInput) {
                Toast.makeText(getContext(), "On Enter: " + newInput, Toast.LENGTH_SHORT).show();
            }
        });
        _searchBox.setOnToggleListener(new SearchBox.OnToggleListener() {
            @Override
            public void onToggle(boolean compactMode) {
                Toast.makeText(getContext(), "is compact mode: " + compactMode, Toast.LENGTH_SHORT).show();
            }
        });
    }
}