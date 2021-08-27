package the.wind.library.dialog;

import android.content.Context;

import the.wind.library.R;
import the.wind.library.view.Button;

public class ConfirmTemplate implements WindDialog.ITemplate {

    // singleton instance
    private static final ConfirmTemplate _instance = new ConfirmTemplate();

    /**
     * Private constructor
     * Eliminate initiating template instance from outside
     */
    private ConfirmTemplate() {
    }

    @Override
    public void onSetting(final WindDialog dialog) {
        Context context = dialog.getContext();
        // https://www.flaticon.com/free-icon/completed-task_1632670#
        dialog.setIcon(R.drawable.wl_dialog_icon_confirm);
        dialog.addButton(Button.Type.GRAY, context.getString(R.string.wl_cancel), null);
        dialog.addButton(Button.Type.PRIMARY, context.getString(R.string.wl_yes), null);
    }

    /**
     * get singleton instance
     *
     * @return singleton instance
     */
    public static ConfirmTemplate instance() {
        return _instance;
    }
}
