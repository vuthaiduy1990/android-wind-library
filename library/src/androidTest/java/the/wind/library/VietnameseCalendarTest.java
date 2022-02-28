package the.wind.library;

import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import the.wind.library.calendar.VietnameseCalendar;

@RunWith(AndroidJUnit4.class)
public class VietnameseCalendarTest {

    @Test
    public void getYear() {
        VietnameseCalendar cal = new VietnameseCalendar();
        Calendar solarCal = new GregorianCalendar();

        {
            solarCal.set(2021, Calendar.FEBRUARY, 11);
            cal.setTime(solarCal.getTime());
            Assert.assertEquals(2020, cal.getYear(solarCal));
        }
        {
            solarCal.set(2021, Calendar.FEBRUARY, 12);
            cal.setTime(solarCal.getTime());
            Assert.assertEquals(2021, cal.getYear(solarCal));
        }
        {
            solarCal.set(2021, Calendar.FEBRUARY, 13);
            cal.setTime(solarCal.getTime());
            Assert.assertEquals(2021, cal.getYear(solarCal));
        }
    }
}
