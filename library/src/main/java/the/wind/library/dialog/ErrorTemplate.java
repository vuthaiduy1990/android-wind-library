package the.wind.library.dialog;

import android.content.Context;

import com.airbnb.lottie.LottieAnimationView;

import the.wind.library.R;
import the.wind.library.view.Button;

public class ErrorTemplate implements WindDialog.ITemplate {

    // singleton instance
    private static final ErrorTemplate _instance = new ErrorTemplate();

    /**
     * Private constructor
     * Eliminate initiating template instance from outside
     */
    private ErrorTemplate() {
    }

    @Override
    public void onSetting(final WindDialog dialog) {
        Context context = dialog.getContext();
        // https://lottiefiles.com/4970-unapproved-cross
        dialog.setLottieIcon(R.raw.wind_dialog_icon_error);
        LottieAnimationView icon = ((LottieAnimationView) dialog.icon());
        icon.setRepeatCount(1);
        icon.setMinProgress(7f / 89f);
        icon.setMaxProgress(75f / 89f);
        dialog.addButton(Button.Type.GRAY, context.getString(R.string.wind_dialog_button_close), null);
    }

    /**
     * get singleton instance
     *
     * @return singleton instance
     */
    public static ErrorTemplate instance() {
        return _instance;
    }
}
