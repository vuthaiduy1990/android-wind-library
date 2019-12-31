package the.wind.library.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import the.wind.library.CWRegex;
import the.wind.library.CWUnicode;

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
    public static String join(String delimiter, Iterable<String> elements) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> it = elements.iterator();
        while (it.hasNext()) {
            builder.append(delimiter).append(it.next());
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
     * @param text a text
     * @return list of words
     */
    public static List<String> text2words(String text) {
        List<String> result = new LinkedList<>();
        String regex = CWStringUtils.join(
                "|",
                CWRegex.REGEX_JAV_CHARS,
                CWRegex.REGEX_THAI_CHARS,
                "(\\s+)",
                "([^\\s" + CWUnicode.JAV_CHARS + CWUnicode.THAI_CHARS + "]+)"
        );
        Matcher m = Pattern.compile(regex).matcher(text);
        while (m.find()) {
            result.add(m.group());
        }
        return result;
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

}
