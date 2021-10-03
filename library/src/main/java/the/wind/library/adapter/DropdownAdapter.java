package the.wind.library.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import the.wind.library.R;
import the.wind.library.view.Checkbox;

public class DropdownAdapter<T> extends ArrayAdapter<T> {

    private final LayoutInflater inflater;
    private @LayoutRes
    final int itemViewLayoutId;
    private @LayoutRes
    final int popupItemViewLayoutId;

    // style
    private int popupItemBackground;
    private int itemTextColor;
    private float itemTextSize;
    private int popupTextTextColor;
    private float popupTextSize;
    private int popupCheckboxSize;

    // data
    private int selectedPosition;
    private DataTransformer<T> dataTransformer;

    /**
     * Constructor
     *
     * @param context activity context
     * @param dataset dataset
     */
    public DropdownAdapter(@NonNull Context context, @NonNull List<T> dataset) {
        this(context, R.layout.wl_dropdown_item_view, dataset);
    }

    /**
     * Constructor
     *
     * @param context        activity context
     * @param itemViewLayout item view resource layout
     * @param dataset        dataset
     */
    public DropdownAdapter(@NonNull Context context, @LayoutRes int itemViewLayout, @NonNull List<T> dataset) {
        super(context, itemViewLayout, dataset);
        this.itemViewLayoutId = itemViewLayout;
        inflater = LayoutInflater.from(context);
        popupItemViewLayoutId = R.layout.wl_dropdown_popup_item_view;
        setDropDownViewResource(popupItemViewLayoutId);

        Resources res = getContext().getResources();
        int defaultTextColor = ContextCompat.getColor(getContext(), R.color.wl_text);

        // default value for item text view
        setItemTextColor(defaultTextColor);
        setItemTextSize(R.dimen.wl_text);

        // default value for popup item view
        setPopupItemBackground(R.drawable.wl_background_hover_pressed);
        setPopupTextTextColor(defaultTextColor);
        setPopupTextSize(R.dimen.wl_text_small);
        setPopupCheckboxSizeRestId(R.dimen.wl_icon_small);
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // For dropbox item view
        T item = getItem(position);
        View view;
        if (convertView == null) {
            view = inflater.inflate(itemViewLayoutId, parent, false);
        } else {
            view = convertView;
        }
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setTextColor(itemTextColor);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemTextSize);
            textView.setText(dataTransformer.dataToItemText(item));
            return view;
        }
        throw new IllegalStateException("ArrayAdapter requires the resource ID to be a TextView");
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // For dropbox popup item view
        T item = getItem(position);
        View view;
        if (convertView == null) {
            view = inflater.inflate(popupItemViewLayoutId, parent, false);
        } else {
            view = convertView;
        }
        view.setBackgroundResource(popupItemBackground);

        // bind text value
        {
            TextView _textView = view.findViewById(R.id._textView);
            _textView.setTextColor(popupTextTextColor);
            _textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, popupTextSize);
            _textView.setText(dataTransformer.dataToPopupText(item));
        }

        // bind checkbox value
        {
            Checkbox _checkbox = view.findViewById(R.id._checkbox);
            ViewGroup.LayoutParams lp = _checkbox.getLayoutParams();
            lp.width = popupCheckboxSize;
            lp.height = popupCheckboxSize;
            _checkbox.setChecked(selectedPosition == position);
        }
        return view;
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Set item text color
     *
     * @param color text color
     */
    public void setItemTextColor(@ColorInt int color) {
        itemTextColor = color;
    }

    /**
     * Set item text size
     *
     * @param sizeResId text size resource id
     */
    public void setItemTextSize(@DimenRes int sizeResId) {
        Resources res = getContext().getResources();
        itemTextSize = res.getDimension(sizeResId);
    }

    /**
     * Set popup text color
     *
     * @param color text color
     */
    public void setPopupTextTextColor(@ColorInt int color) {
        popupTextTextColor = color;
    }

    /**
     * Set popup text size
     *
     * @param sizeResId popup text size resource id
     */
    public void setPopupTextSize(@DimenRes int sizeResId) {
        Resources res = getContext().getResources();
        popupTextSize = res.getDimension(sizeResId);
    }

    /**
     * Set popup item back ground
     *
     * @param backgroundResId popup item background
     */
    public void setPopupItemBackground(@DrawableRes int backgroundResId) {
        popupItemBackground = backgroundResId;
    }

    /**
     * Set checkbox size resource id
     *
     * @param sizeResId dimension resource id
     */
    public void setPopupCheckboxSizeRestId(@DimenRes int sizeResId) {
        Resources res = getContext().getResources();
        setPopupCheckboxSize((int) res.getDimension(sizeResId));
    }

    /**
     * Set checkbox size
     *
     * @param size size
     */
    public void setPopupCheckboxSize(int size) {
        popupCheckboxSize = size;
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
     * @return selected data
     */
    public T getSelectedData() {
        return getItem(selectedPosition);
    }

    /**
     * Get selected position
     *
     * @return selected position
     */
    public int getSelectedPosition() {
        return selectedPosition;
    }

    /**
     * Set selected item
     *
     * @param itemData item data
     */
    @SuppressWarnings("unchecked")
    public void setSelected(@Nullable Object itemData) {
        selectedPosition = getPosition((T) itemData);
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

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
        String dataToItemText(@NonNull T itemData);

        /**
         * get item text
         *
         * @param itemData item data
         * @return item text
         */
        String dataToPopupText(@NonNull T itemData);

        /**
         * Compare two item data
         *
         * @param a item data 1
         * @param b item data 2
         * @return true if equal
         */
        boolean compare(@NonNull T a, @NonNull T b);
    }
}
