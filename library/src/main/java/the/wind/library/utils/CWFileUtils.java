package the.wind.library.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import androidx.annotation.NonNull;
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
            File[] files = dir.listFiles();
            if (files == null) return;
            for (File f : files) {
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
    public static boolean renameTo(File srcFile, File destFile) {
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
     * @param srcFile  source file path
     * @param destFile destination file path
     * @throws IOException exception
     */
    public static void copyFile(File srcFile, File destFile) throws IOException {
        InputStream in = new FileInputStream(srcFile);
        OutputStream out = new FileOutputStream(destFile);

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
     * Copy directory to specific directory
     *
     * @param srcDir  source directory
     * @param destDir destination directory
     * @throws IOException exception
     */
    public static void copyDir(@NonNull File srcDir, @NonNull File destDir) throws Exception {
        if (!srcDir.isDirectory()) {
            throw new IllegalArgumentException(srcDir.getAbsolutePath() + " is not directory");
        }

        if (destDir.exists() || destDir.mkdirs()) {
            File[] files = srcDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    File destFile = new File(destDir, file.getName());
                    if (file.isDirectory()) {
                        copyDir(file, destFile);
                    } else {
                        copyFile(file, destFile);
                    }
                }
            }
        } else {
            throw new FileNotFoundException(destDir.getAbsolutePath() + " does not exist");
        }
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
     * This method populates all the files in a directory to a List
     *
     * @param dir directory
     * @return all files and folder
     */
    public static List<File> populateFilesList(@NonNull File dir) {
        List<File> fileListInDir = new LinkedList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileListInDir.add(file);
                } else {
                    fileListInDir.addAll(populateFilesList(file));
                }
            }
        }
        return fileListInDir;
    }

    /**
     * Zip directory.
     * Zip operation will ignore empty directory
     * https://www.digitalocean.com/community/tutorials/java-zip-file-folder-example
     *
     * @param dir directory
     * @throws IOException exception
     */
    public static void zipDir(@NonNull File dir, @NonNull File zipFile) throws IOException {
        //create ZipOutputStream to write to the zip file
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);

        //now zip files one by one
        int dirPathLength = dir.getAbsolutePath().length();
        byte[] buffer = new byte[1024];
        int len;
        for (File file : populateFilesList(dir)) {
            //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
            String filePath = file.getAbsolutePath();
            ZipEntry ze = new ZipEntry(filePath.substring(dirPathLength + 1));
            zos.putNextEntry(ze);
            //read the file and write to ZipOutputStream
            FileInputStream fis = new FileInputStream(filePath);
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            zos.closeEntry();
            fis.close();
        }
        zos.close();
        fos.close();
    }

    /**
     * Unzip data
     * https://www.digitalocean.com/community/tutorials/java-unzip-file-example
     *
     * @param zipFile zip file
     * @param destDir destination directory
     * @throws IOException exception
     */
    public static void unzipDir(@NonNull File zipFile, @NonNull File destDir) throws IOException {
        if (!zipFile.isFile()) {
            throw new IllegalArgumentException(zipFile.getAbsolutePath() + " is not zip file");
        }
        if (!(destDir.exists() || destDir.mkdirs())) {
            throw new FileNotFoundException(destDir.getAbsolutePath() + " does not exist");
        }

        // create output directory if it doesn't exist
        //buffer for read and write data to file
        FileInputStream fis = new FileInputStream(zipFile);
        ZipInputStream zis = new ZipInputStream(fis);
        byte[] buffer = new byte[1024];
        int len;
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            String fileName = ze.getName();
            File newFile = new File(destDir.getAbsolutePath() + File.separator + fileName);
            File fileDir = newFile.getParentFile();
            //create directories for sub directories in zip
            if (fileDir != null && (fileDir.exists() || fileDir.mkdirs())) {
                FileOutputStream fos = new FileOutputStream(newFile);
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
        }
        //close last ZipEntry
        zis.closeEntry();
        zis.close();
        fis.close();
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
     * @return byte data
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
        return FileProvider.getUriForFile(
                context,
                context.getApplicationContext().getPackageName() + ".provider",
                file);
    }

}
