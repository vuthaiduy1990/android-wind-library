package the.wind.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import the.wind.library.R;
import the.wind.library.view.Button;

public class WindDialog extends Dialog {

    // layout
    private ViewGroup _layout;
    private View _customContentView;
    private int mWidth;
    private int mHeight;
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;

    // views
    private View _dialogView;
    private ImageView _icon;
    private LottieAnimationView _lottieIcon;
    private TextView _tvTitle;
    private ViewGroup _bodyHolder;
    private ViewGroup _footerHolder;
    private List<Button> _btnList = new LinkedList<>();

    // visibility
    private boolean mIconVisible = true;

    // model
    private LayoutType mLayoutType;
    private Timer mTimer;
    private int mIconResId;
    private Bitmap mIconBitmap;
    private int mLottieIconResId;

    // Animation
    @Nullable
    private Animation mInAnim;
    @Nullable
    private Animation mOutAnim;

    /**
     * Constructor
     *
     * @param context application context
     */
    public WindDialog(@NonNull Context context) {
        this(context, LayoutType.TATSUMAKI);
    }

    /**
     * Constructor
     *
     * @param context    application context
     * @param layoutType dialog layout type
     */
    public WindDialog(@NonNull Context context, LayoutType layoutType) {
        super(context, R.style.wind_dialog);
        super.setContentView(layoutType.getDialogLayout());
        mLayoutType = layoutType;
        mTimer = new Timer();

        // Bind the layout and set default layout's size, padding, etc.
        _layout = findViewById(R.id._layout);
        setWidth((int) context.getResources().getDimension(R.dimen.wind_dialog_width));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setPadding(
                (int) context.getResources().getDimension(R.dimen.wind_dialog_padding_start),
                (int) context.getResources().getDimension(R.dimen.wind_dialog_padding_top),
                (int) context.getResources().getDimension(R.dimen.wind_dialog_padding_end),
                (int) context.getResources().getDimension(R.dimen.wind_dialog_padding_bottom)
        );

        // bind views
        _dialogView = findViewById(android.R.id.content);
        _icon = findViewById(R.id._icon);
        _lottieIcon = findViewById(R.id._lottieIcon);
        _tvTitle = findViewById(R.id._tvTitle);
        _bodyHolder = findViewById(R.id._bodyHolder);
        _footerHolder = findViewById(R.id._footerHolder);

        // default values
        setContentView(mLayoutType.getContentLayout());
        setInOutAnimType(InOutAnimType.SWEET_ALERT);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure the layout
        ViewGroup.LayoutParams layoutParams = _layout.getLayoutParams();
        layoutParams.width = mWidth;
        layoutParams.height = mHeight;
        _layout.setLayoutParams(layoutParams);
        _layout.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (_dialogView != null) {
            if (mInAnim != null) _dialogView.startAnimation(mInAnim);
            if (_lottieIcon != null && _lottieIcon.getVisibility() == View.VISIBLE) {
                _lottieIcon.playAnimation();
            }
        }
    }

