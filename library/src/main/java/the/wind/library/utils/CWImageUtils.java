package the.wind.library.utils;

import android.content.Context;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

/**
 * Provide function for converting image from a format to other
 */
public final class CWImageUtils {

    /**
     * Convert drawable to bitmap
     *
     * @param drawable android drawable
     * @return bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Convert android drawable to bitmap
     *
     * @param context     application context
     * @param drawableRes drawable resources
     * @return bitmap
     */
    public static Bitmap drawbleToBitmap(Context context, int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        return drawableToBitmap(drawable);
    }

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
     * Convert stream to bitmap
     *
     * @param stream file input stream
     * @param width  width of image viewer
     * @param height height image viewer
     * @return bitmap
     */
    @Nullable
    public static Bitmap streamToBitmap(InputStream stream, int width, int height) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, bmOptions);

        // Determine how much to scale down the image
        int scaleFactor = calculateInSampleSize(bmOptions, width, height);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        return BitmapFactory.decodeStream(stream, null, bmOptions);
    }

    /**
     * Load bitmap from file with the given dimension
     *
     * @param imageFile absolute image file path
     * @param width     width of image viewer
     * @param height    height image viewer
     * @return scaled bitmap
     */
    @Nullable
    public static Bitmap fileToBitmap(File imageFile, int width, int height) {
        try {
            return streamToBitmap(new FileInputStream(imageFile), width, height);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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
     * Compress image.
     * Refer: http://stackoverflow.com/questions/28424942/decrease-image-size-without-losing-its-quality-in-android
     *
     * @param filePath      image file path
     * @param maxWidth      scaled width
     * @param maxHeight     scaled height. If height=0, image will remain ratio
     * @param checkRotation check and fix the rotation or not. (cost performance)
     * @return compressed bitmap if success else return null (also in case image is not compressed)
     */
    @Nullable
    public static Bitmap compress(String filePath, float maxWidth, float maxHeight, boolean checkRotation) {
        try {
            // By setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
            // You try the use the bitmap here, you will get null.
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

            // return if current image's width < expected width
            if (actualWidth < maxWidth) {
                return null;
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
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
            Bitmap scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp,
                              middleX - bmp.getWidth() / 2f,
                              middleY - bmp.getHeight() / 2f,
                              new Paint(Paint.FILTER_BITMAP_FLAG));

            // check the rotation of the image and display it properly
            if (checkRotation) {
                ExifInterface exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                scaledBitmap = Bitmap.createBitmap(
                        scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(),
                        matrix, true);

            }
            return scaledBitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Calculated sample size of bitmap
     * https://developer.android.com/topic/performance/graphics/load-bitmap.html
     *
     * @param options   bitmap options
     * @param reqWidth  required width
     * @param reqHeight required height
     * @return sample size
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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

    /**
     * Load bitmap from asset file
     *
     * @param assetMgr asset manager
     * @param fileName assert filename
     * @param width    width of image viewer
     * @param height   height image viewer
     * @return bitmap image
     */
    public static Bitmap loadBitmap(
            AssetManager assetMgr, String fileName,
            int width, int height) throws IOException {

        InputStream is = assetMgr.open(fileName);
        Bitmap bitmap = streamToBitmap(is, width, height);
        is.close();
        return bitmap;
    }

    /**
     * Save bitmap image to file
     *
     * @param bmp  bitmap image
     * @param file bitmap file
     * @return file
     */
    public static File write(Bitmap bmp, File file, Bitmap.CompressFormat format) throws Exception {
        FileOutputStream out = new FileOutputStream(file);
        bmp.compress(format, 80, out);
        out.flush();
        out.close();
        return file;
    }
}
