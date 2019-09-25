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
     *
     * @return result
     */
    public T onSuccess(T result) {
        onResult(result, null);
        return result;
    }

    /**
     * On returning fail
     *
     * @param t throwable
     * @return true/false. You can use this returned value to decide whether stop process or not
     */
    public boolean onFail(Throwable t) {
        onResult(null, t);
        return true;
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