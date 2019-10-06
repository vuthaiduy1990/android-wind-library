package the.wind.library.nlp;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import the.wind.library.CWCallback;
import the.wind.library.CWRegex;
import the.wind.library.CWUnicode;
import the.wind.library.utils.CWStringUtils;

/**
 * Usage
 * <pre>
 *     // load text or object which implement {{@link INLPText}}
 *     CWNLPEngine<NlpString> engine = new CWNLPEngine<>();
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
 *     // or data is changed then rebuild on changed data
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

    // Configuration for the engine
    private Options mOptions;

    // the input target for processing
    private List<T> mTargetList = new LinkedList<>();
    private Queue<T> mTargetQueue = new LinkedList<>();

    /**
     * Construct engine with the default setting
     */
    public CWNLPEngine() {
        mOptions = new Options();
    }

    /**
     * Construct engine with the given setting
     */
    public CWNLPEngine(Options options) {
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
     * @see CWNLPEngine#build
     * @see CWNLPEngine#rebuild
     */
    @SafeVarargs
    public final CWNLPEngine<T> load(T... targets) {
        mTargetQueue.addAll(Arrays.asList(targets));
        return this;
    }

    /**
     * Load targets to engine
     * Loaded targets will be stored in queue for later processing
     *
     * @param targetIt iterator
     * @return engine
     * @see CWNLPEngine#build
     * @see CWNLPEngine#rebuild
     */
    public CWNLPEngine<T> load(Iterator<T> targetIt) {
        while (targetIt.hasNext()) {
            mTargetQueue.add(targetIt.next());
        }
        return this;
    }

    /**
     * Free resource (texts, byte data, etc.)
     */
    public void freeMemory() {
        mTargetList.clear();
        mTargetQueue.clear();
        System.gc();
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Pre-processing the the target
     *
     * @param target which implement {@link INLPText}
     */
    private <X extends INLPText> X preProcess(X target) {
        target.nlpText(target.toTextValue());

        if (!mOptions.useSpecialChars) {
            String regex = "[" + mOptions.specialChars + "]+";
            target.nlpText(target.nlpText().replaceAll(regex, " "));
        }

        if (mOptions.strip) {
            target.nlpText(CWStringUtils.strip(target.nlpText()));
        }

        if (!mOptions.caseSensitive) {
            target.nlpText(target.nlpText().toLowerCase());
        }

        return target;
    }

    /**
     * Build engine.
     * Build loaded data in queue only which haven't process yet.
     *
     * @return engine
     */
    public CWNLPEngine<T> build() {
        T target = mTargetQueue.poll();
        while (target != null) {
            preProcess(target);
            mTargetList.add(target);
            target = mTargetQueue.poll();
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
        // rebuild pre-loaded texts
        for (T tx : mTargetList) {
            preProcess(tx);
        }

        // build texts is waiting in queue
        return this.build();
    }

    /**
     * Rebuild only the given target
     *
     * @param target NLP text
     * @return engine
     */
    public CWNLPEngine<T> rebuild(T target) {
        preProcess(target);
        return this;
    }

    /**
     * Check if the input string matches with search string or not.
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
     * @param search   search string
     * @param targets  list of NLP texts
     * @param callback callback function
     */
    public void doMatching(CharSequence search, List<T> targets, CWCallback<NLPMatchResult<T>> callback) {
        callback.onBegin();
        String _search = preProcess(new NLPString(search)).nlpText();

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

        // no keys are extracted
        if (_searchPat.isEmpty()) {
            callback.onEnd();
            return;
        }

        // Check matching
        for (T tx : targets) {
            NLPMatchResult<T> result = new NLPMatchResult<>(tx);
            for (Map.Entry<String, Pattern> entry : _searchPat.entrySet()) {
                Matcher m = entry.getValue().matcher(tx.nlpText());
                if (m.find()) {
                    result.indexes.add(m.start());
                    result.indexes.add(m.end());
                    result.keys.add(entry.getKey());
                }
            }
            if (result.keys.size() == _searchPat.size()) {
                result.status = NLPMatchResult.Status.FULL_MATCH;
            } else if (result.keys.size() > 0) {
                result.status = NLPMatchResult.Status.PARTIAL_MATCH;
            } else {
                result.status = NLPMatchResult.Status.NOT_MATCH;
            }
            callback.onSuccess(result);
        }
        callback.onEnd();
    }

    /**
     * Check if the input string matches with search string or not.
     *
     * @param search   search string
     * @param callback callback function
     * @see CWNLPEngine#doMatching(CharSequence, List, CWCallback)
     */
    public void doMatching(CharSequence search, CWCallback<NLPMatchResult<T>> callback) {
        doMatching(search, mTargetList, callback);
    }

    /**
     * Check if the input string matches with search string or not.
     *
     * @param search search string
     * @see CWNLPEngine#doMatching(CharSequence, CWCallback)
     */
    public List<NLPMatchResult<T>> doMatching(CharSequence search) {
        final List<NLPMatchResult<T>> list = new LinkedList<>();
        doMatching(search, new CWCallback<NLPMatchResult<T>>() {
            @Override
            public NLPMatchResult<T> onSuccess(NLPMatchResult<T> result) {
                list.add(result);
                return super.onSuccess(result);
            }
        });
        return list;
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
    }
}
