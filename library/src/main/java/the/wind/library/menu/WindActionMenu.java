package the.wind.library.menu;

import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import the.wind.library.R;
import the.wind.library.dialog.WindDialog;

/**
 * Item setting popup menu
 */
public class WindActionMenu extends WindDialog {

    // view
    private final ViewGroup _menuHolder;
    private final LayoutInflater inflater;

    // styling attributes
    @DrawableRes
    private int itemBackground;
    @ColorRes
    private int itemTextColor;
    @DimenRes
    private int itemTextSize;
    @DimenRes
    private int itemIconSize;

    // model
    private final MenuType menuType;
    private int selectedId = -1;

    // listener
    private OnItemSelectListener itemSelectListener;
    private OnMenuConfigListener menuConfigListener;

    /**
     * Constructor
     *
     * @param context application context
     */
    public WindActionMenu(@NonNull Context context) {
        this(context, MenuType.LIST);
    }

    /**
     * Constructor
     *
     * @param context  application context
     * @param menuType menu type
     */
    public WindActionMenu(@NonNull Context context, MenuType menuType) {
        super(context, LayoutType.FUBUKI);
        this.menuType = menuType;
        if (MenuType.SIDEBAR.equals(menuType)) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            setContentView(menuType.getContentLayout(), layoutParams);
        } else {
            setContentView(menuType.getContentLayout());
        }
        inflater = LayoutInflater.from(context);

        // bind view
        _menuHolder = contentView().findViewById(R.id._menuHolder);

        // Configure menu based on type
        setFooterVisible(false);
        setTitleVisible(false);
        setIconVisible(false);
        setItemBackground(R.drawable.wl_background_hover_pressed);
        setItemTextColor(R.color.wl_text);
        switch (menuType) {
            case LIST:
                setGravity(Gravity.TOP);
                setInOutAnimType(InOutAnimType.SLIDE_TOP_2_BOTTOM);
                setCancelable(true);
                setCanceledOnTouchOutside(true);
                setItemTextSize(R.dimen.wl_text);
                setItemIconSize(R.dimen.wl_icon);
                break;
            case TOOLBAR:
                int padding = (int) context.getResources().getDimension(R.dimen.wl_spacing_level_1);
                setPadding(padding, padding, padding, padding);
                setGravity(Gravity.BOTTOM);
                ((ViewGroup.MarginLayoutParams) getContentHolder().getLayoutParams()).topMargin = 0;
                getLayout().setBackgroundResource(R.drawable.wl_toolbar_menu_background);
                setInOutAnimType(InOutAnimType.SLIDE_BOTTOM_2_TOP);
                setCancelable(false);
                setCanceledOnTouchOutside(false);
                setMarginBottom((int) context.getResources().getDimension(R.dimen.wl_spacing_level_3));
                setItemTextSize(R.dimen.wl_text_tiny);
                setItemIconSize(R.dimen.wl_icon_big);
                getWindow().setDimAmount(0);
                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
                break;
            case SIDEBAR:
                setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                setPadding(0, 0, 0, 0);
                setGravity(Gravity.TOP | Gravity.END);
                getLayout().setBackground(null);
                setInOutAnimType(InOutAnimType.SLIDE_RIGHT_2_LEFT);
                setCancelable(false);
                setCanceledOnTouchOutside(false);
                setMarginRight((int) context.getResources().getDimension(R.dimen.wl_spacing_level_2));
                setMarginTop((int) context.getResources().getDimension(R.dimen.wl_sidebar_menu_margin_top));
                setItemTextSize(R.dimen.wl_text_tiny);
                setItemIconSize(R.dimen.wl_icon_big);
                getWindow().setDimAmount(0);
                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
                break;
            default:
        }

        // bind listener
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
    public WindActionMenu addItem(@IdRes final int id, @DrawableRes int iconResId, @StringRes int textResId) {
        View itemView = inflater.inflate(menuType.getItemLayout(), _menuHolder, false);
        itemView.setId(id);
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
        itemView.setOnClickListener(v -> {
            if (MenuType.LIST.equals(menuType)) {
                // For list type -> auto dismiss menu dialog when user select an menu item
                selectedId = id;
                dismiss();
            } else {
                if (itemSelectListener != null) itemSelectListener.onSelect(id);
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


    /**
     * Layout type
     */
    public enum MenuType {
        LIST(R.layout.wl_menu_list, R.layout.wl_menu_list_item),
        TOOLBAR(R.layout.wl_menu_toolbar, R.layout.wl_menu_toolbar_item),
        SIDEBAR(R.layout.wl_menu_sidebar, R.layout.wl_menu_sidebar_item);

        private final int contentLayout;
        private final int itemLayout;

        /**
         * Constructor
         *
         * @param contentLayout dialog content layout
         * @param itemLayout    item layout
         */
        MenuType(@LayoutRes int contentLayout, @LayoutRes int itemLayout) {
            this.contentLayout = contentLayout;
            this.itemLayout = itemLayout;
        }

        /**
         * @return dialog content layout
         */
        @LayoutRes
        public int getContentLayout() {
            return contentLayout;
        }

        /**
         * @return item layout
         */
        @LayoutRes
        public int getItemLayout() {
            return itemLayout;
        }
    }
}
