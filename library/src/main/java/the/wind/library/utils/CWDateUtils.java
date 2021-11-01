package the.wind.library.utils;


import android.icu.util.Calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.Nullable;

/**
 * Provide method for handling date
 * https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
 */
public final class CWDateUtils {

    public static final String DATE_TIME_FORMATTER = "yyyy'-'MM'-'dd'T'HH':'mm':'ss.SSS'Z'";
    public static final String DATE_FORMATTER = "yyyy'-'MM'-'dd";

    /**
     * Convert string to UTC date
     *
     * @param str    date string
     * @param format date format, ex yyyy'-'MM'-'dd hh':'mm':'ss
     * @return Date object
     */
    @Nullable
    public static Date stringToUtcDate(String str, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        try {
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return formatter.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convert date to date string
     *
     * @param date   date
     * @param format ex, yyyy'-'MM'-'dd'T'HH':'mm':'ss.SSS'Z'
     * @return date string. For ex, "2012-12-21T08:27:21.050Z"
     */
    public static String dateToString(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    /**
     * Convert date to date string
     * For ex, "2012-12-21T08:27:21.050Z"
     *
     * @param date date
     * @return date string with {{DATE_TIME_FORMATTER}}
     */
    public static String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMATTER, Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    /**
     * Get start of day
     *
     * @param cal  calendar
     * @param date given date
     * @return start of day
     */
    public static Date getStartOfDay(Calendar cal, Date date) {
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Get start of day
     *
     * @param cal calendar
     * @return start of day
     */
    public static Date getStartOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Get end of day
     *
     * @param cal  calendar
     * @param date given date
     * @return end of day
     */
    public static Date getEndOfDay(Calendar cal, Date date) {
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * Get end of day
     *
     * @param cal calendar
     * @return end of day
     */
    public static Date getEndOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}
