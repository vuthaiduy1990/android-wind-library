package the.wind.library;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;

import the.wind.library.utils.CWMathUtils;

public class MathUtilsTest {

    @Test
    public void round() {
        // round double number (negative and positive)
        double doubleValue = 1.14;
        Assert.assertEquals(1, CWMathUtils.round(doubleValue, 0), 0);
        Assert.assertEquals(1.1, CWMathUtils.round(doubleValue, 1), 0);
        Assert.assertEquals(1.14, CWMathUtils.round(doubleValue, 2), 0);
        Assert.assertEquals(1.14, CWMathUtils.round(doubleValue, 3), 0);
        doubleValue = -1.14;
        Assert.assertEquals(-1, CWMathUtils.round(doubleValue, 0), 0);
        Assert.assertEquals(-1.1, CWMathUtils.round(doubleValue, 1), 0);
        Assert.assertEquals(-1.14, CWMathUtils.round(doubleValue, 2), 0);
        Assert.assertEquals(-1.14, CWMathUtils.round(doubleValue, 3), 0);
        doubleValue = 1.15;
        Assert.assertEquals(1, CWMathUtils.round(doubleValue, 0), 0);
        Assert.assertEquals(1.2, CWMathUtils.round(doubleValue, 1), 0);
        Assert.assertEquals(1.15, CWMathUtils.round(doubleValue, 2), 0);
        Assert.assertEquals(1.15, CWMathUtils.round(doubleValue, 3), 0);
        doubleValue = -1.15;
        Assert.assertEquals(-1, CWMathUtils.round(doubleValue, 0), 0);
        Assert.assertEquals(-1.1, CWMathUtils.round(doubleValue, 1), 0);
        Assert.assertEquals(-1.15, CWMathUtils.round(doubleValue, 2), 0);
        Assert.assertEquals(-1.15, CWMathUtils.round(doubleValue, 3), 0);
        doubleValue = 1.16;
        Assert.assertEquals(1, CWMathUtils.round(doubleValue, 0), 0);
        Assert.assertEquals(1.2, CWMathUtils.round(doubleValue, 1), 0);
        Assert.assertEquals(1.16, CWMathUtils.round(doubleValue, 2), 0);
        Assert.assertEquals(1.16, CWMathUtils.round(doubleValue, 3), 0);
        doubleValue = -1.16;
        Assert.assertEquals(-1, CWMathUtils.round(doubleValue, 0), 0);
        Assert.assertEquals(-1.2, CWMathUtils.round(doubleValue, 1), 0);
        Assert.assertEquals(-1.16, CWMathUtils.round(doubleValue, 2), 0);
        Assert.assertEquals(-1.16, CWMathUtils.round(doubleValue, 3), 0);

        // round float number (negative and positive)
        float floatValue = 1.14f;
        Assert.assertEquals(1f, CWMathUtils.round(floatValue, 0), 0);
        Assert.assertEquals(1.1f, CWMathUtils.round(floatValue, 1), 0);
        Assert.assertEquals(1.14f, CWMathUtils.round(floatValue, 2), 0);
        Assert.assertEquals(1.14f, CWMathUtils.round(floatValue, 3), 0);
        floatValue = -1.14f;
        Assert.assertEquals(-1f, CWMathUtils.round(floatValue, 0), 0);
        Assert.assertEquals(-1.1f, CWMathUtils.round(floatValue, 1), 0);
        Assert.assertEquals(-1.14f, CWMathUtils.round(floatValue, 2), 0);
        Assert.assertEquals(-1.14f, CWMathUtils.round(floatValue, 3), 0);
        floatValue = 1.15f;
        Assert.assertEquals(1f, CWMathUtils.round(floatValue, 0), 0);
        Assert.assertEquals(1.2f, CWMathUtils.round(floatValue, 1), 0);
        Assert.assertEquals(1.15f, CWMathUtils.round(floatValue, 2), 0);
        Assert.assertEquals(1.15f, CWMathUtils.round(floatValue, 3), 0);
        floatValue = -1.15f;
        Assert.assertEquals(-1f, CWMathUtils.round(floatValue, 0), 0);
        Assert.assertEquals(-1.1f, CWMathUtils.round(floatValue, 1), 0);
        Assert.assertEquals(-1.15f, CWMathUtils.round(floatValue, 2), 0);
        Assert.assertEquals(-1.15f, CWMathUtils.round(floatValue, 3), 0);
        floatValue = 1.16f;
        Assert.assertEquals(1f, CWMathUtils.round(floatValue, 0), 0);
        Assert.assertEquals(1.2f, CWMathUtils.round(floatValue, 1), 0);
        Assert.assertEquals(1.16f, CWMathUtils.round(floatValue, 2), 0);
        Assert.assertEquals(1.16f, CWMathUtils.round(floatValue, 3), 0);
        floatValue = -1.16f;
        Assert.assertEquals(-1f, CWMathUtils.round(floatValue, 0), 0);
        Assert.assertEquals(-1.2f, CWMathUtils.round(floatValue, 1), 0);
        Assert.assertEquals(-1.16f, CWMathUtils.round(floatValue, 2), 0);
        Assert.assertEquals(-1.16f, CWMathUtils.round(floatValue, 3), 0);
    }

