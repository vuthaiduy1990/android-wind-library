package the.wind.library.dialog;

import android.content.Context;

import the.wind.library.R;
import the.wind.library.view.Button;

public class SimpleTaskTemplate implements WindDialog.ITemplate {

    // singleton instance
    private static final SimpleTaskTemplate _instance = new SimpleTaskTemplate();

    /**
     * Private constructor
     * Eliminate initiating template instance from outside
     */
    private SimpleTaskTemplate() {
    }

    @Override
    public void onSetting(final WindDialog dialog) {
        Context context = dialog.getContext();
        // https://www.flaticon.com/free-icon/completed-task_1632670#
        dialog.setIcon(R.drawable.wl_dialog_icon_task);
        dialog.addButton(Button.Type.GRAY, context.getString(R.string.wl_dialog_button_cancel), null);
        dialog.addButton(Button.Type.PRIMARY, context.getString(R.string.wl_dialog_button_confirm), null);
    }

    /**
     * get singleton instance
     *
     * @return singleton instance
     */
    public static SimpleTaskTemplate instance() {
        return _instance;
    }
}
