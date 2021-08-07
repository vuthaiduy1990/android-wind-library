package the.wind.library;

import android.content.Context;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Iterator;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import the.wind.library.calendar.CalendarUtil;
import the.wind.library.calendar.DateInfo;
import the.wind.library.calendar.MonthInfo;
import the.wind.library.calendar.VietnameseCalendar;
import the.wind.library.calendar.WeekStartsOn;

@RunWith(AndroidJUnit4.class)
public class CalendarUtilTest {

    private static final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Test
    public void getMonthDays() {
        Calendar cal = new GregorianCalendar();
        cal.set(2021, Calendar.JANUARY, 1);

        int[] monthDays = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        Iterator<DateInfo> dateIt;
        for (int i = 0; i < monthDays.length; i++) {
            cal.set(Calendar.MONTH, i);
            Collection<DateInfo> dates = CalendarUtil.getMonthDays(cal, null, cal.getTime(), WeekStartsOn.MONDAY);
            Assert.assertEquals(42, dates.size());
            dateIt = dates.iterator();
            while (dateIt.hasNext()) {
                if (dateIt.next() == null) {
                    dateIt.remove();
                }
            }
            Assert.assertEquals(monthDays[i], dates.size());
        }
    }

    @Test
    public void createMonthLink() {
        Calendar cal = new GregorianCalendar();
        cal.set(2021, Calendar.JANUARY, 1);
        int year = cal.get(Calendar.YEAR);
        while (cal.get(Calendar.YEAR) == year) {
            MonthInfo selected = CalendarUtil.createMonthLink(cal, null, cal.getTime(), WeekStartsOn.MONDAY, 2);
            Assert.assertEquals(year, selected.getYear());
            Assert.assertEquals(cal.get(Calendar.MONTH), selected.getMonth());

            MonthInfo monthIt = selected;
            MonthInfo next = null;
            int count = 0;
            while ((next = monthIt.next()) != null) {
                Assert.assertEquals(monthIt.getYear(), next.previous().getYear());
                Assert.assertEquals(monthIt.getMonth(), next.previous().getMonth());
                Assert.assertTrue(next.getYear() >= monthIt.getYear());
                if (next.getYear() > monthIt.getYear()) {
                    Assert.assertTrue(next.getMonth() < monthIt.getMonth());
                } else {
                    Assert.assertTrue(next.getMonth() > monthIt.getMonth());
                }
                monthIt = next;
                count++;
            }
            Assert.assertEquals(2, count);

            monthIt = selected;
            MonthInfo pre = null;
            count = 0;
            while ((pre = monthIt.previous()) != null) {
                Assert.assertEquals(monthIt.getYear(), pre.next().getYear());
                Assert.assertEquals(monthIt.getMonth(), pre.next().getMonth());
                Assert.assertTrue(pre.getYear() <= monthIt.getYear());
                if (pre.getYear() < monthIt.getYear()) {
                    Assert.assertTrue(pre.getMonth() > monthIt.getMonth());
                } else {
                    Assert.assertTrue(pre.getMonth() < monthIt.getMonth());
                }
                monthIt = pre;
                count++;
            }
            Assert.assertEquals(2, count);
            cal.add(Calendar.MONTH, 1);
        }
    }

    @Test
    public void testVietnameseCalendar() {
        VietnameseCalendar cal = new VietnameseCalendar();

        // Testcase: convert from solar to lunar date
        {
            int[] lunarDate = cal.convertSolar2Lunar(2021, 1, 1, 7);
            Assert.assertEquals(2020, lunarDate[0]);
            Assert.assertEquals(11, lunarDate[1]);
            Assert.assertEquals(19, lunarDate[2]);
            Assert.assertEquals(0, lunarDate[3]);
        }

        // Testcase: convert from solar to lunar date in leap month
        {
            int[] lunarDate = cal.convertSolar2Lunar(2020, 4, 23, 7);
            Assert.assertEquals(2020, lunarDate[0]);
            Assert.assertEquals(4, lunarDate[1]);
            Assert.assertEquals(1, lunarDate[2]);
            Assert.assertEquals(0, lunarDate[3]);

            // leap month
            lunarDate = cal.convertSolar2Lunar(2020, 5, 23, 7);
            Assert.assertEquals(2020, lunarDate[0]);
            Assert.assertEquals(4, lunarDate[1]);
            Assert.assertEquals(1, lunarDate[2]);
            Assert.assertEquals(1, lunarDate[3]);
        }

        // Testcase: convert from lunar to solar date
        {
            int[] lunarDate = cal.convertLunar2Solar(2020, 11, 19, true, 7);
            Assert.assertEquals(2021, lunarDate[0]);
            Assert.assertEquals(1, lunarDate[1]);
            Assert.assertEquals(1, lunarDate[2]);
        }

        // Testcase: convert from lunar to solar date with leap year
        {
            int[] lunarDate = cal.convertLunar2Solar(2020, 4, 1, false, 7);
            Assert.assertEquals(2020, lunarDate[0]);
            Assert.assertEquals(4, lunarDate[1]);
            Assert.assertEquals(23, lunarDate[2]);

            lunarDate = cal.convertLunar2Solar(2020, 4, 1, true, 7);
            Assert.assertEquals(2020, lunarDate[0]);
            Assert.assertEquals(5, lunarDate[1]);
            Assert.assertEquals(23, lunarDate[2]);
        }

    }
}
