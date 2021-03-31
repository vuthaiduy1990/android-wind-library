package the.wind.library.sample.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import the.wind.library.anim.TextTypingFakulator;
import the.wind.library.sample.R;

public class TextPage extends Fragment {

    private TextTypingFakulator fakulator;
    private boolean loopTyping = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createTextTypingFakulator(view);
    }

    private void createTextTypingFakulator(View view) {
        TextView _textTypingView = view.findViewById(R.id._textTypingView);
        fakulator = new TextTypingFakulator(_textTypingView);
        fakulator.config("Text typing fakulator demo ...", "", 100);
        fakulator.setOnTypingListener(new TextTypingFakulator.OnTypingListener() {
            @Override
            public void onStart(TextTypingFakulator fakulator, TextView textView) {
                textView.setText("");
            }

            @Override
            public void onTyping(TextTypingFakulator fakulator, TextView textView) {

            }

            @Override
            public void onEnd(TextTypingFakulator fakulator, TextView textView) {
                if (loopTyping) {
                    fakulator.startDelay(100); // delay when start next typing
                }
            }
        });

        view.findViewById(R.id._startTypingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loopTyping = false;
                fakulator.start();
            }
        });
        view.findViewById(R.id._stopTypingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fakulator.stop();
                loopTyping = false;
            }
        });
        view.findViewById(R.id._loopTypingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loopTyping = true;
                fakulator.start();
            }
        });

    }
}