    @Test
    public void truncate() {
        // truncate float number (negative and positive)
        float floatValue = 1.14f;
        Assert.assertEquals(1f, CWMathUtils.truncate(floatValue, 0), 0);
        Assert.assertEquals(1.1f, CWMathUtils.truncate(floatValue, 1), 0);
        Assert.assertEquals(1.14f, CWMathUtils.truncate(floatValue, 2), 0);
        Assert.assertEquals(1.14f, CWMathUtils.truncate(floatValue, 3), 0);
        floatValue = -1.14f;
        Assert.assertEquals(-1f, CWMathUtils.truncate(floatValue, 0), 0);
        Assert.assertEquals(-1.1f, CWMathUtils.truncate(floatValue, 1), 0);
        Assert.assertEquals(-1.14f, CWMathUtils.truncate(floatValue, 2), 0);
        Assert.assertEquals(-1.14f, CWMathUtils.truncate(floatValue, 3), 0);
        floatValue = 1.15f;
        Assert.assertEquals(1f, CWMathUtils.truncate(floatValue, 0), 0);
        Assert.assertEquals(1.1f, CWMathUtils.truncate(floatValue, 1), 0);
        Assert.assertEquals(1.15f, CWMathUtils.truncate(floatValue, 2), 0);
        Assert.assertEquals(1.15f, CWMathUtils.truncate(floatValue, 3), 0);
        floatValue = -1.15f;
        Assert.assertEquals(-1f, CWMathUtils.truncate(floatValue, 0), 0);
        Assert.assertEquals(-1.1f, CWMathUtils.truncate(floatValue, 1), 0);
        Assert.assertEquals(-1.15f, CWMathUtils.truncate(floatValue, 2), 0);
        Assert.assertEquals(-1.15f, CWMathUtils.truncate(floatValue, 3), 0);
        floatValue = 1.16f;
        Assert.assertEquals(1f, CWMathUtils.truncate(floatValue, 0), 0);
        Assert.assertEquals(1.1f, CWMathUtils.truncate(floatValue, 1), 0);
        Assert.assertEquals(1.16f, CWMathUtils.truncate(floatValue, 2), 0);
        Assert.assertEquals(1.16f, CWMathUtils.truncate(floatValue, 3), 0);
        floatValue = -1.16f;
        Assert.assertEquals(-1f, CWMathUtils.truncate(floatValue, 0), 0);
        Assert.assertEquals(-1.1f, CWMathUtils.truncate(floatValue, 1), 0);
        Assert.assertEquals(-1.16f, CWMathUtils.truncate(floatValue, 2), 0);
        Assert.assertEquals(-1.16f, CWMathUtils.truncate(floatValue, 3), 0);
    }

    @Test
    public void random() {
        // Testcase 1: randomize integer number
        {
            // in range (0, 1)
            for (int i = 0; i < 10; i++) {
                int rand = CWMathUtils.random(0, 1);
                Assert.assertTrue(rand == 0 || rand == 1);
            }

            // in positive range (5, 17)
            for (int i = 0; i < 1000; i++) {
                int rand = CWMathUtils.random(5, 17);
                Assert.assertTrue(rand >= 5 && rand <= 17);
            }

            // in negative range (-17, -5)
            for (int i = 0; i < 1000; i++) {
                int rand = CWMathUtils.random(-17, -5);
                Assert.assertTrue(rand >= -17 && rand <= -5);
            }

            // in negative to positive range
            boolean hasPositive = false;
            boolean hasNegative = false;
            for (int i = 0; i < 1000; i++) {
                int rand = CWMathUtils.random(-5, 5);
                if (rand >= 0) hasPositive = true;
                else hasNegative = true;
                Assert.assertTrue(rand >= -5 && rand <= 5);
            }
            Assert.assertTrue(hasPositive);
            Assert.assertTrue(hasNegative);
        }

        // Testcase 2: randomize float number
        {
            // in range (0, 1)
            for (int i = 0; i < 10; i++) {
                float rand = CWMathUtils.random(0f, 1f);
                Assert.assertTrue(rand >= 0 && rand <= 1);
            }

            // in positive range (5, 17)
            for (int i = 0; i < 1000; i++) {
                float rand = CWMathUtils.random(5.5f, 17f);
                Assert.assertTrue(rand >= 5.5f && rand <= 17f);
            }

            // in negative range (-17, -5)
            for (int i = 0; i < 1000; i++) {
                float rand = CWMathUtils.random(-17.1f, -5f);
                Assert.assertTrue(rand >= -17.1f && rand <= -5f);
            }

            // in negative to positive range
            boolean hasPositive = false;
            boolean hasNegative = false;
            for (int i = 0; i < 1000; i++) {
                float rand = CWMathUtils.random(-5.3f, 5f);
                if (rand >= 0) hasPositive = true;
                else hasNegative = true;
                Assert.assertTrue(rand >= -5.3f && rand <= 5f);
            }
            Assert.assertTrue(hasPositive);
            Assert.assertTrue(hasNegative);
        }

        // Testcase 2: randomize double number
        {
            // in range (0, 1)
            for (int i = 0; i < 10; i++) {
                double rand = CWMathUtils.random(0, 1.0);
                Assert.assertTrue(rand >= 0 && rand <= 1);
            }

            // in positive range (5, 17)
            for (int i = 0; i < 1000; i++) {
                double rand = CWMathUtils.random(5.5, 17);
                Assert.assertTrue(rand >= 5.5 && rand <= 17);
            }

            // in negative range (-17, -5)
            for (int i = 0; i < 1000; i++) {
                double rand = CWMathUtils.random(-17.1, -5);
                Assert.assertTrue(rand >= -17.1 && rand <= -5);
            }

            // in negative to positive range
            boolean hasPositive = false;
            boolean hasNegative = false;
            for (int i = 0; i < 1000; i++) {
                double rand = CWMathUtils.random(-5.3, 5);
                if (rand >= 0) hasPositive = true;
                else hasNegative = true;
                Assert.assertTrue(rand >= -5.3 && rand <= 5);
            }
            Assert.assertTrue(hasPositive);
            Assert.assertTrue(hasNegative);
        }
    }

