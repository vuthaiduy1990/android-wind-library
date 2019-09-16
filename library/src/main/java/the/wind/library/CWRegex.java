package the.wind.library;

/**
 * Be-careful when modifying the regex pattern
 */
public final class CWRegex {

    // Regex to detect spaces
    public static String REGEX_SPACES = "\\s+";
    public static String REGEX_SPACE_BREAKING = "(\\s+)|([^\\s]+)";

    // Regex to detect non-spaces breaking languages (Chinese/Japanese/Korean/Thai)
    // Thanks to https://en.wikipedia.org/wiki/List_of_Unicode_characters
    // Thanks to https://anycount.com/WordCountBlog/word-count-in-oriental-languages/
    public static String REGEX_CHINA_CHARS = "[\\u3000-\\u303F]|[\\u4E00-\\u9FFF]|[\\u3400–\\u4DBF]";
    public static String REGEX_JAV_CHARS = "[\\u3040-\\u309F]|[\\u30A0-\\u30FF]|" + REGEX_CHINA_CHARS;
    public static String REGEX_KOR_CHARS = "[\\uAC00-\\uD7A3]|" + REGEX_CHINA_CHARS;
    public static String REGEX_THAI_CHARS = "[\\u0E00-\\u0E7F]";

}
