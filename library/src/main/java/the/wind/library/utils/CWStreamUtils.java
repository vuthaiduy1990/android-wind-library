package the.wind.library.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class CWStreamUtils {

    /**
     * Convert byte to hex
     *
     * @param data byte data
     * @return hex string
     */
    public static String bytesToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfByte = (b >>> 4) & 0x0F;
            int twoHalf = 0;
            do {
                buf.append(halfByte <= 9 ? (char) ('0' + halfByte) : (char) ('a' + halfByte - 10));
                halfByte = b & 0x0F;
            } while (twoHalf++ < 1);
        }
        return buf.toString();
    }

    /**
     * Convert stream to byte arrays
     *
     * @param is input stream
     * @return byte array
     */
    public static byte[] streamToBytes(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024 * 4];
        int len;
        while ((len = is.read(buffer)) >= 0) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    /**
     * Convert input stream to string
     *
     * @param is input stream
     * @return string content
     *
     * @throws IOException exception
     */
    public static String streamToString(InputStream is) throws IOException {
        return bytesToString(streamToBytes(is));
    }

    /**
     * Convert bytes to string
     *
     * @param data byte data
     * @return string content
     */
    public static String bytesToString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    /**
     * Convert string to bytes
     *
     * @param data string data
     * @return byte data
     */
    public static byte[] stringToBytes(String data) {
        return data.getBytes(StandardCharsets.UTF_8);
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
}
