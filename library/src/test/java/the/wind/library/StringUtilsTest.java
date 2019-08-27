package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import the.wind.library.utils.CWStringUtils;

public class StringUtilsTest {

    @Test
    public void testRegex() {
        // ---->>> REGEX_SPACES ---->>> String.split()
        {
            String input = "Hey, what the hell is that?. \n I   have   no   idea.";
            String[] expected = new String[]{"Hey,", "what", "the", "hell", "is", "that?.", "I", "have", "no", "idea."};
            String[] actual = input.split(CWStringUtils.REGEX_SPACES);
            Assert.assertEquals(expected.length, actual.length);
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual[i]);
            }
        }
        // ---->>> REGEX_SPACES ---->>> String.replace()
        {
            String input = "Color the   wind";
            Assert.assertEquals("Color_the_wind", input.replaceAll(CWStringUtils.REGEX_SPACES, "_"));
        }
        // ---->>> JAV_CHAR_REGEX
        {
            String input = "律令国家の成立と貴族政治の展開すむませんテスト。";
            Matcher m = Pattern.compile(CWStringUtils.REGEX_JAV_CHAR).matcher(input);
            List<String> words = new LinkedList<>();
            while (m.find()) {
                words.add(m.group());
            }
            Assert.assertEquals(24, words.size());
            String[] expect = new String[]{"律", "令", "国", "家", "の", "成", "立", "と", "貴", "族", "政", "治", "の", "展", "開", "す", "む", "ま", "せ", "ん", "テ", "ス", "ト", "。"};
            for (int i = 0; i < words.size(); i++) {
                Assert.assertEquals(expect[i], words.get(i));
            }
        }
    }

    @Test
    public void joinUrlPaths() {
        String[] paths = new String[]{"http://localhost:3000/"};
        Assert.assertEquals("http://localhost:3000/", CWStringUtils.joinUrlPaths(paths));

        paths = new String[]{"/http://localhost:3000"};
        Assert.assertEquals("http://localhost:3000/", CWStringUtils.joinUrlPaths(paths));

        paths = new String[]{"http://localhost:3000", "users/id"};
        Assert.assertEquals("http://localhost:3000/users/id/", CWStringUtils.joinUrlPaths(paths));

        paths = new String[]{"http://localhost:3000/", "users/id"};
        Assert.assertEquals("http://localhost:3000/users/id/", CWStringUtils.joinUrlPaths(paths));

        paths = new String[]{"http://localhost:3000", "/users/id"};
        Assert.assertEquals("http://localhost:3000/users/id/", CWStringUtils.joinUrlPaths(paths));

        paths = new String[]{"http://localhost:3000/", "/users/id/"};
        Assert.assertEquals("http://localhost:3000/users/id/", CWStringUtils.joinUrlPaths(paths));

        paths = new String[]{"///http://localhost:3000///", "///users/id///"};
        Assert.assertEquals("http://localhost:3000/users/id/", CWStringUtils.joinUrlPaths(paths));
    }

    @Test
    public void getUserNameFromEmail() {
        Assert.assertEquals("test", CWStringUtils.getUserNameFromEmail("test@gmail.com"));
        Assert.assertEquals("test.lab", CWStringUtils.getUserNameFromEmail("test.lab@gmail.com"));
        Assert.assertEquals("@test@lab", CWStringUtils.getUserNameFromEmail("@test@lab@gmail.com"));
        Assert.assertEquals("test", CWStringUtils.getUserNameFromEmail("test"));
    }

    @Test
    public void compactNumber() {
        Assert.assertEquals("0", CWStringUtils.compactNumber(0, 1));
        Assert.assertEquals("1", CWStringUtils.compactNumber(1, 1));
        Assert.assertEquals("101", CWStringUtils.compactNumber(101, 1));

        Assert.assertEquals("101", CWStringUtils.compactNumber(101.00, 0));
        Assert.assertEquals("101", CWStringUtils.compactNumber(101.25, 0));
        Assert.assertEquals("101.2", CWStringUtils.compactNumber(101.25, 1));
        Assert.assertEquals("101.25", CWStringUtils.compactNumber(101.25, 2));
        Assert.assertEquals("101.25", CWStringUtils.compactNumber(101.25, 3));

        Assert.assertEquals("1K", CWStringUtils.compactNumber(1000, 1));
        Assert.assertEquals("1K", CWStringUtils.compactNumber(1550, 0));
        Assert.assertEquals("1.5K", CWStringUtils.compactNumber(1550, 1));
        Assert.assertEquals("1.55K", CWStringUtils.compactNumber(1550, 2));
        Assert.assertEquals("1.55K", CWStringUtils.compactNumber(1550, 3));

        Assert.assertEquals("1M", CWStringUtils.compactNumber(1000000, 1));
        Assert.assertEquals("1M", CWStringUtils.compactNumber(1550000, 0));
        Assert.assertEquals("1.5M", CWStringUtils.compactNumber(1550000, 1));
        Assert.assertEquals("1.55M", CWStringUtils.compactNumber(1550000, 2));
        Assert.assertEquals("1.55M", CWStringUtils.compactNumber(1550000, 3));
    }

    @Test
    public void compactRoundNumber() {
        Assert.assertEquals("101", CWStringUtils.compactRoundNumber(101.00, 0));
        Assert.assertEquals("101", CWStringUtils.compactRoundNumber(101.25, 0));
        Assert.assertEquals("101.3", CWStringUtils.compactRoundNumber(101.25, 1));
        Assert.assertEquals("101.25", CWStringUtils.compactRoundNumber(101.25, 2));
        Assert.assertEquals("101.25", CWStringUtils.compactRoundNumber(101.25, 3));

        Assert.assertEquals("1K", CWStringUtils.compactRoundNumber(1000, 1));
        Assert.assertEquals("2K", CWStringUtils.compactRoundNumber(1550, 0));
        Assert.assertEquals("1.6K", CWStringUtils.compactRoundNumber(1550, 1)); // <<<---
        Assert.assertEquals("1.55K", CWStringUtils.compactRoundNumber(1550, 2));
        Assert.assertEquals("1.55K", CWStringUtils.compactRoundNumber(1550, 3));

        Assert.assertEquals("1M", CWStringUtils.compactRoundNumber(1000000, 1));
        Assert.assertEquals("2M", CWStringUtils.compactRoundNumber(1550000, 0));
        Assert.assertEquals("1.6M", CWStringUtils.compactRoundNumber(1550000, 1)); // <<<---
        Assert.assertEquals("1.55M", CWStringUtils.compactRoundNumber(1550000, 2));
        Assert.assertEquals("1.55M", CWStringUtils.compactRoundNumber(1550000, 3));
    }

    @Test
    public void strip() {
        Assert.assertEquals("Color the wind", CWStringUtils.strip("   Color \nthe   wind  "));
    }

    @Test
    public void text2words() {
        // Testcase 1:
        {
            String input = "  Hey, what the hell is that?. \n I   have   no  idea.";
            String[] expected = new String[]{
                    "  ", "Hey,", " ", "what", " ", "the", " ", "hell", " ", "is", " ", "that?.",
                    " \n ",
                    "I", "   ", "have", "   ", "no", "  ", "idea."};
            List<String> actual = CWStringUtils.text2words(input);
            Assert.assertEquals(expected.length, actual.size());
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual.get(i));
            }
        }

        // Testcase 2:
        {
            String input = "Color \n the wind    ";
            String[] expected = new String[]{"Color", " \n ", "the", " ", "wind", "    "};
            List<String> actual = CWStringUtils.text2words(input);
            Assert.assertEquals(expected.length, actual.size());
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual.get(i));
            }
        }

        // Testcase 2 - use Japanese
        {
            String input = "一所懸命 勉強 \n きました-hey";
            String[] expected = new String[]{"一", "所", "懸", "命", " ", "勉", "強", " \n ", "き", "ま", "し", "た", "-hey"};
            List<String> actual = CWStringUtils.text2words(input, Locale.JAPANESE);
            Assert.assertEquals(expected.length, actual.size());
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual.get(i));
            }
            actual = CWStringUtils.text2words(input, Locale.JAPAN);
            Assert.assertEquals(expected.length, actual.size());
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual.get(i));
            }
        }
    }

    @Test
    public void isUpperCase() {
        Assert.assertTrue(CWStringUtils.isUpperCase("COLOR THE WIND"));
        Assert.assertFalse(CWStringUtils.isUpperCase("Color The Wind"));
        Assert.assertFalse(CWStringUtils.isUpperCase("color the wind"));
    }

    @Test
    public void isLowerCase() {
        Assert.assertFalse(CWStringUtils.isLowerCase("COLOR THE WIND"));
        Assert.assertFalse(CWStringUtils.isLowerCase("Color The Wind"));
        Assert.assertTrue(CWStringUtils.isLowerCase("color the wind"));
    }

    @Test
    public void searchMatching() {
        // Testcase 1: Match with any keys from the search string ---->>> partial match
        {
            String input = "Color the wind";
            Assert.assertTrue(CWStringUtils.searchPartialMatch("Color the wind", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("Color    the  \n wind   ", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("COLOR THE WIND", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("color", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("the", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("wind", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("color storm", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("storm color", input));

            input = "Color-the_wind";
            Assert.assertTrue(CWStringUtils.searchPartialMatch("Color the wind", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("Color    the  \n wind   ", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("COLOR THE WIND", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("color", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("the", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("wind", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("wind storm", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("storm wind", input));

            input = "ColorTheWind";
            Assert.assertTrue(CWStringUtils.searchPartialMatch("Color the wind", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("Color the wind", "colorthewind"));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("Color    the  \n wind   ", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("COLOR THE WIND", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("color", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("the", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("wind", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("wind storm", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("storm wind", input));

            Assert.assertTrue(CWStringUtils.searchPartialMatch("Color the wind", "~!@#$%^&*()_+=}{wind~!@#$%^&*()_+=}{"));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("Color the wind", "^&*$%-$_000Wind_"));
        }

        // Testcase 2:  Match with all the keys from the search string ---->>> full match
        {
            String input = "Color the wind";
            Assert.assertTrue(CWStringUtils.searchFullMatch("color the", input));
            Assert.assertTrue(CWStringUtils.searchFullMatch("the color", input));
            Assert.assertFalse(CWStringUtils.searchFullMatch("color storm", input));
            Assert.assertFalse(CWStringUtils.searchFullMatch("storm color", input));
        }

        // Testcase 3:  Neither partial nor full match
        {
            Assert.assertFalse(CWStringUtils.searchPartialMatch("Color the wind", "nothing"));
        }

        // Testcase 4:  Test with array and list
        {
            String input = "Color the wind";
            Assert.assertTrue(CWStringUtils.searchMatching(new String[]{"the", "color"}, input, true, Locale.US));
            Assert.assertFalse(CWStringUtils.searchMatching(new String[]{"color", "storm"}, input, true, Locale.US));
            Assert.assertTrue(CWStringUtils.searchMatching(new String[]{"%7897color", "storm"}, input, false, Locale.US));
            Assert.assertFalse(CWStringUtils.searchMatching(new String[]{"nothing"}, input, false, Locale.US));

            String[] keys = new String[]{"the", "color"};
            List<String> searchList = Arrays.asList(keys);
            Assert.assertTrue(CWStringUtils.searchMatching(searchList, input, true, Locale.US));
        }

        // Testcase 5:  Test with Vietnamese language
        {
            String input = "Tô màu cho gió";
            Assert.assertTrue(CWStringUtils.searchPartialMatch("gió", input));
            Assert.assertTrue(CWStringUtils.searchPartialMatch("màu gió", input));
            Assert.assertTrue(CWStringUtils.searchFullMatch("màu gió", input));
            Assert.assertTrue(CWStringUtils.searchFullMatch("gió màu", input));
            Assert.assertFalse(CWStringUtils.searchFullMatch("cơn gió", input));
        }

        // Testcase 6:  Test with Japanese language
        {
            String input = "風を彩る。";
            Assert.assertTrue(CWStringUtils.searchMatching("彩", input, false, Locale.JAPAN));
            Assert.assertTrue(CWStringUtils.searchMatching("波風", input, false, Locale.JAPAN));
            Assert.assertTrue(CWStringUtils.searchMatching("風波", input, false, Locale.JAPAN));
            Assert.assertTrue(CWStringUtils.searchMatching("風彩", input, true, Locale.JAPANESE));
            Assert.assertTrue(CWStringUtils.searchMatching("彩風", input, true, Locale.JAPANESE));
        }
    }

}
