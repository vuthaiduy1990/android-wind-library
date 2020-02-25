package the.wind.library;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class WindColorTest {

    @Test
    public void genShadesAndTints() {
        String[] shades = new String[]{"#3BA193", "#348F82", "#2E7D72", "#276B62", "#215A52"};
        String[] tints = new String[]{"#54BBAC", "#67C2B5", "#7ACABF", "#8DD1C8", "#A0D9D1"};
        int idx = 0;
        for (CWindColor shade : CWindColor.SUCCESS.shades()) {
            Assert.assertEquals(shades[idx], shade.toHex());
            idx++;
        }
        idx = 0;
        for (CWindColor tint : CWindColor.SUCCESS.tints()) {
            Assert.assertEquals(tints[idx], tint.toHex());
            idx++;
        }
    }
}
