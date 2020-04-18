package the.wind.library.dialog;

import android.content.Context;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import the.wind.library.R;

public class ProgressTemplate implements WindDialog.ITemplate {

    // singleton instance
    private static final ProgressTemplate _instance = new ProgressTemplate();

    /**
     * Private constructor
     * Eliminate initiating template instance from outside
     */
    private ProgressTemplate() {
    }

    @Override
    public void onSetting(final WindDialog dialog) {
        Context context = dialog.getContext();
        dialog.setTitleVisible(false);
        dialog.setContentViewVisible(false);
        dialog.setFooterVisible(false);

        // configure waiting icon
        // https://lottiefiles.com/627-loading-success-failed
        dialog.setLottieIcon(R.raw.wl_dialog_icon_waiting);
        LottieAnimationView icon = ((LottieAnimationView) dialog.icon());
        icon.setRepeatCount(LottieDrawable.INFINITE);
        icon.setMaxProgress(310f / 841f);
        int size = (int) context.getResources().getDimension(R.dimen.wl_dialog_waiting_icon_size);
        int margin = (int) context.getResources().getDimension(R.dimen.wl_dialog_tatsumaki_big_progress_icon_margin);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) icon.getLayoutParams();
        params.width = size;
        params.height = size;
        params.setMargins(0, margin, 0, margin);
        icon.setLayoutParams(params);
    }

    /**
     * get singleton instance
     *
     * @return singleton instance
     */
    public static ProgressTemplate instance() {
        return _instance;
    }
}
