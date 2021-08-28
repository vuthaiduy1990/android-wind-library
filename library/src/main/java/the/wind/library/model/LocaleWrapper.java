package the.wind.library.model;

import android.content.Context;

import java.util.Locale;

import androidx.annotation.Nullable;
import the.wind.library.nlp.INLPText;

/**
 * Wrapper of locales
 */
public class LocaleWrapper implements INLPText {

    // locale instance
    private final Locale locale;
    private final String code;
    private final String displayText;

    /**
     * Constructor
     *
     * @param lc locale
     */
    public LocaleWrapper(Locale lc) {
        locale = lc;
        code = String.format("%s,%s,%s", locale.getLanguage(), locale.getCountry(), locale.getVariant());
        displayText = getLocaleDisplayText(lc);
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public String nlpTextId(@Nullable Context context) {
        return code;
    }

    @Override
    public String nlpRawText(@Nullable Context context) {
        return getDisplayText();
    }

    /* ---------------------- STATIC ------------------------- */

    /**
     * Get locale display text
     *
     * @param lc locale
     * @return display text
     */
    public static String getLocaleDisplayText(Locale lc) {
        Locale defaultLc = Locale.getDefault();
        String text = String.format("%s (%s)", lc.getDisplayCountry(defaultLc), lc.getDisplayLanguage(defaultLc));
        if (!lc.getVariant().isEmpty()) {
            text = String.format("%s (%s)", text, lc.getDisplayVariant(defaultLc));
        }
        return text;
    }

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Get locale instance
     *
     * @return locale instance
     */
    public Locale get() {
        return locale;
    }

    /**
     * Get unique code
     *
     * @return unique code
     */
    public String getCode() {
        return code;
    }

    /**
     * Get display text
     *
     * @return display text
     */
    public String getDisplayText() {
        return displayText;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */
}