    @Override
    public void dismiss() {
        if (_dialogView != null && mOutAnim != null) {
            _dialogView.startAnimation(mOutAnim);
        } else {
            super.dismiss();
        }
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        _tvTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getString(titleId));
    }

    @Override
    public void setContentView(int layoutResID) {
        if (layoutResID != 0) {
            View view = getLayoutInflater().inflate(layoutResID, null);
            setContentView(view);
        }
    }

    @Override
    public void setContentView(@NonNull View view) {
        setContentView(view, null);
    }

    @Override
    public void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        _customContentView = view;
        if (_bodyHolder != null) {
            _bodyHolder.removeAllViews();
            if (params != null) {
                _bodyHolder.addView(view, params);
            } else {
                _bodyHolder.addView(view);
            }
        }
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- EVENT -------------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return dialog's layout
     */
    public ViewGroup getLayout() {
        return _layout;
    }

    /**
     * @return dialog's width
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * Set dialog's width
     *
     * @param width pixel value
     */
    public void setWidth(int width) {
        mWidth = width;
    }

    /**
     * @return dialog's height
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * Set dialog's height
     *
     * @param height dialog's height
     */
    public void setHeight(int height) {
        mHeight = height;
    }

    /**
     * Get dialog's padding
     *
     * @return [left, top, right, bottom]
     */
    public int[] getPadding() {
        return new int[]{mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom};
    }

    /**
     * Set padding
     *
     * @param left   padding left
     * @param top    padding top
     * @param right  padding right
     * @param bottom padding bottom
     */
    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingTop = top;
        mPaddingRight = right;
        mPaddingBottom = bottom;
    }

    /**
     * Set inout animation
     *
     * @param animType in/out animation type
     * @return dialog
     */
    public WindDialog setInOutAnimType(InOutAnimType animType) {
        if (animType == null) /* clear inout animation */ {
            mInAnim = null;
            mOutAnim = null;
            return this;
        }

        // set new animation
        mInAnim = animType.getInAnim(getContext());
        mOutAnim = animType.getOutAnim(getContext());
        mOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (_dialogView == null) return;
                // Fix: Only the original thread that created a view hierarchy can touch its views.
                _dialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        WindDialog.super.dismiss();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return this;
    }

    /**
     * Get icon
     *
     * @return icon (animation or static icon)
     */
    public View icon() {
        if (_lottieIcon.getVisibility() == View.VISIBLE) {
            return _lottieIcon;
        }
        return _icon;
    }

    /**
     * Set icon's visibility
     *
     * @param visible true/false
     * @return dialog
     */
    public WindDialog setIconVisible(boolean visible) {
        mIconVisible = visible;
        if (_icon != null && _lottieIcon != null) {
            judgeIcon();
        }
        return this;
    }

    /**
     * Set static icon
     *
     * @param resId resource id
     * @return dialog
     */
    public WindDialog setIcon(int resId) {
        mIconResId = resId;
        if (_icon != null && _lottieIcon != null) {
            judgeIcon();
        }
        return this;
    }

    /**
     * Set static bitmap icon
     *
     * @param bitmap bitmap icon
     * @return dialog
     */
    public WindDialog setIcon(Bitmap bitmap) {
        mIconBitmap = bitmap;
        if (_icon != null && _lottieIcon != null) {
            judgeIcon();
        }
        return this;
    }

    /**
     * Set animation icon
     *
     * @param resId resource id
     * @return dialog
     */
    public WindDialog setLottieIcon(int resId) {
        mLottieIconResId = resId;
        if (_icon != null && _lottieIcon != null) {
            judgeIcon();
        }
        return this;
    }

    /**
     * Judge which kind of icon will be used (inline/static/animation)
     */
    private void judgeIcon() {
        if (!mIconVisible) {
            _icon.setVisibility(View.GONE);
            _lottieIcon.setVisibility(View.GONE);
            return;
        }

        // lottie icon -> static icon -> bitmap icon
        boolean useAnim;
        if (mLottieIconResId != 0) {
            // use animation icon
            useAnim = true;
            _lottieIcon.setAnimation(mLottieIconResId);

        } else if (mIconResId != 0) {
            // use static icon
            useAnim = false;
            _icon.setImageResource(mIconResId);

        } else if (mIconBitmap != null) {
            // use bitmap icon
            useAnim = false;
            _icon.setImageBitmap(mIconBitmap);
        } else {
            // hide icon
            _lottieIcon.setVisibility(View.GONE);
            _icon.setVisibility(View.GONE);
            return;
        }
        _lottieIcon.setVisibility(useAnim ? View.VISIBLE : View.GONE);
        _icon.setVisibility(!useAnim ? View.VISIBLE : View.GONE);
    }

    /**
     * @return title text view
     */
    public TextView titleView() {
        return _tvTitle;
    }

    /**
     * Set title visibility
     *
     * @param visible true/false
     * @return dialog
     */
    public WindDialog setTitleVisible(boolean visible) {
        _tvTitle.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * @return body view
     */
    public View contentView() {
        return _customContentView;
    }

    /**
     * Set content text
     * Available for tatsumaki layout only
     *
     * @param resId string resource id
     * @return dialog
     */
    public WindDialog setContentText(int resId) {
        return setContentText(getContext().getString(resId));
    }

    /**
     * Set content text
     * Available for tatsumaki layout only
     *
     * @param text string value
     * @return dialog
     */
    public WindDialog setContentText(CharSequence text) {
        if (LayoutType.TATSUMAKI.equals(mLayoutType)) {
            TextView textView = _customContentView.findViewById(R.id._content);
            if (textView != null) {
                textView.setText(text);
            }
        }
        return this;
    }


    /**
     * Set footer visibility
     *
     * @param visible true/false
     * @return dialog
     */
    public WindDialog setFooterVisible(boolean visible) {
        _footerHolder.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * A dialog can have many button but this function will return the first one
     *
     * @return first button
     */
    public Button button() {
        return _btnList.size() > 0 ? _btnList.get(0) : null;
    }

    /**
     * @return all buttons
     */
    public List<Button> buttons() {
        return _btnList;
    }

    /**
     * Add button
     *
     * @param btn button
     * @return added button
     */
    public Button addButton(Button btn) {
        _footerHolder.addView(btn);
        _btnList.add(btn);
        return btn;
    }

    /**
     * Add button
     *
     * @param type button type
     * @param text text
     * @param icon inline icon
     * @return added button
     */
    public Button addButton(@NonNull Button.Type type, @NonNull CharSequence text, Button.InlineIcon icon) {
        Button btn = new Button(getContext());
        int margin = (int) getContext().getResources().getDimension(R.dimen.button_spacing);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, 0, margin, 0);
        btn.setLayoutParams(params);
        btn.setMinimumWidth((int) getContext().getResources().getDimension(R.dimen.wind_dialog_button_min_width));
        btn.setType(type);
        btn.setText(text);
        if (icon != null) {
            btn.setIconType(icon);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return addButton(btn);
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Show dialog with timeout in milliseconds.
     * It means that after timeout, dialog will be auto dismissed
     *
     * @param timeout in milliseconds
     */
    public void show(long timeout) {
        show();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (_dialogView == null) return;
                // Fix: Only the original thread that created a view hierarchy can touch its views.
                _dialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                });
            }
        }, timeout);
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Dialog type
     */
    public enum LayoutType {
        TATSUMAKI(R.layout.wind_dialog_tatsumaki, R.layout.wind_dialog_tatsumaki_content),
        FUBUKI(R.layout.wind_dialog_fubuki, 0);

        private int layout;
        private int content;

        LayoutType(int layout, int content) {
            this.layout = layout;
            this.content = content;
        }

        /**
         * @return respective layout
         */
        private int getDialogLayout() {
            return layout;
        }

        /**
         * @return custom content layout id
         */
        private int getContentLayout() {
            return content;
        }
    }

    /**
     * Modal InOut animation
     */
    public enum InOutAnimType {
        // Sweet Alert Dialog
        // https://github.com/pedant/sweet-alert-dialog
        SWEET_ALERT(R.anim.wind_dialog_in_anim_sweet_alert, R.anim.wind_dialog_out_anim_sweet_alert);

        private int inAnimResId;
        private int outAnimResId;

        InOutAnimType(int inAnim, int outAnim) {
            this.inAnimResId = inAnim;
            this.outAnimResId = outAnim;
        }

        /**
         * Get modal in animation
         *
         * @param context application context
         * @return modal in animation
         */
        private Animation getInAnim(Context context) {
            return AnimationUtils.loadAnimation(context, inAnimResId);
        }

        /**
         * Get modal out animation
         *
         * @param context application context
         * @return modal out animation
         */
        private Animation getOutAnim(Context context) {
            return AnimationUtils.loadAnimation(context, outAnimResId);
        }
    }
}
