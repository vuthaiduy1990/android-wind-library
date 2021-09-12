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
    private ViewHolderGenerator<T> vhGenerator;

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
        if (vhGenerator != null) {
            return vhGenerator.newViewHolder(parent);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wl_dialog_selection_list_item, parent, false);
        return new DefaultViewHolder<>(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WindRecycleView.ViewHolder<T> holder, int position) {
        if (holder instanceof SelectionListAdapter.ViewHolder) {
            ViewHolder<T> _holder = (ViewHolder<T>) holder;
            _holder.setDataTransformer(dataTransformer);
            _holder.setSelected(selectedData);
        }
        super.onBindViewHolder(holder, position);
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Set view holder generator
     *
     * @param generator view holder generator
     */
    public void setCustomViewHolderGenerator(ViewHolderGenerator<T> generator) {
        this.vhGenerator = generator;
    }

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
     * View holder generator
     *
     * @param <T> data template
     */
    public interface ViewHolderGenerator<T> {
        /**
         * Create new custom view holder
         *
         * @param parent parent view group
         * @return new view holder
         */
        ViewHolder<T> newViewHolder(ViewGroup parent);
    }

    /**
     * Data transformation
     *
     * @param <T> data template
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
     * View Holder
     *
     * @param <T>
     */
    public static class ViewHolder<T> extends WindRecycleView.ViewHolder<T> {

        protected DataTransformer<T> dataTransformer;
        protected T selectedData;

        /**
         * Constructor
         *
         * @param itemView item view
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        /**
         * Set data transformer
         *
         * @param transformer transformer
         */
        public void setDataTransformer(DataTransformer<T> transformer) {
            dataTransformer = transformer;
        }

        /**
         * Set selected item
         *
         * @param itemData item data
         */
        public void setSelected(T itemData) {
            selectedData = itemData;
        }
    }

    /**
     * Selection item view holder
     *
     * @param <T> item data type
     */
    public static class DefaultViewHolder<T> extends ViewHolder<T> {

        private final Checkbox _checkbox;
        private final TextView _textView;

        /**
         * Constructor
         *
         * @param itemView item view
         */
        public DefaultViewHolder(@NonNull View itemView) {
            super(itemView);
            _checkbox = itemView.findViewById(R.id._checkbox);
            _textView = itemView.findViewById(R.id._textView);
            itemView.setClickable(true);
            _checkbox.setEnabled(false);
        }

        @Override
        protected void bindData(T data) {
            super.bindData(data);
            _textView.setText(dataTransformer.dataToText(data));
            _checkbox.setChecked(selectedData != null && dataTransformer.compare(selectedData, data));
        }
    }
}
