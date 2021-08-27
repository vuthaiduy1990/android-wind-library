package the.wind.library.model;

import android.content.Context;

import java.util.Currency;
import java.util.Locale;

import androidx.annotation.Nullable;
import the.wind.library.nlp.INLPText;

public class CurrencyWrapper implements INLPText {

    // currency instance
    private final Currency currency;

    /**
     * Constructor
     *
     * @param cur currency
     */
    public CurrencyWrapper(Currency cur) {
        currency = cur;
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public String nlpTextId(@Nullable Context context) {
        return currency.getCurrencyCode();
    }

    @Override
    public String nlpRawText(@Nullable Context context) {
        return getCurrencyDisplayText(currency);
    }

    /* ---------------------- STATIC ------------------------- */

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
     * Get currency display text
     *
     * @param cur currency
     * @return display text
     */
    public static String getCurrencyDisplayText(Currency cur) {
        Locale lc = Locale.getDefault();
        return String.format("%s (%s)", cur.getDisplayName(lc), cur.getSymbol(lc));
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */
}
