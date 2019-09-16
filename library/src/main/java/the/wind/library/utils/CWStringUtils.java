package the.wind.library.utils;

import android.support.annotation.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import the.wind.library.CWLocale;
import the.wind.library.CWRegex;

public class CWStringUtils {

    /**
     * Join strings with given delimiter
     * From API level 26, you can use String.join() method
     * <pre>
     *     join(".", "color", "the", "wind") -> color.the.wind
     *     join("-", ["color", "the", "wind"]) -> color-the-wind
     * </pre>
     *
     * @param delimiter delimiter
     * @param elements  array of strings
     * @return joined string
     */
    public static String join(String delimiter, CharSequence... elements) {
        StringBuilder builder = new StringBuilder();
        for (CharSequence el : elements) {
            builder.append(delimiter).append(el);
        }
        String result = builder.toString();
        return result.length() > 0 ? result.substring(1) : result;
    }

    /**
     * Join strings with given delimiter
     * From API level 26, you can use String.join() method
     * <pre>
     *     List<String> list = new LinkedList();
     *     join(".", list.iterator()) -> Color.the.wind
     *     join("-", list.iterator()) -> Color-the-wind
     * </pre>
     *
     * @param delimiter delimiter
     * @param elements  list of strings
     * @return joined string
     */
    public static String join(String delimiter, Iterator<String> elements) {
        StringBuilder builder = new StringBuilder();
        while (elements.hasNext()) {
            builder.append(delimiter).append(elements.next());
        }
        String result = builder.toString();
        return result.length() > 0 ? result.substring(1) : result;
    }

    /**
     * Joint url path
     * <pre>
     *     joinUrlPaths("http//:localhost", "graphics.com")
     *     joinUrlPaths("http//:localhost/", "/graphics.com")
     * </pre>
     *
     * @param segments list of segments
     * @return a path
     */
    public static String joinUrlPaths(String... segments) {
        StringBuilder result = new StringBuilder();
        String regex = "(/+$)|(^/+)";

        for (String val : segments) {
            val = val.replaceAll(regex, "");
            result.append(val).append("/");
        }
        return result.toString();
    }

    /**
     * Get username from email
     * For example: admin@gmail.com -> admin
     *
     * @param email an email
     * @return username from email
     */
    public static String getUserNameFromEmail(String email) {
        int splitIdx = email.lastIndexOf('@');
        if (splitIdx > 0) {
            return email.substring(0, splitIdx);
        }
        return email;
    }

    /**
     * Get compact number for display.
     * For examples:
     * 1190000  -> 1.19M
     * 2000     -> 2K
     *
     * @param num    number
     * @param places number of digits after decimal point
     * @return compact number
     */
    public static String compactNumber(double num, int places) {
        String compact, postfix = "";
        if (num >= 1000000) {
            postfix = "M";
            compact = Double.toString(num / 1000000);
        } else if (num >= 1000) {
            postfix = "K";
            compact = Double.toString(num / 1000);
        } else {
            compact = Double.toString(num);
        }
        // https://www.javamex.com/tutorials/regular_expressions/search_replace.shtml
        String regex = String.format(Locale.getDefault(), "(\\d*\\.\\d{%d}).*", places);
        compact = compact.replaceFirst(regex, "$1"); // 1.1155 -> 1.11 (places = 2)
        compact = compact.replaceFirst("\\.0*\\z", ""); // 1.0 -> 1
        return compact + postfix;
    }

    /**
     * Get compact number for display.
     * For examples:
     * 1190000  -> 2M
     * 2000     -> 2K
     *
     * @param num    number
     * @param places number of digits after decimal point
     * @return compact number
     */
    public static String compactRoundNumber(double num, int places) {
        String compact, postfix = "";
        if (num >= 1000000) {
            postfix = "M";
            compact = Double.toString(CWMathUtils.round(num / 1000000, places));
        } else if (num >= 1000) {
            postfix = "K";
            compact = Double.toString(CWMathUtils.round(num / 1000, places));
        } else {
            compact = Double.toString(CWMathUtils.round(num, places));
        }
        compact = compact.replaceFirst("\\.0*\\z", ""); // 1.0 -> 1
        return compact + postfix;
    }

