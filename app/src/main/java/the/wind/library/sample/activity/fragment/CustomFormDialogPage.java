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
import the.wind.library.model.CurrencyWrapper;
import the.wind.library.model.LocaleWrapper;
import the.wind.library.sample.R;

public class CustomFormDialogPage extends Fragment {

    private CurrencyDialog currencyDialog;
    private CurrencyWrapper selectedCurrency;

    private LocaleDialog localeDialog;
    private LocaleWrapper selectedLocale;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_form_dialog_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // currency button
        view.findViewById(R.id._currencyDialogBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrencyDialog(v.getContext()).show(selectedCurrency);
            }
        });

        // local button
        view.findViewById(R.id._localeDialogBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocaleDialog(v.getContext()).show(selectedLocale);
            }
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
}