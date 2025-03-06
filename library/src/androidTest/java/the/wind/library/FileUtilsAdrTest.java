package the.wind.library;

import android.content.Context;
import android.os.Environment;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import the.wind.library.utils.CWFileUtils;

@RunWith(AndroidJUnit4.class)
public class FileUtilsAdrTest {

    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Before
    public void before() {
        CWFileUtils.clearDir(context.getExternalFilesDir(null));
    }


    @After
    public void after() {
        CWFileUtils.clearDir(context.getExternalFilesDir(null));
    }

    @Test
    public void deleteFile() throws IOException {
        String data = "Color the wind";

        // Testcase 1: Delete an exist file
        {
            File file = new File(context.getExternalFilesDir(null), "data.text");
            CWFileUtils.write(data.getBytes(StandardCharsets.UTF_8), file);

            Assert.assertTrue(file.exists());
            Assert.assertTrue(CWFileUtils.deleteFile(file));
            Assert.assertFalse(file.exists());
            Assert.assertFalse(CWFileUtils.deleteFile(file));
        }

        // Testcase 2: Delete an exist file by path
        {
            File file = new File(context.getExternalFilesDir(null), "data.text");
            CWFileUtils.write(data.getBytes(StandardCharsets.UTF_8), file);

            Assert.assertTrue(CWFileUtils.deleteFile(file.getAbsolutePath() + "   "));
            Assert.assertFalse(file.isFile());
            Assert.assertFalse(CWFileUtils.deleteFile(file));
        }

        // Testcase 3: delete invalid file
        {
            // file not found
            File file = new File(context.getExternalFilesDir(null), "data.text");
            Assert.assertFalse(CWFileUtils.deleteFile(file));
            Assert.assertFalse(CWFileUtils.deleteFile("   "));
            Assert.assertFalse(CWFileUtils.deleteFile("   color.png "));

            // file is directory
            file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            assert file != null;
            Assert.assertTrue(file.isDirectory());
            Assert.assertFalse(CWFileUtils.deleteFile(file));
            Assert.assertTrue(file.isDirectory());
        }
    }

    @Test
    public void deleteDir() throws IOException {
        // Testcase 1: delete empty directory
        {
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            Assert.assertTrue(CWFileUtils.deleteDir(dir));
        }

        // Testcase 2: delete not empty directory
        {
            /*
             * |-- download
             *      |-- data.text
             *      |-- color.png
             *      |-- program
             */
            File download = new File(context.getExternalFilesDir(null), "download");
            Assert.assertTrue(download.mkdir());
            File program = new File(download, "program");
            Assert.assertTrue(program.mkdir());
            File data = new File(download, "data.text");
            Assert.assertTrue(data.createNewFile());
            File color = new File(download, "color.png");
            Assert.assertTrue(color.createNewFile());
            Assert.assertEquals(3, download.list().length);

            // delete directory
            Assert.assertTrue(CWFileUtils.deleteDir(download));
            Assert.assertFalse(download.exists());
            Assert.assertFalse(program.exists());
            Assert.assertFalse(data.exists());
        }

        // Testcase 3: delete invalid directory
        {
            Assert.assertFalse(CWFileUtils.deleteDir("   "));
            Assert.assertFalse(CWFileUtils.deleteDir("   color.png "));

            File file = new File(context.getExternalFilesDir(null), "data.text");
            Assert.assertFalse(CWFileUtils.deleteDir(file));
        }
    }

    @Test
    public void clearDir() throws IOException {
        /*
         * |-- download
         *      |-- data.text
         *      |-- color.png
         *      |-- program
         */
        File download = new File(context.getExternalFilesDir(null), "download");
        Assert.assertTrue(download.mkdir());
        File program = new File(download, "program");
        Assert.assertTrue(program.mkdir());
        File data = new File(download, "data.text");
        Assert.assertTrue(data.createNewFile());
        File color = new File(download, "color.png");
        Assert.assertTrue(color.createNewFile());
        Assert.assertEquals(3, download.list().length);

        // clear directory
        CWFileUtils.clearDir(download);
        Assert.assertTrue(download.exists());
        Assert.assertFalse(program.exists());
        Assert.assertFalse(data.exists());
        Assert.assertFalse(color.exists());
    }

