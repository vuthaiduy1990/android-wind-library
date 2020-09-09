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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import the.wind.library.CWBundle;
import the.wind.library.R;
import the.wind.library.utils.CWAndroidUtils;

public class SearchBox extends RelativeLayout {

    private static final long LAZY_TIME = 600;

    // views
    private final ImageView _icCloseSearch, _icCompactSearch;
    private final ViewGroup _inputBox;
    private final EditText _ipSearch;
    private final ImageView _icSearchBtn, _icClearSearch;
    private final View _closeIconSpace;

    // data model
    private boolean mCloseVisible = true;
    private String mOldSearchInput = "";
    private String mNewSearchInput = "";
    private CWBundle mBundle = new CWBundle();
    private long mLazyTime = LAZY_TIME;
    private boolean isProcessed = true;

    // listener
    private OnActionListener mActionListener;

    // on lazy input
    private Runnable OnLazyInput = new Runnable() {
        @Override
        public void run() {
            if (!isProcessed) /* has not processed new input */ {
                isProcessed = true;
                handleSearch(_ipSearch.getText().toString());
            }
        }
    };

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
        _icCloseSearch = findViewById(R.id._icCloseSearch);
        _closeIconSpace = findViewById(R.id._closeIconSpace);
        _icCompactSearch = findViewById(R.id._icCompactSearch);
        _inputBox = findViewById(R.id._inputBox);
        _ipSearch = _inputBox.findViewById(R.id._ipSearch);
        _icSearchBtn = _inputBox.findViewById(R.id._icSearchBtn);
        _icClearSearch = _inputBox.findViewById(R.id._icClearSearch);

        // bind attributes
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.SearchBox, defStyleAttr, defStyleRes);
        try {

            // text size
            float textSize = typeArray.getDimension(
                    R.styleable.SearchBox_textSize,
                    getResources().getDimension(R.dimen.wl_text_big));
            _ipSearch.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            // compact mode
            mCloseVisible = typeArray.getBoolean(R.styleable.SearchBox_closeVisible, true);
            boolean compactMode = typeArray.getBoolean(R.styleable.SearchBox_compactMode, false);
            setCompactMode(compactMode);

            // background
            setInputBackground(getBackground());
            setBackground(null);

            // set padding
            int top = getPaddingTop();
            top = top > 0 ? top : getResources().getDimensionPixelOffset(R.dimen.wl_search_box_padding_ver);
            int bottom = getPaddingBottom();
            bottom = bottom > 0 ? bottom : getResources().getDimensionPixelOffset(R.dimen.wl_search_box_padding_ver);
            setInputPadding(getPaddingLeft(), top, getPaddingRight(), bottom);
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
                // show/hide the clear icon
                if (s.length() > 0) {
                    _icClearSearch.setVisibility(View.VISIBLE);
                } else {
                    _icClearSearch.setVisibility(View.GONE);
                }

                // trigger search event
                isProcessed = false;
                _icCompactSearch.postDelayed(OnLazyInput, mLazyTime);
            }
        });
        _ipSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    handleSearch(v.getText().toString());
                    CWAndroidUtils.hideSoftKeyboard(_ipSearch);
                }
                return false;
            }
        });
        _icSearchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // click on search button inside the search box
                CWAndroidUtils.hideSoftKeyboard(_ipSearch);
                handleSearch(_ipSearch.getText().toString());
            }
        });
        _icClearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _ipSearch.setText(""); // this will notify a text change
                _icClearSearch.setVisibility(View.GONE);
            }
        });
        _icCompactSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCompactMode(false);
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

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return bundle data
     */
    public CWBundle bundle() {
        return mBundle;
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
     * Set input background
     *
     * @param background background
     */
    public void setInputBackground(Drawable background) {
        if (_inputBox != null) {
            _inputBox.setBackground(background);
        }
    }

    /**
     * Set background resource id
     *
     * @param resId resource id
     */
    public void setInputBackground(@DrawableRes int resId) {
        if (_inputBox != null) {
            _inputBox.setBackgroundResource(resId);
        }
    }

    /**
     * Set input padding
     *
     * @param left   padding left
     * @param top    padding top
     * @param right  padding right
     * @param bottom padding top
     */
    public void setInputPadding(int left, int top, int right, int bottom) {
        if (_inputBox != null) {
            _inputBox.setPadding(left, top, right, bottom);
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
            _closeIconSpace.setVisibility(GONE);
            _inputBox.setVisibility(GONE);
            _icCompactSearch.setVisibility(VISIBLE);
        } else {
            // show full search mode
            _icCloseSearch.setVisibility(mCloseVisible ? VISIBLE : GONE);
            _closeIconSpace.setVisibility(mCloseVisible ? VISIBLE : GONE);
            _inputBox.setVisibility(VISIBLE);
            _icCompactSearch.setVisibility(GONE);
        }
        if (mActionListener != null) mActionListener.onToggle(compactMode);
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
     * @return previous search input
     */
    public String getOldSearchInput() {
        return mOldSearchInput;
    }

    /**
     * @return current search input
     */
    public String getNewSearchInput() {
        return mNewSearchInput;
    }

    /**
     * Set lazy time.
     * If user input the series of words into search box quickly, it will not trigger event for each word.
     * but trigger event for the final string when reaching the lazy time
     *
     * @param milliseconds unit is milliseconds
     */
    public void setLazyTime(long milliseconds) {
        mLazyTime = milliseconds;
    }

    /**
     * Set action listener
     *
     * @param listener action listener
     */
    public void setOnActionListener(OnActionListener listener) {
        mActionListener = listener;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Close search mode
     */
    public void closeSearch() {
        setCompactMode(true);
        _ipSearch.setText(""); // this will notify a text change
    }

    /**
     * Handle searching
     *
     * @param searchInput search input
     */
    private void handleSearch(@NonNull String searchInput) {
        mOldSearchInput = mNewSearchInput;
        mNewSearchInput = searchInput.trim();
        if (mActionListener != null) mActionListener.onSearch(_ipSearch, mOldSearchInput, mNewSearchInput);
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * On action listener
     */
    public interface OnActionListener {

        /**
         * Trigger when user click on search icon or input
         *
         * @param view     search input view
         * @param oldInput old search input value
         * @param newInput new search input value
         */
        void onSearch(EditText view, String oldInput, String newInput);

        /**
         * On toggle search mode
         *
         * @param compactMode true -> show search icon only, false -> show full search
         */
        void onToggle(boolean compactMode);
    }
}
