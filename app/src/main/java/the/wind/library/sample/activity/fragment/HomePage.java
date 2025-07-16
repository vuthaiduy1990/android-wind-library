package the.wind.library.sample.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import the.wind.library.sample.R;

public class HomePage extends Fragment {

    // underline span
    private final static UnderlineSpan UNDERLINE_SPAN = new UnderlineSpan();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            view.findViewById(R.id._footerText1).setOnClickListener(v -> {
                openBambooNoteApp(v.getContext());
            });
            TextView _footerText2 = view.findViewById(R.id._footerText2);
            String text1 = getString(R.string.bamboo_note);
            SpannableString span = new SpannableString(text1);
            span.setSpan(UNDERLINE_SPAN, 0, text1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            _footerText2.setText(span, TextView.BufferType.NORMAL);
            _footerText2.setOnClickListener(v -> {
                openBambooNoteApp(v.getContext());
            });
        }

        {
            View _icon1 = view.findViewById(R.id._icon1);
            _icon1.setOnClickListener(v -> {
                openBambooNoteApp(v.getContext());
            });
            view.findViewById(R.id._downloadBtn).setOnClickListener(v -> {
                openBambooNoteApp(v.getContext());
            });
        }

    }

    /**
     * Open this application page on play store
     *
     * @param context application context
     */
    public static void openBambooNoteApp(Context context) {
        final String appPackageName = "the.wind.bamboo.note";
        Uri uri;
        try {
            uri = Uri.parse("market://details?id=" + appPackageName);
        } catch (Exception ex) {
            uri = Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName);
        }
        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}