package the.wind.library.calendar;

import android.icu.util.Calendar;
import android.icu.util.ChineseCalendar;

import androidx.annotation.NonNull;

/**
 * Copyright (c) 2006 Ho Ngoc Duc. All Rights Reserved.
 * Astronomical algorithms from the book "Astronomical Algorithms" by Jean Meeus, 1998
 * <p>
 * Permission to use, copy, modify, and redistribute this software and its
 * documentation for personal, non-commercial use is hereby granted provided that
 * this copyright notice and appropriate documentation appears in all copies.
 *
 * @see https://www.informatik.uni-leipzig.de/~duc/amlich/calrules.html
 */
public class VietnameseCalendar extends ChineseCalendar {

    private final double PI = Math.PI;

    /**
     * Discard the fractional part of a number, e.g., INT(3.2) = 3
     *
     * @param number number
     * @return round number
     */
    private int INT(double number) {
        return (int) Math.floor(number);
    }

    /**
     * Compute the (integral) Julian day number of day dd/mm/yyyy, i.e., the number
     * of days between 1/1/4713 BC (Julian calendar) and dd/mm/yyyy.
     * Formula from http://www.tondering.dk/claus/calendar.html
     *
     * @param dd date
     * @param mm month
     * @param yy year
     * @return number of julian day
     */
    private int jdFromDate(int dd, int mm, int yy) {
        int a, y, m, jd;
        a = INT((14 - mm) / 12d);
        y = yy + 4800 - a;
        m = mm + 12 * a - 3;
        jd = dd + INT((153 * m + 2) / 5d) + 365 * y + INT(y / 4d) - INT(y / 100d) + INT(y / 400d) - 32045;
        if (jd < 2299161) {
            jd = dd + INT((153 * m + 2) / 5d) + 365 * y + INT(y / 4d) - 32083;
        }
        return jd;
    }

    /**
     * Convert a Julian day number to day/month/year. Parameter jd is an integer
     *
     * @param jd julian day
     * @return date [year, month, day]
     */
    private int[] jdToDate(int jd) {
        int a, b, c, d, e, m, day, month, year;
        if (jd > 2299160) { // After 5/10/1582, Gregorian calendar
            a = jd + 32044;
            b = INT((4 * a + 3) / 146097d);
            c = a - INT((b * 146097) / 4d);
        } else {
            b = 0;
            c = jd + 32082;
        }
        d = INT((4 * c + 3) / 1461d);
        e = c - INT((1461 * d) / 4d);
        m = INT((5 * e + 2) / 153d);
        day = e - INT((153 * m + 2) / 5d) + 1;
        month = m + 3 - 12 * INT(m / 10d);
        year = b * 100 + d - 4800 + INT(m / 10d);
        return new int[]{year, month, day};
    }

    /**
     * Compute the time of the k-th new moon after the new moon of 1/1/1900 13:52 UCT
     * (measured as the number of days since 1/1/4713 BC noon UCT, e.g., 2451545.125 is 1/1/2000 15:00 UTC).
     * Algorithm from: "Astronomical Algorithms" by Jean Meeus, 1998
     *
     * @param k the k-th new moon
     * @return a floating number, e.g., 2415079.9758617813 for k=2 or 2414961.935157746 for k=-2.
     */
    private double NewMoon(int k) {
        double T, T2, T3, dr, Jd1, M, Mpr, F, C1, delta, JdNew;
        T = k / 1236.85; // Time in Julian centuries from 1900 January 0.5
        T2 = T * T;
        T3 = T2 * T;
        dr = PI / 180;
        Jd1 = 2415020.75933 + 29.53058868 * k + 0.0001178 * T2 - 0.000000155 * T3;
        Jd1 = Jd1 + 0.00033 * Math.sin((166.56 + 132.87 * T - 0.009173 * T2) * dr); // Mean new moon
        M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - 0.00000347 * T3; // Sun's mean anomaly
        Mpr = 306.0253 + 385.81691806 * k + 0.0107306 * T2 + 0.00001236 * T3; // Moon's mean anomaly
        F = 21.2964 + 390.67050646 * k - 0.0016528 * T2 - 0.00000239 * T3; // Moon's argument of latitude
        C1 = (0.1734 - 0.000393 * T) * Math.sin(M * dr) + 0.0021 * Math.sin(2 * dr * M);
        C1 = C1 - 0.4068 * Math.sin(Mpr * dr) + 0.0161 * Math.sin(dr * 2 * Mpr);
        C1 = C1 - 0.0004 * Math.sin(dr * 3 * Mpr);
        C1 = C1 + 0.0104 * Math.sin(dr * 2 * F) - 0.0051 * Math.sin(dr * (M + Mpr));
        C1 = C1 - 0.0074 * Math.sin(dr * (M - Mpr)) + 0.0004 * Math.sin(dr * (2 * F + M));
        C1 = C1 - 0.0004 * Math.sin(dr * (2 * F - M)) - 0.0006 * Math.sin(dr * (2 * F + Mpr));
        C1 = C1 + 0.0010 * Math.sin(dr * (2 * F - Mpr)) + 0.0005 * Math.sin(dr * (2 * Mpr + M));
        if (T < -11) {
            delta = 0.001 + 0.000839 * T + 0.0002261 * T2 - 0.00000845 * T3 - 0.000000081 * T * T3;
        } else {
            delta = -0.000278 + 0.000265 * T + 0.000262 * T2;
        }
        JdNew = Jd1 + C1 - delta;
        return JdNew;
    }

