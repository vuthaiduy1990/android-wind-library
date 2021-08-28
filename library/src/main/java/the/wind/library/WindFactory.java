package the.wind.library;

import java.util.Collections;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import the.wind.library.model.CurrencyWrapper;
import the.wind.library.model.LocaleWrapper;

/**
 * The factory where you can get some default products
 */
public final class WindFactory {

    // singleton instance
    private static final WindFactory instance = new WindFactory();

    // list of currencies
    private List<CurrencyWrapper> currencies;

    // list of locales
    private List<LocaleWrapper> locales;

    /**
     * Private constructor
     */
    private WindFactory() {
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return singleton instance of factory
     */
    public static WindFactory instance() {
        return instance;
    }

    /**
     * Get available currencies
     *
     * @return list of wrapper currencies
     */
    public List<CurrencyWrapper> getAvailableCurrencies() {
        if (currencies == null || currencies.isEmpty()) {
            currencies = new LinkedList<>();
            for (Currency cur : Currency.getAvailableCurrencies()) {
                currencies.add(new CurrencyWrapper(cur));
            }
            currencies.sort((cur1, cur2) -> cur1.getDisplayText().compareTo(cur2.getDisplayText()));
        }
        return Collections.unmodifiableList(currencies);
    }

    /**
     * Get available locales
     *
     * @return list of wrapper locales
     */
    public List<LocaleWrapper> getAvailableLocales() {
        if (locales == null || locales.isEmpty()) {
            locales = new LinkedList<>();
            for (Locale lc : Locale.getAvailableLocales()) {
                if (!lc.getCountry().isEmpty() && !lc.getLanguage().isEmpty()) {
                    locales.add(new LocaleWrapper(lc));
                }
            }
            locales.sort((lc1, lc2) -> lc1.getDisplayText().compareTo(lc2.getDisplayText()));
        }
        return Collections.unmodifiableList(locales);
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Reset data
     */
    public void reset() {
        currencies = null;
        locales = null;
    }

    /* ---------------------- INNER CLASS -------------------- */


}
