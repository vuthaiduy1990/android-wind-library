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
    public static final WindColor LIGHT = WindColor.fromHex("#e5e5e4");
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
    private final int color;

    // shades and tints
    private WindColor[] shades;
    private int[] shadeValues;
    private WindColor[] tints;
    private int[] tintValues;
    private WindColor[] balances;
    private int[] balancesValues;

    /**
     * Constructor
     *
     * @param color color
     */
    private WindColor(int color) {
        this.color = color;
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
        return color;
    }

    /**
     * Get hex color
     *
     * @return hex color
     */
    public String toHex() {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    /**
     * Array of 5 shades
     *
     * @return array of shades
     */
    public WindColor[] shades() {
        if (shades == null) {
            genShades();
        }
        return shades;
    }

    /**
     * Array of 5 shades
     *
     * @return array of shades
     */
    public int[] shadeValues() {
        shades();
        return shadeValues;
    }

    /**
     * Array of 5 tints
     *
     * @return array of tints
     */
    public WindColor[] tints() {
        if (tints == null) {
            genTints();
        }
        return tints;
    }

    /**
     * Array of 5 tints
     *
     * @return tints
     */
    public int[] tintValues() {
        tints();
        return tintValues;
    }

    /**
     * Array of 5 balances
     *
     * @return balanced colors
     */
    public WindColor[] balances() {
        if (balances == null) {
            genBalances();
        }
        return balances;
    }

    /**
     * Array of 5 balances
     *
     * @return balanced colors
     */
    public int[] balancesValues() {
        balances();
        return balancesValues;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Generate shade colors
     */
    private void genShades() {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        shades = new WindColor[SHADE_FACTORS.length];
        shadeValues = new int[SHADE_FACTORS.length];
        for (int i = 0; i < SHADE_FACTORS.length; i++) {
            float factor = SHADE_FACTORS[i];
            shades[i] = WindColor.fromRgb(
                    Math.round(r * factor),
                    Math.round(g * factor),
                    Math.round(b * factor)
            );
            shadeValues[i] = shades[i].value();
        }
    }

    /**
     * Generate tint colors
     */
    private void genTints() {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        tints = new WindColor[TINT_FACTORS.length];
        tintValues = new int[TINT_FACTORS.length];
        for (int i = 0; i < TINT_FACTORS.length; i++) {
            float factor = TINT_FACTORS[i];
            tints[i] = WindColor.fromRgb(
                    Math.round(r + (255 - r) * factor),
                    Math.round(g + (255 - g) * factor),
                    Math.round(b + (255 - b) * factor)
            );
            tintValues[i] = tints[i].value();
        }
    }

    /**
     * Generate balanced colors
     */
    private void genBalances() {
        if (shades == null) {
            genShades();
        }
        if (tints == null) {
            genTints();
        }
        balances = new WindColor[BALANCE_COLOR_NUM];
        balancesValues = new int[BALANCE_COLOR_NUM];
        int middle = BALANCE_COLOR_NUM / 2;
        balances[middle] = this;
        balancesValues[middle] = value();
        for (int i = 0; i < middle; i++) {
            balances[i] = shades[middle - i - 1];
            balancesValues[i] = shadeValues[middle - i - 1];
            balances[middle + i + 1] = tints[i];
            balancesValues[middle + i + 1] = tintValues[i];
        }
    }

    /* ---------------------- INNER CLASS -------------------- */
}
