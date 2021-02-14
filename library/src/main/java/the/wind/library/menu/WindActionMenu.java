package the.wind.library.menu;

import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import the.wind.library.R;
import the.wind.library.dialog.WindDialog;

/**
 * Item setting popup menu
 */
public class WindActionMenu extends WindDialog {

    private final ViewGroup _menuHolder;
    private final LayoutInflater inflater;
    private int itemBackground;
    private int itemTextColor;
    private int itemTextSize;
    private int itemIconSize;

    // model
    private int selectedId;

    // listener
    private OnItemSelectListener itemSelectListener;
    private OnMenuConfigListener menuConfigListener;

    /**
     * Constructor
     *
     * @param context application context
     */
    public WindActionMenu(@NonNull Context context) {
        super(context, LayoutType.FUBUKI);
        setContentView(R.layout.wl_action_menu_content_view);
        inflater = LayoutInflater.from(context);
        setFooterVisible(false);
        setTitleVisible(false);
        setIconVisible(false);
        setInOutAnimType(InOutAnimType.SLIDE_TOP_2_BOTTOM);
        setGravity(Gravity.TOP);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        // bind view
        _menuHolder = contentView().findViewById(R.id._menuHolder);
        setItemBackground(R.drawable.wl_background_hover);
        setItemTextColor(R.color.wl_text);
        setItemTextSize(R.dimen.wl_text);
        setItemIconSize(R.dimen.wl_icon);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (itemSelectListener != null && selectedId > 0) {
                    itemSelectListener.onSelect(selectedId);
                    selectedId = -1;
                }
            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public void show() {
        if (menuConfigListener != null) {
            menuConfigListener.onConfig(_menuHolder);
        }
        super.show();
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Add item
     *
     * @param iconResId icon resource id
     * @param textResId text resource id
     * @return menu
     */
    public WindActionMenu addItem(@IdRes final int id, int iconResId, int textResId) {
        View itemView = inflater.inflate(R.layout.wl_action_menu_item_view, _menuHolder, false);
        itemView.setId(id);
        itemView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemView.setBackgroundResource(itemBackground);
        _menuHolder.addView(itemView);

        // bind data
        ImageView _iconView = itemView.findViewById(R.id._iconView);
        if (iconResId != 0) {
            _iconView.setImageResource(iconResId);
            int iconSize = (int) getContext().getResources().getDimension(itemIconSize);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) _iconView.getLayoutParams();
            params.width = iconSize;
            params.height = iconSize;
            _iconView.setLayoutParams(params);
        } else {
            _iconView.setVisibility(View.GONE);
        }
        TextView _textView = itemView.findViewById(R.id._textView);
        if (textResId != 0) {
            _textView.setText(textResId);
            _textView.setTextColor(ContextCompat.getColor(getContext(), itemTextColor));
            _textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(itemTextSize));
        } else {
            _textView.setVisibility(View.GONE);
        }

        // bind attribute and listener
        itemView.setClickable(true);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedId = id;
                dismiss();
            }
        });


        return this;
    }

    /**
     * Set item background
     *
     * @param drawableId background resource id
     * @return menu
     */
    public WindActionMenu setItemBackground(@DrawableRes int drawableId) {
        itemBackground = drawableId;
        return this;
    }

    /**
     * Set text color
     *
     * @param colorResId color resource id
     * @return menu
     */
    public WindActionMenu setItemTextColor(@ColorRes int colorResId) {
        itemTextColor = colorResId;
        return this;
    }

    /**
     * Set text size
     *
     * @param size resource dimension id
     * @return menu
     */
    public WindActionMenu setItemTextSize(@DimenRes int size) {
        itemTextSize = size;
        return this;
    }

    /**
     * Set icon size
     *
     * @param size resource dimension id
     * @return menu
     */
    public WindActionMenu setItemIconSize(@DimenRes int size) {
        itemIconSize = size;
        return this;
    }

    /**
     * Set item select listener
     *
     * @param listener listener
     * @return menu
     */
    public WindActionMenu setOnItemSelectListener(OnItemSelectListener listener) {
        itemSelectListener = listener;
        return this;
    }

    /**
     * Set menu config listener.
     * We con use this listener to configure the menu before showing
     *
     * @param listener listener
     * @return menu
     */
    public WindActionMenu setOnMenuConfigListener(OnMenuConfigListener listener) {
        menuConfigListener = listener;
        return this;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * On item select listener
     */
    public interface OnItemSelectListener {
        /**
         * Trigger when user click on item
         *
         * @param id item's id
         */
        void onSelect(@IdRes int id);
    }

    /**
     * On showing menu listener
     */
    public interface OnMenuConfigListener {
        /**
         * Trigger before menu is showing
         *
         * @param menuHolder menu place holder
         */
        void onConfig(ViewGroup menuHolder);
    }
}
