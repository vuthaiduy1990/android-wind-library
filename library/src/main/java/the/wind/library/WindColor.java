package the.wind.library;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

/**
 * Thanks to
 * https://github.com/edelstone/tints-and-shades
 * https://visme.co/blog/website-color-schemes/
 * http://colorsafe.co/
 * https://jxnblk.github.io/colorable/demos/text
 */
public final class WindColor {

    public static final WindColor PRIMARY = WindColor.fromHex("#3191bf");
    public static final WindColor SUCCESS = WindColor.fromHex("#41b3a3");
    public static final WindColor INFO = WindColor.fromHex("#30a3b2");
    public static final WindColor HIGHLIGHT = WindColor.fromHex("#ecd06f");
    public static final WindColor WARNING = WindColor.fromHex("#f4bb1a");
    public static final WindColor DANGER = WindColor.fromHex("#e26060");
    public static final WindColor NEUTRAL = WindColor.fromHex("#e8a87c");
    public static final WindColor LIGHT = WindColor.fromHex("#dfdfde");
    public static final WindColor GRAY = WindColor.fromHex("#afafaf");
    public static final WindColor DARK = WindColor.fromHex("#191919");
    public static final WindColor PURPLE = WindColor.fromHex("#c38d9e");
    public static final WindColor PEA = WindColor.fromHex("#c3cb71");

    // shade factors
    private static final float[] SHADE_FACTORS = new float[]{
            0.9f, 0.8f, 0.7f, 0.6f, 0.5f
    };
    // tint factors
    private static final float[] TINT_FACTORS = new float[]{
            0.1f, 0.2f, 0.3f, 0.4f, 0.5f
    };
    private static final int BALANCE_COLOR_NUM = 5;

    // int ARGB color
    // https://developer.android.com/reference/android/graphics/Color
    private int mColor;

    // shades and tints
    private WindColor[] mShades;
    private int[] mShadeValues;
    private WindColor[] mTints;
    private int[] mTintValues;
    private WindColor[] mBalances;
    private int[] mBalancesValues;

    /**
     * Constructor
     *
     * @param color color
     */
    private WindColor(int color) {
        mColor = color;
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /**
     * Convert hex string to wind color
     *
     * @return wind color
     */
    public static WindColor fromHex(String hex) {
        return new WindColor(Color.parseColor(hex));
    }

    /**
     * Convert ARGB to wind color
     *
     * @return wind color
     */
    public static WindColor fromRgb(int r, int g, int b) {
        return new WindColor(Color.rgb(r, g, b));
    }

    /**
     * Convert resource color to wind color
     *
     * @param context  application context
     * @param colorRes resource color
     * @return wind color
     */
    public static WindColor fromRes(Context context, int colorRes) {
        return new WindColor(ContextCompat.getColor(context, colorRes));
    }

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return value value
     */
    public int value() {
        return mColor;
    }

    /**
     * Get hex color
     *
     * @return hex color
     */
    public String toHex() {
        return String.format("#%06X", (0xFFFFFF & mColor));
    }

    /**
     * Array of 5 shades
     *
     * @return array of shades
     */
    public WindColor[] shades() {
        if (mShades == null) {
            genShades();
        }
        return mShades;
    }

    /**
     * Array of 5 shades
     *
     * @return array of shades
     */
    public int[] shadeValues() {
        shades();
        return mShadeValues;
    }

    /**
     * Array of 5 tints
     *
     * @return array of tints
     */
    public WindColor[] tints() {
        if (mTints == null) {
            genTints();
        }
        return mTints;
    }

    /**
     * Array of 5 tints
     *
     * @return tints
     */
    public int[] tintValues() {
        tints();
        return mTintValues;
    }

    /**
     * Array of 5 balances
     *
     * @return balanced colors
     */
    public WindColor[] balances() {
        if (mBalances == null) {
            genBalances();
        }
        return mBalances;
    }

    /**
     * Array of 5 balances
     *
     * @return balanced colors
     */
    public int[] balancesValues() {
        balances();
        return mBalancesValues;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Generate shade colors
     */
    private void genShades() {
        int r = Color.red(mColor);
        int g = Color.green(mColor);
        int b = Color.blue(mColor);

        mShades = new WindColor[SHADE_FACTORS.length];
        mShadeValues = new int[SHADE_FACTORS.length];
        for (int i = 0; i < SHADE_FACTORS.length; i++) {
            float factor = SHADE_FACTORS[i];
            mShades[i] = WindColor.fromRgb(
                    Math.round(r * factor),
                    Math.round(g * factor),
                    Math.round(b * factor)
            );
            mShadeValues[i] = mShades[i].value();
        }
    }

    /**
     * Generate tint colors
     */
    private void genTints() {
        int r = Color.red(mColor);
        int g = Color.green(mColor);
        int b = Color.blue(mColor);

        mTints = new WindColor[TINT_FACTORS.length];
        mTintValues = new int[TINT_FACTORS.length];
        for (int i = 0; i < TINT_FACTORS.length; i++) {
            float factor = TINT_FACTORS[i];
            mTints[i] = WindColor.fromRgb(
                    Math.round(r + (255 - r) * factor),
                    Math.round(g + (255 - g) * factor),
                    Math.round(b + (255 - b) * factor)
            );
            mTintValues[i] = mTints[i].value();
        }
    }

    /**
     * Generate balanced colors
     */
    private void genBalances() {
        if (mShades == null) {
            genShades();
        }
        if (mTints == null) {
            genTints();
        }
        mBalances = new WindColor[BALANCE_COLOR_NUM];
        mBalancesValues = new int[BALANCE_COLOR_NUM];
        int middle = BALANCE_COLOR_NUM / 2;
        mBalances[middle] = this;
        mBalancesValues[middle] = value();
        for (int i = 0; i < middle; i++) {
            mBalances[i] = mShades[middle - i - 1];
            mBalancesValues[i] = mShadeValues[middle - i - 1];
            mBalances[middle + i + 1] = mTints[i];
            mBalancesValues[middle + i + 1] = mTintValues[i];
        }
    }

    /* ---------------------- INNER CLASS -------------------- */
}
