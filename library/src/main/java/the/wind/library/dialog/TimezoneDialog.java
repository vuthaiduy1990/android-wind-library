package the.wind.library.dialog;

import android.content.Context;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import the.wind.library.CWCallback;
import the.wind.library.R;
import the.wind.library.WindFactory;
import the.wind.library.adapter.SelectionListAdapter;
import the.wind.library.model.TimezoneWrapper;
import the.wind.library.nlp.CWNLPEngine;
import the.wind.library.nlp.NLPMatchResult;
import the.wind.library.utils.CWAndroidUtils;
import the.wind.library.view.SearchBox;

/**
 * Timezone dialog
 */
public class TimezoneDialog extends SelectionListDialog<TimezoneWrapper> {

    // Layout inflater
    private final LayoutInflater inflater;

    // List of locales
    private Collection<TimezoneWrapper> timezones;

    // search engine
    private CWNLPEngine<TimezoneWrapper> searchEngine;

    // Calendar and date format
    private final Calendar solarCal = new GregorianCalendar();
    private final DateFormat dateFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);

    // On search result handler
    private final CWCallback<NLPMatchResult<TimezoneWrapper>> OnSearchResultHandler = new CWCallback<NLPMatchResult<TimezoneWrapper>>() {

        @Override
        public void onBegin() {
            super.onBegin();
            getAdapter().clearData();
        }

        @Override
        public NLPMatchResult<TimezoneWrapper> onSuccess(NLPMatchResult<TimezoneWrapper> result) {
            if (result != null && result.isFullMatched()) {
                getAdapter().addData(result.target);
            }
            return super.onSuccess(result);
        }
    };

    // Custom view holder generator
    private final SelectionListAdapter.ViewHolderGenerator<TimezoneWrapper> vhGenerator = new SelectionListAdapter.ViewHolderGenerator<TimezoneWrapper>() {
        @Override
        public SelectionListAdapter.ViewHolder<TimezoneWrapper> newViewHolder(ViewGroup parent) {
            View view = inflater.inflate(R.layout.wl_dialog_timezone_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.dateFormatter = dateFormatter;
            viewHolder.solarCal = solarCal;
            return viewHolder;
        }
    };

    /**
     * Constructor
     *
     * @param context application context
     * @param mode    selection mode
     */
    public TimezoneDialog(@NonNull Context context, SelectionMode mode) {
        super(context, mode, new ArrayList<>());
        inflater = LayoutInflater.from(context);
        setTimezones(WindFactory.instance().getAvailableTimezones());
        setTitle(R.string.wl_timezone);
        setHeight((int) (CWAndroidUtils.getScreenSize(context).getHeight() * 0.8));
        setCustomViewHolderGenerator(vhGenerator);

        // Configure search box
        setSearchBoxVisible(true);
        setOnSearchBoxListener(new SearchBox.OnSearchListener() {
            @Override
            public int onSearch(EditText view, String oldInput, String newInput) {
                SelectionListAdapter<TimezoneWrapper> adapter = getAdapter();
                if (oldInput.equals(newInput)) return adapter.getItemCount();
                if (newInput.isEmpty()) {
                    adapter.setData(new LinkedList<>(getTimezones()));
                    adapter.notifyDataSetChanged();
                    return 0;
                }

                // do searching
                searchEngine().doMatching(newInput, OnSearchResultHandler);
                return adapter.getItemCount();
            }
        });
        setOnSearchToggleListener(new SearchBox.OnToggleListener() {
            @Override
            public void onToggle(boolean compactMode) {
                // pre-init search engine when user open search mode
                if (!compactMode) {
                    searchEngine();
                }
            }
        });
    }

    /**
     * Constructor
     *
     * @param context application context
     */
    public TimezoneDialog(@NonNull Context context) {
        this(context, SelectionMode.SINGLE);
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    protected String itemId(@NonNull TimezoneWrapper itemData) {
        return itemData.getCode();
    }

    @Override
    protected String itemText(@NonNull TimezoneWrapper itemData) {
        return itemData.getName();
    }

    @Override
    public void show(@Nullable TimezoneWrapper item) {
        solarCal.setTime(new Date());
        super.show(item);
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return list of timezones
     */
    public Collection<TimezoneWrapper> getTimezones() {
        return timezones;
    }

    /**
     * Set timezones
     *
     * @param timezones collection of timezones
     */
    public void setTimezones(Collection<TimezoneWrapper> timezones) {
        this.timezones = timezones;
        getAdapter().setData(new LinkedList<>(timezones));
    }

    /**
     * @return build search engine
     */
    private CWNLPEngine<TimezoneWrapper> searchEngine() {
        if (searchEngine == null) {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.strip = true;
            opts.useSpecialChars = true;
            opts.caseSensitive = false;
            opts.matchOnly = true;
            opts.cache = 0; // small data -> no need to cache
            opts.greedy = false;
            searchEngine = new CWNLPEngine<>(null, opts);

            // load data
            searchEngine.load(timezones);
            searchEngine.build();
        }
        return searchEngine;
    }

    /**
     * Set search engine
     *
     * @param engine timezones search engine
     */
    public void setSearchEngine(CWNLPEngine<TimezoneWrapper> engine) {
        this.searchEngine = engine;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Custom view holder
     */
    private static class ViewHolder extends SelectionListAdapter.DefaultViewHolder<TimezoneWrapper> {

        private final TextView _offsetView;
        private final TextView _locationView;
        private Calendar solarCal;
        private DateFormat dateFormatter;

        /**
         * Constructor
         *
         * @param itemView item view
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _offsetView = itemView.findViewById(R.id._offsetView);
            _locationView = itemView.findViewById(R.id._locationView);
        }

        @Override
        protected void bindData(TimezoneWrapper data) {
            super.bindData(data);
            _locationView.setText(data.getLocation());
            _offsetView.setText(data.getOffset(solarCal, dateFormatter));
        }
    }
}
