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

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import the.wind.library.R;
import the.wind.library.dialog.WindDialog;

/**
 * Item setting popup menu
 */
public class WindActionMenu extends WindDialog {

    private ViewGroup _menuHolder;
    private LayoutInflater mInflater;
    private int mItemBackground;
    private int mItemTextColor;
    private int mItemTextSize;
    private int mItemIconSize;

    // model
    private int mSelectedId;


    // listener
    private OnItemSelectListener mItemSelectListener;
    private OnMenuConfigListener mMenuConfigListener;

    /**
     * Constructor
     *
     * @param context application context
     */
    public WindActionMenu(@NonNull Context context) {
        super(context, LayoutType.FUBUKI);
        setContentView(R.layout.wl_action_menu_content_view);
        mInflater = LayoutInflater.from(context);
        setFooterVisible(false);
        setTitleVisible(false);
        setIconVisible(false);
        setInOutAnimType(InOutAnimType.SLIDE_TOP_2_BOTTOM);
        setGravity(Gravity.TOP);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        // bind view
        _menuHolder = contentView().findViewById(R.id._menuHolder);
        setItemBackground(R.drawable.wl_action_menu_item_background);
        setItemTextColor(R.color.wl_text);
        setItemTextSize(R.dimen.wl_text);
        setItemIconSize(R.dimen.wl_icon);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mItemSelectListener != null && mSelectedId > 0) {
                    mItemSelectListener.onSelect(mSelectedId);
                    mSelectedId = -1;
                }
            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public void show() {
        if (mMenuConfigListener != null) {
            mMenuConfigListener.onConfig(_menuHolder);
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
        View itemView = mInflater.inflate(R.layout.wl_action_menu_item_view, _menuHolder, false);
        itemView.setId(id);
        itemView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemView.setBackgroundResource(mItemBackground);
        _menuHolder.addView(itemView);

        // bind data
        ImageView icon = itemView.findViewById(R.id._icon);
        if (iconResId != 0) {
            icon.setImageResource(iconResId);
            int iconSize = (int) getContext().getResources().getDimension(mItemIconSize);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) icon.getLayoutParams();
            params.width = iconSize;
            params.height = iconSize;
            icon.setLayoutParams(params);
        } else {
            icon.setVisibility(View.GONE);
        }
        TextView textView = itemView.findViewById(R.id._textView);
        if (textResId != 0) {
            textView.setText(textResId);
            textView.setTextColor(ContextCompat.getColor(getContext(), mItemTextColor));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(mItemTextSize));
        } else {
            textView.setVisibility(View.GONE);
        }

        // bind attribute and listener
        itemView.setClickable(true);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedId = id;
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
    public WindActionMenu setItemBackground(int drawableId) {
        mItemBackground = drawableId;
        return this;
    }

    /**
     * Set text color
     *
     * @param colorResId color resource id
     * @return menu
     */
    public WindActionMenu setItemTextColor(int colorResId) {
        mItemTextColor = colorResId;
        return this;
    }

    /**
     * Set text size
     *
     * @param size resource dimentsion id
     * @return menu
     */
    public WindActionMenu setItemTextSize(int size) {
        mItemTextSize = size;
        return this;
    }

    /**
     * Set icon size
     *
     * @param size resource dimension id
     * @return menu
     */
    public WindActionMenu setItemIconSize(int size) {
        mItemIconSize = size;
        return this;
    }

    /**
     * Set item select listener
     *
     * @param listener listener
     * @return menu
     */
    public WindActionMenu setOnItemSelectListener(OnItemSelectListener listener) {
        mItemSelectListener = listener;
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
        mMenuConfigListener = listener;
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
