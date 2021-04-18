package the.wind.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import the.wind.library.CWBundle;
import the.wind.library.R;
import the.wind.library.adapter.AutoCompleteAdapter;
import the.wind.library.nlp.CWNLPEngine;
import the.wind.library.nlp.INLPText;
import the.wind.library.nlp.NLPString;
import the.wind.library.utils.CWAndroidUtils;

public class SearchBox extends LinearLayout {

    private static final long LAZY_TIME = 200;

    // views
    private final LinearLayout _rootView;
    private final ImageView _icCloseSearch, _icCompactSearch;
    private final ViewGroup _inputBox;
    private final AutoCompleteTextView _ipSearch;
    private final TextView _searchResultCountView;
    private final ImageView _icSearchBtn, _icClearSearch;

    // styling
    private int gravity;
    private Drawable background;

    // data model
    private boolean resultCountVisible;
    private boolean closeVisible = true;
    private String oldSearchInput = "";
    private String inputText = "";
    private final CWBundle bundle = new CWBundle();
    private long lazyTime = LAZY_TIME;
    private long lastInputTime;
    private boolean closeKeyboardOnSearch;

    // listener
    private OnSearchListener searchListener;
    // on lazy input
    private final Runnable OnLazyInput = new Runnable() {
        @Override
        public void run() {
            // 5 is secure time to make sure the last input will be executed
            if (System.currentTimeMillis() - lastInputTime >= lazyTime - 5) {
                handleSearch();
            }
        }
    };
    private OnToggleListener toggleListener;
    private OnEnterListener enterListener;

    /**
     * Constructor
     *
     * @param context application context
     */
    public SearchBox(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context application context
     * @param attrs   collection of attributes
     */
    public SearchBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context      application context
     * @param attrs        collection of attributes
     * @param defStyleAttr style attribute
     */
    public SearchBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    public SearchBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.wl_search_box, this);

        // bind vies
        _rootView = findViewById(R.id._rootView);
        _icCloseSearch = findViewById(R.id._icCloseSearch);
        _icCompactSearch = findViewById(R.id._icCompactSearch);
        _inputBox = findViewById(R.id._inputBox);
        _ipSearch = _inputBox.findViewById(R.id._ipSearch);
        _ipSearch.setDropDownBackgroundResource(R.color.wl_white);
        _searchResultCountView = _inputBox.findViewById(R.id._searchResultCountView);
        _icSearchBtn = _inputBox.findViewById(R.id._icSearchBtn);
        _icClearSearch = _inputBox.findViewById(R.id._icClearSearch);

