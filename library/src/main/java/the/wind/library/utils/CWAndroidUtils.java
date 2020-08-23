package the.wind.library.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.util.Size;
import android.view.Display;
import android.view.View;
import android.view.Window;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Provide utility methods
 */
public final class CWAndroidUtils {

    /**
     * Check if the internet is available or not
     *
     * @param context android context
     * @return true if available
     */
    public static boolean isNetworkConnected(Context context) {
        if (context == null) return false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    /**
     * Check if GPS service on or of
     *
     * @param context android context
     * @return true if on
     */
    public static boolean isGpsAvailable(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm != null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * TODO: will implement this function later
     * Get IMEI number which is unique for each android device
     *
     * @param context application context
     * @return device number.
     */
    @Deprecated
    public static String getImei(Context context) {
        return null;
    }

    /**
     * Get device id. for example: 9774d56d682e549c
     * This id may be changed if user reset factory.
     *
     * @param context application context
     * @return device ID
     */
    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Checks if external storage is available for read and write
     *
     * @return true if available
     */
    public static boolean isExtStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Check if device has camera or not
     *
     * @param context activity
     * @return true if system has camera
     */
    public static boolean isCameraAvailable(Context context) {
        PackageManager manager = context.getPackageManager();
        return manager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Get size of screen
     *
     * @param context android activity
     * @return size of current screen
     */
    public static Size getScreenSize(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new Size(size.x, size.y);
    }

    /**
     * get status bar's height
     *
     * @param activity activity
     * @return status bar's height
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect rectangle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    /**
     * Take screen shot
     *
     * @param activity activity
     * @return a bitmap
     */
    public static Bitmap takeScreenshot(Activity activity) {
        // create bitmap screen capture
        View rootView = activity.getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * Determine if the device has an extra-large screen.
     * For example, 10" tablets are extra-large.
     *
     * @param context android context
     * @return true if device has an extra-large screen
     */
    public static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Get facebook url
     *
     * @return facebook url
     */
    public static String getFacebookPageURL(Context context, String pageUrl, String pageID) {
        try {
            int versionCode = context.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + pageUrl;
            } else { //older versions of fb app
                return "fb://page/" + pageID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return pageUrl; //normal web url
        }
    }

    /**
     * Open this application page on play store
     *
     * @param context application context
     */
    public static void openAppOnPlayStore(Context context) {
        final String appPackageName = context.getPackageName();
        Uri uri;
        try {
            uri = Uri.parse("market://details?id=" + appPackageName);
        } catch (Exception ex) {
            uri = Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName);
        }
        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    /**
     * Convert resource id to uri
     *
     * @param context context
     * @param id      resource id. Ex, R.drawable.thumbnail
     * @return Uri
     */
    public static Uri resourceToUri(Context context, int id) {
        return Uri.parse("android.resource://" + context.getPackageName() + File.pathSeparator + +id);
    }

    /**
     * @param path path. Ex, "sticker/butterfly.png"
     * @return full assets path
     */
    public static String getFullAssetsPath(String path) {
        return "file:///android_asset/" + path;
    }

    /**
     * Generate random hash string for given object
     *
     * @param context application context
     * @param seed    a key to make hash more unique
     * @return random unique hash string
     */
    public static String randomHash(Context context, String seed) {
        String hash = UUID.randomUUID().toString()
                + CWAndroidUtils.getDeviceId(context)
                + seed
                + Math.random()
                + new Date().getTime();
        return CWCryptoUtils.sha1(hash);
    }

    /**
     * Check if current thread is main UI thread or not
     *
     * @return true if current thread is main thread
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}
