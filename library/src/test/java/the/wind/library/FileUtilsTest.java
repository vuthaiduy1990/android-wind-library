package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import the.wind.library.utils.CWFileUtils;

public class FileUtilsTest {

    @Test
    public void join() {
        // Testcase : root is string
        {
            String root = "file:/color";

            File expected = new File("file://color/the");
            Assert.assertEquals(expected, CWFileUtils.join(root, "the"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "/the"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "the/"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "/the/"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "//the//"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "///the///"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "\\the\\"));

            expected = new File("file:/color/the/wind");
            Assert.assertEquals(expected, CWFileUtils.join(root, "the", "wind"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "/the/", "/wind/"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "//the//", "//wind//"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "\\the\\", "\\wind\\"));
        }

        // Testcase : root is File
        {
            File root = new File("file:/color");

            File expected = new File("file://color/the");
            Assert.assertEquals(expected, CWFileUtils.join(root, "the"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "/the"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "the/"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "/the/"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "//the//"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "///the///"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "\\the\\"));

            expected = new File("file:/color/the/wind");
            Assert.assertEquals(expected, CWFileUtils.join(root, "the", "wind"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "/the/", "/wind/"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "//the//", "//wind//"));
            Assert.assertEquals(expected, CWFileUtils.join(root, "\\the\\", "\\wind\\"));
        }
    }

    @Test
    public void toLinuxPath() {
        Assert.assertEquals("file:/color/the/wind", CWFileUtils.toLinuxPath("file:\\color\\the\\wind"));
        Assert.assertEquals("file:/color/the/wind/", CWFileUtils.toLinuxPath("file:\\color\\the\\wind\\"));
    }

    @Test
    public void getFileNameWithoutExtension() {
        Assert.assertEquals("", CWFileUtils.getFileNameWithoutExtension(""));
        Assert.assertEquals(".", CWFileUtils.getFileNameWithoutExtension("."));
        Assert.assertEquals("color", CWFileUtils.getFileNameWithoutExtension("color"));
        Assert.assertEquals("color", CWFileUtils.getFileNameWithoutExtension("color."));
        Assert.assertEquals("color", CWFileUtils.getFileNameWithoutExtension("color.png"));
        Assert.assertEquals("color.exe", CWFileUtils.getFileNameWithoutExtension("color.exe.png"));
    }

    @Test
    public void getFileExtension() {
        Assert.assertEquals("", CWFileUtils.getFileExtension(""));
        Assert.assertEquals("", CWFileUtils.getFileExtension("color."));
        Assert.assertEquals("", CWFileUtils.getFileExtension("color.."));
        Assert.assertEquals("", CWFileUtils.getFileExtension("color.%?"));
        Assert.assertEquals(".png", CWFileUtils.getFileExtension("color.png"));
        Assert.assertEquals(".png", CWFileUtils.getFileExtension("color..exe.png"));
    }
}
