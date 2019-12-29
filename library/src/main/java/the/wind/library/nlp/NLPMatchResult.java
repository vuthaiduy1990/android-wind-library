package the.wind.library.nlp;

import java.util.LinkedList;
import java.util.List;

/**
 * Matching Result
 */
public final class NLPMatchResult<T extends INLPText> {

    // NLP text
    public T target;

    // search keys which match with the given input
    public List<String> keys = new LinkedList<>();

    // start and end indexes of character matched
    public List<Integer> indexes = new LinkedList<>();

    // status
    public Status status = Status.NOT_MATCH;

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

    /* ---------------------- EVENT -------------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return true if the returned matching result is matched
     */
    public boolean isMatched() {
        return isPartialMatched() || isFullMatched();
    }

    /**
     * @return true if the returned matching result is full matched
     */
    public boolean isFullMatched() {
        return status.equals(Status.FULL_MATCH);
    }

    /**
     * @return true if the returned matching result is partial matched
     */
    public boolean isPartialMatched() {
        return status.equals(Status.PARTIAL_MATCH);
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    // status
    public enum Status {
        NOT_MATCH,
        FULL_MATCH,
        PARTIAL_MATCH;
    }
}
