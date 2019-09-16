package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import the.wind.library.utils.CWStringUtils;

public class StringUtilsTest {

    @Test
    public void join() {
        // Testcase: test with array of string
        {
            Assert.assertEquals("", CWStringUtils.join("."));
            Assert.assertEquals("color", CWStringUtils.join(".", "color"));
            Assert.assertEquals("color.wind", CWStringUtils.join(".", "color", "wind"));
            Assert.assertEquals(
                    "color.the.wind",
                    CWStringUtils.join(".", "color", "the", "wind"));
            Assert.assertEquals(
                    "color-the-wind",
                    CWStringUtils.join("-", "color", "the", "wind"));
        }

        // Testcase: test list/set
        {
            List<String> list = new LinkedList<>();
            list.add("color");
            list.add("the");
            list.add("wind");
            Assert.assertEquals("color.the.wind", CWStringUtils.join(".", list.iterator()));

            Set<String> set = new LinkedHashSet<>();
            set.add("color");
            set.add("the");
            set.add("wind");
            Assert.assertEquals("color.the.wind", CWStringUtils.join(".", set.iterator()));
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
        String input = "   Color \nthe   wind  ";
        String strip = CWStringUtils.strip(input);
        Assert.assertEquals("Color the wind", strip);
        Assert.assertNotEquals(input, strip); // do not modify  the input
    }

    @Test
    public void text2words() {
        // Testcase: complicated non-space breaking language string
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

        // Testcase: less complicated non-space breaking language string
        {
            String input = "Color \n the wind    ";
            String[] expected = new String[]{"Color", " \n ", "the", " ", "wind", "    "};
            List<String> actual = CWStringUtils.text2words(input);
            Assert.assertEquals(expected.length, actual.size());
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual.get(i));
            }
        }

        // Testcase - Input is Japanese
        {
            String input = "一所懸命 1508 勉強 \n きました-hey";
            String[] expected = new String[]{
                    "一", "所", "懸", "命", " ", "1508", " ", "勉", "強",
                    " \n ", "き", "ま", "し", "た", "-hey"};

            // specify the regex for detecting Japanese language
            List<String> actual = CWStringUtils.text2words(input, CWRegex.REGEX_JAV_CHARS);
            Assert.assertEquals(expected.length, actual.size());
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual.get(i));
            }

            // auto detect japanese language
            actual = CWStringUtils.text2words(input);
            Assert.assertEquals(expected.length, actual.size());
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual.get(i));
            }
        }

        // Testcase - Input is Thai
        {
            String input = "หน่วยเสียงวรรณยุกต์ 1508";
            String[] expected = new String[]{
                    "ห", "น", "่", "ว", "ย", "เ", "ส", "ี", "ย", "ง", "ว",
                    "ร", "ร", "ณ", "ย", "ุ", "ก", "ต", "์", " ", "1508"};

            // specify the regex for detecting Thai language
            List<String> actual = CWStringUtils.text2words(input, CWRegex.REGEX_THAI_CHARS);
            Assert.assertEquals(expected.length, actual.size());
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual.get(i));
            }

            // auto detect Thai language
            actual = CWStringUtils.text2words(input);
            Assert.assertEquals(expected.length, actual.size());
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual.get(i));
            }
        }

        // Testcase - can't auto detect language -> use default regex for space-breaking language
        {
            String input = "一所懸 color the wind";
            String[] expected = new String[]{"一", "所", "懸", " ", "color", " ", "the", " ", "wind"};

            // specify the regex for detecting Japanese language
            List<String> actual = CWStringUtils.text2words(input, CWRegex.REGEX_JAV_CHARS);
            Assert.assertEquals(expected.length, actual.size());
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual.get(i));
            }

            // can't not auto detect japanese language
            expected = new String[]{"一所懸", " ", "color", " ", "the", " ", "wind"};
            actual = CWStringUtils.text2words(input);
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
