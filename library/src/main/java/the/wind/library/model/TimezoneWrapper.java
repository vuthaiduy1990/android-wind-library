package the.wind.library.model;

import android.content.Context;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;

import java.text.DateFormat;

import androidx.annotation.Nullable;
import the.wind.library.nlp.INLPText;

/**
 * Wrapper of timezone
 */
public class TimezoneWrapper implements INLPText {

    // locale instance
    private final TimeZone timezone;
    private final String code;
    private final String name;
    private final String location;
    private String offset;

    /**
     * Constructor
     *
     * @param timezone timezone
     */
    public TimezoneWrapper(TimeZone timezone) {
        this.timezone = timezone;
        this.code = timezone.getID();
        this.name = timezone.getDisplayName();
        this.location = timezone.getDisplayName(false, TimeZone.GENERIC_LOCATION);
        this.offset = timezone.getDisplayName(false, TimeZone.LONG_GMT);
        if (this.offset.equals("GMT")) {
            this.offset += "+0";
        }
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public String nlpTextId(@Nullable Context context) {
        return code;
    }

    @Override
    public String nlpRawText(@Nullable Context context) {
        return String.format("%s %s %s", getName(), getLocation(), offset);
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Get timezone instance
     *
     * @return timezone instance
     */
    public TimeZone get() {
        return timezone;
    }

    /**
     * Get unique code
     *
     * @return unique code
     */
    public String getCode() {
        return code;
    }

    /**
     * Get display text
     *
     * @return display text
     */
    public String getName() {
        return name;
    }

    /**
     * Get location
     *
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Get offset display
     *
     * @param solarCal solar calendar which hold the current date
     * @return offset display
     */
    public String getOffset(Calendar solarCal, DateFormat formatter) {
        solarCal.setTimeZone(timezone);
        return String.format("%s  %s", formatter.format(solarCal.getTime()), offset);
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */
}
