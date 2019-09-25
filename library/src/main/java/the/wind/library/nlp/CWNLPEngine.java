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

public final class CWNLPEngine {

    // Configuration for the engine
    private Options mOptions;

    // the input text for processing
    private List<NLPText> mNlpTexts = new LinkedList<>();
    private Queue<NLPText> mQueue = new LinkedList<>();

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
     * Note: you can modify this list
     *
     * @return list of NLP texts
     */
    public List<NLPText> nlpTexts() {
        return Collections.unmodifiableList(mNlpTexts);
    }

    /**
     * Remove a NLP text
     *
     * @param nlpText NLP text
     */
    public void remove(NLPText nlpText) {
        mNlpTexts.remove(nlpText);
    }

    /**
     * Load texts to engine
     * Loaded text will be stored in queue for later processing
     *
     * @param nlpTexts array of text
     * @return engine
     * @see CWNLPEngine#build
     * @see CWNLPEngine#rebuild
     */
    public CWNLPEngine loadText(NLPText... nlpTexts) {
        mQueue.addAll(Arrays.asList(nlpTexts));
        return this;
    }

    /**
     * Load text to engine
     * Loaded text will be stored in queue for later processing
     *
     * @param nlpTextIt iterator
     * @return engine
     * @see CWNLPEngine#build
     * @see CWNLPEngine#rebuild
     */
    public CWNLPEngine loadText(Iterator<NLPText> nlpTextIt) {
        while (nlpTextIt.hasNext()) {
            mQueue.add(nlpTextIt.next());
        }
        return this;
    }

    /**
     * Free resource (texts, byte data, etc.)
     */
    public void freeMemory() {
        mNlpTexts.clear();
        mQueue.clear();
        System.gc();
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Pre-processing the input text
     */
    private NLPText preProcess(NLPText nlpText) {
        if (!mOptions.useSpecialChars) {
            String regex = "[" + mOptions.specialChars + "]+";
            nlpText.setText(nlpText.getText().replaceAll(regex, " "));
        }

        if (mOptions.strip) {
            nlpText.setText(CWStringUtils.strip(nlpText.getText()));
        }

        if (!mOptions.caseSensitive) {
            nlpText.setText(nlpText.getText().toLowerCase());
        }

        return nlpText;
    }

    /**
     * Build engine
     *
     * @return engine
     */
    public CWNLPEngine build() {
        // build texts in queue only then flush it to list
        NLPText nlpText = mQueue.poll();
        while (nlpText != null) {
            nlpText.refresh();
            preProcess(nlpText);
            mNlpTexts.add(nlpText);
            nlpText = mQueue.poll();
        }
        return this;
    }

    /**
     * Rebuild engine
     *
     * @return engine
     */
    public CWNLPEngine rebuild() {
        // rebuild pre-loaded texts
        for (NLPText tx : mNlpTexts) {
            tx.refresh();
            preProcess(tx);
        }

        // build texts is waiting in queue
        return this.build();
    }

    /**
     * Rebuild only the given text
     *
     * @param text NLP text
     * @return engine
     */
    public CWNLPEngine rebuild(NLPText text) {
        text.refresh();
        preProcess(text);
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
     * @param texts    list of NLP texts
     * @param callback callback function
     */
    public void doMatching(CharSequence search, List<NLPText> texts, CWCallback<NLPMatchResult> callback) {
        callback.onBegin();
        String _search = preProcess(new NLPText(search)).getText();

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
        for (NLPText tx : texts) {
            NLPMatchResult result = new NLPMatchResult(tx);
            for (Map.Entry<String, Pattern> entry : _searchPat.entrySet()) {
                Matcher m = entry.getValue().matcher(tx.getText());
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
    public void doMatching(CharSequence search, CWCallback<NLPMatchResult> callback) {
        doMatching(search, mNlpTexts, callback);
    }

    /**
     * Check if the input string matches with search string or not.
     *
     * @param search search string
     * @see CWNLPEngine#doMatching(CharSequence, CWCallback)
     */
    public List<NLPMatchResult> doMatching(CharSequence search) {
        final List<NLPMatchResult> list = new LinkedList<>();
        doMatching(search, new CWCallback<NLPMatchResult>() {
            @Override
            public NLPMatchResult onSuccess(NLPMatchResult result) {
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
