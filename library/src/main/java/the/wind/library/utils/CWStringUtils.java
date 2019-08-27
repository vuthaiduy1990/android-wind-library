package the.wind.library.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CWStringUtils {

    // ================================================================
    // Common Regex - Be-careful when modifying the regex pattern
    // ================================================================
    // Regex to detect spaces
    public static String REGEX_SPACES = "\\s+";
    // Regex to detect japanese words
    // Thanks https://gist.github.com/ryanmcgrath/982242
    // Thanks https://gist.github.com/oanhnn/9043867
    public static String REGEX_JAV_CHAR = "[\\u3000-\\u303F]|[\\u3040-\\u309F]|[\\u30A0-\\u30FF]|[\\uFF00-\\uFFEF]|[\\u4E00-\\u9FAF]|[\\u2605-\\u2606]|[\\u2190-\\u2195]|\\u203B";
    // ================================================================

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
     * To convert text to words without including spaces, use String.split(REGEX_SPACES)
     *
     * @param text   a text
     * @param locale locale, ex, {{{@link Locale#JAPANESE}}}
     * @return list of words
     */
    public static List<String> text2words(String text, Locale locale) {
        List<String> result = new LinkedList<>();
        String regex = "(\\s+)|([^\\s]+)";
        if (locale.equals(Locale.JAPAN) || locale.equals(Locale.JAPANESE)) {
            regex = REGEX_JAV_CHAR + "|" + regex;
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
     * To convert text to words without including spaces, use String.split(REGEX_SPACES)
     *
     * @param text a text
     * @return list of words
     */
    public static List<String> text2words(String text) {
        return text2words(text, Locale.US);
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
     *     searchMatching("Color the wind", "wind", true) -> true
     *     searchMatching("Color_the_wind", "wind_storm", true) -> false
     *
     *     // not full match - match any keys from the search string
     *     searchMatching("Color_the_wind", "wind_storm", false) -> true
     *
     *     // not match
     *     searchMatching("Color_the_wind", "nothing") -> false
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
        String regex;
        if (locale.equals(Locale.JAPANESE) || locale.equals(Locale.JAPAN)) {
            regex = "(\\d+)|" + REGEX_JAV_CHAR;
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
     * The input will be considered as matching if it matches any keys from the search string
     * <pre>
     *     searchPartialMatch("Color the wind", "wind") -> true
     *     searchPartialMatch("Color_the_wind", "wind_storm") -> true
     *     searchPartialMatch("Color_the_wind", "storm_wind") -> true
     * </pre>
     *
     * @param search search string
     * @param input  input string
     * @return true if matching
     */
    public static boolean searchPartialMatch(String search, String input) {
        return searchMatching(search, input, false, Locale.US);
    }

    /**
     * The input will be considered as matching if it matches all the keys from the search string
     * without caring about the order
     * <pre>
     *     searchFullMatch("Color the wind", "color the") -> true
     *     searchFullMatch("Color_the_wind", "the color") -> true
     *     searchFullMatch("Color_the_wind", "color storm") -> false
     * </pre>
     *
     * @param search search string
     * @param input  input string
     * @return true if matching
     */
    public static boolean searchFullMatch(String search, String input) {
        return searchMatching(search, input, true, Locale.US);
    }

    /**
     * Check if the input string matches with search string or not.
     * <pre>
     *     // full match
     *     searchMatching("Color the wind", ["wind"], true) -> true
     *     searchMatching("Color_the_wind", ["wind", "storm"], true) -> false
     *
     *     // not full match (match any keys from the search string)
     *     searchMatching("Color_the_wind", ["wind", "storm"], false) -> true
     *
     *     // not match
     *     searchMatching("Color_the_wind", ["nothing"]) -> false
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
     *     searchMatching("Color the wind", ["wind"], true) -> true
     *     searchMatching("Color_the_wind", ["wind", "storm"], true) -> false
     *
     *     // not full match (match any keys from the search string)
     *     searchMatching("Color_the_wind", ["wind", "storm"], false) -> true
     *
     *     // not match
     *     searchMatching("Color_the_wind", ["nothing"]) -> false
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

}
