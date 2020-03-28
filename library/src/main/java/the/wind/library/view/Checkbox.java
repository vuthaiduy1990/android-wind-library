package the.wind.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;

import androidx.core.content.ContextCompat;
import the.wind.library.R;
import the.wind.library.utils.CWStringUtils;

/**
 * Checkbox view.
 * Basic usage:
 * <pre>
 *     <the.wind.library.view.Checkbox
 *          android:layout_width="wrap_content"
 *          android:layout_height="wrap_content"
 *          app:animColor="#3191bf"
 *          app:animType="VICTOR_KAI" />
 * </pre>
 */
public class Checkbox extends LottieAnimationView {

    // checkbox status. true -> checked else unchecked
    private boolean mChecked = false;

    // animation type
    private AnimType mAnimType = AnimType.DEFAULT_CIRCLE;

    // animation shape color
    private int mAnimColor;

    // listener
    private OnCheckedListener mCheckedListener;

    public Checkbox(Context context) {
        this(context, null);
    }

    public Checkbox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Checkbox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.Checkbox,
                0, 0);
        try {
            // get anim type;
            int animIdx = typeArray.getInt(R.styleable.Checkbox_animType, 0);
            mAnimType = AnimType.values()[animIdx];
            // get anim color
            mAnimColor = typeArray.getColor(R.styleable.Checkbox_animColor, 0);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            typeArray.recycle();
        }

        // config properties
        setAnimType(mAnimType);
        setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        setRepeatCount(0);
        if (mAnimColor == 0) {
            mAnimColor = ContextCompat.getColor(getContext(), R.color.success);
        }
        setAnimColor(mAnimColor);

        // set event listeners
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAnimating()) {
                    return;
                }
                if (mChecked) {
                    setProgress(mAnimType.getInitialProgress());
                } else {
                    playAnimation();
                }
                mChecked = !mChecked;
                if (mCheckedListener != null) mCheckedListener.onChecked(v, mChecked);
            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    protected void onAttachedToWindow() {
        // set default width/height
        setDefaultSize();
        super.onAttachedToWindow();
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT -----------------------*/

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Check if the checkbox is checked or not
     *
     * @return true if checked else return false
     */
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * Set checked
     *
     * @param checked true -> checked
     * @return checkbox
     */
    public Checkbox setChecked(boolean checked) {
        mChecked = checked;
        if (mChecked) {
            setProgress(mAnimType.getMaxProgress());
        } else {
            setProgress(mAnimType.getInitialProgress());
        }
        return this;
    }

    /**
     * Set checked listener
     *
     * @param listener listener
     * @return checkbox
     */
    public Checkbox setOnCheckedListener(OnCheckedListener listener) {
        mCheckedListener = listener;
        return this;
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
     * @return checkbox
     */
    public Checkbox setAnimType(AnimType type) {
        mAnimType = type;
        setAnimation(type.getResource());
        setMinProgress(type.getMinProgress());
        setMaxProgress(type.getMaxProgress());
        setProgress(type.getInitialProgress());
        return this;
    }

    /**
     * Set animation color
     *
     * @param color color
     * @return checkbox
     */
    public Checkbox setAnimColor(int color) {
        mAnimColor = color;
        if (mAnimColor == 0 || mAnimType == null) return this;
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
        return this;
    }

    /**
     * Set default size
     */
    protected void setDefaultSize() {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams.width == -2 && layoutParams.height == -2) {
            int size = mAnimType.getDefaultSize(getContext());
            layoutParams.width = size;
            layoutParams.height = size;
            setLayoutParams(layoutParams);
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
                R.dimen.checkbox_vinnhed_size,
                R.raw.checkbox_victor_vinnhed,
                0f, 46f / 72f, 24f / 72f,
                new String[]{"Rectangle 6 Copy"}),
        // https://lottiefiles.com/2492-check
        TAKAYA_DEGUCHI_1(
                R.dimen.checkbox_takaya_deguchi_size,
                R.raw.checkbox_takaya_deguchi,
                0f, 35f / 60f, 12f / 60f,
                new String[]{"circle fill", "circle stroke"}),
        TAKAYA_DEGUCHI_2(
                R.dimen.checkbox_takaya_deguchi_size,
                R.raw.checkbox_takaya_deguchi,
                0f, 35f / 60f, 25f / 60f,
                new String[]{"circle fill", "circle stroke"}),
        // https://lottiefiles.com/527-check
        VICTOR_KAI(
                R.dimen.checkbox_victor_kai_size,
                R.raw.checkbox_victor_kai,
                0f, 23f / 60f, 17f / 60f,
                new String[]{"Shape Layer 4"}
        ),
        // https://lottiefiles.com/3253-uploading-and-done
        LORIN(
                R.dimen.checkbox_lorin_size,
                R.raw.checkbox_lorin,
                145f / 210f, 180f / 210f, 159f / 210f,
                new String[]{"glod 6"}),
        // https://lottiefiles.com/1127-success
        DARIUS_AFCHAR(
                R.dimen.checkbox_darius_afchar_size,
                R.raw.checkbox_darius_afchar,
                0f, 34f / 45f, 14f / 45f,
                new String[]{"Shape Layer 1"}),
        // https://lottiefiles.com/13820-icon-check
        GILSON_SANTOS_1(
                R.dimen.checkbox_gilson_santos_size,
                R.raw.checkbox_gilson_santos,
                0f, 72f / 130f, 15f / 130f,
                new String[]{"circle-base"}
        ),
        GILSON_SANTOS_2(
                R.dimen.checkbox_gilson_santos_size,
                R.raw.checkbox_gilson_santos,
                0f, 72f / 130f, 22f / 130f,
                new String[]{"circle-base"}
        ),
        // https://lottiefiles.com/9613-tick
        AVIRAL_BAHUGUNA(
                R.dimen.checkbox_aviral_bahuguna_size,
                R.raw.checkbox_aviral_bahuguna,
                0f, 35f / 40f, 16f / 40f,
                new String[]{"Circle Stroke", "Circle Green Fill", "Circle Flash"}
        ),
        // https://lottiefiles.com/8729-checkbox-animation
        // VAISHAK_SHETTY_K
        DEFAULT_SQUARE(
                R.dimen.checkbox_default_square_size,
                R.raw.checkbox_default_square,
                0f, 30f / 55f, 10f / 55f,
                new String[]{"Layer 3/check Outlines 2", "Layer 3/check Outlines"}
        ),
        // https://lottiefiles.com/8600-check-list
        // ILYA_PAVLOV
        DEFAULT_CHECKLIST(
                R.dimen.checkbox_default_checklist_size,
                R.raw.checkbox_default_checklist,
                0f, 56f / 60f, 0f,
                new String[]{"Sheet"}
        );

        // default size
        private int defaultSize;

        // animation resource
        private int resource;

        // progress
        private float minProgress;
        private float maxProgress;
        private float initialProgress;

        // props
        private String[] props;

        AnimType(int defaultSize,
                 int resource,
                 float minProgress, float maxProgress, float initialProgress,
                 String[] props) {
            this.defaultSize = defaultSize;
            this.resource = resource;
            this.minProgress = minProgress;
            this.maxProgress = maxProgress;
            this.initialProgress = initialProgress;
            this.props = props;
        }

        /**
         * Get default size
         *
         * @param context application context
         * @return default size
         */
        public int getDefaultSize(Context context) {
            return (int) context.getResources().getDimension(defaultSize);
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
