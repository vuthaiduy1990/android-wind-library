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
import the.wind.library.WindColor;
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
        Map<String, WindColor> colors = new LinkedHashMap<>();
        colors.put("PRIMARY", WindColor.PRIMARY);
        colors.put("SUCCESS", WindColor.SUCCESS);
        colors.put("INFO", WindColor.INFO);
        colors.put("HIGHLIGHT", WindColor.HIGHLIGHT);
        colors.put("WARNING", WindColor.WARNING);
        colors.put("NEUTRAL", WindColor.NEUTRAL);
        colors.put("DANGER", WindColor.DANGER);
        colors.put("LIGHT", WindColor.LIGHT);
        colors.put("GRAY", WindColor.GRAY);
        colors.put("DARK", WindColor.DARK);
        colors.put("PURPLE", WindColor.PURPLE);
        colors.put("PEA", WindColor.PEA);

        for (Map.Entry<String, WindColor> entry : colors.entrySet()) {
            EditText item = new EditText(view.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            int margin = (int) getResources().getDimension(R.dimen.wl_padding_ver_big);
            params.setMargins(0, 0, 0, margin);
            item.setLayoutParams(params);
            int padding = (int) getResources().getDimension(R.dimen.wl_padding_ver);
            item.setPadding(padding, padding, padding, padding);
            item.setText(entry.getKey());
            item.setBackgroundColor(entry.getValue().value());
            item.setEnabled(false);
            item.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.wl_text_big));
            item.setGravity(Gravity.CENTER);
            item.setTextColor(ContextCompat.getColor(view.getContext(), R.color.textColor));
            _colorLayout.addView(item);
        }
    }
}