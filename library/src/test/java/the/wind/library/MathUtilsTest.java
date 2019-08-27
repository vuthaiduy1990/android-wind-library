package the.wind.library;

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
        // rand number in range (0, 1)
        for (int i = 0; i < 10; i++) {
            int rand = CWMathUtils.random(0, 1);
            Assert.assertTrue(rand == 0 || rand == 1);
        }

        // rand number in range (5, 17)
        for (int i = 0; i < 100; i++) {
            int rand = CWMathUtils.random(5, 17);
            Assert.assertTrue(rand >= 5 || rand <= 17);
        }
    }

}
