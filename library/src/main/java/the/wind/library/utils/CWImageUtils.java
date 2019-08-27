package the.wind.library.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provide function for converting image from a format to other
 */
public final class CWImageUtils {

    /**
     * Convert bitmap to bytes
     *
     * @param bitmap bitmap image
     * @param format image format. Ex, PNG, JPEG
     * @return bytes
     */
    public static byte[] bitmapToBytes(Bitmap bitmap, Bitmap.CompressFormat format) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(format, 100, stream);
        byte[] result = stream.toByteArray();
        stream.flush();
        stream.close();
        return result;
    }

    /**
     * Convert bytes to bitmap
     *
     * @param data image's data in bytes
     * @return bitmap image
     */
    public static Bitmap bytesToBitmap(byte[] data) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(data);
        byte[] bmpData = stream.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bmpData, 0, bmpData.length);
        stream.flush();
        stream.close();
        return bitmap;
    }

    /**
     * Convert bitmap to stream
     *
     * @param bmp bitmap image
     *            param format image format. Ex, PNG, JPEG
     * @return data stream
     */
    public static InputStream bitmapToStream(Bitmap bmp, Bitmap.CompressFormat format) throws IOException {
        byte[] bytes = bitmapToBytes(bmp, format);
        return bytes == null ? null : new ByteArrayInputStream(bytes);
    }

    /**
     * Convert stream to bitmap
     *
     * @param stream input stream
     * @return bitmap
     */
    public static Bitmap streamToBitmap(InputStream stream) {
        return BitmapFactory.decodeStream(stream);
    }

    /**
     * Load bitmap from file with specific dimension
     *
     * @param imagePath absolute image file path
     * @param width     width of image viewer
     * @param height    height image viewer
     * @return scaled bitmap
     */
    public static Bitmap fileToBitmap(String imagePath, int width, int height) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);

        // Determine how much to scale down the image
        int scaleFactor = calculateInSampleSize(bmOptions, width, height);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        return BitmapFactory.decodeFile(imagePath, bmOptions);
    }

    /**
     * Load bitmap from file
     *
     * @param imageFile image  file
     * @param width     width of image viewer
     * @param height    height image viewer
     * @return bitmap
     */
    public static Bitmap fileToBitmap(File imageFile, int width, int height) {
        return fileToBitmap(imageFile.getAbsolutePath(), width, height);
    }

    /**
     * Save bitmap image to file
     *
     * @param bmp      bitmap image
     * @param filePath file path to save
     * @return file
     */
    public static File bitmapToFile(Bitmap bmp, String filePath, Bitmap.CompressFormat format) throws IOException {
        FileOutputStream out = new FileOutputStream(filePath);
        bmp.compress(format, 80, out);
        out.flush();
        out.close();
        return new File(filePath);
    }

    /**
     * Save bitmap image to file
     *
     * @param bmp  bitmap image
     * @param file file for save
     * @return file
     */
    public static File bitmapToFile(Bitmap bmp, File file, Bitmap.CompressFormat format) throws IOException {
        return bitmapToFile(bmp, file.getAbsolutePath(), format);
    }

    /**
     * Create rounded image
     * https://ruibm.com/2009/06/16/rounded-corner-bitmaps-on-android/
     *
     * @param bitmap bitmap image
     * @param pixels radius in pixel
     * @return rounded bitmap
     */
    public static Bitmap getRoundedBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, pixels, pixels, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * Scale down and compress image.
     * If originBitmap != null, filePath should be null and otherwise
     * http://stackoverflow.com/questions/28424942/decrease-image-size-without-losing-its-quality-in-android
     *
     * @param originBitmap origin bitmap.
     * @param filePath     origin image file
     * @param maxWidth     scaled down width should be less then actual width
     * @param maxHeight    scaled down height. If height=0, image will remain ratio
     * @param format       image format. Ex, PNG, JPEG
     * @return scaled compressed image file path
     */
    private static Bitmap compress(
            Bitmap originBitmap,
            String filePath,
            float maxWidth, float maxHeight,
            Bitmap.CompressFormat format) throws Exception {

        Bitmap scaledBitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();

        // Compute actual size of bitmap
        int actualWidth, actualHeight;
        if (originBitmap != null) {
            actualHeight = originBitmap.getHeight();
            actualWidth = originBitmap.getWidth();

        } else {
            // By setting this field as true, the actual bitmap pixels are not loaded in the memory.
            // Just the bounds are loaded. If You try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            actualWidth = options.outWidth;
            actualHeight = options.outHeight;
        }

        // return if current image's width < expected width
        if (actualWidth < maxWidth) {
            if (originBitmap != null) {
                return originBitmap;
            }
            return fileToBitmap(filePath, actualWidth, actualHeight);
        }

        // max Height and width values of the compressed image is taken as 816x612
        float imgRatio = (float) actualWidth / actualHeight;
        if (maxHeight == 0) {
            maxHeight = maxWidth * actualHeight / actualWidth;
        }
        float maxRatio = maxWidth / maxHeight;

        // width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        // setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        // inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inTempStorage = new byte[16 * 1024];
        Bitmap bmp;
        if (originBitmap != null) {
            bmp = BitmapFactory.decodeStream(bitmapToStream(originBitmap, format), null, options);
        } else /* bitmap */ {
            bmp = BitmapFactory.decodeFile(filePath, options);
        }
        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        assert bmp != null;
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2f, middleY - bmp.getHeight() / 2f, new Paint(Paint.FILTER_BITMAP_FLAG));

