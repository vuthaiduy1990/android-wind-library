package the.wind.library.dialog;

import android.content.Context;

import the.wind.library.R;
import the.wind.library.view.Button;

public class InfoTemplate implements WindDialog.ITemplate {

    // singleton instance
    private static final InfoTemplate _instance = new InfoTemplate();

    /**
     * Private constructor
     * Eliminate initiating template instance from outside
     */
    private InfoTemplate() {
    }

    @Override
    public void onSetting(final WindDialog dialog) {
        Context context = dialog.getContext();
        dialog.setIcon(R.drawable.wl_dialog_icon_info);
        dialog.addButton(Button.Type.GRAY, context.getString(R.string.wl_dialog_button_close), null);
    }

    /**
     * get singleton instance
     *
     * @return singleton instance
     */
    public static InfoTemplate instance() {
        return _instance;
    }
}
