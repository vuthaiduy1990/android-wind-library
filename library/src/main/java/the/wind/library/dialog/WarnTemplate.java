package the.wind.library.dialog;

import android.content.Context;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import the.wind.library.R;
import the.wind.library.view.Button;

public class WarnTemplate implements WindDialog.ITemplate {

    // singleton instance
    private static final WarnTemplate _instance = new WarnTemplate();

    /**
     * Private constructor
     * Eliminate initiating template instance from outside
     */
    private WarnTemplate() {
    }

    @Override
    public void onSetting(final WindDialog dialog) {
        Context context = dialog.getContext();
        // https://lottiefiles.com/11124-error-icon
        dialog.setLottieIcon(R.raw.wl_dialog_icon_warn);
        LottieAnimationView icon = ((LottieAnimationView) dialog.icon());
        icon.setRepeatCount(LottieDrawable.INFINITE);
        icon.setMinProgress(5f / 80f);
        icon.setMaxProgress(1f);
        dialog.addButton(Button.Type.GRAY, context.getString(R.string.wl_dialog_button_cancel), null);
        dialog.addButton(Button.Type.WARNING, context.getString(R.string.wl_dialog_button_confirm), null);
    }

    /**
     * get singleton instance
     *
     * @return singleton instance
     */
    public static WarnTemplate instance() {
        return _instance;
    }
}
