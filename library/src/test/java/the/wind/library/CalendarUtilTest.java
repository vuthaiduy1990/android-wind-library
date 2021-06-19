package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import the.wind.library.calendar.CalendarUtil;
import the.wind.library.calendar.DateInfo;
import the.wind.library.calendar.MonthInfo;

public class CalendarUtilTest {

    @Test
    public void getMonthDays() {
        Calendar cal = Calendar.getInstance();
        cal.set(2021, Calendar.JANUARY, 1);


        int[] monthDays = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        Iterator<DateInfo> dateIt;
        for (int i = 0; i < monthDays.length; i++) {
            cal.set(Calendar.MONTH, i);
            Collection<DateInfo> dates = CalendarUtil.getMonthDays(cal, cal.getTime());
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
        Calendar cal = Calendar.getInstance();
        cal.set(2021, Calendar.JANUARY, 1);
        int year = cal.get(Calendar.YEAR);
        while (cal.get(Calendar.YEAR) == year) {
            MonthInfo selected = CalendarUtil.createMonthLink(cal, cal.getTime(), 2);
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
}
