package the.wind.library.nlp;

import java.util.LinkedList;
import java.util.List;

/**
 * Matching Result
 */
public final class NLPMatchResult<T extends INLPText> {

    // NLP text
    public final T target;

    // matching or not
    public boolean match = false;

    // search keys which match with the given input
    public final List<String> keys = new LinkedList<>();

    // start and end indexes of character matched
    public final List<Integer> indexes = new LinkedList<>();

    /**
     * Protected constructor to disable user from initializing an NLP result
     *
     * @param target a NLP text
     */
    protected NLPMatchResult(T target) {
        this.target = target;
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return true if the returned matching result is matched
     */
    public boolean isMatching() {
        return match;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    // status
    public enum Status {
        NOT_MATCH,
        FULL_MATCH,
        PARTIAL_MATCH
    }
}
