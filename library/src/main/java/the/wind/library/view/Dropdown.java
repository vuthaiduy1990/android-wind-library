package the.wind.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import the.wind.library.R;
import the.wind.library.adapter.DropdownAdapter;

/**
 * Dropdown view
 */
public class Dropdown extends RelativeLayout {

    // Views
    private final Spinner _spinner;
    private final ImageView _arrow;

    // listener
    private OnItemSelectedListener itemSelectListener;

    /**
     * Constructor
     *
     * @param context application context
     */
    public Dropdown(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context application context
     * @param attrs   collection of attributes
     */
    public Dropdown(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context      application context
     * @param attrs        collection of attributes
     * @param defStyleAttr style attribute
     */
    public Dropdown(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Constructor
     *
     * @param context      application context
     * @param attrs        collection of attributes
     * @param defStyleAttr style attribute
     * @param defStyleRes  style resource id
     */
    public Dropdown(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.wl_dropdown, this);

        // bind views
        _spinner = findViewById(R.id._spinner);
        _arrow = findViewById(R.id._arrowIcon);

        // bind attributes
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.Dropdown, defStyleAttr, defStyleRes);
        try {
            // popup background
            int popupBackground = typeArray.getResourceId(R.styleable.Dropdown_popupBackground, R.drawable.wl_dialog_background);
            _spinner.setPopupBackgroundResource(popupBackground);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            typeArray.recycle();
        }
        if (getBackground() == null) {
            setBackgroundResource(R.drawable.wl_form_input_background); // default background
        }
        int padding = (int) getResources().getDimension(R.dimen.wl_spacing_level_2);
        int paddingLeft = getPaddingLeft() > 0 ? getPaddingLeft() : padding;
        int paddingTop = getPaddingTop() > 0 ? getPaddingTop() : padding;
        int paddingRight = getPaddingRight() > 0 ? getPaddingRight() : padding;
        int paddingBottom = getPaddingBottom() > 0 ? getPaddingBottom() : padding;
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        setDropDownVerticalOffset((int) getResources().getDimension(R.dimen.wl_dropdown_popup_vertical_offset));

        // Bind event listeners
        _spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object data = parent.getItemAtPosition(position);
                Adapter adapter = parent.getAdapter();
                if (adapter instanceof DropdownAdapter) {
                    ((DropdownAdapter<?>) adapter).setSelected(data);
                }
                if (itemSelectListener != null) itemSelectListener.onItemSelected(parent, view, position, data);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (itemSelectListener != null) itemSelectListener.onNothingSelected(parent);
            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return spinner view
     */
    public Spinner spinner() {
        return _spinner;
    }

    /**
     * @return arrow
     */
    public ImageView arrow() {
        return _arrow;
    }

    /**
     * Set adapter
     *
     * @param adapter adapter
     */
    public void setAdapter(DropdownAdapter<?> adapter) {
        _spinner.setAdapter(adapter);
        _spinner.setSelection(adapter.getSelectedPosition());
    }

    /**
     * Set a horizontal offset in pixels for the spinner's popup window of choices.
     *
     * @param pixels Horizontal offset in pixels
     */
    public void setDropDownHorizontalOffset(int pixels) {
        _spinner.setDropDownHorizontalOffset(pixels);
    }

    /**
     * Set a vertical offset in pixels for the spinner's popup window of choices.
     *
     * @param pixels Vertical offset in pixels
     */
    public void setDropDownVerticalOffset(int pixels) {
        _spinner.setDropDownVerticalOffset(pixels);
    }

    /**
     * Set the width of the spinner's popup window of choices in pixels. This value
     * may also be set to {@link android.view.ViewGroup.LayoutParams#MATCH_PARENT}
     * to match the width of the Spinner itself, or
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} to wrap to the measured size
     * of contained dropdown list items.
     *
     * @param pixels Width in pixels, WRAP_CONTENT, or MATCH_PARENT
     */
    public void setDropDownWidth(int pixels) {
        _spinner.setDropDownWidth(pixels);
    }

    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been selected.
     *
     * @param listener The callback that will run
     */
    public void setOnItemSelectedListener(@Nullable OnItemSelectedListener listener) {
        this.itemSelectListener = listener;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * On item selected listener
     */
    public interface OnItemSelectedListener {
        /**
         * <p>Callback method to be invoked when an item in this view has been
         * selected. This callback is invoked only when the newly selected
         * position is different from the previously selected position or if
         * there was no selected item.</p>
         * <p>
         * Implementers can call getItemAtPosition(position) if they need to access the
         * data associated with the selected item.
         *
         * @param parent   The AdapterView where the selection happened
         * @param view     The view within the AdapterView that was clicked
         * @param position The position of the view in the adapter
         * @param item     selected item data
         */
        void onItemSelected(AdapterView<?> parent, View view, int position, Object item);

        /**
         * Callback method to be invoked when the selection disappears from this
         * view. The selection can disappear for instance when touch is activated
         * or when the adapter becomes empty.
         *
         * @param parent The AdapterView that now contains no selected item.
         */
        void onNothingSelected(AdapterView<?> parent);
    }
}