//        // Do not need to check rotation -> Comment out this process to increase performance
//        // check the rotation of the image and display it properly
//        ExifInterface exif;
//        try {
//            exif = new ExifInterface(filePath);
//
//            int orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION, 0);
//            Matrix matrix = new Matrix();
//            if (orientation == 6) {
//                matrix.postRotate(90);
//            } else if (orientation == 3) {
//                matrix.postRotate(180);
//            } else if (orientation == 8) {
//                matrix.postRotate(270);
//            }
//            scaledBitmap = Bitmap.createBitmap(
//                    scaledBitmap, 0, 0,
//                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return scaledBitmap;
    }

    /**
     * Compress image.
     * Refer: http://stackoverflow.com/questions/28424942/decrease-image-size-without-losing-its-quality-in-android
     *
     * @param filePath  image file path
     * @param maxWidth  scaled down width should be less then actual width
     * @param maxHeight scaled down height. If height=0, image will remain ratio
     * @param format    image format. Ex, PNG, JPEG
     * @return scaled compressed image file path
     */
    public static Bitmap compress(
            String filePath,
            float maxWidth, float maxHeight,
            Bitmap.CompressFormat format) throws Exception {
        return compress(null, filePath, maxWidth, maxHeight, format);
    }

    /**
     * Compress image.
     * Refer: http://stackoverflow.com/questions/28424942/decrease-image-size-without-losing-its-quality-in-android
     *
     * @param originBitmap bitmap
     * @param maxWidth     scaled down width should be less then actual width
     * @param maxHeight    scaled down height. If height=0, image will remain ratio
     * @param format       image format. Ex, PNG, JPEG
     * @return scaled compressed  image
     */
    public static Bitmap compress(
            Bitmap originBitmap,
            float maxWidth, float maxHeight,
            Bitmap.CompressFormat format) throws Exception {
        return compress(originBitmap, null, maxWidth, maxHeight, format);
    }

    /**
     * Calculated sample size of bitmap
     *
     * @param options   bitmap options
     * @param reqWidth  required width
     * @param reqHeight required height
     * @return sample size
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    /**
     * Scale down bitmap to specified size
     *
     * @param width  desired width
     * @param height desired height
     * @param format image format. Ex, PNG, JPEG
     * @return resized bitmap
     */
    public static Bitmap scaleDown(Bitmap bmp, int width, int height, Bitmap.CompressFormat format) throws IOException {
        // Get the dimensions of the bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(bitmapToStream(bmp, format), null, options);

        // Determine how much to scale down the image
        int scaleFactor = calculateInSampleSize(options, width, height);

        // scale down bitmap to specified scale
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        return BitmapFactory.decodeStream(bitmapToStream(bmp, format), null, options);
    }

    /**
     * Load bitmap from asset file
     *
     * @param assetMgr asset manager
     * @param fileName assert filename
     * @return bitmap image
     */
    public static Bitmap loadBitmap(AssetManager assetMgr, String fileName) throws IOException {
        InputStream is = assetMgr.open(fileName);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        is.close();
        return bitmap;
    }
}
