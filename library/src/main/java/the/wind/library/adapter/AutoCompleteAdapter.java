package the.wind.library.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import the.wind.library.CWCallback;
import the.wind.library.R;
import the.wind.library.nlp.CWNLPEngine;
import the.wind.library.nlp.INLPText;
import the.wind.library.nlp.NLPMatchResult;

/**
 * Define auto complete adapter
 *
 * @param <T> data type
 */
public class AutoCompleteAdapter<T extends INLPText> extends ArrayAdapter<T> implements Filterable {

    // NLP engine
    private final CWNLPEngine<T> nlpEngine;
    // Application context
    private Context context;
    // Filter resultW
    private final List<T> filterResult = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context   application context
     * @param nlpEngine application context
     */
    public AutoCompleteAdapter(@NonNull Context context, CWNLPEngine<T> nlpEngine) {
        super(context, R.layout.wl_autocomplete_item, R.id._textView);
        this.nlpEngine = nlpEngine;
    }

    @Override
    public int getCount() {
        return filterResult.size();
    }

    @Nullable
    @Override
    public T getItem(int position) {
        return filterResult.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = super.getView(position, convertView, parent);
        T data = getItem(position);
        if (data == null) return itemView;

        TextView _textView = itemView.findViewById(R.id._textView);
        _textView.setText(data.nlpRawText(getContext()));
        return itemView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence text) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence text, FilterResults results) {
                if (text == null) return;
                nlpEngine.doMatching(text.toString(), new CWCallback<NLPMatchResult<T>>() {
                    @Override
                    public void onBegin() {
                        super.onBegin();
                        filterResult.clear();
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        notifyDataSetChanged();
                    }

                    @Override
                    public NLPMatchResult<T> onSuccess(NLPMatchResult<T> result) {
                        if (result != null) {
                            filterResult.add(result.target);
                        }
                        return super.onSuccess(result);
                    }
                });
            }
        };
    }
}
