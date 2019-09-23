package the.wind.library;

/**
 * Be-careful when modifying the regex pattern
 */
public final class CWRegex {

    // Regex to detect spaces
    public static String REGEX_SPACES = "\\s+";
    public static String REGEX_SPACE_BREAKING = "(\\s+)|([^\\s]+)";

    // Regex to detect special symbols such as [])"/|%$
    public static String REGEX_LATIN_SYMBOLS = "[" + CWUnicode.LATIN_SYMBOLS + "]";

    // Regex to detect non-spaces breaking languages (Chinese/Japanese/Korean/Thai)
    // Thanks to https://en.wikipedia.org/wiki/List_of_Unicode_characters
    // Thanks to https://anycount.com/WordCountBlog/word-count-in-oriental-languages/
    public static String REGEX_CHINA_CHARS = "[" + CWUnicode.CHINA_CHARS + "]";
    public static String REGEX_JAV_CHARS = "[" + CWUnicode.JAV_CHARS + "]";
    public static String REGEX_KOR_CHARS = "[" + CWUnicode.KOR_CHARS + "]";
    public static String REGEX_THAI_CHARS = "[" + CWUnicode.THAI_CHARS + "]";

}
