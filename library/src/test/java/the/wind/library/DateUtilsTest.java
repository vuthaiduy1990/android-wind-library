package the.wind.library;

import android.icu.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import the.wind.library.utils.CWDateUtils;

public class DateUtilsTest {

    @Test
    public void stringToUTCDate() {
        Calendar cal = Calendar.getInstance();

        // date with time
        Date date = CWDateUtils.stringToUTCDate("2019-08-15T15:54:22.928Z", CWDateUtils.DATE_TIME_FORMATTER);
        cal.setTime(date);
        Assert.assertEquals(1565884462928L, cal.getTimeInMillis());

        // date only
        date = CWDateUtils.stringToUTCDate("2019-08-15", CWDateUtils.DATE_FORMATTER);
        cal.setTime(date);
        Assert.assertEquals(1565827200000L, cal.getTimeInMillis());
    }

    @Test
    public void dateToUTCString() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(1565884462928L);
        Date date = cal.getTime();

        Assert.assertEquals("2019-08-15T15:54:22.928Z", CWDateUtils.dateToUTCString(date, CWDateUtils.DATE_TIME_FORMATTER));
        Assert.assertEquals("2019-08-15", CWDateUtils.dateToUTCString(date, CWDateUtils.DATE_FORMATTER));
    }
}
