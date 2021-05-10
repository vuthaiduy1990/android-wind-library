package the.wind.library.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import the.wind.library.R;
import the.wind.library.view.Checkbox;
import the.wind.library.view.WindRecycleView;

public class SelectionListAdapter<T> extends WindRecycleView.Adapter<T> {

    private DataTransformer<T> dataTransformer;
    private T selectedData;

    /**
     * Constructor
     *
     * @param dataset list of item data
     */
    public SelectionListAdapter(List<T> dataset) {
        super(dataset);
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @NonNull
    @Override
    public WindRecycleView.ViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wl_dialog_selection_list_item, parent, false);
        return new ViewHolder<>(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WindRecycleView.ViewHolder<T> holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof ViewHolder) {
            ViewHolder<T> _holder = (ViewHolder<T>) holder;
            T data = getData(position);
            _holder._textView.setText(dataTransformer.dataToText(data));
            _holder.bindChecked(selectedData != null && dataTransformer.compare(selectedData, data));
        }
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Set data transformer
     *
     * @param transformer transformer
     * @return data transformer
     */
    public SelectionListAdapter<T> setDataTransformer(DataTransformer<T> transformer) {
        dataTransformer = transformer;
        return this;
    }

    /**
     * @return selected data
     */
    public T getSelected() {
        return selectedData;
    }

    /**
     * Set selected item
     *
     * @param itemData item data
     */
    public void setSelected(T itemData) {
        selectedData = itemData;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Data transformation
     */
    public interface DataTransformer<T> {

        /**
         * get item text
         *
         * @param itemData item data
         * @return item text
         */
        String dataToText(@NonNull T itemData);

        /**
         * Compare two item data
         *
         * @param a item data 1
         * @param b item data 2
         * @return true if equal
         */
        boolean compare(@NonNull T a, @NonNull T b);
    }

    /**
     * Selection item view holder
     *
     * @param <T> item data type
     */
    public static class ViewHolder<T> extends WindRecycleView.ViewHolder<T> {

        private final Checkbox _checkbox;
        private final TextView _textView;

        /**
         * Constructor
         *
         * @param itemView item view
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _checkbox = itemView.findViewById(R.id._checkbox);
            _textView = itemView.findViewById(R.id._textView);
            itemView.setClickable(true);
            _checkbox.setEnabled(false);
        }

        /**
         * Bind selected value
         *
         * @param checked true -> checked, else unchecked
         */
        private void bindChecked(boolean checked) {
            _checkbox.setChecked(checked);
        }
    }
}
