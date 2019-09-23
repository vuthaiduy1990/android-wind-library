package the.wind.library;

// https://en.wikipedia.org/wiki/List_of_Unicode_characters
public final class CWUnicode {

    // Symbols & Punctuations
    public static String LATIN_SYMBOLS = "\\u0021-\\u002F\\u003A-\\u0040\\u005B-\\u0060\\u007B-\\u007E\\u00A0-\\u00BF";

    // Languages
    public static String CHINA_CHARS = "\\u3000-\\u303F\\u4E00-\\u9FFF\\u3400–\\u4DBF";
    public static String JAV_CHARS = "\\u3040-\\u309F\\u30A0-\\u30FF" + CHINA_CHARS;
    public static String KOR_CHARS = "\\uAC00-\\uD7A3" + CHINA_CHARS;
    public static String THAI_CHARS = "\\u0E00-\\u0E7F";
}
