package the.wind.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import the.wind.library.R;

/**
 * Button
 * Basic usage:
 * <pre>
 *     <the.wind.library.view.Button
 *          android:layout_width="wrap_content"
 *          android:layout_height="wrap_content"
 *          app:type="DANGER"
 *          app:text="@string/action_menu_settings" />
 *
 *     <the.wind.library.view.Button
 *          android:layout_width="wrap_content"
 *          android:layout_height="wrap_content"
 *          app:type="DANGER"
 *          app:inlineIcon="WAITING"
 *          app:customIcon="@drawable/com_ic_save"
 *          app:customAnim="@raw/button_waiting"
 *          app:iconSize="@dimen/com_icon_size"
 *          app:textSize="@dimen/com_label_size"
 *          app:textColor="@color/success"
 *          app:text="@string/action_menu_settings" />
 * </pre>
 */
public class Button extends LinearLayout {

    // views
    private TextView _textView;
    private ImageView _icon;
    private LottieAnimationView _lottieIcon;
    private View _space;

    // icon resource
    private InlineIcon mInlineIcon;
    private int mCustomIconRes;
    private int mCustomAnimRes;

    public Button(Context context) {
        this(context, null);
    }

    public Button(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Button(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Button(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.wl_button, this);

        // bind views
        _textView = findViewById(R.id._textView);
        _icon = findViewById(R.id._icon);
        _lottieIcon = findViewById(R.id._lottieIcon);
        _space = findViewById(R.id._space);

        // bind attributes
        TypedArray btTypeArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.Button,
                defStyleAttr, defStyleRes);
        try {
            // retrieve button type
            int typeIdx = btTypeArray.getInt(R.styleable.Button_type, 0);
            if (getBackground() == null) {
                setType(Type.values()[typeIdx]);
            }

            // bind icon's attributes
            int iconIdx = btTypeArray.getInt(R.styleable.Button_inlineIcon, -1);
            if (iconIdx >= 0) {
                mInlineIcon = InlineIcon.values()[iconIdx];
            }
            mCustomIconRes = btTypeArray.getResourceId(R.styleable.Button_customIcon, 0);
            mCustomAnimRes = btTypeArray.getResourceId(R.styleable.Button_customAnim, 0);
            float iconSize = btTypeArray.getDimension(
                    R.styleable.Button_iconSize,
                    getResources().getDimension(R.dimen.wl_button_icon_size));
            RelativeLayout.LayoutParams iconLayout = new RelativeLayout.LayoutParams((int) iconSize, (int) iconSize);
            _icon.setLayoutParams(iconLayout);
            _lottieIcon.setLayoutParams(iconLayout);
            judgeIcon();

            // bind text attributes
            String textValue = btTypeArray.getString(R.styleable.Button_text);
            int textColorRes = btTypeArray.getColor(
                    R.styleable.Button_textColor,
                    ContextCompat.getColor(context, R.color.wl_button_text));
            float textSize = btTypeArray.getDimension(
                    R.styleable.Button_textSize,
                    getResources().getDimension(R.dimen.wl_button_text));
            LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            _textView.setLayoutParams(textLayout);
            setText(textValue);
            setTextColor(textColorRes);
            _textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            // bind space attribute
            float spacing = btTypeArray.getDimension(
                    R.styleable.Button_spacing,
                    getResources().getDimension(R.dimen.wl_button_spacing));
            _space.setLayoutParams(new LinearLayout.LayoutParams((int) spacing, 1));
            _space.setVisibility(isSpacing() ? VISIBLE : GONE);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            btTypeArray.recycle();
        }
        setClickable(true);
        setGravity(Gravity.CENTER);
        // set default padding
        setDefaultPadding(
                R.dimen.wl_button_padding_start,
                R.dimen.wl_button_padding_top,
                R.dimen.wl_button_padding_end,
                R.dimen.wl_button_padding_bottom
        );
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT -----------------------*/

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Get icon
     *
     * @return icon (animation or static icon)
     */
    public View icon() {
        if (_lottieIcon.getVisibility() == VISIBLE) {
            return _lottieIcon;
        }
        return _icon;
    }

    /**
     * @return text view
     */
    public TextView textView() {
        return _textView;
    }

    /**
     * Set button type
     *
     * @param type type
     * @return button
     */
    public Button setType(Type type) {
        setBackgroundResource(type.getBackground());
        return this;
    }

    /**
     * Set inline icon type
     *
     * @param type type
     * @return button
     */
    public Button setIconType(InlineIcon type) {
        mInlineIcon = type;
        judgeIcon();
        return this;
    }

    /**
     * Set custom static icon
     *
     * @param resId drawable resource
     * @return button
     */
    public Button setCustomIcon(@DrawableRes int resId) {
        mCustomIconRes = resId;
        judgeIcon();
        return this;
    }

    /**
     * Set custom animation icon
     *
     * @param resId resource id
     * @return button
     */
    public Button setCustomAnim(@RawRes int resId) {
        mCustomAnimRes = resId;
        judgeIcon();
        return this;
    }

    /**
     * Judge which kind of icon will be used (inline/static/animation)
     */
    private void judgeIcon() {
        // custom animation -> custom static -> inline icon
        boolean useAnim;
        if (mCustomAnimRes != 0) {
            // use animation icon
            useAnim = true;
            _lottieIcon.setAnimation(mCustomAnimRes);

        } else if (mCustomIconRes != 0) {
            // use static icon
            useAnim = false;
            _icon.setImageResource(mCustomIconRes);

        } else if (mInlineIcon != null) {
            // use inline icon
            useAnim = mInlineIcon.useAnimation();
            if (useAnim) {
                _lottieIcon.setAnimation(mInlineIcon.getIconResource());
            } else {
                _icon.setImageResource(mInlineIcon.getIconResource());
            }
        } else {
            // hide icon
            _lottieIcon.setVisibility(GONE);
            _icon.setVisibility(GONE);
            _space.setVisibility(isSpacing() ? VISIBLE : GONE);
            return;
        }
        _lottieIcon.setVisibility(useAnim ? VISIBLE : GONE);
        _icon.setVisibility(!useAnim ? VISIBLE : GONE);
        _space.setVisibility(isSpacing() ? VISIBLE : GONE);
    }

    /**
     * Show/hide icon
     *
     * @param visible true -> show else hide
     * @return button
     */
    public Button setIconVisible(boolean visible) {
        if (visible) {
            judgeIcon();
        } else {
            _icon.setVisibility(GONE);
            _lottieIcon.setVisibility(GONE);
        }
        _space.setVisibility(isSpacing() ? VISIBLE : GONE);
        return this;
    }

    /**
     * Set text style (italic/bold/normal/etc.)
     *
     * @param style type face. Ex, Typeface.BOLD
     * @return button
     *
     * @see {{{@link Typeface}}}
     */
    public Button setTextStyle(int style) {
        _textView.setTypeface(_textView.getTypeface(), style);
        return this;
    }

    /**
     * Set text resource
     *
     * @param resId string resource id
     * @return button
     */
    public Button setText(@StringRes int resId) {
        _textView.setText(resId);
        _textView.setVisibility(resId != 0 ? VISIBLE : GONE);
        _space.setVisibility(isSpacing() ? VISIBLE : GONE);
        return this;
    }

    /**
     * Set text value
     *
     * @param text text value
     * @return button
     */
    public Button setText(CharSequence text) {
        _textView.setText(text);
        if (text == null || text.toString().isEmpty()) {
            _textView.setVisibility(GONE);
        } else {
            _textView.setVisibility(VISIBLE);
        }
        _space.setVisibility(isSpacing() ? VISIBLE : GONE);
        return this;
    }

    /**
     * Set text color
     *
     * @param color color
     * @return button
     */
    public Button setTextColor(@ColorInt int color) {
        _textView.setTextColor(color);
        return this;
    }

    /**
     * Show/hide text
     *
     * @param visible true -> show else hide
     * @return button
     */
    public Button setTextVisible(boolean visible) {
        _textView.setVisibility(visible ? VISIBLE : GONE);
        _space.setVisibility(isSpacing() ? VISIBLE : GONE);
        return this;
    }

    /**
     * Set default padding
     *
     * @param leftRes   dimension resource
     * @param topRes    dimension resource
     * @param rightRes  dimension resource
     * @param bottomRes dimension resource
     */
    protected void setDefaultPadding(@DimenRes int leftRes, @DimenRes int topRes, @DimenRes int rightRes, @DimenRes int bottomRes) {
        int pLeft = getPaddingLeft();
        int pTop = getPaddingTop();
        int pRight = getPaddingRight();
        int pBottom = getPaddingBottom();
        if (pLeft == 0) {
            pLeft = (int) getResources().getDimension(leftRes);
        }
        if (pTop == 0) {
            pTop = (int) getResources().getDimension(topRes);
        }
        if (pRight == 0) {
            pRight = (int) getResources().getDimension(rightRes);
        }
        if (pBottom == 0) {
            pBottom = (int) getResources().getDimension(bottomRes);
        }
        setPadding(pLeft, pTop, pRight, pBottom);
    }

    /**
     * Check if we need to add space between text and icon or not
     *
     * @return true if space should be added
     */
    private boolean isSpacing() {
        return _textView.getVisibility() == View.VISIBLE
                && (_icon.getVisibility() == VISIBLE || _lottieIcon.getVisibility() == View.VISIBLE);
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Button type
     */
    public enum Type {
        PRIMARY(R.drawable.wl_button_background_primary),
        SUCCESS(R.drawable.wl_button_background_success),
        INFO(R.drawable.wl_button_background_info),
        HIGHLIGHT(R.drawable.wl_button_background_highlight),
        WARNING(R.drawable.wl_button_background_warning),
        DANGER(R.drawable.wl_button_background_danger),
        GRAY(R.drawable.wl_button_background_gray),
        NEUTRAL(R.drawable.wl_button_background_neutral);

        private int background;

        Type(int background) {
            this.background = background;
        }

        /**
         * @return background resource
         */
        public int getBackground() {
            return this.background;
        }
    }

    /**
     * Inline icon type
     */
    public enum InlineIcon {
        OK(R.drawable.wl_ic_ok, false),
        EDIT(R.drawable.wl_ic_edit, false),
        SAVE(R.drawable.wl_ic_save, false),
        TRASH(R.drawable.wl_ic_trash, false),
        WAITING(R.raw.wl_button_waiting, true),
        SEARCH(R.drawable.wl_ic_search, false),
        INFO(R.drawable.wl_ic_info, false),
        LOCK(R.drawable.wl_ic_lock, false),
        SETTING(R.drawable.wl_ic_setting, false);

        private int iconRes;
        private boolean useAnim;

        InlineIcon(int iconRes, boolean useAnim) {
            this.iconRes = iconRes;
            this.useAnim = useAnim;
        }

        /**
         * @return static icon resource
         */
        public int getIconResource() {
            return iconRes;
        }

        /**
         * Check if icon use animation or not
         *
         * @return true if use animation
         */
        public boolean useAnimation() {
            return useAnim;
        }
    }
}
