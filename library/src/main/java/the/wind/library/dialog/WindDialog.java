package the.wind.library.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import the.wind.library.CWBundle;
import the.wind.library.CWCallback;
import the.wind.library.R;
import the.wind.library.view.Button;

public class WindDialog extends Dialog {

    // UI Thread
    private final Handler UiThread = new Handler();

    // layout
    private final ViewGroup _layout;
    private View _customContentView;
    private int width;
    private int height;
    private int maxHeight;
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int marginLeft;
    private int marginTop;
    private int marginRight;
    private int marginBottom;

    // views
    private final View _dialogView;
    private final ImageView _iconView;
    private final LottieAnimationView _lottieIconView;
    private final ViewGroup _titleLayout;
    private final TextView _titleView;
    private final TextView _subTitleView;
    private final ViewGroup _headerHolder;
    private final ViewGroup _bodyHolder;
    private final ViewGroup _footerHolder;
    private final List<Button> _btnList = new LinkedList<>();
    private final ViewGroup _waitingMask;
    private final LottieAnimationView _waitingIcon;

    // view attribute
    private boolean iconVisible = true;
    private boolean showImmediately = false;

    // model
    private final LayoutType layoutType;
    @DrawableRes
    private int iconResId;
    private Bitmap iconBitmap;
    @RawRes
    private int lottieIconResId;
    private final CWBundle bundle = new CWBundle();
    private boolean showingAfterDelay = false;

    // Animation
    @Nullable
    private Animation inAnim;
    @Nullable
    private Animation outAnim;

    // Listener
    private OnShowListener showListener;

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
        this.layoutType = layoutType;

        // Bind the layout and set default layout's size, padding, etc.
        _layout = findViewById(R.id._layout);
        if (LayoutType.FUBUKI.equals(layoutType)) {
            setWidth((int) context.getResources().getDimension(R.dimen.wl_dialog_fubuki_width));
        } else {
            setWidth((int) context.getResources().getDimension(R.dimen.wl_dialog_tatsumaki_width));
        }
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setMaxHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setPadding(
                (int) context.getResources().getDimension(R.dimen.wl_dialog_padding_start),
                (int) context.getResources().getDimension(R.dimen.wl_spacing_level_2),
                (int) context.getResources().getDimension(R.dimen.wl_dialog_padding_end),
                (int) context.getResources().getDimension(R.dimen.wl_spacing_level_2)
        );
        setMargin(0, 0, 0, 0); // default margin

        // bind views
        _dialogView = findViewById(android.R.id.content);
        _headerHolder = _layout.findViewById(R.id._headerHolder);
        _iconView = _headerHolder.findViewById(R.id._iconView);
        _lottieIconView = _headerHolder.findViewById(R.id._lottieIconView);
        _titleLayout = _layout.findViewById(R.id._titleLayout);
        _titleView = _titleLayout.findViewById(R.id._titleView);
        _subTitleView = _titleLayout.findViewById(R.id._subTitleView);
        _bodyHolder = _layout.findViewById(R.id._bodyHolder);
        _footerHolder = _layout.findViewById(R.id._footerHolder);

