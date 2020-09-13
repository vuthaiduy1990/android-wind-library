package the.wind.library.dialog;

import android.content.Context;

import the.wind.library.R;
import the.wind.library.view.Button;

public class SupportTemplate implements WindDialog.ITemplate {

    // singleton instance
    private static final SupportTemplate _instance = new SupportTemplate();

    /**
     * Private constructor
     * Eliminate initiating template instance from outside
     */
    private SupportTemplate() {
    }

    @Override
    public void onSetting(final WindDialog dialog) {
        Context context = dialog.getContext();
        dialog.setIcon(R.drawable.wl_dialog_icon_idea);
        dialog.addButton(Button.Type.GRAY, context.getString(R.string.wl_dialog_button_close), null);
    }

    /**
     * get singleton instance
     *
     * @return singleton instance
     */
    public static SupportTemplate instance() {
        return _instance;
    }
}
