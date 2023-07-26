package the.wind.library.model;

import android.content.Context;

import java.util.Currency;
import java.util.Locale;

import androidx.annotation.Nullable;
import the.wind.library.nlp.INLPText;

/**
 * Wrapper of currency
 */
public class CurrencyWrapper implements INLPText {

    // currency instance
    private final Currency currency;
    private final String displayText;

    /**
     * Constructor
     *
     * @param cur currency
     */
    public CurrencyWrapper(Currency cur) {
        currency = cur;
        displayText = getCurrencyDisplayText(cur);
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public String nlpTextId(@Nullable Context context) {
        return getCode();
    }

    @Override
    public String nlpRawText(@Nullable Context context) {
        return getDisplayText();
    }

    /* ---------------------- STATIC ------------------------- */

    /**
     * Get currency display text
     *
     * @param cur currency
     * @return display text
     */
    public static String getCurrencyDisplayText(Currency cur) {
        Locale lc = Locale.getDefault();
        return String.format("%s (%s)", cur.getDisplayName(lc), cur.getSymbol(Locale.US));
    }

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Get currency instance
     *
     * @return currency instance
     */
    public Currency get() {
        return currency;
    }

    /**
     * Get unique code
     *
     * @return unique code
     */
    public String getCode() {
        return currency.getCurrencyCode();
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
