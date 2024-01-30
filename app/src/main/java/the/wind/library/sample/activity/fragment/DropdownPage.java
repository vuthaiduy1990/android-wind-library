package the.wind.library.sample.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import the.wind.library.sample.R;
import the.wind.library.view.Dropdown;
import the.wind.library.view.DropdownAdapter;

public class DropdownPage extends Fragment {

    private Dropdown _dropdown1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dropdown, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // dropdown 1
        {
            _dropdown1 = view.findViewById(R.id._dropdown1);
            _dropdown1.setAdapter(createAdapter(view.getContext()));
        }

        // dropdown 2
        {
            Dropdown _dropdown2 = view.findViewById(R.id._dropdown2);
            DropdownAdapter<Color> adapter = createAdapter(view.getContext());
            adapter.setItemTextColor(ContextCompat.getColor(view.getContext(), the.wind.library.R.color.wl_white));
            adapter.setItemTextSize(the.wind.library.R.dimen.wl_text_big);
            adapter.setPopupTextSize(the.wind.library.R.dimen.wl_text_big);
            adapter.setPopupCheckboxSizeRestId(the.wind.library.R.dimen.wl_icon_big);
            adapter.setPopupItemBackground(the.wind.library.R.drawable.wl_button_background_highlight_light);
            _dropdown2.setAdapter(adapter);
            _dropdown2.setSelectedData(Color.One);
            _dropdown2.setOnItemSelectedListener(new Dropdown.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, Object item) {
                    Color color = (Color) item;
                    _dropdown1.setSelection(position);
                    Toast.makeText(getContext(), color.getText(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(getContext(), "Select nothing", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private DropdownAdapter<Color> createAdapter(Context context) {
        DropdownAdapter<Color> adapter = new DropdownAdapter<>(context, Arrays.asList(Color.values()));
        adapter.setDataTransformer(new DropdownAdapter.DataTransformer<Color>() {
            @Override
            public String dataToItemText(@NonNull Color itemData) {
                return itemData.name();
            }

            @Override
            public String dataToPopupText(@NonNull Color itemData) {
                return itemData.getText();
            }

            @Override
            public boolean compare(@NonNull Color a, @NonNull Color b) {
                return a.equals(b);
            }
        });
        return adapter;
    }

    public enum Color {
        ColorWithTheWind("Color of the wind"),
        Red("Red"),
        Yellow("Yellow"),
        One("Stuck with the known, fear the unknown");

        private final String text;

        Color(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}