    /**
     * Remove white spaces at the begin and at the end of text.
     * Replace multiple whitespaces by single white space.
     * <pre>
     *     strip("   Color the   wind  ") -> "Color the wind"
     * </pre>
     *
     * @param text string value
     * @return stripped string
     */
    public static String strip(String text) {
        return text.trim().replaceAll("\\s{2,}", " ");
    }

    /**
     * Convert text to list of words including space or newline
     * <pre>
     *     text2words("\nColor \n the wind    ")
     *     -> ["\n", "Color", " \n ", "the", " ", "wind", "    "]
     * </pre>
     * Support both space and non-space breaking languages
     * https://anycount.com/WordCountBlog/word-count-in-oriental-languages/
     * <pre>
     *      + All space-breaking language systems (English/Vietnamese/French, etc.)
     *      + Non-space breaking language systems
     *          |- Chinese/Japanese
     *          |- Thai
     * </pre>
     *
     * @param text                  a text
     * @param nonSpaceBreakingRegex regex to detect no-space breaking language
     * @return list of words
     */
    public static List<String> text2words(String text, @Nullable String nonSpaceBreakingRegex) {
        List<String> result = new LinkedList<>();
        String regex = CWRegex.REGEX_SPACE_BREAKING;
        if (nonSpaceBreakingRegex != null) {
            regex = nonSpaceBreakingRegex + "|" + regex;
        }
        Matcher m = Pattern.compile(regex).matcher(text);
        while (m.find()) {
            result.add(m.group());
        }
        return result;
    }

    /**
     * Convert text to list of words including space or newline
     * <pre>
     *     text2words("\nColor \n the wind    ")
     *     -> ["\n", "Color", " \n ", "the", " ", "wind", "    "]
     * </pre>
     * Support both space and non-space breaking languages
     * https://anycount.com/WordCountBlog/word-count-in-oriental-languages/
     * <pre>
     *      + All space-breaking language systems (English/Vietnamese/French, etc.)
     *      + Non-space breaking language systems
     *          |- Chinese/Japanese
     *          |- Thai
     * </pre>
     *
     * @param text a text
     * @return list of words
     */
    public static List<String> text2words(String text) {
        String[] regexList = new String[2];
        Locale loc = Locale.getDefault();
        // give high priority to regex respective to the default locale
        if (loc.equals(CWLocale.THAI) || loc.equals(CWLocale.THAILAND)) {
            regexList[0] = CWRegex.REGEX_THAI_CHARS;
            regexList[1] = CWRegex.REGEX_JAV_CHARS;
        } else {
            regexList[0] = CWRegex.REGEX_JAV_CHARS;
            regexList[1] = CWRegex.REGEX_THAI_CHARS;
        }

        // Automatically detect if the input is non-space breaking language or not
        // Check with maximum of 10 character only to ensure the good performance
        // a text is considered as belonging to a language
        // if 50% characters of this text is belonged to a checked language
        String text10 = text.trim().replaceAll("\\s", "");
        text10 = text10.length() > 10 ? text10.substring(0, 10) : text10;
        for (String re : regexList) {
            int count = 0;
            Matcher m = Pattern.compile(re).matcher(text10);
            while (m.find()) {
                count++;
            }
            if ((float) count / text10.length() > 0.49f) /*30%*/ {
                return text2words(text, re);
            }
        }
        return text2words(text, null);
    }

    /**
     * Check if string is uppercase or not
     *
     * @param value string value
     * @return true if all characters of string are uppercase
     */
    public static boolean isUpperCase(String value) {
        return value.equals(value.toUpperCase());
    }

    /**
     * Check if string is lowercase or not
     *
     * @param value string value
     * @return true if all characters of string are lowercase
     */
    public static boolean isLowerCase(String value) {
        return value.equals(value.toLowerCase());
    }

