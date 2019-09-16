package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

    @Test
    public void testRegex_REGEX_SPACES() {
        // ---->>> REGEX_SPACES ---->>> String.split()
        {
            String input = "Hey, what the hell is that?. \n I   have   no   idea 6969.";
            String[] expected = new String[]{
                    "Hey,", "what", "the", "hell", "is", "that?.",
                    "I", "have", "no", "idea", "6969."};
            String[] actual = input.split(CWRegex.REGEX_SPACES);
            Assert.assertEquals(expected.length, actual.length);
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual[i]);
            }
        }
        // ---->>> REGEX_SPACES ---->>> String.replace()
        {
            String input = "Color the   wind";
            Assert.assertEquals("Color_the_wind", input.replaceAll(CWRegex.REGEX_SPACES, "_"));
        }
    }

    @Test
    public void testRegex_REGEX_SPACE_BREAKING() {
        // ---->>> REGEX_SPACE_BREAKING ---->>> Break string to word
        {
            String input = "1508 - color the wind" + // English
                    "\nTô màu gió" + /*Vietnamese*/
                    "\nраскрась ветер %*Y("; /*Russian*/
            Matcher m = Pattern.compile(CWRegex.REGEX_SPACE_BREAKING).matcher(input);
            List<String> words = new LinkedList<>();
            while (m.find()) {
                words.add(m.group());
            }
            String[] expected = new String[]{
                    "1508", " ", "-", " ", "color", " ", "the", " ", "wind",
                    "\n", "Tô", " ", "màu", " ", "gió",
                    "\n", "раскрась", " ", "ветер", " ", "%*Y("
            };

            Assert.assertEquals(21, words.size());
            Assert.assertEquals(expected.length, words.size());
            for (int i = 0; i < words.size(); i++) {
                Assert.assertEquals(expected[i], words.get(i));
            }
        }
    }

    @Test
    public void testRegex_REGEX_CHINA_CHARS() {
        String input = "目前世界有五分之一人口做為母語。";
        Matcher m = Pattern.compile(CWRegex.REGEX_JAV_CHARS).matcher(input);
        List<String> words = new LinkedList<>();
        while (m.find()) {
            words.add(m.group());
        }
        String[] expect = new String[]{
                "目", "前", "世", "界", "有", "五", "分", "之",
                "一", "人", "口", "做", "為", "母", "語", "。"};

        Assert.assertEquals(16, words.size());
        Assert.assertEquals(expect.length, words.size());
        for (int i = 0; i < words.size(); i++) {
            Assert.assertEquals(expect[i], words.get(i));
        }
    }

    @Test
    public void testRegex_REGEX_JAV_CHARS() {
        String input = "律令国家の成立と貴族政治の 569 展開 %&^ すむませんテスト。";
        Matcher m = Pattern.compile(CWRegex.REGEX_JAV_CHARS).matcher(input);
        List<String> words = new LinkedList<>();
        while (m.find()) {
            words.add(m.group());
        }
        String[] expected = new String[]{
                "律", "令", "国", "家", "の", "成", "立", "と", "貴", "族", "政", "治", "の",
                "展", "開", "す", "む", "ま", "せ", "ん", "テ", "ス", "ト", "。"};

        Assert.assertEquals(24, words.size());
        Assert.assertEquals(expected.length, words.size());
        for (int i = 0; i < words.size(); i++) {
            Assert.assertEquals(expected[i], words.get(i));
        }
    }

    @Test
    public void testRegex_REGEX_KOR_CHARS() {
        String input = "이 부분의 본문은 한국어의 호칭 문제입니다.";
        Matcher m = Pattern.compile(CWRegex.REGEX_KOR_CHARS).matcher(input);
        List<String> words = new LinkedList<>();
        while (m.find()) {
            words.add(m.group());
        }
        String[] expect = new String[]{
                "이", "부", "분", "의", "본", "문", "은", "한", "국", "어",
                "의", "호", "칭", "문", "제", "입", "니", "다"};

        Assert.assertEquals(18, words.size());
        Assert.assertEquals(expect.length, words.size());
        for (int i = 0; i < words.size(); i++) {
            Assert.assertEquals(expect[i], words.get(i));
        }
    }

    @Test
    public void testRegex_REGEX_THAI_CHARS() {
        String input = "หน่วยเสียงวรรณยุกต์";
        Matcher m = Pattern.compile(CWRegex.REGEX_THAI_CHARS).matcher(input);
        List<String> words = new LinkedList<>();
        while (m.find()) {
            words.add(m.group());
        }
        String[] expected = new String[]{
                "ห", "น", "่", "ว", "ย", "เ", "ส", "ี", "ย", "ง", "ว",
                "ร", "ร", "ณ", "ย", "ุ", "ก", "ต", "์"};
        Assert.assertEquals(19, words.size());
        Assert.assertEquals(expected.length, words.size());
        for (int i = 0; i < words.size(); i++) {
            Assert.assertEquals(expected[i], words.get(i));
        }
    }

}