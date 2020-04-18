package the.wind.library.dialog;

import android.content.Context;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import the.wind.library.R;
import the.wind.library.view.Button;

public class NotificationTemplate implements WindDialog.ITemplate {

    // singleton instance
    private static final NotificationTemplate _instance = new NotificationTemplate();

    /**
     * Private constructor
     * Eliminate initiating template instance from outside
     */
    private NotificationTemplate() {
    }

    @Override
    public void onSetting(final WindDialog dialog) {
        Context context = dialog.getContext();
        // https://lottiefiles.com/15268-notification
        dialog.setLottieIcon(R.raw.wl_dialog_icon_notification);
        LottieAnimationView icon = ((LottieAnimationView) dialog.icon());
        icon.setRepeatCount(LottieDrawable.INFINITE);
        icon.setMaxProgress(40f / 48f);
        dialog.addButton(Button.Type.GRAY, context.getString(R.string.wl_dialog_button_close), null);
    }

    /**
     * get singleton instance
     *
     * @return singleton instance
     */
    public static NotificationTemplate instance() {
        return _instance;
    }
}
