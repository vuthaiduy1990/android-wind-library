package the.wind.library.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.util.List;

import androidx.annotation.NonNull;
import the.wind.library.R;
import the.wind.library.WindFactory;
import the.wind.library.utils.CWAndroidUtils;
import the.wind.library.view.Button;

/**
 * Year selection dialog
 */
public abstract class YearSelectionDialog extends SelectionListDialog<Integer> {

    /**
     * Constructor
     *
     * @param context application context
     * @param dataset dataset
     */
    public YearSelectionDialog(@NonNull Context context, List<Integer> dataset) {
        super(context, dataset);
        headerLayout().setVisibility(View.GONE);
        setWidth((int) context.getResources().getDimension(R.dimen.wl_calendar_year_selection_dialog_width));
        if (context instanceof Activity) {
            setHeight((int) (CWAndroidUtils.getScreenSize(context).getHeight() * 0.8f));
        }
        buttons().get(0).setType(Button.Type.GRAY_LIGHT);
    }

    /**
     * Constructor
     *
     * @param context application context
     */
    public YearSelectionDialog(@NonNull Context context) {
        this(context, WindFactory.instance().getAvailableYears());
    }

    @Override
    protected boolean equal(@NonNull Integer a, @NonNull Integer b) {
        return a.equals(b);
    }

    @Override
    protected String itemText(@NonNull Integer itemData) {
        return Integer.toString(itemData);
    }
}