    /**
     * Check if the input string matches with search string or not.
     * <pre>
     *     // full match - match all the keys without considering the order
     *     searchMatching("wind", "Color the wind", true) -> true
     *     searchMatching("wind_storm", "Color the wind", true) -> false
     *
     *     // not full match - match any keys from the search string
     *     searchMatching("wind_storm", "Color_the_wind", false) -> true
     *
     *     // not match
     *     searchMatching("nothing", "Color_the_wind") -> false
     * </pre>
     *
     * @param search    search string
     * @param input     input string
     * @param fullMatch true -> the input has to match all the search keys
     * @param locale    locale, ex, {{{@link Locale#JAPANESE}}}
     * @return true if matching
     */
    public static boolean searchMatching(String search, String input, boolean fullMatch, Locale locale) {
        // Normalize the input and search key
        String _input = strip(input.toLowerCase());
        String _search = strip(search.toLowerCase());

        // search key is empty
        if (_search.isEmpty()) return true;

        // Use String.contain() to check the matching
        // searchMatching("Color the wind", "wind") -> true
        if (_input.contains(_search)) return true;

        // use regex to check matching
        // searchMatching("Color_the_wind", "from wind") -> true
        String regex = "(\\d+)|";
        if (locale.equals(Locale.JAPANESE) || locale.equals(Locale.JAPAN)) {
            regex += CWRegex.REGEX_JAV_CHARS;
        } else {
            // Default is Latin language
            regex = "(\\d+)|([a-zA-Z]+)";
        }
        Matcher m = Pattern.compile(regex).matcher(_search);
        boolean match = false;
        while (m.find()) {
            match = _input.contains(m.group());
            if (fullMatch && !match) return false;
            if (!fullMatch && match) return true;
        }

        return match;
    }

    /**
     * Check if the input string matches with search string or not.
     * <pre>
     *     // full match
     *     searchMatching(["wind"], "Color the wind", true) -> true
     *     searchMatching(["wind", "storm"], "Color the wind", true) -> false
     *
     *     // not full match (match any keys from the search string)
     *     searchMatching(["wind", "storm"], "Color the wind", false) -> true
     *
     *     // not match
     *     searchMatching(["nothing"], "Color the wind", ) -> false
     * </pre>
     *
     * @param searchKeys list of search keys
     * @param input      input string
     * @param fullMatch  true -> the input has to match all the search keys
     * @param locale     locale, ex, {{{@link Locale#JAPANESE}}}
     * @return true if matching
     */
    public static boolean searchMatching(String[] searchKeys, String input, boolean fullMatch, Locale locale) {
        StringBuilder builder = new StringBuilder();
        for (String key : searchKeys) {
            builder.append(key).append(" ");
        }
        return searchMatching(builder.toString(), input, fullMatch, locale);
    }

    /**
     * Check if the input string matches with search string or not.
     * <pre>
     *     // full match
     *     searchMatching(["wind"], "Color the wind", true) -> true
     *     searchMatching(["wind", "storm"], "Color the wind", true) -> false
     *
     *     // not full match (match any keys from the search string)
     *     searchMatching(["wind", "storm"], "Color the wind", false) -> true
     *
     *     // not match
     *     searchMatching(["nothing"], "Color the wind", ) -> false
     * </pre>
     *
     * @param searchKeys list of search keys
     * @param input      input string
     * @param fullMatch  true -> the input has to match all the search keys
     * @param locale     locale, ex, {{{@link Locale#JAPANESE}}}
     * @return true if matching
     */
    public static boolean searchMatching(List<String> searchKeys, String input, boolean fullMatch, Locale locale) {
        StringBuilder builder = new StringBuilder();
        for (String key : searchKeys) {
            builder.append(key).append(" ");
        }
        return searchMatching(builder.toString(), input, fullMatch, locale);
    }

    /**
     * The input will be considered as matching if it matches any keys from the search string
     * <pre>
     *     searchPartialMatch("wind", "Color the wind", ) -> true
     *     searchPartialMatch("wind_storm", "Color the wind") -> true
     *     searchPartialMatch("storm_wind", "Color the wind") -> true
     * </pre>
     *
     * @param search search string
     * @param input  input string
     * @return true if matching
     */
    public static boolean searchPartialMatch(String search, String input) {
        return searchMatching(search, input, false, Locale.getDefault());
    }

    /**
     * The input will be considered as matching if it matches all the keys from the search string
     * without caring about the order
     * <pre>
     *     searchFullMatch("color the", "Color the wind") -> true
     *     searchFullMatch("the color", "Color the wind") -> true
     *     searchFullMatch("color storm", "Color the wind") -> false
     * </pre>
     *
     * @param search search string
     * @param input  input string
     * @return true if matching
     */
    public static boolean searchFullMatch(String search, String input) {
        return searchMatching(search, input, true, Locale.getDefault());
    }

}
