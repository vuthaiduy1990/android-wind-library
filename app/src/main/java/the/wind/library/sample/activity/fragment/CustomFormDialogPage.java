package the.wind.library.sample.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import the.wind.library.dialog.CurrencyDialog;
import the.wind.library.dialog.LocaleDialog;
import the.wind.library.dialog.SelectionListDialog;
import the.wind.library.dialog.YearSelectionDialog;
import the.wind.library.model.CurrencyWrapper;
import the.wind.library.model.LocaleWrapper;
import the.wind.library.sample.R;

public class CustomFormDialogPage extends Fragment {

    // currency dialog
    private CurrencyDialog currencyDialog;
    private CurrencyWrapper selectedCurrency;

    // locale dialog
    private LocaleDialog localeDialog;
    private LocaleWrapper selectedLocale;

    // Year selection dialog
    private YearSelectionDialog yearSelectionDialog;
    private Integer selectedYear;

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

        // local button
        view.findViewById(R.id._localeBtn).setOnClickListener(v -> getLocaleDialog(v.getContext()).show(selectedLocale));

        // year selection dialog
        view.findViewById(R.id._yearSelectionBtn).setOnClickListener(v -> {
            getYearSelectionDialog(v.getContext()).show(selectedYear);
        });
    }

    private CurrencyDialog getCurrencyDialog(Context context) {
        if (currencyDialog == null) {
            currencyDialog = new CurrencyDialog(context) {
                @Override
                protected boolean onSelection(@NonNull SelectionListDialog<CurrencyWrapper> dialog, @NonNull View itemView, @NonNull CurrencyWrapper data) {
                    selectedCurrency = data;
                    Toast.makeText(getContext(), data.getDisplayText(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            };
        }
        return currencyDialog;
    }

    private LocaleDialog getLocaleDialog(Context context) {
        if (localeDialog == null) {
            localeDialog = new LocaleDialog(context) {
                @Override
                protected boolean onSelection(@NonNull SelectionListDialog<LocaleWrapper> dialog, @NonNull View itemView, @NonNull LocaleWrapper data) {
                    selectedLocale = data;
                    Toast.makeText(getContext(), data.getDisplayText(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            };
        }
        return localeDialog;
    }

    private YearSelectionDialog getYearSelectionDialog(Context context) {
        if (yearSelectionDialog == null) {
            yearSelectionDialog = new YearSelectionDialog(context) {
                @Override
                protected boolean onSelection(@NonNull SelectionListDialog<Integer> dialog, @NonNull View itemView, @NonNull Integer data) {
                    selectedYear = data;
                    Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
                    return false;
                }
            };
        }
        return yearSelectionDialog;
    }
}