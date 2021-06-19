package the.wind.library.calendar;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import the.wind.library.CWBundle;
import the.wind.library.R;

public class WindCalendar extends LinearLayout {

    // Views
    private final FragmentManager fragManager;
    private final ViewGroup _rootView;
    private final CalendarViewPager _calendarViewPager;

    // Adapter
    private final CalendarAdapter calendarAdapter;


    // bundle data
    private final CWBundle bundle = new CWBundle();

    /**
     * Constructor
     *
     * @param context application context
     */
    public WindCalendar(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context application context
     * @param attrs   collection of attributes
     */
    public WindCalendar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context      application context
     * @param attrs        collection of attributes
     * @param defStyleAttr style attribute
     */
    public WindCalendar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Constructor
     *
     * @param context      application context
     * @param attrs        collection of attributes
     * @param defStyleAttr style attribute
     * @param defStyleRes  style resource
     */
    public WindCalendar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.wl_calendar, this);
        if (context instanceof FragmentActivity) {
            fragManager = ((FragmentActivity) context).getSupportFragmentManager();
        } else {
            throw new ActivityNotFoundException("Context is not an fragment activity");
        }

        // Bind views
        _rootView = findViewById(R.id._rootView);
        _calendarViewPager = _rootView.findViewById(R.id._calendarViewPager);

        // Styling
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.Calendar, defStyleAttr, defStyleRes);
        try {

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            typeArray.recycle();
        }

        calendarAdapter = new CalendarAdapter(fragManager);
        _calendarViewPager.setAdapter(calendarAdapter);
        _calendarViewPager.setSelectedDate(new Date());
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return bundle data
     */
    public CWBundle bundle() {
        return bundle;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

}