        // bind attributes
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.SearchBox, defStyleAttr, defStyleRes);
        try {

            // text
            String textValue = typeArray.getString(R.styleable.SearchBox_text);
            setText(textValue);
            float textSize = typeArray.getDimension(
                    R.styleable.SearchBox_textSize,
                    getResources().getDimension(R.dimen.wl_text_big));
            setTextSize(textSize);

            // hint
            String hint = typeArray.getString(R.styleable.SearchBox_hint);
            setHint(hint);

            // icon size
            float iconSize = typeArray.getDimension(
                    R.styleable.SearchBox_iconSize,
                    getResources().getDimension(R.dimen.wl_icon_small));
            setIconSize(iconSize);

            // search icon
            int searchIconRes = typeArray.getResourceId(R.styleable.SearchBox_searchIcon, R.drawable.wl_ic_search);
            setSearchIcon(searchIconRes);
            closeKeyboardOnSearch = typeArray.getBoolean(R.styleable.SearchBox_closeKeyboardOnSearch, true);

            // compact mode
            closeVisible = typeArray.getBoolean(R.styleable.SearchBox_closeVisible, true);
            boolean compactMode = typeArray.getBoolean(R.styleable.SearchBox_compactMode, false);
            setCompactMode(compactMode);

            // Show search count result
            resultCountVisible = typeArray.getBoolean(R.styleable.SearchBox_resultCountVisible, false);
            setResultCountVisible(resultCountVisible);

            // override gravity and background
            setGravity(this.gravity);
            setBackground(this.background);

            // set padding
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop() > 0 ? getPaddingTop() : (int) getResources().getDimension(R.dimen.wl_search_box_padding_ver);
            int paddingRight = getPaddingRight();
            int paddingBottom = getPaddingBottom() > 0 ? getPaddingBottom() : (int) getResources().getDimension(R.dimen.wl_search_box_padding_ver);
            _inputBox.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            setPadding(0, 0, 0, 0);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            typeArray.recycle();
        }

        // bind listener
        _ipSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                oldSearchInput = inputText;
                inputText = s.toString().trim();

                // show/hide the clear icon
                if (s.length() > 0) {
                    _icClearSearch.setVisibility(View.VISIBLE);
                    if (resultCountVisible) {
                        _searchResultCountView.setVisibility(VISIBLE);
                    }
                } else {
                    _icClearSearch.setVisibility(View.GONE);
                    _searchResultCountView.setVisibility(GONE);
                }

                // trigger search event
                lastInputTime = System.currentTimeMillis();
                _icCompactSearch.postDelayed(OnLazyInput, lazyTime);
            }
        });
        _ipSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (closeKeyboardOnSearch) {
                        CWAndroidUtils.hideSoftKeyboard(_ipSearch);
                    }
                    handleSearch();
                    if (enterListener != null) enterListener.onEnter(_ipSearch, oldSearchInput, inputText);
                }
                return true; // prevent keyboard from auto closing
            }
        });
        _icSearchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // click on search button inside the search box
                if (closeKeyboardOnSearch) {
                    CWAndroidUtils.hideSoftKeyboard(_ipSearch);
                }
                handleSearch();
                if (enterListener != null) enterListener.onEnter(_ipSearch, oldSearchInput, inputText);
            }
        });
        _icClearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _ipSearch.setText(""); // this will notify a text change
                _icClearSearch.setVisibility(View.GONE);
                _searchResultCountView.setVisibility(GONE);
            }
        });
        _icCompactSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCompactMode(false);
                _ipSearch.requestFocus();
                _ipSearch.post(new Runnable() {
                    @Override
                    public void run() {
                        CWAndroidUtils.showSoftKeyboard(_ipSearch);
                    }
                });
            }
        });
        _icCloseSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearch();
            }
        });

    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public void setGravity(int gravity) {
        this.gravity = gravity;
        if (_rootView != null) {
            _rootView.setGravity(gravity);
        }
    }

    @Override
    public void setBackground(Drawable background) {
        this.background = background;
        if (_inputBox != null) {
            _inputBox.setBackground(background);
        }
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return bundle data
     */
    public CWBundle bundle() {
        return bundle;
    }

    /**
     * Get input text view
     *
     * @return input text view
     */
    public EditText inputView() {
        return _ipSearch;
    }

    /**
     * @return search result count view
     */
    public TextView searchResultCountView() {
        return _searchResultCountView;
    }

    /**
     * Get compact search icon
     *
     * @return icon
     */
    public ImageView compactSearchIcon() {
        return _icCompactSearch;
    }

    /**
     * Get close search icon
     *
     * @return icon
     */
    public ImageView closeSearchIcon() {
        return _icClearSearch;
    }

    /**
     * Set text size.
     *
     * @param resId dimension resource id
     */
    public void setTextSize(@DimenRes int resId) {
        setTextSize(getResources().getDimension(resId));
    }

    /**
     * Set text size.
     *
     * @param textSize text size in pixel
     */
    public void setTextSize(float textSize) {
        _searchResultCountView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        _ipSearch.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    /**
     * Set icon size
     *
     * @param resId dimension resource id
     */
    public void setIconSize(@DimenRes int resId) {
        float size = getResources().getDimension(resId);
        setIconSize(size);
    }

    /**
     * Set icon size
     *
     * @param iconSize icon size in pixel
     */
    public void setIconSize(float iconSize) {
        if (_icSearchBtn != null) {
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) _icSearchBtn.getLayoutParams();
            param.width = (int) iconSize;
            param.height = (int) iconSize;
            _icSearchBtn.setLayoutParams(param);
        }
        if (_icClearSearch != null) {
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) _icClearSearch.getLayoutParams();
            param.width = (int) iconSize;
            param.height = (int) iconSize;
            _icClearSearch.setLayoutParams(param);
        }
    }

    /**
     * Set search icon
     *
     * @param resId drawable resource id
     */
    public void setSearchIcon(@DrawableRes int resId) {
        if (_icSearchBtn != null) {
            _icSearchBtn.setImageResource(resId);
        }
    }

    /**
     * Set compact mode
     *
     * @param compactMode true -> show search icon only, false -> show full search
     */
    public void setCompactMode(boolean compactMode) {
        if (compactMode) {
            // show compact mode (search icon only)
            _icCloseSearch.setVisibility(GONE);
            _inputBox.setVisibility(GONE);
            _icCompactSearch.setVisibility(VISIBLE);
        } else {
            // show full search mode
            _icCloseSearch.setVisibility(closeVisible ? VISIBLE : GONE);
            _inputBox.setVisibility(VISIBLE);
            _icCompactSearch.setVisibility(GONE);
        }
        if (toggleListener != null) toggleListener.onToggle(compactMode);
    }

    /**
     * Check if search box is in compact mode or not
     *
     * @return true if in compact mode (display search icon oly)
     */
    public boolean isCompactMode() {
        return _icCompactSearch.getVisibility() == VISIBLE;
    }

    /**
     * Set suggestions
     *
     * @param nlpEngine search engine
     */
    public <T extends INLPText> void setSuggestions(CWNLPEngine<T> nlpEngine) {
        AutoCompleteAdapter<T> adapter = new AutoCompleteAdapter<>(getContext(), nlpEngine);
        _ipSearch.setAdapter(adapter);
    }

    /**
     * Set suggestion text
     *
     * @param suggestions suggestion
     */
    public void setSuggestions(CharSequence... suggestions) {
        CWNLPEngine.Options opts = new CWNLPEngine.Options();
        opts.strip = true;
        opts.useSpecialChars = false;
        opts.caseSensitive = false;
        opts.matchOnly = true;
        opts.cache = 1; // use 1 caching for searching on previous result
        opts.greedy = false;
        CWNLPEngine<NLPString> nlpEngine = new CWNLPEngine<>(getContext(), opts);

        for (CharSequence val : suggestions) {
            nlpEngine.load(new NLPString(val));
        }
        nlpEngine.build();
        setSuggestions(nlpEngine);
    }

    /**
     * Set result count visible
     *
     * @param visible true if visible
     */
    public void setResultCountVisible(boolean visible) {
        resultCountVisible = visible;
        _searchResultCountView.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * @return previous search input
     */
    public String getOldSearchInput() {
        return oldSearchInput;
    }

    /**
     * @return current search input
     */
    public String getText() {
        return inputText;
    }

    /**
     * Set input text
     *
     * @param text input text
     */
    public void setText(@Nullable String text) {
        _ipSearch.setText(text != null ? text : "");
    }

    /**
     * Set input text
     *
     * @param resId text resource id
     */
    public void setText(@StringRes int resId) {
        _ipSearch.setText(resId);
    }

    /**
     * Set hint
     *
     * @param hint hint
     */
    public void setHint(@Nullable String hint) {
        _ipSearch.setHint(hint);
    }

    /**
     * Set hint
     *
     * @param resId text resource id
     */
    public void setHint(@StringRes int resId) {
        _ipSearch.setHint(resId);
    }

    /**
     * Set lazy time.
     * If user input the series of words into search box quickly, it will not trigger event for each word.
     * but trigger event for the final string when reaching the lazy time
     *
     * @param milliseconds unit is milliseconds
     */
    public void setLazyTime(long milliseconds) {
        lazyTime = milliseconds;
    }

    /**
     * Set close keyboard on search
     *
     * @param close if true, keyboard will be closed when user press search button
     */
    public void setCloseKeyboardOnSearch(boolean close) {
        this.closeKeyboardOnSearch = close;
    }

    /**
     * Set search listener
     *
     * @param listener listener
     */
    public void setOnSearchListener(OnSearchListener listener) {
        searchListener = listener;
    }

    /**
     * Set toggle listener
     *
     * @param listener toggle listener
     */
    public void setOnToggleListener(OnToggleListener listener) {
        toggleListener = listener;
    }


    /**
     * Set enter listener
     *
     * @param listener enter listener
     */
    public void setOnEnterListener(OnEnterListener listener) {
        enterListener = listener;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Close search mode
     */
    public void closeSearch() {
        setCompactMode(true);
        _ipSearch.setText(""); // this will notify a text change
        CWAndroidUtils.hideSoftKeyboard(_ipSearch);
    }

    /**
     * Handle searching
     */
    private void handleSearch() {
        if (searchListener != null) {
            int results = searchListener.onSearch(_ipSearch, oldSearchInput, inputText);
            _searchResultCountView.setText(String.format(Locale.getDefault(), "(%d)", results));
        }
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * On search listener
     */
    public interface OnSearchListener {
        /**
         * Trigger when user click on search icon or input
         *
         * @param view     search input view
         * @param oldInput old search input value
         * @param newInput new search input value
         * @return number of results
         */
        int onSearch(EditText view, String oldInput, String newInput);
    }

    /**
     * On toggle listener
     */
    public interface OnToggleListener {
        /**
         * On toggle search mode
         *
         * @param compactMode true -> show search icon only, false -> show full search
         */
        void onToggle(boolean compactMode);
    }

    /**
     * On search enter listener
     */
    public interface OnEnterListener {
        /**
         * Trigger when user press enter or search button
         *
         * @param view     search input view
         * @param oldInput old search input value
         * @param newInput new search input value
         */
        void onEnter(EditText view, String oldInput, String newInput);
    }
}
