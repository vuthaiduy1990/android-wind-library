package the.wind.library.dialog;

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
public class YearSelectionDialog extends SelectionListDialog<Integer> {

    /**
     * Constructor
     *
     * @param context application context
     * @param mode    selection mode
     * @param dataset dataset
     */
    public YearSelectionDialog(@NonNull Context context, SelectionMode mode, List<Integer> dataset) {
        super(context, mode, dataset);
        headerLayout().setVisibility(View.GONE);
        setWidth((int) context.getResources().getDimension(R.dimen.wl_calendar_year_selection_dialog_width));
        setHeight((int) (CWAndroidUtils.getScreenSize(context).getHeight() * 0.8f));
        buttons().get(0).setType(Button.Type.GRAY_LIGHT);
    }

    /**
     * Constructor
     *
     * @param context application context
     * @param dataset dataset
     */
    public YearSelectionDialog(@NonNull Context context, List<Integer> dataset) {
        this(context, SelectionMode.SINGLE, dataset);
    }

    /**
     * Constructor
     *
     * @param context application context
     * @param mode    selection mode
     */
    public YearSelectionDialog(@NonNull Context context, SelectionMode mode) {
        this(context, mode, WindFactory.instance().getAvailableYears());
    }

    /**
     * Constructor
     *
     * @param context application context
     */
    public YearSelectionDialog(@NonNull Context context) {
        this(context, SelectionMode.SINGLE);
    }

    @Override
    protected String itemId(@NonNull Integer itemData) {
        return Integer.toString(itemData);
    }

    @Override
    protected String itemText(@NonNull Integer itemData) {
        return Integer.toString(itemData);
    }
}
