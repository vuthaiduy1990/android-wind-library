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
import the.wind.library.dialog.SelectionListDialog;
import the.wind.library.model.CurrencyWrapper;
import the.wind.library.sample.R;

public class CustomFormDialogPage extends Fragment {

    private CurrencyDialog currencyDialog;
    private CurrencyWrapper selectedCurrency;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_form_dialog_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id._currencyDialogBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog(v.getContext()).show(selectedCurrency);
            }
        });
    }

    private CurrencyDialog getDialog(Context context) {
        if (currencyDialog == null) {
            currencyDialog = new CurrencyDialog(context) {
                @Override
                protected boolean onSelection(@NonNull SelectionListDialog<CurrencyWrapper> dialog, @NonNull View itemView, @NonNull CurrencyWrapper data) {
                    selectedCurrency = data;
                    Toast.makeText(getContext(), data.nlpRawText(getContext()), Toast.LENGTH_SHORT).show();
                    return false;
                }
            };
        }
        return currencyDialog;
    }
}