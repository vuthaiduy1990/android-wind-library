package the.wind.library.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import the.wind.library.R;
import the.wind.library.view.Checkbox;
import the.wind.library.view.WindRecycleView;

public class SelectionListAdapter<T> extends WindRecycleView.Adapter<T> {

    private DataTransformer<T> dataTransformer;
    private final Map<String, T> selectorMap = new HashMap<>();
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
            _holder.setSelectors(selectorMap);
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
    public List<T> getSelectors() {
        return new ArrayList<>(selectorMap.values());
    }

    /**
     * Check if item is selected or not
     *
     * @param item item
     * @return true of item is selected
     */
    public boolean isSelectedItem(T item) {
        return selectorMap.get(dataTransformer.dataToId(item)) != null;
    }

    /**
     * Set selected item.
     * This function should called after user set data transformer
     *
     * @param items list of selected data
     */
    public void setSelectors(List<T> items) {
        selectorMap.clear();
        if (dataTransformer != null && items != null) {
            for (T item : items) {
                String id = dataTransformer.dataToId(item);
                selectorMap.put(id, item);
            }
        }
    }

    /**
     * Set selected item.
     * This function should called after user set data transformer
     *
     * @param item selected item
     */
    public void setSelector(T item) {
        selectorMap.clear();
        if (dataTransformer != null && item != null) {
            String id = dataTransformer.dataToId(item);
            selectorMap.put(id, item);
        }
    }

    /**
     * add selected item
     *
     * @param item item
     */
    public void addSelector(T item) {
        if (dataTransformer != null && item != null) {
            String id = dataTransformer.dataToId(item);
            selectorMap.put(id, item);
        }
    }

    /**
     * Remove selected item
     *
     * @param item selected item
     */
    public void removeSelector(T item) {
        if (dataTransformer != null && item != null) {
            String id = dataTransformer.dataToId(item);
            selectorMap.remove(id);
        }
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
         * Get item text
         *
         * @param itemData item data
         * @return item text
         */
        String dataToText(@NonNull T itemData);

        /**
         * retrieve item's id
         *
         * @return unique ID of item
         */
        String dataToId(@NonNull T itemData);
    }

    /**
     * View Holder
     *
     * @param <T>
     */
    public static class ViewHolder<T> extends WindRecycleView.ViewHolder<T> {

        protected DataTransformer<T> dataTransformer;
        protected Map<String, T> selectorMap;

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
         * @param selectorMap map between ID and selected item
         */
        public void setSelectors(Map<String, T> selectorMap) {
            this.selectorMap = selectorMap;
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
            // _checkbox.setEnabled(false);
            _checkbox.preventToSelect(v -> {
                OnItemClickListener<T> clickListener = getItemClickListener();
                if (clickListener != null) {
                    clickListener.onClick(DefaultViewHolder.this, v, getAdapterData());
                }
            });
        }

        @Override
        protected void bindData(T data) {
            super.bindData(data);
            _textView.setText(dataTransformer.dataToText(data));
            if (selectorMap != null) {
                _checkbox.setChecked(selectorMap.get(dataTransformer.dataToId(data)) != null);
            } else {
                _checkbox.setChecked(false);
            }
        }

        /**
         * Select item
         */
        public void select() {
            _checkbox.setChecked(true);
        }

        /**
         * Select item
         */
        public void unselect() {
            _checkbox.setChecked(false);
        }
    }
}
