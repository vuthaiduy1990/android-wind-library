package the.wind.library.sample.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import the.wind.library.sample.R;
import the.wind.library.view.Button;

public class ButtonPage extends Fragment {

    private int state;
    private ScaleAnimation anim;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_button_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button _eventButton = view.findViewById(R.id._eventButton);
        _eventButton.setTextColor(ContextCompat.getColor(view.getContext(), R.color.button_text));

        anim = new ScaleAnimation(
                0.5f, 1f, 0.5f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        anim.setRepeatMode(Animation.RESTART);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(800);

        _eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 0) /* animate icon */ {
                    state = 1;
                    _eventButton.setIconVisible(true);
                    _eventButton.icon().startAnimation(anim);

                } else if (state == 1) /*hide text*/ {
                    state = 2;
                    _eventButton.icon().clearAnimation();
                    _eventButton.setTextVisible(false);
                } else if (state == 2) /*show text again*/ {
                    state = 3;
                    _eventButton.setTextVisible(true);
                } else /* hide icon */ {
                    state = 0;
                    _eventButton.setIconVisible(false);
                }
            }
        });
    }
}