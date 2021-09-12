package the.wind.library.dialog;

import android.content.Context;
import android.widget.EditText;

import java.util.Collection;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import the.wind.library.CWCallback;
import the.wind.library.R;
import the.wind.library.WindFactory;
import the.wind.library.adapter.SelectionListAdapter;
import the.wind.library.model.LocaleWrapper;
import the.wind.library.nlp.CWNLPEngine;
import the.wind.library.nlp.NLPMatchResult;
import the.wind.library.utils.CWAndroidUtils;
import the.wind.library.view.SearchBox;

/**
 * Locale list dialog
 */
public abstract class LocaleDialog extends SelectionListDialog<LocaleWrapper> {

    // List of locales
    private Collection<LocaleWrapper> locales;

    // search engine
    private CWNLPEngine<LocaleWrapper> searchEngine;

    // On search result handler
    private final CWCallback<NLPMatchResult<LocaleWrapper>> OnSearchResultHandler = new CWCallback<NLPMatchResult<LocaleWrapper>>() {

        @Override
        public void onBegin() {
            super.onBegin();
            getAdapter().clearData();
        }

        @Override
        public NLPMatchResult<LocaleWrapper> onSuccess(NLPMatchResult<LocaleWrapper> result) {
            if (result != null && result.isFullMatched()) {
                getAdapter().addData(result.target);
            }
            return super.onSuccess(result);
        }
    };


    /**
     * Constructor
     *
     * @param context application context
     */
    public LocaleDialog(@NonNull Context context) {
        super(context, new LinkedList<>());
        setLocales(WindFactory.instance().getAvailableLocales());
        setTitle(R.string.wl_locale);
        setHeight((int) (CWAndroidUtils.getScreenSize(context).getHeight() * 0.8));

        // Configure search box
        setSearchBoxVisible(true);
        setOnSearchBoxListener(new SearchBox.OnSearchListener() {
            @Override
            public int onSearch(EditText view, String oldInput, String newInput) {
                SelectionListAdapter<LocaleWrapper> adapter = getAdapter();
                if (oldInput.equals(newInput)) return adapter.getItemCount();
                if (newInput.isEmpty()) {
                    adapter.setData(new LinkedList<>(getLocales()));
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
    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    protected boolean equal(@NonNull LocaleWrapper a, @NonNull LocaleWrapper b) {
        return a.getCode().equals(b.getCode());
    }

    @Override
    protected String itemText(@NonNull LocaleWrapper itemData) {
        return itemData.getDisplayText();
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return list of locales
     */
    public Collection<LocaleWrapper> getLocales() {
        return locales;
    }

    /**
     * Set locales
     *
     * @param locales collection of locales
     */
    public void setLocales(Collection<LocaleWrapper> locales) {
        this.locales = locales;
        getAdapter().setData(new LinkedList<>(locales));
    }

    /**
     * @return build search engine
     */
    private CWNLPEngine<LocaleWrapper> searchEngine() {
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
            searchEngine.load(locales);
            searchEngine.build();
        }
        return searchEngine;
    }

    /**
     * Set search engine
     *
     * @param engine locales search engine
     */
    public void setSearchEngine(CWNLPEngine<LocaleWrapper> engine) {
        this.searchEngine = engine;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

}
