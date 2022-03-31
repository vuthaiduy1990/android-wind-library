package the.wind.library.sample.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import the.wind.library.dialog.CurrencyDialog;
import the.wind.library.dialog.LocaleDialog;
import the.wind.library.dialog.SelectionListDialog;
import the.wind.library.dialog.TimezoneDialog;
import the.wind.library.dialog.YearMonthSelectionDialog;
import the.wind.library.dialog.YearSelectionDialog;
import the.wind.library.model.CurrencyWrapper;
import the.wind.library.model.LocaleWrapper;
import the.wind.library.model.TimezoneWrapper;
import the.wind.library.sample.R;

public class CustomFormDialogPage extends Fragment {

    // currency dialog
    private CurrencyDialog currencyDialog;
    private CurrencyWrapper selectedCurrency;

    // multiple selection list dialog
    private CurrencyDialog multiCurrencyDialog;
    private Collection<CurrencyWrapper> multiSelectedCurrencies;

    // locale dialog
    private LocaleDialog localeDialog;
    private LocaleWrapper selectedLocale;

    // Timezone dialog
    private TimezoneDialog timezoneDialog;
    private TimezoneWrapper selectedTimeZone;

    // Year selection dialog
    private YearSelectionDialog yearSelectionDialog;
    private Integer selectedYear;

    // year month selection dialog
    private YearMonthSelectionDialog yearMonthSelectionDialog;
    private final Integer[] selectedYearMonth = new Integer[]{null, null};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_form_dialog_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // currency button
        view.findViewById(R.id._currencyBtn).setOnClickListener(v -> getCurrencyDialog(v.getContext()).show(selectedCurrency));

        // multiple currency button
        view.findViewById(R.id._multipleCurrencyBtn).setOnClickListener(v -> getMultiSelectCurrencyDialog(v.getContext()).show(multiSelectedCurrencies));

        // local button
        view.findViewById(R.id._localeBtn).setOnClickListener(v -> getLocaleDialog(v.getContext()).show(selectedLocale));

        // timezone button
        view.findViewById(R.id._timezoneBtn).setOnClickListener(v -> getTimezoneDialog(v.getContext()).show(selectedTimeZone));

        // year selection dialog
        view.findViewById(R.id._yearSelectionBtn).setOnClickListener(v -> {
            getYearSelectionDialog(v.getContext()).show(selectedYear);
        });

        // year month selection list dialog
        view.findViewById(R.id._yearMonthSelectionBtn).setOnClickListener(v -> {
            getYearMonthSelectionDialog(v.getContext()).show(selectedYearMonth[0], selectedYearMonth[1]);
        });
    }

    private CurrencyDialog getCurrencyDialog(Context context) {
        if (currencyDialog == null) {
            currencyDialog = new CurrencyDialog(context);
            currencyDialog.setOnItemSelectionListener(new SelectionListDialog.OnItemSelectionListener<CurrencyWrapper>() {
                @Override
                public boolean onSelect(@NonNull SelectionListDialog<CurrencyWrapper> dialog, @NonNull View itemView, @NonNull CurrencyWrapper data) {
                    selectedCurrency = data;
                    Toast.makeText(getContext(), data.getDisplayText(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
        return currencyDialog;
    }

    private CurrencyDialog getMultiSelectCurrencyDialog(Context context) {
        if (multiCurrencyDialog == null) {
            multiCurrencyDialog = new CurrencyDialog(context, SelectionListDialog.SelectionMode.MULTIPLE);
            multiCurrencyDialog.setOnMultiSelectionListener(new SelectionListDialog.OnMultiSelectionListener<CurrencyWrapper>() {
                @Override
                public boolean onSelect(@NonNull SelectionListDialog<CurrencyWrapper> dialog, Collection<CurrencyWrapper> items) {
                    multiSelectedCurrencies = new ArrayList<>(items);
                    Toast.makeText(getContext(), items.size() + " items selected", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
        return multiCurrencyDialog;
    }

    private LocaleDialog getLocaleDialog(Context context) {
        if (localeDialog == null) {
            localeDialog = new LocaleDialog(context);
            localeDialog.setOnItemSelectionListener(new SelectionListDialog.OnItemSelectionListener<LocaleWrapper>() {
                @Override
                public boolean onSelect(@NonNull SelectionListDialog<LocaleWrapper> dialog, @NonNull View itemView, @NonNull LocaleWrapper data) {
                    selectedLocale = data;
                    Toast.makeText(getContext(), data.getDisplayText(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
        return localeDialog;
    }

    private TimezoneDialog getTimezoneDialog(Context context) {
        if (timezoneDialog == null) {
            timezoneDialog = new TimezoneDialog(context);
            timezoneDialog.setOnItemSelectionListener(new SelectionListDialog.OnItemSelectionListener<TimezoneWrapper>() {
                @Override
                public boolean onSelect(@NonNull SelectionListDialog<TimezoneWrapper> dialog, @NonNull View itemView, @NonNull TimezoneWrapper data) {
                    selectedTimeZone = data;
                    Toast.makeText(getContext(), data.getCode() + ": " + data.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
        return timezoneDialog;
    }

    private YearSelectionDialog getYearSelectionDialog(Context context) {
        if (yearSelectionDialog == null) {
            yearSelectionDialog = new YearSelectionDialog(context);
            yearSelectionDialog.setOnItemSelectionListener(new SelectionListDialog.OnItemSelectionListener<Integer>() {
                @Override
                public boolean onSelect(@NonNull SelectionListDialog<Integer> dialog, @NonNull View itemView, @NonNull Integer data) {
                    selectedYear = data;
                    Toast.makeText(getContext(), Integer.toString(data), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
        return yearSelectionDialog;
    }

    private YearMonthSelectionDialog getYearMonthSelectionDialog(Context context) {
        if (yearMonthSelectionDialog == null) {
            yearMonthSelectionDialog = new YearMonthSelectionDialog(context) {
                @Override
                protected boolean onSelection(@NonNull YearMonthSelectionDialog dialog, int year, int month) {
                    selectedYearMonth[0] = year;
                    selectedYearMonth[1] = month;
                    Toast.makeText(getContext(), Integer.toString(year) + "-" + Integer.toString(month + 1), Toast.LENGTH_SHORT).show();
                    return false;
                }
            };
        }
        return yearMonthSelectionDialog;
    }
}