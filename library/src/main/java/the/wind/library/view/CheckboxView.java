package the.wind.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;

import the.wind.library.R;
import the.wind.library.utils.CWStringUtils;

/**
 * Checkbox view
 */
public class CheckboxView extends LottieAnimationView {

    // checkbox status. true -> checked else unchecked
    private boolean isChecked = false;

    // animation type
    private AnimType mAnimType = AnimType.DEFAULT_CIRCLE;

    // animation shape color
    private int mAnimColor;

    // listener
    private OnCheckedListener mCheckedListener = new OnCheckedListener() {
        @Override
        public void onChecked(View view, boolean checked) {

        }
    };

    public CheckboxView(Context context) {
        this(context, null);
    }

    public CheckboxView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckboxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.CheckboxView,
                0, 0);
        try {
            // get anim type;
            int animIdx = typeArray.getInt(R.styleable.CheckboxView_animType, 0);
            mAnimType = AnimType.values()[animIdx];
            // get anim color
            mAnimColor = typeArray.getColor(R.styleable.CheckboxView_animColor, 0);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            typeArray.recycle();
        }

        // config properties
        setAnimType(mAnimType);
        setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        setRepeatCount(0);
        setAnimColor(mAnimColor);

        // set event listeners
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAnimating()) {
                    return;
                }
                if (isChecked) {
                    setProgress(mAnimType.getInitialProgress());
                } else {
                    playAnimation();
                }
                isChecked = !isChecked;
                mCheckedListener.onChecked(v, isChecked);
            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- EVENT -------------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Set checked listener
     *
     * @param listener listener
     */
    public void setOnCheckedListener(OnCheckedListener listener) {
        mCheckedListener = listener;
    }

    /**
     * Get animation type
     *
     * @return animation type
     */
    public AnimType getAnimType() {
        return mAnimType;
    }

    /**
     * Set anim type
     *
     * @param type type
     */
    public void setAnimType(AnimType type) {
        mAnimType = type;
        setAnimation(type.getResource());
        setMinProgress(type.getMinProgress());
        setMaxProgress(type.getMaxProgress());
        setProgress(type.getInitialProgress());
    }

    /**
     * Set animation color
     *
     * @param color color
     */
    public void setAnimColor(int color) {
        mAnimColor = color;
        if (mAnimColor == 0 || mAnimType == null) return;
        if (mAnimType.getProperties() != null) {
            for (String prop : mAnimType.getProperties()) {
                if (!CWStringUtils.hasText(prop)) continue;
                addValueCallback(
                        new KeyPath(prop.trim(), "**"),
                        LottieProperty.COLOR, new LottieValueCallback<>(mAnimColor));
                addValueCallback(
                        new KeyPath(prop.trim(), "**"),
                        LottieProperty.STROKE_COLOR, new LottieValueCallback<>(mAnimColor));
            }
        }
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * AnimType
     */
    public enum AnimType {
        // https://lottiefiles.com/4964-check-mark-success-animation
        // VICTOR_VINNHED
        DEFAULT_CIRCLE(
                R.raw.checkbox_view_victor_vinnhed,
                0f, 46f / 73f, 24f / 73f,
                new String[]{"Rectangle 6 Copy"}),
        // https://lottiefiles.com/2492-check
        TAKAYA_DEGUCHI_1(
                R.raw.checkbox_view_takaya_deguchi,
                0f, 35f / 60f, 12f / 60f,
                new String[]{"circle fill", "circle stroke"}),
        TAKAYA_DEGUCHI_2(
                R.raw.checkbox_view_takaya_deguchi,
                0f, 35f / 60f, 25f / 60f,
                new String[]{"circle fill", "circle stroke"}),
        // https://lottiefiles.com/527-check
        VICTOR_KAI(
                R.raw.checkbox_view_victor_kai,
                0f, 23f / 60f, 6f / 60f,
                null),
        // https://lottiefiles.com/3253-uploading-and-done
        LORIN(
                R.raw.checkbox_view_lorin,
                140f / 210f, 180f / 210f, 159f / 210f,
                new String[]{"glod 6"}),
        // https://lottiefiles.com/1127-success
        DARIUS_AFCHAR(
                R.raw.checkbox_view_darius_afchar,
                0f, 34f / 45f, 14f / 45f,
                new String[]{"Shape Layer 1"}),
        // https://lottiefiles.com/13820-icon-check
        GILSON_SANTOS_1(
                R.raw.checkbox_view_gilson_santos,
                0f, 72f / 130f, 15f / 130f,
                new String[]{"circle-base"}
        ),
        GILSON_SANTOS_2(
                R.raw.checkbox_view_gilson_santos,
                0f, 72f / 130f, 22f / 130f,
                new String[]{"circle-base"}
        ),
        // https://lottiefiles.com/8729-checkbox-animation
        // VAISHAK_SHETTY_K
        DEFAULT_SQUARE(
                R.raw.checkbox_view_default_square,
                0f, 30f / 55f, 10f / 55f,
                new String[]{"Layer 3/check Outlines 2", "Layer 3/check Outlines"}
        ),
        // https://lottiefiles.com/8600-check-list
        // ILYA_PAVLOV
        DEFAULT_CHECKLIST(
                R.raw.checkbox_view_default_checklist,
                0f, 56f / 60f, 0f,
                new String[]{"Sheet"}
        );

        // animation resource
        private int resource;

        // progress
        private float minProgress;
        private float maxProgress;
        private float initialProgress;

        // props
        private String[] props;

        AnimType(int resource,
                 float minProgress, float maxProgress, float initialProgress,
                 String[] props) {

            this.resource = resource;
            this.minProgress = minProgress;
            this.maxProgress = maxProgress;
            this.initialProgress = initialProgress;
            this.props = props;
        }

        /**
         * Get animation resource
         *
         * @return animation resource
         */
        public int getResource() {
            return this.resource;
        }

        /**
         * Get max progress
         *
         * @return max progress
         */
        private float getMinProgress() {
            return this.minProgress;
        }

        /**
         * Get max progress
         *
         * @return max progress
         */
        private float getMaxProgress() {
            return this.maxProgress;
        }

        /**
         * Get initial progress
         *
         * @return initial progress
         */
        public float getInitialProgress() {
            return this.initialProgress;
        }

        /**
         * Get properties
         *
         * @return properties' name
         */
        public String[] getProperties() {
            return this.props;
        }
    }

    /**
     * Checked event
     */
    public interface OnCheckedListener {
        /**
         * Trigger when user clicks on checkbox view
         *
         * @param view    checkbox view
         * @param checked checked status
         */
        void onChecked(View view, boolean checked);
    }
}
