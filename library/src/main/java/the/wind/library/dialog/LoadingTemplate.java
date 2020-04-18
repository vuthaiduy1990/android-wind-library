package the.wind.library.dialog;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import the.wind.library.R;

public class LoadingTemplate implements WindDialog.ITemplate {

    // singleton instance
    private static final LoadingTemplate _instance = new LoadingTemplate();

    /**
     * Private constructor
     * Eliminate initiating template instance from outside
     */
    private LoadingTemplate() {
    }

    @Override
    public void onSetting(final WindDialog dialog) {
        // https://lottiefiles.com/627-loading-success-failed
        dialog.setLottieIcon(R.raw.wl_dialog_icon_waiting);
        LottieAnimationView icon = ((LottieAnimationView) dialog.icon());
        icon.setRepeatCount(LottieDrawable.INFINITE);
        icon.setMaxProgress(310f / 841f);
    }

    /**
     * get singleton instance
     *
     * @return singleton instance
     */
    public static LoadingTemplate instance() {
        return _instance;
    }
}
