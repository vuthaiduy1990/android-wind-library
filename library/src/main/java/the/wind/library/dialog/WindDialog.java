package the.wind.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import the.wind.library.R;

public class WindDialog extends Dialog {

    // views
    @Nullable
    private View _dialogView;

    // model
    private Type mType;
    private Timer mTimer;

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
        this(context, Type.TATSUMAKI);
    }

    /**
     * Constructor
     *
     * @param context application context
     * @param type    dialog type
     */
    public WindDialog(@NonNull Context context, Type type) {
        super(context, R.style.wind_dialog);
        mType = type;
        setInOutAnimType(InOutAnimType.SWEET_ALERT);
        mTimer = new Timer();
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mType.getLayout());
        if (getWindow() != null) {
            _dialogView = Objects.requireNonNull(getWindow()).getDecorView().findViewById(android.R.id.content);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (_dialogView != null && mInAnim != null) {
            _dialogView.startAnimation(mInAnim);
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

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- EVENT -------------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Set inout animation
     *
     * @param animType in/out animation type
     */
    public void setInOutAnimType(InOutAnimType animType) {
        if (animType == null) /* clear inout animation */ {
            mInAnim = null;
            mOutAnim = null;
            return;
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
                dismiss();
            }
        }, timeout);
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Dialog type
     */
    public enum Type {
        TATSUMAKI(R.layout.wind_dialog_tatsumaki),
        FUBUKI(R.layout.wind_dialog_fubuki);

        private int layout;

        Type(int layout) {
            this.layout = layout;
        }

        /**
         * @return respective layout
         */
        private int getLayout() {
            return layout;
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
