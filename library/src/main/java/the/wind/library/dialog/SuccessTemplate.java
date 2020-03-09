package the.wind.library.dialog;

import android.content.Context;

import the.wind.library.R;
import the.wind.library.view.Button;

public class SuccessTemplate implements WindDialog.ITemplate {

    // singleton instance
    private static final SuccessTemplate _instance = new SuccessTemplate();

    /**
     * Private constructor
     * Eliminate initiating template instance from outside
     */
    private SuccessTemplate() {
    }

    @Override
    public void onSetting(final WindDialog dialog) {
        Context context = dialog.getContext();
        // https://lottiefiles.com/2492-check
        dialog.setLottieIcon(R.raw.wind_dialog_icon_success);
        dialog.addButton(Button.Type.GRAY, context.getString(R.string.wind_dialog_button_close), null);
    }

    /**
     * get singleton instance
     *
     * @return singleton instance
     */
    public static SuccessTemplate instance() {
        return _instance;
    }
}
