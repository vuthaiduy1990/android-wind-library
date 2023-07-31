package the.wind.library;

import android.icu.util.Calendar;
import android.icu.util.TimeZone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import the.wind.library.model.CurrencyWrapper;
import the.wind.library.model.LocaleWrapper;
import the.wind.library.model.TimezoneWrapper;

/**
 * The factory where you can get some default products
 */
public final class WindFactory {

    // singleton instance
    private static final WindFactory instance = new WindFactory();

    // list of years
    private List<Integer> years;

    // list of month
    private List<Integer> months;

    // list of currencies
    private List<CurrencyWrapper> currencies;

    // list of locales
    private List<LocaleWrapper> locales;

    // list of timezone
    private List<TimezoneWrapper> timezones;

    private final List<String> topCurrencyList = Arrays.asList("TRY", "THB", "VND", "RUB", "JPY", "KRW", "GBP", "EUR", "USD");

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
     * Get available years
     *
     * @return years
     */
    public List<Integer> getAvailableYears() {
        if (years == null || years.isEmpty()) {
            years = new ArrayList<>();
            for (int i = 1900; i <= 2222; i++) {
                years.add(i);
            }
        }
        return years;
    }

    /**
     * Get available months
     *
     * @return months
     */
    public List<Integer> getAvailableMonths() {
        if (months == null || months.isEmpty()) {
            months = new ArrayList<>();
            months.add(Calendar.JANUARY);
            months.add(Calendar.FEBRUARY);
            months.add(Calendar.MARCH);
            months.add(Calendar.APRIL);
            months.add(Calendar.MAY);
            months.add(Calendar.JUNE);
            months.add(Calendar.JULY);
            months.add(Calendar.AUGUST);
            months.add(Calendar.SEPTEMBER);
            months.add(Calendar.OCTOBER);
            months.add(Calendar.NOVEMBER);
            months.add(Calendar.DECEMBER);
        }
        return months;
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
            currencies.sort(Comparator
                                    .comparing(CurrencyWrapper::getCode, (x1, x2) -> Integer.compare(-topCurrencyList.indexOf(x1), -topCurrencyList.indexOf(x2)))
                                    .thenComparing(CurrencyWrapper::getDisplayText));
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

    /**
     * Get available timezones
     *
     * @return list of wrapper timezones
     */
    public List<TimezoneWrapper> getAvailableTimezones() {
        if (timezones == null || timezones.isEmpty()) {
            timezones = new LinkedList<>();
            Set<String> duplicate = new HashSet<>();
            for (String id : TimeZone.getAvailableIDs()) {
                String lowerId = id.toLowerCase();
                if (lowerId.startsWith("etc/") || lowerId.startsWith("systemv/")) {
                    continue;
                }
                TimeZone tz = TimeZone.getTimeZone(id);
                TimezoneWrapper wrapper = new TimezoneWrapper(tz);
                String displayId = String.format("%s,%s", wrapper.getName(), wrapper.getLocation());
                if (duplicate.contains(displayId)) {
                    continue;
                } else {
                    duplicate.add(displayId);
                }
                timezones.add(wrapper);
            }
            timezones.sort((tz1, tz2) -> Integer.compare(tz1.get().getRawOffset(), tz2.get().getRawOffset()));
        }
        return Collections.unmodifiableList(timezones);
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Reset data
     */
    public void reset() {
        currencies = null;
        locales = null;
        timezones = null;
    }

    /* ---------------------- INNER CLASS -------------------- */


}