        // bind waiting mask
        _waitingMask = findViewById(R.id._waitingMask);
        _waitingIcon = _waitingMask.findViewById(R.id._waitingIcon);
        _waitingMask.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                return true;
            }
        });

        // default values
        setContentView(this.layoutType.getContentLayout());
        setInOutAnimType(InOutAnimType.SWEET_ALERT);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setCustomWaitingIcon(R.raw.wl_dialog_icon_waiting);
        _waitingIcon.setMaxProgress(310f / 841f);

        // Set
        super.setOnShowListener(dialog -> {
            reshapeHeight();
            if (showListener != null) {
                showListener.onShow(dialog);
            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public void dismiss() {
        showingAfterDelay = false;
        if (_dialogView != null && outAnim != null) {
            _dialogView.startAnimation(outAnim);
        } else {
            super.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure the layout
        ViewGroup.LayoutParams layoutParams = _layout.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        _layout.setLayoutParams(layoutParams);
        _layout.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        // Config the root dialog layout
        ViewGroup.LayoutParams _dialogLayoutParams = _dialogView.getLayoutParams();
        _dialogLayoutParams.width = width;
        _dialogLayoutParams.height = height;
        if (_dialogLayoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLp = (ViewGroup.MarginLayoutParams) _dialogLayoutParams;
            marginLp.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        }
        _dialogView.setLayoutParams(_dialogLayoutParams);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (_dialogView == null) return;

        // Show dialog with animation in case the animation is available and the showing status is not immediately
        if (inAnim != null && !showImmediately) {
            _dialogView.startAnimation(inAnim);
        }
        showImmediately = false;

        // Animate the lottie icon
        if (_lottieIconView != null && _lottieIconView.getVisibility() == View.VISIBLE) {
            _lottieIconView.post(new Runnable() {
                @Override
                public void run() {
                    _lottieIconView.playAnimation();
                }
            });
        }
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        _titleView.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getString(titleId));
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(layoutResID, null);
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
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                _bodyHolder.addView(view, layoutParams);
            }
        }
    }

    @Override
    public void setOnShowListener(@Nullable OnShowListener listener) {
        this.showListener = listener;
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Set content view
     *
     * @param layoutResID layout resource ID
     * @param params      layout params
     */
    public void setContentView(@LayoutRes int layoutResID, @Nullable ViewGroup.LayoutParams params) {
        if (layoutResID != 0) {
            View view = getLayoutInflater().inflate(layoutResID, null);
            setContentView(view, params);
        }
    }

    /**
     * @return bundle data
     */
    public CWBundle bundle() {
        return bundle;
    }

    /**
     * @return dialog's layout
     */
    public ViewGroup getLayout() {
        return _layout;
    }

    /**
     * Return holder view which is parent layout of content view
     *
     * @return content holder layout
     */
    public ViewGroup getContentHolder() {
        return _bodyHolder;
    }

    /**
     * @return dialog's width
     */
    public int getWidth() {
        if (_layout != null) {
            _layout.getWidth();
        }
        return width;
    }

    /**
     * Set dialog's width
     *
     * @param width pixel value
     * @return dialog
     */
    public WindDialog setWidth(int width) {
        this.width = width;
        return this;
    }

    /**
     * @return dialog's height
     */
    public int getHeight() {
        if (_layout != null) {
            return _layout.getHeight();
        }
        return height;
    }

    /**
     * Set dialog's height
     *
     * @param height dialog's height
     * @return dialog
     */
    public WindDialog setHeight(int height) {
        this.height = height;
        return this;
    }

    /**
     * Set max height
     *
     * @param maxHeight max height
     * @return max height
     */
    public WindDialog setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    /**
     * Get dialog's padding
     *
     * @return [left, top, right, bottom]
     */
    public int[] getPadding() {
        return new int[]{paddingLeft, paddingTop, paddingRight, paddingBottom};
    }

    /**
     * Set padding.
     * The padding value should be "getResources().getDimension(R.dimen.padding)"
     *
     * @param left   padding left
     * @param top    padding top
     * @param right  padding right
     * @param bottom padding bottom
     * @return dialog
     */
    public WindDialog setPadding(int left, int top, int right, int bottom) {
        paddingLeft = left;
        paddingTop = top;
        paddingRight = right;
        paddingBottom = bottom;
        return this;
    }

    /**
     * Set padding left
     * The padding value should be "getResources().getDimension(R.dimen.padding)"
     *
     * @param left padding left.
     * @return dialog
     */
    public WindDialog setPaddingLeft(int left) {
        paddingLeft = left;
        return this;
    }

    /**
     * Set padding top
     * The padding value should be "getResources().getDimension(R.dimen.padding)"
     *
     * @param top padding top
     * @return dialog
     */
    public WindDialog setPaddingTop(int top) {
        paddingTop = top;
        return this;
    }

    /**
     * Set padding right
     * The padding value should be "getResources().getDimension(R.dimen.padding)"
     *
     * @param right padding right.
     * @return dialog
     */
    public WindDialog setPaddingRight(int right) {
        paddingRight = right;
        return this;
    }

    /**
     * Set padding bottom
     * The padding value should be "getResources().getDimension(R.dimen.padding)"
     *
     * @param bottom padding bottom.
     * @return dialog
     */
    public WindDialog setPaddingBottom(int bottom) {
        paddingBottom = bottom;
        return this;
    }

    /**
     * Set margin
     * The margin value should be "getResources().getDimension(R.dimen.padding)"
     *
     * @param left   margin left
     * @param top    margin top
     * @param right  margin right
     * @param bottom margin bottom
     * @return dialog
     */
    public WindDialog setMargin(int left, int top, int right, int bottom) {
        marginLeft = left;
        marginTop = top;
        marginRight = right;
        marginBottom = bottom;
        return this;
    }

    /**
     * Set margin left
     * The margin value should be "getResources().getDimension(R.dimen.margin)"
     *
     * @param left padding left.
     * @return dialog
     */
    public WindDialog setMarginLeft(int left) {
        marginLeft = left;
        return this;
    }

    /**
     * Set margin top
     * The margin value should be "getResources().getDimension(R.dimen.margin)"
     *
     * @param top padding top.
     * @return dialog
     */
    public WindDialog setMarginTop(int top) {
        marginTop = top;
        return this;
    }

    /**
     * Set margin right
     * The margin value should be "getResources().getDimension(R.dimen.margin)"
     *
     * @param right padding right.
     * @return dialog
     */
    public WindDialog setMarginRight(int right) {
        marginRight = right;
        return this;
    }

    /**
     * Set margin bottom
     * The margin value should be "getResources().getDimension(R.dimen.margin)"
     *
     * @param bottom padding bottom.
     * @return dialog
     */
    public WindDialog setMarginBottom(int bottom) {
        marginBottom = bottom;
        return this;
    }

    /**
     * Set dialog's gravity
     *
     * @param gravity gravity.
     * @return dialog
     *
     * @see {{{@link Gravity}}}
     */
    public WindDialog setGravity(int gravity) {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = gravity;
            window.setAttributes(wlp);
        }
        return this;
    }

    /**
     * Set inout animation
     *
     * @param animType in/out animation type
     * @return dialog
     */
    public WindDialog setInOutAnimType(InOutAnimType animType) {
        if (animType == null) /* clear inout animation */ {
            inAnim = null;
            outAnim = null;
            return this;
        }

        // set new animation
        inAnim = animType.getInAnim(getContext());
        outAnim = animType.getOutAnim(getContext());
        outAnim.setAnimationListener(new Animation.AnimationListener() {
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
                        if (isActivityFinishing()) return;
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
        if (_lottieIconView.getVisibility() == View.VISIBLE) {
            return _lottieIconView;
        }
        return _iconView;
    }

    /**
     * Set icon's visibility
     *
     * @param visible true/false
     * @return dialog
     */
    public WindDialog setIconVisible(boolean visible) {
        iconVisible = visible;
        if (_iconView != null && _lottieIconView != null) {
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
    public WindDialog setIcon(@DrawableRes int resId) {
        iconResId = resId;
        if (_iconView != null && _lottieIconView != null) {
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
        iconBitmap = bitmap;
        if (_iconView != null && _lottieIconView != null) {
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
    public WindDialog setLottieIcon(@RawRes int resId) {
        lottieIconResId = resId;
        if (_iconView != null && _lottieIconView != null) {
            judgeIcon();
        }
        return this;
    }

    /**
     * Judge which kind of icon will be used (inline/static/animation)
     */
    private void judgeIcon() {
        if (!iconVisible) {
            _iconView.setVisibility(View.GONE);
            _lottieIconView.setVisibility(View.GONE);
            return;
        }

        // lottie icon -> static icon -> bitmap icon
        boolean useAnim;
        if (lottieIconResId != 0) {
            // use animation icon
            useAnim = true;
            _lottieIconView.setAnimation(lottieIconResId);

        } else if (iconResId != 0) {
            // use static icon
            useAnim = false;
            _iconView.setImageResource(iconResId);

        } else if (iconBitmap != null) {
            // use bitmap icon
            useAnim = false;
            _iconView.setImageBitmap(iconBitmap);
        } else {
            // hide icon
            _lottieIconView.setVisibility(View.GONE);
            _iconView.setVisibility(View.GONE);
            return;
        }
        _lottieIconView.setVisibility(useAnim ? View.VISIBLE : View.GONE);
        _iconView.setVisibility(!useAnim ? View.VISIBLE : View.GONE);
    }

    /**
     * @return title text view
     */
    public TextView titleView() {
        return _titleView;
    }

    /**
     * Set title visibility
     *
     * @param visible true/false
     * @return dialog
     */
    public WindDialog setTitleVisible(boolean visible) {
        _titleLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * @return sub title view
     */
    public TextView subTitleView() {
        return _subTitleView;
    }

    /**
     * Set sub title text
     *
     * @param subTitle sub title text
     * @return dialog
     */
    public WindDialog setSubTitle(@Nullable CharSequence subTitle) {
        _subTitleView.setText(subTitle);
        if (subTitle != null) {
            if (subTitle.toString().trim().isEmpty()) {
                _subTitleView.setVisibility(View.GONE);
            } else {
                _subTitleView.setVisibility(View.VISIBLE);
            }
        }
        return this;
    }

    /**
     * Set sub title
     *
     * @param subTitleResId sub title resource string
     * @return dialog
     */
    public WindDialog setSubTitle(@StringRes int subTitleResId) {
        return setSubTitle(getContext().getString(subTitleResId));
    }

    /**
     * @return body view
     */
    public View contentView() {
        return _customContentView;
    }

    /**
     * Set content view visibility
     *
     * @param visible true/false
     * @return dialog
     */
    public WindDialog setContentViewVisible(boolean visible) {
        _bodyHolder.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * Set content text
     * Available for tatsumaki layout only
     *
     * @param resId string resource id
     * @return dialog
     */
    public WindDialog setContentText(@StringRes int resId) {
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
        if (LayoutType.TATSUMAKI.equals(layoutType)) {
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
     * @param idx the position at which to add the button
     * @param btn button
     * @return added button
     */
    public Button addButton(int idx, Button btn) {
        _footerHolder.addView(btn, idx);
        if (idx < 0) {
            _btnList.add(btn);
        } else {
            _btnList.add(idx, btn);
        }
        return btn;
    }

    /**
     * Add button
     *
     * @param btn button
     * @return added button
     */
    public Button addButton(Button btn) {
        return addButton(-1, btn);
    }

    /**
     * Add button
     *
     * @param idx  the position at which to add the button
     * @param type button type
     * @param text text
     * @param icon inline icon
     * @return added button
     */
    public Button addButton(int idx, @NonNull Button.Type type, @NonNull CharSequence text, Button.InlineIcon icon) {
        Button btn = new Button(getContext());
        int margin = (int) getContext().getResources().getDimension(R.dimen.wl_spacing_level_1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 0;
        params.setMargins(margin, 0, margin, 0);
        btn.setLayoutParams(params);
        btn.setMinimumWidth((int) getContext().getResources().getDimension(R.dimen.wl_dialog_button_min_width));
        btn.setType(type);
        btn.setText(text);
        if (icon != null) {
            btn.setIconType(icon);
        }
        btn.setOnClickListener(v -> dismiss());
        return addButton(idx, btn);
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
        return addButton(-1, type, text, icon);
    }

    /**
     * Set button text
     *
     * @param idx   the button number
     * @param resId string resource id
     * @return dialog
     */
    public WindDialog setButtonText(int idx, @StringRes int resId) {
        return setButtonText(idx, getContext().getString(resId));
    }

    /**
     * Set button text
     *
     * @param idx  @param idx the button number
     * @param text string value
     * @return dialog
     */
    public WindDialog setButtonText(int idx, CharSequence text) {
        if (idx < _btnList.size()) {
            _btnList.get(idx).setText(text);
        }
        return this;
    }

    /**
     * @return header layout
     */
    public ViewGroup headerLayout() {
        return _headerHolder;
    }

    /**
     * Add custom view to header
     *
     * @param view view
     * @return inserted view
     */
    public View addViewToHeader(View view) {
        _headerHolder.addView(view);
        return view;
    }

    /**
     * Add custom view to footer
     *
     * @param idx  the position at which to add the button
     * @param view view
     * @return inserted view
     */
    public View addViewToFooter(int idx, View view) {
        _footerHolder.addView(view, idx);
        return view;
    }

    /**
     * @return footer layout
     */
    public ViewGroup footerLayout() {
        return _footerHolder;
    }

    /**
     * Add custom view to footer
     *
     * @param view view
     * @return inserted view
     */
    public View addViewToFooter(View view) {
        return addViewToFooter(-1, view);
    }

    /**
     * Set custom waiting icon
     *
     * @param resId resource lottie animation id
     * @return dialog
     */
    public WindDialog setCustomWaitingIcon(@RawRes int resId) {
        _waitingIcon.setAnimation(resId);
        return this;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Check if this dialog is initiated in given context or not
     *
     * @param context activity context
     * @return true if in context otherwise return false
     */
    public boolean isInContext(Context context) {
        Context dialogContext = getContext();
        if (dialogContext.equals(context)) return true;
        if (dialogContext instanceof ContextWrapper) {
            return ((ContextWrapper) dialogContext).getBaseContext().equals(context);
        }
        return false;
    }

    /**
     * Check if activity is destroying or not
     *
     * @return true/false
     */
    public boolean isActivityFinishing() {
        Context ctx = getContext();
        if (ctx instanceof Activity) {
            return ((Activity) ctx).isFinishing();

        } else if (ctx instanceof ContextWrapper) {
            Context ctxWrapper = ((ContextWrapper) ctx).getBaseContext();
            if (ctxWrapper instanceof Activity) {
                return ((Activity) ctxWrapper).isFinishing();
            }
        }
        return false;
    }

    /**
     * Show dialog with timeout in milliseconds.
     * It means that after timeout, dialog will be auto dismissed
     *
     * @param timeout in milliseconds
     */
    public void showTimeout(long timeout) {
        showTimeout(timeout, null);
    }

    /**
     * Show dialog with timeout in milliseconds.
     * It means that after timeout, dialog will be auto dismissed
     *
     * @param timeout  in milliseconds
     * @param callback callback when timeout
     */
    public void showTimeout(long timeout, CWCallback<?> callback) {
        show();
        postDelayed(() -> {
            if (isActivityFinishing()) return;
            if (callback != null) callback.onEnd();
            dismiss();
        }, timeout);
    }

    /**
     * Delay a given time before showing dialog
     *
     * @param delay milliseconds
     */
    public void showDelay(long delay) {
        showingAfterDelay = true;
        UiThread.postDelayed(() -> {
            if (isActivityFinishing()) return;
            if (showingAfterDelay) show();
        }, delay);
    }

    /**
     * Delay a given time before showing dialog
     *
     * @param delay   milliseconds
     * @param timeout auto close dialog when timeout
     */
    public void showDelay(long delay, long timeout) {
        showDelay(delay, timeout, null);
    }

    /**
     * Delay a given time before showing dialog
     *
     * @param delay   milliseconds
     * @param timeout auto close dialog when timeout
     */
    public void showDelay(long delay, long timeout, CWCallback<?> callback) {
        showingAfterDelay = true;
        UiThread.postDelayed(() -> {
            if (isActivityFinishing()) return;
            if (showingAfterDelay) showTimeout(timeout, callback);
        }, delay);
    }

    /**
     * Show dialog immediately without animation
     */
    public void showImmediately() {
        showImmediately = true;
        show();
    }

    /**
     * Show dialog immediately without animation
     * After timeout, the dialog will be auto dismissed
     *
     * @param timeout in milliseconds
     */
    public void showImmediately(long timeout) {
        showImmediately = true;
        showTimeout(timeout);
    }

    /**
     * Dismiss immediately
     */
    public void dismissImmediately() {
        showingAfterDelay = false;
        super.dismiss();
    }

    /**
     * Show waiting icon
     */
    public void waitMe() {
        // update waiting mask's size to current dialog size
        int width = _layout.getWidth();
        int height = _layout.getHeight();
        ViewGroup.LayoutParams layout = _waitingMask.getLayoutParams();
        layout.width = width;
        layout.height = height;
        _waitingMask.setLayoutParams(layout);

        int iconSize = (int) ((Math.min(width, height)) * 0.6f);
        int maxIconSize = (int) getContext().getResources().getDimension(R.dimen.wl_dialog_waiting_icon_size);
        iconSize = Math.min(iconSize, maxIconSize);
        ViewGroup.LayoutParams iconLayout = _waitingIcon.getLayoutParams();
        iconLayout.width = iconSize;
        iconLayout.height = iconSize;
        _waitingIcon.setLayoutParams(iconLayout);

        _waitingMask.setVisibility(View.VISIBLE);
        _waitingIcon.playAnimation();
    }

    /**
     * Show waiting icon with timeout in milliseconds.
     * It means that after timeout, the waiting will be auto dismissed
     *
     * @param timeout in milliseconds
     * @param quit    true -> close dialog else keep opening
     */
    public void waitMe(long timeout, final boolean quit) {
        waitMe();
        postDelayed(() -> {
            if (isActivityFinishing()) return;
            imDone(quit);
        }, timeout);
    }

    /**
     * Hide waiting icon
     *
     * @param quit true -> close dialog else keep opening
     */
    public void imDone(boolean quit) {
        if (quit) {
            dismissImmediately();
        }
        _waitingMask.setVisibility(View.GONE);
    }

    /**
     * Apply custom template.
     * This function is available for tatsumaki layout type only
     *
     * @param template dialog template
     * @return dialog
     */
    public WindDialog apply(ITemplate template) {
        if (LayoutType.TATSUMAKI.equals(layoutType)) {
            template.onSetting(this);
        }
        return this;
    }

    /**
     * Override post method of view
     *
     * @param action runnable
     * @return Returns true if the Runnable was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.
     */
    public boolean post(Runnable action) {
        return _dialogView.post(action);
    }

    /**
     * Override post method of view
     *
     * @param action      runnable
     * @param delayMillis The delay (in milliseconds) until the Runnable will be executed.
     * @return Returns true if the Runnable was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.
     */
    public boolean postDelayed(Runnable action, long delayMillis) {
        return _dialogView.postDelayed(action, delayMillis);
    }

    /**
     * Reshape dialog match with setting max height
     */
    private void reshapeHeight() {
        if (maxHeight > 0 && _layout.getHeight() > maxHeight) {
            // user set max height
            ViewGroup.LayoutParams layoutParams = _layout.getLayoutParams();
            layoutParams.height = maxHeight;
            _layout.setLayoutParams(layoutParams);

            ViewGroup.LayoutParams _dialogLayoutParams = _dialogView.getLayoutParams();
            _dialogLayoutParams.height = maxHeight;
            _dialogView.setLayoutParams(_dialogLayoutParams);
        }
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Dialog type
     */
    public enum LayoutType {
        TATSUMAKI(R.layout.wl_dialog_tatsumaki, R.layout.wl_dialog_tatsumaki_content),
        FUBUKI(R.layout.wl_dialog_fubuki, 0);

        private final int layout;
        private final int content;

        /**
         * Layout type
         *
         * @param layout  resource layout
         * @param content resource content layout
         */
        LayoutType(@LayoutRes int layout, @LayoutRes int content) {
            this.layout = layout;
            this.content = content;
        }

        /**
         * @return respective layout
         */
        @LayoutRes
        private int getDialogLayout() {
            return layout;
        }

        /**
         * @return custom content layout id
         */
        @LayoutRes
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
        SWEET_ALERT(R.anim.wl_dialog_in_anim_sweet_alert, R.anim.wl_dialog_out_anim_sweet_alert),
        FADE(R.anim.wl_dialog_in_anim_fade, R.anim.wl_dialog_out_anim_fade),
        SLIDE_LEFT_2_RIGHT(R.anim.wl_dialog_in_anim_slide_left_right, R.anim.wl_dialog_out_anim_slide_left_right),
        SLIDE_RIGHT_2_LEFT(R.anim.wl_dialog_in_anim_slide_right_left, R.anim.wl_dialog_out_anim_slide_right_left),
        SLIDE_TOP_2_BOTTOM(R.anim.wl_dialog_in_anim_slide_top_bottom, R.anim.wl_dialog_out_anim_slide_top_bottom),
        SLIDE_BOTTOM_2_TOP(R.anim.wl_dialog_in_anim_slide_bottom_top, R.anim.wl_dialog_out_anim_slide_bottom_top);

        private final int inAnimResId;
        private final int outAnimResId;

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

    /**
     * Dialog Template
     */
    public interface ITemplate {
        /**
         * On setting dialog
         *
         * @param dialog wind dialog
         */
        void onSetting(WindDialog dialog);
    }

}
