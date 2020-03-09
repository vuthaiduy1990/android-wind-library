package the.wind.library.sample.activity.fragment;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import the.wind.library.CWindColor;
import the.wind.library.sample.R;

public class ColorPage extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_color_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup _colorLayout = view.findViewById(R.id._colorLayout);
        Map<String, CWindColor> colors = new LinkedHashMap<>();
        colors.put("PRIMARY", CWindColor.PRIMARY);
        colors.put("SUCCESS", CWindColor.SUCCESS);
        colors.put("INFO", CWindColor.INFO);
        colors.put("HIGHLIGHT", CWindColor.HIGHLIGHT);
        colors.put("WARNING", CWindColor.WARNING);
        colors.put("NEUTRAL", CWindColor.NEUTRAL);
        colors.put("DANGER", CWindColor.DANGER);
        colors.put("LIGHT", CWindColor.LIGHT);
        colors.put("GRAY", CWindColor.GRAY);
        colors.put("DARK", CWindColor.DARK);
        colors.put("PURPLE", CWindColor.PURPLE);
        colors.put("PEA", CWindColor.PEA);

        for (Map.Entry<String, CWindColor> entry : colors.entrySet()) {
            EditText item = new EditText(view.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            int margin = (int) getResources().getDimension(R.dimen.com_margin_ver);
            params.setMargins(0, 0, 0, margin);
            item.setLayoutParams(params);
            int padding = (int) getResources().getDimension(R.dimen.com_text_pad_ver);
            item.setPadding(padding, padding, padding, padding);
            item.setText(entry.getKey());
            item.setBackgroundColor(entry.getValue().value());
            item.setEnabled(false);
            item.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.com_label_size));
            item.setGravity(Gravity.CENTER);
            item.setTextColor(ContextCompat.getColor(view.getContext(), R.color.textColor));
            _colorLayout.addView(item);
        }
    }
}