    @Test
    public void shuffle() {
        // Testcase: shuffle with start = end (empty array)
        {
            int[] shuffle = CWMathUtils.shuffle(0, 0);
            Assert.assertEquals(0, shuffle.length);
        }

        // Testcase: generate a shuffled index array
        {
            int[] origin = new int[]{0, 1, 2, 3, 4};
            for (int i = 0; i < 1000; i++) {
                int[] shuffle = CWMathUtils.shuffle(0, 5);
                for (int val : shuffle) {
                    Assert.assertTrue(val >= 0 && val < 5);
                }
                Assert.assertThat(origin, IsNot.not(IsEqual.equalTo(shuffle)));
            }
        }

        // Testcase: generate a shuffled array with positive range
        {
            int[] origin = new int[]{3, 4, 5, 6, 7, 8, 9};
            for (int i = 0; i < 1000; i++) {
                int[] shuffle = CWMathUtils.shuffle(3, 10);
                for (int val : shuffle) {
                    Assert.assertTrue(val >= 3 && val < 10);
                }
                Assert.assertThat(origin, IsNot.not(IsEqual.equalTo(shuffle)));
            }
        }

        // Testcase: generate a shuffled array with negative range
        {
            int[] origin = new int[]{-5, -4, -3, -2, -1};
            for (int i = 0; i < 1000; i++) {
                int[] shuffle = CWMathUtils.shuffle(-5, 0);
                for (int val : shuffle) {
                    Assert.assertTrue(val >= -5 && val < 0);
                }
                Assert.assertThat(origin, IsNot.not(IsEqual.equalTo(shuffle)));
            }

            origin = new int[]{-10, -9, -8, -7, -6, -5, -4};
            for (int i = 0; i < 1000; i++) {
                int[] shuffle = CWMathUtils.shuffle(-10, -3);
                for (int val : shuffle) {
                    Assert.assertTrue(val >= -10 && val < -3);
                }
                Assert.assertThat(origin, IsNot.not(IsEqual.equalTo(shuffle)));
            }
        }

        // Testcase: generate a shuffled array from negative to positive
        {
            int[] origin = new int[]{-3, -2, -1, 0, 1, 2};
            for (int i = 0; i < 1000; i++) {
                int[] shuffle = CWMathUtils.shuffle(-3, 3);
                for (int val : shuffle) {
                    Assert.assertTrue(val >= -3 && val < 3);
                }
                Assert.assertThat(origin, IsNot.not(IsEqual.equalTo(shuffle)));
            }
        }

        // Testcase: throw an exception when parameter is invalid
        {
            // start > end
            try {
                CWMathUtils.shuffle(5, 0);
                Assert.fail();
            } catch (Exception ex) {
                Assert.assertEquals(IllegalArgumentException.class, ex.getClass());
            }
        }

        // Testcase: shuffle empty string array
        {
            String[] shuffle = CWMathUtils.shuffle(String.class, new String[]{});
            Assert.assertEquals(0, shuffle.length);
        }

        // Testcase: shuffle non-empty string array
        {
            String[] origin = new String[]{"The", "never", "ending", "story"};
            for (int i = 0; i < 1000; i++) {
                String[] shuffle = CWMathUtils.shuffle(String.class, origin);
                Assert.assertThat(origin, IsNot.not(IsEqual.equalTo(shuffle)));
            }
        }
    }

}
