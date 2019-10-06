package the.wind.library.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

/**
 * Provide method for handling file, folder
 */
public final class CWFileUtils {

    /**
     * Join file paths
     *
     * @param rootPath root path
     * @param paths    sub paths
     * @return joined path by "/" symbol
     */
    public static File join(String rootPath, String... paths) {
        File root = new File(rootPath);
        for (String path : paths) {
            root = new File(root.getPath(), path);
        }
        return root;
    }

    /**
     * Join file path
     *
     * @param root  root file path
     * @param paths sub file paths
     * @return joined path by "/" symbol
     */
    public static File join(File root, String... paths) {
        return join(root.getPath(), paths);
    }

    /**
     * Replace slash path (\) used in window by (/) in linux environment
     *
     * @param path path
     * @return path with slash of linux environment
     */
    public static String toLinuxPath(String path) {
        return path.replace("\\", "/");
    }

    /**
     * Get file name without extension.
     * <pre>
     *     "color.png" -> color
     *     "color.exe.png" -> color.exe
     * </pre>
     *
     * @param fileName name with extension
     * @return name without extension
     */
    public static String getFileNameWithoutExtension(String fileName) {
        int extIdx = fileName.lastIndexOf(".");
        return (extIdx > 0) ? fileName.substring(0, extIdx) : fileName;
    }

    /**
     * Get file extension
     *
     * @param fileName file name
     * @return extension of file. For example: ".jpg" or ".png"
     */
    public static String getFileExtension(String fileName) {
        int extIdx = fileName.lastIndexOf(".");
        String result = (extIdx > 0) ? fileName.substring(extIdx) : "";
        return result.matches("\\.\\w+") ? result : "";
    }

    /**
     * Delete file
     *
     * @param file file
     */
    public static boolean deleteFile(File file) {
        if (file != null && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Delete file
     *
     * @param filePath absolute file path
     */
    public static boolean deleteFile(String filePath) {
        if (filePath != null && !filePath.trim().isEmpty()) {
            return deleteFile(new File(filePath.trim()));
        }
        return false;
    }

    /**
     * Delete directory
     *
     * @param dir directory path
     */
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            clearDir(dir);
            return dir.delete();
        }
        return false;
    }

    /**
     * Delete directory
     *
     * @param dirPath absolute directory path
     */
    public static boolean deleteDir(String dirPath) {
        if (dirPath != null && !dirPath.trim().isEmpty()) {
            return deleteDir(new File(dirPath.trim()));
        }
        return false;
    }

    /**
     * Clear all files and directories in given directory.
     * Note: Be-careful when using this function.
     *
     * @param dir directory
     */
    public static void clearDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    deleteFile(f);
                }
            }
        }
    }

    /**
     * Clear all files in given directory
     *
     * @param dir directory
     */
    public static void clearDir(String dir) {
        if (dir != null && !dir.trim().isEmpty()) {
            clearDir(new File(dir.trim()));
        }
    }


    /**
     * Move file to specific directory
     *
     * @param file file
     * @param dir  target directory
     * @return true if moving successfully
     */
    @Nullable
    public static File moveFileToDir(File file, File dir) {
        if (renameTo(file, new File(dir, file.getName()))) {
            return new File(dir, file.getName());
        }
        return null;
    }

    /**
     * Rename/move file to another directory
     *
     * @param srcFile  absolute old file path
     * @param destFile absolute new file path
     * @return true if renaming successfully
     */
    private static boolean renameTo(File srcFile, File destFile) {
        return srcFile.renameTo(destFile);
    }

    /**
     * Rename file
     *
     * @param file    file
     * @param newName new file name without extension
     * @return true if renaming successfully
     */
    @Nullable
    public static File rename(File file, String newName) {
        String ext = getFileExtension(file.getName());
        File renamedFile = new File(file.getParentFile(), newName + ext);
        if (renameTo(file, renamedFile)) {
            return renamedFile;
        }
        return null;
    }

    /**
     * Copy file to specific path
     *
     * @param src source file path
     * @param dst destination file path
     * @throws IOException exception
     */
    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    /**
     * Check where file is given file types or not
     *
     * @param file         file
     * @param supportTypes checked type (extension of file. For ex, .png, .exe)
     * @return true if file is image
     */
    public static boolean isType(File file, String[] supportTypes) {
        // check file is file or directory
        if (!file.isFile()) {
            return false;
        }

        // check extension to determine file is image or not
        String fileExt = getFileExtension(file.getName());
        for (String ext : supportTypes) {
            if (fileExt.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Write data to file
     *
     * @param data byte array
     * @param file file to saved
     * @throws IOException exception
     */
    public static void write(byte[] data, File file) throws IOException {
        if (data == null) throw new NullPointerException();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(data);
        bos.flush();
        bos.close();
    }

    /**
     * Write stream data to file
     *
     * @param stream stream data
     * @param file   file
     */
    public static void write(InputStream stream, File file) throws IOException {
        if (stream == null) throw new NullPointerException();
        byte[] data = CWStreamUtils.streamToBytes(stream);
        write(data, file);
    }

    /**
     * Read data from file
     *
     * @param file file which contain string value
     * @return bytes data
     */
    public static byte[] read(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        return CWStreamUtils.streamToBytes(is);
    }

    /**
     * Read string data from file
     *
     * @param file file which contain string value
     * @return string data
     */
    public static String readString(File file) throws IOException {
        return CWStreamUtils.bytesToString(read(file));
    }

    /**
     * Read bytes data from asset file
     * Asset directory is named after "assets"
     * <pre>
     *     |-- assets
     *     |    |-- images
     *     |    |   |-- color.png
     *     |    |   |-- wind.png
     *     |    |-- setting
     *     |    |   |-- user.json
     *     |    |-- app.json
     * </pre>
     * <pre>
     *     read(context.getAssets(), "images/color.png")
     *     read(context.getAssets(), "setting/user.json")
     *     read(context.getAssets(), "app.json")
     * </pre>
     *
     * @param assetMgr asset manager
     * @param fileName asset filename
     * @return bytes data
     */
    public static byte[] read(AssetManager assetMgr, String fileName) throws IOException {
        InputStream is = assetMgr.open(fileName);
        return CWStreamUtils.streamToBytes(is);
    }

    /**
     * Read string data from asset file
     * Asset directory is named after "assets"
     * <pre>
     *     |-- assets
     *     |    |-- images
     *     |    |   |-- color.png
     *     |    |   |-- wind.png
     *     |    |-- setting
     *     |    |   |-- user.json
     * </pre>
     * <pre>
     *     read(context.getAssets(), "images/color.png")
     *     read(context.getAssets(), "setting/user.json")
     *     read(context.getAssets(), "app.json")
     * </pre>
     *
     * @param assetMgr asset manager
     * @param fileName asset filename
     * @return string data
     */
    public static String readString(AssetManager assetMgr, String fileName) throws IOException {
        return CWStreamUtils.bytesToString(read(assetMgr, fileName));
    }

    /**
     * Load asset file
     *
     * @param assetMgr asset manager
     * @param fileName asset filename
     * @return
     */
    public static byte[] loadAssetFile(AssetManager assetMgr, String fileName) throws IOException {
        InputStream is = assetMgr.open(fileName);
        return CWStreamUtils.streamToBytes(is);
    }

    /**
     * Get Uri from file
     * https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
     *
     * @param context application context
     * @param file    file
     * @return uri
     */
    public static Uri getUriFromFile(Context context, File file) {
        if (!file.exists()) return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(
                    context,
                    context.getApplicationContext().getPackageName() + ".provider",
                    file);
        }
        return Uri.fromFile(file);
    }

}
