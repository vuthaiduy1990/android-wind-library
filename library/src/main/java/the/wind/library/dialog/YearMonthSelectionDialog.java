package the.wind.library.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import the.wind.library.R;
import the.wind.library.WindFactory;
import the.wind.library.utils.CWAndroidUtils;
import the.wind.library.view.Button;
import the.wind.library.view.Dropdown;
import the.wind.library.view.DropdownAdapter;

/**
 * Allow to select both year and month
 */
public abstract class YearMonthSelectionDialog extends WindDialog {

    // views
    private final TextView _yearLabeView;
    private final Dropdown _yearSelectionView;
    private final TextView _monthLabelView;
    private final Dropdown _monthSelectionView;

    // adapter
    private final DropdownAdapter<Integer> yearAdapter;
    private final DropdownAdapter<Integer> monthAdapter;

    // data
    private final Calendar solarCal = new GregorianCalendar();
    private Integer selectedYear;
    private Integer selectedMonth;

    /**
     * Constructor
     *
     * @param context activity context
     */
    public YearMonthSelectionDialog(@NonNull Context context) {
        super(context, LayoutType.FUBUKI);
        headerLayout().setVisibility(View.GONE);
        setContentView(R.layout.wl_dialog_year_month_selection);
        setInOutAnimType(InOutAnimType.SWEET_ALERT);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        View contentView = contentView();
        int screenHeight = CWAndroidUtils.getScreenSize(context).getHeight();
        setGravity(Gravity.TOP);
        setMarginTop((int) (screenHeight * 0.3f));

        // add action button
        addButton(Button.Type.GRAY_LIGHT, context.getString(R.string.wl_close), null).setOnClickListener(v -> {
            dismiss();
        });
        Button saveBtn = addButton(Button.Type.GRAY_LIGHT, context.getString(R.string.wl_ok), null);
        saveBtn.setOnClickListener(v -> {
            if (!onSelection(YearMonthSelectionDialog.this, selectedYear, selectedMonth)) {
                dismiss();
            }
        });
        saveBtn.setTextColor(ContextCompat.getColor(context, R.color.wl_success));

        // bind event
        setOnDismissListener(this::onDialogDismiss);

        // bind year selection view
        _yearLabeView = contentView.findViewById(R.id._yearLabelView);
        _yearSelectionView = contentView.findViewById(R.id._yearSelectionView);
        yearAdapter = new DropdownAdapter<>(context, WindFactory.instance().getAvailableYears());
        // data transformer
        yearAdapter.setDataTransformer(new DropdownAdapter.DataTransformer<Integer>() {
            @Override
            public String dataToItemText(@NonNull Integer itemData) {
                return Integer.toString(itemData);
            }

            @Override
            public String dataToPopupText(@NonNull Integer itemData) {
                return Integer.toString(itemData);
            }

            @Override
            public boolean compare(@NonNull Integer a, @NonNull Integer b) {
                return a.equals(b);
            }
        });
        _yearSelectionView.setAdapter(yearAdapter);
        _yearSelectionView.setOnItemSelectedListener(new Dropdown.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, Object item) {
                selectedYear = (Integer) item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // bind month selection view
        _monthLabelView = contentView.findViewById(R.id._monthLabelView);
        _monthSelectionView = contentView.findViewById(R.id._monthSelectionView);
        monthAdapter = new DropdownAdapter<>(context, WindFactory.instance().getAvailableMonths());
        monthAdapter.setDataTransformer(new DropdownAdapter.DataTransformer<Integer>() {
            @Override
            public String dataToItemText(@NonNull Integer itemData) {
                return Integer.toString(itemData + 1);
            }

            @Override
            public String dataToPopupText(@NonNull Integer itemData) {
                return Integer.toString(itemData + 1);
            }

            @Override
            public boolean compare(@NonNull Integer a, @NonNull Integer b) {
                return a.equals(b);
            }
        });
        _monthSelectionView.setAdapter(monthAdapter);
        _monthSelectionView.setOnItemSelectedListener(new Dropdown.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, Object item) {
                selectedMonth = (Integer) item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /**
     * Trigger when user press OK button
     *
     * @param dialog selection dialog
     * @param year   selected year value
     * @param month  selected month value
     * @return true if consume the event, else return false
     */
    protected abstract boolean onSelection(@NonNull YearMonthSelectionDialog dialog, int year, int month);

    /**
     * On dismiss
     *
     * @param dialog dialog
     */
    protected void onDialogDismiss(@NonNull DialogInterface dialog) {

    }

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Set year label
     *
     * @param resId label resource id
     */
    public void setYearLabel(@StringRes int resId) {
        _yearLabeView.setText(resId);
    }

    /**
     * Set month label
     *
     * @param resId label resource id
     */
    public void setMonthLabel(@StringRes int resId) {
        _monthLabelView.setText(resId);
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Show dialog with given selected year and month
     *
     * @param year  year
     * @param month month
     */
    public void show(@Nullable Integer year, @Nullable Integer month) {
        if (year == null || month == null) {
            solarCal.setTime(new Date());
        }
        _yearSelectionView.setSelectedData(year != null ? year : solarCal.get(Calendar.YEAR));
        _monthSelectionView.setSelectedData(month != null ? month : solarCal.get(Calendar.MONTH));
        show();
    }

    /**
     * show dialog with given date
     *
     * @param date date
     */
    public void show(Date date) {
        solarCal.setTime(date);
        show(solarCal.get(Calendar.YEAR), solarCal.get(Calendar.MONTH));
    }

    /* ---------------------- INNER CLASS -------------------- */
}