    /**
     * Compute the longitude of the sun at any time.
     * Algorithm from: "Astronomical Algorithms" by Jean Meeus, 1998
     *
     * @param jdn floating number jdn, the number of days since 1/1/4713 BC noon
     * @return the longitude of the sun
     */
    private double SunLongitude(double jdn) {
        double T, T2, dr, M, L0, DL, L;
        T = (jdn - 2451545.0) / 36525; // Time in Julian centuries from 2000-01-01 12:00:00 GMT
        T2 = T * T;
        dr = PI / 180; // degree to radian
        M = 357.52910 + 35999.05030 * T - 0.0001559 * T2 - 0.00000048 * T * T2; // mean anomaly, degree
        L0 = 280.46645 + 36000.76983 * T + 0.0003032 * T2; // mean longitude, degree
        DL = (1.914600 - 0.004817 * T - 0.000014 * T2) * Math.sin(dr * M);
        DL = DL + (0.019993 - 0.000101 * T) * Math.sin(dr * 2 * M) + 0.000290 * Math.sin(dr * 3 * M);
        L = L0 + DL; // true longitude, degree
        L = L * dr;
        L = L - PI * 2 * (INT(L / (PI * 2))); // Normalize to (0, 2*PI)
        return L;
    }

    /**
     * Compute sun position at midnight of the day with the given Julian day number.
     * The time zone if the time difference between local time and UTC: 7.0 for UTC+7:00.
     * The function returns a number between 0 and 11.
     * From the day after March equinox and the 1st major term after March equinox, 0 is returned.
     * After that, return 1, 2, 3 ...
     *
     * @param jdn      julian day number
     * @param timeZone time zone
     * @return sun longitude
     */
    private int getSunLongitude(int jdn, float timeZone) {
        return INT(SunLongitude(jdn - 0.5 - timeZone / 24d) / PI * 6);
    }

    /**
     * Compute the day of the k-th new moon in the given time zone.
     * The time zone if the time difference between local time and UTC: 7.0 for UTC+7:00
     *
     * @param k        the k-th new moon
     * @param timeZone timezone
     */
    private int getNewMoonDay(int k, float timeZone) {
        return INT(NewMoon(k) + 0.5 + timeZone / 24f);
    }

    /**
     * Find the day that starts the lunar month 11 of the given year for the given time zone
     *
     * @param yy       year
     * @param timeZone timezone
     * @return lunar month
     */
    private int getLunarMonth11(int yy, float timeZone) {
        int k, off, nm, sunLong;
        //off = jdFromDate(31, 12, yy) - 2415021.076998695;
        off = jdFromDate(31, 12, yy) - 2415021;
        k = INT(off / 29.530588853);
        nm = getNewMoonDay(k, timeZone);
        sunLong = getSunLongitude(nm, timeZone); // sun longitude at local midnight
        if (sunLong >= 9) {
            nm = getNewMoonDay(k - 1, timeZone);
        }
        return nm;
    }

