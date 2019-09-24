package the.wind.library;

public class CWCallback<T> {

    // tagged object
    private CWTag mTag = new CWTag();

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- OVERRIDE ----------------------- */

    /**
     * On begin of doing something
     */
    public void onBegin() {
    }

    /**
     * On end of doing something
     */
    public void onEnd() {
    }

    /**
     * On returning successfully
     */
    public void onSuccess(T result) {
        onResult(result, null);
    }

    /**
     * On returning fail
     *
     * @param t throwable
     */
    public void onFail(Throwable t) {
        onResult(null, t);
    }

    /**
     * On result
     *
     * @param result result if success
     * @param t      error if failed
     */
    public void onResult(T result, Throwable t) {
    }

    /* ---------------------- EVENT -------------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return tag
     */
    public CWTag tag() {
        return mTag;
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- OVERRIDE ----------------------- */

}