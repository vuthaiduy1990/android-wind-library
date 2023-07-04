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
 * <a href="https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html">...</a>
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

    /**
     * Check if two date info is equal.
     * Note: compare date without time
     *
     * @param d1 date info [era, year, month, month day, leap, week day, hour, minute, second]
     * @param d2 date info [era, year, month, month day, leap, week day, hour, minute, second]
     * @return true if equal else, return false
     */
    public static boolean equalDate(int[] d1, int[] d2) {
        // equal from date
        if (d1[0] != d2[0]) return false;
        int m1 = d1[1] * 12 + d1[2];
        int m2 = d2[1] * 12 + d2[2];
        return m1 == m2 && d1[3] == d2[3] && d1[4] == d2[4];
    }

    /**
     * Check if two date info is equal without comparing year.
     * Note: compare date without time
     *
     * @param d1 date info [era, year, month, month day, leap, week day, hour, minute, second]
     * @param d2 date info [era, year, month, month day, leap, week day, hour, minute, second]
     * @return true if equal else, return false
     */
    public static boolean equalDateWithoutYear(int[] d1, int[] d2) {
        return d1[2] == d2[2] && d1[3] == d2[3] && d1[4] == d2[4];
    }

    /**
     * Check if the first date less than the second date.
     * Note: compare date without time
     *
     * @param d1 date info [era, year, month, month day, leap, week day, hour, minute, second]
     * @param d2 date info [era, year, month, month day, leap, week day, hour, minute, second]
     * @return true if the first date less than the second date
     */
    public static boolean dateLessThan(int[] d1, int[] d2) {
        // era less than
        if (d1[0] < d2[0]) return true;

        if (d1[0] == d2[0]) {
            int m1 = d1[1] * 12 + d1[2];
            int m2 = d2[1] * 12 + d2[2];

            if (m1 < m2) return true; // month less than
            else if (m1 == m2) {
                //  month less than (leap month)
                if (d1[4] < d2[4]) return true;
                else if (d1[4] == d2[4]) {
                    // month equal -> compare date
                    return d1[3] < d2[3]; // date less than
                }
            }
        }
        return false;
    }

    /**
     * Check if the first date less than the second date without comparing year.
     * Note: compare date without time
     *
     * @param d1 date info [era, year, month, month day, leap, week day, hour, minute, second]
     * @param d2 date info [era, year, month, month day, leap, week day, hour, minute, second]
     * @return true if the first date less than the second date
     */
    public static boolean dateLessThanWithoutYear(int[] d1, int[] d2) {
        if (d1[2] < d2[2]) return true; // month less than
        else if (d1[2] == d2[2]) {
            //  month less than (leap month)
            if (d1[4] < d2[4]) return true;
            else if (d1[4] == d2[4]) {
                // month equal -> compare date
                return d1[3] < d2[3]; // date less than
            }
        }
        return false;
    }

    /**
     * Check if the first date greater than the second date.
     * Note: compare date without time
     * Note: compare date without time
     *
     * @param d1 date info [era, year, month, month day, leap, week day, hour, minute, second]
     * @param d2 date info [era, year, month, month day, leap, week day, hour, minute, second]
     * @return true if the first date less than the second date
     */
    public static boolean dateGreaterThan(int[] d1, int[] d2) {
        // era greater than
        if (d1[0] > d2[0]) return true;

        if (d1[0] == d2[0]) {
            int m1 = d1[1] * 12 + d1[2];
            int m2 = d2[1] * 12 + d2[2];

            if (m1 > m2) return true; // month greater than
            else if (m1 == m2) {
                //  month greater than (leap month)
                if (d1[4] > d2[4]) return true;
                else if (d1[4] == d2[4]) {
                    // month equal -> compare date
                    return d1[3] > d2[3]; // date greater than
                }
            }
        }
        return false;
    }

    /**
     * Check if the first date greater than the second date without comparing year.
     * Note: compare date without time
     * Note: compare date without time
     *
     * @param d1 date info [era, year, month, month day, leap, week day, hour, minute, second]
     * @param d2 date info [era, year, month, month day, leap, week day, hour, minute, second]
     * @return true if the first date less than the second date
     */
    public static boolean dateGreaterThanWithoutYear(int[] d1, int[] d2) {
        if (d1[2] > d2[2]) return true; // month greater than
        else if (d1[2] == d2[2]) {
            //  month greater than (leap month)
            if (d1[4] > d2[4]) return true;
            else if (d1[4] == d2[4]) {
                // month equal -> compare date
                return d1[3] > d2[3]; // date greater than
            }
        }
        return false;
    }

    /**
     * Compute number of days difference between two dates
     *
     * @param startDate start date
     * @param endDate   end date
     * @return number of days difference
     */
    public static int diffDays(Date startDate, Date endDate) {
        return (int) Math.floor((endDate.getTime() - startDate.getTime()) / 86400000d); // 24 * 60 * 60 * 1000
    }

    /**
     * Compute number of days difference between two dates
     *
     * @param startTime start time in milliseconds
     * @param endTime   end time in milliseconds
     * @return number of days difference
     */
    public static int diffDays(long startTime, long endTime) {
        return (int) Math.floor((endTime - startTime) / 86400000d); // 24 * 60 * 60 * 1000
    }
}
