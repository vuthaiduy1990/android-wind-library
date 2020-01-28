package the.wind.library.nlp;

import android.content.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import the.wind.library.CWCallback;
import the.wind.library.CWRegex;
import the.wind.library.CWUnicode;
import the.wind.library.utils.CWStringUtils;

/**
 * Usage
 * <pre>
 *     // load text or object which implement {{@link INLPText}}
 *     CWNLPEngine<NlpString> engine = new CWNLPEngine<>(context);
 *     engine.loadText(new NlpString("color the wind"));
 *     engine.loadText(new NlpString("風を彩る。"));
 *
 *     // build
 *     engine.build();
 *
 *     // load more text then build again
 *     engine.loadText(new NlpString("Tô màu cho gió"));
 *     engine.build();
 *
 *     // or data is changed then rebuild the changed data only
 *     nlpText.changeData(data);
 *     engine.rebuild(nlpText);
 *
 *     // or rebuild all loaded data
 *     engine.rebuild();
 *
 *     // do matching
 *     List<NLPMatchResult<NlpString>> results = engine.doMatching("search-key");
 *     engine.doMatching("search-key", CWCallback);
 *     engine.doMatching("search-key", new CWCallback<NLPMatchResult<NlpString>>(){
 *          @Override
 *          public NLPMatchResult<NlpString> onSuccess(NLPMatchResult<NlpString> result) {
 *              // you can do something with the search result here
 *              return super.onSuccess(result);
 *          }
 *
 *          @Override
 *          public void onEnd() {
 *              super.onEnd();
 *          }
 *     });
 *
 *     // free
 *     engine.freeMemory();
 * </pre>
 */
public final class CWNLPEngine<T extends INLPText> {

    // Application context
    @Nullable
    private Context mContext;

    // Configuration for the engine
    private Options mOptions;

    // the input target for processing
    private List<T> mTargetList = new LinkedList<>();
    private Queue<T> mTargetQueue = new LinkedList<>();

    // map target id with processed text
    private Map<String, String> mTextMap = new HashMap<>();

    // Map between the search key with results
    private Map<String, List<NLPMatchResult<T>>> mCaches = new HashMap<>();
    // contains search keys which has been pre-processed.
    private Queue<String> mCacheSearchKeys = new LinkedList<>();

    /**
     * Construct engine with the default setting
     *
     * @param context application context. It can be null
     */
    public CWNLPEngine(@Nullable Context context) {
        mContext = context;
        mOptions = new Options();
    }

