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
public final class CWindColor {

    public static final CWindColor PRIMARY = CWindColor.fromHex("#3191bf");
    public static final CWindColor SUCCESS = CWindColor.fromHex("#41b3a3");
    public static final CWindColor INFO = CWindColor.fromHex("#30a3b2");
    public static final CWindColor HIGHLIGHT = CWindColor.fromHex("#ecd06f");
    public static final CWindColor WARNING = CWindColor.fromHex("#f4bb1a");
    public static final CWindColor DANGER = CWindColor.fromHex("#e26060");
    public static final CWindColor NEUTRAL = CWindColor.fromHex("#e8a87c");
    public static final CWindColor LIGHT = CWindColor.fromHex("#dfdfde");
    public static final CWindColor GRAY = CWindColor.fromHex("#afafaf");
    public static final CWindColor DARK = CWindColor.fromHex("#191919");
    public static final CWindColor PURPLE = CWindColor.fromHex("#c38d9e");
    public static final CWindColor PEA = CWindColor.fromHex("#c3cb71");

    // shade factors
    private static final float[] SHADE_FACTORS = new float[]{
            0.9f, 0.8f, 0.7f, 0.6f, 0.5f
    };
    // tint factors
    private static final float[] TINT_FACTORS = new float[]{
            0.1f, 0.2f, 0.3f, 0.4f, 0.5f
    };
    // int ARGB color
    // https://developer.android.com/reference/android/graphics/Color
    private int mColor;

    // shades and tints
    private CWindColor[] mShades;
    private int[] mShadeValues;
    private CWindColor[] mTints;
    private int[] mTintValues;

    /**
     * Constructor
     *
     * @param color color
     */
    private CWindColor(int color) {
        mColor = color;
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /**
     * Convert hex string to wind color
     *
     * @return wind color
     */
    public static CWindColor fromHex(String hex) {
        return new CWindColor(Color.parseColor(hex));
    }

    /**
     * Convert ARGB to wind color
     *
     * @return wind color
     */
    public static CWindColor fromRgb(int r, int g, int b) {
        return new CWindColor(Color.rgb(r, g, b));
    }

    /**
     * Convert resource color to wind color
     *
     * @param context  application context
     * @param colorRes resource color
     * @return wind color
     */
    public static CWindColor fromRes(Context context, int colorRes) {
        return new CWindColor(ContextCompat.getColor(context, colorRes));
    }

    /* ---------------------- ABSTRACT -----------------------*/

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
    public CWindColor[] shades() {
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
    public CWindColor[] tints() {
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

    /* ---------------------- METHOD ------------------------- */

    /**
     * Generate shade colors
     */
    private void genShades() {
        int r = Color.red(mColor);
        int g = Color.green(mColor);
        int b = Color.blue(mColor);

        mShades = new CWindColor[SHADE_FACTORS.length];
        mShadeValues = new int[SHADE_FACTORS.length];
        for (int i = 0; i < SHADE_FACTORS.length; i++) {
            float factor = SHADE_FACTORS[i];
            mShades[i] = CWindColor.fromRgb(
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

        mTints = new CWindColor[TINT_FACTORS.length];
        mTintValues = new int[TINT_FACTORS.length];
        for (int i = 0; i < TINT_FACTORS.length; i++) {
            float factor = TINT_FACTORS[i];
            mTints[i] = CWindColor.fromRgb(
                    Math.round(r + (255 - r) * factor),
                    Math.round(g + (255 - g) * factor),
                    Math.round(b + (255 - b) * factor)
            );
            mTintValues[i] = mTints[i].value();
        }
    }

    /* ---------------------- INNER CLASS -------------------- */
}
