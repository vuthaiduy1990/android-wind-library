package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import the.wind.library.utils.CWStreamUtils;
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
            Assert.assertEquals("color.the.wind", CWStringUtils.join(".", list));

            Set<String> set = new LinkedHashSet<>();
            set.add("color");
            set.add("the");
            set.add("wind");
            Assert.assertEquals("color.the.wind", CWStringUtils.join(".", set));
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
            String input = "一所懸命color 1508 the勉強wind \n きました-hey";
            String[] expected = new String[]{
                    "一", "所", "懸", "命", "color", " ", "1508", " ", "the", "勉", "強", "wind",
                    " \n ", "き", "ま", "し", "た", "-hey"};

            // specify the regex for detecting Japanese language
            List<String> actual = CWStringUtils.text2words(input);
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
            List<String> actual = CWStringUtils.text2words(input);
            Assert.assertEquals(expected.length, actual.size());
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual.get(i));
            }
        }

        // Testcase - Input include both Japanese, Thai and Latin languages
        {
            String input = "一所懸命color 1508 the勉強wind \nหนнаиболее распространённы";
            String[] expected = new String[]{
                    "一", "所", "懸", "命", "color", " ", "1508", " ", "the", "勉", "強", "wind",
                    " \n", "ห", "น", "наиболее", " ", "распространённы"};

            // specify the regex for detecting Japanese language
            List<String> actual = CWStringUtils.text2words(input);
            Assert.assertEquals(expected.length, actual.size());
            for (int i = 0; i < expected.length; i++) {
                Assert.assertEquals(expected[i], actual.get(i));
            }
        }
    }

    @Test
    public void random() {
        // Testcase: unique random string
        {
            int length = 128;
            String firstRandom = CWStringUtils.random(length);
            //System.out.println("unbounded random string: " + firstRandom);
            for (int i = 0; i < 1000; i++) {
                Assert.assertNotEquals(firstRandom, CWStringUtils.random(length));
            }
        }

        // Testcase: check length
        {
            int[] lengths = new int[]{
                    1,
                    128, // bytes
                    128 * 1024, // kbs
                    5 * 1024 * 1024 // mbs
            };
            for (int len : lengths) {
                String random = CWStringUtils.random(len);
                // System.out.println(random);
                Assert.assertTrue(CWStreamUtils.stringToBytes(random).length >= len);
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
    public void hasText() {
        Assert.assertFalse(CWStringUtils.hasText(null));
        Assert.assertFalse(CWStringUtils.hasText(""));
        Assert.assertFalse(CWStringUtils.hasText("   "));
        Assert.assertFalse(CWStringUtils.hasText("\n"));
        Assert.assertFalse(CWStringUtils.hasText("\t"));
        Assert.assertTrue(CWStringUtils.hasText("1235"));
        Assert.assertTrue(CWStringUtils.hasText("xyz"));
        Assert.assertTrue(CWStringUtils.hasText(" xyz "));

    }
}