    /**
     * Find the index of the leap month after the month starting on the day a11.
     *
     * @param a11      day
     * @param timeZone timezone
     * @return leap month offset
     */
    private int getLeapMonthOffset(int a11, float timeZone) {
        int k, arc, i, last;
        k = INT((a11 - 2415021.076998695) / 29.530588853 + 0.5);
        i = 1; // We start with the month following lunar month 11
        arc = getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone);
        do {
            last = arc;
            i++;
            arc = getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone);
        } while (arc != last && i < 14);
        return i - 1;
    }

    /**
     * Convert solar date dd/mm/yyyy to the corresponding lunar date
     *
     * @param yy       year
     * @param mm       month
     * @param dd       date
     * @param timeZone timezone
     * @return lunar date [yyyy, mm, dd, leap]
     */
    public int[] convertSolar2Lunar(int yy, int mm, int dd, float timeZone) {
        int k, dayNumber, monthStart, a11, b11, lunarDay, lunarMonth, lunarYear, lunarLeap;
        dayNumber = jdFromDate(dd, mm, yy);
        k = INT((dayNumber - 2415021.076998695) / 29.530588853);
        monthStart = getNewMoonDay(k + 1, timeZone);
        if (monthStart > dayNumber) {
            monthStart = getNewMoonDay(k, timeZone);
        }
        //alert(dayNumber+" -> "+monthStart);
        a11 = getLunarMonth11(yy, timeZone);
        b11 = a11;
        if (a11 >= monthStart) {
            lunarYear = yy;
            a11 = getLunarMonth11(yy - 1, timeZone);
        } else {
            lunarYear = yy + 1;
            b11 = getLunarMonth11(yy + 1, timeZone);
        }
        lunarDay = dayNumber - monthStart + 1;
        int diff = INT((monthStart - a11) / 29d);
        lunarLeap = 0;
        lunarMonth = diff + 11;
        if (b11 - a11 > 365) {
            int leapMonthDiff = getLeapMonthOffset(a11, timeZone);
            if (diff >= leapMonthDiff) {
                lunarMonth = diff + 10;
                if (diff == leapMonthDiff) {
                    lunarLeap = 1;
                }
            }
        }
        if (lunarMonth > 12) {
            lunarMonth = lunarMonth - 12;
        }
        if (lunarMonth >= 11 && diff < 4) {
            lunarYear -= 1;
        }
        return new int[]{lunarYear, lunarMonth, lunarDay, lunarLeap};
    }

    /**
     * Convert a lunar date to the corresponding solar date
     *
     * @param lunarYear  lunar year
     * @param lunarMonth lunar month
     * @param lunarDay   lunar day
     * @param lunarLeap  leap month or not
     * @param timeZone   timezone
     * @return solar date [yyyy, mm, dd]
     */
    public int[] convertLunar2Solar(int lunarYear, int lunarMonth, int lunarDay, boolean lunarLeap, float timeZone) {
        int k, a11, b11, off, leapOff, leapMonth, monthStart;
        if (lunarMonth < 11) {
            a11 = getLunarMonth11(lunarYear - 1, timeZone);
            b11 = getLunarMonth11(lunarYear, timeZone);
        } else {
            a11 = getLunarMonth11(lunarYear, timeZone);
            b11 = getLunarMonth11(lunarYear + 1, timeZone);
        }
        k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853);
        off = lunarMonth - 11;
        if (off < 0) {
            off += 12;
        }
        if (b11 - a11 > 365) {
            leapOff = getLeapMonthOffset(a11, timeZone);
            leapMonth = leapOff - 2;
            if (leapMonth < 0) {
                leapMonth += 12;
            }
            if (lunarLeap && lunarMonth != leapMonth) {
                return new int[]{0, 0, 0};
            } else if (lunarLeap || off >= leapOff) {
                off += 1;
            }
        }
        monthStart = getNewMoonDay(k + off, timeZone);
        return jdToDate(monthStart + lunarDay - 1);
    }

    /**
     * Get date info
     *
     * @param solarCal solar calendar
     * @return [year, month, month day, leap, week day, hour, minute, second]
     */
    public int[] getDateInfo(@NonNull Calendar solarCal) {
        int[] result = new int[8];
        float timezone = CalendarUtil.getTimeZone(this, solarCal.getTime());
        int[] lunarDate = convertSolar2Lunar(
                solarCal.get(Calendar.YEAR), solarCal.get(Calendar.MONTH) + 1, solarCal.get(Calendar.DAY_OF_MONTH),
                timezone);
        result[0] = lunarDate[0]; // year
        result[1] = lunarDate[1] - 1; // month
        result[2] = lunarDate[2]; // day
        result[3] = lunarDate[3]; // leap
        result[4] = solarCal.get(Calendar.DAY_OF_WEEK);
        result[5] = solarCal.get(Calendar.HOUR_OF_DAY);
        result[6] = solarCal.get(Calendar.MINUTE);
        result[7] = solarCal.get(Calendar.SECOND);
        return result;
    }
}
