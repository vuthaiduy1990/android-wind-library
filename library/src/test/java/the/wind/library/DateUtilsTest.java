package the.wind.library;


import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import the.wind.library.utils.CWDateUtils;

public class DateUtilsTest {

    @Test
    public void stringToDate() {
        // date with time
        {
            String dateStr = "2019-08-15T15:54:22.928Z";
            Date date = CWDateUtils.stringToUtcDate(dateStr, CWDateUtils.DATE_TIME_FORMATTER);
            assert date != null;
            Assert.assertEquals(dateStr, CWDateUtils.dateToString(date, CWDateUtils.DATE_TIME_FORMATTER));
        }
        {
            String dateStr = "2019-08-15";
            Date date = CWDateUtils.stringToUtcDate(dateStr, CWDateUtils.DATE_FORMATTER);
            assert date != null;
            Assert.assertEquals(dateStr, CWDateUtils.dateToString(date, CWDateUtils.DATE_FORMATTER));
        }
    }
}
