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
import the.wind.library.model.CurrencyWrapper;
import the.wind.library.nlp.CWNLPEngine;
import the.wind.library.nlp.NLPMatchResult;
import the.wind.library.utils.CWAndroidUtils;
import the.wind.library.view.SearchBox;

/**
 * Currency dialog
 */
public abstract class CurrencyDialog extends SelectionListDialog<CurrencyWrapper> {

    // List of currencies
    private Collection<CurrencyWrapper> currencies;

    // search engine
    private CWNLPEngine<CurrencyWrapper> searchEngine;

    // On search result handler
    private final CWCallback<NLPMatchResult<CurrencyWrapper>> OnSearchResultHandler = new CWCallback<NLPMatchResult<CurrencyWrapper>>() {

        @Override
        public void onBegin() {
            super.onBegin();
            getAdapter().clearData();
        }

        @Override
        public NLPMatchResult<CurrencyWrapper> onSuccess(NLPMatchResult<CurrencyWrapper> result) {
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
    public CurrencyDialog(@NonNull Context context) {
        super(context, new LinkedList<>());
        setCurrencies(WindFactory.instance().getAvailableCurrencies());
        setTitle(R.string.wl_currency);
        setHeight((int) (CWAndroidUtils.getScreenSize(context).getHeight() * 0.8));

        // Configure search box
        setSearchBoxVisible(true);
        setOnSearchBoxListener(new SearchBox.OnSearchListener() {
            @Override
            public int onSearch(EditText view, String oldInput, String newInput) {
                SelectionListAdapter<CurrencyWrapper> adapter = getAdapter();
                if (oldInput.equals(newInput)) return adapter.getItemCount();
                if (newInput.isEmpty()) {
                    adapter.setData(new LinkedList<>(getCurrencies()));
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
    protected boolean equal(@NonNull CurrencyWrapper a, @NonNull CurrencyWrapper b) {
        return a.getCode().equals(b.getCode());
    }

    @Override
    protected String itemText(@NonNull CurrencyWrapper itemData) {
        return itemData.getDisplayText();
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return list of currencies
     */
    public Collection<CurrencyWrapper> getCurrencies() {
        return currencies;
    }

    /**
     * Set currencies
     *
     * @param currencies collection of currencies
     */
    public void setCurrencies(Collection<CurrencyWrapper> currencies) {
        this.currencies = currencies;
        getAdapter().setData(new LinkedList<>(currencies));
    }

    /**
     * @return build search engine
     */
    private CWNLPEngine<CurrencyWrapper> searchEngine() {
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
            searchEngine.load(currencies);
            searchEngine.build();
        }
        return searchEngine;
    }

    /**
     * Set search engine
     *
     * @param engine currencies search engine
     */
    public void setSearchEngine(CWNLPEngine<CurrencyWrapper> engine) {
        this.searchEngine = engine;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

}