    @Test
    public void renameTo() throws IOException {
        File file = new File(context.getExternalFilesDir(null), "data.text");
        Assert.assertTrue(file.createNewFile());
        Assert.assertNotNull(CWFileUtils.rename(file, "color"));
        Assert.assertFalse(file.exists());

        file = new File(context.getExternalFilesDir(null), "color.text");
        Assert.assertTrue(file.exists());
    }

    @Test
    public void moveFileToDir() throws IOException {
        File file = new File(context.getExternalFilesDir(null), "data.text");
        Assert.assertTrue(file.createNewFile());
        File download = new File(context.getExternalFilesDir(null), "download");
        Assert.assertTrue(download.mkdir());

        // Move file into directory
        file = CWFileUtils.moveFileToDir(file, download);
        assert file != null;
        Assert.assertEquals(1, download.list().length);
        Assert.assertTrue(file.exists());
        Assert.assertEquals(CWFileUtils.join(download, "data.text").getAbsolutePath(), file.getAbsolutePath());
    }

    @Test
    public void copy() throws IOException {
        String data = "Color the wind";

        // create source file
        File src = new File(context.getExternalFilesDir(null), "src.text");
        CWFileUtils.write(data.getBytes(StandardCharsets.UTF_8), src);

        // create destination file
        File dest = new File(context.getExternalFilesDir(null), "dest.text");

        // Copy data from source file to dest file
        CWFileUtils.copyFile(src, dest);
        Assert.assertTrue(src.exists());
        Assert.assertTrue(dest.exists());
        Assert.assertEquals(data, CWFileUtils.readString(src));
        Assert.assertEquals(data, CWFileUtils.readString(dest));
    }

    @Test
    public void copyDir() throws Exception {
        File rootDir = context.getExternalFilesDir(null);
        String rootPath = rootDir.getAbsolutePath();
        File srcDir = createSampleDir(rootDir);

        File destDir = new File(rootDir, "dest");
        CWFileUtils.copyDir(srcDir, destDir);

        // List all copied file in destination directory
        List<String> paths = listDir(destDir).stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(rootPath + "/dest", paths.get(0));
        Assert.assertEquals(rootPath + "/dest/empty", paths.get(1));
        Assert.assertEquals(rootPath + "/dest/file1.txt", paths.get(2));
        Assert.assertEquals(rootPath + "/dest/file2.txt", paths.get(3));
        Assert.assertEquals(rootPath + "/dest/sub-1", paths.get(4));
        Assert.assertEquals(rootPath + "/dest/sub-1/file3.txt", paths.get(5));
        Assert.assertEquals(rootPath + "/dest/sub-1/file4.txt", paths.get(6));
        Assert.assertEquals(rootPath + "/dest/sub-2", paths.get(7));
        Assert.assertEquals(rootPath + "/dest/sub-2/sub-3", paths.get(8));
        Assert.assertEquals(rootPath + "/dest/sub-2/sub-3/file5.txt", paths.get(9));

        // Delete directory
        CWFileUtils.deleteDir(srcDir);
        CWFileUtils.deleteDir(destDir);
    }

