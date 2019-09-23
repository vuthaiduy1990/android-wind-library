package the.wind.library;

import java.util.Locale;

public final class CWLocale {

    // Vietnamese
    public static final Locale VIETNAMESE = new Locale("vi");
    public static final Locale VIETNAM = new Locale("vi", "VN");

    // Thai
    public static final Locale THAI = new Locale("th");
    public static final Locale THAILAND = new Locale("th", "TH");

    /**
     * Check if given locale is non-space breaking language or not
     *
     * @param loc locale
     * @return true if non-space breaking language
     */
    public static boolean isNonSpaceBreakingLanguage(Locale loc) {
        return loc.equals(Locale.JAPANESE)
                || loc.equals(Locale.JAPAN)
                || loc.equals(Locale.CHINESE)
                || loc.equals(Locale.SIMPLIFIED_CHINESE)
                || loc.equals(Locale.TRADITIONAL_CHINESE)
                || loc.equals(CWLocale.THAI)
                || loc.equals(CWLocale.THAILAND);
    }

}
