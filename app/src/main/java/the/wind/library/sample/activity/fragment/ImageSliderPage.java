package the.wind.library.sample.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import the.wind.library.dialog.IntroductionDialog;
import the.wind.library.sample.R;
import the.wind.library.utils.CWAndroidUtils;
import the.wind.library.view.ImageSlider;

public class ImageSliderPage extends Fragment {

    private final ImageSlider.Data[] dataset = new ImageSlider.Data[]{
            new ImageSlider.Data(R.drawable.illustration_1, R.string.illustration_title, R.string.illustration_text),
            new ImageSlider.Data(R.drawable.illustration_2, 0, 0),
            new ImageSlider.Data(R.drawable.illustration_3, R.string.illustration_title, R.string.illustration_text),
            new ImageSlider.Data(R.drawable.illustration_4, R.string.illustration_title, R.string.illustration_text),
            new ImageSlider.Data(R.drawable.illustration_5, R.string.illustration_title, R.string.illustration_text),
            new ImageSlider.Data(R.drawable.illustration_6, R.string.illustration_title, R.string.illustration_text),
            new ImageSlider.Data(R.drawable.illustration_7, R.string.illustration_title, R.string.illustration_text),
    };

    private IntroductionDialog introDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_slider, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createImageSlider(view);
        createIntroductionDialog(view);
    }

    private void createImageSlider(View rootView) {
        ImageSlider _imageSlider = rootView.findViewById(R.id._imageSlider);
        _imageSlider.setDataset(dataset);
        _imageSlider.setCurrentItem(0);
    }

    private void createIntroductionDialog(View rootView) {
        introDialog = new IntroductionDialog(rootView.getContext());
        introDialog.setDataset(dataset);
        int height = CWAndroidUtils.getScreenSize(rootView.getContext()).getHeight();
        introDialog.setMaxHeight((int) (height * 0.75f));
        introDialog.setOffScreen(1);
        introDialog.setMaxDots(15);
    }

    public void showDialog() {
        introDialog.show();
    }
}