    @Test
    public void zipAndUnzipDir() throws IOException {
        File rootDir = context.getExternalFilesDir(null);
        String rootPath = rootDir.getAbsolutePath();
        File srcDir = createSampleDir(rootDir);
        File zipFile = new File(rootDir, "src.zip");
        File destDir = new File(rootDir, "dest");

        // zip directory
        CWFileUtils.zipDir(srcDir, zipFile);

        // Unzip directory
        CWFileUtils.unzipDir(zipFile, destDir);
        // List all copied file in destination directory
        List<String> paths = listDir(destDir).stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(rootPath + "/dest", paths.get(0));
        Assert.assertEquals(rootPath + "/dest/file1.txt", paths.get(1));
        Assert.assertEquals(rootPath + "/dest/file2.txt", paths.get(2));
        Assert.assertEquals(rootPath + "/dest/sub-1", paths.get(3));
        Assert.assertEquals(rootPath + "/dest/sub-1/file3.txt", paths.get(4));
        Assert.assertEquals(rootPath + "/dest/sub-1/file4.txt", paths.get(5));
        Assert.assertEquals(rootPath + "/dest/sub-2", paths.get(6));
        Assert.assertEquals(rootPath + "/dest/sub-2/sub-3", paths.get(7));
        Assert.assertEquals(rootPath + "/dest/sub-2/sub-3/file5.txt", paths.get(8));

        // Delete directory
        CWFileUtils.deleteDir(srcDir);
        CWFileUtils.deleteDir(destDir);
        CWFileUtils.deleteFile(zipFile);

    }

    private File createSampleDir(File rootDir) throws IOException {
        /*
         * |- src
         *      |- file1.txt
         *      |- file2.txt
         *      |- sub-1
         *          |- file3.txt
         *          |- file4.txt
         *      |- sub-2
         *          |- sub-3
         *              |- file5.txt
         *      |-empty
         * |- dest
         */
        File srcDir = new File(rootDir, "src");
        {
            srcDir.mkdirs();
            String data = "Color the wind";
            CWFileUtils.write(data.getBytes(StandardCharsets.UTF_8), new File(srcDir, "file1.txt"));
            CWFileUtils.write(data.getBytes(StandardCharsets.UTF_8), new File(srcDir, "file2.txt"));

            // sub-1 dir
            {
                File sub1Dir = new File(srcDir, "sub-1");
                sub1Dir.mkdirs();
                CWFileUtils.write(data.getBytes(StandardCharsets.UTF_8), new File(sub1Dir, "file3.txt"));
                CWFileUtils.write(data.getBytes(StandardCharsets.UTF_8), new File(sub1Dir, "file4.txt"));
            }

            // sub-2 dir
            {
                File sub2Dir = new File(srcDir, "sub-2");
                File sub3Dir = new File(sub2Dir, "sub-3");
                sub3Dir.mkdirs();
                CWFileUtils.write(data.getBytes(StandardCharsets.UTF_8), new File(sub3Dir, "file5.txt"));
            }

            // empty dir
            new File(srcDir, "empty").mkdirs();
        }
        return srcDir;
    }

    private static List<String> listDir(File dir) {
        List<String> results = new LinkedList<>();
        results.add(dir.getAbsolutePath());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                results.addAll(listDir(file));
            } else {
                results.add(file.getAbsolutePath());
            }
        }
        return results;
    }

    @Test
    public void readWriteString() throws IOException {
        // Testcase 1: write and read English
        {
            String data = "Color the wind";
            File file = new File(context.getExternalFilesDir(null), "data.text");
            CWFileUtils.write(data.getBytes(StandardCharsets.UTF_8), file);
            Assert.assertEquals(data, CWFileUtils.readString(file));
        }

        // Testcase 1: write and read Vietnamese
        {
            String data = "Tô màu cho gió";
            File file = new File(context.getExternalFilesDir(null), "data.text");
            CWFileUtils.write(data.getBytes(StandardCharsets.UTF_8), file);
            Assert.assertEquals(data, CWFileUtils.readString(file));
        }

        // Testcase 1: write and read Japanese
        {
            String data = "風を彩る。";
            File file = new File(context.getExternalFilesDir(null), "data.text");
            CWFileUtils.write(data.getBytes(StandardCharsets.UTF_8), file);
            Assert.assertEquals(data, CWFileUtils.readString(file));
        }
    }

}
