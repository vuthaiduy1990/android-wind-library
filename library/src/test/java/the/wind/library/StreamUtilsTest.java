package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import the.wind.library.utils.CWStreamUtils;

public class StreamUtilsTest {

    @Test
    public void stringToBytes() {
        String text = "$^IP)O_}+BbhGI:^&*DTR";
        for (int i = 0; i < 100; i++) {
            byte[] encode = CWStreamUtils.stringToBytes(text);
            Assert.assertEquals(text, CWStreamUtils.bytesToString(encode));
        }
    }
}