    /**
     * Construct engine with the given setting
     *
     * @param context application context. It can be null
     * @param options option for building text
     */
    public CWNLPEngine(@Nullable Context context, Options options) {
        this(context);
        mOptions = options;
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Note: you can't modify this list
     *
     * @return list of targets which implement {@link INLPText} and are loaded into engine
     */
    public List<T> targets() {
        return Collections.unmodifiableList(mTargetList);
    }

    /**
     * Remove a target
     *
     * @param target target which implement {@link INLPText}
     */
    public void remove(T target) {
        mTargetList.remove(target);
    }

    /**
     * Load targets into to engine
     * Loaded targets will be stored in queue for later processing
     *
     * @param targets array of targets which implement {@link INLPText}
     * @return engine
     *
     * @see CWNLPEngine#build
     * @see CWNLPEngine#rebuild
     */
    @SafeVarargs
    public final CWNLPEngine<T> load(T... targets) {
        for (T tx : targets) {
            if (tx == null) continue;
            mTargetQueue.add(tx);
        }
        return this;
    }

    /**
     * Load targets to engine
     * Loaded targets will be stored in queue for later processing
     *
     * @param targetIt iterator
     * @return engine
     *
     * @see CWNLPEngine#build
     * @see CWNLPEngine#rebuild
     */
    public CWNLPEngine<T> load(Iterator<T> targetIt) {
        while (targetIt.hasNext()) {
            T tx = targetIt.next();
            if (tx == null) continue;
            mTargetQueue.add(tx);
        }
        return this;
    }

    /**
     * Free resource (texts, byte data, etc.)
     */
    public void freeMemory() {
        mTargetList.clear();
        mTargetQueue.clear();
        mTextMap.clear();
        mCaches.clear();
        mCacheSearchKeys.clear();
        System.gc();
    }

    /**
     * Get target text which has been pre-processed
     *
     * @param target target which implement {@link INLPText}
     * @return text
     */
    @Nullable
    public String getCookedText(T target) {
        return mTextMap.get(target.nlpTextId(mContext));
    }

    /**
     * Get cache map
     *
     * @return map between the search key and cached result
     */
    @NonNull
    public Map<String, List<NLPMatchResult<T>>> getCaches() {
        return Collections.unmodifiableMap(mCaches);
    }

    /**
     * Check if the engine is using cache or not
     *
     * @return true if using else return false;
     */
    public boolean useCache() {
        return mOptions.cache > 0;
    }

    /**
     * Clear cache
     */
    public void clearCache() {
        mCaches.clear();
        mCacheSearchKeys.clear();
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Pre-processing the the target
     *
     * @param target which implement {@link INLPText}
     */
    private <X extends INLPText> String preProcess(X target) {
        String text = target.nlpRawText(mContext);

        if (!mOptions.useSpecialChars) {
            String regex = "[" + mOptions.specialChars + "]+";
            text = text.replaceAll(regex, " ");
        }

        if (mOptions.strip) {
            text = CWStringUtils.strip(text);
        }

        if (!mOptions.caseSensitive) {
            text = text.toLowerCase();
        }

        return text;
    }

    /**
     * Build engine.
     * Build loaded data in queue only which haven't process yet.
     *
     * @return engine
     */
    public CWNLPEngine<T> build() {
        // clear cache if new data is added
        if (mTargetQueue.size() > 0) {
            clearCache();
        }
        T target;
        while ((target = mTargetQueue.poll()) != null) {
            mTextMap.put(target.nlpTextId(mContext), preProcess(target));
            mTargetList.add(target);
        }
        return this;
    }

    /**
     * Rebuild engine
     * Build all loaded data both in queue and processed list
     *
     * @return engine
     */
    public CWNLPEngine<T> rebuild() {
        // clear cache to make sure doMatching will always perform on updated data
        clearCache();

        // rebuild pre-loaded texts
        for (T tx : mTargetList) {
            mTextMap.put(tx.nlpTextId(mContext), preProcess(tx));
        }

        // build texts is waiting in queue
        return this.build();
    }

    /**
     * Rebuild only the given target
     *
     * @param target NLP text which should be loaded before
     * @return engine
     */
    public CWNLPEngine<T> rebuild(T target) {
        // clear cache to make sure doMatching will always perform on updated data
        clearCache();
        // pre-process only this given target
        mTextMap.put(target.nlpTextId(mContext), preProcess(target));
        return this;
    }

    /**
     * Build the regex pattern for searching
     *
     * @param searchKey the search input
     * @return map between each split search key with respective pattern
     */
    private Map<String, Pattern> buildSearchPattern(CharSequence searchKey) {
        String _search = preProcess(new NLPString(searchKey));
        // split the search string into array of keys
        String regex = CWStringUtils.join(
                "|",
                CWRegex.REGEX_JAV_CHARS,
                CWRegex.REGEX_THAI_CHARS,
                "(\\d+)",
                "([^\\s\\d" + CWUnicode.JAV_CHARS + CWUnicode.THAI_CHARS + "]+)"
        );
        Matcher splitPar = Pattern.compile(regex).matcher(_search);
        Map<String, Pattern> _searchPat = new LinkedHashMap<>();
        while (splitPar.find()) {
            String key = splitPar.group();
            _searchPat.put(key, Pattern.compile(key));
        }
        return _searchPat;
    }

    /**
     * Check if the input string (target) matches with search key or not.
     * <pre>
     *     String input = "Color the wind";
     *
     *     // full match - match all the keys without considering the order
     *     doMatching("the color") -> status = FULL_MATCH
     *
     *     // partial match - match any keys from the search string
     *     doMatching("color storm") -> status = PARTIAL_MATCH
     *
     *     // not match
     *     doMatching("nothing") -> status = NOT_MATCH
     * </pre>
     *
     * @param searchPat the search pattern. {{buildSearchPattern}}
     * @param target    the target for searching
     * @return match result
     */
    @Nullable
    private NLPMatchResult<T> doMatching(Map<String, Pattern> searchPat, T target) {
        NLPMatchResult<T> result = new NLPMatchResult<>(target);
        for (Map.Entry<String, Pattern> entry : searchPat.entrySet()) {
            Matcher m = entry.getValue().matcher(Objects.requireNonNull(mTextMap.get(target.nlpTextId(mContext))));
            if (m.find()) {
                result.indexes.add(m.start());
                result.indexes.add(m.end());
                result.keys.add(entry.getKey());
            }
        }
        if (result.keys.size() == searchPat.size()) {
            result.status = NLPMatchResult.Status.FULL_MATCH;
        } else if (result.keys.size() > 0) {
            result.status = NLPMatchResult.Status.PARTIAL_MATCH;
        } else {
            if (mOptions.matchOnly) {
                // do not include the not-match item in the result
                return null;
            } else {
                result.status = NLPMatchResult.Status.NOT_MATCH;
            }
        }
        return result;
    }

    /**
     * Check if the input string (targets) matches with search key or not.
     *
     * @param searchKey the search input. Should be pre-processed
     * @param targets   list of NLP texts
     * @param callback  callback function
     */
    private void doMatching(CharSequence searchKey, List<T> targets, CWCallback<NLPMatchResult<T>> callback) {
        String _search = searchKey.toString();
        Map<String, Pattern> _searchPat = buildSearchPattern(_search);
        if (_searchPat.isEmpty()) /* the search input is not valid for searching */ {
            callback.onEnd();
            return;
        }

        // Check matching
        for (T tx : targets) {
            NLPMatchResult<T> result = doMatching(_searchPat, tx);
            // we can decide to accept this result or not by control the returned value of onSuccess function.
            // Return null to inform that the result is not accepted.
            // For example: we can use this way to accept the result which is full-match only
            result = callback.onSuccess(result);
            if (result != null && useCache()) {
                // cache result is caching is available
                Objects.requireNonNull(mCaches.get(_search)).add(result);
            }
        }
    }

    /**
     * Do matching on results returned by previous search which is wrapped by new search condition
     *
     * @param searchKey          the search input. Should be pre-processed
     * @param preMatchingResults list of NLP results
     * @param callback           callback function
     */
    private void doMatchingOnPreResult(CharSequence searchKey, List<NLPMatchResult<T>> preMatchingResults, CWCallback<NLPMatchResult<T>> callback) {
        String _search = searchKey.toString();
        Map<String, Pattern> _searchPat = buildSearchPattern(_search);
        if (_searchPat.isEmpty()) /* the search input is not valid for searching */ {
            callback.onEnd();
            return;
        }

        // Check matching
        for (NLPMatchResult<T> rsx : preMatchingResults) {
            NLPMatchResult<T> result = doMatching(_searchPat, rsx.target);
            // we can decide to accept this result or not by control the returned value of onSuccess function.
            // Return null to inform that the result is not accepted.
            // For example: we can use this way to accept the result which is full-match only
            result = callback.onSuccess(result);
            if (result != null && useCache()) {
                // cache result is caching is available
                Objects.requireNonNull(mCaches.get(_search)).add(result);
            }
        }
    }

    /**
     * Check if the input string matches with search key or not.
     * <p>
     * Usage:
     * <pre>
     *     String input = "Color the wind";
     *
     *     // full match - match all the keys without considering the order
     *     doMatching("the color") -> status = FULL_MATCH
     *
     *     // partial match - match any keys from the search string
     *     doMatching("color storm") -> status = PARTIAL_MATCH
     *
     *     // not match
     *     doMatching("nothing") -> status = NOT_MATCH
     * </pre>
     * <p>
     * Tip1: Handle onSuccess(result) callback function
     * <pre>
     *     We can decide to accept this result or not by control the returned value of onSuccess function.
     *     Return null to inform that the result is not accepted.
     *     For example: we can use this way to accept the result which is full-match only
     * </pre>
     *
     * @param searchKey the search input
     * @param callback  callback function
     * @see CWNLPEngine#doMatching(CharSequence, List, CWCallback)
     */
    public void doMatching(CharSequence searchKey, CWCallback<NLPMatchResult<T>> callback) {
        callback.onBegin();
        String _search = preProcess(new NLPString(searchKey));
        if (useCache()) {
            // the search result respective to given condition already cached -> use cache
            List<NLPMatchResult<T>> cacheResults;
            if ((cacheResults = mCaches.get(_search)) != null) {
                // Get the search result from cache if available
                for (NLPMatchResult<T> result : cacheResults) {
                    callback.onSuccess(result);
                }
                callback.onEnd();
                return;
            } else {
                mCaches.put(_search, new LinkedList<NLPMatchResult<T>>());
                mCacheSearchKeys.add(_search);
                if (mCacheSearchKeys.size() > mOptions.cache) /* exceed the cache limit */ {
                    mCaches.remove(mCacheSearchKeys.poll()); // remove the oldest one
                }
            }

            // Check if the search condition wraps the previous search condition or not.
            // If has -> only do matching on the previous results.
            // For example:
            // List<NLPMatchResult<T>> preResults = engine.doMatching("col");
            // engine.doMatching("color", preResults); // search on previous results because "colors" wrap "col"
            String preSearchKey = null;
            for (String key : mCacheSearchKeys) {
                if (_search.length() > key.length() && _search.startsWith(key)
                        && (preSearchKey == null || preSearchKey.length() < key.length())) {
                    preSearchKey = key;
                }
            }
            if (preSearchKey != null) {
                doMatchingOnPreResult(_search, mCaches.get(preSearchKey), callback);
                callback.onEnd();
                return;
            }
        }
        doMatching(_search, mTargetList, callback);
        callback.onEnd();
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Configuration for engine
     */
    public static final class Options {
        // language locale.
        // used to speedup and enhance the ability of processing text
        public Locale locale = Locale.getDefault();

        // strip while space at the beginning and the end of text
        // Replace multiple while spaces by a single while space
        public boolean strip = true;

        // Use case-sensitive or not (uppercase and lowercase)
        public boolean caseSensitive = false;

        // use special characters when performing the match/search/
        public boolean useSpecialChars = false;
        // default special characters are all latin symbols
        public String specialChars = CWUnicode.LATIN_SYMBOLS;

        // Whether include result which is not matched or not
        // set false if you want to include the not-match item in the returned result
        public boolean matchOnly = true;

        // Cache result.
        // 0 -> no caching
        // n -> the number of caching result
        public int cache = 0;
    }
}
